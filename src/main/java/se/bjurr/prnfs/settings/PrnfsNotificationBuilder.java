package se.bjurr.prnfs.settings;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

import se.bjurr.prnfs.admin.AdminFormValues.INEJCTION_TYPE;
import se.bjurr.prnfs.listener.PrnfsPullRequestAction;

public class PrnfsNotificationBuilder {
 public static final String YES = "YES";
 public static final String NO = "NO";

 public static PrnfsNotificationBuilder prnfsNotificationBuilder() {
  return new PrnfsNotificationBuilder();
 }

 private String password;
 private final List<PrnfsPullRequestAction> triggers = newArrayList();
 private String url;
 private String user;
 private String filterRegexp;
 private String filterString;
 private String method;
 private String postContent;
 private final List<Header> headers = newArrayList();
 private String proxyUser;
 private String proxyPassword;
 private String proxyServer;
 private String proxyPort;
 private String name;
 private String injectionUrl;
 private String injectionUrlJsonPath;
 private String injectionUrlXPath;
 private INEJCTION_TYPE injectionUrlType;

 private PrnfsNotificationBuilder() {
 }

 public PrnfsNotification build() throws ValidationException {
  return new PrnfsNotification(triggers, url, user, password, filterString, filterRegexp, method, postContent, headers,
    proxyUser, proxyPassword, proxyServer, proxyPort, name, injectionUrl, injectionUrlJsonPath, injectionUrlXPath,
    injectionUrlType);
 }

 public PrnfsNotificationBuilder withInjectionUrl(String injectionUrl) {
  this.injectionUrl = checkNotNull(injectionUrl);
  return this;
 }

 public PrnfsNotificationBuilder withInjectionUrlJsonPath(String injectionUrlJsonPath) {
  this.injectionUrlJsonPath = checkNotNull(injectionUrlJsonPath);
  return this;
 }

 public PrnfsNotificationBuilder withInjectionUrlXPath(String injectionUrlXPath) {
  this.injectionUrlXPath = checkNotNull(injectionUrlXPath);
  return this;
 }

 public PrnfsNotificationBuilder withInjectionUrlType(INEJCTION_TYPE injectionUrlType) {
  this.injectionUrlType = injectionUrlType;
  return this;
 }

 public PrnfsNotificationBuilder withPassword(String password) {
  this.password = checkNotNull(password);
  return this;
 }

 public PrnfsNotificationBuilder withFilterRegexp(String filterRegexp) {
  this.filterRegexp = checkNotNull(filterRegexp);
  return this;
 }

 public PrnfsNotificationBuilder withFilterString(String filterString) {
  this.filterString = checkNotNull(filterString);
  return this;
 }

 public PrnfsNotificationBuilder withTrigger(PrnfsPullRequestAction trigger) {
  this.triggers.add(checkNotNull(trigger));
  return this;
 }

 public PrnfsNotificationBuilder withUrl(String url) {
  this.url = checkNotNull(url);
  return this;
 }

 public PrnfsNotificationBuilder withUser(String user) {
  this.user = checkNotNull(user);
  return this;
 }

 public PrnfsNotificationBuilder withMethod(String method) {
  this.method = checkNotNull(method);
  return this;
 }

 public PrnfsNotificationBuilder withPostContent(String postContent) {
  this.postContent = checkNotNull(postContent);
  return this;
 }

 public PrnfsNotificationBuilder withHeader(String name, String value) {
  headers.add(new Header(checkNotNull(name), checkNotNull(value)));
  return this;
 }

 public PrnfsNotificationBuilder withProxyServer(String s) {
  this.proxyServer = checkNotNull(s);
  return this;
 }

 public PrnfsNotificationBuilder withProxyPort(String s) {
  this.proxyPort = checkNotNull(s);
  return this;
 }

 public PrnfsNotificationBuilder withProxyUser(String s) {
  this.proxyUser = checkNotNull(s);
  return this;
 }

 public PrnfsNotificationBuilder withProxyPassword(String s) {
  this.proxyPassword = checkNotNull(s);
  return this;
 }

 public PrnfsNotificationBuilder withName(String name) {
  this.name = name;
  return this;
 }
}
