package se.bjurr.prnfs.settings;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

import se.bjurr.prnfs.listener.PrnfsPullRequestAction;

public class PrnfsNotificationBuilder {
 public static PrnfsNotificationBuilder prnfsNotificationBuilder() {
  return new PrnfsNotificationBuilder();
 }

 private String password;
 private final List<PrnfsPullRequestAction> triggers = newArrayList();
 private String url;
 private String user;
 private String filterRegexp;
 private String filterString;

 private PrnfsNotificationBuilder() {
 }

 public PrnfsNotification build() throws ValidationException {
  return new PrnfsNotification(triggers, url, user, password, filterString, filterRegexp);
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
}
