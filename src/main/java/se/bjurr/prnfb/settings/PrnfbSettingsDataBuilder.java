package se.bjurr.prnfb.settings;

public class PrnfbSettingsDataBuilder {
 public static PrnfbSettingsDataBuilder prnfbSettingsDataBuilder() {
  return new PrnfbSettingsDataBuilder();
 }

 public static PrnfbSettingsDataBuilder prnfbSettingsDataBuilder(PrnfbSettingsData settings) {
  return new PrnfbSettingsDataBuilder(settings);
 }

 private boolean shouldAcceptAnyCertificate;
 private String keyStore;
 private String keyStoreType;
 private String keyStorePassword;
 private USER_LEVEL adminRestriction;

 private PrnfbSettingsDataBuilder() {
 }

 private PrnfbSettingsDataBuilder(PrnfbSettingsData settings) {
  this.shouldAcceptAnyCertificate = settings.isShouldAcceptAnyCertificate();
  this.keyStore = settings.getKeyStore().orNull();
  this.keyStoreType = settings.getKeyStoreType();
  this.keyStorePassword = settings.getKeyStorePassword().orNull();
 }

 public PrnfbSettingsData build() {
  return new PrnfbSettingsData(this);
 }

 public PrnfbSettingsDataBuilder setShouldAcceptAnyCertificate(boolean shouldAcceptAnyCertificate) {
  this.shouldAcceptAnyCertificate = shouldAcceptAnyCertificate;
  return this;
 }

 public boolean shouldAcceptAnyCertificate() {
  return shouldAcceptAnyCertificate;
 }

 public String getKeyStore() {
  return keyStore;
 }

 public String getKeyStoreType() {
  return keyStoreType;
 }

 public String getKeyStorePassword() {
  return keyStorePassword;
 }

 public PrnfbSettingsDataBuilder setKeyStore(String keyStore) {
  this.keyStore = keyStore;
  return this;
 }

 public PrnfbSettingsDataBuilder setKeyStorePassword(String keyStorePassword) {
  this.keyStorePassword = keyStorePassword;
  return this;
 }

 public PrnfbSettingsDataBuilder setKeyStoreType(String keyStoreType) {
  this.keyStoreType = keyStoreType;
  return this;
 }

 public PrnfbSettingsDataBuilder setAdminRestriction(USER_LEVEL adminRestriction) {
  this.adminRestriction = adminRestriction;
  return this;
 }

 public USER_LEVEL getAdminRestriction() {
  return adminRestriction;
 }
}
