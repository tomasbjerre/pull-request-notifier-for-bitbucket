package se.bjurr.prnfb.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static se.bjurr.prnfb.service.SettingsService.STORAGE_KEY;
import static se.bjurr.prnfb.settings.PrnfbNotificationBuilder.prnfbNotificationBuilder;
import static se.bjurr.prnfb.settings.PrnfbSettingsBuilder.prnfbSettingsBuilder;
import static se.bjurr.prnfb.settings.PrnfbSettingsDataBuilder.prnfbSettingsDataBuilder;
import static se.bjurr.prnfb.settings.USER_LEVEL.ADMIN;
import static se.bjurr.prnfb.settings.USER_LEVEL.EVERYONE;

import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

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
 private final PluginSettingsMap pluginSettings = new PluginSettingsMap();
 @Mock
 private PluginSettingsFactory pluginSettingsFactory;
 @Mock
 private SecurityService securityService;
 private TransactionTemplate transactionTemplate;
 private SettingsService sut;
 private EscalatedSecurityContext escalatedSecurityContext;

 @Before
 public void before() {
  initMocks(this);
  when(pluginSettingsFactory.createGlobalSettings())//
    .thenReturn(pluginSettings);
  escalatedSecurityContext = new EscalatedSecurityContext() {
   @Override
   public EscalatedSecurityContext withPermissions(Set<Permission> arg0) {
    return null;
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
   public <T, E extends Throwable> T call(Operation<T, E> arg0) throws E {
    return arg0.perform();
   }

   @Override
   public void applyToRequest() {
   }
  };
  when(securityService.withPermission(Permission.ADMIN, "Getting config"))//
    .thenReturn(escalatedSecurityContext);
  transactionTemplate = new TransactionTemplate() {
   @Override
   public <T> T execute(TransactionCallback<T> action) {
    return action.doInTransaction();
   }
  };
  this.sut = new SettingsService(pluginSettingsFactory, transactionTemplate, securityService);
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
  pluginSettings.getPluginSettingsMap().put(STORAGE_KEY, oldSettingsString);

  PrnfbSettings actual = sut.getPrnfbSettings();
  assertThat(actual)//
    .isEqualTo(oldSettings);
  assertThat(sut.getPrnfbSettingsData())//
    .isEqualTo(oldSettings.getPrnfbSettingsData());
 }

 @Test
 public void testThatSettingsCanBeReadWhenNoneAreSaved() {
  pluginSettings.getPluginSettingsMap().put(STORAGE_KEY, null);

  PrnfbSettings actual = sut.getPrnfbSettings();
  assertThat(actual)//
    .isNotNull();
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

  pluginSettings.getPluginSettingsMap().put(STORAGE_KEY, oldSettingsString);

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

  sut.setPrnfbSettingsData(newSettings.getPrnfbSettingsData());

  String expectedSettingsString = new Gson().toJson(newSettings);
  assertThat(pluginSettings.getPluginSettingsMap().get(STORAGE_KEY))//
    .isEqualTo(expectedSettingsString);

 }

 @Test
 public void testThatButtonCanBeAddedUpdatedAndDeleted() {
  PrnfbButton button1 = new PrnfbButton("title", EVERYONE);
  assertThat(sut.getButtons())//
    .isEmpty();

  sut.addOrUpdateButton(button1);
  assertThat(sut.getButtons())//
    .containsExactly(button1);

  PrnfbButton button2 = new PrnfbButton("title", EVERYONE);
  sut.addOrUpdateButton(button2);
  assertThat(sut.getButtons())//
    .containsExactly(button1, button2);

  PrnfbButton updated = new PrnfbButton(button1.getUuid(), "title2", ADMIN);
  sut.addOrUpdateButton(updated);
  assertThat(sut.getButtons())//
    .containsExactly(button2, updated);

  sut.deleteButton(button1.getUuid());
  assertThat(sut.getButtons())//
    .containsExactly(button2);

  PrnfbButton b2 = sut.getButton(button2.getUuid());
  assertThat(b2)//
    .isEqualTo(button2);
 }

 @Test
 public void testThatNotificationCanBeAddedUpdatedAndDeleted() throws ValidationException {
  PrnfbNotification notification1 = prnfbNotificationBuilder()//
    .withUrl("http://hej.com/")//
    .build();
  assertThat(sut.getButtons())//
    .isEmpty();

  sut.addOrUpdateNotification(notification1);
  assertThat(sut.getNotifications())//
    .containsExactly(notification1);

  PrnfbNotification notification2 = prnfbNotificationBuilder()//
    .withUrl("http://hej.com/")//
    .build();
  sut.addOrUpdateNotification(notification2);
  assertThat(sut.getNotifications())//
    .containsExactly(notification1, notification2);

  PrnfbNotification updated = prnfbNotificationBuilder()//
    .withUuid(notification1.getUuid())//
    .withUrl("http://hej2.com/")//
    .build();
  sut.addOrUpdateNotification(updated);
  assertThat(sut.getNotifications())//
    .containsExactly(notification2, updated);

  sut.deleteNotification(notification1.getUuid());
  assertThat(sut.getNotifications())//
    .containsExactly(notification2);
 }

}
