package se.bjurr.prnfb.presentation.dto;

import static javax.xml.bind.annotation.XmlAccessType.FIELD;

import java.util.List;
import java.util.UUID;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import se.bjurr.prnfb.http.UrlInvoker.HTTP_METHOD;
import se.bjurr.prnfb.listener.PrnfbPullRequestAction;
import se.bjurr.prnfb.settings.TRIGGER_IF_MERGE;

import com.atlassian.bitbucket.pull.PullRequestState;

@XmlRootElement
@XmlAccessorType(FIELD)
public class NotificationDTO {

 private String filterRegexp;
 private String filterString;
 private List<HeaderDTO> headers;
 private String injectionUrl;
 private String injectionUrlRegexp;
 private HTTP_METHOD method;
 private String password;
 private String name;
 private String postContent;
 private String proxyPassword;
 private Integer proxyPort;
 private String proxyServer;
 private String proxyUser;
 private TRIGGER_IF_MERGE triggerIfCanMerge;
 private List<PullRequestState> triggerIgnoreStateList;
 private List<PrnfbPullRequestAction> triggers;
 private String url;
 private String user;
 private UUID uuid;

 public String getPostContent() {
  return postContent;
 }

 public String getProxyPassword() {
  return proxyPassword;
 }

 public Integer getProxyPort() {
  return proxyPort;
 }

 public String getProxyServer() {
  return proxyServer;
 }

 public String getProxyUser() {
  return proxyUser;
 }

 public TRIGGER_IF_MERGE getTriggerIfCanMerge() {
  return triggerIfCanMerge;
 }

 public List<PullRequestState> getTriggerIgnoreStateList() {
  return triggerIgnoreStateList;
 }

 public List<PrnfbPullRequestAction> getTriggers() {
  return triggers;
 }

 public String getUrl() {
  return url;
 }

 public String getUser() {
  return user;
 }

 public UUID getUuid() {
  return uuid;
 }

 public String getFilterRegexp() {
  return filterRegexp;
 }

 public String getFilterString() {
  return filterString;
 }

 public List<HeaderDTO> getHeaders() {
  return headers;
 }

 public String getInjectionUrl() {
  return injectionUrl;
 }

 public String getInjectionUrlRegexp() {
  return injectionUrlRegexp;
 }

 public HTTP_METHOD getMethod() {
  return method;
 }

 public String getName() {
  return name;
 }

 public String getPassword() {
  return password;
 }

 public void setFilterRegexp(String filterRegexp) {
  this.filterRegexp = filterRegexp;
 }

 public void setFilterString(String filterString) {
  this.filterString = filterString;
 }

 public void setHeaders(List<HeaderDTO> headers) {
  this.headers = headers;
 }

 public void setInjectionUrl(String injectionUrl) {
  this.injectionUrl = injectionUrl;
 }

 public void setInjectionUrlRegexp(String injectionUrlRegexp) {
  this.injectionUrlRegexp = injectionUrlRegexp;
 }

 public void setMethod(HTTP_METHOD method) {
  this.method = method;
 }

 public void setName(String name) {
  this.name = name;
 }

 public void setPassword(String password) {
  this.password = password;
 }

 public void setPostContent(String postContent) {
  this.postContent = postContent;
 }

 public void setProxyPassword(String proxyPassword) {
  this.proxyPassword = proxyPassword;
 }

 public void setProxyPort(Integer proxyPort) {
  this.proxyPort = proxyPort;
 }

 public void setProxyServer(String proxyServer) {
  this.proxyServer = proxyServer;
 }

 public void setProxyUser(String proxyUser) {
  this.proxyUser = proxyUser;
 }

 public void setTriggerIfCanMerge(TRIGGER_IF_MERGE triggerIfCanMerge) {
  this.triggerIfCanMerge = triggerIfCanMerge;
 }

 public void setTriggerIgnoreStateList(List<PullRequestState> triggerIgnoreStateList) {
  this.triggerIgnoreStateList = triggerIgnoreStateList;
 }

 public void setTriggers(List<PrnfbPullRequestAction> triggers) {
  this.triggers = triggers;
 }

 public void setUrl(String url) {
  this.url = url;
 }

 public void setUser(String user) {
  this.user = user;
 }

 public void setUuid(UUID uuid) {
  this.uuid = uuid;
 }

 @Override
 public int hashCode() {
  final int prime = 31;
  int result = 1;
  result = prime * result + ((filterRegexp == null) ? 0 : filterRegexp.hashCode());
  result = prime * result + ((filterString == null) ? 0 : filterString.hashCode());
  result = prime * result + ((headers == null) ? 0 : headers.hashCode());
  result = prime * result + ((injectionUrl == null) ? 0 : injectionUrl.hashCode());
  result = prime * result + ((injectionUrlRegexp == null) ? 0 : injectionUrlRegexp.hashCode());
  result = prime * result + ((method == null) ? 0 : method.hashCode());
  result = prime * result + ((name == null) ? 0 : name.hashCode());
  result = prime * result + ((password == null) ? 0 : password.hashCode());
  result = prime * result + ((postContent == null) ? 0 : postContent.hashCode());
  result = prime * result + ((proxyPassword == null) ? 0 : proxyPassword.hashCode());
  result = prime * result + ((proxyPort == null) ? 0 : proxyPort.hashCode());
  result = prime * result + ((proxyServer == null) ? 0 : proxyServer.hashCode());
  result = prime * result + ((proxyUser == null) ? 0 : proxyUser.hashCode());
  result = prime * result + ((triggerIfCanMerge == null) ? 0 : triggerIfCanMerge.hashCode());
  result = prime * result + ((triggerIgnoreStateList == null) ? 0 : triggerIgnoreStateList.hashCode());
  result = prime * result + ((triggers == null) ? 0 : triggers.hashCode());
  result = prime * result + ((url == null) ? 0 : url.hashCode());
  result = prime * result + ((user == null) ? 0 : user.hashCode());
  result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
  return result;
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
  NotificationDTO other = (NotificationDTO) obj;
  if (filterRegexp == null) {
   if (other.filterRegexp != null) {
    return false;
   }
  } else if (!filterRegexp.equals(other.filterRegexp)) {
   return false;
  }
  if (filterString == null) {
   if (other.filterString != null) {
    return false;
   }
  } else if (!filterString.equals(other.filterString)) {
   return false;
  }
  if (headers == null) {
   if (other.headers != null) {
    return false;
   }
  } else if (!headers.equals(other.headers)) {
   return false;
  }
  if (injectionUrl == null) {
   if (other.injectionUrl != null) {
    return false;
   }
  } else if (!injectionUrl.equals(other.injectionUrl)) {
   return false;
  }
  if (injectionUrlRegexp == null) {
   if (other.injectionUrlRegexp != null) {
    return false;
   }
  } else if (!injectionUrlRegexp.equals(other.injectionUrlRegexp)) {
   return false;
  }
  if (method != other.method) {
   return false;
  }
  if (name == null) {
   if (other.name != null) {
    return false;
   }
  } else if (!name.equals(other.name)) {
   return false;
  }
  if (password == null) {
   if (other.password != null) {
    return false;
   }
  } else if (!password.equals(other.password)) {
   return false;
  }
  if (postContent == null) {
   if (other.postContent != null) {
    return false;
   }
  } else if (!postContent.equals(other.postContent)) {
   return false;
  }
  if (proxyPassword == null) {
   if (other.proxyPassword != null) {
    return false;
   }
  } else if (!proxyPassword.equals(other.proxyPassword)) {
   return false;
  }
  if (proxyPort == null) {
   if (other.proxyPort != null) {
    return false;
   }
  } else if (!proxyPort.equals(other.proxyPort)) {
   return false;
  }
  if (proxyServer == null) {
   if (other.proxyServer != null) {
    return false;
   }
  } else if (!proxyServer.equals(other.proxyServer)) {
   return false;
  }
  if (proxyUser == null) {
   if (other.proxyUser != null) {
    return false;
   }
  } else if (!proxyUser.equals(other.proxyUser)) {
   return false;
  }
  if (triggerIfCanMerge != other.triggerIfCanMerge) {
   return false;
  }
  if (triggerIgnoreStateList == null) {
   if (other.triggerIgnoreStateList != null) {
    return false;
   }
  } else if (!triggerIgnoreStateList.equals(other.triggerIgnoreStateList)) {
   return false;
  }
  if (triggers == null) {
   if (other.triggers != null) {
    return false;
   }
  } else if (!triggers.equals(other.triggers)) {
   return false;
  }
  if (url == null) {
   if (other.url != null) {
    return false;
   }
  } else if (!url.equals(other.url)) {
   return false;
  }
  if (user == null) {
   if (other.user != null) {
    return false;
   }
  } else if (!user.equals(other.user)) {
   return false;
  }
  if (uuid == null) {
   if (other.uuid != null) {
    return false;
   }
  } else if (!uuid.equals(other.uuid)) {
   return false;
  }
  return true;
 }

 @Override
 public String toString() {
  return "NotificationDTO [filterRegexp=" + filterRegexp + ", filterString=" + filterString + ", headers=" + headers
    + ", injectionUrl=" + injectionUrl + ", injectionUrlRegexp=" + injectionUrlRegexp + ", method=" + method
    + ", password=" + password + ", name=" + name + ", postContent=" + postContent + ", proxyPassword=" + proxyPassword
    + ", proxyPort=" + proxyPort + ", proxyServer=" + proxyServer + ", proxyUser=" + proxyUser + ", triggerIfCanMerge="
    + triggerIfCanMerge + ", triggerIgnoreStateList=" + triggerIgnoreStateList + ", triggers=" + triggers + ", url="
    + url + ", user=" + user + ", uuid=" + uuid + "]";
 }

}
