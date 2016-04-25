package se.bjurr.prnfb.listener;

import se.bjurr.prnfb.settings.PrnfbNotification;

import com.atlassian.bitbucket.event.pull.PullRequestEvent;
import com.atlassian.bitbucket.event.pull.PullRequestRescopedEvent;

public enum PrnfbPullRequestAction {

 APPROVED, //
 BUTTON_TRIGGER, //
 COMMENTED, //
 DECLINED, //
 MERGED, //
 OPENED, //
 REOPENED, //
 RESCOPED, //
 RESCOPED_FROM, //
 RESCOPED_TO, //
 UNAPPROVED, //
 UPDATED;

 public static PrnfbPullRequestAction fromPullRequestEvent(PullRequestEvent event, PrnfbNotification notification) {
  if (event instanceof PullRequestRescopedEvent) {
   PullRequestRescopedEvent rescopedEvent = (PullRequestRescopedEvent) event;
   boolean toChanged = !rescopedEvent.getPreviousToHash().equals(
     rescopedEvent.getPullRequest().getToRef().getLatestCommit());
   boolean fromChanged = !rescopedEvent.getPreviousFromHash().equals(
     rescopedEvent.getPullRequest().getFromRef().getLatestCommit());
   if (fromChanged && !toChanged) {
    return RESCOPED_FROM;
   } else if (toChanged && !fromChanged) {
    return RESCOPED_TO;
   } else {
    if (notification.getTriggers().contains(RESCOPED_FROM)) {
     return RESCOPED_FROM;
    } else if (notification.getTriggers().contains(RESCOPED_TO)) {
     return RESCOPED_TO;
    }
   }
  }
  return PrnfbPullRequestAction.valueOf(event.getAction().name());
 }
}
