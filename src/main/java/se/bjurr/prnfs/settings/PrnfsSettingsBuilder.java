package se.bjurr.prnfs.settings;

import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

public class PrnfsSettingsBuilder {
 public static PrnfsSettingsBuilder prnfsSettingsBuilder() {
  return new PrnfsSettingsBuilder();
 }

 private final List<PrnfsNotification> notifications = newArrayList();
 private final List<PrnfsButton> buttons = newArrayList();
 private boolean usersAllowed;
 private boolean adminsAllowed;

 private PrnfsSettingsBuilder() {
 }

 public PrnfsSettings build() {
  return new PrnfsSettings(notifications, buttons, usersAllowed, adminsAllowed);
 }

 public PrnfsSettingsBuilder withNotification(PrnfsNotification notification) {
  this.notifications.add(notification);
  return this;
 }

 public void withButton(PrnfsButton prnfsButton) {
  this.buttons.add(prnfsButton);
 }

 public List<PrnfsButton> getButtons() {
  return buttons;
 }

 public List<PrnfsNotification> getNotifications() {
  return notifications;
 }

 public PrnfsSettingsBuilder withUsersAllowed(boolean allowed) {
  this.usersAllowed = allowed;
  return this;
 }

 public PrnfsSettingsBuilder withAdminsAllowed(boolean allowed) {
  this.adminsAllowed = allowed;
  return this;
 }

 public boolean isAdminsAllowed() {
  return adminsAllowed;
 }

 public boolean isUsersAllowed() {
  return usersAllowed;
 }
}
