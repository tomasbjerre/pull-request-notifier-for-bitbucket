package se.bjurr.prnfb.settings;

import static com.google.common.base.Optional.fromNullable;
import static com.google.common.base.Strings.emptyToNull;

import com.google.common.base.Optional;

public class PrnfbSettingsData {
 private final String keyStore;
 private final String keyStoreType;
 private final String keyStorePassword;
 private final boolean shouldAcceptAnyCertificate;
 private final USER_LEVEL adminRestriction;

 public PrnfbSettingsData() {
  this.keyStore = null;
  this.keyStoreType = null;
  this.keyStorePassword = null;
  this.shouldAcceptAnyCertificate = false;
  this.adminRestriction = null;
 }

 public PrnfbSettingsData(PrnfbSettingsDataBuilder builder) {
  this.keyStore = emptyToNull(builder.getKeyStore());
  this.keyStoreType = builder.getKeyStoreType();
  this.keyStorePassword = emptyToNull(builder.getKeyStorePassword());
  this.shouldAcceptAnyCertificate = builder.shouldAcceptAnyCertificate();
  this.adminRestriction = builder.getAdminRestriction();
 }

 public Optional<String> getKeyStore() {
  return fromNullable(keyStore);
 }

 public USER_LEVEL getAdminRestriction() {
  return adminRestriction;
 }

 public Optional<String> getKeyStorePassword() {
  return fromNullable(keyStorePassword);
 }

 public String getKeyStoreType() {
  return keyStoreType;
 }

 public boolean isShouldAcceptAnyCertificate() {
  return shouldAcceptAnyCertificate;
 }

 @Override
 public int hashCode() {
  final int prime = 31;
  int result = 1;
  result = prime * result + ((adminRestriction == null) ? 0 : adminRestriction.hashCode());
  result = prime * result + ((keyStore == null) ? 0 : keyStore.hashCode());
  result = prime * result + ((keyStorePassword == null) ? 0 : keyStorePassword.hashCode());
  result = prime * result + ((keyStoreType == null) ? 0 : keyStoreType.hashCode());
  result = prime * result + (shouldAcceptAnyCertificate ? 1231 : 1237);
  return result;
 }

 @Override
 public boolean equals(Object obj) {
  if (this == obj) {
   return true;
  }
  if (obj == null) {
   return false;
  }
  if (getClass() != obj.getClass()) {
   return false;
  }
  PrnfbSettingsData other = (PrnfbSettingsData) obj;
  if (adminRestriction != other.adminRestriction) {
   return false;
  }
  if (keyStore == null) {
   if (other.keyStore != null) {
    return false;
   }
  } else if (!keyStore.equals(other.keyStore)) {
   return false;
  }
  if (keyStorePassword == null) {
   if (other.keyStorePassword != null) {
    return false;
   }
  } else if (!keyStorePassword.equals(other.keyStorePassword)) {
   return false;
  }
  if (keyStoreType == null) {
   if (other.keyStoreType != null) {
    return false;
   }
  } else if (!keyStoreType.equals(other.keyStoreType)) {
   return false;
  }
  if (shouldAcceptAnyCertificate != other.shouldAcceptAnyCertificate) {
   return false;
  }
  return true;
 }

 @Override
 public String toString() {
  return "PrnfbSettingsData [keyStore=" + keyStore + ", keyStoreType=" + keyStoreType + ", keyStorePassword="
    + keyStorePassword + ", shouldAcceptAnyCertificate=" + shouldAcceptAnyCertificate + ", adminRestriction="
    + adminRestriction + "]";
 }

}