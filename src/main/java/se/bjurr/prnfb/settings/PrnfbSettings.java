package se.bjurr.prnfb.settings;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

public class PrnfbSettings {
 private List<PrnfbNotification> notifications = newArrayList();
 private final List<PrnfbButton> buttons;
 private final PrnfbSettingsData prnfbSettingsData;

 public PrnfbSettings(PrnfbSettingsBuilder builder) {
  this.notifications = checkNotNull(builder.getNotifications());
  this.buttons = checkNotNull(builder.getButtons());
  this.prnfbSettingsData = checkNotNull(builder.getPrnfbSettingsData(), "prnfbSettingsData");
 }

 public List<PrnfbNotification> getNotifications() {
  return notifications;
 }

 public List<PrnfbButton> getButtons() {
  return buttons;
 }

 public PrnfbSettingsData getPrnfbSettingsData() {
  return prnfbSettingsData;
 }

 @Override
 public int hashCode() {
  final int prime = 31;
  int result = 1;
  result = prime * result + ((buttons == null) ? 0 : buttons.hashCode());
  result = prime * result + ((notifications == null) ? 0 : notifications.hashCode());
  result = prime * result + ((prnfbSettingsData == null) ? 0 : prnfbSettingsData.hashCode());
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
  PrnfbSettings other = (PrnfbSettings) obj;
  if (buttons == null) {
   if (other.buttons != null) {
    return false;
   }
  } else if (!buttons.equals(other.buttons)) {
   return false;
  }
  if (notifications == null) {
   if (other.notifications != null) {
    return false;
   }
  } else if (!notifications.equals(other.notifications)) {
   return false;
  }
  if (prnfbSettingsData == null) {
   if (other.prnfbSettingsData != null) {
    return false;
   }
  } else if (!prnfbSettingsData.equals(other.prnfbSettingsData)) {
   return false;
  }
  return true;
 }

}
