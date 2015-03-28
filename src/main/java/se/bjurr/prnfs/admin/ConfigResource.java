package se.bjurr.prnfs.admin;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.noContent;
import static javax.ws.rs.core.Response.ok;
import static javax.ws.rs.core.Response.status;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;
import static se.bjurr.prnfs.settings.SettingsStorage.deleteSettings;
import static se.bjurr.prnfs.settings.SettingsStorage.getPrnfsNotification;
import static se.bjurr.prnfs.settings.SettingsStorage.getSettingsAsFormValues;
import static se.bjurr.prnfs.settings.SettingsStorage.storeSettings;

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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.bjurr.prnfs.settings.ValidationException;

import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.transaction.TransactionCallback;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.user.UserProfile;

@Path("/")
public class ConfigResource {
 private final PluginSettingsFactory pluginSettingsFactory;
 private final TransactionTemplate transactionTemplate;
 private static final Logger logger = LoggerFactory.getLogger(ConfigResource.class);
 private final UserManager userManager;

 public ConfigResource(UserManager userManager, PluginSettingsFactory pluginSettingsFactory,
   TransactionTemplate transactionTemplate) {
  this.userManager = userManager;
  this.pluginSettingsFactory = pluginSettingsFactory;
  this.transactionTemplate = transactionTemplate;
 }

 @DELETE
 @Path("{id}")
 public Response delete(@PathParam("id") final String id, @Context HttpServletRequest request) {
  if (!isAdminLoggedIn(request)) {
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
 public Response get(@Context HttpServletRequest request) {
  if (!isAdminLoggedIn(request)) {
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

 private boolean isAdminLoggedIn(HttpServletRequest request) {
  final UserProfile user = userManager.getRemoteUser(request);
  if (user == null) {
   return false;
  }
  return userManager.isSystemAdmin(user.getUserKey());
 }

 /**
  * Store a single notification setting.
  */
 @POST
 @Consumes(APPLICATION_JSON)
 @Produces(APPLICATION_JSON)
 public Response post(final AdminFormValues config, @Context HttpServletRequest request) {
  if (!isAdminLoggedIn(request)) {
   return status(UNAUTHORIZED).build();
  }

  /**
   * Validate
   */
  try {
   getPrnfsNotification(config);
  } catch (final ValidationException e) {
   return status(BAD_REQUEST).entity(new AdminFormError(e.getField(), e.getError())).build();
  }

  transactionTemplate.execute(new TransactionCallback<Object>() {
   @Override
   public Object doInTransaction() {
    try {
     storeSettings(pluginSettingsFactory.createGlobalSettings(), config);
    } catch (final ValidationException e) {
     logger.error("", e);
    }
    return null;
   }
  });
  return noContent().build();
 }
}