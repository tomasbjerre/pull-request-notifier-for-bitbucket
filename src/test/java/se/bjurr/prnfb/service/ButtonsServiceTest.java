package se.bjurr.prnfb.service;

import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.mockito.internal.verification.VerificationModeFactory.times;
import static se.bjurr.prnfb.listener.PrnfbPullRequestAction.BUTTON_TRIGGER;
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

public class ButtonsServiceTest {

 @Mock
 private PullRequestService pullRequestService;
 @Mock
 private PrnfbPullRequestEventListener prnfbPullRequestEventListener;
 @Mock
 private ApplicationPropertiesService propertiesService;
 @Mock
 private RepositoryService repositoryService;
 @Mock
 private AuthenticationContext authenticationContext;
 @Mock
 private SettingsService settingsService;
 @Mock
 private UserCheckService userCheckService;
 private ButtonsService sut;
 private ButtonDTO buttonDto1;
 private PrnfbButton button1;
 private ButtonDTO buttonDto2;
 private PrnfbButton button2;
 private List<PrnfbNotification> notifications;
 @Mock
 private ClientKeyStore clientKeyStore;
 @Mock
 private PullRequest pullRequest;
 private final Boolean shouldAcceptAnyCertificate = true;
 private PrnfbNotification notification1;
 private NotificationDTO notificationDto2;
 private PrnfbNotification notification2;
 private NotificationDTO notificationDto1;
 private final PrnfbPullRequestAction pullRequestAction = BUTTON_TRIGGER;
 @Mock
 private PrnfbRenderer renderer;
 @Mock
 private PrnfbRendererFactory prnfbRendererFactory;

 @SuppressWarnings("unchecked")
 @Before
 public void before() throws ValidationException {
  initMocks(this);
  sut = new ButtonsService(pullRequestService, prnfbPullRequestEventListener, prnfbRendererFactory, settingsService,
    userCheckService);
  when(
    prnfbRendererFactory.create(any(PullRequest.class), any(PrnfbPullRequestAction.class),
      any(PrnfbNotification.class), anyMap()))//
    .thenReturn(renderer);

  buttonDto1 = new PodamFactoryImpl().manufacturePojo(ButtonDTO.class);
  button1 = toPrnfbButton(buttonDto1);
  buttonDto2 = new PodamFactoryImpl().manufacturePojo(ButtonDTO.class);
  button2 = toPrnfbButton(buttonDto2);

  when(settingsService.getButton(button1.getUuid()))//
    .thenReturn(button1);
  when(settingsService.getButton(button2.getUuid()))//
    .thenReturn(button2);

  notificationDto1 = new PodamFactoryImpl().manufacturePojo(NotificationDTO.class);
  notificationDto1.setUrl("http://hej.com");
  notification1 = toPrnfbNotification(notificationDto1);

  notificationDto2 = new PodamFactoryImpl().manufacturePojo(NotificationDTO.class);
  notificationDto2.setUrl("http://hej.com");
  notification2 = toPrnfbNotification(notificationDto2);

  notifications = newArrayList(notification1, notification2);
  when(settingsService.getNotifications())//
    .thenReturn(notifications);
 }

 @Test
 public void testThatButtonsCanBeRetrievedWhenNoneAllowed() {

  List<PrnfbButton> candidates = newArrayList(button1, button2);
  when(settingsService.getButtons())//
    .thenReturn(candidates);
  when(userCheckService.isAllowedUseButton(button1))//
    .thenReturn(false);
  when(userCheckService.isAllowedUseButton(button2))//
    .thenReturn(false);

  List<PrnfbButton> actual = sut.doGetButtons(notifications, clientKeyStore, pullRequest, shouldAcceptAnyCertificate);

  assertThat(actual)//
    .isEmpty();
 }

 @Test
 public void testThatButtonsCanBeRetrievedWhenAllAllowed() {

  List<PrnfbButton> candidates = newArrayList(button1, button2);
  when(settingsService.getButtons())//
    .thenReturn(candidates);
  when(userCheckService.isAllowedUseButton(button1))//
    .thenReturn(true);
  when(userCheckService.isAllowedUseButton(button2))//
    .thenReturn(true);
  when(
    prnfbPullRequestEventListener.isNotificationTriggeredByAction(notification1, pullRequestAction, renderer,
      pullRequest, clientKeyStore, shouldAcceptAnyCertificate))//
    .thenReturn(true);
  when(
    prnfbPullRequestEventListener.isNotificationTriggeredByAction(notification2, pullRequestAction, renderer,
      pullRequest, clientKeyStore, shouldAcceptAnyCertificate))//
    .thenReturn(true);

  List<PrnfbButton> actual = sut.doGetButtons(notifications, clientKeyStore, pullRequest, shouldAcceptAnyCertificate);

  assertThat(actual)//
    .containsExactly(button1, button2);
 }

 @Test
 public void testThatPressedButtonDoesNotDoAnythingIfNoMatchingNotification() {
  UUID buttonUuid = button1.getUuid();

  sut.doHandlePressed(buttonUuid, clientKeyStore, shouldAcceptAnyCertificate, pullRequest);

  verify(prnfbPullRequestEventListener, times(0))//
    .notify(any(), any(), any(), any(), any(), any(), any());
 }

 @Test
 public void testThatPressedButtonDoesTriggerIfMatchingNotification() {
  UUID buttonUuid = button1.getUuid();
  when(prnfbPullRequestEventListener.isNotificationTriggeredByAction(any(), any(), any(), any(), any(), any()))//
    .thenReturn(true);

  sut.doHandlePressed(buttonUuid, clientKeyStore, shouldAcceptAnyCertificate, pullRequest);

  verify(prnfbPullRequestEventListener, times(2))//
    .notify(any(), any(), any(), any(), any(), any(), any());
 }
}
