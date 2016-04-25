package se.bjurr.prnfb.presentation;

import static com.google.common.base.Throwables.propagate;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.TEXT_PLAIN;
import static javax.ws.rs.core.Response.ok;
import static javax.ws.rs.core.Response.status;
import static javax.ws.rs.core.Response.Status.OK;
import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;
import static se.bjurr.prnfb.transformer.NotificationTransformer.toNotificationDto;
import static se.bjurr.prnfb.transformer.NotificationTransformer.toNotificationDtoList;
import static se.bjurr.prnfb.transformer.NotificationTransformer.toPrnfbNotification;

import java.util.List;
import java.util.UUID;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import se.bjurr.prnfb.presentation.dto.NotificationDTO;
import se.bjurr.prnfb.service.SettingsService;
import se.bjurr.prnfb.service.UserCheckService;
import se.bjurr.prnfb.settings.PrnfbNotification;

import com.atlassian.annotations.security.XsrfProtectionExcluded;

@Path("/settings/notifications")
public class NotificationServlet {
 private final SettingsService settingsService;
 private final UserCheckService userCheckService;

 public NotificationServlet(SettingsService settingsService, UserCheckService userCheckService) {
  this.settingsService = settingsService;
  this.userCheckService = userCheckService;
 }

 @POST
 @XsrfProtectionExcluded
 @Produces(TEXT_PLAIN)
 public Response create(NotificationDTO notificationDto) {
  if (!this.userCheckService.isAdminAllowed()) {
   return status(UNAUTHORIZED).build();
  }
  try {
   PrnfbNotification prnfbNotification = toPrnfbNotification(notificationDto);
   this.settingsService.addOrUpdateNotification(prnfbNotification);
  } catch (Exception e) {
   propagate(e);
  }
  return status(OK).build();
 }

 @DELETE
 @Path("{uuid}")
 @XsrfProtectionExcluded
 @Produces(APPLICATION_JSON)
 public Response delete(@PathParam("uuid") UUID notification) {
  if (!this.userCheckService.isAdminAllowed()) {
   return status(UNAUTHORIZED).build();
  }
  this.settingsService.deleteNotification(notification);
  return status(OK).build();
 }

 @GET
 @Produces(APPLICATION_JSON)
 public Response get() {
  if (!this.userCheckService.isViewAllowed()) {
   return status(UNAUTHORIZED).build();
  }
  List<PrnfbNotification> notifications = this.settingsService.getNotifications();
  List<NotificationDTO> dtos = toNotificationDtoList(notifications);
  return ok(dtos).build();
 }

 @GET
 @Path("{uuid}")
 @Produces(APPLICATION_JSON)
 public Response get(@PathParam("uuid") UUID notificationUuid) {
  if (!this.userCheckService.isViewAllowed()) {
   return status(UNAUTHORIZED).build();
  }
  PrnfbNotification notification = this.settingsService.getNotification(notificationUuid);
  NotificationDTO dto = toNotificationDto(notification);
  return ok(dto).build();
 }

}