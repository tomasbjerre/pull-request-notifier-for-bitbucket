package se.bjurr.prnfs.settings;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

public class PrnfsSettings {
 private List<PrnfsNotification> notifications = newArrayList();

 public PrnfsSettings(List<PrnfsNotification> notifications) {
  this.notifications = checkNotNull(notifications);
 }

 public List<PrnfsNotification> getNotifications() {
  return notifications;
 }
}
