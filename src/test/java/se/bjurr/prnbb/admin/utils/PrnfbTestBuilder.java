package se.bjurr.prnbb.admin.utils;

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
import static se.bjurr.prnbb.admin.utils.PullRequestEventBuilder.pullRequestEventBuilder;
import static se.bjurr.prnfb.admin.AdminFormValues.NAME;
import static se.bjurr.prnfb.admin.AdminFormValues.VALUE;
import static se.bjurr.prnfb.admin.AdminFormValues.FIELDS.FORM_IDENTIFIER;
import static se.bjurr.prnfb.listener.PrnfbPullRequestEventListener.setInvoker;
import static se.bjurr.prnfb.listener.UrlInvoker.getHeaderValue;
import static se.bjurr.prnfb.settings.PrnfbPredicates.predicate;
import static se.bjurr.prnfb.settings.SettingsStorage.fakeRandom;
import static se.bjurr.prnfb.settings.SettingsStorage.getPrnfbSettings;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.mockito.Matchers;

import se.bjurr.prnbb.admin.data.PluginSettingsImpl;
import se.bjurr.prnfb.ManualResource;
import se.bjurr.prnfb.admin.AdminFormError;
import se.bjurr.prnfb.admin.AdminFormValues;
import se.bjurr.prnfb.admin.ConfigResource;
import se.bjurr.prnfb.listener.PrnfbPullRequestEventListener;
import se.bjurr.prnfb.listener.PrnfbRenderer;
import se.bjurr.prnfb.listener.UrlInvoker;
import se.bjurr.prnfb.listener.UrlInvoker.HTTP_METHOD;
import se.bjurr.prnfb.settings.Header;
import se.bjurr.prnfb.settings.PrnfbButton;
import se.bjurr.prnfb.settings.PrnfbSettings;

import com.atlassian.bitbucket.event.pull.PullRequestEvent;
import com.atlassian.bitbucket.permission.Permission;
import com.atlassian.bitbucket.pull.PullRequest;
import com.atlassian.bitbucket.pull.PullRequestMergeability;
import com.atlassian.bitbucket.pull.PullRequestService;
import com.atlassian.bitbucket.repository.RepositoryService;
import com.atlassian.bitbucket.server.ApplicationPropertiesService;
import com.atlassian.bitbucket.user.EscalatedSecurityContext;
import com.atlassian.bitbucket.user.SecurityService;
import com.atlassian.bitbucket.user.UserService;
import com.atlassian.bitbucket.util.Operation;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.transaction.TransactionCallback;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.user.UserProfile;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class PrnfbTestBuilder {
 public class FakeRandom extends Random {
  private static final long serialVersionUID = 8653569936232699363L;

  @Override
  public long nextLong() {
   return fakeRandomCounter++;
  }
 }

 private static Long fakeRandomCounter = null;

 private static final Logger logger = Logger.getLogger(PrnfbTestBuilder.class.getName());

 public static PrnfbTestBuilder prnfbTestBuilder() {
  return new PrnfbTestBuilder();
 }

 private final Map<String, AdminFormValues> adminFormValuesMap = newTreeMap();

 private long adminFormValuesMapCounter = 0;

 private final ConfigResource configResource;

 private PrnfbPullRequestEventListener listener;

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

 private PrnfbTestBuilder() {
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
  listener = new PrnfbPullRequestEventListener(pluginSettingsFactory, repositoryService, propertiesService,
    pullRequestService, new SyncExecutorService());
  UserService userService = mock(UserService.class);
  withPullRequest(pullRequestEventBuilder().build().getPullRequest());
  manualResouce = new ManualResource(userManager, userService, pluginSettingsFactory, pullRequestService, listener,
    repositoryService, propertiesService, securityService);
 }

 public PrnfbTestBuilder delete(String id) throws Exception {
  configResource.delete(id, request);
  return this;
 }

 public PrnfbTestBuilder didNotSendHeaders() {
  for (UrlInvoker u : urlInvokers) {
   assertTrue(toMap(u.getHeaders()).isEmpty());
  }
  return this;
 }

 public PrnfbTestBuilder didNotSendHeader(int index, String name) {
  assertFalse(index + " " + name, toMap(urlInvokers.get(index).getHeaders()).containsKey(name));
  return this;
 }

 public PrnfbTestBuilder usedHeader(int index, String name, String value) {
  Map<String, Header> headerMap = toMap(urlInvokers.get(index).getHeaders());
  if (headerMap.containsKey(name)) {
   assertEquals(index + " " + name, value, getHeaderValue(headerMap.get(name)));
  } else {
   fail(Joiner.on(", ").join(headerMap.keySet()));
  }
  return this;
 }

 private Map<String, Header> toMap(List<Header> headers) {
  return uniqueIndex(headers, (Function<Header, String>) input -> input.getName());
 }

 @SuppressWarnings("unchecked")
 private Map<String, AdminFormValues> getAdminFormFields() throws Exception {
  return uniqueIndex((List<AdminFormValues>) configResource.get(request).getEntity(),
    (Function<AdminFormValues, String>) input -> find(input, predicate(FORM_IDENTIFIER.name())).get(VALUE));
 }

 public PrnfbTestBuilder hasFieldValueAt(AdminFormValues.FIELDS field, String value, String id) throws Exception {
  for (final Map<String, String> fieldValue : getAdminFormFields().get(id)) {
   if (fieldValue.get(NAME).equals(field.name()) && fieldValue.get(VALUE).equals(value)) {
    return this;
   }
  }
  fail("Could not find " + field + " " + value + " at " + id);
  return this;
 }

 public PrnfbTestBuilder hasNoneEmptyFieldAt(AdminFormValues.FIELDS field, String id) throws Exception {
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

 public PrnfbTestBuilder hasNotifications(int num) throws Exception {
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

 public PrnfbTestBuilder invokedNoUrl() {
  assertEquals(0, urlInvokers.size());
  return this;
 }

 public PrnfbTestBuilder invokedOnlyUrl(String url) {
  assertEquals(1, urlInvokers.size());
  assertEquals(url, urlInvokers.get(0).getUrlParam());
  return this;
 }

 public PrnfbTestBuilder invokedUrl(int index, String url) {
  assertEquals(url, urlInvokers.get(index).getUrlParam());
  return this;
 }

 public PrnfbTestBuilder invokedMethod(HTTP_METHOD method) {
  for (UrlInvoker u : urlInvokers) {
   if (method.equals(u.getMethod())) {
    return this;
   }
  }
  fail(method.name());
  return this;
 }

 public PrnfbTestBuilder isLoggedInAsAdmin() {
  when(userProfile.getUserKey()).thenReturn(userKey);
  when(userManager.isSystemAdmin(Matchers.any(UserKey.class))).thenReturn(TRUE);
  when(userManager.getRemoteUser(Matchers.any(HttpServletRequest.class))).thenReturn(userProfile);
  return this;
 }

 public PrnfbTestBuilder withBaseUrl(String baseUrl) throws Exception {
  when(propertiesService.getBaseUrl()).thenReturn(new URI(baseUrl));
  return this;
 }

 public PrnfbTestBuilder isNotLoggedInAsAdmin() {
  when(userProfile.getUserKey()).thenReturn(null);
  return this;
 }

 public PrnfbTestBuilder store() throws Exception {
  postResponses = newArrayList();
  for (final AdminFormValues adminFormValues : adminFormValuesMap.values()) {
   final Optional<Object> postResponseOpt = fromNullable(configResource.post(adminFormValues, request).getEntity());
   if (postResponseOpt.isPresent()) {
    postResponses.add((AdminFormError) postResponseOpt.get());
   }
  }
  return this;
 }

 public PrnfbTestBuilder trigger(PullRequestEvent event) {
  setInvoker(urlInvoker -> urlInvokers.add(urlInvoker));
  listener.handleEventAsync(event);
  return this;
 }

 public PrnfbTestBuilder withPullRequest(PullRequest pullRequest) {
  this.pullRequest = pullRequest;
  return this;
 }

 @SuppressWarnings("unchecked")
 public PrnfbTestBuilder triggerButton(String formIdentifier) throws Exception {
  when(pullRequestService.getById(anyInt(), anyLong())).thenReturn(pullRequest);
  try {
   PrnfbSettings prnfbSettings = getPrnfbSettings(pluginSettings);
   Operation<Object, RuntimeException> callMatcher = Matchers.any(Operation.class);
   when(escalatedSecurityContext.call(callMatcher)).thenReturn(prnfbSettings);
  } catch (Throwable e) {
   propagate(e);
  }
  setInvoker(urlInvoker -> urlInvokers.add(urlInvoker));
  Integer repositoryId = 0;
  Long pullRequestId = 0L;
  manualResouce.post(request, repositoryId, pullRequestId, formIdentifier);
  return this;
 }

 public PrnfbTestBuilder hasNoButtonsEnabled() throws Exception {
  return hasButtonsEnabled();
 }

 public PrnfbTestBuilder hasButtonsEnabled(final String... formIdentifiers) throws Exception {
  Integer repositoryId = 0;
  Long pullRequestId = 0L;
  List<PrnfbButton> enabledButtons = new Gson().fromJson(
    (String) manualResouce.get(request, repositoryId, pullRequestId).getEntity(), new TypeToken<List<PrnfbButton>>() {
    }.getType());
  assertEquals(formIdentifiers.length, enabledButtons.size());
  for (final String formIdentifier : formIdentifiers) {
   assertTrue(tryFind(enabledButtons, input -> input.getFormIdentifier().equals(formIdentifier)).isPresent());
  }
  return this;
 }

 public PullRequestEventBuilder triggerPullRequestEventBuilder() {
  return pullRequestEventBuilder(this);
 }

 public PrnfbTestBuilder withNotification(AdminFormValues adminFormValues) {
  final Optional<Map<String, String>> existing = tryFind(adminFormValues, predicate(FORM_IDENTIFIER.name()));
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

 public PrnfbTestBuilder usedNoProxy(int index) {
  assertFalse(urlInvokers.get(index).shouldUseProxy());
  return this;
 }

 public PrnfbTestBuilder usedNoProxyUser(int index) {
  assertFalse(urlInvokers.get(index).getProxyUser().isPresent());
  assertFalse(urlInvokers.get(index).shouldAuthenticateProxy());
  return this;
 }

 public PrnfbTestBuilder usedNoProxyPassword(int index) {
  assertFalse(urlInvokers.get(index).getProxyPassword().isPresent());
  assertFalse(urlInvokers.get(index).shouldAuthenticateProxy());
  return this;
 }

 public PrnfbTestBuilder usedNoProxyAuthentication(int index) {
  assertFalse(urlInvokers.get(index).shouldAuthenticateProxy());
  return this;
 }

 public PrnfbTestBuilder usedProxyHost(int index, String host) {
  assertEquals(host, urlInvokers.get(index).getProxyHost().get());
  return this;
 }

 public PrnfbTestBuilder usedProxyPort(int index, Integer port) {
  assertEquals(port, urlInvokers.get(index).getProxyPort());
  return this;
 }

 public PrnfbTestBuilder usedProxyUser(int i, String user) {
  assertEquals(user, urlInvokers.get(i).getProxyUser().get());
  assertTrue(urlInvokers.get(i).shouldAuthenticateProxy());
  return this;
 }

 public PrnfbTestBuilder usedProxyPassword(int i, String password) {
  assertEquals(password, urlInvokers.get(i).getProxyPassword().get());
  assertTrue(urlInvokers.get(i).shouldAuthenticateProxy());
  return this;
 }

 public RepositoryService getRepositoryService() {
  return repositoryService;
 }

 public PrnfbTestBuilder withResponse(final String url, final String response) {
  PrnfbRenderer.setInvoker(urlInvoker -> {
   if (urlInvoker.getUrlParam().equals(url)) {
    urlInvoker.setResponseString(response);
   }
  });
  return this;
 }

 public PrnfbTestBuilder isConflicting() {
  return isConflicting(TRUE);
 }

 public PrnfbTestBuilder isNotConflicting() {
  return isConflicting(FALSE);
 }

 private PrnfbTestBuilder isConflicting(Boolean conflicting) {
  PullRequestMergeability value = mock(PullRequestMergeability.class);
  when(value.isConflicted()).thenReturn(conflicting);
  when(pullRequestService.canMerge(anyInt(), anyInt())).thenReturn(value);
  return this;
 }
}
