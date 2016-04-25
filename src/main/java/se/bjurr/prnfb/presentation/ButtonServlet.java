package se.bjurr.prnfb.presentation;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.TEXT_PLAIN;
import static javax.ws.rs.core.Response.ok;
import static javax.ws.rs.core.Response.status;
import static javax.ws.rs.core.Response.Status.OK;
import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;
import static se.bjurr.prnfb.transformer.ButtonTransformer.toButtonDtoList;
import static se.bjurr.prnfb.transformer.ButtonTransformer.toPrnfbButton;

import java.util.List;
import java.util.UUID;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import se.bjurr.prnfb.presentation.dto.ButtonDTO;
import se.bjurr.prnfb.service.ButtonsService;
import se.bjurr.prnfb.service.SettingsService;
import se.bjurr.prnfb.service.UserCheckService;
import se.bjurr.prnfb.settings.PrnfbButton;

import com.atlassian.annotations.security.XsrfProtectionExcluded;

@Path("/button")
public class ButtonServlet {
 private final ButtonsService buttonsService;
 private final SettingsService settingsService;
 private final UserCheckService userCheckService;

 public ButtonServlet(ButtonsService buttonsService, SettingsService settingsService, UserCheckService userCheckService) {
  this.buttonsService = buttonsService;
  this.settingsService = settingsService;
  this.userCheckService = userCheckService;
 }

 @GET
 @Produces(APPLICATION_JSON)
 public Response get(@QueryParam("repositoryId") Integer repositoryId, @QueryParam("pullRequestId") Long pullRequestId)
   throws Exception {
  if (!userCheckService.isViewAllowed()) {
   return status(UNAUTHORIZED).build();
  }
  List<PrnfbButton> buttons = buttonsService.getButtons(repositoryId, pullRequestId);
  Iterable<PrnfbButton> allowedButtons = userCheckService.filterAllowed(buttons);
  List<ButtonDTO> dtos = toButtonDtoList(allowedButtons);
  return ok(dtos, APPLICATION_JSON).build();
 }

 @GET
 @Produces(APPLICATION_JSON)
 public Response get() {
  List<PrnfbButton> buttons = settingsService.getButtons();
  Iterable<PrnfbButton> allowedButtons = userCheckService.filterAllowed(buttons);
  List<ButtonDTO> dtos = toButtonDtoList(allowedButtons);
  return ok(dtos, APPLICATION_JSON).build();
 }

 @POST
 @Path("/press")
 @XsrfProtectionExcluded
 @Produces(APPLICATION_JSON)
 public Response press(@QueryParam("repositoryId") Integer repositoryId,
   @QueryParam("pullRequestId") Long pullRequestId, @QueryParam("uuid") final UUID buttionUuid) throws Exception {
  PrnfbButton button = settingsService.getButton(buttionUuid);
  if (!userCheckService.isAllowedUseButton(button)) {
   return status(UNAUTHORIZED).build();
  }
  buttonsService.handlePressed(repositoryId, pullRequestId, buttionUuid);

  return status(OK).build();
 }

 @POST
 @XsrfProtectionExcluded
 @Produces(TEXT_PLAIN)
 public Response create(ButtonDTO buttonDto) throws Exception {
  if (!userCheckService.isAdminAllowed()) {
   return status(UNAUTHORIZED).build();
  }
  PrnfbButton prnfbButton = toPrnfbButton(buttonDto);
  settingsService.addOrUpdateButton(prnfbButton);
  return status(OK).build();
 }

 @PUT
 @XsrfProtectionExcluded
 @Produces(APPLICATION_JSON)
 public Response update(ButtonDTO buttonDto) throws Exception {
  return create(buttonDto);
 }

 @DELETE
 @XsrfProtectionExcluded
 @Produces(APPLICATION_JSON)
 public Response delete(UUID prnfbButton) throws Exception {
  if (!userCheckService.isAdminAllowed()) {
   return status(UNAUTHORIZED).build();
  }
  settingsService.deleteButton(prnfbButton);
  return status(OK).build();
 }

}