package se.bjurr.prnfb.settings.legacy;

import static com.google.common.base.Optional.fromNullable;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.emptyToNull;
import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

import com.google.common.base.Optional;

@Deprecated
public class PrnfbSettings {
  private final boolean adminsAllowed;
  private final List<PrnfbButton> buttons;
  private final String keyStore;
  private final String keyStorePassword;
  private final String keyStoreType;
  private List<PrnfbNotification> notifications = newArrayList();
  private final boolean shouldAcceptAnyCertificate;
  private final boolean usersAllowed;

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

  public List<PrnfbButton> getButtons() {
    return this.buttons;
  }

  public Optional<String> getKeyStore() {
    return fromNullable(this.keyStore);
  }

  public Optional<String> getKeyStorePassword() {
    return fromNullable(this.keyStorePassword);
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

  public boolean shouldAcceptAnyCertificate() {
    return this.shouldAcceptAnyCertificate;
  }
}
