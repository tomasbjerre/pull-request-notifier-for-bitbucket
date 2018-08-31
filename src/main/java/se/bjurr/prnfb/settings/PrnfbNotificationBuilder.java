package se.bjurr.prnfb.settings;

import static com.google.common.base.MoreObjects.firstNonNull;
import static com.google.common.base.Strings.emptyToNull;
import static com.google.common.collect.Lists.newArrayList;
import static java.util.UUID.randomUUID;
import static se.bjurr.prnfb.http.UrlInvoker.HTTP_METHOD.GET;

import com.atlassian.bitbucket.pull.PullRequestState;
import java.util.List;
import java.util.UUID;
import se.bjurr.prnfb.http.UrlInvoker.HTTP_METHOD;
import se.bjurr.prnfb.listener.PrnfbPullRequestAction;
import se.bjurr.prnfb.service.PrnfbRenderer.ENCODE_FOR;

public class PrnfbNotificationBuilder {
  public static PrnfbNotificationBuilder prnfbNotificationBuilder() {
    return new PrnfbNotificationBuilder();
  }

  public PrnfbNotificationBuilder(
      final String filterRegexp,
      final String filterString,
      final List<PrnfbHeader> headers,
      final String injectionUrl,
      final String injectionUrlRegexp,
      final HTTP_METHOD method,
      final String name,
      final String password,
      final String postContent,
      final String projectKey,
      final String proxyPassword,
      final Integer proxyPort,
      final String proxyServer,
      final String proxyUser,
      final String repositorySlug,
      final TRIGGER_IF_MERGE triggerIfCanMerge,
      final List<PullRequestState> triggerIgnoreStateList,
      final List<PrnfbPullRequestAction> triggers,
      final boolean updatePullRequestRefs,
      final String url,
      final String user,
      final UUID uuid,
      final ENCODE_FOR postContentEncoding,
      final String proxySchema,
      final String httpVersion) {
    this.filterRegexp = filterRegexp;
    this.filterString = filterString;
    this.headers = headers;
    this.injectionUrl = injectionUrl;
    this.injectionUrlRegexp = injectionUrlRegexp;
    this.method = method;
    this.name = name;
    this.password = password;
    this.postContent = postContent;
    this.projectKey = projectKey;
    this.proxyPassword = proxyPassword;
    this.proxyPort = proxyPort;
    this.proxyServer = proxyServer;
    this.proxyUser = proxyUser;
    this.repositorySlug = repositorySlug;
    this.triggerIfCanMerge = triggerIfCanMerge;
    this.triggerIgnoreStateList = triggerIgnoreStateList;
    this.triggers = triggers;
    this.updatePullRequestRefs = updatePullRequestRefs;
    this.url = url;
    this.user = user;
    this.uuid = uuid;
    this.postContentEncoding = postContentEncoding;
    this.proxySchema = proxySchema;
    this.httpVersion = httpVersion;
  }

  public static PrnfbNotificationBuilder prnfbNotificationBuilder(final PrnfbNotification from) {
    final PrnfbNotificationBuilder b = new PrnfbNotificationBuilder();

    b.uuid = from.getUuid();
    b.password = from.getPassword().orNull();
    b.triggers = from.getTriggers();
    b.updatePullRequestRefs = from.isUpdatePullRequestRefs();
    b.url = from.getUrl();
    b.user = from.getUser().orNull();
    b.filterRegexp = from.getFilterRegexp().orNull();
    b.filterString = from.getFilterString().orNull();
    b.method = from.getMethod();
    b.postContent = from.getPostContent().orNull();
    b.headers = from.getHeaders();
    b.triggerIgnoreStateList = from.getTriggerIgnoreStateList();
    b.proxyUser = from.getProxyUser().orNull();
    b.proxyPassword = from.getProxyPassword().orNull();
    b.proxyServer = from.getProxyServer().orNull();
    b.proxySchema = from.getProxySchema().orNull();
    b.proxyPort = from.getProxyPort();
    b.projectKey = from.getProjectKey().orNull();
    b.repositorySlug = from.getRepositorySlug().orNull();
    b.name = from.getName();
    b.injectionUrl = from.getInjectionUrl().orNull();
    b.injectionUrlRegexp = from.getInjectionUrlRegexp().orNull();
    b.triggerIfCanMerge = from.getTriggerIfCanMerge();
    b.postContentEncoding = from.getPostContentEncoding();
    b.httpVersion = from.getHttpVersion();
    return b;
  }

  private String filterRegexp;
  private String filterString;
  private List<PrnfbHeader> headers = newArrayList();
  private String injectionUrl;
  private String injectionUrlRegexp;
  private HTTP_METHOD method;
  private String name;
  private String password;
  private String postContent;
  private String projectKey;
  private String proxyPassword;
  private Integer proxyPort;
  private String proxyServer;
  private String proxyUser;
  private String repositorySlug;
  private TRIGGER_IF_MERGE triggerIfCanMerge;
  private List<PullRequestState> triggerIgnoreStateList = newArrayList();
  private List<PrnfbPullRequestAction> triggers = newArrayList();
  private boolean updatePullRequestRefs;
  private String url;
  private String user;
  private UUID uuid;
  private ENCODE_FOR postContentEncoding;
  private String proxySchema;
  private String httpVersion;

  private PrnfbNotificationBuilder() {
    this.uuid = randomUUID();
  }

  public ENCODE_FOR getPostContentEncoding() {
    return postContentEncoding;
  }

  public PrnfbNotification build() throws ValidationException {
    return new PrnfbNotification(this);
  }

  public String getFilterRegexp() {
    return this.filterRegexp;
  }

  public String getFilterString() {
    return this.filterString;
  }

  public List<PrnfbHeader> getHeaders() {
    return this.headers;
  }

  public String getInjectionUrl() {
    return this.injectionUrl;
  }

  public String getInjectionUrlRegexp() {
    return this.injectionUrlRegexp;
  }

  public HTTP_METHOD getMethod() {
    return this.method;
  }

  public String getName() {
    return this.name;
  }

  public String getPassword() {
    return this.password;
  }

  public String getPostContent() {
    return this.postContent;
  }

  public String getProjectKey() {
    return this.projectKey;
  }

  public String getProxyPassword() {
    return this.proxyPassword;
  }

  public Integer getProxyPort() {
    return this.proxyPort;
  }

  public String getProxyServer() {
    return this.proxyServer;
  }

  public String getProxySchema() {
    return proxySchema;
  }

  public String getProxyUser() {
    return this.proxyUser;
  }

  public String getRepositorySlug() {
    return this.repositorySlug;
  }

  public TRIGGER_IF_MERGE getTriggerIfCanMerge() {
    return this.triggerIfCanMerge;
  }

  public List<PullRequestState> getTriggerIgnoreStateList() {
    return this.triggerIgnoreStateList;
  }

  public List<PrnfbPullRequestAction> getTriggers() {
    return this.triggers;
  }

  public boolean isUpdatePullRequestRefs() {
    return this.updatePullRequestRefs;
  }

  public String getUrl() {
    return this.url;
  }

  public String getUser() {
    return this.user;
  }

  public UUID getUuid() {
    return this.uuid;
  }

  public UUID getUUID() {
    return this.uuid;
  }

  public PrnfbNotificationBuilder setHeaders(final List<PrnfbHeader> headers) {
    this.headers = headers;
    return this;
  }

  public PrnfbNotificationBuilder setTriggerIgnoreState(
      final List<PullRequestState> triggerIgnoreStateList) {
    this.triggerIgnoreStateList = triggerIgnoreStateList;
    return this;
  }

  public PrnfbNotificationBuilder setTriggers(final List<PrnfbPullRequestAction> triggers) {
    this.triggers = triggers;
    return this;
  }

  public PrnfbNotificationBuilder setUpdatePullRequestRefs(final boolean updatePullRequestRefs) {
    this.updatePullRequestRefs = updatePullRequestRefs;
    return this;
  }

  public PrnfbNotificationBuilder withFilterRegexp(final String filterRegexp) {
    this.filterRegexp = emptyToNull(filterRegexp);
    return this;
  }

  public PrnfbNotificationBuilder withFilterString(final String filterString) {
    this.filterString = emptyToNull(filterString);
    return this;
  }

  public PrnfbNotificationBuilder withHeader(final String name, final String value) {
    this.headers.add(new PrnfbHeader(name, value));
    return this;
  }

  public PrnfbNotificationBuilder withInjectionUrl(final String injectionUrl) {
    this.injectionUrl = emptyToNull(injectionUrl);
    return this;
  }

  public PrnfbNotificationBuilder withInjectionUrlRegexp(final String injectionUrlRegexp) {
    this.injectionUrlRegexp = emptyToNull(injectionUrlRegexp);
    return this;
  }

  public PrnfbNotificationBuilder withMethod(final HTTP_METHOD method) {
    this.method = firstNonNull(method, GET);
    return this;
  }

  public PrnfbNotificationBuilder withName(final String name) {
    this.name = name;
    return this;
  }

  public PrnfbNotificationBuilder withPassword(final String password) {
    this.password = emptyToNull(password);
    return this;
  }

  public PrnfbNotificationBuilder withPostContent(final String postContent) {
    this.postContent = emptyToNull(postContent);
    return this;
  }

  public PrnfbNotificationBuilder withProjectKey(final String projectKey) {
    this.projectKey = projectKey;
    return this;
  }

  public PrnfbNotificationBuilder withProxyPassword(final String s) {
    this.proxyPassword = emptyToNull(s);
    return this;
  }

  public PrnfbNotificationBuilder withProxyPort(final Integer s) {
    this.proxyPort = s;
    return this;
  }

  public PrnfbNotificationBuilder withProxyServer(final String s) {
    this.proxyServer = emptyToNull(s);
    return this;
  }

  public PrnfbNotificationBuilder withProxyUser(final String s) {
    this.proxyUser = emptyToNull(s);
    return this;
  }

  public PrnfbNotificationBuilder withRepositorySlug(final String repositorySlug) {
    this.repositorySlug = repositorySlug;
    return this;
  }

  public PrnfbNotificationBuilder withTrigger(final PrnfbPullRequestAction trigger) {
    this.triggers.add(trigger);
    return this;
  }

  public PrnfbNotificationBuilder withUpdatePullRequestRefs(final boolean updatePullRequestRefs) {
    this.updatePullRequestRefs = updatePullRequestRefs;
    return this;
  }

  public PrnfbNotificationBuilder withTriggerIfCanMerge(final TRIGGER_IF_MERGE triggerIfCanMerge) {
    this.triggerIfCanMerge = triggerIfCanMerge;
    return this;
  }

  public PrnfbNotificationBuilder withTriggerIgnoreState(
      final PullRequestState triggerIgnoreState) {
    this.triggerIgnoreStateList.add(triggerIgnoreState);
    return this;
  }

  public PrnfbNotificationBuilder withUrl(final String url) {
    this.url = url;
    return this;
  }

  public PrnfbNotificationBuilder withUser(final String user) {
    this.user = emptyToNull(user);
    return this;
  }

  public PrnfbNotificationBuilder withPostContentEncoding(final ENCODE_FOR postContentEncoding) {
    this.postContentEncoding = postContentEncoding;
    return this;
  }

  public PrnfbNotificationBuilder withUuid(final UUID uuid) {
    this.uuid = uuid;
    return this;
  }

  public PrnfbNotificationBuilder withProxySchema(final String proxySchema) {
    this.proxySchema = proxySchema;
    return this;
  }

  public String getHttpVersion() {
    return httpVersion;
  }

  public PrnfbNotificationBuilder withHttpVersion(final String httpVersion) {
    this.httpVersion = httpVersion;
    return this;
  }
}
