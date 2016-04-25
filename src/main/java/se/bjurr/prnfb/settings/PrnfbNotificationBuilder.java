package se.bjurr.prnfb.settings;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;
import static java.util.UUID.randomUUID;

import java.util.List;
import java.util.UUID;

import se.bjurr.prnfb.http.UrlInvoker.HTTP_METHOD;
import se.bjurr.prnfb.listener.PrnfbPullRequestAction;

import com.atlassian.bitbucket.pull.PullRequestState;

public class PrnfbNotificationBuilder {
 public static PrnfbNotificationBuilder prnfbNotificationBuilder() {
  return new PrnfbNotificationBuilder();
 }

 public static PrnfbNotificationBuilder prnfbNotificationBuilder(PrnfbNotification from) {
  PrnfbNotificationBuilder b = new PrnfbNotificationBuilder();

  b.uuid = from.getUuid();
  b.password = from.getPassword().orNull();
  b.triggers = from.getTriggers();
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
  b.proxyPort = from.getProxyPort();
  b.name = from.getName();
  b.injectionUrl = from.getInjectionUrl().orNull();
  b.injectionUrlRegexp = from.getInjectionUrlRegexp().orNull();
  b.triggerIfCanMerge = from.getTriggerIfCanMerge();
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
 private String proxyPassword;
 private Integer proxyPort;
 private String proxyServer;
 private String proxyUser;
 private TRIGGER_IF_MERGE triggerIfCanMerge;
 private List<PullRequestState> triggerIgnoreStateList = newArrayList();
 private List<PrnfbPullRequestAction> triggers = newArrayList();
 private String url;
 private String user;
 private UUID uuid;

 private PrnfbNotificationBuilder() {
  this.uuid = randomUUID();
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

 public String getProxyPassword() {
  return this.proxyPassword;
 }

 public Integer getProxyPort() {
  return this.proxyPort;
 }

 public String getProxyServer() {
  return this.proxyServer;
 }

 public String getProxyUser() {
  return this.proxyUser;
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

 public String getUrl() {
  return this.url;
 }

 public String getUser() {
  return this.user;
 }

 public UUID getUUID() {
  return this.uuid;
 }

 public PrnfbNotificationBuilder setHeaders(List<PrnfbHeader> headers) {
  this.headers = headers;
  return this;
 }

 public PrnfbNotificationBuilder setTriggerIgnoreState(List<PullRequestState> triggerIgnoreStateList) {
  this.triggerIgnoreStateList = triggerIgnoreStateList;
  return this;
 }

 public PrnfbNotificationBuilder setTriggers(List<PrnfbPullRequestAction> triggers) {
  this.triggers = triggers;
  return this;
 }

 public PrnfbNotificationBuilder withFilterRegexp(String filterRegexp) {
  this.filterRegexp = checkNotNull(filterRegexp);
  return this;
 }

 public PrnfbNotificationBuilder withFilterString(String filterString) {
  this.filterString = checkNotNull(filterString);
  return this;
 }

 public PrnfbNotificationBuilder withHeader(String name, String value) {
  this.headers.add(new PrnfbHeader(checkNotNull(name), checkNotNull(value)));
  return this;
 }

 public PrnfbNotificationBuilder withInjectionUrl(String injectionUrl) {
  this.injectionUrl = checkNotNull(injectionUrl);
  return this;
 }

 public PrnfbNotificationBuilder withInjectionUrlRegexp(String injectionUrlRegexp) {
  this.injectionUrlRegexp = checkNotNull(injectionUrlRegexp);
  return this;
 }

 public PrnfbNotificationBuilder withMethod(HTTP_METHOD method) {
  this.method = checkNotNull(method);
  return this;
 }

 public PrnfbNotificationBuilder withName(String name) {
  this.name = name;
  return this;
 }

 public PrnfbNotificationBuilder withPassword(String password) {
  this.password = checkNotNull(password);
  return this;
 }

 public PrnfbNotificationBuilder withPostContent(String postContent) {
  this.postContent = checkNotNull(postContent);
  return this;
 }

 public PrnfbNotificationBuilder withProxyPassword(String s) {
  this.proxyPassword = checkNotNull(s);
  return this;
 }

 public PrnfbNotificationBuilder withProxyPort(Integer s) {
  this.proxyPort = checkNotNull(s);
  return this;
 }

 public PrnfbNotificationBuilder withProxyServer(String s) {
  this.proxyServer = checkNotNull(s);
  return this;
 }

 public PrnfbNotificationBuilder withProxyUser(String s) {
  this.proxyUser = checkNotNull(s);
  return this;
 }

 public PrnfbNotificationBuilder withTrigger(PrnfbPullRequestAction trigger) {
  this.triggers.add(checkNotNull(trigger));
  return this;
 }

 public PrnfbNotificationBuilder withTriggerIfCanMerge(TRIGGER_IF_MERGE triggerIfCanMerge) {
  this.triggerIfCanMerge = triggerIfCanMerge;
  return this;
 }

 public PrnfbNotificationBuilder withTriggerIgnoreState(PullRequestState triggerIgnoreState) {
  this.triggerIgnoreStateList.add(checkNotNull(triggerIgnoreState));
  return this;
 }

 public PrnfbNotificationBuilder withUrl(String url) {
  this.url = checkNotNull(url);
  return this;
 }

 public PrnfbNotificationBuilder withUser(String user) {
  this.user = checkNotNull(user);
  return this;
 }

 public PrnfbNotificationBuilder withUuid(UUID uuid) {
  this.uuid = uuid;
  return this;
 }
}
