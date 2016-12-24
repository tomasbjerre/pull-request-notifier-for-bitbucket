package se.bjurr.prnfb.service;

import static com.google.common.base.Charsets.UTF_8;
import static com.google.common.base.Throwables.propagate;
import static java.net.URLEncoder.encode;
import static org.slf4j.LoggerFactory.getLogger;
import static se.bjurr.prnfb.service.PrnfbVariable.EVERYTHING_URL;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import org.slf4j.Logger;

import com.atlassian.bitbucket.pull.PullRequest;
import com.atlassian.bitbucket.repository.RepositoryService;
import com.atlassian.bitbucket.server.ApplicationPropertiesService;
import com.atlassian.bitbucket.user.ApplicationUser;
import com.atlassian.bitbucket.user.SecurityService;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Supplier;

import se.bjurr.prnfb.http.ClientKeyStore;
import se.bjurr.prnfb.listener.PrnfbPullRequestAction;
import se.bjurr.prnfb.settings.PrnfbNotification;

public class PrnfbRenderer {
 private static final Logger LOG = getLogger(PrnfbRenderer.class);
 
 private final ApplicationUser applicationUser;
 private final PrnfbNotification prnfbNotification;
 private final ApplicationPropertiesService propertiesService;
 private final PullRequest pullRequest;
 private final PrnfbPullRequestAction pullRequestAction;
 private final RepositoryService repositoryService;
 private final SecurityService securityService;
 /**
  * Contains special variables that are only available for specific events like
  * {@link PrnfbVariable#BUTTON_TRIGGER_TITLE} and
  * {@link PrnfbVariable#PULL_REQUEST_COMMENT_TEXT}.
  */
 private final Map<PrnfbVariable, Supplier<String>> variables;

 PrnfbRenderer(PullRequest pullRequest, PrnfbPullRequestAction pullRequestAction, ApplicationUser applicationUser,
   RepositoryService repositoryService, ApplicationPropertiesService propertiesService,
   PrnfbNotification prnfbNotification, Map<PrnfbVariable, Supplier<String>> variables,
   SecurityService securityService) {
  this.pullRequest = pullRequest;
  this.pullRequestAction = pullRequestAction;
  this.applicationUser = applicationUser;
  this.repositoryService = repositoryService;
  this.prnfbNotification = prnfbNotification;
  this.propertiesService = propertiesService;
  this.variables = variables;
  this.securityService = securityService;
 }

 private boolean containsVariable(String string, final String regExpStr) {
  return string.contains(regExpStr.replaceAll("\\\\", ""));
 }

 @VisibleForTesting
 String getRenderedStringResolved(String string, Boolean forUrl, Boolean forJson, final String regExpStr, String resolved) {
  String replaceWith = null;
  try {
    if (forUrl) {
      replaceWith = forUrl ? encode(resolved, UTF_8.name()) : resolved;
    } else if (forJson) {
      // The string we're replacing as JSON is already within a string-encoded JSON blob, so we
      // just need to replace any quotes with \\", which will have the behavior we want.
      replaceWith = resolved.replace("\"", "\\\\\""); 
    }
    else {
      replaceWith = resolved;
    }
  } catch (UnsupportedEncodingException e) {
   propagate(e);
  }
  try {
   string = string.replaceAll(regExpStr, replaceWith);
  } catch (IllegalArgumentException e) {
   throw new RuntimeException("Tried to replace " + regExpStr + " with " + replaceWith, e);
  }
  return string;
 }

 @VisibleForTesting
 String regexp(PrnfbVariable variable) {
  return "\\$\\{" + variable.name() + "\\}";
 }

 public String render(String string, Boolean forUrl, Boolean forJson, ClientKeyStore clientKeyStore,
   Boolean shouldAcceptAnyCertificate) {
  string = renderVariable(string, false, forJson, clientKeyStore, shouldAcceptAnyCertificate, EVERYTHING_URL);

  for (final PrnfbVariable variable : PrnfbVariable.values()) {
   string = renderVariable(string, forUrl, forJson, clientKeyStore, shouldAcceptAnyCertificate, variable);
  }
  return string;
 }

 private String renderVariable(String string, Boolean forUrl, Boolean forJson, ClientKeyStore clientKeyStore,
   Boolean shouldAcceptAnyCertificate, final PrnfbVariable variable) {
  final String regExpStr = regexp(variable);
  if (containsVariable(string, regExpStr)) {
   String resolved = "";
   try {
    resolved = variable.resolve(pullRequest, pullRequestAction, applicationUser, repositoryService, propertiesService,
      prnfbNotification, variables, clientKeyStore, shouldAcceptAnyCertificate, securityService);
    if (resolved == null) {
     resolved = "";
    }
   } catch (Exception e) {
    LOG.error("Error when resolving " + variable, e);
   }
   return getRenderedStringResolved(string, forUrl, forJson, regExpStr, resolved);
  }
  return string;
 }
}
