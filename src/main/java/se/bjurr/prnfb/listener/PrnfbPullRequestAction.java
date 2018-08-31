package se.bjurr.prnfb.listener;

import com.atlassian.bitbucket.event.pull.PullRequestEvent;
import com.atlassian.bitbucket.event.pull.PullRequestRescopedEvent;
import se.bjurr.prnfb.settings.PrnfbNotification;

public enum PrnfbPullRequestAction {
  APPROVED, //
  BUTTON_TRIGGER, //
  COMMENTED, //
  DELETED, //
  DECLINED, //
  MERGED, //
  OPENED, //
  REOPENED, //
  RESCOPED, //
  RESCOPED_FROM, //
  RESCOPED_TO, //
  UNAPPROVED, //
  UPDATED,
  REVIEWED; //

  public static PrnfbPullRequestAction fromPullRequestEvent(
      PullRequestEvent event, PrnfbNotification notification) {
    if (event instanceof PullRequestRescopedEvent) {
      final PullRequestRescopedEvent rescopedEvent = (PullRequestRescopedEvent) event;
      final boolean toChanged =
          !rescopedEvent
              .getPreviousToHash()
              .equals(rescopedEvent.getPullRequest().getToRef().getLatestCommit());
      final boolean fromChanged =
          !rescopedEvent
              .getPreviousFromHash()
              .equals(rescopedEvent.getPullRequest().getFromRef().getLatestCommit());
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
