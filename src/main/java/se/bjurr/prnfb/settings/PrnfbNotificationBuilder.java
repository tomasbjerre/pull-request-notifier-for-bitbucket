package se.bjurr.prnfb.settings;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

import se.bjurr.prnfb.listener.PrnfbPullRequestAction;

import com.atlassian.bitbucket.pull.PullRequestState;

public class PrnfbNotificationBuilder {
 public static PrnfbNotificationBuilder prnfbNotificationBuilder() {
  return new PrnfbNotificationBuilder();
 }

 private String password;
 private final List<PrnfbPullRequestAction> triggers = newArrayList();
 private String url;
 private String user;
 private String filterRegexp;
 private String filterString;
 private String method;
 private String postContent;
 private final List<Header> headers = newArrayList();
 private final List<PullRequestState> triggerIgnoreStateList = newArrayList();
 private String proxyUser;
 private String proxyPassword;
 private String proxyServer;
 private String proxyPort;
 private String name;
 private String injectionUrl;
 private String injectionUrlRegexp;
 private String triggerIfCanMerge;
 private boolean shouldAcceptAnyCertificate;

 private PrnfbNotificationBuilder() {
 }

 public PrnfbNotification build() throws ValidationException {
  return new PrnfbNotification(this);
 }

 public void setShouldAcceptAnyCertificate(boolean shouldAcceptAnyCertificate) {
  this.shouldAcceptAnyCertificate = shouldAcceptAnyCertificate;
 }

 public PrnfbNotificationBuilder withInjectionUrl(String injectionUrl) {
  this.injectionUrl = checkNotNull(injectionUrl);
  return this;
 }

 public PrnfbNotificationBuilder withInjectionUrlRegexp(String injectionUrlRegexp) {
  this.injectionUrlRegexp = checkNotNull(injectionUrlRegexp);
  return this;
 }

 public PrnfbNotificationBuilder withPassword(String password) {
  this.password = checkNotNull(password);
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

 public PrnfbNotificationBuilder withTrigger(PrnfbPullRequestAction trigger) {
  this.triggers.add(checkNotNull(trigger));
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

 public PrnfbNotificationBuilder withMethod(String method) {
  this.method = checkNotNull(method);
  return this;
 }

 public PrnfbNotificationBuilder withPostContent(String postContent) {
  this.postContent = checkNotNull(postContent);
  return this;
 }

 public PrnfbNotificationBuilder withHeader(String name, String value) {
  headers.add(new Header(checkNotNull(name), checkNotNull(value)));
  return this;
 }

 public PrnfbNotificationBuilder withProxyServer(String s) {
  this.proxyServer = checkNotNull(s);
  return this;
 }

 public PrnfbNotificationBuilder withProxyPort(String s) {
  this.proxyPort = checkNotNull(s);
  return this;
 }

 public List<PullRequestState> getTriggerIgnoreStateList() {
  return triggerIgnoreStateList;
 }

 public PrnfbNotificationBuilder withProxyUser(String s) {
  this.proxyUser = checkNotNull(s);
  return this;
 }

 public PrnfbNotificationBuilder withProxyPassword(String s) {
  this.proxyPassword = checkNotNull(s);
  return this;
 }

 public PrnfbNotificationBuilder withName(String name) {
  this.name = name;
  return this;
 }

 public PrnfbNotificationBuilder withTriggerIfCanMerge(String triggerIfCanMerge) {
  this.triggerIfCanMerge = triggerIfCanMerge;
  return this;
 }

 public String getFilterRegexp() {
  return filterRegexp;
 }

 public String getFilterString() {
  return filterString;
 }

 public List<Header> getHeaders() {
  return headers;
 }

 public String getInjectionUrl() {
  return injectionUrl;
 }

 public String getInjectionUrlRegexp() {
  return injectionUrlRegexp;
 }

 public String getMethod() {
  return method;
 }

 public String getName() {
  return name;
 }

 public String getPassword() {
  return password;
 }

 public String getPostContent() {
  return postContent;
 }

 public String getProxyPassword() {
  return proxyPassword;
 }

 public String getProxyPort() {
  return proxyPort;
 }

 public String getProxyServer() {
  return proxyServer;
 }

 public String getProxyUser() {
  return proxyUser;
 }

 public String getTriggerIfCanMerge() {
  return triggerIfCanMerge;
 }

 public List<PrnfbPullRequestAction> getTriggers() {
  return triggers;
 }

 public String getUrl() {
  return url;
 }

 public String getUser() {
  return user;
 }

 public PrnfbNotificationBuilder withTriggerIgnoreState(PullRequestState triggerIgnoreState) {
  this.triggerIgnoreStateList.add(checkNotNull(triggerIgnoreState));
  return this;
 }

 public boolean shouldAcceptAnyCertificate() {
  return shouldAcceptAnyCertificate;
 }
}
