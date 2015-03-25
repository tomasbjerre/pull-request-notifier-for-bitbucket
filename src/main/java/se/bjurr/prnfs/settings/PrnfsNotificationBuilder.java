package se.bjurr.prnfs.settings;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

import com.atlassian.stash.pull.PullRequestAction;

public class PrnfsNotificationBuilder {
 public static PrnfsNotificationBuilder prnfsNotificationBuilder() {
  return new PrnfsNotificationBuilder();
 }

 private String password;
 private final List<PullRequestAction> triggers = newArrayList();
 private String url;
 private String user;

 private PrnfsNotificationBuilder() {
 }

 public PrnfsNotification build() throws ValidationException {
  return new PrnfsNotification(triggers, password, url, user);
 }

 public PrnfsNotificationBuilder withPassword(String password) {
  this.password = checkNotNull(password);
  return this;
 }

 public PrnfsNotificationBuilder withTrigger(PullRequestAction trigger) {
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
