package se.bjurr.prnfb.service;

import static com.google.common.base.Charsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static se.bjurr.prnfb.service.PrnfbVariable.EVERYTHING_URL;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;

import org.junit.Test;

import se.bjurr.prnfb.http.ClientKeyStore;
import se.bjurr.prnfb.listener.PrnfbPullRequestAction;
import se.bjurr.prnfb.settings.PrnfbNotification;

import com.atlassian.bitbucket.pull.PullRequest;
import com.atlassian.bitbucket.repository.RepositoryService;
import com.atlassian.bitbucket.server.ApplicationPropertiesService;
import com.atlassian.bitbucket.user.ApplicationUser;
import com.atlassian.bitbucket.user.SecurityService;
import com.google.common.base.Supplier;
import com.google.common.io.Files;
import com.google.common.io.Resources;

public class PrnfbVariableTest {

 private ApplicationUser applicationUser;
 private ClientKeyStore clientKeyStore;
 private PrnfbNotification prnfbNotification;
 private ApplicationPropertiesService propertiesService;
 private PullRequest pullRequest;
 private PrnfbPullRequestAction pullRequestAction;
 private RepositoryService repositoryService;
 private SecurityService securityService;
 private boolean shouldAcceptAnyCertificate;
 private Map<PrnfbVariable, Supplier<String>> variables;

 @Test
 public void testThatAdminAndReadmeContainsVariables() throws IOException, URISyntaxException {
  URL adminResource = Resources.getResource("admin.vm");
  String adminPageContent = Resources.toString(adminResource, UTF_8);
  File readme = findReadme(new File(adminResource.toURI()));
  String readmeContent = Files.toString(readme, UTF_8);
  for (PrnfbVariable variable : PrnfbVariable.values()) {
   assertThat(adminPageContent)//
     .as("admin.vm should include " + variable.name() + "\nWas:" + adminPageContent)//
     .contains(variable.name());
   assertThat(readmeContent)//
     .as("README.md should include " + variable.name() + "\nWas:" + readmeContent)//
     .contains(variable.name());
  }
 }

 @Test
 public void testThatEverythingVariableIsResolvedToEveryOtherVariable() {
  String actual = EVERYTHING_URL.resolve(this.pullRequest, this.pullRequestAction, this.applicationUser,
    this.repositoryService, this.propertiesService, this.prnfbNotification, this.variables, this.clientKeyStore,
    this.shouldAcceptAnyCertificate, this.securityService);
  assertThat(actual)//
    .isEqualTo(
      "BUTTON_TRIGGER_TITLE=\\${BUTTON_TRIGGER_TITLE}&INJECTION_URL_VALUE=\\${INJECTION_URL_VALUE}&PULL_REQUEST_ACTION=\\${PULL_REQUEST_ACTION}&PULL_REQUEST_AUTHOR_DISPLAY_NAME=\\${PULL_REQUEST_AUTHOR_DISPLAY_NAME}&PULL_REQUEST_AUTHOR_EMAIL=\\${PULL_REQUEST_AUTHOR_EMAIL}&PULL_REQUEST_AUTHOR_ID=\\${PULL_REQUEST_AUTHOR_ID}&PULL_REQUEST_AUTHOR_NAME=\\${PULL_REQUEST_AUTHOR_NAME}&PULL_REQUEST_AUTHOR_SLUG=\\${PULL_REQUEST_AUTHOR_SLUG}&PULL_REQUEST_COMMENT_TEXT=\\${PULL_REQUEST_COMMENT_TEXT}&PULL_REQUEST_FROM_BRANCH=\\${PULL_REQUEST_FROM_BRANCH}&PULL_REQUEST_FROM_HASH=\\${PULL_REQUEST_FROM_HASH}&PULL_REQUEST_FROM_HTTP_CLONE_URL=\\${PULL_REQUEST_FROM_HTTP_CLONE_URL}&PULL_REQUEST_FROM_ID=\\${PULL_REQUEST_FROM_ID}&PULL_REQUEST_FROM_REPO_ID=\\${PULL_REQUEST_FROM_REPO_ID}&PULL_REQUEST_FROM_REPO_NAME=\\${PULL_REQUEST_FROM_REPO_NAME}&PULL_REQUEST_FROM_REPO_PROJECT_ID=\\${PULL_REQUEST_FROM_REPO_PROJECT_ID}&PULL_REQUEST_FROM_REPO_PROJECT_KEY=\\${PULL_REQUEST_FROM_REPO_PROJECT_KEY}&PULL_REQUEST_FROM_REPO_SLUG=\\${PULL_REQUEST_FROM_REPO_SLUG}&PULL_REQUEST_FROM_SSH_CLONE_URL=\\${PULL_REQUEST_FROM_SSH_CLONE_URL}&PULL_REQUEST_ID=\\${PULL_REQUEST_ID}&PULL_REQUEST_MERGE_COMMIT=\\${PULL_REQUEST_MERGE_COMMIT}&PULL_REQUEST_PARTICIPANTS_APPROVED_COUNT=\\${PULL_REQUEST_PARTICIPANTS_APPROVED_COUNT}&PULL_REQUEST_REVIEWERS=\\${PULL_REQUEST_REVIEWERS}&PULL_REQUEST_REVIEWERS_APPROVED_COUNT=\\${PULL_REQUEST_REVIEWERS_APPROVED_COUNT}&PULL_REQUEST_REVIEWERS_ID=\\${PULL_REQUEST_REVIEWERS_ID}&PULL_REQUEST_REVIEWERS_SLUG=\\${PULL_REQUEST_REVIEWERS_SLUG}&PULL_REQUEST_TITLE=\\${PULL_REQUEST_TITLE}&PULL_REQUEST_TO_BRANCH=\\${PULL_REQUEST_TO_BRANCH}&PULL_REQUEST_TO_HASH=\\${PULL_REQUEST_TO_HASH}&PULL_REQUEST_TO_HTTP_CLONE_URL=\\${PULL_REQUEST_TO_HTTP_CLONE_URL}&PULL_REQUEST_TO_ID=\\${PULL_REQUEST_TO_ID}&PULL_REQUEST_TO_REPO_ID=\\${PULL_REQUEST_TO_REPO_ID}&PULL_REQUEST_TO_REPO_NAME=\\${PULL_REQUEST_TO_REPO_NAME}&PULL_REQUEST_TO_REPO_PROJECT_ID=\\${PULL_REQUEST_TO_REPO_PROJECT_ID}&PULL_REQUEST_TO_REPO_PROJECT_KEY=\\${PULL_REQUEST_TO_REPO_PROJECT_KEY}&PULL_REQUEST_TO_REPO_SLUG=\\${PULL_REQUEST_TO_REPO_SLUG}&PULL_REQUEST_TO_SSH_CLONE_URL=\\${PULL_REQUEST_TO_SSH_CLONE_URL}&PULL_REQUEST_URL=\\${PULL_REQUEST_URL}&PULL_REQUEST_USER_DISPLAY_NAME=\\${PULL_REQUEST_USER_DISPLAY_NAME}&PULL_REQUEST_USER_EMAIL_ADDRESS=\\${PULL_REQUEST_USER_EMAIL_ADDRESS}&PULL_REQUEST_USER_ID=\\${PULL_REQUEST_USER_ID}&PULL_REQUEST_USER_NAME=\\${PULL_REQUEST_USER_NAME}&PULL_REQUEST_USER_SLUG=\\${PULL_REQUEST_USER_SLUG}&PULL_REQUEST_VERSION=\\${PULL_REQUEST_VERSION}")//
    .doesNotContain(EVERYTHING_URL.name());
 }

 private File findReadme(File file) {
  File candidate = new File(file.getAbsolutePath() + "/README.md");
  if (candidate.exists()) {
   return candidate;
  }
  return findReadme(file.getParentFile());
 }

}
