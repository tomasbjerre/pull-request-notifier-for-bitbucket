package se.bjurr.prnfs.listener;

import static com.atlassian.stash.pull.PullRequestAction.OPENED;
import static com.atlassian.stash.pull.PullRequestAction.RESCOPED;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static se.bjurr.prnfs.listener.PrnfsPullRequestAction.RESCOPED_FROM;
import static se.bjurr.prnfs.listener.PrnfsPullRequestAction.RESCOPED_TO;
import static se.bjurr.prnfs.listener.PrnfsPullRequestAction.fromPullRequestEvent;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.stash.event.pull.PullRequestEvent;
import com.atlassian.stash.event.pull.PullRequestRescopedEvent;
import com.atlassian.stash.pull.PullRequest;
import com.atlassian.stash.pull.PullRequestAction;
import com.atlassian.stash.pull.PullRequestRef;

public class PrnfsPullRequestActionTest {
 private PullRequestRescopedEvent event;
 private PullRequest pullRequest;
 private PullRequestRef fromRef;
 private PullRequestRef toRef;

 @Before
 public void before() {
  event = mock(PullRequestRescopedEvent.class);
  pullRequest = mock(PullRequest.class);
  fromRef = mock(PullRequestRef.class);
  toRef = mock(PullRequestRef.class);
  when(event.getAction()).thenReturn(RESCOPED);
  when(event.getPullRequest()).thenReturn(pullRequest);
  when(event.getPullRequest().getFromRef()).thenReturn(fromRef);
  when(event.getPullRequest().getToRef()).thenReturn(toRef);
 }

 @Test
 public void testThatAllPullRequestActionEventsAreMapped() {
  for (PullRequestAction a : PullRequestAction.values()) {
   assertNotNull(a.name(), PrnfsPullRequestAction.valueOf(a.name()));
   assertEquals(a.name(), a.name(), PrnfsPullRequestAction.valueOf(a.name()).getName());
  }
 }

 @SuppressWarnings("deprecation")
 @Test
 public void testThatRescopedEventsAreCalculatedCorrectlyWhenFromAndToChanges() {
  when(event.getPreviousFromHash()).thenReturn("FROM");
  when(event.getPreviousToHash()).thenReturn("TO");
  when(event.getPullRequest().getFromRef().getLatestChangeset()).thenReturn("FROM2");
  when(event.getPullRequest().getToRef().getLatestChangeset()).thenReturn("TO2");
  assertEquals(RESCOPED.name(), fromPullRequestEvent(event).getName());
 }

 @SuppressWarnings("deprecation")
 @Test
 public void testThatRescopedEventsAreCalculatedCorrectlyWhenOnlyFromChanges() {
  when(event.getPreviousFromHash()).thenReturn("FROM");
  when(event.getPreviousToHash()).thenReturn("TO");
  when(event.getPullRequest().getFromRef().getLatestChangeset()).thenReturn("FROM2");
  when(event.getPullRequest().getToRef().getLatestChangeset()).thenReturn("TO");
  assertEquals(RESCOPED_FROM, fromPullRequestEvent(event).getName());
 }

 @SuppressWarnings("deprecation")
 @Test
 public void testThatRescopedEventsAreCalculatedCorrectlyWhenOnlyToChanges() {
  when(event.getPreviousFromHash()).thenReturn("FROM");
  when(event.getPreviousToHash()).thenReturn("TO");
  when(event.getPullRequest().getFromRef().getLatestChangeset()).thenReturn("FROM");
  when(event.getPullRequest().getToRef().getLatestChangeset()).thenReturn("TO2");
  assertEquals(RESCOPED_TO, fromPullRequestEvent(event).getName());
 }

 @Test
 public void testThatNoneRescopedEventsAreCalculatedCorrectly() {
  PullRequestEvent superEvent = mock(PullRequestEvent.class);
  when(superEvent.getAction()).thenReturn(OPENED);
  assertEquals(OPENED.name(), fromPullRequestEvent(superEvent).getName());
 }
}
