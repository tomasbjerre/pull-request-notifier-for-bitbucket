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
import static se.bjurr.prnfs.admin.AdminFormValues.FIELDS.filter_regexp;
import static se.bjurr.prnfs.admin.AdminFormValues.FIELDS.filter_string;
import static se.bjurr.prnfs.admin.AdminFormValues.FORM_TYPE.TRIGGER_CONFIG_FORM;
import static se.bjurr.prnfs.admin.AdminFormValues.TRIGGER_IF_MERGE.ALWAYS;
import static se.bjurr.prnfs.listener.UrlInvoker.HTTP_METHOD.GET;
import static se.bjurr.prnfs.settings.PrnfsPredicates.predicate;

import java.net.URL;
import java.util.List;
import java.util.Map;

import se.bjurr.prnfs.admin.AdminFormValues;
import se.bjurr.prnfs.admin.AdminFormValues.FIELDS;
import se.bjurr.prnfs.admin.AdminFormValues.FORM_TYPE;
import se.bjurr.prnfs.admin.AdminFormValues.TRIGGER_IF_MERGE;
import se.bjurr.prnfs.listener.PrnfsPullRequestAction;
import se.bjurr.prnfs.listener.UrlInvoker.HTTP_METHOD;

import com.atlassian.stash.pull.PullRequestState;
import com.google.common.base.Optional;

public class PrnfsNotification {
 private final String filterRegexp;
 private final String filterString;
 private final String password;
 private final List<PrnfsPullRequestAction> triggers;
 private final String url;
 private final String user;
 private final HTTP_METHOD method;
 private final String postContent;
 private final List<Header> headers;
 private final String proxyUser;
 private final String proxyPassword;
 private final String proxyServer;
 private final Integer proxyPort;
 private final String name;
 private final String injectionUrl;
 private final String injectionUrlRegexp;
 private final TRIGGER_IF_MERGE triggerIfCanMerge;
 private final List<PullRequestState> triggerIgnoreStateList;

 public PrnfsNotification(PrnfsNotificationBuilder builder) throws ValidationException {
  this.proxyUser = emptyToNull(nullToEmpty(builder.getProxyUser()).trim());
  this.proxyPassword = emptyToNull(nullToEmpty(builder.getProxyPassword()).trim());
  this.proxyServer = emptyToNull(nullToEmpty(builder.getProxyServer()).trim());
  this.proxyPort = Integer.valueOf(firstNonNull(emptyToNull(nullToEmpty(builder.getProxyPort()).trim()), "-1"));
  this.headers = checkNotNull(builder.getHeaders());
  this.postContent = emptyToNull(nullToEmpty(builder.getPostContent()).trim());
  this.method = HTTP_METHOD.valueOf(firstNonNull(emptyToNull(nullToEmpty(builder.getMethod()).trim()), GET.name()));
  this.triggerIfCanMerge = TRIGGER_IF_MERGE.valueOf(firstNonNull(
    emptyToNull(nullToEmpty(builder.getTriggerIfCanMerge()).trim()), ALWAYS.name()));
  if (nullToEmpty(builder.getUrl()).trim().isEmpty()) {
   throw new ValidationException(FIELDS.url.name(), "URL not set!");
  }
  try {
   new URL(builder.getUrl());
  } catch (final Exception e) {
   throw new ValidationException(FIELDS.url.name(), "URL not valid!");
  }
  if (!nullToEmpty(builder.getFilterRegexp()).trim().isEmpty()) {
   try {
    compile(builder.getFilterRegexp());
   } catch (final Exception e) {
    throw new ValidationException(filter_regexp.name(), "Filter regexp not valid! "
      + e.getMessage().replaceAll("\n", " "));
   }
   if (nullToEmpty(builder.getFilterString()).trim().isEmpty()) {
    throw new ValidationException(filter_string.name(), "Filter string not set, nothing to match regexp against!");
   }
  }
  this.url = builder.getUrl();
  this.user = emptyToNull(nullToEmpty(builder.getUser()).trim());
  this.password = emptyToNull(nullToEmpty(builder.getPassword()).trim());
  this.triggers = checkNotNull(builder.getTriggers());
  this.filterString = builder.getFilterString();
  this.filterRegexp = builder.getFilterRegexp();
  this.name = firstNonNull(emptyToNull(nullToEmpty(builder.getName()).trim()), DEFAULT_NAME);
  this.injectionUrl = emptyToNull(nullToEmpty(builder.getInjectionUrl()).trim());
  this.injectionUrlRegexp = emptyToNull(nullToEmpty(builder.getInjectionUrlRegexp()).trim());
  this.triggerIgnoreStateList = builder.getTriggerIgnoreStateList();
 }

 public List<PullRequestState> getTriggerIgnoreStateList() {
  return triggerIgnoreStateList;
 }

 public TRIGGER_IF_MERGE getTriggerIfCanMerge() {
  return triggerIfCanMerge;
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

 public HTTP_METHOD getMethod() {
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
  return !formTypeOpt.isPresent() && formType.name().equals(TRIGGER_CONFIG_FORM.name())
    || formTypeOpt.get().get(VALUE).equals(TRIGGER_CONFIG_FORM.name());
 }

 public Optional<String> getInjectionUrl() {
  return fromNullable(injectionUrl);
 }

 public Optional<String> getInjectionUrlRegexp() {
  return fromNullable(injectionUrlRegexp);
 }
}
