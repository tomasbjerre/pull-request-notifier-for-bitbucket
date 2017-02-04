package se.bjurr.prnfb.presentation;

import static com.google.common.base.Strings.isNullOrEmpty;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.ok;
import static javax.ws.rs.core.Response.status;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
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

import com.atlassian.annotations.security.XsrfProtectionExcluded;
import com.google.common.base.Optional;
import com.google.common.collect.Iterables;

import se.bjurr.prnfb.http.NotificationResponse;
import se.bjurr.prnfb.presentation.dto.ButtonDTO;
import se.bjurr.prnfb.presentation.dto.ButtonFormElementDTO;
import se.bjurr.prnfb.presentation.dto.ButtonPressDTO;
import se.bjurr.prnfb.service.ButtonsService;
import se.bjurr.prnfb.service.PrnfbRenderer.ENCODE_FOR;
import se.bjurr.prnfb.service.PrnfbRendererWrapper;
import se.bjurr.prnfb.service.SettingsService;
import se.bjurr.prnfb.service.UserCheckService;
import se.bjurr.prnfb.settings.PrnfbButton;

@Path("/settings/buttons")
public class ButtonServlet {

  private final ButtonsService buttonsService;
  private final SettingsService settingsService;
  private final UserCheckService userCheckService;

  public ButtonServlet(
      ButtonsService buttonsService,
      SettingsService settingsService,
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
    if (!userCheckService.isAdminAllowed(buttonDto)) {
      return status(UNAUTHORIZED) //
          .build();
    }

    PrnfbButton prnfbButton = toPrnfbButton(buttonDto);
    PrnfbButton created = settingsService.addOrUpdateButton(prnfbButton);
    ButtonDTO createdDto = toButtonDto(created);

    return status(OK) //
        .entity(createdDto) //
        .build();
  }

  @DELETE
  @Path("{uuid}")
  @XsrfProtectionExcluded
  @Produces(APPLICATION_JSON)
  public Response delete(@PathParam("uuid") UUID prnfbButtonUuid) {
    PrnfbButton prnfbButton = settingsService.getButton(prnfbButtonUuid);
    if (!userCheckService.isAdminAllowed(prnfbButton)) {
      return status(UNAUTHORIZED) //
          .build();
    }
    settingsService.deleteButton(prnfbButtonUuid);
    return status(OK).build();
  }

  @GET
  @Produces(APPLICATION_JSON)
  public Response get() {
    List<PrnfbButton> buttons = settingsService.getButtons();
    Iterable<PrnfbButton> allowedButtons = userCheckService.filterAdminAllowed(buttons);
    List<ButtonDTO> dtos = toButtonDtoList(allowedButtons);
    Collections.sort(dtos);
    return ok(dtos, APPLICATION_JSON).build();
  }

  @GET
  @Path("/projectKey/{projectKey}")
  @Produces(APPLICATION_JSON)
  public Response get(@PathParam("projectKey") String projectKey) {
    List<PrnfbButton> buttons = settingsService.getButtons(projectKey);
    Iterable<PrnfbButton> allowedButtons = userCheckService.filterAdminAllowed(buttons);
    List<ButtonDTO> dtos = toButtonDtoList(allowedButtons);
    Collections.sort(dtos);
    return ok(dtos, APPLICATION_JSON).build();
  }

  @GET
  @Path("/projectKey/{projectKey}/repositorySlug/{repositorySlug}")
  @Produces(APPLICATION_JSON)
  public Response get(
      @PathParam("projectKey") String projectKey,
      @PathParam("repositorySlug") String repositorySlug) {
    List<PrnfbButton> buttons = settingsService.getButtons(projectKey, repositorySlug);
    Iterable<PrnfbButton> allowedButtons = userCheckService.filterAdminAllowed(buttons);
    List<ButtonDTO> dtos = toButtonDtoList(allowedButtons);
    Collections.sort(dtos);
    return ok(dtos, APPLICATION_JSON).build();
  }

  @GET
  @Path("{uuid}")
  @Produces(APPLICATION_JSON)
  public Response get(@PathParam("uuid") UUID uuid) {
    PrnfbButton button = settingsService.getButton(uuid);
    if (!userCheckService.isAdminAllowed(button)) {
      return status(UNAUTHORIZED).build();
    }
    ButtonDTO dto = toButtonDto(button);
    return ok(dto, APPLICATION_JSON).build();
  }

  @GET
  @Path("/repository/{repositoryId}/pullrequest/{pullRequestId}")
  @Produces(APPLICATION_JSON)
  public Response get(
      @PathParam("repositoryId") Integer repositoryId,
      @PathParam("pullRequestId") Long pullRequestId) {
    List<PrnfbButton> buttons = buttonsService.getButtons(repositoryId, pullRequestId);
    List<ButtonDTO> dtos = toButtonDtoList(buttons);
    Collections.sort(dtos);

    populateButtonFormDtoList(repositoryId, pullRequestId, dtos);

    return ok(dtos, APPLICATION_JSON).build();
  }

  @POST
  @Path("{uuid}/press/repository/{repositoryId}/pullrequest/{pullRequestId}")
  @XsrfProtectionExcluded
  @Produces(APPLICATION_JSON)
  public Response press(
      @Context HttpServletRequest request,
      @PathParam("repositoryId") Integer repositoryId,
      @PathParam("pullRequestId") Long pullRequestId,
      @PathParam("uuid") final UUID buttionUuid) {
    List<PrnfbButton> buttons = buttonsService.getButtons(repositoryId, pullRequestId);
    Optional<PrnfbButton> button =
        Iterables.tryFind(buttons, (b) -> b.getUuid().equals(buttionUuid));
    if (!button.isPresent()) {
      return status(NOT_FOUND).build();
    }
    String formData = request.getParameter("form");
    List<NotificationResponse> results =
        buttonsService.handlePressed(repositoryId, pullRequestId, buttionUuid, formData);

    ButtonPressDTO dto = toTriggerResultDto(button.get(), results);
    return ok(dto, APPLICATION_JSON).build();
  }

  private void populateButtonFormDtoList(
      Integer repositoryId, Long pullRequestId, List<ButtonDTO> dtos) {
    for (ButtonDTO dto : dtos) {
      PrnfbRendererWrapper renderer =
          buttonsService.getRenderer(repositoryId, pullRequestId, dto.getUuid());
      List<ButtonFormElementDTO> buttonFormDtoList = dto.getButtonFormList();
      if (buttonFormDtoList != null) {
        for (ButtonFormElementDTO buttonFormElementDto : buttonFormDtoList) {
          String defaultValue = buttonFormElementDto.getDefaultValue();
          if (!isNullOrEmpty(defaultValue)) {
            String defaultValueRendered = renderer.render(defaultValue, ENCODE_FOR.NONE);
            buttonFormElementDto.setDefaultValue(defaultValueRendered);
          }
        }
        dto.setButtonFormList(buttonFormDtoList);
      }
    }
  }
}
