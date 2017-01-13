package se.bjurr.prnfb.service;

import static com.google.common.collect.Maps.newHashMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static se.bjurr.prnfb.listener.PrnfbPullRequestAction.APPROVED;
import static se.bjurr.prnfb.service.PrnfbRenderer.ENCODE_FOR.NONE;
import static se.bjurr.prnfb.service.PrnfbVariable.EVERYTHING_URL;
import static se.bjurr.prnfb.service.PrnfbVariable.INJECTION_URL_VALUE;
import static se.bjurr.prnfb.service.PrnfbVariable.PULL_REQUEST_AUTHOR_EMAIL;
import static se.bjurr.prnfb.service.PrnfbVariable.PULL_REQUEST_COMMENT_TEXT;
import static se.bjurr.prnfb.service.PrnfbVariable.PULL_REQUEST_FROM_HASH;
import static se.bjurr.prnfb.service.PrnfbVariable.PULL_REQUEST_MERGE_COMMIT;
import static se.bjurr.prnfb.settings.PrnfbNotificationBuilder.prnfbNotificationBuilder;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import com.atlassian.bitbucket.pull.PullRequest;
import com.atlassian.bitbucket.pull.PullRequestParticipant;
import com.atlassian.bitbucket.pull.PullRequestRef;
import com.atlassian.bitbucket.repository.RepositoryService;
import com.atlassian.bitbucket.server.ApplicationPropertiesService;
import com.atlassian.bitbucket.user.ApplicationUser;
import com.atlassian.bitbucket.user.SecurityService;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;

import se.bjurr.prnfb.http.ClientKeyStore;
import se.bjurr.prnfb.http.HttpResponse;
import se.bjurr.prnfb.http.Invoker;
import se.bjurr.prnfb.http.UrlInvoker;
import se.bjurr.prnfb.listener.PrnfbPullRequestAction;
import se.bjurr.prnfb.service.PrnfbRenderer.ENCODE_FOR;
import se.bjurr.prnfb.settings.PrnfbNotification;
import se.bjurr.prnfb.settings.ValidationException;

public class PrnfbRendererTest {

  @Mock private ApplicationUser applicationUser;
  @Mock private PullRequestParticipant author;
  private ClientKeyStore clientKeyStore;
  private final ENCODE_FOR encodeFor = NONE;
  @Mock private PullRequestRef fromRef;
  private PrnfbNotification prnfbNotification;
  @Mock private ApplicationPropertiesService propertiesService;
  @Mock private PullRequest pullRequest;
  private PrnfbPullRequestAction pullRequestAction;
  @Mock private RepositoryService repositoryService;
  private SecurityService securityService;
  private final Boolean shouldAcceptAnyCertificate = true;
  private PrnfbRenderer sut;
  @Mock private ApplicationUser user;
  private final Map<PrnfbVariable, Supplier<String>> variables = newHashMap();

  @Before
  public void before() throws ValidationException {
    initMocks(this);
    prnfbNotification =
        prnfbNotificationBuilder() //
            .withUrl("http://hej.com") //
            .withTrigger(APPROVED) //
            .build();
    sut =
        new PrnfbRenderer(
            pullRequest,
            pullRequestAction,
            applicationUser,
            repositoryService,
            propertiesService,
            prnfbNotification,
            variables,
            securityService);

    when(pullRequest.getFromRef()) //
        .thenReturn(fromRef);
    when(pullRequest.getFromRef().getLatestCommit()) //
        .thenReturn("latestCommitHash");

    PrnfbVariable.setInvoker(
        new Invoker() {
          @Override
          public HttpResponse invoke(UrlInvoker toInvoke) {
            HttpResponse response = null;
            try {
              response = new HttpResponse(new URI("http://fake.om/"), 200, "theResponse");
            } catch (URISyntaxException e) {
              e.printStackTrace();
            }
            toInvoke.setResponse(response);
            return response;
          }
        });
  }

  @Test
  public void testThatEverythingCanBeRendered() throws UnsupportedEncodingException {
    String actual =
        sut.getRenderedStringResolved(
            "asd ${" + EVERYTHING_URL.name() + "} asd",
            encodeFor,
            sut.regexp(EVERYTHING_URL),
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
                securityService));

    assertThat(actual) //
        .isEqualTo(
            "asd BUTTON_FORM_DATA=${BUTTON_FORM_DATA}&BUTTON_TRIGGER_TITLE=${BUTTON_TRIGGER_TITLE}&INJECTION_URL_VALUE=${INJECTION_URL_VALUE}&PULL_REQUEST_ACTION=${PULL_REQUEST_ACTION}&PULL_REQUEST_AUTHOR_DISPLAY_NAME=${PULL_REQUEST_AUTHOR_DISPLAY_NAME}&PULL_REQUEST_AUTHOR_EMAIL=${PULL_REQUEST_AUTHOR_EMAIL}&PULL_REQUEST_AUTHOR_ID=${PULL_REQUEST_AUTHOR_ID}&PULL_REQUEST_AUTHOR_NAME=${PULL_REQUEST_AUTHOR_NAME}&PULL_REQUEST_AUTHOR_SLUG=${PULL_REQUEST_AUTHOR_SLUG}&PULL_REQUEST_COMMENT_ACTION=${PULL_REQUEST_COMMENT_ACTION}&PULL_REQUEST_COMMENT_TEXT=${PULL_REQUEST_COMMENT_TEXT}&PULL_REQUEST_FROM_BRANCH=${PULL_REQUEST_FROM_BRANCH}&PULL_REQUEST_FROM_HASH=${PULL_REQUEST_FROM_HASH}&PULL_REQUEST_FROM_HTTP_CLONE_URL=${PULL_REQUEST_FROM_HTTP_CLONE_URL}&PULL_REQUEST_FROM_ID=${PULL_REQUEST_FROM_ID}&PULL_REQUEST_FROM_REPO_ID=${PULL_REQUEST_FROM_REPO_ID}&PULL_REQUEST_FROM_REPO_NAME=${PULL_REQUEST_FROM_REPO_NAME}&PULL_REQUEST_FROM_REPO_PROJECT_ID=${PULL_REQUEST_FROM_REPO_PROJECT_ID}&PULL_REQUEST_FROM_REPO_PROJECT_KEY=${PULL_REQUEST_FROM_REPO_PROJECT_KEY}&PULL_REQUEST_FROM_REPO_SLUG=${PULL_REQUEST_FROM_REPO_SLUG}&PULL_REQUEST_FROM_SSH_CLONE_URL=${PULL_REQUEST_FROM_SSH_CLONE_URL}&PULL_REQUEST_ID=${PULL_REQUEST_ID}&PULL_REQUEST_MERGE_COMMIT=${PULL_REQUEST_MERGE_COMMIT}&PULL_REQUEST_PARTICIPANTS_APPROVED_COUNT=${PULL_REQUEST_PARTICIPANTS_APPROVED_COUNT}&PULL_REQUEST_PARTICIPANTS_EMAIL=${PULL_REQUEST_PARTICIPANTS_EMAIL}&PULL_REQUEST_REVIEWERS=${PULL_REQUEST_REVIEWERS}&PULL_REQUEST_REVIEWERS_APPROVED_COUNT=${PULL_REQUEST_REVIEWERS_APPROVED_COUNT}&PULL_REQUEST_REVIEWERS_EMAIL=${PULL_REQUEST_REVIEWERS_EMAIL}&PULL_REQUEST_REVIEWERS_ID=${PULL_REQUEST_REVIEWERS_ID}&PULL_REQUEST_REVIEWERS_NEEDS_WORK_COUNT=${PULL_REQUEST_REVIEWERS_NEEDS_WORK_COUNT}&PULL_REQUEST_REVIEWERS_SLUG=${PULL_REQUEST_REVIEWERS_SLUG}&PULL_REQUEST_REVIEWERS_UNAPPROVED_COUNT=${PULL_REQUEST_REVIEWERS_UNAPPROVED_COUNT}&PULL_REQUEST_STATE=${PULL_REQUEST_STATE}&PULL_REQUEST_TITLE=${PULL_REQUEST_TITLE}&PULL_REQUEST_TO_BRANCH=${PULL_REQUEST_TO_BRANCH}&PULL_REQUEST_TO_HASH=${PULL_REQUEST_TO_HASH}&PULL_REQUEST_TO_HTTP_CLONE_URL=${PULL_REQUEST_TO_HTTP_CLONE_URL}&PULL_REQUEST_TO_ID=${PULL_REQUEST_TO_ID}&PULL_REQUEST_TO_REPO_ID=${PULL_REQUEST_TO_REPO_ID}&PULL_REQUEST_TO_REPO_NAME=${PULL_REQUEST_TO_REPO_NAME}&PULL_REQUEST_TO_REPO_PROJECT_ID=${PULL_REQUEST_TO_REPO_PROJECT_ID}&PULL_REQUEST_TO_REPO_PROJECT_KEY=${PULL_REQUEST_TO_REPO_PROJECT_KEY}&PULL_REQUEST_TO_REPO_SLUG=${PULL_REQUEST_TO_REPO_SLUG}&PULL_REQUEST_TO_SSH_CLONE_URL=${PULL_REQUEST_TO_SSH_CLONE_URL}&PULL_REQUEST_URL=${PULL_REQUEST_URL}&PULL_REQUEST_USER_DISPLAY_NAME=${PULL_REQUEST_USER_DISPLAY_NAME}&PULL_REQUEST_USER_EMAIL_ADDRESS=${PULL_REQUEST_USER_EMAIL_ADDRESS}&PULL_REQUEST_USER_ID=${PULL_REQUEST_USER_ID}&PULL_REQUEST_USER_NAME=${PULL_REQUEST_USER_NAME}&PULL_REQUEST_USER_SLUG=${PULL_REQUEST_USER_SLUG}&PULL_REQUEST_VERSION=${PULL_REQUEST_VERSION} asd");
  }

  @Test
  public void testThatIfAVariableChrashesOnResolveItWillBeEmpty() {
    String actual =
        sut.render(
            "my ${" + PULL_REQUEST_AUTHOR_EMAIL + "} string",
            encodeFor,
            clientKeyStore,
            shouldAcceptAnyCertificate);
    assertThat(actual) //
        .isEqualTo("my  string");
  }

  @Test
  public void testThatIfAVariableChrashesResolvedToNullOnResolveItWillBeEmpty() {
    when(pullRequest.getAuthor()) //
        .thenReturn(author);
    when(pullRequest.getAuthor().getUser()) //
        .thenReturn(user);
    when(pullRequest.getAuthor().getUser().getEmailAddress()) //
        .thenReturn(null);
    String actual =
        sut.render(
            "my ${" + PULL_REQUEST_AUTHOR_EMAIL + "} string",
            encodeFor,
            clientKeyStore,
            shouldAcceptAnyCertificate);
    assertThat(actual) //
        .isEqualTo("my  string");
  }

  @Test
  public void testThatInjectionUrlCanBeRendered() throws ValidationException {
    prnfbNotification =
        prnfbNotificationBuilder(prnfbNotification) //
            .withInjectionUrl("http://getValueFrom.com/") //
            .withTrigger(APPROVED) //
            .build();
    sut =
        new PrnfbRenderer(
            pullRequest,
            pullRequestAction,
            applicationUser,
            repositoryService,
            propertiesService,
            prnfbNotification,
            variables,
            securityService);

    String actual =
        sut.render(
            "my ${" + INJECTION_URL_VALUE + "} string",
            encodeFor,
            clientKeyStore,
            shouldAcceptAnyCertificate);
    assertThat(actual) //
        .isEqualTo("my theResponse string");
  }

  @Test
  public void testThatMergeCommitCanBeRenderedIfExists() {
    variables.put(PULL_REQUEST_MERGE_COMMIT, Suppliers.ofInstance("mergeHash"));
    String actual =
        sut.render(
            "my ${" + PULL_REQUEST_MERGE_COMMIT + "} string",
            encodeFor,
            clientKeyStore,
            shouldAcceptAnyCertificate);
    assertThat(actual) //
        .isEqualTo("my mergeHash string");
  }

  @Test
  public void testThatMergeCommitCanBeRenderedIfNotExists() {
    String actual =
        sut.render(
            "my ${" + PULL_REQUEST_MERGE_COMMIT + "} string",
            encodeFor,
            clientKeyStore,
            shouldAcceptAnyCertificate);
    assertThat(actual) //
        .isEqualTo("my  string");
  }

  @Test
  public void testThatStringCanBeRendered() {
    String actual =
        sut.render(
            "my ${" + PULL_REQUEST_FROM_HASH + "} string",
            encodeFor,
            clientKeyStore,
            shouldAcceptAnyCertificate);
    assertThat(actual) //
        .isEqualTo("my latestCommitHash string");
  }

  @Test
  public void testThatStringCanBeRenderedForUrl() {
    variables.put(PULL_REQUEST_COMMENT_TEXT, Suppliers.ofInstance("the comment"));
    String actual =
        sut.render(
            "my ${" + PULL_REQUEST_COMMENT_TEXT + "} string",
            ENCODE_FOR.URL,
            clientKeyStore,
            shouldAcceptAnyCertificate);
    assertThat(actual) //
        .isEqualTo("my the+comment string");
  }
}
