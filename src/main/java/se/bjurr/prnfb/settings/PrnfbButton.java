package se.bjurr.prnfb.settings;

import static com.google.common.base.MoreObjects.firstNonNull;
import static com.google.common.base.Optional.fromNullable;
import static com.google.common.base.Strings.emptyToNull;
import static java.util.UUID.randomUUID;

import java.util.UUID;

import com.google.common.base.Optional;

public class PrnfbButton implements HasUuid {

 private final String name;
 private final String projectKey;
 private final String repositorySlug;
 private final USER_LEVEL userLevel;
 private final UUID uuid;

 public PrnfbButton(UUID uuid, String name, USER_LEVEL userLevel, String projectKey, String repositorySlug) {
  this.uuid = firstNonNull(uuid, randomUUID());
  this.name = name;
  this.userLevel = userLevel;
  this.repositorySlug = emptyToNull(repositorySlug);
  this.projectKey = emptyToNull(projectKey);
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
  PrnfbButton other = (PrnfbButton) obj;
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
  if (this.uuid == null) {
   if (other.uuid != null) {
    return false;
   }
  } else if (!this.uuid.equals(other.uuid)) {
   return false;
  }
  return true;
 }

 public String getName() {
  return this.name;
 }

 public Optional<String> getProjectKey() {
  return fromNullable(this.projectKey);
 }

 public Optional<String> getRepositorySlug() {
  return fromNullable(this.repositorySlug);
 }

 public USER_LEVEL getUserLevel() {
  return this.userLevel;
 }

 @Override
 public UUID getUuid() {
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
  return result;
 }

 @Override
 public String toString() {
  return "PrnfbButton [projectKey=" + this.projectKey + ", repositorySlug=" + this.repositorySlug + ", name="
    + this.name + ", userLevel=" + this.userLevel + ", uuid=" + this.uuid + "]";
 }

}
