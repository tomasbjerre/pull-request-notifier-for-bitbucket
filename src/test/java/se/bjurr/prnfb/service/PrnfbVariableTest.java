package se.bjurr.prnfb.service;

import static com.google.common.base.Charsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static se.bjurr.prnfb.service.PrnfbVariable.EVERYTHING_URL;
import static se.bjurr.prnfb.service.PrnfbVariable.PULL_REQUEST_DESCRIPTION;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;

import org.junit.Test;

import com.atlassian.bitbucket.pull.PullRequest;
import com.atlassian.bitbucket.repository.RepositoryService;
import com.atlassian.bitbucket.server.ApplicationPropertiesService;
import com.atlassian.bitbucket.user.ApplicationUser;
import com.atlassian.bitbucket.user.SecurityService;
import com.google.common.base.Supplier;
import com.google.common.io.Files;
import com.google.common.io.Resources;

import se.bjurr.prnfb.http.ClientKeyStore;
import se.bjurr.prnfb.listener.PrnfbPullRequestAction;
import se.bjurr.prnfb.settings.PrnfbNotification;

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

  private File findReadme(File file) {
    File candidate = new File(file.getAbsolutePath() + "/README.md");
    if (candidate.exists()) {
      return candidate;
    }
    return findReadme(file.getParentFile());
  }

  @Test
  public void testThatAdminAndReadmeContainsVariables() throws IOException, URISyntaxException {
    URL adminResource = Resources.getResource("admin.vm");
    String adminPageContent = Resources.toString(adminResource, UTF_8);
    File readme = findReadme(new File(adminResource.toURI()));
    String readmeContent = Files.toString(readme, UTF_8);
    for (PrnfbVariable variable : PrnfbVariable.values()) {
      assertThat(adminPageContent) //
          .as("admin.vm should include " + variable.name() + "\nWas:" + adminPageContent) //
          .contains(variable.name());
      assertThat(readmeContent) //
          .as("README.md should include " + variable.name() + "\nWas:" + readmeContent) //
          .contains(variable.name());
    }
  }

  @Test
  public void testThatEverythingVariableIsResolvedToEveryOtherVariable() {
    String actual =
        EVERYTHING_URL.resolve(
            pullRequest,
            pullRequestAction,
            applicationUser,
            repositoryService,
            propertiesService,
            prnfbNotification,
            variables,
            clientKeyStore,
            shouldAcceptAnyCertificate,
            securityService);
    for (PrnfbVariable v : PrnfbVariable.values()) {
      if (v != EVERYTHING_URL && v != PULL_REQUEST_DESCRIPTION) {
        assertThat(actual) //
            .containsOnlyOnce(v.name() + "=\\${" + v.name() + "}") //
            .doesNotContain(EVERYTHING_URL.name());
      }
    }
  }
}
