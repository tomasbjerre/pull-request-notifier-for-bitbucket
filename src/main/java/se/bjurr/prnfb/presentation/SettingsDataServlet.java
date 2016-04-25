package se.bjurr.prnfb.presentation;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.TEXT_PLAIN;
import static javax.ws.rs.core.Response.noContent;
import static javax.ws.rs.core.Response.ok;
import static javax.ws.rs.core.Response.status;
import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;
import static se.bjurr.prnfb.transformer.SettingsTransformer.toDto;
import static se.bjurr.prnfb.transformer.SettingsTransformer.toPrnfbSettingsData;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import se.bjurr.prnfb.presentation.dto.SettingsDataDTO;
import se.bjurr.prnfb.service.SettingsService;
import se.bjurr.prnfb.service.UserCheckService;
import se.bjurr.prnfb.settings.PrnfbSettingsData;

import com.atlassian.annotations.security.XsrfProtectionExcluded;

@Path("/settingsData")
public class SettingsDataServlet {
 private final UserCheckService userCheckService;
 private final SettingsService settingsService;

 public SettingsDataServlet(UserCheckService userCheckService, SettingsService settingsService) {
  this.userCheckService = userCheckService;
  this.settingsService = settingsService;
 }

 @GET
 @Produces(APPLICATION_JSON)
 public Response get() throws Exception {
  if (!userCheckService.isViewAllowed()) {
   return status(UNAUTHORIZED).build();
  }
  PrnfbSettingsData settingsData = settingsService.getPrnfbSettingsData();
  SettingsDataDTO settingsDataDto = toDto(settingsData);
  return ok(settingsDataDto).build();
 }

 @POST
 @XsrfProtectionExcluded
 @Consumes(APPLICATION_JSON)
 @Produces(TEXT_PLAIN)
 public Response post(SettingsDataDTO settingsDataDto) throws Exception {
  if (!userCheckService.isAdminAllowed()) {
   return status(UNAUTHORIZED).build();
  }

  PrnfbSettingsData prnfbSettingsData = toPrnfbSettingsData(settingsDataDto);
  settingsService.setPrnfbSettingsData(prnfbSettingsData);

  return noContent().build();
 }
}