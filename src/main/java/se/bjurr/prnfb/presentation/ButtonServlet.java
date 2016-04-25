package se.bjurr.prnfb.presentation;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.TEXT_PLAIN;
import static javax.ws.rs.core.Response.ok;
import static javax.ws.rs.core.Response.status;
import static javax.ws.rs.core.Response.Status.OK;
import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;
import static se.bjurr.prnfb.transformer.ButtonTransformer.toButtonDto;
import static se.bjurr.prnfb.transformer.ButtonTransformer.toButtonDtoList;
import static se.bjurr.prnfb.transformer.ButtonTransformer.toPrnfbButton;

import java.util.List;
import java.util.UUID;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import se.bjurr.prnfb.presentation.dto.ButtonDTO;
import se.bjurr.prnfb.service.ButtonsService;
import se.bjurr.prnfb.service.SettingsService;
import se.bjurr.prnfb.service.UserCheckService;
import se.bjurr.prnfb.settings.PrnfbButton;

import com.atlassian.annotations.security.XsrfProtectionExcluded;

@Path("/settings/buttons")
public class ButtonServlet {
 private final ButtonsService buttonsService;
 private final SettingsService settingsService;
 private final UserCheckService userCheckService;

 public ButtonServlet(ButtonsService buttonsService, SettingsService settingsService, UserCheckService userCheckService) {
  this.buttonsService = buttonsService;
  this.settingsService = settingsService;
  this.userCheckService = userCheckService;
 }

 @POST
 @XsrfProtectionExcluded
 @Produces(TEXT_PLAIN)
 public Response create(ButtonDTO buttonDto) {
  if (!this.userCheckService.isAdminAllowed()) {
   return status(UNAUTHORIZED).build();
  }
  PrnfbButton prnfbButton = toPrnfbButton(buttonDto);
  this.settingsService.addOrUpdateButton(prnfbButton);
  return status(OK).build();
 }

 @DELETE
 @Path("{uuid}")
 @XsrfProtectionExcluded
 @Produces(APPLICATION_JSON)
 public Response delete(UUID prnfbButton) {
  if (!this.userCheckService.isAdminAllowed()) {
   return status(UNAUTHORIZED).build();
  }
  this.settingsService.deleteButton(prnfbButton);
  return status(OK).build();
 }

 @GET
 @Produces(APPLICATION_JSON)
 public Response get() {
  List<PrnfbButton> buttons = this.settingsService.getButtons();
  Iterable<PrnfbButton> allowedButtons = this.userCheckService.filterAllowed(buttons);
  List<ButtonDTO> dtos = toButtonDtoList(allowedButtons);
  return ok(dtos, APPLICATION_JSON).build();
 }

 @GET
 @Path("/repository/{repositoryId}/pullrequest/{pullRequestId}")
 @Produces(APPLICATION_JSON)
 public Response get(@PathParam("repositoryId") Integer repositoryId, @PathParam("pullRequestId") Long pullRequestId) {
  if (!this.userCheckService.isViewAllowed()) {
   return status(UNAUTHORIZED).build();
  }
  List<PrnfbButton> buttons = this.buttonsService.getButtons(repositoryId, pullRequestId);
  Iterable<PrnfbButton> allowedButtons = this.userCheckService.filterAllowed(buttons);
  List<ButtonDTO> dtos = toButtonDtoList(allowedButtons);
  return ok(dtos, APPLICATION_JSON).build();
 }

 @GET
 @Path("{uuid}")
 @Produces(APPLICATION_JSON)
 public Response get(@PathParam("uuid") UUID uuid) {
  PrnfbButton button = this.settingsService.getButton(uuid);
  if (!this.userCheckService.isAllowedUseButton(button)) {
   return status(UNAUTHORIZED).build();
  }
  ButtonDTO dto = toButtonDto(button);
  return ok(dto, APPLICATION_JSON).build();
 }

 @POST
 @Path("/press")
 @XsrfProtectionExcluded
 @Produces(APPLICATION_JSON)
 public Response press(@QueryParam("repositoryId") Integer repositoryId,
   @QueryParam("pullRequestId") Long pullRequestId, @QueryParam("uuid") final UUID buttionUuid) {
  PrnfbButton button = this.settingsService.getButton(buttionUuid);
  if (!this.userCheckService.isAllowedUseButton(button)) {
   return status(UNAUTHORIZED).build();
  }
  this.buttonsService.handlePressed(repositoryId, pullRequestId, buttionUuid);

  return status(OK).build();
 }

}