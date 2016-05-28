package se.bjurr.prnfs;

import static com.atlassian.stash.user.Permission.ADMIN;
import static com.google.common.collect.Iterables.find;
import static com.google.common.collect.Lists.newArrayList;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.ok;
import static javax.ws.rs.core.Response.status;
import static javax.ws.rs.core.Response.Status.OK;
import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;
import static se.bjurr.prnfs.admin.AdminFormValues.BUTTON_VISIBILITY.EVERYONE;
import static se.bjurr.prnfs.admin.AdminFormValues.BUTTON_VISIBILITY.SYSTEM_ADMIN;
import static se.bjurr.prnfs.listener.PrnfsPullRequestAction.BUTTON_TRIGGER;
import static se.bjurr.prnfs.listener.PrnfsRenderer.PrnfsVariable.BUTTON_TRIGGER_TITLE;
import static se.bjurr.prnfs.settings.SettingsStorage.getPrnfsSettings;

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

import se.bjurr.prnfs.admin.AdminFormValues;
import se.bjurr.prnfs.admin.AdminFormValues.BUTTON_VISIBILITY;
import se.bjurr.prnfs.listener.PrnfsPullRequestAction;
import se.bjurr.prnfs.listener.PrnfsPullRequestEventListener;
import se.bjurr.prnfs.listener.PrnfsRenderer;
import se.bjurr.prnfs.listener.PrnfsRenderer.PrnfsVariable;
import se.bjurr.prnfs.settings.PrnfsButton;
import se.bjurr.prnfs.settings.PrnfsNotification;
import se.bjurr.prnfs.settings.PrnfsSettings;

import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.stash.pull.PullRequest;
import com.atlassian.stash.pull.PullRequestService;
import com.atlassian.stash.repository.RepositoryService;
import com.atlassian.stash.server.ApplicationPropertiesService;
import com.atlassian.stash.user.SecurityService;
import com.atlassian.stash.user.StashUser;
import com.atlassian.stash.user.UserService;
import com.atlassian.stash.util.Operation;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.gson.Gson;

@Path("/manual")
public class ManualResource {
 private static final List<AdminFormValues.BUTTON_VISIBILITY> adminOk = newArrayList();
 private static Gson gson = new Gson();
 private static final List<AdminFormValues.BUTTON_VISIBILITY> systemAdminOk = newArrayList();
 static {
  adminOk.add(BUTTON_VISIBILITY.ADMIN);
  adminOk.add(SYSTEM_ADMIN);
  systemAdminOk.add(SYSTEM_ADMIN);
 }

 static boolean allowedUseButton(PrnfsButton candidate, boolean isAdmin, boolean isSystemAdmin) {
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

 private final PluginSettingsFactory pluginSettingsFactory;
 private final PrnfsPullRequestEventListener prnfsPullRequestEventListener;
 private final ApplicationPropertiesService propertiesService;
 private final PullRequestService pullRequestService;
 private final RepositoryService repositoryService;
 private final SecurityService securityService;
 private final UserManager userManager;

 private final UserService userService;

 public ManualResource(UserManager userManager, UserService userService, PluginSettingsFactory pluginSettingsFactory,
   PullRequestService pullRequestService, PrnfsPullRequestEventListener prnfsPullRequestEventListener,
   RepositoryService repositoryService, ApplicationPropertiesService propertiesService, SecurityService securityService) {
  this.userManager = userManager;
  this.userService = userService;
  this.pullRequestService = pullRequestService;
  this.prnfsPullRequestEventListener = prnfsPullRequestEventListener;
  this.securityService = securityService;
  this.pluginSettingsFactory = pluginSettingsFactory;
  this.propertiesService = propertiesService;
  this.repositoryService = repositoryService;
 }

 @GET
 @Produces(APPLICATION_JSON)
 public Response get(@Context HttpServletRequest request, @QueryParam("repositoryId") Integer repositoryId,
   @QueryParam("pullRequestId") Long pullRequestId) throws Exception {
  if (this.userManager.getRemoteUser(request) == null) {
   return status(UNAUTHORIZED).build();
  }
  List<PrnfsButton> buttons = newArrayList();
  final PrnfsSettings settings = getSettings();
  for (PrnfsButton candidate : settings.getButtons()) {
   UserKey userKey = this.userManager.getRemoteUserKey();
   PrnfsPullRequestAction pullRequestAction = PrnfsPullRequestAction.valueOf(BUTTON_TRIGGER);
   final PullRequest pullRequest = this.pullRequestService.getById(repositoryId, pullRequestId);
   Map<PrnfsVariable, Supplier<String>> variables = getVariables(settings, candidate.getFormIdentifier());
   if (allowedUseButton(candidate, this.userManager.isAdmin(userKey), this.userManager.isSystemAdmin(userKey))
     && triggeredByAction(settings, pullRequestAction, pullRequest, variables, request)) {
    buttons.add(candidate);
   }
  }
  return ok(gson.toJson(buttons), APPLICATION_JSON).build();
 }

 @POST
 @Produces(APPLICATION_JSON)
 public Response post(@Context HttpServletRequest request, @QueryParam("repositoryId") Integer repositoryId,
   @QueryParam("pullRequestId") Long pullRequestId, @QueryParam("formIdentifier") final String formIdentifier)
   throws Exception {
  if (this.userManager.getRemoteUser(request) == null) {
   return status(UNAUTHORIZED).build();
  }

  final PrnfsSettings settings = getSettings();
  for (PrnfsNotification prnfsNotification : settings.getNotifications()) {
   PrnfsPullRequestAction pullRequestAction = PrnfsPullRequestAction.valueOf(BUTTON_TRIGGER);
   final PullRequest pullRequest = this.pullRequestService.getById(repositoryId, pullRequestId);
   Map<PrnfsVariable, Supplier<String>> variables = getVariables(settings, formIdentifier);
   PrnfsRenderer renderer = getRenderer(pullRequest, prnfsNotification, pullRequestAction, variables, request);
   if (this.prnfsPullRequestEventListener.notificationTriggeredByAction(prnfsNotification, pullRequestAction, renderer,
     pullRequest)) {
    this.prnfsPullRequestEventListener.notify(prnfsNotification, pullRequestAction, pullRequest, variables, renderer);
   }
  }
  return status(OK).build();
 }

 private PrnfsRenderer getRenderer(final PullRequest pullRequest, PrnfsNotification prnfsNotification,
   PrnfsPullRequestAction pullRequestAction, Map<PrnfsVariable, Supplier<String>> variables, HttpServletRequest request) {
  StashUser stashUser = this.userService.getUserBySlug(this.userManager.getRemoteUser(request).getUsername());
  return new PrnfsRenderer(pullRequest, pullRequestAction, stashUser, this.repositoryService, this.propertiesService,
    prnfsNotification, variables, this.securityService);
 }

 private PrnfsSettings getSettings() throws Exception {
  final PrnfsSettings settings = this.securityService.withPermission(ADMIN, "Getting config").call(
    new Operation<PrnfsSettings, Exception>() {
     @Override
     public PrnfsSettings perform() throws Exception {
      return getPrnfsSettings(ManualResource.this.pluginSettingsFactory.createGlobalSettings());
     }
    });
  return settings;
 }

 private Map<PrnfsVariable, Supplier<String>> getVariables(final PrnfsSettings settings, final String formIdentifier) {
  Map<PrnfsVariable, Supplier<String>> variables = new HashMap<PrnfsRenderer.PrnfsVariable, Supplier<String>>();
  variables.put(BUTTON_TRIGGER_TITLE, new Supplier<String>() {
   @Override
   public String get() {
    return find(settings.getButtons(), new Predicate<PrnfsButton>() {
     @Override
     public boolean apply(PrnfsButton input) {
      return input.getFormIdentifier().equals(formIdentifier);
     }
    }).getTitle();
   }
  });
  return variables;
 }

 private boolean triggeredByAction(PrnfsSettings settings, PrnfsPullRequestAction pullRequestAction,
   PullRequest pullRequest, Map<PrnfsVariable, Supplier<String>> variables, HttpServletRequest request) {
  for (PrnfsNotification prnfsNotification : settings.getNotifications()) {
   PrnfsRenderer renderer = getRenderer(pullRequest, prnfsNotification, pullRequestAction, variables, request);
   if (this.prnfsPullRequestEventListener.notificationTriggeredByAction(prnfsNotification, pullRequestAction, renderer,
     pullRequest)) {
    return TRUE;
   }
  }
  return FALSE;
 }
}