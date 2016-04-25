package se.bjurr.prnfb.presentation.dto;

import static javax.xml.bind.annotation.XmlAccessType.FIELD;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import se.bjurr.prnfb.settings.USER_LEVEL;

@XmlRootElement
@XmlAccessorType(FIELD)
public class SettingsDataDTO {

 private USER_LEVEL adminRestriction;
 private String keyStore;
 private String keyStorePassword;
 private String keyStoreType;
 private boolean shouldAcceptAnyCertificate;

 public void setShouldAcceptAnyCertificate(boolean shouldAcceptAnyCertificate) {
  this.shouldAcceptAnyCertificate = shouldAcceptAnyCertificate;
 }

 public boolean isShouldAcceptAnyCertificate() {
  return shouldAcceptAnyCertificate;
 }

 public void setAdminRestriction(USER_LEVEL adminRestriction) {
  this.adminRestriction = adminRestriction;
 }

 public USER_LEVEL getAdminRestriction() {
  return adminRestriction;
 }

 public void setKeyStore(String keyStore) {
  this.keyStore = keyStore;
 }

 public String getKeyStore() {
  return keyStore;
 }

 public void setKeyStorePassword(String keyStorePassword) {
  this.keyStorePassword = keyStorePassword;
 }

 public String getKeyStorePassword() {
  return keyStorePassword;
 }

 public void setKeyStoreType(String keyStoreType) {
  this.keyStoreType = keyStoreType;
 }

 public String getKeyStoreType() {
  return keyStoreType;
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
  SettingsDataDTO other = (SettingsDataDTO) obj;
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
  return "SettingsDataDTO [adminRestriction=" + adminRestriction + ", keyStore=" + keyStore + ", keyStorePassword="
    + keyStorePassword + ", keyStoreType=" + keyStoreType + ", shouldAcceptAnyCertificate="
    + shouldAcceptAnyCertificate + "]";
 }

}
