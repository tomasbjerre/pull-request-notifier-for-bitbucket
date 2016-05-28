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

 private EscalatedSecurityContext escalatedSecurityContext;

 private PrnfsPullRequestEventListener listener;

 private final ManualResource manualResouce;

 private final PluginSettings pluginSettings;

 private final PluginSettingsFactory pluginSettingsFactory;

 private List<AdminFormError> postResponses;

 private final ApplicationPropertiesService propertiesService;

 private PullRequest pullRequest;

 private PullRequestService pullRequestService;

 private final RepositoryService repositoryService;

 private HttpServletRequest request;

 private TransactionTemplate transactionTemplate;

 private final List<UrlInvoker> urlInvokers = newArrayList();

 private final UserKey userKey;

 private final UserManager userManager;

 private final UserProfile userProfile;

 private PrnfsTestBuilder() {
  fakeRandomCounter = 0L;
  fakeRandom(new FakeRandom());
  this.pluginSettings = new PluginSettingsImpl();
  this.userProfile = mock(UserProfile.class);
  this.userKey = new UserKey("asd");
  this.userManager = mock(UserManager.class);
  this.pluginSettingsFactory = mock(PluginSettingsFactory.class);
  this.repositoryService = mock(RepositoryService.class);
  this.propertiesService = mock(ApplicationPropertiesService.class);
  this.transactionTemplate = new TransactionTemplate() {
   @Override
   public <T> T execute(TransactionCallback<T> action) {
    return action.doInTransaction();
   }
  };
  when(this.pluginSettingsFactory.createGlobalSettings()).thenReturn(this.pluginSettings);
  SecurityService securityService = mock(SecurityService.class);
  this.escalatedSecurityContext = mock(EscalatedSecurityContext.class);
  when(securityService.withPermission(Matchers.any(Permission.class), Matchers.anyString())).thenReturn(
    this.escalatedSecurityContext);
  this.configResource = new ConfigResource(this.userManager, this.pluginSettingsFactory, this.transactionTemplate,
    securityService);
  this.pullRequestService = mock(PullRequestService.class);
  this.listener = new PrnfsPullRequestEventListener(this.pluginSettingsFactory, this.repositoryService,
    this.propertiesService, this.pullRequestService, new SyncExecutorService(), securityService);
  UserService userService = mock(UserService.class);
  withPullRequest(pullRequestEventBuilder().build().getPullRequest());
  this.manualResouce = new ManualResource(this.userManager, userService, this.pluginSettingsFactory,
    this.pullRequestService, this.listener, this.repositoryService, this.propertiesService, securityService);
 }

 public PrnfsTestBuilder delete(String id) throws Exception {
  this.configResource.delete(id, this.request);
  return this;
 }

 public PrnfsTestBuilder didNotSendHeader(int index, String name) {
  assertFalse(index + " " + name, toMap(this.urlInvokers.get(index).getHeaders()).containsKey(name));
  return this;
 }

 public PrnfsTestBuilder didNotSendHeaders() {
  for (UrlInvoker u : this.urlInvokers) {
   assertTrue(toMap(u.getHeaders()).isEmpty());
  }
  return this;
 }

 public void didNotSendPostContentAt(int i) {
  assertFalse(this.urlInvokers.get(i).shouldPostContent());
 }

 public void didSendPostContentAt(int i, String string) {
  assertTrue(this.urlInvokers.get(i).shouldPostContent());
  assertEquals(string, this.urlInvokers.get(i).getPostContent().get());
 }

 public RepositoryService getRepositoryService() {
  return this.repositoryService;
 }

 public PrnfsTestBuilder hasButtonsEnabled(final String... formIdentifiers) throws Exception {
  Integer repositoryId = 0;
  Long pullRequestId = 0L;
  List<PrnfsButton> enabledButtons = new Gson().fromJson(
    (String) this.manualResouce.get(this.request, repositoryId, pullRequestId).getEntity(),
    new TypeToken<List<PrnfsButton>>() {
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

 public PrnfsTestBuilder hasFieldValueAt(AdminFormValues.FIELDS field, String value, String id) throws Exception {
  for (final Map<String, String> fieldValue : getAdminFormFields().get(id)) {
   if (fieldValue.get(NAME).equals(field.name()) && fieldValue.get(VALUE).equals(value)) {
    return this;
   }
  }
  fail("Could not find " + field + " " + value + " at " + id);
  return this;
 }

 public PrnfsTestBuilder hasNoButtonsEnabled() throws Exception {
  return hasButtonsEnabled();
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
  for (final AdminFormError e : this.postResponses) {
   if (e.getField().equals(field.name())) {
    assertEquals(value, e.getValue());
    return;
   }
   logger.info(e.getField() + " " + e.getValue());
  }
  fail(field + " " + value + " not found");
 }

 public PrnfsTestBuilder invokedMethod(HTTP_METHOD method) {
  for (UrlInvoker u : this.urlInvokers) {
   if (method.equals(u.getMethod())) {
    return this;
   }
  }
  fail(method.name());
  return this;
 }

 public PrnfsTestBuilder invokedNoUrl() {
  assertEquals(0, this.urlInvokers.size());
  return this;
 }

 public PrnfsTestBuilder invokedOnlyUrl(String url) {
  assertEquals(1, this.urlInvokers.size());
  assertEquals(url, this.urlInvokers.get(0).getUrlParam());
  return this;
 }

 public PrnfsTestBuilder invokedUrl(int index, String url) {
  assertEquals(url, this.urlInvokers.get(index).getUrlParam());
  return this;
 }

 public PrnfsTestBuilder isConflicting() {
  return isConflicting(TRUE);
 }

 public PrnfsTestBuilder isLoggedInAsAdmin() {
  when(this.userProfile.getUserKey()).thenReturn(this.userKey);
  when(this.userManager.isSystemAdmin(Matchers.any(UserKey.class))).thenReturn(TRUE);
  when(this.userManager.getRemoteUser(Matchers.any(HttpServletRequest.class))).thenReturn(this.userProfile);
  return this;
 }

 public PrnfsTestBuilder isNotConflicting() {
  return isConflicting(FALSE);
 }

 public PrnfsTestBuilder isNotLoggedInAsAdmin() {
  when(this.userProfile.getUserKey()).thenReturn(null);
  return this;
 }

 public PrnfsTestBuilder store() throws Exception {
  this.postResponses = newArrayList();
  for (final AdminFormValues adminFormValues : this.adminFormValuesMap.values()) {
   final Optional<Object> postResponseOpt = fromNullable(this.configResource.post(adminFormValues, this.request)
     .getEntity());
   if (postResponseOpt.isPresent()) {
    this.postResponses.add((AdminFormError) postResponseOpt.get());
   }
  }
  return this;
 }

 public PrnfsTestBuilder trigger(PullRequestEvent event) {
  setInvoker(new Invoker() {
   @Override
   public void invoke(UrlInvoker urlInvoker) {
    PrnfsTestBuilder.this.urlInvokers.add(urlInvoker);
   }
  });
  this.listener.handleEvent(event);
  return this;
 }

 public PrnfsTestBuilder triggerButton(String formIdentifier) throws Exception {
  when(this.pullRequestService.getById(anyInt(), anyLong())).thenReturn(this.pullRequest);
  try {
   PrnfsSettings prnfsSettings = getPrnfsSettings(this.pluginSettings);
   when(this.escalatedSecurityContext.call(Matchers.any(Operation.class))).thenReturn(prnfsSettings);
  } catch (Throwable e) {
   propagate(e);
  }
  setInvoker(new Invoker() {
   @Override
   public void invoke(UrlInvoker urlInvoker) {
    PrnfsTestBuilder.this.urlInvokers.add(urlInvoker);
   }
  });
  Integer repositoryId = 0;
  Long pullRequestId = 0L;
  this.manualResouce.post(this.request, repositoryId, pullRequestId, formIdentifier);
  return this;
 }

 public PullRequestEventBuilder triggerPullRequestEventBuilder() {
  return pullRequestEventBuilder(this);
 }

 public PrnfsTestBuilder usedHeader(int index, String name, String value) {
  Map<String, Header> headerMap = toMap(this.urlInvokers.get(index).getHeaders());
  if (headerMap.containsKey(name)) {
   assertEquals(index + " " + name, value, getHeaderValue(headerMap.get(name)));
  } else {
   fail(Joiner.on(", ").join(headerMap.keySet()));
  }
  return this;
 }

 public PrnfsTestBuilder usedNoProxy(int index) {
  assertFalse(this.urlInvokers.get(index).shouldUseProxy());
  return this;
 }

 public PrnfsTestBuilder usedNoProxyAuthentication(int index) {
  assertFalse(this.urlInvokers.get(index).shouldAuthenticateProxy());
  return this;
 }

 public PrnfsTestBuilder usedNoProxyPassword(int index) {
  assertFalse(this.urlInvokers.get(index).getProxyPassword().isPresent());
  assertFalse(this.urlInvokers.get(index).shouldAuthenticateProxy());
  return this;
 }

 public PrnfsTestBuilder usedNoProxyUser(int index) {
  assertFalse(this.urlInvokers.get(index).getProxyUser().isPresent());
  assertFalse(this.urlInvokers.get(index).shouldAuthenticateProxy());
  return this;
 }

 public PrnfsTestBuilder usedProxyHost(int index, String host) {
  assertEquals(host, this.urlInvokers.get(index).getProxyHost().get());
  return this;
 }

 public PrnfsTestBuilder usedProxyPassword(int i, String password) {
  assertEquals(password, this.urlInvokers.get(i).getProxyPassword().get());
  assertTrue(this.urlInvokers.get(i).shouldAuthenticateProxy());
  return this;
 }

 public PrnfsTestBuilder usedProxyPort(int index, Integer port) {
  assertEquals(port, this.urlInvokers.get(index).getProxyPort());
  return this;
 }

 public PrnfsTestBuilder usedProxyUser(int i, String user) {
  assertEquals(user, this.urlInvokers.get(i).getProxyUser().get());
  assertTrue(this.urlInvokers.get(i).shouldAuthenticateProxy());
  return this;
 }

 public PrnfsTestBuilder withBaseUrl(String baseUrl) throws Exception {
  when(this.propertiesService.getBaseUrl()).thenReturn(new URI(baseUrl));
  return this;
 }

 public PrnfsTestBuilder withNotification(AdminFormValues adminFormValues) {
  final Optional<Map<String, String>> existing = tryFind(adminFormValues,
    predicate(AdminFormValues.FIELDS.FORM_IDENTIFIER.name()));
  if (existing.isPresent()) {
   this.adminFormValuesMap.put(existing.get().get(VALUE), adminFormValues);
  } else {
   this.adminFormValuesMap.put((this.adminFormValuesMapCounter++) + "", adminFormValues);
  }
  return this;
 }

 public PrnfsTestBuilder withPullRequest(PullRequest pullRequest) {
  this.pullRequest = pullRequest;
  return this;
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

 @SuppressWarnings("unchecked")
 private Map<String, AdminFormValues> getAdminFormFields() throws Exception {
  return uniqueIndex((List<AdminFormValues>) this.configResource.get(this.request).getEntity(),
    new Function<AdminFormValues, String>() {
     @Override
     public String apply(AdminFormValues input) {
      return find(input, predicate(AdminFormValues.FIELDS.FORM_IDENTIFIER.name())).get(VALUE);
     }
    });
 }

 private PrnfsTestBuilder isConflicting(Boolean conflicting) {
  PullRequestMergeability value = mock(PullRequestMergeability.class);
  when(value.isConflicted()).thenReturn(conflicting);
  when(this.pullRequestService.canMerge(anyInt(), anyInt())).thenReturn(value);
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
}
