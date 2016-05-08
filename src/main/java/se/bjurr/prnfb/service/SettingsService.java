package se.bjurr.prnfb.service;

import static com.atlassian.bitbucket.permission.Permission.ADMIN;
import static com.google.common.base.Predicates.not;
import static com.google.common.base.Throwables.propagate;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.find;
import static com.google.common.collect.Iterables.tryFind;
import static com.google.common.collect.Lists.newArrayList;
import static se.bjurr.prnfb.settings.PrnfbNotificationBuilder.prnfbNotificationBuilder;
import static se.bjurr.prnfb.settings.PrnfbSettingsBuilder.prnfbSettingsBuilder;
import static se.bjurr.prnfb.settings.PrnfbSettingsDataBuilder.prnfbSettingsDataBuilder;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.bjurr.prnfb.listener.PrnfbPullRequestAction;
import se.bjurr.prnfb.settings.HasUuid;
import se.bjurr.prnfb.settings.PrnfbButton;
import se.bjurr.prnfb.settings.PrnfbNotification;
import se.bjurr.prnfb.settings.PrnfbNotificationBuilder;
import se.bjurr.prnfb.settings.PrnfbSettings;
import se.bjurr.prnfb.settings.PrnfbSettingsData;
import se.bjurr.prnfb.settings.TRIGGER_IF_MERGE;
import se.bjurr.prnfb.settings.USER_LEVEL;
import se.bjurr.prnfb.settings.ValidationException;
import se.bjurr.prnfb.settings.legacy.AdminFormValues.BUTTON_VISIBILITY;
import se.bjurr.prnfb.settings.legacy.Header;
import se.bjurr.prnfb.settings.legacy.SettingsStorage;

import com.atlassian.bitbucket.pull.PullRequestState;
import com.atlassian.bitbucket.user.SecurityService;
import com.atlassian.bitbucket.util.Operation;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.transaction.TransactionCallback;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.gson.Gson;

public class SettingsService {

 public static final String STORAGE_KEY = "se.bjurr.prnfb.pull-request-notifier-for-bitbucket-3";
 private static Gson gson = new Gson();
 private final Logger logger = LoggerFactory.getLogger(SettingsService.class);
 private final PluginSettings pluginSettings;
 private final SecurityService securityService;
 private final TransactionTemplate transactionTemplate;

 public SettingsService(PluginSettingsFactory pluginSettingsFactory, TransactionTemplate transactionTemplate,
   SecurityService securityService) {
  this.pluginSettings = pluginSettingsFactory.createGlobalSettings();
  this.transactionTemplate = transactionTemplate;
  this.securityService = securityService;
 }

 public PrnfbButton addOrUpdateButton(PrnfbButton prnfbButton) {
  return inSynchronizedTransaction(new TransactionCallback<PrnfbButton>() {
   @Override
   public PrnfbButton doInTransaction() {
    return doAddOrUpdateButton(prnfbButton);
   }
  });
 }

 public PrnfbNotification addOrUpdateNotification(PrnfbNotification prnfbNotification) throws ValidationException {
  return inSynchronizedTransaction(new TransactionCallback<PrnfbNotification>() {
   @Override
   public PrnfbNotification doInTransaction() {
    try {
     return doAddOrUpdateNotification(prnfbNotification);
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

 public Optional<PrnfbButton> findButton(UUID uuid) {
  return tryFind(getPrnfbSettings().getButtons(), withUuid(uuid));
 }

 public Optional<PrnfbNotification> findNotification(UUID notificationUuid) {
  return tryFind(getPrnfbSettings().getNotifications(), withUuid(notificationUuid));
 }

 public PrnfbButton getButton(UUID buttionUuid) {
  return find(getButtons(), withUuid(buttionUuid));
 }

 public List<PrnfbButton> getButtons() {
  return getPrnfbSettings().getButtons();
 }

 public List<PrnfbButton> getButtons(String projectKey) {
  List<PrnfbButton> found = newArrayList();
  for (PrnfbButton candidate : getPrnfbSettings().getButtons()) {
   if (candidate.getProjectKey().isPresent() && candidate.getProjectKey().get().equals(projectKey)) {
    found.add(candidate);
   }
  }
  return found;
 }

 public List<PrnfbButton> getButtons(String projectKey, String repositorySlug) {
  List<PrnfbButton> found = newArrayList();
  for (PrnfbButton candidate : getPrnfbSettings().getButtons()) {
   if (candidate.getProjectKey().isPresent() && candidate.getProjectKey().get().equals(projectKey)//
     && candidate.getRepositorySlug().isPresent() && candidate.getRepositorySlug().get().equals(repositorySlug)) {
    found.add(candidate);
   }
  }
  return found;
 }

 public PrnfbNotification getNotification(UUID notificationUuid) {
  return find(getPrnfbSettings().getNotifications(), withUuid(notificationUuid));
 }

 public List<PrnfbNotification> getNotifications() {
  return getPrnfbSettings().getNotifications();
 }

 public List<PrnfbNotification> getNotifications(String projectKey) {
  List<PrnfbNotification> found = newArrayList();
  for (PrnfbNotification candidate : getPrnfbSettings().getNotifications()) {
   if (candidate.getProjectKey().isPresent() && candidate.getProjectKey().get().equals(projectKey)) {
    found.add(candidate);
   }
  }
  return found;
 }

 public List<PrnfbNotification> getNotifications(String projectKey, String repositorySlug) {
  List<PrnfbNotification> found = newArrayList();
  for (PrnfbNotification candidate : getPrnfbSettings().getNotifications()) {
   if (candidate.getProjectKey().isPresent() && candidate.getProjectKey().get().equals(projectKey)//
     && candidate.getRepositorySlug().isPresent() && candidate.getRepositorySlug().get().equals(repositorySlug)) {
    found.add(candidate);
   }
  }
  return found;
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

 private PrnfbButton doAddOrUpdateButton(PrnfbButton prnfbButton) {
  if (findButton(prnfbButton.getUuid()).isPresent()) {
   doDeleteButton(prnfbButton.getUuid());
  }

  PrnfbSettings originalSettings = doGetPrnfbSettings();
  PrnfbSettings updated = prnfbSettingsBuilder(originalSettings)//
    .withButton(prnfbButton)//
    .build();

  doSetPrnfbSettings(updated);
  return prnfbButton;
 }

 private PrnfbNotification doAddOrUpdateNotification(PrnfbNotification prnfbNotification) throws ValidationException {
  if (findNotification(prnfbNotification.getUuid()).isPresent()) {
   doDeleteNotification(prnfbNotification.getUuid());
  }

  PrnfbSettings originalSettings = doGetPrnfbSettings();
  PrnfbSettings updated = prnfbSettingsBuilder(originalSettings)//
    .withNotification(prnfbNotification)//
    .build();

  doSetPrnfbSettings(updated);
  return prnfbNotification;
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
   try {
    se.bjurr.prnfb.settings.legacy.PrnfbSettings oldSettings = SettingsStorage.getPrnfbSettings(this.pluginSettings);

    String ks = oldSettings.getKeyStore().orNull();
    String ksp = oldSettings.getKeyStorePassword().orNull();
    String kst = oldSettings.getKeyStoreType();
    USER_LEVEL adminRestr = USER_LEVEL.SYSTEM_ADMIN;
    if (oldSettings.isAdminsAllowed()) {
     adminRestr = USER_LEVEL.ADMIN;
    }
    if (oldSettings.isUsersAllowed()) {
     adminRestr = USER_LEVEL.EVERYONE;
    }

    boolean shouldAcceptAnyCertificate = false;

    List<PrnfbButton> newButtons = newArrayList();
    for (se.bjurr.prnfb.settings.legacy.PrnfbButton oldButton : oldSettings.getButtons()) {
     USER_LEVEL userLevel = USER_LEVEL.SYSTEM_ADMIN;
     if (oldButton.getVisibility() == BUTTON_VISIBILITY.ADMIN) {
      userLevel = USER_LEVEL.ADMIN;
     }
     if (oldButton.getVisibility() == BUTTON_VISIBILITY.EVERYONE) {
      userLevel = USER_LEVEL.EVERYONE;
     }
     newButtons.add(new PrnfbButton(UUID.randomUUID(), oldButton.getTitle(), userLevel, null, null));
    }

    List<PrnfbNotification> newNotifications = newArrayList();
    for (se.bjurr.prnfb.settings.legacy.PrnfbNotification oldNotification : oldSettings.getNotifications()) {
     try {
      PrnfbNotificationBuilder builder = prnfbNotificationBuilder()//
        .withFilterRegexp(oldNotification.getFilterRegexp().orNull())//
        .withFilterString(oldNotification.getFilterString().orNull())//
        .withInjectionUrl(oldNotification.getInjectionUrl().orNull())//
        .withInjectionUrlRegexp(oldNotification.getInjectionUrlRegexp().orNull())//
        .withMethod(oldNotification.getMethod())//
        .withName(oldNotification.getName())//
        .withPassword(oldNotification.getPassword().orNull())//
        .withPostContent(oldNotification.getPostContent().orNull())//
        .withProxyPassword(oldNotification.getProxyPassword().orNull())//
        .withProxyPort(oldNotification.getProxyPort())//
        .withProxyServer(oldNotification.getProxyServer().orNull())//
        .withProxyUser(oldNotification.getProxyUser().orNull())//
        .withTriggerIfCanMerge(TRIGGER_IF_MERGE.valueOf(oldNotification.getTriggerIfCanMerge().name()))//
        .withUrl(oldNotification.getUrl())//
        .withUser(oldNotification.getUser().orNull());

      for (Header h : oldNotification.getHeaders()) {
       builder.withHeader(h.getName(), h.getValue());
      }

      for (PullRequestState t : oldNotification.getTriggerIgnoreStateList()) {
       builder.withTriggerIgnoreState(t);
      }

      for (PrnfbPullRequestAction t : oldNotification.getTriggers()) {
       builder.withTrigger(t);
      }

      newNotifications.add(builder.build());
     } catch (ValidationException e) {
      this.logger.error("", e);
     }
    }

    return prnfbSettingsBuilder()//
      .setPrnfbSettingsData(//
        prnfbSettingsDataBuilder()//
          .setAdminRestriction(adminRestr)//
          .setKeyStore(ks)//
          .setKeyStorePassword(ksp)//
          .setKeyStoreType(kst)//
          .setShouldAcceptAnyCertificate(shouldAcceptAnyCertificate)//
          .build())//
      .setButtons(newButtons)//
      .setNotifications(newNotifications)//
      .build();

   } catch (se.bjurr.prnfb.settings.legacy.ValidationException e) {
    this.logger.error("", e);
   }
   return prnfbSettingsBuilder()//
     .setPrnfbSettingsData(//
       prnfbSettingsDataBuilder()//
         .setAdminRestriction(USER_LEVEL.ADMIN)//
         .build())//
     .build();
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
