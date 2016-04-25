package se.bjurr.prnfb.service;

import static com.google.common.collect.Maps.newHashMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
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
 private PullRequest pullRequest;
 private PrnfbPullRequestAction pullRequestAction;
 @Mock
 private ApplicationUser applicationUser;
 @Mock
 private RepositoryService repositoryService;
 @Mock
 private ApplicationPropertiesService propertiesService;
 private PrnfbNotification prnfbNotification;
 private final Map<PrnfbVariable, Supplier<String>> variables = newHashMap();
 private PrnfbRenderer sut;
 private ClientKeyStore clientKeyStore;
 private final Boolean forUrl = false;
 private final Boolean shouldAcceptAnyCertificate = true;
 @Mock
 private PullRequestRef fromRef;

 @Before
 public void before() throws ValidationException {
  initMocks(this);
  prnfbNotification = prnfbNotificationBuilder()//
    .withUrl("http://hej.com")//
    .build();
  sut = new PrnfbRenderer(pullRequest, pullRequestAction, applicationUser, repositoryService, propertiesService,
    prnfbNotification, variables);

  when(pullRequest.getFromRef())//
    .thenReturn(fromRef);
  when(pullRequest.getFromRef().getLatestCommit())//
    .thenReturn("latestCommitHash");

  PrnfbVariable.setInvoker(new Invoker() {
   @Override
   public void invoke(UrlInvoker toInvoke) {
    toInvoke.setResponseString("theResponse");
   }

  });
 }

 @Test
 public void testThatStringCanBeRendered() {
  String actual = sut.render("my ${" + PULL_REQUEST_FROM_HASH + "} string", forUrl, clientKeyStore,
    shouldAcceptAnyCertificate);
  assertThat(actual)//
    .isEqualTo("my latestCommitHash string");
 }

 @Test
 public void testThatStringCanBeRenderedForUrl() {
  variables.put(PULL_REQUEST_COMMENT_TEXT, Suppliers.ofInstance("the comment"));
  String actual = sut.render("my ${" + PULL_REQUEST_COMMENT_TEXT + "} string", true, clientKeyStore,
    shouldAcceptAnyCertificate);
  assertThat(actual)//
    .isEqualTo("my the+comment string");
 }

 @Test
 public void testThatMergeCommitCanBeRenderedIfExists() {
  variables.put(PULL_REQUEST_MERGE_COMMIT, Suppliers.ofInstance("mergeHash"));
  String actual = sut.render("my ${" + PULL_REQUEST_MERGE_COMMIT + "} string", forUrl, clientKeyStore,
    shouldAcceptAnyCertificate);
  assertThat(actual)//
    .isEqualTo("my mergeHash string");
 }

 @Test
 public void testThatMergeCommitCanBeRenderedIfNotExists() {
  String actual = sut.render("my ${" + PULL_REQUEST_MERGE_COMMIT + "} string", forUrl, clientKeyStore,
    shouldAcceptAnyCertificate);
  assertThat(actual)//
    .isEqualTo("my  string");
 }

 @Test
 public void testThatInjectionUrlCanBeRendered() throws ValidationException {
  prnfbNotification = prnfbNotificationBuilder(prnfbNotification)//
    .withInjectionUrl("http://getValueFrom.com/")//
    .build();
  sut = new PrnfbRenderer(pullRequest, pullRequestAction, applicationUser, repositoryService, propertiesService,
    prnfbNotification, variables);

  String actual = sut.render("my ${" + INJECTION_URL_VALUE + "} string", forUrl, clientKeyStore,
    shouldAcceptAnyCertificate);
  assertThat(actual)//
    .isEqualTo("my theResponse string");

 }
}
