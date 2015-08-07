package se.bjurr.prnfs.settings;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

public class PrnfsSettings {
 private List<PrnfsNotification> notifications = newArrayList();
 private final List<PrnfsButton> buttons;

 public PrnfsSettings(List<PrnfsNotification> notifications, List<PrnfsButton> buttons) {
  this.notifications = checkNotNull(notifications);
  this.buttons = checkNotNull(buttons);
 }

 public List<PrnfsNotification> getNotifications() {
  return notifications;
 }

 public List<PrnfsButton> getButtons() {
  return buttons;
 }
}
