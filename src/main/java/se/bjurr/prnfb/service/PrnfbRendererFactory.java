package se.bjurr.prnfb.service;

import com.atlassian.bitbucket.auth.AuthenticationContext;
import com.atlassian.bitbucket.pull.PullRequest;
import com.atlassian.bitbucket.repository.RepositoryService;
import com.atlassian.bitbucket.server.ApplicationPropertiesService;
import com.atlassian.bitbucket.user.ApplicationUser;
import com.atlassian.bitbucket.user.SecurityService;

import se.bjurr.prnfb.http.ClientKeyStore;
import se.bjurr.prnfb.listener.PrnfbPullRequestAction;
import se.bjurr.prnfb.settings.PrnfbNotification;

public class PrnfbRendererFactory {

  private final AuthenticationContext authenticationContext;
  private final ApplicationPropertiesService propertiesService;
  private final RepositoryService repositoryService;
  private final SecurityService securityService;

  public PrnfbRendererFactory(
      RepositoryService repositoryService,
      ApplicationPropertiesService propertiesService,
      AuthenticationContext authenticationContext,
      SecurityService securityService) {
    this.repositoryService = repositoryService;
    this.propertiesService = propertiesService;
    this.authenticationContext = authenticationContext;
    this.securityService = securityService;
  }

  public PrnfbRendererWrapper create(
      PullRequest pullRequest,
      PrnfbPullRequestAction pullRequestAction,
      VariablesContext variables,
      ClientKeyStore clientKeyStore,
      boolean shouldAcceptAnyCertificate) {
    PrnfbNotification prnfbNotification = null;
    PrnfbRenderer renderer = create(pullRequest, pullRequestAction, prnfbNotification, variables);
    return new PrnfbRendererWrapper(renderer, clientKeyStore, shouldAcceptAnyCertificate);
  }

  public PrnfbRenderer create(
      PullRequest pullRequest,
      PrnfbPullRequestAction pullRequestAction,
      PrnfbNotification prnfbNotification,
      VariablesContext variables) {
    return create(
        pullRequest,
        pullRequestAction,
        prnfbNotification,
        variables,
        this.authenticationContext.getCurrentUser());
  }

  public PrnfbRenderer create(
      PullRequest pullRequest,
      PrnfbPullRequestAction pullRequestAction,
      PrnfbNotification prnfbNotification,
      VariablesContext variables,
      ApplicationUser currentUser) {
    return new PrnfbRenderer(
        pullRequest,
        pullRequestAction,
        currentUser,
        this.repositoryService,
        this.propertiesService,
        prnfbNotification,
        variables.getVariables(),
        this.securityService);
  }
}
