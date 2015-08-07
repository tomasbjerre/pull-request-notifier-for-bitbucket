package se.bjurr.prnfs;

import static com.atlassian.stash.user.Permission.ADMIN;
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
import javax.ws.rs.PathParam;
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
import com.atlassian.stash.event.pull.PullRequestEvent;
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
import com.google.common.collect.Iterables;
import com.google.gson.Gson;

@Path("/manual")
public class ManualResource {
 private static Gson gson = new Gson();
 private final UserManager userManager;
 private final UserService userService;
 private final PullRequestService pullRequestService;
 private final PrnfsPullRequestEventListener prnfsPullRequestEventListener;
 private final RepositoryService repositoryService;
 private final SecurityService securityService;
 private final PluginSettingsFactory pluginSettingsFactory;
 private final ApplicationPropertiesService propertiesService;
 private static final List<AdminFormValues.BUTTON_VISIBILITY> adminOk = newArrayList();
 private static final List<AdminFormValues.BUTTON_VISIBILITY> systemAdminOk = newArrayList();
 static {
  adminOk.add(BUTTON_VISIBILITY.ADMIN);
  adminOk.add(SYSTEM_ADMIN);
  systemAdminOk.add(SYSTEM_ADMIN);
 }

 public ManualResource(UserManager userManager, UserService userService, PluginSettingsFactory pluginSettingsFactory,
   PullRequestService pullRequestService, PrnfsPullRequestEventListener prnfsPullRequestEventListener,
   RepositoryService repositoryService, ApplicationPropertiesService propertiesService, SecurityService securityService) {
  this.userManager = userManager;
  this.userService = userService;
  this.pullRequestService = pullRequestService;
  this.prnfsPullRequestEventListener = prnfsPullRequestEventListener;
  this.repositoryService = repositoryService;
  this.securityService = securityService;
  this.pluginSettingsFactory = pluginSettingsFactory;
  this.propertiesService = propertiesService;
 }

 @GET
 @Produces(APPLICATION_JSON)
 public Response get(@Context HttpServletRequest request, @PathParam("repositoryId") Integer repositoryId,
   @PathParam("pullRequestId") Long pullRequestId) throws Exception {
  if (userManager.getRemoteUser(request) == null) {
   return status(UNAUTHORIZED).build();
  }
  List<PrnfsButton> buttons = newArrayList();
  for (PrnfsButton candidate : getSettings().getButtons()) {
   UserKey userKey = userManager.getRemoteUserKey();
   if (canUseButton(candidate, userManager.isAdmin(userKey), userManager.isSystemAdmin(userKey))) {
    buttons.add(candidate);
   }
  }
  return ok(gson.toJson(buttons), APPLICATION_JSON).build();
 }

 static boolean canUseButton(PrnfsButton candidate, boolean isAdmin, boolean isSystemAdmin) {
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

 @POST
 @Produces(APPLICATION_JSON)
 public Response post(@Context HttpServletRequest request, @QueryParam("repositoryId") Integer repositoryId,
   @QueryParam("pullRequestId") Long pullRequestId, @QueryParam("formIdentifier") final String formIdentifier)
   throws Exception {
  if (userManager.getRemoteUser(request) == null) {
   return status(UNAUTHORIZED).build();
  }

  final PullRequest pullRequest = pullRequestService.getById(repositoryId, pullRequestId);
  PullRequestEvent pullRequestEvent = null;
  final PrnfsSettings settings = getSettings();
  for (PrnfsNotification prnfsNotification : settings.getNotifications()) {
   PrnfsPullRequestAction pullRequestAction = PrnfsPullRequestAction.valueOf(BUTTON_TRIGGER);
   if (!prnfsNotification.getTriggers().contains(pullRequestAction)) {
    continue;
   }
   StashUser stashUser = userService.getUserBySlug(userManager.getRemoteUser(request).getUsername());
   Map<PrnfsVariable, Supplier<String>> variables = new HashMap<PrnfsRenderer.PrnfsVariable, Supplier<String>>();
   variables.put(BUTTON_TRIGGER_TITLE, new Supplier<String>() {
    @Override
    public String get() {
     return Iterables.find(settings.getButtons(), new Predicate<PrnfsButton>() {
      @Override
      public boolean apply(PrnfsButton input) {
       return input.getFormIdentifier().equals(formIdentifier);
      }
     }).getTitle();
    }
   });
   PrnfsRenderer renderer = new PrnfsRenderer(pullRequest, pullRequestAction, stashUser, repositoryService,
     propertiesService, prnfsNotification, pullRequestEvent, variables);
   prnfsPullRequestEventListener.notify(prnfsNotification, renderer, pullRequest);
  }
  return status(OK).build();
 }

 private PrnfsSettings getSettings() throws Exception {
  final PrnfsSettings settings = securityService.withPermission(ADMIN, "Getting config").call(
    new Operation<PrnfsSettings, Exception>() {
     @Override
     public PrnfsSettings perform() throws Exception {
      return getPrnfsSettings(pluginSettingsFactory.createGlobalSettings());
     }
    });
  return settings;
 }
}