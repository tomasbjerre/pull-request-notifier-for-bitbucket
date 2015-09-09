package se.bjurr.prnfs.admin.utils;

import static com.google.common.base.Optional.fromNullable;
import static com.google.common.base.Throwables.propagate;
import static com.google.common.collect.Iterables.find;
import static com.google.common.collect.Iterables.tryFind;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newTreeMap;
import static com.google.common.collect.Maps.uniqueIndex;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static se.bjurr.prnfs.admin.AdminFormValues.NAME;
import static se.bjurr.prnfs.admin.AdminFormValues.VALUE;
import static se.bjurr.prnfs.admin.utils.PullRequestEventBuilder.pullRequestEventBuilder;
import static se.bjurr.prnfs.listener.PrnfsPullRequestEventListener.setInvoker;
import static se.bjurr.prnfs.listener.UrlInvoker.getHeaderValue;
import static se.bjurr.prnfs.settings.PrnfsPredicates.predicate;
import static se.bjurr.prnfs.settings.SettingsStorage.fakeRandom;
import static se.bjurr.prnfs.settings.SettingsStorage.getPrnfsSettings;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.mockito.Matchers;

import se.bjurr.prnfs.ManualResource;
import se.bjurr.prnfs.admin.AdminFormError;
import se.bjurr.prnfs.admin.AdminFormValues;
import se.bjurr.prnfs.admin.ConfigResource;
import se.bjurr.prnfs.admin.data.PluginSettingsImpl;
import se.bjurr.prnfs.listener.Invoker;
import se.bjurr.prnfs.listener.PrnfsPullRequestEventListener;
import se.bjurr.prnfs.listener.PrnfsRenderer;
import se.bjurr.prnfs.listener.UrlInvoker;
import se.bjurr.prnfs.listener.UrlInvoker.HTTP_METHOD;
import se.bjurr.prnfs.settings.Header;
import se.bjurr.prnfs.settings.PrnfsButton;
import se.bjurr.prnfs.settings.PrnfsSettings;

import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.transaction.TransactionCallback;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.user.UserProfile;
import com.atlassian.stash.event.pull.PullRequestEvent;
import com.atlassian.stash.pull.PullRequest;
import com.atlassian.stash.pull.PullRequestMergeability;
import com.atlassian.stash.pull.PullRequestService;
import com.atlassian.stash.repository.RepositoryService;
import com.atlassian.stash.server.ApplicationPropertiesService;
import com.atlassian.stash.user.EscalatedSecurityContext;
import com.atlassian.stash.user.Permission;
import com.atlassian.stash.user.SecurityService;
import com.atlassian.stash.user.UserService;
import com.atlassian.stash.util.Operation;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class PrnfsTestBuilder {
 public class FakeRandom extends Random {
  private static final long serialVersionUID = 8653569936232699363L;

  @Override
  public long nextLong() {
   return fakeRandomCounter++;
  }
 }

 private static Long fakeRandomCounter = null;

 private static final Logger logger = Logger.getLogger(PrnfsTestBuilder.class.getName());

 public static PrnfsTestBuilder prnfsTestBuilder() {
  return new PrnfsTestBuilder();
 }

 private final Map<String, AdminFormValues> adminFormValuesMap = newTreeMap();

 private long adminFormValuesMapCounter = 0;

 private final ConfigResource configResource;

 private PrnfsPullRequestEventListener listener;

 private final ManualResource manualResouce;

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

 private EscalatedSecurityContext escalatedSecurityContext;

 private PullRequestService pullRequestService;

 private PullRequest pullRequest;

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
  SecurityService securityService = mock(SecurityService.class);
  escalatedSecurityContext = mock(EscalatedSecurityContext.class);
  when(securityService.withPermission(Matchers.any(Permission.class), Matchers.anyString())).thenReturn(
    escalatedSecurityContext);
  configResource = new ConfigResource(userManager, pluginSettingsFactory, transactionTemplate, securityService);
  pullRequestService = mock(PullRequestService.class);
  listener = new PrnfsPullRequestEventListener(pluginSettingsFactory, repositoryService, propertiesService,
    pullRequestService);
  UserService userService = mock(UserService.class);
  withPullRequest(pullRequestEventBuilder().build().getPullRequest());
  manualResouce = new ManualResource(userManager, userService, pluginSettingsFactory, pullRequestService, listener,
    repositoryService, propertiesService, securityService);
 }

 public PrnfsTestBuilder delete(String id) throws Exception {
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
 private Map<String, AdminFormValues> getAdminFormFields() throws Exception {
  return uniqueIndex((List<AdminFormValues>) configResource.get(request).getEntity(),
    new Function<AdminFormValues, String>() {
     @Override
     public String apply(AdminFormValues input) {
      return find(input, predicate(AdminFormValues.FIELDS.FORM_IDENTIFIER.name())).get(VALUE);
     }
    });
 }

 public PrnfsTestBuilder hasFieldValueAt(AdminFormValues.FIELDS field, String value, String id) throws Exception {
  for (final Map<String, String> fieldValue : getAdminFormFields().get(id)) {
   if (fieldValue.get(NAME).equals(field.name()) && fieldValue.get(VALUE).equals(value)) {
    return this;
   }
  }
  fail("Could not find " + field + " " + value + " at " + id);
  return this;
 }

 public PrnfsTestBuilder hasNoneEmptyFieldAt(AdminFormValues.FIELDS field, String id) throws Exception {
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

 public PrnfsTestBuilder hasNotifications(int num) throws Exception {
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
  assertEquals(url, urlInvokers.get(0).getUrlParam());
  return this;
 }

 public PrnfsTestBuilder invokedUrl(int index, String url) {
  assertEquals(url, urlInvokers.get(index).getUrlParam());
  return this;
 }

 public PrnfsTestBuilder invokedMethod(HTTP_METHOD method) {
  for (UrlInvoker u : urlInvokers) {
   if (method.equals(u.getMethod())) {
    return this;
   }
  }
  fail(method.name());
  return this;
 }

 public PrnfsTestBuilder isLoggedInAsAdmin() {
  when(userProfile.getUserKey()).thenReturn(userKey);
  when(userManager.isSystemAdmin(Matchers.any(UserKey.class))).thenReturn(TRUE);
  when(userManager.getRemoteUser(Matchers.any(HttpServletRequest.class))).thenReturn(userProfile);
  return this;
 }

 public PrnfsTestBuilder withBaseUrl(String baseUrl) throws Exception {
  when(propertiesService.getBaseUrl()).thenReturn(new URI(baseUrl));
  return this;
 }

 public PrnfsTestBuilder isNotLoggedInAsAdmin() {
  when(userProfile.getUserKey()).thenReturn(null);
  return this;
 }

 public PrnfsTestBuilder store() throws Exception {
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

 public PrnfsTestBuilder withPullRequest(PullRequest pullRequest) {
  this.pullRequest = pullRequest;
  return this;
 }

 public PrnfsTestBuilder triggerButton(String formIdentifier) throws Exception {
  when(pullRequestService.getById(anyInt(), anyLong())).thenReturn(pullRequest);
  try {
   PrnfsSettings prnfsSettings = getPrnfsSettings(pluginSettings);
   when(escalatedSecurityContext.call(Matchers.any(Operation.class))).thenReturn(prnfsSettings);
  } catch (Throwable e) {
   propagate(e);
  }
  setInvoker(new Invoker() {
   @Override
   public void invoke(UrlInvoker urlInvoker) {
    urlInvokers.add(urlInvoker);
   }
  });
  Integer repositoryId = 0;
  Long pullRequestId = 0L;
  manualResouce.post(request, repositoryId, pullRequestId, formIdentifier);
  return this;
 }

 public PrnfsTestBuilder hasNoButtonsEnabled() throws Exception {
  return hasButtonsEnabled();
 }

 public PrnfsTestBuilder hasButtonsEnabled(final String... formIdentifiers) throws Exception {
  Integer repositoryId = 0;
  Long pullRequestId = 0L;
  List<PrnfsButton> enabledButtons = new Gson().fromJson(
    (String) manualResouce.get(request, repositoryId, pullRequestId).getEntity(), new TypeToken<List<PrnfsButton>>() {
    }.getType());
  assertEquals(formIdentifiers.length, enabledButtons.size());
  for (final String formIdentifier : formIdentifiers) {
   assertTrue(tryFind(enabledButtons, new Predicate<PrnfsButton>() {
    @Override
    public boolean apply(PrnfsButton input) {
     return input.getFormIdentifier().equals(formIdentifier);
    }
   }).isPresent());
  }
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
  assertTrue(urlInvokers.get(i).shouldPostContent());
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

 public PrnfsTestBuilder withResponse(final String url, final String response) {
  PrnfsRenderer.setInvoker(new Invoker() {
   @Override
   public void invoke(UrlInvoker urlInvoker) {
    if (urlInvoker.getUrlParam().equals(url)) {
     urlInvoker.setResponseString(response);
    }
   }
  });
  return this;
 }

 public PrnfsTestBuilder isConflicting() {
  return isConflicting(TRUE);
 }

 public PrnfsTestBuilder isNotConflicting() {
  return isConflicting(FALSE);
 }

 private PrnfsTestBuilder isConflicting(Boolean conflicting) {
  PullRequestMergeability value = mock(PullRequestMergeability.class);
  when(value.isConflicted()).thenReturn(conflicting);
  when(pullRequestService.canMerge(anyInt(), anyInt())).thenReturn(value);
  return this;
 }
}
