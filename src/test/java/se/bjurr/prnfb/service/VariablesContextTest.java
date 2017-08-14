package se.bjurr.prnfb.service;

import static com.atlassian.bitbucket.comment.CommentAction.ADDED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static se.bjurr.prnfb.service.PrnfbVariable.PULL_REQUEST_COMMENT_ACTION;
import static se.bjurr.prnfb.service.PrnfbVariable.PULL_REQUEST_COMMENT_ID;
import static se.bjurr.prnfb.service.PrnfbVariable.PULL_REQUEST_COMMENT_TEXT;
import static se.bjurr.prnfb.service.PrnfbVariable.PULL_REQUEST_MERGE_COMMIT;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import se.bjurr.prnfb.service.VariablesContext.VariablesContextBuilder;
import se.bjurr.prnfb.settings.PrnfbButton;
import se.bjurr.prnfb.settings.ValidationException;

import com.atlassian.bitbucket.comment.Comment;
import com.atlassian.bitbucket.commit.MinimalCommit;
import com.atlassian.bitbucket.event.pull.PullRequestCommentAddedEvent;
import com.atlassian.bitbucket.event.pull.PullRequestCommentEvent;
import com.atlassian.bitbucket.event.pull.PullRequestEvent;
import com.atlassian.bitbucket.event.pull.PullRequestMergedEvent;
import com.google.common.base.Supplier;

public class VariablesContextTest {

  private VariablesContext sut;
  @Mock private PrnfbButton button;
  @Mock private PullRequestEvent pullRequestOpenedEvent;
  @Mock private PullRequestCommentEvent pullRequestCommentEvent;

  @Before
  public void before() throws ValidationException {
    initMocks(this);
  }

  @Test
  public void testThatPullRequestMergeComitIsAddedToVariables() {
    final PullRequestMergedEvent pullRequestEvent = mock(PullRequestMergedEvent.class);
    final MinimalCommit commit = mock(MinimalCommit.class);
    when(pullRequestEvent.getCommit()) //
        .thenReturn(commit);
    when(pullRequestEvent.getCommit().getId()) //
        .thenReturn("hash");

    sut =
        new VariablesContextBuilder() //
            .setPullRequestEvent(pullRequestEvent) //
            .build();
    final Map<PrnfbVariable, Supplier<String>> actual = sut.getVariables();

    assertThat(actual) //
        .hasSize(1);
    assertThat(actual.get(PULL_REQUEST_MERGE_COMMIT).get()) //
        .isEqualTo("hash");
  }

  @Test
  public void testThatPullRequestCommentIsAddedToVariables() {
    final PullRequestCommentAddedEvent pullRequestEvent = mock(PullRequestCommentAddedEvent.class);
    final Comment comment = mock(Comment.class);
    when(pullRequestEvent.getComment()) //
        .thenReturn(comment);
    when(pullRequestEvent.getComment().getText()) //
        .thenReturn("The comment");
    when(pullRequestEvent.getCommentAction()) //
        .thenReturn(ADDED);

    sut =
        new VariablesContextBuilder() //
            .setPullRequestEvent(pullRequestEvent) //
            .build();
    final Map<PrnfbVariable, Supplier<String>> actual = sut.getVariables();

    assertThat(actual) //
        .hasSize(3);
    assertThat(actual.get(PULL_REQUEST_COMMENT_TEXT).get()) //
        .isEqualTo("The comment");
    assertThat(actual.get(PULL_REQUEST_COMMENT_ACTION).get()) //
        .isEqualTo("ADDED");
    assertThat(actual.get(PULL_REQUEST_COMMENT_ID).get()) //
        .isEqualTo("0");
  }
}
