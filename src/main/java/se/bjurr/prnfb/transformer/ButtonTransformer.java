package se.bjurr.prnfb.transformer;

import static com.google.common.collect.Lists.newArrayList;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import se.bjurr.prnfb.http.NotificationResponse;
import se.bjurr.prnfb.presentation.dto.ButtonDTO;
import se.bjurr.prnfb.presentation.dto.NotificationResponseDTO;
import se.bjurr.prnfb.settings.PrnfbButton;

public class ButtonTransformer {

 public static ButtonDTO toButtonDto(PrnfbButton from) {
  ButtonDTO to = new ButtonDTO();
  to.setName(from.getName());
  to.setUserLevel(from.getUserLevel());
  to.setUuid(from.getUuid());
  to.setProjectKey(from.getProjectKey().orNull());
  to.setRepositorySlug(from.getRepositorySlug().orNull());
  to.setConfirmation(from.getConfirmation());
  return to;
 }

 public static List<ButtonDTO> toButtonDtoList(Iterable<PrnfbButton> allowedButtons) {
  List<ButtonDTO> to = newArrayList();
  for (PrnfbButton from : allowedButtons) {
   to.add(toButtonDto(from));
  }
  return to;
 }

 public static PrnfbButton toPrnfbButton(ButtonDTO buttonDto) {
  return new PrnfbButton(//
    buttonDto.getUUID(), //
    buttonDto.getName(), //
    buttonDto.getUserLevel(),//
    buttonDto.getConfirmation(),//
    buttonDto.getProjectKey().orNull(),//
    buttonDto.getRepositorySlug().orNull());//
 }

 public static List<NotificationResponseDTO> toTriggerResultDto(List<NotificationResponse> results) {
  List<NotificationResponseDTO> to = newArrayList();
  for (NotificationResponse from : results) {
   String content = from.getHttpResponse().getContent();
   int status = from.getHttpResponse().getStatus();
   UUID notification = from.getNotification();
   String notificationName = from.getNotificationName();
   URI uri = from.getHttpResponse().getUri();
   to.add(new NotificationResponseDTO(uri, content, status, notification, notificationName));
  }
  return to;
 }
}
