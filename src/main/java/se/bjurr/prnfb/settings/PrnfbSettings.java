package se.bjurr.prnfb.settings;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

public class PrnfbSettings {
 private List<PrnfbNotification> notifications = newArrayList();
 private final List<PrnfbButton> buttons;
 private final boolean usersAllowed;
 private final boolean adminsAllowed;

 public PrnfbSettings(List<PrnfbNotification> notifications, List<PrnfbButton> buttons, boolean usersAllowed,
   boolean adminsAllowed) {
  this.notifications = checkNotNull(notifications);
  this.buttons = checkNotNull(buttons);
  this.usersAllowed = usersAllowed;
  this.adminsAllowed = adminsAllowed;
 }

 public List<PrnfbNotification> getNotifications() {
  return notifications;
 }

 public List<PrnfbButton> getButtons() {
  return buttons;
 }

 public boolean isUsersAllowed() {
  return usersAllowed;
 }

 public boolean isAdminsAllowed() {
  return adminsAllowed;
 }
}
