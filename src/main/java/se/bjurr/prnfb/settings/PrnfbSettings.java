package se.bjurr.prnfb.settings;

import static com.google.common.base.Optional.fromNullable;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.emptyToNull;
import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

import com.google.common.base.Optional;

public class PrnfbSettings {
 private List<PrnfbNotification> notifications = newArrayList();
 private final List<PrnfbButton> buttons;
 private final boolean usersAllowed;
 private final boolean adminsAllowed;
 private final String keyStore;
 private final String keyStoreType;
 private final String keyStorePassword;
 private final boolean shouldAcceptAnyCertificate;

 public PrnfbSettings(PrnfbSettingsBuilder builder) {
  this.notifications = checkNotNull(builder.getNotifications());
  this.buttons = checkNotNull(builder.getButtons());
  this.usersAllowed = builder.isUsersAllowed();
  this.adminsAllowed = builder.isAdminsAllowed();
  this.keyStore = emptyToNull(builder.getKeyStore());
  this.keyStoreType = builder.getKeyStoreType();
  this.keyStorePassword = emptyToNull(builder.getKeyStorePassword());
  this.shouldAcceptAnyCertificate = builder.shouldAcceptAnyCertificate();
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

 public Optional<String> getKeyStore() {
  return fromNullable(keyStore);
 }

 public String getKeyStoreType() {
  return keyStoreType;
 }

 public Optional<String> getKeyStorePassword() {
  return fromNullable(keyStorePassword);
 }

 public boolean shouldAcceptAnyCertificate() {
  return shouldAcceptAnyCertificate;
 }
}
