package se.bjurr.prnfs.admin.utils;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.atlassian.stash.event.pull.PullRequestEvent;
import com.atlassian.stash.pull.PullRequest;
import com.atlassian.stash.pull.PullRequestAction;

public class PullRequestEventBuilder {
 private PullRequestAction pullRequestAction;
 private PullRequestRefBuilder toRef;
 private PullRequestRefBuilder fromRef;
 private Long id;

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

 public PullRequestEvent build() {
  final PullRequestEvent pullRequestEvent = mock(PullRequestEvent.class);
  final PullRequest pullRequest = mock(PullRequest.class);
  when(pullRequestEvent.getAction()).thenReturn(pullRequestAction);
  when(pullRequestEvent.getPullRequest()).thenReturn(pullRequest);
  when(pullRequestEvent.getPullRequest().getId()).thenReturn(id);
  when(pullRequestEvent.getPullRequest().getFromRef()).thenReturn(fromRef);
  when(pullRequestEvent.getPullRequest().getToRef()).thenReturn(toRef);
  return pullRequestEvent;
 }

 public PullRequestEventBuilder withId(Long id) {
  this.id = id;
  return this;
 }
}
