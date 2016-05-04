package se.bjurr.prnfb.settings;

import static com.google.common.base.MoreObjects.firstNonNull;
import static com.google.common.base.Optional.fromNullable;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.emptyToNull;
import static com.google.common.base.Strings.nullToEmpty;
import static java.util.UUID.randomUUID;
import static java.util.regex.Pattern.compile;
import static se.bjurr.prnfb.http.UrlInvoker.HTTP_METHOD.GET;
import static se.bjurr.prnfb.settings.TRIGGER_IF_MERGE.ALWAYS;

import java.net.URL;
import java.util.List;
import java.util.UUID;

import se.bjurr.prnfb.http.UrlInvoker.HTTP_METHOD;
import se.bjurr.prnfb.listener.PrnfbPullRequestAction;

import com.atlassian.bitbucket.pull.PullRequestState;
import com.google.common.base.Optional;

public class PrnfbNotification implements HasUuid {

 private static final String DEFAULT_NAME = "Notification";
 private final String filterRegexp;
 private final String filterString;
 private final List<PrnfbHeader> headers;
 private final String injectionUrl;
 private final String injectionUrlRegexp;
 private final HTTP_METHOD method;
 private final String name;
 private final String password;
 private final String postContent;
 private final String projectKey;
 private final String proxyPassword;
 private final Integer proxyPort;
 private final String proxyServer;
 private final String proxyUser;
 private final String repositorySlug;
 private final TRIGGER_IF_MERGE triggerIfCanMerge;
 private final List<PullRequestState> triggerIgnoreStateList;
 private final List<PrnfbPullRequestAction> triggers;
 private final String url;
 private final String user;
 private final UUID uuid;

 public PrnfbNotification(PrnfbNotificationBuilder builder) throws ValidationException {
  this.uuid = firstNonNull(builder.getUUID(), randomUUID());
  this.proxyUser = emptyToNull(nullToEmpty(builder.getProxyUser()).trim());
  this.proxyPassword = emptyToNull(nullToEmpty(builder.getProxyPassword()).trim());
  this.proxyServer = emptyToNull(nullToEmpty(builder.getProxyServer()).trim());
  this.proxyPort = firstNonNull(builder.getProxyPort(), -1);
  this.headers = checkNotNull(builder.getHeaders());
  this.postContent = emptyToNull(nullToEmpty(builder.getPostContent()).trim());
  this.method = firstNonNull(builder.getMethod(), GET);
  this.triggerIfCanMerge = firstNonNull(builder.getTriggerIfCanMerge(), ALWAYS);
  this.repositorySlug = emptyToNull(builder.getRepositorySlug());
  this.projectKey = emptyToNull(builder.getProjectKey());
  try {
   new URL(builder.getUrl());
  } catch (final Exception e) {
   throw new ValidationException("url", "URL not valid!");
  }
  if (!nullToEmpty(builder.getFilterRegexp()).trim().isEmpty()) {
   try {
    compile(builder.getFilterRegexp());
   } catch (final Exception e) {
    throw new ValidationException("filter_regexp", "Filter regexp not valid! " + e.getMessage().replaceAll("\n", " "));
   }
   if (nullToEmpty(builder.getFilterString()).trim().isEmpty()) {
    throw new ValidationException("filter_string", "Filter string not set, nothing to match regexp against!");
   }
  }
  this.url = builder.getUrl();
  this.user = emptyToNull(nullToEmpty(builder.getUser()).trim());
  this.password = emptyToNull(nullToEmpty(builder.getPassword()).trim());
  this.triggers = checkNotNull(builder.getTriggers(), "triggers");
  if (this.triggers.isEmpty()) {
   throw new ValidationException("triggers", "At least one trigger must be selected.");
  }
  this.filterString = builder.getFilterString();
  this.filterRegexp = builder.getFilterRegexp();
  this.name = firstNonNull(emptyToNull(nullToEmpty(builder.getName()).trim()), DEFAULT_NAME);
  this.injectionUrl = emptyToNull(nullToEmpty(builder.getInjectionUrl()).trim());
  this.injectionUrlRegexp = emptyToNull(nullToEmpty(builder.getInjectionUrlRegexp()).trim());
  this.triggerIgnoreStateList = builder.getTriggerIgnoreStateList();
 }

 @Override
 public boolean equals(Object obj) {
  if (this == obj) {
   return true;
  }
  if (obj == null) {
   return false;
  }
  if (getClass() != obj.getClass()) {
   return false;
  }
  PrnfbNotification other = (PrnfbNotification) obj;
  if (this.filterRegexp == null) {
   if (other.filterRegexp != null) {
    return false;
   }
  } else if (!this.filterRegexp.equals(other.filterRegexp)) {
   return false;
  }
  if (this.filterString == null) {
   if (other.filterString != null) {
    return false;
   }
  } else if (!this.filterString.equals(other.filterString)) {
   return false;
  }
  if (this.headers == null) {
   if (other.headers != null) {
    return false;
   }
  } else if (!this.headers.equals(other.headers)) {
   return false;
  }
  if (this.injectionUrl == null) {
   if (other.injectionUrl != null) {
    return false;
   }
  } else if (!this.injectionUrl.equals(other.injectionUrl)) {
   return false;
  }
  if (this.injectionUrlRegexp == null) {
   if (other.injectionUrlRegexp != null) {
    return false;
   }
  } else if (!this.injectionUrlRegexp.equals(other.injectionUrlRegexp)) {
   return false;
  }
  if (this.method != other.method) {
   return false;
  }
  if (this.name == null) {
   if (other.name != null) {
    return false;
   }
  } else if (!this.name.equals(other.name)) {
   return false;
  }
  if (this.password == null) {
   if (other.password != null) {
    return false;
   }
  } else if (!this.password.equals(other.password)) {
   return false;
  }
  if (this.postContent == null) {
   if (other.postContent != null) {
    return false;
   }
  } else if (!this.postContent.equals(other.postContent)) {
   return false;
  }
  if (this.projectKey == null) {
   if (other.projectKey != null) {
    return false;
   }
  } else if (!this.projectKey.equals(other.projectKey)) {
   return false;
  }
  if (this.proxyPassword == null) {
   if (other.proxyPassword != null) {
    return false;
   }
  } else if (!this.proxyPassword.equals(other.proxyPassword)) {
   return false;
  }
  if (this.proxyPort == null) {
   if (other.proxyPort != null) {
    return false;
   }
  } else if (!this.proxyPort.equals(other.proxyPort)) {
   return false;
  }
  if (this.proxyServer == null) {
   if (other.proxyServer != null) {
    return false;
   }
  } else if (!this.proxyServer.equals(other.proxyServer)) {
   return false;
  }
  if (this.proxyUser == null) {
   if (other.proxyUser != null) {
    return false;
   }
  } else if (!this.proxyUser.equals(other.proxyUser)) {
   return false;
  }
  if (this.repositorySlug == null) {
   if (other.repositorySlug != null) {
    return false;
   }
  } else if (!this.repositorySlug.equals(other.repositorySlug)) {
   return false;
  }
  if (this.triggerIfCanMerge != other.triggerIfCanMerge) {
   return false;
  }
  if (this.triggerIgnoreStateList == null) {
   if (other.triggerIgnoreStateList != null) {
    return false;
   }
  } else if (!this.triggerIgnoreStateList.equals(other.triggerIgnoreStateList)) {
   return false;
  }
  if (this.triggers == null) {
   if (other.triggers != null) {
    return false;
   }
  } else if (!this.triggers.equals(other.triggers)) {
   return false;
  }
  if (this.url == null) {
   if (other.url != null) {
    return false;
   }
  } else if (!this.url.equals(other.url)) {
   return false;
  }
  if (this.user == null) {
   if (other.user != null) {
    return false;
   }
  } else if (!this.user.equals(other.user)) {
   return false;
  }
  if (this.uuid == null) {
   if (other.uuid != null) {
    return false;
   }
  } else if (!this.uuid.equals(other.uuid)) {
   return false;
  }
  return true;
 }

 public Optional<String> getFilterRegexp() {
  return fromNullable(this.filterRegexp);
 }

 public Optional<String> getFilterString() {
  return fromNullable(this.filterString);
 }

 public List<PrnfbHeader> getHeaders() {
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

 public Optional<String> getProjectKey() {
  return fromNullable(this.projectKey);
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

 public Optional<String> getRepositorySlug() {
  return fromNullable(this.repositorySlug);
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

 @Override
 public UUID getUuid() {
  return this.uuid;
 }

 @Override
 public int hashCode() {
  final int prime = 31;
  int result = 1;
  result = prime * result + ((this.filterRegexp == null) ? 0 : this.filterRegexp.hashCode());
  result = prime * result + ((this.filterString == null) ? 0 : this.filterString.hashCode());
  result = prime * result + ((this.headers == null) ? 0 : this.headers.hashCode());
  result = prime * result + ((this.injectionUrl == null) ? 0 : this.injectionUrl.hashCode());
  result = prime * result + ((this.injectionUrlRegexp == null) ? 0 : this.injectionUrlRegexp.hashCode());
  result = prime * result + ((this.method == null) ? 0 : this.method.hashCode());
  result = prime * result + ((this.name == null) ? 0 : this.name.hashCode());
  result = prime * result + ((this.password == null) ? 0 : this.password.hashCode());
  result = prime * result + ((this.postContent == null) ? 0 : this.postContent.hashCode());
  result = prime * result + ((this.projectKey == null) ? 0 : this.projectKey.hashCode());
  result = prime * result + ((this.proxyPassword == null) ? 0 : this.proxyPassword.hashCode());
  result = prime * result + ((this.proxyPort == null) ? 0 : this.proxyPort.hashCode());
  result = prime * result + ((this.proxyServer == null) ? 0 : this.proxyServer.hashCode());
  result = prime * result + ((this.proxyUser == null) ? 0 : this.proxyUser.hashCode());
  result = prime * result + ((this.repositorySlug == null) ? 0 : this.repositorySlug.hashCode());
  result = prime * result + ((this.triggerIfCanMerge == null) ? 0 : this.triggerIfCanMerge.hashCode());
  result = prime * result + ((this.triggerIgnoreStateList == null) ? 0 : this.triggerIgnoreStateList.hashCode());
  result = prime * result + ((this.triggers == null) ? 0 : this.triggers.hashCode());
  result = prime * result + ((this.url == null) ? 0 : this.url.hashCode());
  result = prime * result + ((this.user == null) ? 0 : this.user.hashCode());
  result = prime * result + ((this.uuid == null) ? 0 : this.uuid.hashCode());
  return result;
 }

 @Override
 public String toString() {
  return "PrnfbNotification [filterRegexp=" + this.filterRegexp + ", filterString=" + this.filterString + ", headers="
    + this.headers + ", injectionUrl=" + this.injectionUrl + ", injectionUrlRegexp=" + this.injectionUrlRegexp
    + ", method=" + this.method + ", name=" + this.name + ", password=" + this.password + ", postContent="
    + this.postContent + ", projectKey=" + this.projectKey + ", proxyPassword=" + this.proxyPassword + ", proxyPort="
    + this.proxyPort + ", proxyServer=" + this.proxyServer + ", proxyUser=" + this.proxyUser + ", repositorySlug="
    + this.repositorySlug + ", triggerIfCanMerge=" + this.triggerIfCanMerge + ", triggerIgnoreStateList="
    + this.triggerIgnoreStateList + ", triggers=" + this.triggers + ", url=" + this.url + ", user=" + this.user
    + ", uuid=" + this.uuid + "]";
 }

}
