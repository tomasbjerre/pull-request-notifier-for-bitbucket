package se.bjurr.prnfb.service;

import static com.atlassian.bitbucket.pull.PullRequestState.DECLINED;
import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.mockito.internal.verification.VerificationModeFactory.times;
import static se.bjurr.prnfb.listener.PrnfbPullRequestAction.BUTTON_TRIGGER;
import static se.bjurr.prnfb.listener.PrnfbPullRequestAction.MERGED;
import static se.bjurr.prnfb.test.Podam.populatedInstanceOf;
import static se.bjurr.prnfb.transformer.ButtonTransformer.toPrnfbButton;
import static se.bjurr.prnfb.transformer.NotificationTransformer.toPrnfbNotification;

import com.atlassian.bitbucket.auth.AuthenticationContext;
import com.atlassian.bitbucket.project.Project;
import com.atlassian.bitbucket.pull.PullRequest;
import com.atlassian.bitbucket.pull.PullRequestRef;
import com.atlassian.bitbucket.pull.PullRequestService;
import com.atlassian.bitbucket.repository.Repository;
import com.atlassian.bitbucket.repository.RepositoryService;
import com.atlassian.bitbucket.server.ApplicationPropertiesService;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.UUID;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import se.bjurr.prnfb.http.ClientKeyStore;
import se.bjurr.prnfb.listener.PrnfbPullRequestAction;
import se.bjurr.prnfb.listener.PrnfbPullRequestEventListener;
import se.bjurr.prnfb.presentation.dto.ButtonDTO;
import se.bjurr.prnfb.presentation.dto.NotificationDTO;
import se.bjurr.prnfb.presentation.dto.ON_OR_OFF;
import se.bjurr.prnfb.settings.PrnfbButton;
import se.bjurr.prnfb.settings.PrnfbNotification;
import se.bjurr.prnfb.settings.USER_LEVEL;
import se.bjurr.prnfb.settings.ValidationException;

public class ButtonsServiceTest {

  @Mock private AuthenticationContext authenticationContext;
  private PrnfbButton button1;
  private PrnfbButton button2;
  private PrnfbButton button3;
  private ButtonDTO buttonDto1;
  private ButtonDTO buttonDto2;
  private ButtonDTO buttonDto3;
  @Mock private ClientKeyStore clientKeyStore;
  private final ON_OR_OFF confirmation = ON_OR_OFF.on;
  private final String name = "name";
  private PrnfbNotification notification1;
  private PrnfbNotification notification2;
  private NotificationDTO notificationDto1;
  private NotificationDTO notificationDto2;
  private List<PrnfbNotification> notifications;
  @Mock private Repository originRepo;
  @Mock private PrnfbPullRequestEventListener prnfbPullRequestEventListener;
  @Mock private PrnfbRendererFactory prnfbRendererFactory;
  @Mock private Project project;
  @Mock private ApplicationPropertiesService propertiesService;
  @Mock private PullRequestRef prRef;
  @Mock private PullRequest pullRequest;
  private final PrnfbPullRequestAction pullRequestAction = BUTTON_TRIGGER;
  @Mock private PullRequestService pullRequestService;
  @Mock private PrnfbRenderer renderer;
  @Mock private Repository repository;
  @Mock private RepositoryService repositoryService;
  @Mock private SettingsService settingsService;
  private final Boolean shouldAcceptAnyCertificate = true;
  private ButtonsService sut;
  @Mock private UserCheckService userCheckService;
  private final USER_LEVEL userLevel = USER_LEVEL.ADMIN;
  private final UUID uuid = UUID.randomUUID();

  @SuppressWarnings("unchecked")
  @Before
  public void before() throws ValidationException {
    initMocks(this);
    this.sut =
        new ButtonsService(
            this.pullRequestService,
            this.prnfbPullRequestEventListener,
            this.prnfbRendererFactory,
            this.settingsService,
            this.userCheckService);
    when(this.prnfbRendererFactory.create(
            any(PullRequest.class),
            any(PrnfbPullRequestAction.class),
            any(PrnfbNotification.class),
            any(VariablesContext.class))) //
        .thenReturn(this.renderer);

    this.buttonDto1 = populatedInstanceOf(ButtonDTO.class);
    this.buttonDto1.setProjectKey(null);
    this.buttonDto1.setRepositorySlug(null);
    this.buttonDto1.setUserLevel(null);
    this.button1 = toPrnfbButton(this.buttonDto1);
    this.buttonDto2 = populatedInstanceOf(ButtonDTO.class);
    this.buttonDto2.setProjectKey(null);
    this.buttonDto2.setRepositorySlug(null);
    this.buttonDto2.setUserLevel(null);
    this.button2 = toPrnfbButton(this.buttonDto2);
    this.buttonDto3 = populatedInstanceOf(ButtonDTO.class);
    this.buttonDto3.setUserLevel(null);
    this.button3 = toPrnfbButton(this.buttonDto3);

    when(this.settingsService.getButton(this.button1.getUuid())) //
        .thenReturn(this.button1);
    when(this.settingsService.getButton(this.button2.getUuid())) //
        .thenReturn(this.button2);
    when(this.settingsService.getButton(this.button3.getUuid())) //
        .thenReturn(this.button3);

    this.notificationDto1 = populatedInstanceOf(NotificationDTO.class);
    this.notificationDto1.setUrl("http://hej.com");
    this.notificationDto1.setTriggerIgnoreStateList(Lists.newArrayList(DECLINED.name()));
    this.notificationDto1.setTriggers(newArrayList(MERGED.name()));
    this.notification1 = toPrnfbNotification(this.notificationDto1);

    this.notificationDto2 = populatedInstanceOf(NotificationDTO.class);
    this.notificationDto2.setUrl("http://hej.com");
    this.notificationDto2.setTriggerIgnoreStateList(Lists.newArrayList(DECLINED.name()));
    this.notificationDto2.setTriggers(newArrayList(MERGED.name()));
    this.notification2 = toPrnfbNotification(this.notificationDto2);

    this.notifications = newArrayList(this.notification1, this.notification2);
    when(this.settingsService.getNotifications()) //
        .thenReturn(this.notifications);

    when(this.pullRequest.getToRef()).thenReturn(this.prRef);
    when(this.prRef.getRepository()).thenReturn(this.repository);
    when(this.repository.getSlug()).thenReturn(this.button3.getRepositorySlug().get());
    when(this.repository.getProject()).thenReturn(this.project);
    when(this.project.getKey()).thenReturn(this.button3.getProjectKey().get());
  }

  @Test
  public void testThatButtonsCanBeRetrievedWhenAllAllowed() {
    List<PrnfbButton> candidates = newArrayList(this.button1, this.button2, this.button3);
    when(this.settingsService.getButtons()) //
        .thenReturn(candidates);
    String projectKey = prRef.getRepository().getProject().getKey();
    String repoSlug = prRef.getRepository().getSlug();
    when(this.userCheckService.isAllowed(this.button1.getUserLevel(), projectKey, repoSlug)) //
        .thenReturn(true);
    when(this.userCheckService.isAllowed(this.button2.getUserLevel(), projectKey, repoSlug)) //
        .thenReturn(true);
    when(this.userCheckService.isAllowed(this.button3.getUserLevel(), projectKey, repoSlug)) //
        .thenReturn(true);

    when(this.userCheckService.isAllowed(
            this.button1.getUserLevel(), projectKey, "otherrepository")) //
        .thenReturn(true);
    when(this.userCheckService.isAllowed(
            this.button2.getUserLevel(), projectKey, "otherrepository")) //
        .thenReturn(true);
    when(this.userCheckService.isAllowed(
            this.button3.getUserLevel(), projectKey, "otherrepository")) //
        .thenReturn(true);

    when(this.prnfbPullRequestEventListener.isNotificationTriggeredByAction(
            this.notification1,
            this.pullRequestAction,
            this.renderer,
            this.pullRequest,
            this.clientKeyStore,
            this.shouldAcceptAnyCertificate)) //
        .thenReturn(true);
    when(this.prnfbPullRequestEventListener.isNotificationTriggeredByAction(
            this.notification2,
            this.pullRequestAction,
            this.renderer,
            this.pullRequest,
            this.clientKeyStore,
            this.shouldAcceptAnyCertificate)) //
        .thenReturn(true);

    List<PrnfbButton> actual =
        this.sut.doGetButtons(
            this.notifications,
            this.clientKeyStore,
            this.pullRequest,
            this.shouldAcceptAnyCertificate);
    assertThat(actual) //
        .containsOnly(this.button1, this.button2, this.button3);

    // Now do the same with another repository - button3 should disappear
    when(this.repository.getSlug()).thenReturn("otherrepository");
    actual =
        this.sut.doGetButtons(
            this.notifications,
            this.clientKeyStore,
            this.pullRequest,
            this.shouldAcceptAnyCertificate);
    assertThat(actual) //
        .containsOnly(this.button1, this.button2);

    // Now check if the button is inherited from the origin repo
    when(this.repository.getOrigin()).thenReturn(this.originRepo);
    when(this.originRepo.getSlug()).thenReturn(this.button3.getRepositorySlug().get());
    when(this.originRepo.getProject()).thenReturn(this.project);
    actual =
        this.sut.doGetButtons(
            this.notifications,
            this.clientKeyStore,
            this.pullRequest,
            this.shouldAcceptAnyCertificate);
    assertThat(actual) //
        .containsOnly(this.button1, this.button2, this.button3);
  }

  @Test
  public void testThatButtonsCanBeRetrievedWhenNoneAllowed() {

    List<PrnfbButton> candidates = newArrayList(this.button1, this.button2);
    when(this.settingsService.getButtons()) //
        .thenReturn(candidates);
    String projectKey = prRef.getRepository().getProject().getKey();
    String repoSlug = prRef.getRepository().getSlug();
    when(this.userCheckService.isAllowed(this.button1.getUserLevel(), projectKey, repoSlug)) //
        .thenReturn(false);
    when(this.userCheckService.isAllowed(this.button2.getUserLevel(), projectKey, repoSlug)) //
        .thenReturn(false);

    List<PrnfbButton> actual =
        this.sut.doGetButtons(
            this.notifications,
            this.clientKeyStore,
            this.pullRequest,
            this.shouldAcceptAnyCertificate);

    assertThat(actual) //
        .isEmpty();
  }

  @Test
  public void testThatPressedButtonDoesNotDoAnythingIfNoMatchingNotification() {
    UUID buttonUuid = this.button1.getUuid();

    this.sut.doHandlePressed(
        buttonUuid, this.clientKeyStore, this.shouldAcceptAnyCertificate, this.pullRequest, "");

    verify(this.prnfbPullRequestEventListener, times(0)) //
        .notify(any(), any(), any(), any(), any(), any());
  }

  @Test
  public void testThatPressedButtonDoesTriggerIfMatchingNotification() {
    UUID buttonUuid = this.button1.getUuid();
    when(this.prnfbPullRequestEventListener.isNotificationTriggeredByAction(
            any(), any(), any(), any(), any(), any())) //
        .thenReturn(true);

    this.sut.doHandlePressed(
        buttonUuid, this.clientKeyStore, this.shouldAcceptAnyCertificate, this.pullRequest, "");

    verify(this.prnfbPullRequestEventListener, times(2)) //
        .notify(any(), any(), any(), any(), any(), any());
  }

  @Test
  public void testVisibilityOnPullRequest() {
    String buttonProjectKey = "proj";
    String buttonRepositorySlug = "repo";
    String repoProjectKey = "proj";
    String repoRepositorySlug = "repo";
    testVisibilityOnRepository(
        buttonProjectKey, buttonRepositorySlug, repoProjectKey, repoRepositorySlug, true);

    buttonProjectKey = "proj";
    buttonRepositorySlug = "repo";
    repoProjectKey = "proj2";
    repoRepositorySlug = "repo";
    testVisibilityOnRepository(
        buttonProjectKey, buttonRepositorySlug, repoProjectKey, repoRepositorySlug, false);

    buttonProjectKey = "proj";
    buttonRepositorySlug = null;
    repoProjectKey = "proj";
    repoRepositorySlug = "repo";
    testVisibilityOnRepository(
        buttonProjectKey, buttonRepositorySlug, repoProjectKey, repoRepositorySlug, true);

    buttonProjectKey = "proj";
    buttonRepositorySlug = null;
    repoProjectKey = "proj2";
    repoRepositorySlug = "repo";
    testVisibilityOnRepository(
        buttonProjectKey, buttonRepositorySlug, repoProjectKey, repoRepositorySlug, false);

    buttonProjectKey = "proj";
    buttonRepositorySlug = "repo";
    repoProjectKey = "proj";
    repoRepositorySlug = "repo2";
    testVisibilityOnRepository(
        buttonProjectKey, buttonRepositorySlug, repoProjectKey, repoRepositorySlug, false);

    buttonProjectKey = "proj";
    buttonRepositorySlug = "repo";
    repoProjectKey = "proj2";
    repoRepositorySlug = "repo2";
    testVisibilityOnRepository(
        buttonProjectKey, buttonRepositorySlug, repoProjectKey, repoRepositorySlug, false);
  }

  private void testVisibilityOnRepository(
      String buttonProjectKey,
      String buttonRepositorySlug,
      String repoProjectKey,
      String repoRepoSlug,
      boolean expected) {
    PrnfbButton button =
        new PrnfbButton(
            this.uuid,
            this.name,
            this.userLevel,
            this.confirmation,
            buttonProjectKey,
            buttonRepositorySlug,
            "confirmationText",
            null);
    when(this.repository.getProject()).thenReturn(this.project);
    when(this.repository.getProject().getKey()) //
        .thenReturn(repoProjectKey);
    when(this.repository.getSlug()) //
        .thenReturn(repoRepoSlug);
    assertThat(this.sut.isVisibleOnRepository(button, this.repository)) //
        .isEqualTo(expected);
  }
}
