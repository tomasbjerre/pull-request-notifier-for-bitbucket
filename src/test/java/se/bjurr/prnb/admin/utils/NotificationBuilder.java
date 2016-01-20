package se.bjurr.prnb.admin.utils;

import static se.bjurr.prnfb.admin.AdminFormValues.NAME;
import static se.bjurr.prnfb.admin.AdminFormValues.VALUE;
import se.bjurr.prnfb.admin.AdminFormValues;

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

 public NotificationBuilder withFieldValue(AdminFormValues.FIELDS field, String value) {
  adminFormValues.add(new ImmutableMap.Builder<String, String>().put(NAME, field.name()).put(VALUE, value).build());
  return this;
 }
}
