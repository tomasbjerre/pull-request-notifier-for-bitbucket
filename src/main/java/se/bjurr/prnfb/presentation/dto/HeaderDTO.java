package se.bjurr.prnfb.presentation.dto;

import static javax.xml.bind.annotation.XmlAccessType.FIELD;

import java.util.UUID;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(FIELD)
public class HeaderDTO {

 private String name;
 private UUID uuid;
 private String value;

 public void setName(String name) {
  this.name = name;
 }

 public void setUuid(UUID uuid) {
  this.uuid = uuid;
 }

 public void setValue(String value) {
  this.value = value;
 }

 public String getName() {
  return name;
 }

 public UUID getUuid() {
  return uuid;
 }

 public String getValue() {
  return value;
 }

 @Override
 public String toString() {
  return "HeaderDTO [name=" + name + ", uuid=" + uuid + ", value=" + value + "]";
 }

 @Override
 public int hashCode() {
  final int prime = 31;
  int result = 1;
  result = prime * result + ((name == null) ? 0 : name.hashCode());
  result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
  result = prime * result + ((value == null) ? 0 : value.hashCode());
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
  HeaderDTO other = (HeaderDTO) obj;
  if (name == null) {
   if (other.name != null) {
    return false;
   }
  } else if (!name.equals(other.name)) {
   return false;
  }
  if (uuid == null) {
   if (other.uuid != null) {
    return false;
   }
  } else if (!uuid.equals(other.uuid)) {
   return false;
  }
  if (value == null) {
   if (other.value != null) {
    return false;
   }
  } else if (!value.equals(other.value)) {
   return false;
  }
  return true;
 }

}
