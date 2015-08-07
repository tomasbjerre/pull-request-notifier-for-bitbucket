package se.bjurr.prnfs.settings;

import static com.google.common.base.MoreObjects.firstNonNull;
import static com.google.common.base.Optional.fromNullable;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.emptyToNull;
import static com.google.common.base.Strings.nullToEmpty;
import static com.google.common.collect.Iterables.tryFind;
import static java.util.regex.Pattern.compile;
import static se.bjurr.prnfs.admin.AdminFormValues.DEFAULT_NAME;
import static se.bjurr.prnfs.admin.AdminFormValues.VALUE;
import static se.bjurr.prnfs.settings.PrnfsPredicates.predicate;

import java.net.URL;
import java.util.List;
import java.util.Map;

import se.bjurr.prnfs.admin.AdminFormValues;
import se.bjurr.prnfs.admin.AdminFormValues.FORM_TYPE;
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
 private final List<Header> headers;
 private final String proxyUser;
 private final String proxyPassword;
 private final String proxyServer;
 private final Integer proxyPort;
 private final String name;

 public PrnfsNotification(List<PrnfsPullRequestAction> triggers, String url, String user, String password,
   String filterString, String filterRegexp, String method, String postContent, List<Header> headers, String proxyUser,
   String proxyPassword, String proxyServer, String proxyPort, String name) throws ValidationException {
  this.proxyUser = emptyToNull(nullToEmpty(proxyUser).trim());
  this.proxyPassword = emptyToNull(nullToEmpty(proxyPassword).trim());
  this.proxyServer = emptyToNull(nullToEmpty(proxyServer).trim());
  this.proxyPort = Integer.valueOf(firstNonNull(emptyToNull(nullToEmpty(proxyPort).trim()), "-1"));
  this.headers = checkNotNull(headers);
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
  this.password = emptyToNull(nullToEmpty(password).trim());
  this.triggers = checkNotNull(triggers);
  this.filterString = filterString;
  this.filterRegexp = filterRegexp;
  this.name = firstNonNull(emptyToNull(nullToEmpty(name).trim()), DEFAULT_NAME);
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

 public Optional<String> getProxyPassword() {
  return fromNullable(proxyPassword);
 }

 public Integer getProxyPort() {
  return proxyPort;
 }

 public Optional<String> getProxyServer() {
  return fromNullable(proxyServer);
 }

 public Optional<String> getProxyUser() {
  return fromNullable(proxyUser);
 }

 public String getName() {
  return name;
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

 public List<Header> getHeaders() {
  return headers;
 }

 public static boolean isOfType(AdminFormValues config, FORM_TYPE formType) {
  Optional<Map<String, String>> formTypeOpt = tryFind(config, predicate(AdminFormValues.FIELDS.FORM_TYPE.name()));
  return !formTypeOpt.isPresent() && formType.name().equals(FORM_TYPE.TRIGGER_CONFIG_FORM.name())
    || formTypeOpt.get().get(VALUE).equals(AdminFormValues.FORM_TYPE.TRIGGER_CONFIG_FORM.name());
 }
}
