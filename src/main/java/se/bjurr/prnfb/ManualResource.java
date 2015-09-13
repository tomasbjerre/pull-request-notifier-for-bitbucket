package se.bjurr.prnfb;

import static com.atlassian.bitbucket.permission.Permission.ADMIN;
import static com.google.common.collect.Iterables.find;
import static com.google.common.collect.Lists.newArrayList;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.ok;
import static javax.ws.rs.core.Response.status;
import static javax.ws.rs.core.Response.Status.OK;
import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;
import static se.bjurr.prnfb.admin.AdminFormValues.BUTTON_VISIBILITY.EVERYONE;
import static se.bjurr.prnfb.admin.AdminFormValues.BUTTON_VISIBILITY.SYSTEM_ADMIN;
import static se.bjurr.prnfb.listener.PrnfbPullRequestAction.BUTTON_TRIGGER;
import static se.bjurr.prnfb.listener.PrnfbRenderer.PrnfbVariable.BUTTON_TRIGGER_TITLE;
import static se.bjurr.prnfb.settings.SettingsStorage.getPrnfbSettings;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import se.bjurr.prnfb.admin.AdminFormValues;
import se.bjurr.prnfb.admin.AdminFormValues.BUTTON_VISIBILITY;
import se.bjurr.prnfb.listener.PrnfbPullRequestAction;
import se.bjurr.prnfb.listener.PrnfbPullRequestEventListener;
import se.bjurr.prnfb.listener.PrnfbRenderer;
import se.bjurr.prnfb.listener.PrnfbRenderer.PrnfbVariable;
import se.bjurr.prnfb.settings.PrnfbButton;
import se.bjurr.prnfb.settings.PrnfbNotification;
import se.bjurr.prnfb.settings.PrnfbSettings;

import com.atlassian.annotations.security.XsrfProtectionExcluded;
import com.atlassian.bitbucket.pull.PullRequest;
import com.atlassian.bitbucket.pull.PullRequestService;
import com.atlassian.bitbucket.repository.RepositoryService;
import com.atlassian.bitbucket.server.ApplicationPropertiesService;
import com.atlassian.bitbucket.user.ApplicationUser;
import com.atlassian.bitbucket.user.SecurityService;
import com.atlassian.bitbucket.user.UserService;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.user.UserManager;
import com.google.common.base.Supplier;
import com.google.gson.Gson;

@Path("/manual")
public class ManualResource {
 private static Gson gson = new Gson();
 private final UserManager userManager;
 private final UserService userService;
 private final PullRequestService pullRequestService;
 private final PrnfbPullRequestEventListener prnfbPullRequestEventListener;
 private final SecurityService securityService;
 private final PluginSettingsFactory pluginSettingsFactory;
 private final ApplicationPropertiesService propertiesService;
 private final RepositoryService repositoryService;
 private static final List<AdminFormValues.BUTTON_VISIBILITY> adminOk = newArrayList();
 private static final List<AdminFormValues.BUTTON_VISIBILITY> systemAdminOk = newArrayList();
 static {
  adminOk.add(BUTTON_VISIBILITY.ADMIN);
  adminOk.add(SYSTEM_ADMIN);
  systemAdminOk.add(SYSTEM_ADMIN);
 }

 public ManualResource(UserManager userManager, UserService userService, PluginSettingsFactory pluginSettingsFactory,
   PullRequestService pullRequestService, PrnfbPullRequestEventListener prnfbPullRequestEventListener,
   RepositoryService repositoryService, ApplicationPropertiesService propertiesService, SecurityService securityService) {
  this.userManager = userManager;
  this.userService = userService;
  this.pullRequestService = pullRequestService;
  this.prnfbPullRequestEventListener = prnfbPullRequestEventListener;
  this.securityService = securityService;
  this.pluginSettingsFactory = pluginSettingsFactory;
  this.propertiesService = propertiesService;
  this.repositoryService = repositoryService;
 }

 @GET
 @Produces(APPLICATION_JSON)
 public Response get(@Context HttpServletRequest request, @QueryParam("repositoryId") Integer repositoryId,
   @QueryParam("pullRequestId") Long pullRequestId) throws Exception {
  if (userManager.getRemoteUser(request) == null) {
   return status(UNAUTHORIZED).build();
  }
  List<PrnfbButton> buttons = newArrayList();
  final PrnfbSettings settings = getSettings();
  for (PrnfbButton candidate : settings.getButtons()) {
   UserKey userKey = userManager.getRemoteUserKey();
   PrnfbPullRequestAction pullRequestAction = PrnfbPullRequestAction.valueOf(BUTTON_TRIGGER);
   final PullRequest pullRequest = pullRequestService.getById(repositoryId, pullRequestId);
   Map<PrnfbVariable, Supplier<String>> variables = getVariables(settings, candidate.getFormIdentifier());
   if (allowedUseButton(candidate, userManager.isAdmin(userKey), userManager.isSystemAdmin(userKey))
     && triggeredByAction(settings, pullRequestAction, pullRequest, variables, request)) {
    buttons.add(candidate);
   }
  }
  return ok(gson.toJson(buttons), APPLICATION_JSON).build();
 }

 private boolean triggeredByAction(PrnfbSettings settings, PrnfbPullRequestAction pullRequestAction,
   PullRequest pullRequest, Map<PrnfbVariable, Supplier<String>> variables, HttpServletRequest request) {
  for (PrnfbNotification prnfbNotification : settings.getNotifications()) {
   PrnfbRenderer renderer = getRenderer(pullRequest, prnfbNotification, pullRequestAction, variables, request);
   if (prnfbPullRequestEventListener.notificationTriggeredByAction(prnfbNotification, pullRequestAction, renderer,
     pullRequest)) {
    return TRUE;
   }
  }
  return FALSE;
 }

 @POST
 @XsrfProtectionExcluded
 @Produces(APPLICATION_JSON)
 public Response post(@Context HttpServletRequest request, @QueryParam("repositoryId") Integer repositoryId,
   @QueryParam("pullRequestId") Long pullRequestId, @QueryParam("formIdentifier") final String formIdentifier)
   throws Exception {
  if (userManager.getRemoteUser(request) == null) {
   return status(UNAUTHORIZED).build();
  }

  final PrnfbSettings settings = getSettings();
  for (PrnfbNotification prnfbNotification : settings.getNotifications()) {
   PrnfbPullRequestAction pullRequestAction = PrnfbPullRequestAction.valueOf(BUTTON_TRIGGER);
   final PullRequest pullRequest = pullRequestService.getById(repositoryId, pullRequestId);
   Map<PrnfbVariable, Supplier<String>> variables = getVariables(settings, formIdentifier);
   PrnfbRenderer renderer = getRenderer(pullRequest, prnfbNotification, pullRequestAction, variables, request);
   if (prnfbPullRequestEventListener.notificationTriggeredByAction(prnfbNotification, pullRequestAction, renderer,
     pullRequest)) {
    prnfbPullRequestEventListener.notify(prnfbNotification, pullRequestAction, pullRequest, variables, renderer);
   }
  }
  return status(OK).build();
 }

 private Map<PrnfbVariable, Supplier<String>> getVariables(final PrnfbSettings settings, final String formIdentifier) {
  Map<PrnfbVariable, Supplier<String>> variables = new HashMap<PrnfbRenderer.PrnfbVariable, Supplier<String>>();
  variables.put(BUTTON_TRIGGER_TITLE,
    () -> find(settings.getButtons(), input -> input.getFormIdentifier().equals(formIdentifier)).getTitle());
  return variables;
 }

 private PrnfbRenderer getRenderer(final PullRequest pullRequest, PrnfbNotification prnfbNotification,
   PrnfbPullRequestAction pullRequestAction, Map<PrnfbVariable, Supplier<String>> variables, HttpServletRequest request) {
  ApplicationUser bitbucketUser = userService.getUserBySlug(userManager.getRemoteUser(request).getUsername());
  return new PrnfbRenderer(pullRequest, pullRequestAction, bitbucketUser, repositoryService, propertiesService,
    prnfbNotification, variables);
 }

 static boolean allowedUseButton(PrnfbButton candidate, boolean isAdmin, boolean isSystemAdmin) {
  if (candidate.getVisibility().equals(EVERYONE)) {
   return TRUE;
  }
  if (isSystemAdmin && systemAdminOk.contains(candidate.getVisibility())) {
   return TRUE;
  } else if (isAdmin && adminOk.contains(candidate.getVisibility())) {
   return TRUE;
  } else if (candidate.getVisibility().equals(EVERYONE)) {
   return TRUE;
  }
  return FALSE;
 }

 private PrnfbSettings getSettings() throws Exception {
  final PrnfbSettings settings = securityService.withPermission(ADMIN, "Getting config").call(
    () -> getPrnfbSettings(pluginSettingsFactory.createGlobalSettings()));
  return settings;
 }
}