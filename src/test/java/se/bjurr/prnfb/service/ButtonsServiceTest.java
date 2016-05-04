package se.bjurr.prnfb.service;

import static com.atlassian.bitbucket.pull.PullRequestState.DECLINED;
import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.mockito.internal.verification.VerificationModeFactory.times;
import static se.bjurr.prnfb.listener.PrnfbPullRequestAction.BUTTON_TRIGGER;
import static se.bjurr.prnfb.listener.PrnfbPullRequestAction.MERGED;
import static se.bjurr.prnfb.transformer.ButtonTransformer.toPrnfbButton;
import static se.bjurr.prnfb.transformer.NotificationTransformer.toPrnfbNotification;

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
import se.bjurr.prnfb.settings.PrnfbButton;
import se.bjurr.prnfb.settings.PrnfbNotification;
import se.bjurr.prnfb.settings.ValidationException;
import uk.co.jemos.podam.api.PodamFactoryImpl;

import com.atlassian.bitbucket.auth.AuthenticationContext;
import com.atlassian.bitbucket.pull.PullRequest;
import com.atlassian.bitbucket.pull.PullRequestService;
import com.atlassian.bitbucket.repository.RepositoryService;
import com.atlassian.bitbucket.server.ApplicationPropertiesService;
import com.google.common.collect.Lists;

public class ButtonsServiceTest {

 @Mock
 private AuthenticationContext authenticationContext;
 private PrnfbButton button1;
 private PrnfbButton button2;
 private ButtonDTO buttonDto1;
 private ButtonDTO buttonDto2;
 @Mock
 private ClientKeyStore clientKeyStore;
 private PrnfbNotification notification1;
 private PrnfbNotification notification2;
 private NotificationDTO notificationDto1;
 private NotificationDTO notificationDto2;
 private List<PrnfbNotification> notifications;
 @Mock
 private PrnfbPullRequestEventListener prnfbPullRequestEventListener;
 @Mock
 private PrnfbRendererFactory prnfbRendererFactory;
 @Mock
 private ApplicationPropertiesService propertiesService;
 @Mock
 private PullRequest pullRequest;
 private final PrnfbPullRequestAction pullRequestAction = BUTTON_TRIGGER;
 @Mock
 private PullRequestService pullRequestService;
 @Mock
 private PrnfbRenderer renderer;
 @Mock
 private RepositoryService repositoryService;
 @Mock
 private SettingsService settingsService;
 private final Boolean shouldAcceptAnyCertificate = true;
 private ButtonsService sut;
 @Mock
 private UserCheckService userCheckService;

 @SuppressWarnings("unchecked")
 @Before
 public void before() throws ValidationException {
  initMocks(this);
  this.sut = new ButtonsService(this.pullRequestService, this.prnfbPullRequestEventListener, this.prnfbRendererFactory,
    this.settingsService, this.userCheckService);
  when(
    this.prnfbRendererFactory.create(any(PullRequest.class), any(PrnfbPullRequestAction.class),
      any(PrnfbNotification.class), anyMap()))//
    .thenReturn(this.renderer);

  this.buttonDto1 = new PodamFactoryImpl().manufacturePojo(ButtonDTO.class);
  this.buttonDto1.setProjectKey("a");
  this.button1 = toPrnfbButton(this.buttonDto1);
  this.buttonDto2 = new PodamFactoryImpl().manufacturePojo(ButtonDTO.class);
  this.buttonDto2.setProjectKey("b");
  this.button2 = toPrnfbButton(this.buttonDto2);

  when(this.settingsService.getButton(this.button1.getUuid()))//
    .thenReturn(this.button1);
  when(this.settingsService.getButton(this.button2.getUuid()))//
    .thenReturn(this.button2);

  this.notificationDto1 = new PodamFactoryImpl().manufacturePojo(NotificationDTO.class);
  this.notificationDto1.setUrl("http://hej.com");
  this.notificationDto1.setTriggerIgnoreStateList(Lists.newArrayList(DECLINED.name()));
  this.notificationDto1.setTriggers(newArrayList(MERGED.name()));
  this.notification1 = toPrnfbNotification(this.notificationDto1);

  this.notificationDto2 = new PodamFactoryImpl().manufacturePojo(NotificationDTO.class);
  this.notificationDto2.setUrl("http://hej.com");
  this.notificationDto2.setTriggerIgnoreStateList(Lists.newArrayList(DECLINED.name()));
  this.notificationDto2.setTriggers(newArrayList(MERGED.name()));
  this.notification2 = toPrnfbNotification(this.notificationDto2);

  this.notifications = newArrayList(this.notification1, this.notification2);
  when(this.settingsService.getNotifications())//
    .thenReturn(this.notifications);
 }

 @Test
 public void testThatButtonsCanBeRetrievedWhenAllAllowed() {

  List<PrnfbButton> candidates = newArrayList(this.button1, this.button2);
  when(this.settingsService.getButtons())//
    .thenReturn(candidates);
  when(this.userCheckService.isAllowedUseButton(this.button1))//
    .thenReturn(true);
  when(this.userCheckService.isAllowedUseButton(this.button2))//
    .thenReturn(true);
  when(
    this.prnfbPullRequestEventListener.isNotificationTriggeredByAction(this.notification1, this.pullRequestAction,
      this.renderer, this.pullRequest, this.clientKeyStore, this.shouldAcceptAnyCertificate))//
    .thenReturn(true);
  when(
    this.prnfbPullRequestEventListener.isNotificationTriggeredByAction(this.notification2, this.pullRequestAction,
      this.renderer, this.pullRequest, this.clientKeyStore, this.shouldAcceptAnyCertificate))//
    .thenReturn(true);

  List<PrnfbButton> actual = this.sut.doGetButtons(this.notifications, this.clientKeyStore, this.pullRequest,
    this.shouldAcceptAnyCertificate);

  assertThat(actual)//
    .containsExactly(this.button1, this.button2);
 }

 @Test
 public void testThatButtonsCanBeRetrievedWhenNoneAllowed() {

  List<PrnfbButton> candidates = newArrayList(this.button1, this.button2);
  when(this.settingsService.getButtons())//
    .thenReturn(candidates);
  when(this.userCheckService.isAllowedUseButton(this.button1))//
    .thenReturn(false);
  when(this.userCheckService.isAllowedUseButton(this.button2))//
    .thenReturn(false);

  List<PrnfbButton> actual = this.sut.doGetButtons(this.notifications, this.clientKeyStore, this.pullRequest,
    this.shouldAcceptAnyCertificate);

  assertThat(actual)//
    .isEmpty();
 }

 @Test
 public void testThatPressedButtonDoesNotDoAnythingIfNoMatchingNotification() {
  UUID buttonUuid = this.button1.getUuid();

  this.sut.doHandlePressed(buttonUuid, this.clientKeyStore, this.shouldAcceptAnyCertificate, this.pullRequest);

  verify(this.prnfbPullRequestEventListener, times(0))//
    .notify(any(), any(), any(), any(), any(), any());
 }

 @Test
 public void testThatPressedButtonDoesTriggerIfMatchingNotification() {
  UUID buttonUuid = this.button1.getUuid();
  when(this.prnfbPullRequestEventListener.isNotificationTriggeredByAction(any(), any(), any(), any(), any(), any()))//
    .thenReturn(true);

  this.sut.doHandlePressed(buttonUuid, this.clientKeyStore, this.shouldAcceptAnyCertificate, this.pullRequest);

  verify(this.prnfbPullRequestEventListener, times(2))//
    .notify(any(), any(), any(), any(), any(), any());
 }
}
