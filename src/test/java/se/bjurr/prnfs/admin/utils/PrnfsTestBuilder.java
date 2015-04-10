package se.bjurr.prnfs.admin.utils;

import static com.google.common.base.Joiner.on;
import static com.google.common.base.Optional.fromNullable;
import static com.google.common.collect.Iterables.find;
import static com.google.common.collect.Iterables.tryFind;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newTreeMap;
import static com.google.common.collect.Maps.uniqueIndex;
import static java.lang.Boolean.TRUE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static se.bjurr.prnfs.admin.AdminFormValues.NAME;
import static se.bjurr.prnfs.admin.AdminFormValues.VALUE;
import static se.bjurr.prnfs.listener.UrlInvoker.shouldUseBasicAuth;
import static se.bjurr.prnfs.settings.PrnfsPredicates.predicate;
import static se.bjurr.prnfs.settings.SettingsStorage.FORM_IDENTIFIER_NAME;
import static se.bjurr.prnfs.settings.SettingsStorage.fakeRandom;

import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;

import org.mockito.Matchers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.bjurr.prnfs.admin.AdminFormError;
import se.bjurr.prnfs.admin.AdminFormValues;
import se.bjurr.prnfs.admin.ConfigResource;
import se.bjurr.prnfs.admin.data.PluginSettingsImpl;
import se.bjurr.prnfs.listener.PrnfsPullRequestEventListener;
import se.bjurr.prnfs.listener.UrlInvoker;

import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.transaction.TransactionCallback;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.user.UserProfile;
import com.atlassian.stash.event.pull.PullRequestEvent;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class PrnfsTestBuilder {
 public class FakeRandom extends Random {
  private static final long serialVersionUID = 8653569936232699363L;

  @Override
  public long nextLong() {
   return fakeRandomCounter++;
  }
 }

 private static Long fakeRandomCounter = null;

 private static Gson gson = new GsonBuilder().setPrettyPrinting().create();

 private static final Logger logger = LoggerFactory.getLogger(PrnfsTestBuilder.class);

 public static PrnfsTestBuilder prnfsTestBuilder() {
  return new PrnfsTestBuilder();
 }

 private final Map<String, AdminFormValues> adminFormValuesMap = newTreeMap();

 private long adminFormValuesMapCounter = 0;

 private final ConfigResource configResource;

 private final List<String> invokedUrl = newArrayList();

 private PrnfsPullRequestEventListener listener;

 private final PluginSettings pluginSettings;
 private final PluginSettingsFactory pluginSettingsFactory;

 private List<AdminFormError> postResponses;
 private HttpServletRequest request;

 private TransactionTemplate transactionTemplate;

 private final List<Optional<String>> usedPassword = newArrayList();

 private final List<Optional<String>> usedUser = newArrayList();

 private final UserKey userKey;

 private final UserManager userManager;

 private final UserProfile userProfile;

 private PrnfsTestBuilder() {
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
  listener = new PrnfsPullRequestEventListener(pluginSettingsFactory);
 }

 public PrnfsTestBuilder delete(String id) {
  configResource.delete(id, request);
  return this;
 }

 public void didNotUseBasicAuth() {
  for (int i = 0; i < usedUser.size(); i++) {
   assertFalse("user" + i + " \"" + usedUser.get(i).orNull() + "\" password" + i + " \"" + usedUser.get(i).orNull()
     + "\"", shouldUseBasicAuth(usedUser.get(i), usedPassword.get(i)));
  }
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

 public PrnfsTestBuilder hasFieldValueAt(AdminFormValues.FIELDS field, String value, String id) {
  for (final Map<String, String> fieldValue : getAdminFormFields().get(id)) {
   if (fieldValue.get(NAME).equals(field.name()) && fieldValue.get(VALUE).equals(value)) {
    return this;
   }
  }
  fail("Could not find " + field + " " + value + " at " + id);
  return this;
 }

 public PrnfsTestBuilder hasNoneEmptyFieldAt(AdminFormValues.FIELDS field, String id) {
  for (final Map<String, String> fieldValue : getAdminFormFields().get(id)) {
   if (fieldValue.get(NAME).equals(field.name())) {
    if (fieldValue.get(VALUE).trim().isEmpty()) {
     fail(field + " was empty");
    } else {
     return this;
    }
   }
  }
  fail("Could not find " + field + " at " + id);
  return this;
 }

 public PrnfsTestBuilder hasNotifications(int num) {
  assertEquals(num, getAdminFormFields().size());
  return this;
 }

 public void hasValidationError(AdminFormValues.FIELDS field, String value) {
  logger.info("Looking for " + field + "=" + value);
  for (final AdminFormError e : postResponses) {
   if (e.getField().equals(field.name())) {
    assertEquals(value, e.getValue());
    return;
   }
   logger.info(e.getField() + " " + e.getValue());
  }
  fail(field + " " + value + " not found");
 }

 public PrnfsTestBuilder invokedNoUrl() {
  assertEquals(0, invokedUrl.size());
  return this;
 }

 public PrnfsTestBuilder invokedOnlyUrl(String url) {
  assertEquals(1, invokedUrl.size());
  assertTrue(invokedUrl.get(0).equals(url));
  return this;
 }

 public PrnfsTestBuilder invokedPassword(String password) {
  for (Optional<String> opt : usedPassword) {
   if (password.equals(opt.orNull())) {
    return this;
   }
  }
  fail("Found: " + on(" ").join(usedPassword));
  return this;
 }

 public PrnfsTestBuilder invokedUrl(String url) {
  if (invokedUrl.size() == 1) {
   assertEquals(url, invokedUrl.get(0));
  } else {
   assertTrue(gson.toJson(invokedUrl), invokedUrl.contains(url));
  }
  return this;
 }

 public PrnfsTestBuilder invokedUser(String user) {
  for (Optional<String> opt : usedUser) {
   if (user.equals(opt.orNull())) {
    return this;
   }
  }
  fail("Found: " + on(" ").join(usedPassword));
  return this;
 }

 public PrnfsTestBuilder isLoggedInAsAdmin() {
  when(userProfile.getUserKey()).thenReturn(userKey);
  when(userManager.isSystemAdmin(Matchers.any(UserKey.class))).thenReturn(TRUE);
  when(userManager.getRemoteUser(Matchers.any(HttpServletRequest.class))).thenReturn(userProfile);
  return this;
 }

 public PrnfsTestBuilder isNotLoggedInAsAdmin() {
  when(userProfile.getUserKey()).thenReturn(null);
  return this;
 }

 public PrnfsTestBuilder store() {
  postResponses = newArrayList();
  for (final AdminFormValues adminFormValues : adminFormValuesMap.values()) {
   final Optional<Object> postResponseOpt = fromNullable(configResource.post(adminFormValues, request).getEntity());
   if (postResponseOpt.isPresent()) {
    postResponses.add((AdminFormError) postResponseOpt.get());
   }
  }
  return this;
 }

 public PrnfsTestBuilder trigger(PullRequestEvent event) {
  listener.setUrlInvoker(new UrlInvoker() {
   @Override
   public void ivoke(String url, Optional<String> userParam, Optional<String> passwordParam) {
    invokedUrl.add(url);
    usedUser.add(userParam);
    usedPassword.add(passwordParam);
   }
  });
  listener.handleEvent(event);
  return this;
 }

 public PrnfsTestBuilder withNotification(AdminFormValues adminFormValues) {
  final Optional<Map<String, String>> existing = tryFind(adminFormValues, predicate(FORM_IDENTIFIER_NAME));
  if (existing.isPresent()) {
   this.adminFormValuesMap.put(existing.get().get(VALUE), adminFormValues);
  } else {
   this.adminFormValuesMap.put((adminFormValuesMapCounter++) + "", adminFormValues);
  }
  return this;
 }
}
