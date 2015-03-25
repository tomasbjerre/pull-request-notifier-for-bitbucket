package se.bjurr.prnfs.settings;

import static com.google.common.base.Optional.fromNullable;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.nullToEmpty;

import java.net.URL;
import java.util.List;

import com.atlassian.stash.pull.PullRequestAction;
import com.google.common.base.Optional;

public class PrnfsNotification {
 private final String password;
 private final List<PullRequestAction> triggers;
 private final String url;
 private final String user;

 public PrnfsNotification(List<PullRequestAction> triggers, String password, String url, String user)
   throws ValidationException {
  this.password = password;
  if (nullToEmpty(url).isEmpty()) {
   throw new ValidationException("url", "URL not set!");
  }
  try {
   new URL(url);
  } catch (Exception e) {
   throw new ValidationException("url", "URL not valid!");
  }
  this.url = url;
  this.user = user;
  this.triggers = checkNotNull(triggers);
 }

 public Optional<String> getPassword() {
  return fromNullable(password);
 }

 public List<PullRequestAction> getTriggers() {
  return triggers;
 }

 public String getUrl() {
  return url;
 }

 public Optional<String> getUser() {
  return fromNullable(user);
 }
}
