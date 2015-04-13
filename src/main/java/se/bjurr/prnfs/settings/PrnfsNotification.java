package se.bjurr.prnfs.settings;

import static com.google.common.base.MoreObjects.firstNonNull;
import static com.google.common.base.Optional.fromNullable;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.emptyToNull;
import static com.google.common.base.Strings.nullToEmpty;
import static java.util.regex.Pattern.compile;

import java.net.URL;
import java.util.List;

import se.bjurr.prnfs.admin.AdminFormValues;
import se.bjurr.prnfs.listener.PrnfsPullRequestAction;

import com.google.common.base.Optional;

public class PrnfsNotification {
 private final String filterRegexp;
 private final String filterString;
 private final String password;
 private final List<PrnfsPullRequestAction> triggers;
 private final String url;
 private final String user;
 private final String method;
 private final String postContent;

 public PrnfsNotification(List<PrnfsPullRequestAction> triggers, String url, String user, String password,
   String filterString, String filterRegexp, String method, String postContent) throws ValidationException {
  this.password = emptyToNull(nullToEmpty(password).trim());
  this.postContent = emptyToNull(nullToEmpty(postContent).trim());
  this.method = firstNonNull(emptyToNull(nullToEmpty(method).trim()), "GET");
  if (nullToEmpty(url).trim().isEmpty()) {
   throw new ValidationException(AdminFormValues.FIELDS.url.name(), "URL not set!");
  }
  try {
   new URL(url);
  } catch (final Exception e) {
   throw new ValidationException(AdminFormValues.FIELDS.url.name(), "URL not valid!");
  }
  if (!nullToEmpty(filterRegexp).trim().isEmpty()) {
   try {
    compile(filterRegexp);
   } catch (final Exception e) {
    throw new ValidationException(AdminFormValues.FIELDS.filter_regexp.name(), "Filter regexp not valid! "
      + e.getMessage().replaceAll("\n", " "));
   }
   if (nullToEmpty(filterString).trim().isEmpty()) {
    throw new ValidationException(AdminFormValues.FIELDS.filter_string.name(),
      "Filter string not set, nothing to match regexp against!");
   }
  }
  this.url = url;
  this.user = emptyToNull(nullToEmpty(user).trim());
  this.triggers = checkNotNull(triggers);
  this.filterString = filterString;
  this.filterRegexp = filterRegexp;
 }

 public Optional<String> getFilterRegexp() {
  return fromNullable(filterRegexp);
 }

 public Optional<String> getFilterString() {
  return fromNullable(filterString);
 }

 public Optional<String> getPassword() {
  return fromNullable(password);
 }

 public List<PrnfsPullRequestAction> getTriggers() {
  return triggers;
 }

 public String getUrl() {
  return url;
 }

 public Optional<String> getUser() {
  return fromNullable(user);
 }

 public String getMethod() {
  return method;
 }

 public Optional<String> getPostContent() {
  return fromNullable(postContent);
 }
}
