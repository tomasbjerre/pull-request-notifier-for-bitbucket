package se.bjurr.prnfb.listener;

import static com.atlassian.bitbucket.pull.PullRequestAction.OPENED;
import static com.atlassian.bitbucket.pull.PullRequestAction.RESCOPED;
import static com.atlassian.bitbucket.pull.PullRequestState.DECLINED;
import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static se.bjurr.prnfb.listener.PrnfbPullRequestAction.APPROVED;
import static se.bjurr.prnfb.listener.PrnfbPullRequestAction.RESCOPED_FROM;
import static se.bjurr.prnfb.listener.PrnfbPullRequestEventListener.setInvoker;
import static se.bjurr.prnfb.settings.PrnfbNotificationBuilder.prnfbNotificationBuilder;
import static se.bjurr.prnfb.settings.PrnfbSettingsDataBuilder.prnfbSettingsDataBuilder;
import static se.bjurr.prnfb.settings.TRIGGER_IF_MERGE.ALWAYS;
import static se.bjurr.prnfb.settings.TRIGGER_IF_MERGE.CONFLICTING;
import static se.bjurr.prnfb.settings.TRIGGER_IF_MERGE.NOT_CONFLICTING;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import com.atlassian.bitbucket.event.pull.PullRequestRescopedEvent;
import com.atlassian.bitbucket.scm.Command;
import com.atlassian.bitbucket.scm.ScmService;
import com.atlassian.bitbucket.scm.pull.ScmPullRequestCommandFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import se.bjurr.prnfb.http.ClientKeyStore;
import se.bjurr.prnfb.http.HttpResponse;
import se.bjurr.prnfb.http.Invoker;
import se.bjurr.prnfb.http.UrlInvoker;
import se.bjurr.prnfb.service.MockedEscalatedSecurityContext;
import se.bjurr.prnfb.service.PrnfbRenderer;
import se.bjurr.prnfb.service.PrnfbRendererFactory;
import se.bjurr.prnfb.service.SettingsService;
import se.bjurr.prnfb.service.VariablesContext;
import se.bjurr.prnfb.settings.PrnfbNotification;
import se.bjurr.prnfb.settings.PrnfbSettingsData;
import se.bjurr.prnfb.settings.ValidationException;

import com.atlassian.bitbucket.event.pull.PullRequestCommentAddedEvent;
import com.atlassian.bitbucket.event.pull.PullRequestEvent;
import com.atlassian.bitbucket.project.Project;
import com.atlassian.bitbucket.pull.PullRequest;
import com.atlassian.bitbucket.pull.PullRequestRef;
import com.atlassian.bitbucket.pull.PullRequestService;
import com.atlassian.bitbucket.repository.Repository;
import com.atlassian.bitbucket.user.ApplicationUser;
import com.atlassian.bitbucket.user.EscalatedSecurityContext;
import com.atlassian.bitbucket.user.SecurityService;
import com.google.common.base.Function;

public class PrnfbPullRequestEventListenerTest {

  private final ClientKeyStore clientKeyStore = null;
  private final ExecutorService executorService = new FakeExecutorService();
  @Mock private PullRequestRef fromRef;
  private final List<UrlInvoker> invokedUrls = newArrayList();
  private PrnfbNotification notification1;
  private PrnfbNotification notification2;
  private PrnfbNotification notification3;
  private PrnfbSettingsData pluginSettingsData;
  @Mock private PrnfbRendererFactory prnfbRendererFactory;
  @Mock private PullRequest pullRequest;
  @Mock private PullRequestEvent pullRequestOpenedEvent;
  @Mock private PullRequestEvent pullRequestRescopedEvent;
  @Mock private PullRequestService pullRequestService;
  @Mock private PrnfbRenderer renderer;
  @Mock private SettingsService settingsService;
  @Mock private ScmService scmService;
  @Mock private ScmPullRequestCommandFactory pullRequestCommandFactory;
  @Mock private Command pullRequestCommand;

  private Boolean shouldAcceptAnyCertificate;
  private PrnfbPullRequestEventListener sut;
  @Mock private PullRequestRef toRef;

  private void assertInvokedUrls(String... expectedUrls) {
    Iterable<String> urls =
        transform(
            invokedUrls,
            new Function<UrlInvoker, String>() {
              @Override
              public String apply(UrlInvoker input) {
                return input.getUrlParam();
              }
            });
    assertThat(urls) //
        .containsOnly(expectedUrls);
  }

  @Before
  public void before() throws ValidationException {
    initMocks(this);
    SecurityService securityService = mock(SecurityService.class);
    EscalatedSecurityContext escalatedSecurityContext = new MockedEscalatedSecurityContext();
    when(securityService.withPermission(Mockito.any(), Mockito.any())) //
        .thenReturn(escalatedSecurityContext);
    sut =
        new PrnfbPullRequestEventListener(
            prnfbRendererFactory,
            pullRequestService,
            executorService,
            settingsService,
            securityService,
            scmService);
    setInvoker(
        new Invoker() {
          @Override
          public HttpResponse invoke(UrlInvoker urlInvoker) {
            HttpResponse response = null;
            try {
              response = new HttpResponse(new URI("http://fake.com/"), 200, "");
            } catch (URISyntaxException e) {
              e.printStackTrace();
            }
            urlInvoker.setResponse(response);
            invokedUrls.add(urlInvoker);
            return response;
          }
        });

    when(pullRequest.getFromRef()) //
        .thenReturn(fromRef);
    when(pullRequest.getFromRef().getLatestCommit()) //
        .thenReturn("latestCFrom");
    when(pullRequest.getFromRef().getId()) //
        .thenReturn("IFrom");

    when(pullRequest.getToRef()) //
        .thenReturn(toRef);
    when(pullRequest.getToRef().getLatestCommit()) //
        .thenReturn("latestCTo");
    when(pullRequest.getToRef().getId()) //
        .thenReturn("ITo");

    when(pullRequestOpenedEvent.getPullRequest()) //
        .thenReturn(pullRequest);
    when(pullRequestOpenedEvent.getPullRequest().isClosed()) //
        .thenReturn(false);
    when(pullRequestOpenedEvent.getAction()) //
        .thenReturn(OPENED);

    when(pullRequestRescopedEvent.getPullRequest()) //
        .thenReturn(pullRequest);
    when(pullRequestRescopedEvent.getPullRequest().isClosed()) //
        .thenReturn(false);
    when(pullRequestRescopedEvent.getAction()) //
        .thenReturn(RESCOPED);

    pluginSettingsData =
        prnfbSettingsDataBuilder() //
            .build();
    when(settingsService.getPrnfbSettingsData()) //
        .thenReturn(pluginSettingsData);

    notification1 =
        prnfbNotificationBuilder() //
            .withUrl("http://not1.com/") //
            .withTrigger(PrnfbPullRequestAction.OPENED) //
            .build();
    notification2 =
        prnfbNotificationBuilder(notification1) //
            .withUrl("http://not2.com/") //
            .build();
    notification3 =
        prnfbNotificationBuilder(notification1) //
            .withUrl("http://not2.com/") //
            .withTrigger(PrnfbPullRequestAction.RESCOPED_FROM) //
            .withForceMergeOnRescope(true) //
            .build();
    List<PrnfbNotification> notifications =
        newArrayList(notification1, notification2, notification3);
    when(settingsService.getNotifications()) //
        .thenReturn(notifications);

    when(prnfbRendererFactory.create(
            any(PullRequest.class),
            any(PrnfbPullRequestAction.class),
            any(PrnfbNotification.class),
            any(VariablesContext.class),
            any(ApplicationUser.class))) //
        .thenReturn(renderer);
    when(renderer.render(any(), any(), any(), any())) //
        .thenAnswer(
            new Answer<String>() {
              @Override
              public String answer(InvocationOnMock invocation) throws Throwable {
                return (String) invocation.getArguments()[0];
              }
            });
  }

  @Test
  public void testThatCommentOnClosedPRIsIgnored() {
    when(pullRequest.isClosed()) //
        .thenReturn(true);
    PullRequestCommentAddedEvent pullRequestEvent = mock(PullRequestCommentAddedEvent.class);
    when(pullRequestEvent.getPullRequest()) //
        .thenReturn(pullRequest);

    sut.handleEventAsync(pullRequestEvent);

    assertThat(invokedUrls) //
        .isEmpty();
  }

  @Test
  public void testThatHeaderCanContainVariables() {}

  @Test
  public void testThatNotifiationIsNotTriggeredByActionIfAFilterIsNotMatching()
      throws ValidationException {
    PrnfbNotification notification =
        prnfbNotificationBuilder() //
            .withTrigger(RESCOPED_FROM) //
            .withUrl("http://hej.com") //
            .withFilterRegexp("^abc$") //
            .withFilterString("bc") //
            .build();
    PrnfbPullRequestAction pullRequestAction = RESCOPED_FROM;

    boolean actual =
        sut.isNotificationTriggeredByAction(
            notification,
            pullRequestAction,
            renderer,
            pullRequest,
            clientKeyStore,
            shouldAcceptAnyCertificate);

    assertThat(actual) //
        .isFalse();
  }

  @Test
  public void testThatNotifiationIsNotTriggeredByActionIfOnlyBuildingMergingAndItIsConflicting() {
    assertThat(sut.ignoreBecauseOfConflicting(ALWAYS, false)) //
        .isFalse();
    assertThat(sut.ignoreBecauseOfConflicting(CONFLICTING, false)) //
        .isTrue();
    assertThat(sut.ignoreBecauseOfConflicting(NOT_CONFLICTING, false)) //
        .isFalse();

    assertThat(sut.ignoreBecauseOfConflicting(ALWAYS, true)) //
        .isFalse();
    assertThat(sut.ignoreBecauseOfConflicting(CONFLICTING, true)) //
        .isFalse();
    assertThat(sut.ignoreBecauseOfConflicting(NOT_CONFLICTING, true)) //
        .isTrue();
  }

  @Test
  public void testThatNotifiationIsNotTriggeredByActionIfProjectNotSame()
      throws ValidationException {
    PrnfbNotification notification =
        prnfbNotificationBuilder() //
            .withTrigger(RESCOPED_FROM) //
            .withUrl("http://hej.com") //
            .withProjectKey("pk") //
            .build();
    PrnfbPullRequestAction pullRequestAction = RESCOPED_FROM;
    Repository repository = mock(Repository.class);
    when(toRef.getRepository()) //
        .thenReturn(repository);
    Project project = mock(Project.class);
    when(repository.getProject()) //
        .thenReturn(project);
    when(project.getKey()) //
        .thenReturn("pk2");

    boolean actual =
        sut.isNotificationTriggeredByAction(
            notification,
            pullRequestAction,
            renderer,
            pullRequest,
            clientKeyStore,
            shouldAcceptAnyCertificate);

    assertThat(actual) //
        .isFalse();
  }

  @Test
  public void testThatNotifiationIsNotTriggeredByActionIfRepositoryNotSame()
      throws ValidationException {
    PrnfbNotification notification =
        prnfbNotificationBuilder() //
            .withTrigger(RESCOPED_FROM) //
            .withUrl("http://hej.com") //
            .withRepositorySlug("repositorySlug123") //
            .build();
    PrnfbPullRequestAction pullRequestAction = RESCOPED_FROM;
    Repository repository = mock(Repository.class);
    when(toRef.getRepository()) //
        .thenReturn(repository);
    when(repository.getSlug()) //
        .thenReturn("asdasd");

    boolean actual =
        sut.isNotificationTriggeredByAction(
            notification,
            pullRequestAction,
            renderer,
            pullRequest,
            clientKeyStore,
            shouldAcceptAnyCertificate);

    assertThat(actual) //
        .isFalse();
  }

  @Test
  public void testThatNotifiationIsNotTriggeredByActionIfThatActionIsATriggerButStateIgnored()
      throws ValidationException {
    PrnfbNotification notification =
        prnfbNotificationBuilder() //
            .withTrigger(RESCOPED_FROM) //
            .withUrl("http://hej.com") //
            .setTriggerIgnoreState(newArrayList(DECLINED)) //
            .build();
    PrnfbPullRequestAction pullRequestAction = RESCOPED_FROM;
    when(pullRequest.getState()) //
        .thenReturn(DECLINED);

    boolean actual =
        sut.isNotificationTriggeredByAction(
            notification,
            pullRequestAction,
            renderer,
            pullRequest,
            clientKeyStore,
            shouldAcceptAnyCertificate);

    assertThat(actual) //
        .isFalse();
  }

  @Test
  public void testThatNotifiationIsNotTriggeredByActionIfThatActionIsNotATrigger()
      throws ValidationException {
    PrnfbNotification notification =
        prnfbNotificationBuilder() //
            .withTrigger(RESCOPED_FROM) //
            .withUrl("http://hej.com") //
            .build();
    PrnfbPullRequestAction pullRequestAction = APPROVED;

    boolean actual =
        sut.isNotificationTriggeredByAction(
            notification,
            pullRequestAction,
            renderer,
            pullRequest,
            clientKeyStore,
            shouldAcceptAnyCertificate);

    assertThat(actual) //
        .isFalse();
  }

  @Test
  public void testThatNotifiationIsTriggeredByActionIfAFilterIsMatching()
      throws ValidationException {
    PrnfbNotification notification =
        prnfbNotificationBuilder() //
            .withTrigger(RESCOPED_FROM) //
            .withUrl("http://hej.com") //
            .withFilterRegexp("^abc$") //
            .withFilterString("abc") //
            .build();
    PrnfbPullRequestAction pullRequestAction = RESCOPED_FROM;

    boolean actual =
        sut.isNotificationTriggeredByAction(
            notification,
            pullRequestAction,
            renderer,
            pullRequest,
            clientKeyStore,
            shouldAcceptAnyCertificate);

    assertThat(actual) //
        .isTrue();
  }

  @Test
  public void testThatNotifiationIsTriggeredByActionIfAFilterIsMatchingWhenTrimmed()
      throws ValidationException {
    PrnfbNotification notification =
        prnfbNotificationBuilder() //
            .withTrigger(RESCOPED_FROM) //
            .withUrl("http://hej.com") //
            .withFilterRegexp(" ^abc$   ") //
            .withFilterString("  abc ") //
            .build();
    PrnfbPullRequestAction pullRequestAction = RESCOPED_FROM;

    boolean actual =
        sut.isNotificationTriggeredByAction(
            notification,
            pullRequestAction,
            renderer,
            pullRequest,
            clientKeyStore,
            shouldAcceptAnyCertificate);

    assertThat(actual) //
        .isTrue();
  }

  @Test
  public void testThatNotifiationIsTriggeredByActionIfProjectSame() throws ValidationException {
    PrnfbNotification notification =
        prnfbNotificationBuilder() //
            .withTrigger(RESCOPED_FROM) //
            .withUrl("http://hej.com") //
            .withProjectKey("pk2") //
            .build();
    PrnfbPullRequestAction pullRequestAction = RESCOPED_FROM;
    Repository repository = mock(Repository.class);
    when(toRef.getRepository()) //
        .thenReturn(repository);
    Project project = mock(Project.class);
    when(repository.getProject()) //
        .thenReturn(project);
    when(project.getKey()) //
        .thenReturn("pk2");

    boolean actual =
        sut.isNotificationTriggeredByAction(
            notification,
            pullRequestAction,
            renderer,
            pullRequest,
            clientKeyStore,
            shouldAcceptAnyCertificate);

    assertThat(actual) //
        .isTrue();
  }

  @Test
  public void testThatNotifiationIsTriggeredByActionIfRepositorySame() throws ValidationException {
    PrnfbNotification notification =
        prnfbNotificationBuilder() //
            .withTrigger(RESCOPED_FROM) //
            .withUrl("http://hej.com") //
            .withRepositorySlug("repositorySlug123") //
            .build();
    PrnfbPullRequestAction pullRequestAction = RESCOPED_FROM;
    Repository repository = mock(Repository.class);
    when(toRef.getRepository()) //
        .thenReturn(repository);
    when(repository.getSlug()) //
        .thenReturn("repositorySlug123");

    boolean actual =
        sut.isNotificationTriggeredByAction(
            notification,
            pullRequestAction,
            renderer,
            pullRequest,
            clientKeyStore,
            shouldAcceptAnyCertificate);

    assertThat(actual) //
        .isTrue();
  }

  @Test
  public void testThatNotifiationIsTriggeredByActionIfThatActionIsATrigger()
      throws ValidationException {
    PrnfbNotification notification =
        prnfbNotificationBuilder() //
            .withTrigger(RESCOPED_FROM) //
            .withUrl("http://hej.com") //
            .build();
    PrnfbPullRequestAction pullRequestAction = RESCOPED_FROM;

    boolean actual =
        sut.isNotificationTriggeredByAction(
            notification,
            pullRequestAction,
            renderer,
            pullRequest,
            clientKeyStore,
            shouldAcceptAnyCertificate);

    assertThat(actual) //
        .isTrue();
  }

  @Test
  public void testThatPullRequestOpenedCanTriggerNotification() {

    sut.handleEventAsync(pullRequestOpenedEvent);

    assertInvokedUrls("http://not1.com/", "http://not2.com/");
  }

  @Test
  public void testThatTryMergeIsCalledWhenForceMergeOnRescopeEnabled() throws ValidationException {
    when(scmService.getPullRequestCommandFactory(any(PullRequest.class)))
        .thenReturn(pullRequestCommandFactory);
    when(pullRequestCommandFactory.tryMerge(any(PullRequest.class))).thenReturn(pullRequestCommand);
    sut.handleEventAsync(pullRequestRescopedEvent);
    verify(pullRequestCommand, times(1)).call();
  }
}
