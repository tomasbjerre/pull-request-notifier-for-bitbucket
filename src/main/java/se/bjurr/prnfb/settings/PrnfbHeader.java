package se.bjurr.prnfb.settings;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.emptyToNull;
import static com.google.common.base.Strings.nullToEmpty;
import static java.util.UUID.randomUUID;

import java.util.UUID;

public class PrnfbHeader {

 private final String name;
 private final UUID uuid;
 private final String value;

 public PrnfbHeader(String name, String value) {
  this.uuid = randomUUID();
  this.name = checkNotNull(emptyToNull(nullToEmpty(name).trim()));
  this.value = checkNotNull(emptyToNull(nullToEmpty(value).trim()));
 }

 public PrnfbHeader(UUID uuid, String name, String value) {
  this.uuid = uuid;
  this.name = checkNotNull(emptyToNull(nullToEmpty(name).trim()));
  this.value = checkNotNull(emptyToNull(nullToEmpty(value).trim()));
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
  PrnfbHeader other = (PrnfbHeader) obj;
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

 @Override
 public String toString() {
  return "PrnfbHeader [uuid=" + this.uuid + ", name=" + this.name + ", value=" + this.value + "]";
 }

}