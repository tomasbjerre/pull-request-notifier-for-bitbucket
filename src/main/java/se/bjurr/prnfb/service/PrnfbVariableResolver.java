package se.bjurr.prnfb.service;

import com.atlassian.bitbucket.pull.PullRequest;
import com.atlassian.bitbucket.repository.RepositoryService;
import com.atlassian.bitbucket.server.ApplicationPropertiesService;
import com.atlassian.bitbucket.user.ApplicationUser;
import com.atlassian.bitbucket.user.SecurityService;
import com.google.common.base.Supplier;
import java.util.Map;
import se.bjurr.prnfb.http.ClientKeyStore;
import se.bjurr.prnfb.listener.PrnfbPullRequestAction;
import se.bjurr.prnfb.settings.PrnfbNotification;

public interface PrnfbVariableResolver {

  String resolve(
      PullRequest pullRequest,
      PrnfbPullRequestAction pullRequestAction,
      ApplicationUser applicationUser,
      RepositoryService repositoryService,
      ApplicationPropertiesService propertiesService,
      PrnfbNotification prnfbNotification,
      Map<PrnfbVariable, Supplier<String>> variables,
      ClientKeyStore clientKeyStore,
      boolean shouldAcceptAnyCertificate,
      SecurityService securityService);
}
