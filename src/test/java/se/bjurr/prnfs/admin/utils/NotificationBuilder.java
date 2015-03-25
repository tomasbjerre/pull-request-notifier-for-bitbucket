package se.bjurr.prnfs.admin.utils;

import static se.bjurr.prnfs.admin.AdminFormValues.NAME;
import static se.bjurr.prnfs.admin.AdminFormValues.VALUE;
import se.bjurr.prnfs.admin.AdminFormValues;

import com.google.common.collect.ImmutableMap;

public class NotificationBuilder {

 public static NotificationBuilder notificationBuilder() {
  return new NotificationBuilder();
 }

 private final AdminFormValues adminFormValues;

 private NotificationBuilder() {
  adminFormValues = new AdminFormValues();
 }

 public AdminFormValues build() {
  return adminFormValues;
 }

 public NotificationBuilder withFieldValue(String field, String value) {
  adminFormValues.add(new ImmutableMap.Builder<String, String>().put(NAME, field).put(VALUE, value).build());
  return this;
 }
}
