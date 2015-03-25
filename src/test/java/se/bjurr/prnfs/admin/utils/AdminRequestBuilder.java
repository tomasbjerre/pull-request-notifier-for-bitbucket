package se.bjurr.prnfs.admin.utils;

import static com.google.common.base.Optional.fromNullable;
import static com.google.common.collect.Iterables.find;
import static com.google.common.collect.Iterables.tryFind;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newTreeMap;
import static com.google.common.collect.Maps.uniqueIndex;
import static java.lang.Boolean.TRUE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static se.bjurr.prnfs.admin.AdminFormValues.NAME;
import static se.bjurr.prnfs.admin.AdminFormValues.VALUE;
import static se.bjurr.prnfs.settings.PrnfsPredicates.predicate;
import static se.bjurr.prnfs.settings.SettingsStorage.FORM_IDENTIFIER_NAME;
import static se.bjurr.prnfs.settings.SettingsStorage.fakeRandom;

import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;

import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.bjurr.prnfs.admin.AdminFormValues;
import se.bjurr.prnfs.admin.ConfigResource;
import se.bjurr.prnfs.admin.AdminFormError;
import se.bjurr.prnfs.admin.data.PluginSettingsImpl;

import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.transaction.TransactionCallback;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.user.UserProfile;
import com.google.common.base.Function;
import com.google.common.base.Optional;

public class AdminRequestBuilder {
 public class FakeRandom extends Random {
  private static final long serialVersionUID = 8653569936232699363L;

  @Override
  public long nextLong() {
   return fakeRandomCounter++;
  }
 }

 private static Long fakeRandomCounter = null;

 private static final Logger logger = LoggerFactory.getLogger(AdminRequestBuilder.class);

 public static AdminRequestBuilder adminRequestBuilder() {
  return new AdminRequestBuilder();
 }

 private final Map<String, AdminFormValues> adminFormValuesMap = newTreeMap();
 private long adminFormValuesMapCounter = 0;

 private final ConfigResource configResource;

 private final PluginSettings pluginSettings;

 private final PluginSettingsFactory pluginSettingsFactory;

 private List<AdminFormError> postResponses;

 private HttpServletRequest request;
 private TransactionTemplate transactionTemplate;

 private final UserKey userKey;
 private final UserManager userManager;

 private final UserProfile userProfile;

 private AdminRequestBuilder() {
  fakeRandomCounter = 0L;
  fakeRandom(new FakeRandom());
  pluginSettings = new PluginSettingsImpl();
  userProfile = mock(UserProfile.class);
  userKey = new UserKey("asd");
  userManager = mock(UserManager.class);
  pluginSettingsFactory = mock(PluginSettingsFactory.class);
  transactionTemplate = new TransactionTemplate() {
   @Override
   public <T> T execute(TransactionCallback<T> action) {
    return action.doInTransaction();
   }
  };
  when(pluginSettingsFactory.createGlobalSettings()).thenReturn(pluginSettings);
  configResource = new ConfigResource(userManager, pluginSettingsFactory, transactionTemplate);
 }

 public AdminRequestBuilder delete(String id) {
  configResource.delete(id, request);
  return this;
 }

 @SuppressWarnings("unchecked")
 private Map<String, AdminFormValues> getAdminFormFields() {
  return uniqueIndex((List<AdminFormValues>) configResource.get(request).getEntity(),
    new Function<AdminFormValues, String>() {
     @Override
     public String apply(AdminFormValues input) {
      return find(input, predicate(FORM_IDENTIFIER_NAME)).get(VALUE);
     }
    });
 }

 public AdminRequestBuilder hasFieldValueAt(String field, String value, String id) {
  for (Map<String, String> fieldValue : getAdminFormFields().get(id)) {
   if (fieldValue.get(NAME).equals(field) && fieldValue.get(VALUE).equals(value)) {
    return this;
   }
  }
  fail("Could not find " + field + " " + value + " at " + id);
  return this;
 }

 public AdminRequestBuilder hasNoneEmptyFieldAt(String field, String id) {
  for (Map<String, String> fieldValue : getAdminFormFields().get(id)) {
   if (fieldValue.get(NAME).equals(field)) {
    if (fieldValue.get(VALUE).isEmpty()) {
     fail(field + " was empty");
    } else {
     return this;
    }
   }
  }
  fail("Could not find " + field + " at " + id);
  return this;
 }

 public AdminRequestBuilder hasNotifications(int num) {
  assertEquals(num, getAdminFormFields().size());
  return this;
 }

 public void hasValidationError(String field, String value) {
  logger.info("Looking for " + field + "=" + value);
  for (AdminFormError e : postResponses) {
   if (e.getField().equals(field) && e.getValue().equals(value)) {
    return;
   }
   logger.info(e.getField() + " " + e.getValue());
  }
  fail(field + " " + value + " not found");
 }

 public AdminRequestBuilder isLoggedInAsAdmin() {
  when(userProfile.getUserKey()).thenReturn(userKey);
  when(userManager.isSystemAdmin(Mockito.any(UserKey.class))).thenReturn(TRUE);
  when(userManager.getRemoteUser(Mockito.any(HttpServletRequest.class))).thenReturn(userProfile);
  return this;
 }

 public AdminRequestBuilder isNotLoggedInAsAdmin() {
  when(userProfile.getUserKey()).thenReturn(null);
  return this;
 }

 public AdminRequestBuilder store() {
  postResponses = newArrayList();
  for (AdminFormValues adminFormValues : adminFormValuesMap.values()) {
   Optional<Object> postResponseOpt = fromNullable(configResource.post(adminFormValues, request).getEntity());
   if (postResponseOpt.isPresent()) {
    postResponses.add((AdminFormError) postResponseOpt.get());
   }
  }
  return this;
 }

 public AdminRequestBuilder withNotification(AdminFormValues adminFormValues) {
  Optional<Map<String, String>> existing = tryFind(adminFormValues, predicate(FORM_IDENTIFIER_NAME));
  if (existing.isPresent()) {
   this.adminFormValuesMap.put(existing.get().get(VALUE), adminFormValues);
  } else {
   this.adminFormValuesMap.put((adminFormValuesMapCounter++) + "", adminFormValues);
  }
  return this;
 }
}
