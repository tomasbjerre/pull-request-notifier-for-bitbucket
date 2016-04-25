package se.bjurr.prnfb.service;

import static com.google.common.base.Charsets.UTF_8;
import static com.google.common.base.Throwables.propagate;
import static java.net.URLEncoder.encode;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import se.bjurr.prnfb.http.ClientKeyStore;
import se.bjurr.prnfb.listener.PrnfbPullRequestAction;
import se.bjurr.prnfb.settings.PrnfbNotification;

import com.atlassian.bitbucket.pull.PullRequest;
import com.atlassian.bitbucket.repository.RepositoryService;
import com.atlassian.bitbucket.server.ApplicationPropertiesService;
import com.atlassian.bitbucket.user.ApplicationUser;
import com.google.common.base.Supplier;

public class PrnfbRenderer {

 private final ApplicationUser applicationUser;
 private final PrnfbNotification prnfbNotification;
 private final ApplicationPropertiesService propertiesService;
 private final PullRequest pullRequest;
 private final PrnfbPullRequestAction pullRequestAction;
 private final RepositoryService repositoryService;
 /**
  * Contains special variables that are only available for specific events like
  * {@link PrnfbVariable#BUTTON_TRIGGER_TITLE} and
  * {@link PrnfbVariable#PULL_REQUEST_COMMENT_TEXT}.
  */
 private final Map<PrnfbVariable, Supplier<String>> variables;

 PrnfbRenderer(PullRequest pullRequest, PrnfbPullRequestAction pullRequestAction, ApplicationUser applicationUser,
   RepositoryService repositoryService, ApplicationPropertiesService propertiesService,
   PrnfbNotification prnfbNotification, Map<PrnfbVariable, Supplier<String>> variables) {
  this.pullRequest = pullRequest;
  this.pullRequestAction = pullRequestAction;
  this.applicationUser = applicationUser;
  this.repositoryService = repositoryService;
  this.prnfbNotification = prnfbNotification;
  this.propertiesService = propertiesService;
  this.variables = variables;
 }

 public String render(String string, Boolean forUrl, ClientKeyStore clientKeyStore, Boolean shouldAcceptAnyCertificate) {
  for (final PrnfbVariable variable : PrnfbVariable.values()) {
   final String regExpStr = "\\$\\{" + variable.name() + "\\}";
   if (string.contains(regExpStr.replaceAll("\\\\", ""))) {
    try {
     String resolved = variable.resolve(this.pullRequest, this.pullRequestAction, this.applicationUser,
       this.repositoryService, this.propertiesService, this.prnfbNotification, this.variables, clientKeyStore,
       shouldAcceptAnyCertificate);
     string = string.replaceAll(regExpStr, forUrl ? encode(resolved, UTF_8.name()) : resolved);
    } catch (UnsupportedEncodingException e) {
     propagate(e);
    }
   }
  }
  return string;
 }
}
