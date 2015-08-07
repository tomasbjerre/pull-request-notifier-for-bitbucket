package se.bjurr.prnfs.settings;

import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

public class PrnfsSettingsBuilder {
 public static PrnfsSettingsBuilder prnfsSettingsBuilder() {
  return new PrnfsSettingsBuilder();
 }

 private final List<PrnfsNotification> notifications = newArrayList();
 private final List<PrnfsButton> buttons = newArrayList();

 private PrnfsSettingsBuilder() {
 }

 public PrnfsSettings build() {
  return new PrnfsSettings(notifications, buttons);
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
}
