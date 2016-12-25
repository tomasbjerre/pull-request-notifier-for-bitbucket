package se.bjurr.prnfb.settings.legacy;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

import se.bjurr.prnfb.listener.PrnfbPullRequestAction;

import com.atlassian.bitbucket.pull.PullRequestState;

@Deprecated
public class PrnfbNotificationBuilder {
  public static PrnfbNotificationBuilder prnfbNotificationBuilder() {
    return new PrnfbNotificationBuilder();
  }

  private String filterRegexp;
  private String filterString;
  private final List<Header> headers = newArrayList();
  private String injectionUrl;
  private String injectionUrlRegexp;
  private String method;
  private String name;
  private String password;
  private String postContent;
  private String proxyPassword;
  private String proxyPort;
  private String proxyServer;
  private String proxyUser;
  private boolean shouldAcceptAnyCertificate;
  private String triggerIfCanMerge;
  private final List<PullRequestState> triggerIgnoreStateList = newArrayList();
  private final List<PrnfbPullRequestAction> triggers = newArrayList();
  private String url;
  private String user;

  private PrnfbNotificationBuilder() {}

  public PrnfbNotification build() throws ValidationException {
    return new PrnfbNotification(this);
  }

  public String getFilterRegexp() {
    return this.filterRegexp;
  }

  public String getFilterString() {
    return this.filterString;
  }

  public List<Header> getHeaders() {
    return this.headers;
  }

  public String getInjectionUrl() {
    return this.injectionUrl;
  }

  public String getInjectionUrlRegexp() {
    return this.injectionUrlRegexp;
  }

  public String getMethod() {
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

  public String getProxyPort() {
    return this.proxyPort;
  }

  public String getProxyServer() {
    return this.proxyServer;
  }

  public String getProxyUser() {
    return this.proxyUser;
  }

  public String getTriggerIfCanMerge() {
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

  public void setShouldAcceptAnyCertificate(boolean shouldAcceptAnyCertificate) {
    this.shouldAcceptAnyCertificate = shouldAcceptAnyCertificate;
  }

  public boolean shouldAcceptAnyCertificate() {
    return this.shouldAcceptAnyCertificate;
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
    this.headers.add(new Header(checkNotNull(name), checkNotNull(value)));
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

  public PrnfbNotificationBuilder withMethod(String method) {
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

  public PrnfbNotificationBuilder withProxyPort(String s) {
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

  public PrnfbNotificationBuilder withTriggerIfCanMerge(String triggerIfCanMerge) {
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
}
