package se.bjurr.prnfs.admin;

import static com.atlassian.stash.user.Permission.ADMIN;
import static java.util.logging.Level.SEVERE;
import static java.util.logging.Logger.getLogger;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.noContent;
import static javax.ws.rs.core.Response.ok;
import static javax.ws.rs.core.Response.status;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;
import static se.bjurr.prnfs.settings.PrnfsNotification.isOfType;
import static se.bjurr.prnfs.settings.SettingsStorage.checkFieldsRecognized;
import static se.bjurr.prnfs.settings.SettingsStorage.deleteSettings;
import static se.bjurr.prnfs.settings.SettingsStorage.getPrnfsButton;
import static se.bjurr.prnfs.settings.SettingsStorage.getPrnfsNotification;
import static se.bjurr.prnfs.settings.SettingsStorage.getPrnfsSettings;
import static se.bjurr.prnfs.settings.SettingsStorage.getSettingsAsFormValues;
import static se.bjurr.prnfs.settings.SettingsStorage.injectFormIdentifierIfNotSet;
import static se.bjurr.prnfs.settings.SettingsStorage.storeSettings;

import java.util.logging.Logger;

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

import se.bjurr.prnfs.settings.PrnfsSettings;
import se.bjurr.prnfs.settings.ValidationException;

import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.transaction.TransactionCallback;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.user.UserProfile;
import com.atlassian.stash.user.SecurityService;
import com.atlassian.stash.util.Operation;

@Path("/")
public class ConfigResource {
 private static final Logger logger = getLogger(ConfigResource.class.getName());
 private final PluginSettingsFactory pluginSettingsFactory;
 private final TransactionTemplate transactionTemplate;
 private final UserManager userManager;
 private final SecurityService securityService;

 public ConfigResource(UserManager userManager, PluginSettingsFactory pluginSettingsFactory,
   TransactionTemplate transactionTemplate, SecurityService securityService) {
  this.userManager = userManager;
  this.pluginSettingsFactory = pluginSettingsFactory;
  this.transactionTemplate = transactionTemplate;
  this.securityService = securityService;
 }

 @DELETE
 @Path("{id}")
 public Response delete(@PathParam("id") final String id, @Context HttpServletRequest request) throws Exception {
  if (!isAdminAllowed(userManager, request, securityService, pluginSettingsFactory)) {
   return status(UNAUTHORIZED).build();
  }

  transactionTemplate.execute(new TransactionCallback<Object>() {
   @Override
   public Object doInTransaction() {
    deleteSettings(pluginSettingsFactory.createGlobalSettings(), id);
    return null;
   }
  });
  return noContent().build();
 }

 /**
  * Get list of all notifications.
  */
 @GET
 @Produces(APPLICATION_JSON)
 public Response get(@Context HttpServletRequest request) throws Exception {
  if (!isAdminAllowed(userManager, request, securityService, pluginSettingsFactory)) {
   return status(UNAUTHORIZED).build();
  }

  return ok(transactionTemplate.execute(new TransactionCallback<Object>() {
   @Override
   public Object doInTransaction() {
    return getSettingsAsFormValues(pluginSettingsFactory.createGlobalSettings());
   }
  })).build();
 }

 public PluginSettingsFactory getPluginSettingsFactory() {
  return pluginSettingsFactory;
 }

 public TransactionTemplate getTransactionTemplate() {
  return transactionTemplate;
 }

 public UserManager getUserManager() {
  return userManager;
 }

 static boolean isAdminAllowed(UserManager userManager, HttpServletRequest request, SecurityService securityService,
   final PluginSettingsFactory pluginSettingsFactory) throws Exception {
  final UserProfile user = userManager.getRemoteUser(request);
  if (user == null) {
   return false;
  }
  PrnfsSettings settings = securityService.withPermission(ADMIN, "Getting config").call(
    new Operation<PrnfsSettings, Exception>() {
     @Override
     public PrnfsSettings perform() throws Exception {
      return getPrnfsSettings(pluginSettingsFactory.createGlobalSettings());
     }
    });
  return userManager.isSystemAdmin(user.getUserKey()) //
    || settings.isUsersAllowed() //
    || settings.isAdminsAllowed() && userManager.isAdmin(user.getUserKey());
 }

 /**
  * Store a single notification setting.
  */
 @POST
 @Consumes(APPLICATION_JSON)
 @Produces(APPLICATION_JSON)
 public Response post(final AdminFormValues config, @Context HttpServletRequest request) throws Exception {
  if (!isAdminAllowed(userManager, request, securityService, pluginSettingsFactory)) {
   return status(UNAUTHORIZED).build();
  }

  /**
   * Validate
   */
  try {
   injectFormIdentifierIfNotSet(config);
   checkFieldsRecognized(config);
   if (isOfType(config, AdminFormValues.FORM_TYPE.TRIGGER_CONFIG_FORM)) {
    // Assuming TRIGGER_CONFIG_FORM here if field not available, to be backwards
    // compatible
    getPrnfsNotification(config);
   } else if (isOfType(config, AdminFormValues.FORM_TYPE.BUTTON_CONFIG_FORM)) {
    getPrnfsButton(config);
   }
  } catch (final ValidationException e) {
   return status(BAD_REQUEST).entity(new AdminFormError(e.getField(), e.getError())).build();
  }

  transactionTemplate.execute(new TransactionCallback<Object>() {
   @Override
   public Object doInTransaction() {
    try {
     storeSettings(pluginSettingsFactory.createGlobalSettings(), config);
    } catch (final ValidationException e) {
     logger.log(SEVERE, "", e);
    }
    return null;
   }
  });
  return noContent().build();
 }
}