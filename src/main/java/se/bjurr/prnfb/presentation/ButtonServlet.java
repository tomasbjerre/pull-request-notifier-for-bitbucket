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
import se.bjurr.prnfb.settings.USER_LEVEL;

import com.atlassian.annotations.security.XsrfProtectionExcluded;
import com.google.common.base.Optional;
import com.google.common.collect.Iterables;

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
    final USER_LEVEL adminRestriction =
        settingsService.getPrnfbSettingsData().getAdminRestriction();
    if (!userCheckService.isAdminAllowed(buttonDto, adminRestriction)) {
      return status(UNAUTHORIZED) //
          .build();
    }

    final PrnfbButton prnfbButton = toPrnfbButton(buttonDto);
    final PrnfbButton created = settingsService.addOrUpdateButton(prnfbButton);
    final ButtonDTO createdDto = toButtonDto(created);

    return status(OK) //
        .entity(createdDto) //
        .build();
  }

  @DELETE
  @Path("{uuid}")
  @XsrfProtectionExcluded
  @Produces(APPLICATION_JSON)
  public Response delete(@PathParam("uuid") UUID prnfbButtonUuid) {
    final PrnfbButton prnfbButton = settingsService.getButton(prnfbButtonUuid);
    final USER_LEVEL adminRestriction =
        settingsService.getPrnfbSettingsData().getAdminRestriction();
    if (!userCheckService.isAdminAllowed(prnfbButton, adminRestriction)) {
      return status(UNAUTHORIZED) //
          .build();
    }
    settingsService.deleteButton(prnfbButtonUuid);
    return status(OK).build();
  }

  @GET
  @Produces(APPLICATION_JSON)
  public Response get() {
    final List<PrnfbButton> buttons = settingsService.getButtons();
    final Iterable<PrnfbButton> allowedButtons = userCheckService.filterAdminAllowed(buttons);
    final List<ButtonDTO> dtos = toButtonDtoList(allowedButtons);
    Collections.sort(dtos);
    return ok(dtos, APPLICATION_JSON).build();
  }

  @GET
  @Path("/projectKey/{projectKey}")
  @Produces(APPLICATION_JSON)
  public Response get(@PathParam("projectKey") String projectKey) {
    final List<PrnfbButton> buttons = settingsService.getButtons(projectKey);
    final Iterable<PrnfbButton> allowedButtons = userCheckService.filterAdminAllowed(buttons);
    final List<ButtonDTO> dtos = toButtonDtoList(allowedButtons);
    Collections.sort(dtos);
    return ok(dtos, APPLICATION_JSON).build();
  }

  @GET
  @Path("/projectKey/{projectKey}/repositorySlug/{repositorySlug}")
  @Produces(APPLICATION_JSON)
  public Response get(
      @PathParam("projectKey") String projectKey,
      @PathParam("repositorySlug") String repositorySlug) {
    final List<PrnfbButton> buttons = settingsService.getButtons(projectKey, repositorySlug);
    final Iterable<PrnfbButton> allowedButtons = userCheckService.filterAdminAllowed(buttons);
    final List<ButtonDTO> dtos = toButtonDtoList(allowedButtons);
    Collections.sort(dtos);
    return ok(dtos, APPLICATION_JSON).build();
  }

  @GET
  @Path("{uuid}")
  @Produces(APPLICATION_JSON)
  public Response get(@PathParam("uuid") UUID uuid) {
    final PrnfbButton button = settingsService.getButton(uuid);
    final USER_LEVEL adminRestriction =
        settingsService.getPrnfbSettingsData().getAdminRestriction();
    if (!userCheckService.isAdminAllowed(button, adminRestriction)) {
      return status(UNAUTHORIZED).build();
    }
    final ButtonDTO dto = toButtonDto(button);
    return ok(dto, APPLICATION_JSON).build();
  }

  @GET
  @Path("/repository/{repositoryId}/pullrequest/{pullRequestId}")
  @Produces(APPLICATION_JSON)
  public Response get(
      @PathParam("repositoryId") Integer repositoryId,
      @PathParam("pullRequestId") Long pullRequestId) {
    final List<PrnfbButton> buttons = buttonsService.getButtons(repositoryId, pullRequestId);
    final List<ButtonDTO> dtos = toButtonDtoList(buttons);
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
    final List<PrnfbButton> buttons = buttonsService.getButtons(repositoryId, pullRequestId);
    final Optional<PrnfbButton> button =
        Iterables.tryFind(buttons, (b) -> b.getUuid().equals(buttionUuid));
    if (!button.isPresent()) {
      return status(NOT_FOUND).build();
    }
    final String formData = request.getParameter("form");
    final List<NotificationResponse> results =
        buttonsService.handlePressed(repositoryId, pullRequestId, buttionUuid, formData);

    final ButtonPressDTO dto = toTriggerResultDto(button.get(), results);
    return ok(dto, APPLICATION_JSON).build();
  }

  private void populateButtonFormDtoList(
      Integer repositoryId, Long pullRequestId, List<ButtonDTO> dtos) {
    for (final ButtonDTO dto : dtos) {
      final PrnfbRendererWrapper renderer =
          buttonsService.getRenderer(repositoryId, pullRequestId, dto.getUuid());
      final List<ButtonFormElementDTO> buttonFormDtoList = dto.getButtonFormList();
      if (buttonFormDtoList != null) {
        for (final ButtonFormElementDTO buttonFormElementDto : buttonFormDtoList) {
          final String defaultValue = buttonFormElementDto.getDefaultValue();
          if (!isNullOrEmpty(defaultValue)) {
            final String defaultValueRendered = renderer.render(defaultValue, ENCODE_FOR.NONE);
            buttonFormElementDto.setDefaultValue(defaultValueRendered);
          }
        }
        dto.setButtonFormList(buttonFormDtoList);
      }
    }
  }
}
