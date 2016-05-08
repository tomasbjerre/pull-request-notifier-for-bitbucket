package se.bjurr.prnfb.settings.legacy;

import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

@Deprecated
public class PrnfbSettingsBuilder {
 public static PrnfbSettingsBuilder prnfbSettingsBuilder() {
  return new PrnfbSettingsBuilder();
 }

 private boolean adminsAllowed;
 private final List<PrnfbButton> buttons = newArrayList();
 private String keyStore;
 private String keyStorePassword;
 private String keyStoreType;
 private final List<PrnfbNotification> notifications = newArrayList();
 private boolean shouldAcceptAnyCertificate;
 private boolean usersAllowed;

 private PrnfbSettingsBuilder() {
 }

 public PrnfbSettings build() {
  return new PrnfbSettings(this);
 }

 public List<PrnfbButton> getButtons() {
  return this.buttons;
 }

 public String getKeyStore() {
  return this.keyStore;
 }

 public String getKeyStorePassword() {
  return this.keyStorePassword;
 }

 public String getKeyStoreType() {
  return this.keyStoreType;
 }

 public List<PrnfbNotification> getNotifications() {
  return this.notifications;
 }

 public boolean isAdminsAllowed() {
  return this.adminsAllowed;
 }

 public boolean isUsersAllowed() {
  return this.usersAllowed;
 }

 public PrnfbSettingsBuilder setKeyStore(String keyStore) {
  this.keyStore = keyStore;
  return this;
 }

 public PrnfbSettingsBuilder setKeyStorePassword(String keyStorePassword) {
  this.keyStorePassword = keyStorePassword;
  return this;
 }

 public PrnfbSettingsBuilder setKeyStoreType(String keyStoreType) {
  this.keyStoreType = keyStoreType;
  return this;
 }

 public boolean shouldAcceptAnyCertificate() {
  return this.shouldAcceptAnyCertificate;
 }

 public PrnfbSettingsBuilder withAdminsAllowed(boolean allowed) {
  this.adminsAllowed = allowed;
  return this;
 }

 public void withButton(PrnfbButton prnfbButton) {
  this.buttons.add(prnfbButton);
 }

 public PrnfbSettingsBuilder withNotification(PrnfbNotification notification) {
  this.notifications.add(notification);
  return this;
 }

 public PrnfbSettingsBuilder withShouldAcceptAnyCertificate(boolean shouldAcceptAnyCertificate) {
  this.shouldAcceptAnyCertificate = shouldAcceptAnyCertificate;
  return this;
 }

 public PrnfbSettingsBuilder withUsersAllowed(boolean allowed) {
  this.usersAllowed = allowed;
  return this;
 }
}
