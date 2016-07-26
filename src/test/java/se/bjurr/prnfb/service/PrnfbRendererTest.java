package se.bjurr.prnfb.service;

import static com.google.common.collect.Maps.newHashMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static se.bjurr.prnfb.listener.PrnfbPullRequestAction.APPROVED;
import static se.bjurr.prnfb.service.PrnfbVariable.EVERYTHING_URL;
import static se.bjurr.prnfb.service.PrnfbVariable.INJECTION_URL_VALUE;
import static se.bjurr.prnfb.service.PrnfbVariable.PULL_REQUEST_COMMENT_TEXT;
import static se.bjurr.prnfb.service.PrnfbVariable.PULL_REQUEST_FROM_HASH;
import static se.bjurr.prnfb.service.PrnfbVariable.PULL_REQUEST_MERGE_COMMIT;
import static se.bjurr.prnfb.settings.PrnfbNotificationBuilder.prnfbNotificationBuilder;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import se.bjurr.prnfb.http.ClientKeyStore;
import se.bjurr.prnfb.http.HttpResponse;
import se.bjurr.prnfb.http.Invoker;
import se.bjurr.prnfb.http.UrlInvoker;
import se.bjurr.prnfb.listener.PrnfbPullRequestAction;
import se.bjurr.prnfb.settings.PrnfbNotification;
import se.bjurr.prnfb.settings.ValidationException;

import com.atlassian.bitbucket.pull.PullRequest;
import com.atlassian.bitbucket.pull.PullRequestRef;
import com.atlassian.bitbucket.repository.RepositoryService;
import com.atlassian.bitbucket.server.ApplicationPropertiesService;
import com.atlassian.bitbucket.user.ApplicationUser;
import com.atlassian.bitbucket.user.SecurityService;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;

public class PrnfbRendererTest {

 @Mock
 private ApplicationUser applicationUser;
 private ClientKeyStore clientKeyStore;
 private final Boolean forUrl = false;
 @Mock
 private PullRequestRef fromRef;
 private PrnfbNotification prnfbNotification;
 @Mock
 private ApplicationPropertiesService propertiesService;
 @Mock
 private PullRequest pullRequest;
 private PrnfbPullRequestAction pullRequestAction;
 @Mock
 private RepositoryService repositoryService;
 private SecurityService securityService;
 private final Boolean shouldAcceptAnyCertificate = true;
 private PrnfbRenderer sut;
 private final Map<PrnfbVariable, Supplier<String>> variables = newHashMap();

 @Before
 public void before() throws ValidationException {
  initMocks(this);
  this.prnfbNotification = prnfbNotificationBuilder()//
    .withUrl("http://hej.com")//
    .withTrigger(APPROVED)//
    .build();
  this.sut = new PrnfbRenderer(this.pullRequest, this.pullRequestAction, this.applicationUser, this.repositoryService,
    this.propertiesService, this.prnfbNotification, this.variables, this.securityService);

  when(this.pullRequest.getFromRef())//
    .thenReturn(this.fromRef);
  when(this.pullRequest.getFromRef().getLatestCommit())//
    .thenReturn("latestCommitHash");

  PrnfbVariable.setInvoker(new Invoker() {
   @Override
   public HttpResponse invoke(UrlInvoker toInvoke) {
	HttpResponse response = new HttpResponse(200, "theResponse");
    toInvoke.setResponse(response);
    return response;
   }
  });
 }

 @Test
 public void testThatEverythingCanBeRendered() throws UnsupportedEncodingException {
  String actual = this.sut.getRenderedStringResolved("asd ${" + EVERYTHING_URL.name() + "} asd", this.forUrl, this.sut
    .regexp(EVERYTHING_URL), EVERYTHING_URL.resolve(this.pullRequest, this.pullRequestAction, this.applicationUser,
    this.repositoryService, this.propertiesService, this.prnfbNotification, this.variables, this.clientKeyStore,
    this.shouldAcceptAnyCertificate, this.securityService));

  assertThat(actual)//
    .isEqualTo(
      "asd BUTTON_TRIGGER_TITLE=${BUTTON_TRIGGER_TITLE}&INJECTION_URL_VALUE=${INJECTION_URL_VALUE}&PULL_REQUEST_ACTION=${PULL_REQUEST_ACTION}&PULL_REQUEST_AUTHOR_DISPLAY_NAME=${PULL_REQUEST_AUTHOR_DISPLAY_NAME}&PULL_REQUEST_AUTHOR_EMAIL=${PULL_REQUEST_AUTHOR_EMAIL}&PULL_REQUEST_AUTHOR_ID=${PULL_REQUEST_AUTHOR_ID}&PULL_REQUEST_AUTHOR_NAME=${PULL_REQUEST_AUTHOR_NAME}&PULL_REQUEST_AUTHOR_SLUG=${PULL_REQUEST_AUTHOR_SLUG}&PULL_REQUEST_COMMENT_TEXT=${PULL_REQUEST_COMMENT_TEXT}&PULL_REQUEST_FROM_BRANCH=${PULL_REQUEST_FROM_BRANCH}&PULL_REQUEST_FROM_HASH=${PULL_REQUEST_FROM_HASH}&PULL_REQUEST_FROM_HTTP_CLONE_URL=${PULL_REQUEST_FROM_HTTP_CLONE_URL}&PULL_REQUEST_FROM_ID=${PULL_REQUEST_FROM_ID}&PULL_REQUEST_FROM_REPO_ID=${PULL_REQUEST_FROM_REPO_ID}&PULL_REQUEST_FROM_REPO_NAME=${PULL_REQUEST_FROM_REPO_NAME}&PULL_REQUEST_FROM_REPO_PROJECT_ID=${PULL_REQUEST_FROM_REPO_PROJECT_ID}&PULL_REQUEST_FROM_REPO_PROJECT_KEY=${PULL_REQUEST_FROM_REPO_PROJECT_KEY}&PULL_REQUEST_FROM_REPO_SLUG=${PULL_REQUEST_FROM_REPO_SLUG}&PULL_REQUEST_FROM_SSH_CLONE_URL=${PULL_REQUEST_FROM_SSH_CLONE_URL}&PULL_REQUEST_ID=${PULL_REQUEST_ID}&PULL_REQUEST_MERGE_COMMIT=${PULL_REQUEST_MERGE_COMMIT}&PULL_REQUEST_PARTICIPANTS_APPROVED_COUNT=${PULL_REQUEST_PARTICIPANTS_APPROVED_COUNT}&PULL_REQUEST_REVIEWERS=${PULL_REQUEST_REVIEWERS}&PULL_REQUEST_REVIEWERS_APPROVED_COUNT=${PULL_REQUEST_REVIEWERS_APPROVED_COUNT}&PULL_REQUEST_REVIEWERS_ID=${PULL_REQUEST_REVIEWERS_ID}&PULL_REQUEST_REVIEWERS_SLUG=${PULL_REQUEST_REVIEWERS_SLUG}&PULL_REQUEST_TITLE=${PULL_REQUEST_TITLE}&PULL_REQUEST_TO_BRANCH=${PULL_REQUEST_TO_BRANCH}&PULL_REQUEST_TO_HASH=${PULL_REQUEST_TO_HASH}&PULL_REQUEST_TO_HTTP_CLONE_URL=${PULL_REQUEST_TO_HTTP_CLONE_URL}&PULL_REQUEST_TO_ID=${PULL_REQUEST_TO_ID}&PULL_REQUEST_TO_REPO_ID=${PULL_REQUEST_TO_REPO_ID}&PULL_REQUEST_TO_REPO_NAME=${PULL_REQUEST_TO_REPO_NAME}&PULL_REQUEST_TO_REPO_PROJECT_ID=${PULL_REQUEST_TO_REPO_PROJECT_ID}&PULL_REQUEST_TO_REPO_PROJECT_KEY=${PULL_REQUEST_TO_REPO_PROJECT_KEY}&PULL_REQUEST_TO_REPO_SLUG=${PULL_REQUEST_TO_REPO_SLUG}&PULL_REQUEST_TO_SSH_CLONE_URL=${PULL_REQUEST_TO_SSH_CLONE_URL}&PULL_REQUEST_URL=${PULL_REQUEST_URL}&PULL_REQUEST_USER_DISPLAY_NAME=${PULL_REQUEST_USER_DISPLAY_NAME}&PULL_REQUEST_USER_EMAIL_ADDRESS=${PULL_REQUEST_USER_EMAIL_ADDRESS}&PULL_REQUEST_USER_ID=${PULL_REQUEST_USER_ID}&PULL_REQUEST_USER_NAME=${PULL_REQUEST_USER_NAME}&PULL_REQUEST_USER_SLUG=${PULL_REQUEST_USER_SLUG}&PULL_REQUEST_VERSION=${PULL_REQUEST_VERSION} asd");
 }

 @Test
 public void testThatInjectionUrlCanBeRendered() throws ValidationException {
  this.prnfbNotification = prnfbNotificationBuilder(this.prnfbNotification)//
    .withInjectionUrl("http://getValueFrom.com/")//
    .withTrigger(APPROVED)//
    .build();
  this.sut = new PrnfbRenderer(this.pullRequest, this.pullRequestAction, this.applicationUser, this.repositoryService,
    this.propertiesService, this.prnfbNotification, this.variables, this.securityService);

  String actual = this.sut.render("my ${" + INJECTION_URL_VALUE + "} string", this.forUrl, this.clientKeyStore,
    this.shouldAcceptAnyCertificate);
  assertThat(actual)//
    .isEqualTo("my theResponse string");

 }

 @Test
 public void testThatMergeCommitCanBeRenderedIfExists() {
  this.variables.put(PULL_REQUEST_MERGE_COMMIT, Suppliers.ofInstance("mergeHash"));
  String actual = this.sut.render("my ${" + PULL_REQUEST_MERGE_COMMIT + "} string", this.forUrl, this.clientKeyStore,
    this.shouldAcceptAnyCertificate);
  assertThat(actual)//
    .isEqualTo("my mergeHash string");
 }

 @Test
 public void testThatMergeCommitCanBeRenderedIfNotExists() {
  String actual = this.sut.render("my ${" + PULL_REQUEST_MERGE_COMMIT + "} string", this.forUrl, this.clientKeyStore,
    this.shouldAcceptAnyCertificate);
  assertThat(actual)//
    .isEqualTo("my  string");
 }

 @Test
 public void testThatStringCanBeRendered() {
  String actual = this.sut.render("my ${" + PULL_REQUEST_FROM_HASH + "} string", this.forUrl, this.clientKeyStore,
    this.shouldAcceptAnyCertificate);
  assertThat(actual)//
    .isEqualTo("my latestCommitHash string");
 }

 @Test
 public void testThatStringCanBeRenderedForUrl() {
  this.variables.put(PULL_REQUEST_COMMENT_TEXT, Suppliers.ofInstance("the comment"));
  String actual = this.sut.render("my ${" + PULL_REQUEST_COMMENT_TEXT + "} string", true, this.clientKeyStore,
    this.shouldAcceptAnyCertificate);
  assertThat(actual)//
    .isEqualTo("my the+comment string");
 }
}
