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

 public String getKeyStore() {
  return this.keyStore;
 }

 public String getKeyStorePassword() {
  return this.keyStorePassword;
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

 public void setAdminRestriction(USER_LEVEL adminRestriction) {
  this.adminRestriction = adminRestriction;
 }

 public void setKeyStore(String keyStore) {
  this.keyStore = keyStore;
 }

 public void setKeyStorePassword(String keyStorePassword) {
  this.keyStorePassword = keyStorePassword;
 }

 public void setKeyStoreType(String keyStoreType) {
  this.keyStoreType = keyStoreType;
 }

 public void setShouldAcceptAnyCertificate(boolean shouldAcceptAnyCertificate) {
  this.shouldAcceptAnyCertificate = shouldAcceptAnyCertificate;
 }

 @Override
 public String toString() {
  return "SettingsDataDTO [adminRestriction=" + this.adminRestriction + ", keyStore=" + this.keyStore
    + ", keyStorePassword=" + this.keyStorePassword + ", keyStoreType=" + this.keyStoreType
    + ", shouldAcceptAnyCertificate=" + this.shouldAcceptAnyCertificate + "]";
 }

}
