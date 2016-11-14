package se.bjurr.prnfb.listener;

import static com.atlassian.bitbucket.pull.PullRequestAction.OPENED;
import static com.atlassian.bitbucket.pull.PullRequestState.DECLINED;
import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static se.bjurr.prnfb.listener.PrnfbPullRequestAction.APPROVED;
import static se.bjurr.prnfb.listener.PrnfbPullRequestAction.RESCOPED_FROM;
import static se.bjurr.prnfb.listener.PrnfbPullRequestEventListener.setInvoker;
import static se.bjurr.prnfb.service.PrnfbVariable.PULL_REQUEST_COMMENT_TEXT;
import static se.bjurr.prnfb.service.PrnfbVariable.PULL_REQUEST_MERGE_COMMIT;
import static se.bjurr.prnfb.settings.PrnfbNotificationBuilder.prnfbNotificationBuilder;
import static se.bjurr.prnfb.settings.PrnfbSettingsDataBuilder.prnfbSettingsDataBuilder;
import static se.bjurr.prnfb.settings.TRIGGER_IF_MERGE.ALWAYS;
import static se.bjurr.prnfb.settings.TRIGGER_IF_MERGE.CONFLICTING;
import static se.bjurr.prnfb.settings.TRIGGER_IF_MERGE.NOT_CONFLICTING;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import se.bjurr.prnfb.http.ClientKeyStore;
import se.bjurr.prnfb.http.HttpResponse;
import se.bjurr.prnfb.http.Invoker;
import se.bjurr.prnfb.http.UrlInvoker;
import se.bjurr.prnfb.service.PrnfbRenderer;
import se.bjurr.prnfb.service.PrnfbRendererFactory;
import se.bjurr.prnfb.service.PrnfbVariable;
import se.bjurr.prnfb.service.SettingsService;
import se.bjurr.prnfb.settings.PrnfbNotification;
import se.bjurr.prnfb.settings.PrnfbSettingsData;
import se.bjurr.prnfb.settings.ValidationException;

import com.atlassian.bitbucket.comment.Comment;
import com.atlassian.bitbucket.commit.MinimalCommit;
import com.atlassian.bitbucket.event.pull.PullRequestCommentAddedEvent;
import com.atlassian.bitbucket.event.pull.PullRequestEvent;
import com.atlassian.bitbucket.event.pull.PullRequestMergedEvent;
import com.atlassian.bitbucket.project.Project;
import com.atlassian.bitbucket.pull.PullRequest;
import com.atlassian.bitbucket.pull.PullRequestRef;
import com.atlassian.bitbucket.pull.PullRequestService;
import com.atlassian.bitbucket.repository.Repository;
import com.google.common.base.Function;
import com.google.common.base.Supplier;

public class PrnfbPullRequestEventListenerTest {

 private final ClientKeyStore clientKeyStore = null;
 private final ExecutorService executorService = new FakeExecutorService();
 @Mock
 private PullRequestRef fromRef;
 private final List<UrlInvoker> invokedUrls = newArrayList();
 private PrnfbNotification notification1;
 private PrnfbNotification notification2;
 private PrnfbSettingsData pluginSettingsData;
 @Mock
 private PrnfbRendererFactory prnfbRendererFactory;
 @Mock
 private PullRequest pullRequest;
 @Mock
 private PullRequestEvent pullRequestOpenedEvent;
 @Mock
 private PullRequestService pullRequestService;
 @Mock
 private PrnfbRenderer renderer;
 @Mock
 private SettingsService settingsService;
 private Boolean shouldAcceptAnyCertificate;
 private PrnfbPullRequestEventListener sut;
 @Mock
 private PullRequestRef toRef;

 @Before
 public void before() throws ValidationException {
  initMocks(this);
  this.sut = new PrnfbPullRequestEventListener(this.prnfbRendererFactory, this.pullRequestService, this.executorService,
    this.settingsService);
  setInvoker(new Invoker() {
   @Override
   public HttpResponse invoke(UrlInvoker urlInvoker) {
    HttpResponse response = null;
    try {
     response = new HttpResponse(new URI("http://fake.com/"), 200, "");
    } catch (URISyntaxException e) {
     e.printStackTrace();
    }
    urlInvoker.setResponse(response);
    PrnfbPullRequestEventListenerTest.this.invokedUrls.add(urlInvoker);
    return response;
   }
  });

  when(this.pullRequest.getFromRef())//
    .thenReturn(this.fromRef);
  when(this.pullRequest.getFromRef().getLatestCommit())//
    .thenReturn("latestCFrom");
  when(this.pullRequest.getFromRef().getId())//
    .thenReturn("IFrom");

  when(this.pullRequest.getToRef())//
    .thenReturn(this.toRef);
  when(this.pullRequest.getToRef().getLatestCommit())//
    .thenReturn("latestCTo");
  when(this.pullRequest.getToRef().getId())//
    .thenReturn("ITo");

  when(this.pullRequestOpenedEvent.getPullRequest())//
    .thenReturn(this.pullRequest);
  when(this.pullRequestOpenedEvent.getPullRequest().isClosed())//
    .thenReturn(false);
  when(this.pullRequestOpenedEvent.getAction())//
    .thenReturn(OPENED);

  this.pluginSettingsData = prnfbSettingsDataBuilder()//
    .build();
  when(this.settingsService.getPrnfbSettingsData())//
    .thenReturn(this.pluginSettingsData);

  this.notification1 = prnfbNotificationBuilder()//
    .withUrl("http://not1.com/")//
    .withTrigger(PrnfbPullRequestAction.OPENED)//
    .build();
  this.notification2 = prnfbNotificationBuilder(this.notification1)//
    .withUrl("http://not2.com/")//
    .build();
  List<PrnfbNotification> notifications = newArrayList(this.notification1, this.notification2);
  when(this.settingsService.getNotifications())//
    .thenReturn(notifications);

  when(this.prnfbRendererFactory.create(any(), any(), any(), any(), any()))//
    .thenReturn(this.renderer);
  when(this.renderer.render(any(), any(), any(), any()))//
    .thenAnswer(new Answer<String>() {
     @Override
     public String answer(InvocationOnMock invocation) throws Throwable {
      return (String) invocation.getArguments()[0];
     }
    });
 }

 @Test
 public void testThatCommentOnClosedPRIsIgnored() {
  when(this.pullRequest.isClosed())//
    .thenReturn(true);
  PullRequestCommentAddedEvent pullRequestEvent = mock(PullRequestCommentAddedEvent.class);
  when(pullRequestEvent.getPullRequest())//
    .thenReturn(this.pullRequest);

  this.sut.handleEventAsync(pullRequestEvent);

  assertThat(this.invokedUrls)//
    .isEmpty();
 }

 @Test
 public void testThatHeaderCanContainVariables() {
 }

 @Test
 public void testThatNotifiationIsNotTriggeredByActionIfAFilterIsNotMatching() throws ValidationException {
  PrnfbNotification notification = prnfbNotificationBuilder()//
    .withTrigger(RESCOPED_FROM)//
    .withUrl("http://hej.com")//
    .withFilterRegexp("^abc$")//
    .withFilterString("bc")//
    .build();
  PrnfbPullRequestAction pullRequestAction = RESCOPED_FROM;

  boolean actual = this.sut.isNotificationTriggeredByAction(notification, pullRequestAction, this.renderer,
    this.pullRequest, this.clientKeyStore, this.shouldAcceptAnyCertificate);

  assertThat(actual)//
    .isFalse();
 }

 @Test
 public void testThatNotifiationIsNotTriggeredByActionIfOnlyBuildingMergingAndItIsConflicting() {
  assertThat(this.sut.ignoreBecauseOfConflicting(ALWAYS, false))//
    .isFalse();
  assertThat(this.sut.ignoreBecauseOfConflicting(CONFLICTING, false))//
    .isTrue();
  assertThat(this.sut.ignoreBecauseOfConflicting(NOT_CONFLICTING, false))//
    .isFalse();

  assertThat(this.sut.ignoreBecauseOfConflicting(ALWAYS, true))//
    .isFalse();
  assertThat(this.sut.ignoreBecauseOfConflicting(CONFLICTING, true))//
    .isFalse();
  assertThat(this.sut.ignoreBecauseOfConflicting(NOT_CONFLICTING, true))//
    .isTrue();
 }

 @Test
 public void testThatNotifiationIsNotTriggeredByActionIfProjectNotSame() throws ValidationException {
  PrnfbNotification notification = prnfbNotificationBuilder()//
    .withTrigger(RESCOPED_FROM)//
    .withUrl("http://hej.com")//
    .withProjectKey("pk")//
    .build();
  PrnfbPullRequestAction pullRequestAction = RESCOPED_FROM;
  Repository repository = mock(Repository.class);
  when(this.toRef.getRepository())//
    .thenReturn(repository);
  Project project = mock(Project.class);
  when(repository.getProject())//
    .thenReturn(project);
  when(project.getKey())//
    .thenReturn("pk2");

  boolean actual = this.sut.isNotificationTriggeredByAction(notification, pullRequestAction, this.renderer,
    this.pullRequest, this.clientKeyStore, this.shouldAcceptAnyCertificate);

  assertThat(actual)//
    .isFalse();
 }

 @Test
 public void testThatNotifiationIsNotTriggeredByActionIfRepositoryNotSame() throws ValidationException {
  PrnfbNotification notification = prnfbNotificationBuilder()//
    .withTrigger(RESCOPED_FROM)//
    .withUrl("http://hej.com")//
    .withRepositorySlug("repositorySlug123")//
    .build();
  PrnfbPullRequestAction pullRequestAction = RESCOPED_FROM;
  Repository repository = mock(Repository.class);
  when(this.toRef.getRepository())//
    .thenReturn(repository);
  when(repository.getSlug())//
    .thenReturn("asdasd");

  boolean actual = this.sut.isNotificationTriggeredByAction(notification, pullRequestAction, this.renderer,
    this.pullRequest, this.clientKeyStore, this.shouldAcceptAnyCertificate);

  assertThat(actual)//
    .isFalse();
 }

 @Test
 public void testThatNotifiationIsNotTriggeredByActionIfThatActionIsATriggerButStateIgnored()
   throws ValidationException {
  PrnfbNotification notification = prnfbNotificationBuilder()//
    .withTrigger(RESCOPED_FROM)//
    .withUrl("http://hej.com")//
    .setTriggerIgnoreState(newArrayList(DECLINED))//
    .build();
  PrnfbPullRequestAction pullRequestAction = RESCOPED_FROM;
  when(this.pullRequest.getState())//
    .thenReturn(DECLINED);

  boolean actual = this.sut.isNotificationTriggeredByAction(notification, pullRequestAction, this.renderer,
    this.pullRequest, this.clientKeyStore, this.shouldAcceptAnyCertificate);

  assertThat(actual)//
    .isFalse();
 }

 @Test
 public void testThatNotifiationIsNotTriggeredByActionIfThatActionIsNotATrigger() throws ValidationException {
  PrnfbNotification notification = prnfbNotificationBuilder()//
    .withTrigger(RESCOPED_FROM)//
    .withUrl("http://hej.com")//
    .build();
  PrnfbPullRequestAction pullRequestAction = APPROVED;

  boolean actual = this.sut.isNotificationTriggeredByAction(notification, pullRequestAction, this.renderer,
    this.pullRequest, this.clientKeyStore, this.shouldAcceptAnyCertificate);

  assertThat(actual)//
    .isFalse();
 }

 @Test
 public void testThatNotifiationIsTriggeredByActionIfAFilterIsMatching() throws ValidationException {
  PrnfbNotification notification = prnfbNotificationBuilder()//
    .withTrigger(RESCOPED_FROM)//
    .withUrl("http://hej.com")//
    .withFilterRegexp("^abc$")//
    .withFilterString("abc")//
    .build();
  PrnfbPullRequestAction pullRequestAction = RESCOPED_FROM;

  boolean actual = this.sut.isNotificationTriggeredByAction(notification, pullRequestAction, this.renderer,
    this.pullRequest, this.clientKeyStore, this.shouldAcceptAnyCertificate);

  assertThat(actual)//
    .isTrue();
 }

 @Test
 public void testThatNotifiationIsTriggeredByActionIfProjectSame() throws ValidationException {
  PrnfbNotification notification = prnfbNotificationBuilder()//
    .withTrigger(RESCOPED_FROM)//
    .withUrl("http://hej.com")//
    .withProjectKey("pk2")//
    .build();
  PrnfbPullRequestAction pullRequestAction = RESCOPED_FROM;
  Repository repository = mock(Repository.class);
  when(this.toRef.getRepository())//
    .thenReturn(repository);
  Project project = mock(Project.class);
  when(repository.getProject())//
    .thenReturn(project);
  when(project.getKey())//
    .thenReturn("pk2");

  boolean actual = this.sut.isNotificationTriggeredByAction(notification, pullRequestAction, this.renderer,
    this.pullRequest, this.clientKeyStore, this.shouldAcceptAnyCertificate);

  assertThat(actual)//
    .isTrue();
 }

 @Test
 public void testThatNotifiationIsTriggeredByActionIfRepositorySame() throws ValidationException {
  PrnfbNotification notification = prnfbNotificationBuilder()//
    .withTrigger(RESCOPED_FROM)//
    .withUrl("http://hej.com")//
    .withRepositorySlug("repositorySlug123")//
    .build();
  PrnfbPullRequestAction pullRequestAction = RESCOPED_FROM;
  Repository repository = mock(Repository.class);
  when(this.toRef.getRepository())//
    .thenReturn(repository);
  when(repository.getSlug())//
    .thenReturn("repositorySlug123");

  boolean actual = this.sut.isNotificationTriggeredByAction(notification, pullRequestAction, this.renderer,
    this.pullRequest, this.clientKeyStore, this.shouldAcceptAnyCertificate);

  assertThat(actual)//
    .isTrue();
 }

 @Test
 public void testThatNotifiationIsTriggeredByActionIfThatActionIsATrigger() throws ValidationException {
  PrnfbNotification notification = prnfbNotificationBuilder()//
    .withTrigger(RESCOPED_FROM)//
    .withUrl("http://hej.com")//
    .build();
  PrnfbPullRequestAction pullRequestAction = RESCOPED_FROM;

  boolean actual = this.sut.isNotificationTriggeredByAction(notification, pullRequestAction, this.renderer,
    this.pullRequest, this.clientKeyStore, this.shouldAcceptAnyCertificate);

  assertThat(actual)//
    .isTrue();
 }

 @Test
 public void testThatPullRequestCommentIsAddedToVariables() {
  PullRequestCommentAddedEvent pullRequestEvent = mock(PullRequestCommentAddedEvent.class);
  Comment comment = mock(Comment.class);
  when(pullRequestEvent.getComment())//
    .thenReturn(comment);
  when(pullRequestEvent.getComment().getText())//
    .thenReturn("The comment");

  Map<PrnfbVariable, Supplier<String>> actual = this.sut.populateVariables(pullRequestEvent);

  assertThat(actual)//
    .hasSize(1);
  assertThat(actual.get(PULL_REQUEST_COMMENT_TEXT).get())//
    .isEqualTo("The comment");
 }

 @Test
 public void testThatPullRequestMergeComitIsAddedToVariables() {
  PullRequestMergedEvent pullRequestEvent = mock(PullRequestMergedEvent.class);
  MinimalCommit commit = mock(MinimalCommit.class);
  when(pullRequestEvent.getCommit())//
    .thenReturn(commit);
  when(pullRequestEvent.getCommit().getId())//
    .thenReturn("hash");

  Map<PrnfbVariable, Supplier<String>> actual = this.sut.populateVariables(pullRequestEvent);

  assertThat(actual)//
    .hasSize(1);
  assertThat(actual.get(PULL_REQUEST_MERGE_COMMIT).get())//
    .isEqualTo("hash");
 }

 @Test
 public void testThatPullRequestOpenedCanTriggerNotification() {

  this.sut.handleEventAsync(this.pullRequestOpenedEvent);

  assertInvokedUrls("http://not1.com/", "http://not2.com/");
 }

 private void assertInvokedUrls(String... expectedUrls) {
  Iterable<String> urls = transform(this.invokedUrls, new Function<UrlInvoker, String>() {
   @Override
   public String apply(UrlInvoker input) {
    return input.getUrlParam();
   }
  });
  assertThat(urls)//
    .containsOnly(expectedUrls);
 }

}
