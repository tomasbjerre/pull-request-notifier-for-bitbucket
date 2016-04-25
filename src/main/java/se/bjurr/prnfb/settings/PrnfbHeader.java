package se.bjurr.prnfb.settings;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.emptyToNull;
import static com.google.common.base.Strings.nullToEmpty;
import static java.util.UUID.randomUUID;

import java.util.UUID;

public class PrnfbHeader {

 private final UUID uuid;
 private final String name;
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

 public String getName() {
  return name;
 }

 public String getValue() {
  return value;
 }

 public UUID getUuid() {
  return uuid;
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
  PrnfbHeader other = (PrnfbHeader) obj;
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

 @Override
 public String toString() {
  return "PrnfbHeader [uuid=" + uuid + ", name=" + name + ", value=" + value + "]";
 }

}