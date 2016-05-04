package se.bjurr.prnfb.service;

import static com.google.common.collect.Maps.newHashMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static se.bjurr.prnfb.listener.PrnfbPullRequestAction.APPROVED;
import static se.bjurr.prnfb.service.PrnfbVariable.INJECTION_URL_VALUE;
import static se.bjurr.prnfb.service.PrnfbVariable.PULL_REQUEST_COMMENT_TEXT;
import static se.bjurr.prnfb.service.PrnfbVariable.PULL_REQUEST_FROM_HASH;
import static se.bjurr.prnfb.service.PrnfbVariable.PULL_REQUEST_MERGE_COMMIT;
import static se.bjurr.prnfb.settings.PrnfbNotificationBuilder.prnfbNotificationBuilder;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import se.bjurr.prnfb.http.ClientKeyStore;
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
    this.propertiesService, this.prnfbNotification, this.variables);

  when(this.pullRequest.getFromRef())//
    .thenReturn(this.fromRef);
  when(this.pullRequest.getFromRef().getLatestCommit())//
    .thenReturn("latestCommitHash");

  PrnfbVariable.setInvoker(new Invoker() {
   @Override
   public void invoke(UrlInvoker toInvoke) {
    toInvoke.setResponseString("theResponse");
   }

  });
 }

 @Test
 public void testThatInjectionUrlCanBeRendered() throws ValidationException {
  this.prnfbNotification = prnfbNotificationBuilder(this.prnfbNotification)//
    .withInjectionUrl("http://getValueFrom.com/")//
    .withTrigger(APPROVED)//
    .build();
  this.sut = new PrnfbRenderer(this.pullRequest, this.pullRequestAction, this.applicationUser, this.repositoryService,
    this.propertiesService, this.prnfbNotification, this.variables);

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
