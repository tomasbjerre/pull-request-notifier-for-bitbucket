package se.bjurr.prnfb.settings;

public class PrnfbSettingsDataBuilder {
  public static PrnfbSettingsDataBuilder prnfbSettingsDataBuilder() {
    return new PrnfbSettingsDataBuilder();
  }

  public static PrnfbSettingsDataBuilder prnfbSettingsDataBuilder(PrnfbSettingsData settings) {
    return new PrnfbSettingsDataBuilder(settings);
  }

  private USER_LEVEL adminRestriction;
  private String keyStore;
  private String keyStorePassword;
  private String keyStoreType;
  private boolean shouldAcceptAnyCertificate;

  private PrnfbSettingsDataBuilder() {}

  private PrnfbSettingsDataBuilder(PrnfbSettingsData settings) {
    this.shouldAcceptAnyCertificate = settings.isShouldAcceptAnyCertificate();
    this.keyStore = settings.getKeyStore().orNull();
    this.keyStoreType = settings.getKeyStoreType();
    this.keyStorePassword = settings.getKeyStorePassword().orNull();
  }

  public PrnfbSettingsData build() {
    return new PrnfbSettingsData(this);
  }

  public USER_LEVEL getAdminRestriction() {
    return this.adminRestriction;
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

  public PrnfbSettingsDataBuilder setAdminRestriction(USER_LEVEL adminRestriction) {
    this.adminRestriction = adminRestriction;
    return this;
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

  public PrnfbSettingsDataBuilder setShouldAcceptAnyCertificate(
      boolean shouldAcceptAnyCertificate) {
    this.shouldAcceptAnyCertificate = shouldAcceptAnyCertificate;
    return this;
  }

  public boolean shouldAcceptAnyCertificate() {
    return this.shouldAcceptAnyCertificate;
  }
}
