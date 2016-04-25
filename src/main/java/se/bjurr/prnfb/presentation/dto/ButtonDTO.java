package se.bjurr.prnfb.presentation.dto;

import static javax.xml.bind.annotation.XmlAccessType.FIELD;

import java.util.UUID;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import se.bjurr.prnfb.settings.USER_LEVEL;

@XmlRootElement
@XmlAccessorType(FIELD)
public class ButtonDTO {

 private String title;
 private USER_LEVEL userLevel;
 private UUID uuid;

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
  if (this.title == null) {
   if (other.title != null) {
    return false;
   }
  } else if (!this.title.equals(other.title)) {
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

 public String getTitle() {
  return this.title;
 }

 public USER_LEVEL getUserLevel() {
  return this.userLevel;
 }

 public UUID getUUID() {
  return this.uuid;
 }

 @Override
 public int hashCode() {
  final int prime = 31;
  int result = 1;
  result = prime * result + ((this.title == null) ? 0 : this.title.hashCode());
  result = prime * result + ((this.userLevel == null) ? 0 : this.userLevel.hashCode());
  result = prime * result + ((this.uuid == null) ? 0 : this.uuid.hashCode());
  return result;
 }

 public void setTitle(String title) {
  this.title = title;
 }

 public void setUserLevel(USER_LEVEL userLevel) {
  this.userLevel = userLevel;
 }

 public void setUuid(UUID uuid) {
  this.uuid = uuid;
 }

 @Override
 public String toString() {
  return "ButtonDTO [title=" + this.title + ", userLevel=" + this.userLevel + ", uuid=" + this.uuid + "]";
 }

}
