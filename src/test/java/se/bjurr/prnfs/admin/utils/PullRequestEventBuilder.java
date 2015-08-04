package se.bjurr.prnfs.admin.utils;

import static com.atlassian.stash.pull.PullRequestAction.COMMENTED;
import static com.atlassian.stash.pull.PullRequestAction.RESCOPED;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static se.bjurr.prnfs.admin.utils.PullRequestRefBuilder.pullRequestRefBuilder;

import com.atlassian.stash.comment.Comment;
import com.atlassian.stash.event.pull.PullRequestCommentAddedEvent;
import com.atlassian.stash.event.pull.PullRequestEvent;
import com.atlassian.stash.event.pull.PullRequestRescopedEvent;
import com.atlassian.stash.pull.PullRequest;
import com.atlassian.stash.pull.PullRequestAction;
import com.atlassian.stash.pull.PullRequestParticipant;

public class PullRequestEventBuilder {
 public static final String PREVIOUS_TO_HASH = "previousToHash";
 public static final String PREVIOUS_FROM_HASH = "previousFromHash";
 private PullRequestAction pullRequestAction;
 private PullRequestRefBuilder toRef = pullRequestRefBuilder();
 private PullRequestRefBuilder fromRef = pullRequestRefBuilder();
 private Long id;
 private PullRequestParticipant author;
 private String commentText;
 private final PrnfsTestBuilder prnfsTestBuilder;
 private boolean beingClosed;

 private PullRequestEventBuilder(PrnfsTestBuilder prnfsTestBuilder) {
  this.prnfsTestBuilder = prnfsTestBuilder;
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
  return new PullRequestEventBuilder(null);
 }

 public static PullRequestEventBuilder pullRequestEventBuilder(PrnfsTestBuilder prnfsTestBuilder) {
  return new PullRequestEventBuilder(prnfsTestBuilder);
 }

 public PrnfsTestBuilder getPrnfsTestBuilder() {
  return prnfsTestBuilder;
 }

 public PullRequestRefBuilder withFromRefPullRequestRefBuilder() {
  PullRequestRefBuilder ref = pullRequestRefBuilder(this);
  this.withFromRef(ref);
  return ref;
 }

 public PullRequestRefBuilder withToRefPullRequestRefBuilder() {
  PullRequestRefBuilder ref = pullRequestRefBuilder(this);
  this.withToRef(ref);
  return ref;
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
   when(event.getPreviousFromHash()).thenReturn(PREVIOUS_FROM_HASH);
   when(event.getPreviousToHash()).thenReturn(PREVIOUS_TO_HASH);
   pullRequestEvent = event;
  } else if (pullRequestAction == COMMENTED) {
   PullRequestCommentAddedEvent event = mock(PullRequestCommentAddedEvent.class);
   Comment comment = mock(Comment.class);
   when(event.getComment()).thenReturn(comment);
   when(event.getComment().getText()).thenReturn(commentText);
   pullRequestEvent = event;
  }
  final PullRequest pullRequest = mock(PullRequest.class);
  when(pullRequest.isClosed()).thenReturn(beingClosed);
  when(pullRequestEvent.getAction()).thenReturn(pullRequestAction);
  when(pullRequestEvent.getPullRequest()).thenReturn(pullRequest);
  when(pullRequestEvent.getPullRequest().getAuthor()).thenReturn(author);
  when(pullRequestEvent.getPullRequest().getId()).thenReturn(id);
  when(pullRequestEvent.getPullRequest().getFromRef()).thenReturn(fromRef);
  when(pullRequestEvent.getPullRequest().getToRef()).thenReturn(toRef);
  return pullRequestEvent;
 }

 public PrnfsTestBuilder triggerEvent() {
  return prnfsTestBuilder.trigger(build());
 }

 public PullRequestEventBuilder beingClosed() {
  beingClosed = true;
  return this;
 }
}
