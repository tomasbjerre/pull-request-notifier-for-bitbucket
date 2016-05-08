package se.bjurr.prnfb.settings.legacy;

import static com.google.common.base.MoreObjects.firstNonNull;
import static com.google.common.base.Optional.fromNullable;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.emptyToNull;
import static com.google.common.base.Strings.nullToEmpty;
import static com.google.common.collect.Iterables.tryFind;
import static java.util.regex.Pattern.compile;
import static se.bjurr.prnfb.http.UrlInvoker.HTTP_METHOD.GET;
import static se.bjurr.prnfb.settings.legacy.AdminFormValues.DEFAULT_NAME;
import static se.bjurr.prnfb.settings.legacy.AdminFormValues.VALUE;
import static se.bjurr.prnfb.settings.legacy.AdminFormValues.FIELDS.filter_regexp;
import static se.bjurr.prnfb.settings.legacy.AdminFormValues.FIELDS.filter_string;
import static se.bjurr.prnfb.settings.legacy.AdminFormValues.FORM_TYPE.TRIGGER_CONFIG_FORM;
import static se.bjurr.prnfb.settings.legacy.AdminFormValues.TRIGGER_IF_MERGE.ALWAYS;
import static se.bjurr.prnfb.settings.legacy.PrnfbPredicates.predicate;

import java.net.URL;
import java.util.List;
import java.util.Map;

import se.bjurr.prnfb.http.UrlInvoker.HTTP_METHOD;
import se.bjurr.prnfb.listener.PrnfbPullRequestAction;
import se.bjurr.prnfb.settings.legacy.AdminFormValues.FIELDS;
import se.bjurr.prnfb.settings.legacy.AdminFormValues.FORM_TYPE;
import se.bjurr.prnfb.settings.legacy.AdminFormValues.TRIGGER_IF_MERGE;

import com.atlassian.bitbucket.pull.PullRequestState;
import com.google.common.base.Optional;

@Deprecated
public class PrnfbNotification {
 public static boolean isOfType(AdminFormValues config, FORM_TYPE formType) {
  Optional<Map<String, String>> formTypeOpt = tryFind(config, predicate(AdminFormValues.FIELDS.FORM_TYPE.name()));
  return !formTypeOpt.isPresent() && formType.name().equals(TRIGGER_CONFIG_FORM.name())
    || formTypeOpt.get().get(VALUE).equals(TRIGGER_CONFIG_FORM.name());
 }

 private final String filterRegexp;
 private final String filterString;
 private final List<Header> headers;
 private final String injectionUrl;
 private final String injectionUrlRegexp;
 private final HTTP_METHOD method;
 private final String name;
 private final String password;
 private final String postContent;
 private final String proxyPassword;
 private final Integer proxyPort;
 private final String proxyServer;
 private final String proxyUser;
 private final TRIGGER_IF_MERGE triggerIfCanMerge;
 private final List<PullRequestState> triggerIgnoreStateList;
 private final List<PrnfbPullRequestAction> triggers;
 private final String url;

 private final String user;

 public PrnfbNotification(PrnfbNotificationBuilder builder) throws ValidationException {
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

 public Optional<String> getFilterRegexp() {
  return fromNullable(this.filterRegexp);
 }

 public Optional<String> getFilterString() {
  return fromNullable(this.filterString);
 }

 public List<Header> getHeaders() {
  return this.headers;
 }

 public Optional<String> getInjectionUrl() {
  return fromNullable(this.injectionUrl);
 }

 public Optional<String> getInjectionUrlRegexp() {
  return fromNullable(this.injectionUrlRegexp);
 }

 public HTTP_METHOD getMethod() {
  return this.method;
 }

 public String getName() {
  return this.name;
 }

 public Optional<String> getPassword() {
  return fromNullable(this.password);
 }

 public Optional<String> getPostContent() {
  return fromNullable(this.postContent);
 }

 public Optional<String> getProxyPassword() {
  return fromNullable(this.proxyPassword);
 }

 public Integer getProxyPort() {
  return this.proxyPort;
 }

 public Optional<String> getProxyServer() {
  return fromNullable(this.proxyServer);
 }

 public Optional<String> getProxyUser() {
  return fromNullable(this.proxyUser);
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

 public Optional<String> getUser() {
  return fromNullable(this.user);
 }
}
