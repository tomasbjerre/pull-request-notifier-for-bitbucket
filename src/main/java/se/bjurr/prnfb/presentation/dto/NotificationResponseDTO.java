package se.bjurr.prnfb.presentation.dto;

import static javax.xml.bind.annotation.XmlAccessType.FIELD;

import java.net.URI;
import java.util.UUID;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(FIELD)
public class NotificationResponseDTO implements Comparable<NotificationResponseDTO> {
 private final String content;
 private final UUID notification;
 private final String notificationName;
 private final int status;
 private final URI uri;

 public NotificationResponseDTO(URI uri, String content, int status, UUID notification, String notificationName) {
  this.content = content;
  this.status = status;
  this.notification = notification;
  this.notificationName = notificationName;
  this.uri = uri;
 }

 @Override
 public int compareTo(NotificationResponseDTO o) {
  return this.notificationName.compareTo(o.notificationName);
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
  NotificationResponseDTO other = (NotificationResponseDTO) obj;
  if (this.content == null) {
   if (other.content != null) {
    return false;
   }
  } else if (!this.content.equals(other.content)) {
   return false;
  }
  if (this.notification == null) {
   if (other.notification != null) {
    return false;
   }
  } else if (!this.notification.equals(other.notification)) {
   return false;
  }
  if (this.notificationName == null) {
   if (other.notificationName != null) {
    return false;
   }
  } else if (!this.notificationName.equals(other.notificationName)) {
   return false;
  }
  if (this.status != other.status) {
   return false;
  }
  if (this.uri == null) {
   if (other.uri != null) {
    return false;
   }
  } else if (!this.uri.equals(other.uri)) {
   return false;
  }
  return true;
 }

 public URI getUri() {
  return this.uri;
 }

 @Override
 public int hashCode() {
  final int prime = 31;
  int result = 1;
  result = prime * result + ((this.content == null) ? 0 : this.content.hashCode());
  result = prime * result + ((this.notification == null) ? 0 : this.notification.hashCode());
  result = prime * result + ((this.notificationName == null) ? 0 : this.notificationName.hashCode());
  result = prime * result + this.status;
  result = prime * result + ((this.uri == null) ? 0 : this.uri.hashCode());
  return result;
 }

 @Override
 public String toString() {
  return "NotificationResponseDTO [content=" + this.content + ", notification=" + this.notification
    + ", notificationName=" + this.notificationName + ", status=" + this.status + ", uri=" + this.uri + "]";
 }

}
