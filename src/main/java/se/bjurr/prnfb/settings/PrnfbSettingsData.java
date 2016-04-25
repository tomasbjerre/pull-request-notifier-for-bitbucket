package se.bjurr.prnfb.settings;

import static com.google.common.base.Optional.fromNullable;
import static com.google.common.base.Strings.emptyToNull;

import com.google.common.base.Optional;

public class PrnfbSettingsData {
 private final USER_LEVEL adminRestriction;
 private final String keyStore;
 private final String keyStorePassword;
 private final String keyStoreType;
 private final boolean shouldAcceptAnyCertificate;

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
  if (this.adminRestriction != other.adminRestriction) {
   return false;
  }
  if (this.keyStore == null) {
   if (other.keyStore != null) {
    return false;
   }
  } else if (!this.keyStore.equals(other.keyStore)) {
   return false;
  }
  if (this.keyStorePassword == null) {
   if (other.keyStorePassword != null) {
    return false;
   }
  } else if (!this.keyStorePassword.equals(other.keyStorePassword)) {
   return false;
  }
  if (this.keyStoreType == null) {
   if (other.keyStoreType != null) {
    return false;
   }
  } else if (!this.keyStoreType.equals(other.keyStoreType)) {
   return false;
  }
  if (this.shouldAcceptAnyCertificate != other.shouldAcceptAnyCertificate) {
   return false;
  }
  return true;
 }

 public USER_LEVEL getAdminRestriction() {
  return this.adminRestriction;
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

 @Override
 public int hashCode() {
  final int prime = 31;
  int result = 1;
  result = prime * result + ((this.adminRestriction == null) ? 0 : this.adminRestriction.hashCode());
  result = prime * result + ((this.keyStore == null) ? 0 : this.keyStore.hashCode());
  result = prime * result + ((this.keyStorePassword == null) ? 0 : this.keyStorePassword.hashCode());
  result = prime * result + ((this.keyStoreType == null) ? 0 : this.keyStoreType.hashCode());
  result = prime * result + (this.shouldAcceptAnyCertificate ? 1231 : 1237);
  return result;
 }

 public boolean isShouldAcceptAnyCertificate() {
  return this.shouldAcceptAnyCertificate;
 }

 @Override
 public String toString() {
  return "PrnfbSettingsData [keyStore=" + this.keyStore + ", keyStoreType=" + this.keyStoreType + ", keyStorePassword="
    + this.keyStorePassword + ", shouldAcceptAnyCertificate=" + this.shouldAcceptAnyCertificate + ", adminRestriction="
    + this.adminRestriction + "]";
 }

}