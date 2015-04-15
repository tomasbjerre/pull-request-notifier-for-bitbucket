package se.bjurr.prnfs.admin.utils;

import static com.atlassian.stash.pull.PullRequestAction.COMMENTED;
import static com.atlassian.stash.pull.PullRequestAction.RESCOPED;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.atlassian.stash.comment.Comment;
import com.atlassian.stash.event.pull.PullRequestCommentAddedEvent;
import com.atlassian.stash.event.pull.PullRequestEvent;
import com.atlassian.stash.event.pull.PullRequestRescopedEvent;
import com.atlassian.stash.pull.PullRequest;
import com.atlassian.stash.pull.PullRequestAction;
import com.atlassian.stash.pull.PullRequestParticipant;

public class PullRequestEventBuilder {
 private PullRequestAction pullRequestAction;
 private PullRequestRefBuilder toRef;
 private PullRequestRefBuilder fromRef;
 private Long id;
 private PullRequestParticipant author;
 private String commentText;

 private PullRequestEventBuilder() {
 }

 public PullRequestEventBuilder withFromRef(PullRequestRefBuilder fromRef) {
  this.fromRef = fromRef;
  return this;
 }

 public PullRequestEventBuilder withToRef(PullRequestRefBuilder toRef) {
  this.toRef = toRef;
  return this;
 }

 public static PullRequestEventBuilder pullRequestEventBuilder() {
  return new PullRequestEventBuilder();
 }

 public PullRequestEventBuilder withPullRequestAction(PullRequestAction pullRequestAction) {
  this.pullRequestAction = pullRequestAction;
  return this;
 }

 public PullRequestEventBuilder withAuthor(PullRequestParticipant author) {
  this.author = author;
  return this;
 }

 public PullRequestEventBuilder withId(Long id) {
  this.id = id;
  return this;
 }

 public PullRequestEventBuilder withCommentText(String commentText) {
  this.commentText = commentText;
  return this;
 }

 public PullRequestEvent build() {
  PullRequestEvent pullRequestEvent = mock(PullRequestEvent.class);
  if (pullRequestAction == RESCOPED) {
   PullRequestRescopedEvent event = mock(PullRequestRescopedEvent.class);
   when(event.getPreviousFromHash()).thenReturn("previousFromHash");
   when(event.getPreviousToHash()).thenReturn("previousToHash");
   pullRequestEvent = event;
  } else if (pullRequestAction == COMMENTED) {
   PullRequestCommentAddedEvent event = mock(PullRequestCommentAddedEvent.class);
   Comment comment = mock(Comment.class);
   when(event.getComment()).thenReturn(comment);
   when(event.getComment().getText()).thenReturn(commentText);
   pullRequestEvent = event;
  }
  final PullRequest pullRequest = mock(PullRequest.class);
  when(pullRequestEvent.getAction()).thenReturn(pullRequestAction);
  when(pullRequestEvent.getPullRequest()).thenReturn(pullRequest);
  when(pullRequestEvent.getPullRequest().getAuthor()).thenReturn(author);
  when(pullRequestEvent.getPullRequest().getId()).thenReturn(id);
  when(pullRequestEvent.getPullRequest().getFromRef()).thenReturn(fromRef);
  when(pullRequestEvent.getPullRequest().getToRef()).thenReturn(toRef);
  return pullRequestEvent;
 }
}
