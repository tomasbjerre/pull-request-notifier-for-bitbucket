package se.bjurr.prnfs.settings;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

public class PrnfsSettings {
 private List<PrnfsNotification> notifications = newArrayList();
 private final List<PrnfsButton> buttons;
 private final boolean usersAllowed;
 private final boolean adminsAllowed;

 public PrnfsSettings(List<PrnfsNotification> notifications, List<PrnfsButton> buttons, boolean usersAllowed,
   boolean adminsAllowed) {
  this.notifications = checkNotNull(notifications);
  this.buttons = checkNotNull(buttons);
  this.usersAllowed = usersAllowed;
  this.adminsAllowed = adminsAllowed;
 }

 public List<PrnfsNotification> getNotifications() {
  return notifications;
 }

 public List<PrnfsButton> getButtons() {
  return buttons;
 }

 public boolean isUsersAllowed() {
  return usersAllowed;
 }

 public boolean isAdminsAllowed() {
  return adminsAllowed;
 }
}
