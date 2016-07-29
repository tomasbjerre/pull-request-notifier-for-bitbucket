package se.bjurr.prnfb.http;

import java.util.UUID;

public class NotificationResponse {
 private final HttpResponse httpResponse;
 private final UUID notification;
 private final String notificationName;

 public NotificationResponse(UUID notification, String notificationName, HttpResponse httpResponse) {
  this.notification = notification;
  this.notificationName = notificationName;
  this.httpResponse = httpResponse;
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
  NotificationResponse other = (NotificationResponse) obj;
  if (this.httpResponse == null) {
   if (other.httpResponse != null) {
    return false;
   }
  } else if (!this.httpResponse.equals(other.httpResponse)) {
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
  return true;
 }

 public HttpResponse getHttpResponse() {
  return this.httpResponse;
 }

 public UUID getNotification() {
  return this.notification;
 }

 public String getNotificationName() {
  return this.notificationName;
 }

 @Override
 public int hashCode() {
  final int prime = 31;
  int result = 1;
  result = prime * result + ((this.httpResponse == null) ? 0 : this.httpResponse.hashCode());
  result = prime * result + ((this.notification == null) ? 0 : this.notification.hashCode());
  result = prime * result + ((this.notificationName == null) ? 0 : this.notificationName.hashCode());
  return result;
 }

 @Override
 public String toString() {
  return "NotificationResponse [httpResponse=" + this.httpResponse + ", notification=" + this.notification
    + ", notificationName=" + this.notificationName + "]";
 }

}
