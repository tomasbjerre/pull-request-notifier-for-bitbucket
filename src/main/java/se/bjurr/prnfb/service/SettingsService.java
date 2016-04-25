package se.bjurr.prnfb.service;

import static com.atlassian.bitbucket.permission.Permission.ADMIN;
import static com.google.common.base.Predicates.not;
import static com.google.common.base.Throwables.propagate;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.find;
import static com.google.common.collect.Lists.newArrayList;
import static se.bjurr.prnfb.settings.PrnfbSettingsBuilder.prnfbSettingsBuilder;

import java.util.List;
import java.util.UUID;

import se.bjurr.prnfb.settings.HasUuid;
import se.bjurr.prnfb.settings.PrnfbButton;
import se.bjurr.prnfb.settings.PrnfbNotification;
import se.bjurr.prnfb.settings.PrnfbSettings;
import se.bjurr.prnfb.settings.PrnfbSettingsData;
import se.bjurr.prnfb.settings.ValidationException;

import com.atlassian.bitbucket.user.SecurityService;
import com.atlassian.bitbucket.util.Operation;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.transaction.TransactionCallback;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Predicate;
import com.google.gson.Gson;

public class SettingsService {

 public static final String STORAGE_KEY = "se.bjurr.prnfb.pull-request-notifier-for-bitbucket-3";
 private static Gson gson = new Gson();
 private final PluginSettings pluginSettings;
 private final SecurityService securityService;
 private final TransactionTemplate transactionTemplate;

 public SettingsService(PluginSettingsFactory pluginSettingsFactory, TransactionTemplate transactionTemplate,
   SecurityService securityService) {
  this.pluginSettings = pluginSettingsFactory.createGlobalSettings();
  this.transactionTemplate = transactionTemplate;
  this.securityService = securityService;
 }

 public void addOrUpdateButton(PrnfbButton prnfbButton) {
  inSynchronizedTransaction(new TransactionCallback<Void>() {
   @Override
   public Void doInTransaction() {
    doAddOrUpdateButton(prnfbButton);
    return null;
   }
  });
 }

 public void addOrUpdateNotification(PrnfbNotification prnfbNotification) throws ValidationException {
  inSynchronizedTransaction(new TransactionCallback<Void>() {
   @Override
   public Void doInTransaction() {
    try {
     doAddOrUpdateNotification(prnfbNotification);
    } catch (ValidationException e) {
     propagate(e);
    }
    return null;
   }
  });
 }

 public void deleteButton(UUID uuid) {
  inSynchronizedTransaction(new TransactionCallback<Void>() {
   @Override
   public Void doInTransaction() {
    doDeleteButton(uuid);
    return null;
   }
  });
 }

 public void deleteNotification(UUID uuid) {
  inSynchronizedTransaction(new TransactionCallback<Void>() {
   @Override
   public Void doInTransaction() {
    doDeleteNotification(uuid);
    return null;
   }
  });
 }

 public PrnfbButton getButton(UUID buttionUuid) {
  return find(getButtons(), withUuid(buttionUuid));
 }

 public List<PrnfbButton> getButtons() {
  return getPrnfbSettings().getButtons();
 }

 public PrnfbNotification getNotification(UUID notificationUuid) {
  for (PrnfbNotification prnfbNotification : getPrnfbSettings().getNotifications()) {
   if (prnfbNotification.getUuid().equals(notificationUuid)) {
    return prnfbNotification;
   }
  }
  throw new RuntimeException("Cant find notification " + notificationUuid);
 }

 public List<PrnfbNotification> getNotifications() {
  return getPrnfbSettings().getNotifications();
 }

 @VisibleForTesting
 public PrnfbSettings getPrnfbSettings() {
  return inSynchronizedTransaction(new TransactionCallback<PrnfbSettings>() {
   @Override
   public PrnfbSettings doInTransaction() {
    return doGetPrnfbSettings();
   }
  });
 }

 public PrnfbSettingsData getPrnfbSettingsData() {
  return getPrnfbSettings().getPrnfbSettingsData();
 }

 public void setPrnfbSettingsData(PrnfbSettingsData prnfbSettingsData) {
  inSynchronizedTransaction(new TransactionCallback<Void>() {
   @Override
   public Void doInTransaction() {
    PrnfbSettings oldSettings = doGetPrnfbSettings();
    PrnfbSettings newPrnfbSettings = prnfbSettingsBuilder(oldSettings)//
      .setPrnfbSettingsData(prnfbSettingsData)//
      .build();
    doSetPrnfbSettings(newPrnfbSettings);
    return null;
   }
  });
 }

 private void doAddOrUpdateButton(PrnfbButton prnfbButton) {
  doDeleteButton(prnfbButton.getUuid());

  PrnfbSettings originalSettings = doGetPrnfbSettings();
  PrnfbSettings updated = prnfbSettingsBuilder(originalSettings)//
    .withButton(prnfbButton)//
    .build();

  doSetPrnfbSettings(updated);
 }

 private void doAddOrUpdateNotification(PrnfbNotification prnfbNotification) throws ValidationException {
  doDeleteNotification(prnfbNotification.getUuid());

  PrnfbSettings originalSettings = doGetPrnfbSettings();
  PrnfbSettings updated = prnfbSettingsBuilder(originalSettings)//
    .withNotification(prnfbNotification)//
    .build();

  doSetPrnfbSettings(updated);
 }

 private void doDeleteButton(UUID uuid) {
  PrnfbSettings originalSettings = doGetPrnfbSettings();
  List<PrnfbButton> keep = newArrayList(filter(originalSettings.getButtons(), not(withUuid(uuid))));
  PrnfbSettings withoutDeleted = prnfbSettingsBuilder(originalSettings)//
    .setButtons(keep)//
    .build();
  doSetPrnfbSettings(withoutDeleted);
 }

 private void doDeleteNotification(UUID uuid) {
  PrnfbSettings originalSettings = doGetPrnfbSettings();
  List<PrnfbNotification> keep = newArrayList(filter(originalSettings.getNotifications(), not(withUuid(uuid))));
  PrnfbSettings withoutDeleted = prnfbSettingsBuilder(originalSettings)//
    .setNotifications(keep)//
    .build();
  doSetPrnfbSettings(withoutDeleted);
 }

 private PrnfbSettings doGetPrnfbSettings() {
  Object storedSettings = this.pluginSettings.get(STORAGE_KEY);
  if (storedSettings == null) {
   return prnfbSettingsBuilder().build();
  }
  return gson.fromJson(storedSettings.toString(), PrnfbSettings.class);
 }

 private void doSetPrnfbSettings(PrnfbSettings PrnfbSettings) {
  String data = gson.toJson(PrnfbSettings);
  this.pluginSettings.put(STORAGE_KEY, data);
 }

 private synchronized <T> T inSynchronizedTransaction(TransactionCallback<T> transactionCallback) {
  return this.securityService//
    .withPermission(ADMIN, "Getting config")//
    .call(new Operation<T, RuntimeException>() {
     @Override
     public T perform() throws RuntimeException {
      return SettingsService.this.transactionTemplate.execute(transactionCallback);
     }
    });
 }

 private Predicate<HasUuid> withUuid(UUID uuid) {
  return new Predicate<HasUuid>() {
   @Override
   public boolean apply(HasUuid input) {
    return input.getUuid().equals(uuid);
   }
  };
 }

}
