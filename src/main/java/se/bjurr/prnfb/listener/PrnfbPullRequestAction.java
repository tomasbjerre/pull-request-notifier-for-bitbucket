package se.bjurr.prnfb.listener;

import static com.atlassian.bitbucket.pull.PullRequestAction.APPROVED;
import static com.atlassian.bitbucket.pull.PullRequestAction.COMMENTED;
import static com.atlassian.bitbucket.pull.PullRequestAction.DECLINED;
import static com.atlassian.bitbucket.pull.PullRequestAction.MERGED;
import static com.atlassian.bitbucket.pull.PullRequestAction.OPENED;
import static com.atlassian.bitbucket.pull.PullRequestAction.REOPENED;
import static com.atlassian.bitbucket.pull.PullRequestAction.RESCOPED;
import static com.atlassian.bitbucket.pull.PullRequestAction.UNAPPROVED;
import static com.atlassian.bitbucket.pull.PullRequestAction.UPDATED;
import static com.google.common.collect.Lists.newArrayList;
import static java.lang.Boolean.FALSE;

import java.util.List;
import java.util.Map;

import se.bjurr.prnfb.settings.PrnfbNotification;

import com.atlassian.bitbucket.event.pull.PullRequestEvent;
import com.atlassian.bitbucket.event.pull.PullRequestRescopedEvent;
import com.google.common.collect.ImmutableMap;

public class PrnfbPullRequestAction {
 public static final String RESCOPED_TO = "RESCOPED_TO";

 public static final String RESCOPED_FROM = "RESCOPED_FROM";

 public static final String BUTTON_TRIGGER = "BUTTON_TRIGGER";

 private static final Map<String, PrnfbPullRequestAction> values = new ImmutableMap.Builder<String, PrnfbPullRequestAction>()
   .put(APPROVED.name(), new PrnfbPullRequestAction(APPROVED.name())) //
   .put(COMMENTED.name(), new PrnfbPullRequestAction(COMMENTED.name())) //
   .put(DECLINED.name(), new PrnfbPullRequestAction(DECLINED.name())) //
   .put(MERGED.name(), new PrnfbPullRequestAction(MERGED.name())) //
   .put(OPENED.name(), new PrnfbPullRequestAction(OPENED.name())) //
   .put(REOPENED.name(), new PrnfbPullRequestAction(REOPENED.name())) //
   .put(RESCOPED.name(), new PrnfbPullRequestAction(RESCOPED_FROM)) //
   .put(RESCOPED_FROM, new PrnfbPullRequestAction(RESCOPED_FROM)) //
   .put(RESCOPED_TO, new PrnfbPullRequestAction(RESCOPED_TO)) //
   .put(UNAPPROVED.name(), new PrnfbPullRequestAction(UNAPPROVED.name())) //
   .put(UPDATED.name(), new PrnfbPullRequestAction(UPDATED.name())) //
   .put(BUTTON_TRIGGER, new PrnfbPullRequestAction(BUTTON_TRIGGER)) //
   .build();

 private final String name;

 private PrnfbPullRequestAction() {
  name = null;
 }

 private PrnfbPullRequestAction(String name) {
  this.name = name;
 }

 public String getName() {
  return name;
 }

 public static PrnfbPullRequestAction valueOf(String string) {
  if (values.containsKey(string)) {
   return values.get(string);
  }
  throw new RuntimeException("\"" + string + "\" not found!");
 }

 public static List<PrnfbPullRequestAction> values() {
  return newArrayList(values.values());
 }

 public static PrnfbPullRequestAction fromPullRequestEvent(PullRequestEvent event, PrnfbNotification notification) {
  if (event instanceof PullRequestRescopedEvent) {
   PullRequestRescopedEvent rescopedEvent = (PullRequestRescopedEvent) event;
   boolean toChanged = !rescopedEvent.getPreviousToHash().equals(
     rescopedEvent.getPullRequest().getToRef().getLatestCommit());
   boolean fromChanged = !rescopedEvent.getPreviousFromHash().equals(
     rescopedEvent.getPullRequest().getFromRef().getLatestCommit());
   if (fromChanged && !toChanged) {
    return PrnfbPullRequestAction.valueOf(RESCOPED_FROM);
   } else if (toChanged && !fromChanged) {
    return PrnfbPullRequestAction.valueOf(RESCOPED_TO);
   } else {
    if (notification.getTriggers().contains(values.get(RESCOPED_FROM))) {
     return PrnfbPullRequestAction.valueOf(RESCOPED_FROM);
    } else if (notification.getTriggers().contains(values.get(RESCOPED_TO))) {
     return PrnfbPullRequestAction.valueOf(RESCOPED_TO);
    }
   }
  }
  return PrnfbPullRequestAction.valueOf(event.getAction().name());
 }

 @Override
 public boolean equals(Object obj) {
  if (obj instanceof PrnfbPullRequestAction) {
   return getName().equals(((PrnfbPullRequestAction) obj).getName());
  }
  return FALSE;
 }
}
