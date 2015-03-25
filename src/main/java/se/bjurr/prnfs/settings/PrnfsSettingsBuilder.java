package se.bjurr.prnfs.settings;

import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

public class PrnfsSettingsBuilder {
 public static PrnfsSettingsBuilder prnfsSettingsBuilder() {
  return new PrnfsSettingsBuilder();
 }

 private final List<PrnfsNotification> notifications = newArrayList();

 private PrnfsSettingsBuilder() {
 }

 public PrnfsSettings build() {
  return new PrnfsSettings(notifications);
 }

 public PrnfsSettingsBuilder withNotification(PrnfsNotification notification) {
  this.notifications.add(notification);
  return this;
 }
}
