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
  if (this.name == null) {
   if (other.name != null) {
    return false;
   }
  } else if (!this.name.equals(other.name)) {
   return false;
  }
  if (this.uuid == null) {
   if (other.uuid != null) {
    return false;
   }
  } else if (!this.uuid.equals(other.uuid)) {
   return false;
  }
  if (this.value == null) {
   if (other.value != null) {
    return false;
   }
  } else if (!this.value.equals(other.value)) {
   return false;
  }
  return true;
 }

 public String getName() {
  return this.name;
 }

 public UUID getUuid() {
  return this.uuid;
 }

 public String getValue() {
  return this.value;
 }

 @Override
 public int hashCode() {
  final int prime = 31;
  int result = 1;
  result = prime * result + ((this.name == null) ? 0 : this.name.hashCode());
  result = prime * result + ((this.uuid == null) ? 0 : this.uuid.hashCode());
  result = prime * result + ((this.value == null) ? 0 : this.value.hashCode());
  return result;
 }

 public void setName(String name) {
  this.name = name;
 }

 public void setUuid(UUID uuid) {
  this.uuid = uuid;
 }

 public void setValue(String value) {
  this.value = value;
 }

 @Override
 public String toString() {
  return "HeaderDTO [name=" + this.name + ", uuid=" + this.uuid + ", value=" + this.value + "]";
 }

}
