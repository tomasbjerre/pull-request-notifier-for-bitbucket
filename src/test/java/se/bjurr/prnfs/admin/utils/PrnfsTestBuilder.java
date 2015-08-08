package se.bjurr.prnfs.admin.utils;

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
import static se.bjurr.prnfs.admin.utils.PullRequestEventBuilder.pullRequestEventBuilder;
import static se.bjurr.prnfs.listener.PrnfsPullRequestEventListener.setInvoker;
import static se.bjurr.prnfs.listener.UrlInvoker.getHeaderValue;
import static se.bjurr.prnfs.settings.PrnfsPredicates.predicate;
import static se.bjurr.prnfs.settings.SettingsStorage.fakeRandom;

import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;

import com.atlassian.stash.server.ApplicationPropertiesService;
import org.mockito.Matchers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.bjurr.prnfs.admin.AdminFormError;
import se.bjurr.prnfs.admin.AdminFormValues;
import se.bjurr.prnfs.admin.ConfigResource;
import se.bjurr.prnfs.admin.data.PluginSettingsImpl;
import se.bjurr.prnfs.listener.PrnfsPullRequestEventListener;
import se.bjurr.prnfs.listener.PrnfsPullRequestEventListener.Invoker;
import se.bjurr.prnfs.listener.UrlInvoker;
import se.bjurr.prnfs.settings.Header;

import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.transaction.TransactionCallback;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.user.UserProfile;
import com.atlassian.stash.event.pull.PullRequestEvent;
import com.atlassian.stash.repository.RepositoryService;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Optional;

public class PrnfsTestBuilder {
 public class FakeRandom extends Random {
  private static final long serialVersionUID = 8653569936232699363L;

  @Override
  public long nextLong() {
   return fakeRandomCounter++;
  }
 }

 private static Long fakeRandomCounter = null;

 private static final Logger logger = LoggerFactory.getLogger(PrnfsTestBuilder.class);

 public static PrnfsTestBuilder prnfsTestBuilder() {
  return new PrnfsTestBuilder();
 }

 private final Map<String, AdminFormValues> adminFormValuesMap = newTreeMap();

 private long adminFormValuesMapCounter = 0;

 private final ConfigResource configResource;

 private PrnfsPullRequestEventListener listener;

 private final PluginSettings pluginSettings;

 private final PluginSettingsFactory pluginSettingsFactory;

 private final RepositoryService repositoryService;

 private final ApplicationPropertiesService propertiesService;

 private HttpServletRequest request;

 private TransactionTemplate transactionTemplate;

 private final List<UrlInvoker> urlInvokers = newArrayList();

 private final UserKey userKey;

 private final UserManager userManager;

 private final UserProfile userProfile;

 private List<AdminFormError> postResponses;

 private PrnfsTestBuilder() {
  fakeRandomCounter = 0L;
  fakeRandom(new FakeRandom());
  pluginSettings = new PluginSettingsImpl();
  userProfile = mock(UserProfile.class);
  userKey = new UserKey("asd");
  userManager = mock(UserManager.class);
  pluginSettingsFactory = mock(PluginSettingsFactory.class);
  repositoryService = mock(RepositoryService.class);
  propertiesService = mock(ApplicationPropertiesService.class);
  transactionTemplate = new TransactionTemplate() {
   @Override
   public <T> T execute(TransactionCallback<T> action) {
    return action.doInTransaction();
   }
  };
  when(pluginSettingsFactory.createGlobalSettings()).thenReturn(pluginSettings);
  configResource = new ConfigResource(userManager, pluginSettingsFactory, transactionTemplate);
  listener = new PrnfsPullRequestEventListener(pluginSettingsFactory, repositoryService, propertiesService);
 }

 public PrnfsTestBuilder delete(String id) {
  configResource.delete(id, request);
  return this;
 }

 public PrnfsTestBuilder didNotSendHeaders() {
  for (UrlInvoker u : urlInvokers) {
   assertTrue(toMap(u.getHeaders()).isEmpty());
  }
  return this;
 }

 public PrnfsTestBuilder didNotSendHeader(int index, String name) {
  assertFalse(index + " " + name, toMap(urlInvokers.get(index).getHeaders()).containsKey(name));
  return this;
 }

 public PrnfsTestBuilder usedHeader(int index, String name, String value) {
  Map<String, Header> headerMap = toMap(urlInvokers.get(index).getHeaders());
  if (headerMap.containsKey(name)) {
   assertEquals(index + " " + name, value, getHeaderValue(headerMap.get(name)));
  } else {
   fail(Joiner.on(", ").join(headerMap.keySet()));
  }
  return this;
 }

 private Map<String, Header> toMap(List<Header> headers) {
  return uniqueIndex(headers, new Function<Header, String>() {
   @Override
   public String apply(Header input) {
    return input.getName();
   }
  });
 }

 @SuppressWarnings("unchecked")
 private Map<String, AdminFormValues> getAdminFormFields() {
  return uniqueIndex((List<AdminFormValues>) configResource.get(request).getEntity(),
    new Function<AdminFormValues, String>() {
     @Override
     public String apply(AdminFormValues input) {
      return find(input, predicate(AdminFormValues.FIELDS.FORM_IDENTIFIER.name())).get(VALUE);
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
  assertEquals(0, urlInvokers.size());
  return this;
 }

 public PrnfsTestBuilder invokedOnlyUrl(String url) {
  assertEquals(1, urlInvokers.size());
  assertTrue(urlInvokers.get(0).getUrlParam().equals(url));
  return this;
 }

 public PrnfsTestBuilder invokedUrl(int index, String url) {
  assertEquals(url, urlInvokers.get(index).getUrlParam());
  return this;
 }

 public PrnfsTestBuilder invokedMethod(String method) {
  for (UrlInvoker u : urlInvokers) {
   if (method.equals(u.getMethod())) {
    return this;
   }
  }
  fail(method);
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
  setInvoker(new Invoker() {
   @Override
   public void invoke(UrlInvoker urlInvoker) {
    urlInvokers.add(urlInvoker);
   }
  });
  listener.handleEvent(event);
  return this;
 }

 public PullRequestEventBuilder triggerPullRequestEventBuilder() {
  return pullRequestEventBuilder(this);
 }

 public PrnfsTestBuilder withNotification(AdminFormValues adminFormValues) {
  final Optional<Map<String, String>> existing = tryFind(adminFormValues,
    predicate(AdminFormValues.FIELDS.FORM_IDENTIFIER.name()));
  if (existing.isPresent()) {
   this.adminFormValuesMap.put(existing.get().get(VALUE), adminFormValues);
  } else {
   this.adminFormValuesMap.put((adminFormValuesMapCounter++) + "", adminFormValues);
  }
  return this;
 }

 public void didNotSendPostContentAt(int i) {
  assertFalse(urlInvokers.get(i).shouldPostContent());
 }

 public void didSendPostContentAt(int i, String string) {
  assertEquals(string, urlInvokers.get(i).getPostContent().get());
 }

 public PrnfsTestBuilder usedNoProxy(int index) {
  assertFalse(urlInvokers.get(index).shouldUseProxy());
  return this;
 }

 public PrnfsTestBuilder usedNoProxyUser(int index) {
  assertFalse(urlInvokers.get(index).getProxyUser().isPresent());
  assertFalse(urlInvokers.get(index).shouldAuthenticateProxy());
  return this;
 }

 public PrnfsTestBuilder usedNoProxyPassword(int index) {
  assertFalse(urlInvokers.get(index).getProxyPassword().isPresent());
  assertFalse(urlInvokers.get(index).shouldAuthenticateProxy());
  return this;
 }

 public PrnfsTestBuilder usedNoProxyAuthentication(int index) {
  assertFalse(urlInvokers.get(index).shouldAuthenticateProxy());
  return this;
 }

 public PrnfsTestBuilder usedProxyHost(int index, String host) {
  assertEquals(host, urlInvokers.get(index).getProxyHost().get());
  return this;
 }

 public PrnfsTestBuilder usedProxyPort(int index, Integer port) {
  assertEquals(port, urlInvokers.get(index).getProxyPort());
  return this;
 }

 public PrnfsTestBuilder usedProxyUser(int i, String user) {
  assertEquals(user, urlInvokers.get(i).getProxyUser().get());
  assertTrue(urlInvokers.get(i).shouldAuthenticateProxy());
  return this;
 }

 public PrnfsTestBuilder usedProxyPassword(int i, String password) {
  assertEquals(password, urlInvokers.get(i).getProxyPassword().get());
  assertTrue(urlInvokers.get(i).shouldAuthenticateProxy());
  return this;
 }

 public RepositoryService getRepositoryService() {
  return repositoryService;
 }
}
