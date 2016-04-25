package se.bjurr.prnfb.settings;

import static java.util.UUID.randomUUID;

import java.util.UUID;

public class PrnfbButton implements HasUuid {

 private final String title;
 private final USER_LEVEL userLevel;
 private final UUID uuid;

 public PrnfbButton(String title, USER_LEVEL userLevel) {
  this.uuid = randomUUID();
  this.title = title;
  this.userLevel = userLevel;
 }

 public PrnfbButton(UUID uuid, String title, USER_LEVEL userLevel) {
  this.uuid = uuid;
  this.title = title;
  this.userLevel = userLevel;
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
  if (this.title == null) {
   if (other.title != null) {
    return false;
   }
  } else if (!this.title.equals(other.title)) {
   return false;
  }
  if (this.uuid == null) {
   if (other.uuid != null) {
    return false;
   }
  } else if (!this.uuid.equals(other.uuid)) {
   return false;
  }
  if (this.userLevel != other.userLevel) {
   return false;
  }
  return true;
 }

 public String getTitle() {
  return this.title;
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
  result = prime * result + ((this.title == null) ? 0 : this.title.hashCode());
  result = prime * result + ((this.uuid == null) ? 0 : this.uuid.hashCode());
  result = prime * result + ((this.userLevel == null) ? 0 : this.userLevel.hashCode());
  return result;
 }

 @Override
 public String toString() {
  return "PrnfbButton [uuid=" + this.uuid + ", title=" + this.title + ", visibility=" + this.userLevel + "]";
 }

}
