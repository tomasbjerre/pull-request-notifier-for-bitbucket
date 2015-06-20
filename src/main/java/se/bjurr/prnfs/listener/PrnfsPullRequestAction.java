package se.bjurr.prnfs.listener;

import static com.atlassian.stash.pull.PullRequestAction.APPROVED;
import static com.atlassian.stash.pull.PullRequestAction.COMMENTED;
import static com.atlassian.stash.pull.PullRequestAction.DECLINED;
import static com.atlassian.stash.pull.PullRequestAction.MERGED;
import static com.atlassian.stash.pull.PullRequestAction.OPENED;
import static com.atlassian.stash.pull.PullRequestAction.REOPENED;
import static com.atlassian.stash.pull.PullRequestAction.RESCOPED;
import static com.atlassian.stash.pull.PullRequestAction.UNAPPROVED;
import static com.atlassian.stash.pull.PullRequestAction.UPDATED;
import static com.google.common.collect.Lists.newArrayList;
import static java.lang.Boolean.FALSE;

import java.util.List;
import java.util.Map;

import se.bjurr.prnfs.settings.PrnfsNotification;

import com.atlassian.stash.event.pull.PullRequestEvent;
import com.atlassian.stash.event.pull.PullRequestRescopedEvent;
import com.google.common.collect.ImmutableMap;

public class PrnfsPullRequestAction {
 public static final String RESCOPED_TO = "RESCOPED_TO";

 public static final String RESCOPED_FROM = "RESCOPED_FROM";

 private static final Map<String, PrnfsPullRequestAction> values = new ImmutableMap.Builder<String, PrnfsPullRequestAction>()
   .put(APPROVED.name(), new PrnfsPullRequestAction(APPROVED.name())) //
   .put(COMMENTED.name(), new PrnfsPullRequestAction(COMMENTED.name())) //
   .put(DECLINED.name(), new PrnfsPullRequestAction(DECLINED.name())) //
   .put(MERGED.name(), new PrnfsPullRequestAction(MERGED.name())) //
   .put(OPENED.name(), new PrnfsPullRequestAction(OPENED.name())) //
   .put(REOPENED.name(), new PrnfsPullRequestAction(REOPENED.name())) //
   .put(RESCOPED.name(), new PrnfsPullRequestAction(RESCOPED_FROM)) //
   .put(RESCOPED_FROM, new PrnfsPullRequestAction(RESCOPED_FROM)) //
   .put(RESCOPED_TO, new PrnfsPullRequestAction(RESCOPED_TO)) //
   .put(UNAPPROVED.name(), new PrnfsPullRequestAction(UNAPPROVED.name())) //
   .put(UPDATED.name(), new PrnfsPullRequestAction(UPDATED.name())) //
   .build();

 private final String name;

 private PrnfsPullRequestAction() {
  name = null;
 }

 private PrnfsPullRequestAction(String name) {
  this.name = name;
 }

 public String getName() {
  return name;
 }

 public static PrnfsPullRequestAction valueOf(String string) {
  if (values.containsKey(string)) {
   return values.get(string);
  }
  throw new RuntimeException("\"" + string + "\" not found!");
 }

 public static List<PrnfsPullRequestAction> values() {
  return newArrayList(values.values());
 }

 public static PrnfsPullRequestAction fromPullRequestEvent(PullRequestEvent event, PrnfsNotification notification) {
  if (event instanceof PullRequestRescopedEvent) {
   PullRequestRescopedEvent rescopedEvent = (PullRequestRescopedEvent) event;
   boolean toChanged = !rescopedEvent.getPreviousToHash().equals(
     rescopedEvent.getPullRequest().getToRef().getLatestChangeset());
   boolean fromChanged = !rescopedEvent.getPreviousFromHash().equals(
     rescopedEvent.getPullRequest().getFromRef().getLatestChangeset());
   if (fromChanged && !toChanged) {
    return PrnfsPullRequestAction.valueOf(RESCOPED_FROM);
   } else if (toChanged && !fromChanged) {
    return PrnfsPullRequestAction.valueOf(RESCOPED_TO);
   } else {
    if (notification.getTriggers().contains(values.get(RESCOPED_FROM))) {
     return PrnfsPullRequestAction.valueOf(RESCOPED_FROM);
    } else if (notification.getTriggers().contains(values.get(RESCOPED_TO))) {
     return PrnfsPullRequestAction.valueOf(RESCOPED_TO);
    }
   }
  }
  return PrnfsPullRequestAction.valueOf(event.getAction().name());
 }

 @Override
 public boolean equals(Object obj) {
  if (obj instanceof PrnfsPullRequestAction) {
   return getName().equals(((PrnfsPullRequestAction) obj).getName());
  }
  return FALSE;
 }
}
