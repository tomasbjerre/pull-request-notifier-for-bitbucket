package se.bjurr.prnfb.presentation;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.ok;
import static javax.ws.rs.core.Response.status;
import static javax.ws.rs.core.Response.Status.OK;
import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;
import static se.bjurr.prnfb.transformer.ButtonTransformer.toButtonDto;
import static se.bjurr.prnfb.transformer.ButtonTransformer.toButtonDtoList;
import static se.bjurr.prnfb.transformer.ButtonTransformer.toPrnfbButton;
import static se.bjurr.prnfb.transformer.ButtonTransformer.toTriggerResultDto;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import se.bjurr.prnfb.http.NotificationResponse;
import se.bjurr.prnfb.presentation.dto.ButtonDTO;
import se.bjurr.prnfb.presentation.dto.ButtonPressDTO;
import se.bjurr.prnfb.service.ButtonsService;
import se.bjurr.prnfb.service.SettingsService;
import se.bjurr.prnfb.service.UserCheckService;
import se.bjurr.prnfb.settings.PrnfbButton;

import com.atlassian.annotations.security.XsrfProtectionExcluded;
import com.google.gson.Gson;

@Path("/settings/buttons")
public class ButtonServlet {
 private final ButtonsService buttonsService;
 private final SettingsService settingsService;
 private final UserCheckService userCheckService;
 private static final Gson gson = new Gson();
 
 public ButtonServlet(ButtonsService buttonsService, SettingsService settingsService,
   UserCheckService userCheckService) {
  this.buttonsService = buttonsService;
  this.settingsService = settingsService;
  this.userCheckService = userCheckService;
 }

 @POST
 @XsrfProtectionExcluded
 @Consumes(APPLICATION_JSON)
 @Produces(APPLICATION_JSON)
 public Response create(ButtonDTO buttonDto) {
  if (!this.userCheckService.isAdminAllowed(//
    buttonDto.getProjectKey().orNull()//
    , buttonDto.getRepositorySlug().orNull())) {
   return status(UNAUTHORIZED)//
     .build();
  }
  
  if (buttonDto.getButtonForm() != null && !buttonDto.getButtonForm().isEmpty()) {
   try {
     gson.fromJson(buttonDto.getButtonForm(), Object.class);
   } catch(com.google.gson.JsonSyntaxException ex) { 
     throw new Error("The form specification for the button must be a valid JSON string");
   }
  }
  
  PrnfbButton prnfbButton = toPrnfbButton(buttonDto);
  PrnfbButton created = this.settingsService.addOrUpdateButton(prnfbButton);
  ButtonDTO createdDto = toButtonDto(created);

  return status(OK)//
    .entity(createdDto)//
    .build();
 }

 @DELETE
 @Path("{uuid}")
 @XsrfProtectionExcluded
 @Produces(APPLICATION_JSON)
 public Response delete(@PathParam("uuid") UUID prnfbButtonUuid) {
  PrnfbButton prnfbButton = this.settingsService.getButton(prnfbButtonUuid);
  if (!this.userCheckService.isAdminAllowed(//
    prnfbButton.getProjectKey().orNull()//
    , prnfbButton.getRepositorySlug().orNull())) {
   return status(UNAUTHORIZED)//
     .build();
  }
  this.settingsService.deleteButton(prnfbButtonUuid);
  return status(OK).build();
 }

 @GET
 @Produces(APPLICATION_JSON)
 public Response get() {
  List<PrnfbButton> buttons = this.settingsService.getButtons();
  Iterable<PrnfbButton> allowedButtons = this.userCheckService.filterAllowed(buttons);
  List<ButtonDTO> dtos = toButtonDtoList(allowedButtons);
  Collections.sort(dtos);
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
  Collections.sort(dtos);
  
  for(ButtonDTO dto : dtos) {
   if (dto.getButtonForm() != null) {
    dto.setButtonForm(this.buttonsService.getRenderedButtonFormData(repositoryId, pullRequestId, dto.getUuid(), dto.getButtonForm()));
   }
  }
  
  return ok(dtos, APPLICATION_JSON).build();
 }

 @GET
 @Path("/projectKey/{projectKey}")
 @Produces(APPLICATION_JSON)
 public Response get(@PathParam("projectKey") String projectKey) {
  if (!this.userCheckService.isViewAllowed()) {
   return status(UNAUTHORIZED).build();
  }
  List<PrnfbButton> buttons = this.settingsService.getButtons(projectKey);
  Iterable<PrnfbButton> allowedButtons = this.userCheckService.filterAllowed(buttons);
  List<ButtonDTO> dtos = toButtonDtoList(allowedButtons);
  Collections.sort(dtos);
  return ok(dtos, APPLICATION_JSON).build();
 }

 @GET
 @Path("/projectKey/{projectKey}/repositorySlug/{repositorySlug}")
 @Produces(APPLICATION_JSON)
 public Response get(@PathParam("projectKey") String projectKey, @PathParam("repositorySlug") String repositorySlug) {
  if (!this.userCheckService.isViewAllowed()) {
   return status(UNAUTHORIZED).build();
  }
  List<PrnfbButton> buttons = this.settingsService.getButtons(projectKey, repositorySlug);
  Iterable<PrnfbButton> allowedButtons = this.userCheckService.filterAllowed(buttons);
  List<ButtonDTO> dtos = toButtonDtoList(allowedButtons);
  Collections.sort(dtos);
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
 @Path("{uuid}/press/repository/{repositoryId}/pullrequest/{pullRequestId}")
 @XsrfProtectionExcluded
 @Produces(APPLICATION_JSON)
 public Response press(@Context HttpServletRequest request, @PathParam("repositoryId") Integer repositoryId, @PathParam("pullRequestId") Long pullRequestId,
   @PathParam("uuid") final UUID buttionUuid) {
  String formData = request.getParameter("form");
  PrnfbButton button = this.settingsService.getButton(buttionUuid);
  if (!this.userCheckService.isAllowedUseButton(button)) {
   return status(UNAUTHORIZED).build();
  }
  List<NotificationResponse> results = this.buttonsService.handlePressed(repositoryId, pullRequestId, buttionUuid, formData);

  ButtonPressDTO dto = toTriggerResultDto(button, results);
  return ok(dto, APPLICATION_JSON).build();
 }

}
