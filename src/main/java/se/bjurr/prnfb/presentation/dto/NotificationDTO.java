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
 private String name;
 private String password;
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

 public String getFilterRegexp() {
  return this.filterRegexp;
 }

 public String getFilterString() {
  return this.filterString;
 }

 public List<HeaderDTO> getHeaders() {
  return this.headers;
 }

 public String getInjectionUrl() {
  return this.injectionUrl;
 }

 public String getInjectionUrlRegexp() {
  return this.injectionUrlRegexp;
 }

 public HTTP_METHOD getMethod() {
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

 public Integer getProxyPort() {
  return this.proxyPort;
 }

 public String getProxyServer() {
  return this.proxyServer;
 }

 public String getProxyUser() {
  return this.proxyUser;
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

 public String getUser() {
  return this.user;
 }

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
  result = prime * result + ((this.proxyPassword == null) ? 0 : this.proxyPassword.hashCode());
  result = prime * result + ((this.proxyPort == null) ? 0 : this.proxyPort.hashCode());
  result = prime * result + ((this.proxyServer == null) ? 0 : this.proxyServer.hashCode());
  result = prime * result + ((this.proxyUser == null) ? 0 : this.proxyUser.hashCode());
  result = prime * result + ((this.triggerIfCanMerge == null) ? 0 : this.triggerIfCanMerge.hashCode());
  result = prime * result + ((this.triggerIgnoreStateList == null) ? 0 : this.triggerIgnoreStateList.hashCode());
  result = prime * result + ((this.triggers == null) ? 0 : this.triggers.hashCode());
  result = prime * result + ((this.url == null) ? 0 : this.url.hashCode());
  result = prime * result + ((this.user == null) ? 0 : this.user.hashCode());
  result = prime * result + ((this.uuid == null) ? 0 : this.uuid.hashCode());
  return result;
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
 public String toString() {
  return "NotificationDTO [filterRegexp=" + this.filterRegexp + ", filterString=" + this.filterString + ", headers="
    + this.headers + ", injectionUrl=" + this.injectionUrl + ", injectionUrlRegexp=" + this.injectionUrlRegexp
    + ", method=" + this.method + ", password=" + this.password + ", name=" + this.name + ", postContent="
    + this.postContent + ", proxyPassword=" + this.proxyPassword + ", proxyPort=" + this.proxyPort + ", proxyServer="
    + this.proxyServer + ", proxyUser=" + this.proxyUser + ", triggerIfCanMerge=" + this.triggerIfCanMerge
    + ", triggerIgnoreStateList=" + this.triggerIgnoreStateList + ", triggers=" + this.triggers + ", url=" + this.url
    + ", user=" + this.user + ", uuid=" + this.uuid + "]";
 }

}
