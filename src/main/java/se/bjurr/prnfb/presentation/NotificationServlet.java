package se.bjurr.prnfb.presentation;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.TEXT_PLAIN;
import static javax.ws.rs.core.Response.ok;
import static javax.ws.rs.core.Response.status;
import static javax.ws.rs.core.Response.Status.OK;
import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;
import static se.bjurr.prnfb.transformer.NotificationTransformer.toNotificationDtoList;
import static se.bjurr.prnfb.transformer.NotificationTransformer.toPrnfbNotification;

import java.util.List;
import java.util.UUID;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import se.bjurr.prnfb.presentation.dto.NotificationDTO;
import se.bjurr.prnfb.service.SettingsService;
import se.bjurr.prnfb.service.UserCheckService;
import se.bjurr.prnfb.settings.PrnfbNotification;

import com.atlassian.annotations.security.XsrfProtectionExcluded;

@Path("/notification")
public class NotificationServlet {
 private final SettingsService settingsService;
 private final UserCheckService userCheckService;

 public NotificationServlet(SettingsService settingsService, UserCheckService userCheckService) {
  this.settingsService = settingsService;
  this.userCheckService = userCheckService;
 }

 @GET
 @Produces(APPLICATION_JSON)
 public Response get() {
  if (!userCheckService.isViewAllowed()) {
   return status(UNAUTHORIZED).build();
  }
  List<PrnfbNotification> notifications = settingsService.getNotifications();
  List<NotificationDTO> dtos = toNotificationDtoList(notifications);
  return ok(dtos).build();
 }

 @POST
 @XsrfProtectionExcluded
 @Produces(TEXT_PLAIN)
 public Response create(NotificationDTO notificationDto) throws Exception {
  if (!userCheckService.isAdminAllowed()) {
   return status(UNAUTHORIZED).build();
  }
  PrnfbNotification prnfbNotification = toPrnfbNotification(notificationDto);
  settingsService.addOrUpdateNotification(prnfbNotification);
  return status(OK).build();
 }

 @PUT
 @XsrfProtectionExcluded
 @Produces(APPLICATION_JSON)
 public Response update(NotificationDTO notificationDTO) throws Exception {
  return create(notificationDTO);
 }

 @DELETE
 @XsrfProtectionExcluded
 @Produces(APPLICATION_JSON)
 public Response delete(UUID notification) throws Exception {
  if (!userCheckService.isAdminAllowed()) {
   return status(UNAUTHORIZED).build();
  }
  settingsService.deleteNotification(notification);
  return status(OK).build();
 }

}