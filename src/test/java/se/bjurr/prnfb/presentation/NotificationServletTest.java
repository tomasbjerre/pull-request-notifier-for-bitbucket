package se.bjurr.prnfb.presentation;

import static com.atlassian.bitbucket.pull.PullRequestState.DECLINED;
import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static se.bjurr.prnfb.listener.PrnfbPullRequestAction.MERGED;
import static se.bjurr.prnfb.test.Podam.populatedInstanceOf;
import static se.bjurr.prnfb.transformer.NotificationTransformer.toPrnfbNotification;

import java.util.List;

import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import se.bjurr.prnfb.presentation.dto.NotificationDTO;
import se.bjurr.prnfb.service.SettingsService;
import se.bjurr.prnfb.service.UserCheckService;
import se.bjurr.prnfb.settings.PrnfbNotification;

import com.google.common.collect.Lists;

public class NotificationServletTest {
 private PrnfbNotification notification1;
 private PrnfbNotification notification2;
 private NotificationDTO notificationDto1;
 private NotificationDTO notificationDto2;
 @Mock
 private SettingsService settingsService;
 private NotificationServlet sut;
 @Mock
 private UserCheckService userCheckService;

 @Before
 public void before() throws Exception {
  initMocks(this);
  when(this.userCheckService.isViewAllowed())//
    .thenReturn(true);
  when(this.userCheckService.isAdminAllowed(anyString(), anyString()))//
    .thenReturn(true);
  this.sut = new NotificationServlet(this.settingsService, this.userCheckService);
  this.notificationDto1 = populatedInstanceOf(NotificationDTO.class);
  this.notificationDto1.setUrl("http://hej.com/");
  this.notificationDto1.setTriggerIgnoreStateList(newArrayList(DECLINED.name()));
  this.notificationDto1.setTriggers(newArrayList(MERGED.name()));
  this.notificationDto2 = populatedInstanceOf(NotificationDTO.class);
  this.notificationDto2.setUrl("http://hej.com/");
  this.notificationDto2.setTriggerIgnoreStateList(Lists.newArrayList(DECLINED.name()));
  this.notificationDto2.setTriggers(newArrayList(MERGED.name()));
  this.notification1 = toPrnfbNotification(this.notificationDto1);
  this.notification2 = toPrnfbNotification(this.notificationDto2);
 }

 @Test
 public void testNotificationCanBeCreated() throws Exception {
  NotificationDTO incomingDto = populatedInstanceOf(NotificationDTO.class);
  incomingDto.setUrl("http://hej.com/");
  incomingDto.setTriggerIgnoreStateList(newArrayList(DECLINED.name()));
  incomingDto.setTriggers(newArrayList(MERGED.name()));
  PrnfbNotification expectedSavedSettings = toPrnfbNotification(incomingDto);
  when(this.settingsService.addOrUpdateNotification(expectedSavedSettings))//
    .thenReturn(expectedSavedSettings);

  this.sut.create(incomingDto);

  verify(this.settingsService)//
    .addOrUpdateNotification(eq(expectedSavedSettings));
 }

 @Test
 public void testNotificationCanBeDeleted() throws Exception {
  when(this.settingsService.getNotification(this.notification1.getUuid()))//
    .thenReturn(this.notification1);

  this.sut.delete(this.notification1.getUuid());

  verify(this.settingsService)//
    .deleteNotification(this.notification1.getUuid());
 }

 @SuppressWarnings("unchecked")
 @Test
 public void testNotificationCanBeRead() throws Exception {
  List<PrnfbNotification> storedSettings = newArrayList(this.notification1, this.notification2);
  when(this.settingsService.getNotifications())//
    .thenReturn(storedSettings);

  List<NotificationDTO> actual = (List<NotificationDTO>) this.sut.get().getEntity();

  assertThat(actual)//
    .containsOnly(this.notificationDto1, this.notificationDto2);
 }

 @Test
 public void testThatNotificationCanBeListedPerProject() throws Exception {
  when(this.settingsService.getNotifications(this.notificationDto1.getProjectKey()))//
    .thenReturn(newArrayList(this.notification1));

  Response actual = this.sut.get(this.notificationDto1.getProjectKey());
  @SuppressWarnings("unchecked")
  Iterable<NotificationDTO> actualList = (Iterable<NotificationDTO>) actual.getEntity();

  assertThat(actualList)//
    .containsOnly(this.notificationDto1);
 }

 @Test
 public void testThatNotificationCanBeListedPerProjectAndRepo() throws Exception {
  when(this.settingsService.getNotifications(this.notificationDto1.getProjectKey(),
    this.notificationDto1.getRepositorySlug()))//
      .thenReturn(newArrayList(this.notification1));

  Response actual = this.sut.get(this.notificationDto1.getProjectKey(), this.notificationDto1.getRepositorySlug());
  @SuppressWarnings("unchecked")
  Iterable<NotificationDTO> actualList = (Iterable<NotificationDTO>) actual.getEntity();

  assertThat(actualList)//
    .containsOnly(this.notificationDto1);
 }

}
