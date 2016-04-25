package se.bjurr.prnfb.transformer;

import static com.google.common.collect.Lists.newArrayList;
import static se.bjurr.prnfb.settings.PrnfbNotificationBuilder.prnfbNotificationBuilder;

import java.util.List;

import se.bjurr.prnfb.presentation.dto.HeaderDTO;
import se.bjurr.prnfb.presentation.dto.NotificationDTO;
import se.bjurr.prnfb.settings.PrnfbHeader;
import se.bjurr.prnfb.settings.PrnfbNotification;
import se.bjurr.prnfb.settings.ValidationException;

public class NotificationTransformer {

 public static List<NotificationDTO> toNotificationDtoList(List<PrnfbNotification> from) {
  List<NotificationDTO> to = newArrayList();
  for (PrnfbNotification n : from) {
   to.add(toNotificationDto(n));
  }
  return to;
 }

 public static NotificationDTO toNotificationDto(PrnfbNotification from) {
  NotificationDTO to = new NotificationDTO();
  to.setFilterRegexp(from.getFilterRegexp().orNull());
  to.setFilterString(from.getFilterString().orNull());
  to.setHeaders(toHeaderDtoList(from.getHeaders()));
  to.setInjectionUrl(from.getInjectionUrl().orNull());
  to.setInjectionUrlRegexp(from.getInjectionUrlRegexp().orNull());
  to.setMethod(from.getMethod());
  to.setName(from.getName());
  to.setPassword(from.getPassword().orNull());
  to.setPostContent(from.getPostContent().orNull());
  to.setProxyPassword(from.getProxyPassword().orNull());
  to.setProxyPort(from.getProxyPort());
  to.setProxyServer(from.getProxyServer().orNull());
  to.setProxyUser(from.getProxyUser().orNull());
  to.setTriggerIfCanMerge(from.getTriggerIfCanMerge());
  to.setTriggerIgnoreStateList(from.getTriggerIgnoreStateList());
  to.setTriggers(from.getTriggers());
  to.setUrl(from.getUrl());
  to.setUser(from.getUser().orNull());
  to.setUuid(from.getUuid());
  return to;
 }

 public static List<HeaderDTO> toHeaderDtoList(List<PrnfbHeader> headers) {
  List<HeaderDTO> to = newArrayList();
  for (PrnfbHeader h : headers) {
   to.add(toHeaderDto(h));
  }
  return to;
 }

 public static HeaderDTO toHeaderDto(PrnfbHeader h) {
  HeaderDTO to = new HeaderDTO();
  to.setName(h.getName());
  to.setUuid(h.getUuid());
  to.setValue(h.getValue());
  return to;
 }

 public static PrnfbNotification toPrnfbNotification(NotificationDTO from) throws ValidationException {
  return prnfbNotificationBuilder()//
    .withFilterRegexp(from.getFilterRegexp())//
    .withFilterString(from.getFilterString())//
    .setHeaders(toHeaders(from.getHeaders()))//
    .withInjectionUrl(from.getInjectionUrl())//
    .withInjectionUrlRegexp(from.getInjectionUrlRegexp())//
    .withMethod(from.getMethod())//
    .withName(from.getName())//
    .withPassword(from.getPassword())//
    .withPostContent(from.getPostContent())//
    .withProxyPassword(from.getProxyPassword())//
    .withProxyPort(from.getProxyPort())//
    .withProxyServer(from.getProxyServer())//
    .withProxyUser(from.getProxyUser())//
    .setTriggers(from.getTriggers())//
    .withTriggerIfCanMerge(from.getTriggerIfCanMerge())//
    .setTriggerIgnoreState(from.getTriggerIgnoreStateList())//
    .withUrl(from.getUrl())//
    .withUser(from.getUser())//
    .withUuid(from.getUuid())//
    .build();
 }

 private static List<PrnfbHeader> toHeaders(List<HeaderDTO> headerDtos) {
  List<PrnfbHeader> to = newArrayList();
  for (HeaderDTO h : headerDtos) {
   to.add(new PrnfbHeader(h.getUuid(), h.getName(), h.getValue()));
  }
  return to;
 }

}
