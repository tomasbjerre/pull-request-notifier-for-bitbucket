package se.bjurr.prnfb.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static se.bjurr.prnfb.listener.PrnfbPullRequestAction.APPROVED;
import static se.bjurr.prnfb.service.SettingsService.STORAGE_KEY;
import static se.bjurr.prnfb.settings.PrnfbNotificationBuilder.prnfbNotificationBuilder;
import static se.bjurr.prnfb.settings.PrnfbSettingsBuilder.prnfbSettingsBuilder;
import static se.bjurr.prnfb.settings.PrnfbSettingsDataBuilder.prnfbSettingsDataBuilder;
import static se.bjurr.prnfb.settings.USER_LEVEL.ADMIN;
import static se.bjurr.prnfb.settings.USER_LEVEL.EVERYONE;
import static se.bjurr.prnfb.test.Podam.populatedInstanceOf;

import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import se.bjurr.prnfb.presentation.dto.ON_OR_OFF;
import se.bjurr.prnfb.settings.PrnfbButton;
import se.bjurr.prnfb.settings.PrnfbNotification;
import se.bjurr.prnfb.settings.PrnfbSettings;
import se.bjurr.prnfb.settings.ValidationException;

import com.atlassian.bitbucket.permission.Permission;
import com.atlassian.bitbucket.user.EscalatedSecurityContext;
import com.atlassian.bitbucket.user.SecurityService;
import com.atlassian.bitbucket.util.Operation;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.transaction.TransactionCallback;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.google.gson.Gson;

public class SettingsServiceTest {
 private EscalatedSecurityContext escalatedSecurityContext;
 private PrnfbNotification notification1;
 private final PluginSettingsMap pluginSettings = new PluginSettingsMap();
 @Mock
 private PluginSettingsFactory pluginSettingsFactory;
 @Mock
 private SecurityService securityService;
 private SettingsService sut;
 private TransactionTemplate transactionTemplate;

 @Before
 public void before() throws ValidationException {
  initMocks(this);
  when(this.pluginSettingsFactory.createGlobalSettings())//
    .thenReturn(this.pluginSettings);
  this.escalatedSecurityContext = new EscalatedSecurityContext() {
   @Override
   public void applyToRequest() {
   }

   @Override
   public <T, E extends Throwable> T call(Operation<T, E> arg0) throws E {
    return arg0.perform();
   }

   @Override
   public EscalatedSecurityContext withPermission(Object arg0, Permission arg1) {
    return null;
   }

   @Override
   public EscalatedSecurityContext withPermission(Permission arg0) {
    return null;
   }

   @Override
   public EscalatedSecurityContext withPermissions(Set<Permission> arg0) {
    return null;
   }
  };
  when(this.securityService.withPermission(Permission.ADMIN, "Getting config"))//
    .thenReturn(this.escalatedSecurityContext);
  this.transactionTemplate = new TransactionTemplate() {
   @Override
   public <T> T execute(TransactionCallback<T> action) {
    return action.doInTransaction();
   }
  };
  this.sut = new SettingsService(this.pluginSettingsFactory, this.transactionTemplate, this.securityService);

  this.notification1 = prnfbNotificationBuilder()//
    .withUrl("http://hej.com/")//
    .withProjectKey("projectKey")//
    .withRepositorySlug("repositorySlug")//
    .withTrigger(APPROVED)//
    .build();
 }

 @Test
 public void testThatButtonCanBeAddedUpdatedAndDeleted() {
  PrnfbButton button1 = new PrnfbButton(null, "title", EVERYONE, ON_OR_OFF.off, "p1", "r1");
  assertThat(this.sut.getButtons())//
    .isEmpty();

  this.sut.addOrUpdateButton(button1);
  assertThat(this.sut.getButtons())//
    .containsExactly(button1);

  PrnfbButton button2 = new PrnfbButton(null, "title", EVERYONE, ON_OR_OFF.off, "p1", "r1");
  this.sut.addOrUpdateButton(button2);
  assertThat(this.sut.getButtons())//
    .containsExactly(button1, button2);

  PrnfbButton updated = new PrnfbButton(button1.getUuid(), "title2", ADMIN, ON_OR_OFF.off, "p1", "r1");
  this.sut.addOrUpdateButton(updated);
  assertThat(this.sut.getButtons())//
    .containsExactly(button2, updated);

  this.sut.deleteButton(button1.getUuid());
  assertThat(this.sut.getButtons())//
    .containsExactly(button2);

  PrnfbButton b2 = this.sut.getButton(button2.getUuid());
  assertThat(b2)//
    .isEqualTo(button2);
  assertThat(b2.hashCode())//
    .isEqualTo(button2.hashCode());
  assertThat(b2.toString())//
    .isEqualTo(button2.toString());
 }

 @Test
 public void testThatButtonsCanBeRetrievedByProject() {
  PrnfbButton button1 = populatedInstanceOf(PrnfbButton.class);
  this.sut.addOrUpdateButton(button1);

  List<PrnfbButton> actual = this.sut.getButtons(button1.getProjectKey().get());

  assertThat(actual)//
    .containsOnly(button1);
 }

 @Test
 public void testThatButtonsCanBeRetrievedByProjectAndRepo() {
  PrnfbButton button1 = populatedInstanceOf(PrnfbButton.class);
  this.sut.addOrUpdateButton(button1);

  List<PrnfbButton> actual = this.sut.getButtons(button1.getProjectKey().get(), button1.getRepositorySlug().get());

  assertThat(actual)//
    .containsOnly(button1);
 }

 @Test
 public void testThatNotificationCanBeAddedUpdatedAndDeleted() throws ValidationException {
  assertThat(this.sut.getButtons())//
    .isEmpty();

  this.sut.addOrUpdateNotification(this.notification1);
  assertThat(this.sut.getNotifications())//
    .containsExactly(this.notification1);

  PrnfbNotification notification2 = prnfbNotificationBuilder()//
    .withUrl("http://hej.com/")//
    .withTrigger(APPROVED)//
    .build();
  this.sut.addOrUpdateNotification(notification2);
  assertThat(this.sut.getNotifications())//
    .containsExactly(this.notification1, notification2);

  PrnfbNotification updated = prnfbNotificationBuilder()//
    .withUuid(this.notification1.getUuid())//
    .withUrl("http://hej2.com/")//
    .withTrigger(APPROVED)//
    .build();
  this.sut.addOrUpdateNotification(updated);
  assertThat(this.sut.getNotifications())//
    .containsExactly(notification2, updated);

  this.sut.deleteNotification(this.notification1.getUuid());
  assertThat(this.sut.getNotifications())//
    .containsExactly(notification2);
  assertThat(this.sut.getNotifications().get(0).toString())//
    .isEqualTo(notification2.toString());
  assertThat(this.sut.getNotifications().get(0).hashCode())//
    .isEqualTo(notification2.hashCode());
 }

 @Test
 public void testThatNotificationsCanBeRetrievedByProject() throws ValidationException {
  this.sut.addOrUpdateNotification(this.notification1);

  List<PrnfbNotification> actual = this.sut.getNotifications(this.notification1.getProjectKey().orNull());

  assertThat(actual)//
    .containsOnly(this.notification1);
 }

 @Test
 public void testThatNotificationsCanBeRetrievedByProjectAndRepo() throws ValidationException {
  this.sut.addOrUpdateNotification(this.notification1);

  List<PrnfbNotification> actual = this.sut.getNotifications(this.notification1.getProjectKey().orNull(),
    this.notification1.getRepositorySlug().orNull());

  assertThat(actual)//
    .containsOnly(this.notification1);
 }

 @Test
 public void testThatNotificationsCanBeRetrievedByUuid() throws ValidationException {
  this.sut.addOrUpdateNotification(this.notification1);

  PrnfbNotification actual = this.sut.getNotification(this.notification1.getUuid());

  assertThat(actual)//
    .isEqualTo(this.notification1);
 }

 @Test
 public void testThatPluginSettingsDataCanBeStored() {
  PrnfbSettings oldSettings = prnfbSettingsBuilder()//
    .setPrnfbSettingsData(//
      prnfbSettingsDataBuilder()//
        .setKeyStore("12")//
        .setKeyStorePassword("22")//
        .setKeyStoreType("33")//
        .setAdminRestriction(EVERYONE)//
        .setShouldAcceptAnyCertificate(false)//
        .build()//
    )//
    .build();
  String oldSettingsString = new Gson().toJson(oldSettings);

  this.pluginSettings.getPluginSettingsMap().put(STORAGE_KEY, oldSettingsString);

  PrnfbSettings newSettings = prnfbSettingsBuilder()//
    .setPrnfbSettingsData(//
      prnfbSettingsDataBuilder()//
        .setKeyStore("keyStore")//
        .setKeyStorePassword("keyStorePassword")//
        .setKeyStoreType("keyStoreType")//
        .setAdminRestriction(EVERYONE)//
        .setShouldAcceptAnyCertificate(true)//
        .build()//
    )//
    .build();

  this.sut.setPrnfbSettingsData(newSettings.getPrnfbSettingsData());

  String expectedSettingsString = new Gson().toJson(newSettings);
  assertThat(this.pluginSettings.getPluginSettingsMap().get(STORAGE_KEY))//
    .isEqualTo(expectedSettingsString);

 }

 @Test
 public void testThatSettingsCanBeRead() {
  PrnfbSettings oldSettings = prnfbSettingsBuilder()//
    .setPrnfbSettingsData(//
      prnfbSettingsDataBuilder()//
        .setKeyStore("12")//
        .setKeyStorePassword("22")//
        .setKeyStoreType("33")//
        .setAdminRestriction(EVERYONE)//
        .setShouldAcceptAnyCertificate(false)//
        .build()//
    )//
    .build();
  String oldSettingsString = new Gson().toJson(oldSettings);
  this.pluginSettings.getPluginSettingsMap().put(STORAGE_KEY, oldSettingsString);

  PrnfbSettings actual = this.sut.getPrnfbSettings();

  assertThat(actual)//
    .isEqualTo(oldSettings);
  assertThat(actual.toString())//
    .isEqualTo(oldSettings.toString());
  assertThat(actual.hashCode())//
    .isEqualTo(oldSettings.hashCode());

  assertThat(this.sut.getPrnfbSettingsData())//
    .isEqualTo(oldSettings.getPrnfbSettingsData());
  assertThat(this.sut.getPrnfbSettingsData().toString())//
    .isEqualTo(oldSettings.getPrnfbSettingsData().toString());
  assertThat(this.sut.getPrnfbSettingsData().hashCode())//
    .isEqualTo(oldSettings.getPrnfbSettingsData().hashCode());
 }

 @Test
 public void testThatSettingsCanBeReadWhenNoneAreSaved() {
  this.pluginSettings.getPluginSettingsMap().put(STORAGE_KEY, null);

  PrnfbSettings actual = this.sut.getPrnfbSettings();
  assertThat(actual)//
    .isNotNull();
 }

}
