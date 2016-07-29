package se.bjurr.prnfb.presentation.dto;

import static javax.xml.bind.annotation.XmlAccessType.FIELD;

import java.util.UUID;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import se.bjurr.prnfb.settings.USER_LEVEL;

import com.google.common.base.Optional;

@XmlRootElement
@XmlAccessorType(FIELD)
public class ButtonDTO implements Comparable<ButtonDTO> {

 private ON_OR_OFF confirmation;
 private String name;
 private String projectKey;
 private String repositorySlug;
 private USER_LEVEL userLevel;
 private UUID uuid;

 @Override
 public int compareTo(ButtonDTO o) {
  return this.name.compareTo(o.name);
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
  ButtonDTO other = (ButtonDTO) obj;
  if (this.projectKey == null) {
   if (other.projectKey != null) {
    return false;
   }
  } else if (!this.projectKey.equals(other.projectKey)) {
   return false;
  }
  if (this.repositorySlug == null) {
   if (other.repositorySlug != null) {
    return false;
   }
  } else if (!this.repositorySlug.equals(other.repositorySlug)) {
   return false;
  }
  if (this.name == null) {
   if (other.name != null) {
    return false;
   }
  } else if (!this.name.equals(other.name)) {
   return false;
  }
  if (this.userLevel != other.userLevel) {
   return false;
  }
  if (this.confirmation == null) {
   if (other.confirmation != null) {
    return false;
   }
  } else if (!this.confirmation.equals(other.confirmation)) {
   return false;
  }
  if (this.uuid == null) {
   if (other.uuid != null) {
    return false;
   }
  } else if (!this.uuid.equals(other.uuid)) {
   return false;
  }
  return true;
 }

 public ON_OR_OFF getConfirmation() {
  return this.confirmation;
 }

 public String getName() {
  return this.name;
 }

 public Optional<String> getProjectKey() {
  return Optional.fromNullable(this.projectKey);
 }

 public Optional<String> getRepositorySlug() {
  return Optional.fromNullable(this.repositorySlug);
 }

 public USER_LEVEL getUserLevel() {
  return this.userLevel;
 }

 public UUID getUuid() {
  return this.uuid;
 }

 public UUID getUUID() {
  return this.uuid;
 }

 @Override
 public int hashCode() {
  final int prime = 31;
  int result = 1;
  result = prime * result + ((this.projectKey == null) ? 0 : this.projectKey.hashCode());
  result = prime * result + ((this.repositorySlug == null) ? 0 : this.repositorySlug.hashCode());
  result = prime * result + ((this.name == null) ? 0 : this.name.hashCode());
  result = prime * result + ((this.userLevel == null) ? 0 : this.userLevel.hashCode());
  result = prime * result + ((this.uuid == null) ? 0 : this.uuid.hashCode());
  result = prime * result + ((this.confirmation == null) ? 0 : this.confirmation.hashCode());
  return result;
 }

 public void setConfirmation(ON_OR_OFF confirmation) {
  this.confirmation = confirmation;
 }

 public void setName(String name) {
  this.name = name;
 }

 public void setProjectKey(String projectKey) {
  this.projectKey = projectKey;
 }

 public void setRepositorySlug(String repositorySlug) {
  this.repositorySlug = repositorySlug;
 }

 public void setUserLevel(USER_LEVEL userLevel) {
  this.userLevel = userLevel;
 }

 public void setUuid(UUID uuid) {
  this.uuid = uuid;
 }

 @Override
 public String toString() {
  return "ButtonDTO [name=" + this.name + ", userLevel=" + this.userLevel + ", uuid=" + this.uuid + ", repositorySlug="
    + this.repositorySlug + ", projectKey=" + this.projectKey + ", confirmation=" + this.confirmation + "]";
 }

}
