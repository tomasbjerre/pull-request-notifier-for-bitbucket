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

 public void setTitle(String title) {
  this.title = title;
 }

 public void setUserLevel(USER_LEVEL userLevel) {
  this.userLevel = userLevel;
 }

 public void setUuid(UUID uuid) {
  this.uuid = uuid;
 }

 public String getTitle() {
  return title;
 }

 public USER_LEVEL getUserLevel() {
  return userLevel;
 }

 public UUID getUUID() {
  return uuid;
 }

 @Override
 public String toString() {
  return "ButtonDTO [title=" + title + ", userLevel=" + userLevel + ", uuid=" + uuid + "]";
 }

 @Override
 public int hashCode() {
  final int prime = 31;
  int result = 1;
  result = prime * result + ((title == null) ? 0 : title.hashCode());
  result = prime * result + ((userLevel == null) ? 0 : userLevel.hashCode());
  result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
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
  ButtonDTO other = (ButtonDTO) obj;
  if (title == null) {
   if (other.title != null) {
    return false;
   }
  } else if (!title.equals(other.title)) {
   return false;
  }
  if (userLevel != other.userLevel) {
   return false;
  }
  if (uuid == null) {
   if (other.uuid != null) {
    return false;
   }
  } else if (!uuid.equals(other.uuid)) {
   return false;
  }
  return true;
 }

}
