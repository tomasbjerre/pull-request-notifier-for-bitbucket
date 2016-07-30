package se.bjurr.prnfb.presentation.dto;

import static javax.xml.bind.annotation.XmlAccessType.FIELD;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(FIELD)
public class ButtonPressDTO {
 private final ON_OR_OFF confirmation;
 private final List<NotificationResponseDTO> notificationResponses;

 public ButtonPressDTO(ON_OR_OFF confirmation, List<NotificationResponseDTO> notificationResponses) {
  this.confirmation = confirmation;
  this.notificationResponses = notificationResponses;
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
  ButtonPressDTO other = (ButtonPressDTO) obj;
  if (this.confirmation != other.confirmation) {
   return false;
  }
  if (this.notificationResponses == null) {
   if (other.notificationResponses != null) {
    return false;
   }
  } else if (!this.notificationResponses.equals(other.notificationResponses)) {
   return false;
  }
  return true;
 }

 public ON_OR_OFF getConfirmation() {
  return this.confirmation;
 }

 public List<NotificationResponseDTO> getNotificationResponses() {
  return this.notificationResponses;
 }

 @Override
 public int hashCode() {
  final int prime = 31;
  int result = 1;
  result = prime * result + ((this.confirmation == null) ? 0 : this.confirmation.hashCode());
  result = prime * result + ((this.notificationResponses == null) ? 0 : this.notificationResponses.hashCode());
  return result;
 }

 @Override
 public String toString() {
  return "ButtonPressDTO [confirmation=" + this.confirmation + ", notificationResponses=" + this.notificationResponses
    + "]";
 }
}
