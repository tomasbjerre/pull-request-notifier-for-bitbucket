package se.bjurr.prnfb.service;

import java.util.Map;

import se.bjurr.prnfb.listener.PrnfbPullRequestAction;
import se.bjurr.prnfb.settings.PrnfbNotification;

import com.atlassian.bitbucket.auth.AuthenticationContext;
import com.atlassian.bitbucket.pull.PullRequest;
import com.atlassian.bitbucket.repository.RepositoryService;
import com.atlassian.bitbucket.server.ApplicationPropertiesService;
import com.atlassian.bitbucket.user.SecurityService;
import com.google.common.base.Supplier;

public class PrnfbRendererFactory {

 private final AuthenticationContext authenticationContext;
 private final ApplicationPropertiesService propertiesService;
 private final RepositoryService repositoryService;
 private final SecurityService securityService;

 public PrnfbRendererFactory(RepositoryService repositoryService, ApplicationPropertiesService propertiesService,
   AuthenticationContext authenticationContext, SecurityService securityService) {
  this.repositoryService = repositoryService;
  this.propertiesService = propertiesService;
  this.authenticationContext = authenticationContext;
  this.securityService = securityService;
 }

 public PrnfbRenderer create(PullRequest pullRequest, PrnfbPullRequestAction pullRequestAction,
   PrnfbNotification prnfbNotification, Map<PrnfbVariable, Supplier<String>> variables) {
  return new PrnfbRenderer(pullRequest, pullRequestAction, this.authenticationContext.getCurrentUser(),
    this.repositoryService, this.propertiesService, prnfbNotification, variables, this.securityService);
 }
}
