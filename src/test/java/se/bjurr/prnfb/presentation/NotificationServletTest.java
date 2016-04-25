package se.bjurr.prnfb.presentation;

import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static se.bjurr.prnfb.transformer.NotificationTransformer.toPrnfbNotification;

import java.util.List;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import se.bjurr.prnfb.presentation.dto.NotificationDTO;
import se.bjurr.prnfb.service.SettingsService;
import se.bjurr.prnfb.service.UserCheckService;
import se.bjurr.prnfb.settings.PrnfbNotification;
import uk.co.jemos.podam.api.PodamFactoryImpl;

public class NotificationServletTest {
 @Mock
 private SettingsService settingsService;
 @Mock
 private UserCheckService userCheckService;
 private NotificationServlet sut;

 @Before
 public void before() throws Exception {
  initMocks(this);
  when(userCheckService.isViewAllowed())//
    .thenReturn(true);
  when(userCheckService.isAdminAllowed())//
    .thenReturn(true);
  sut = new NotificationServlet(settingsService, userCheckService);
 }

 @Test
 public void testNotificationCanBeCreated() throws Exception {
  NotificationDTO incomingDto = new PodamFactoryImpl().manufacturePojo(NotificationDTO.class);
  incomingDto.setUrl("http://hej.com/");

  sut.create(incomingDto);

  PrnfbNotification expectedSavedSettings = toPrnfbNotification(incomingDto);
  verify(settingsService)//
    .addOrUpdateNotification(eq(expectedSavedSettings));
 }

 @SuppressWarnings("unchecked")
 @Test
 public void testNotificationCanBeRead() throws Exception {
  NotificationDTO notificationDto1 = new PodamFactoryImpl().manufacturePojo(NotificationDTO.class);
  notificationDto1.setUrl("http://hej.com/");
  NotificationDTO notificationDto2 = new PodamFactoryImpl().manufacturePojo(NotificationDTO.class);
  notificationDto2.setUrl("http://hej.com/");
  PrnfbNotification notification1 = toPrnfbNotification(notificationDto1);
  PrnfbNotification notification2 = toPrnfbNotification(notificationDto2);
  List<PrnfbNotification> storedSettings = newArrayList(notification1, notification2);
  when(settingsService.getNotifications())//
    .thenReturn(storedSettings);

  List<NotificationDTO> actual = (List<NotificationDTO>) sut.get().getEntity();

  assertThat(actual)//
    .containsOnly(notificationDto1, notificationDto2);
 }

 @Test
 public void testNotificationCanBeDeleted() throws Exception {
  UUID notificationUid = UUID.randomUUID();

  sut.delete(notificationUid);

  verify(settingsService)//
    .deleteNotification(notificationUid);
 }

}
