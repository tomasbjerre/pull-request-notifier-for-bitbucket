package se.bjurr.prnfb.settings;

import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

public class PrnfbSettingsBuilder {
 public static PrnfbSettingsBuilder prnfbSettingsBuilder() {
  return new PrnfbSettingsBuilder();
 }

 private final List<PrnfbNotification> notifications = newArrayList();
 private final List<PrnfbButton> buttons = newArrayList();
 private boolean usersAllowed;
 private boolean adminsAllowed;

 private PrnfbSettingsBuilder() {
 }

 public PrnfbSettings build() {
  return new PrnfbSettings(notifications, buttons, usersAllowed, adminsAllowed);
 }

 public PrnfbSettingsBuilder withNotification(PrnfbNotification notification) {
  this.notifications.add(notification);
  return this;
 }

 public void withButton(PrnfbButton prnfbButton) {
  this.buttons.add(prnfbButton);
 }

 public List<PrnfbButton> getButtons() {
  return buttons;
 }

 public List<PrnfbNotification> getNotifications() {
  return notifications;
 }

 public PrnfbSettingsBuilder withUsersAllowed(boolean allowed) {
  this.usersAllowed = allowed;
  return this;
 }

 public PrnfbSettingsBuilder withAdminsAllowed(boolean allowed) {
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
