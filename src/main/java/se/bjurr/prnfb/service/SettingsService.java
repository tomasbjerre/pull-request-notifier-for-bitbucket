package se.bjurr.prnfb.service;

import static com.atlassian.bitbucket.permission.Permission.ADMIN;
import static com.google.common.base.Joiner.on;
import static com.google.common.base.Predicates.not;
import static com.google.common.base.Throwables.propagate;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.find;
import static com.google.common.collect.Iterables.tryFind;
import static com.google.common.collect.Lists.newArrayList;
import static se.bjurr.prnfb.settings.PrnfbNotificationBuilder.prnfbNotificationBuilder;
import static se.bjurr.prnfb.settings.PrnfbSettings.UNCHANGED;
import static se.bjurr.prnfb.settings.PrnfbSettingsBuilder.prnfbSettingsBuilder;
import static se.bjurr.prnfb.settings.PrnfbSettingsDataBuilder.prnfbSettingsDataBuilder;

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
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.bjurr.prnfb.settings.HasUuid;
import se.bjurr.prnfb.settings.PrnfbButton;
import se.bjurr.prnfb.settings.PrnfbNotification;
import se.bjurr.prnfb.settings.PrnfbSettings;
import se.bjurr.prnfb.settings.PrnfbSettingsData;
import se.bjurr.prnfb.settings.USER_LEVEL;
import se.bjurr.prnfb.settings.ValidationException;

public class SettingsService {

  public static final String STORAGE_KEY = "se.bjurr.prnfb.pull-request-notifier-for-bitbucket-3";
  private static Gson gson = new Gson();
  private final Logger logger = LoggerFactory.getLogger(SettingsService.class);
  private final PluginSettings pluginSettings;
  private final SecurityService securityService;
  private final TransactionTemplate transactionTemplate;

  public SettingsService(
      PluginSettingsFactory pluginSettingsFactory,
      TransactionTemplate transactionTemplate,
      SecurityService securityService) {
    this.pluginSettings = pluginSettingsFactory.createGlobalSettings();
    this.transactionTemplate = transactionTemplate;
    this.securityService = securityService;
  }

  public PrnfbButton addOrUpdateButton(PrnfbButton prnfbButton) {
    return inSynchronizedTransaction(
        new TransactionCallback<PrnfbButton>() {
          @Override
          public PrnfbButton doInTransaction() {
            return doAddOrUpdateButton(prnfbButton);
          }
        });
  }

  public PrnfbNotification addOrUpdateNotification(PrnfbNotification prnfbNotification)
      throws ValidationException {
    return inSynchronizedTransaction(
        new TransactionCallback<PrnfbNotification>() {
          @Override
          public PrnfbNotification doInTransaction() {
            try {
              return doAddOrUpdateNotification(prnfbNotification);
            } catch (final ValidationException e) {
              propagate(e);
            }
            return null;
          }
        });
  }

  public void deleteButton(UUID uuid) {
    inSynchronizedTransaction(
        new TransactionCallback<Void>() {
          @Override
          public Void doInTransaction() {
            doDeleteButton(uuid);
            return null;
          }
        });
  }

  public void deleteNotification(UUID uuid) {
    inSynchronizedTransaction(
        new TransactionCallback<Void>() {
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
    final Optional<PrnfbButton> foundOpt = findButton(buttionUuid);
    if (!foundOpt.isPresent()) {
      throw new RuntimeException(buttionUuid + " not fond in:\n" + on('\n').join(getButtons()));
    }
    return foundOpt.get();
  }

  public List<PrnfbButton> getButtons() {
    return getPrnfbSettings().getButtons();
  }

  public List<PrnfbButton> getButtons(String projectKey) {
    final List<PrnfbButton> found = newArrayList();
    for (final PrnfbButton candidate : getPrnfbSettings().getButtons()) {
      if (candidate.getProjectKey().isPresent()
          && candidate.getProjectKey().get().equals(projectKey)) {
        found.add(candidate);
      }
    }
    return found;
  }

  public List<PrnfbButton> getButtons(String projectKey, String repositorySlug) {
    final List<PrnfbButton> found = newArrayList();
    for (final PrnfbButton candidate : getPrnfbSettings().getButtons()) {
      if (candidate.getProjectKey().isPresent()
          && candidate.getProjectKey().get().equals(projectKey) //
          && candidate.getRepositorySlug().isPresent()
          && candidate.getRepositorySlug().get().equals(repositorySlug)) {
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
    final List<PrnfbNotification> found = newArrayList();
    for (final PrnfbNotification candidate : getPrnfbSettings().getNotifications()) {
      if (candidate.getProjectKey().isPresent()
          && candidate.getProjectKey().get().equals(projectKey)) {
        found.add(candidate);
      }
    }
    return found;
  }

  public List<PrnfbNotification> getNotifications(String projectKey, String repositorySlug) {
    final List<PrnfbNotification> found = newArrayList();
    for (final PrnfbNotification candidate : getPrnfbSettings().getNotifications()) {
      if (candidate.getProjectKey().isPresent()
          && candidate.getProjectKey().get().equals(projectKey) //
          && candidate.getRepositorySlug().isPresent()
          && candidate.getRepositorySlug().get().equals(repositorySlug)) {
        found.add(candidate);
      }
    }
    return found;
  }

  @VisibleForTesting
  public PrnfbSettings getPrnfbSettings() {
    return inSynchronizedTransaction(
        new TransactionCallback<PrnfbSettings>() {
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
    inSynchronizedTransaction(
        new TransactionCallback<Void>() {
          @Override
          public Void doInTransaction() {
            final PrnfbSettings oldSettings = doGetPrnfbSettings();
            final PrnfbSettings newPrnfbSettings =
                prnfbSettingsBuilder(oldSettings) //
                    .setPrnfbSettingsData(prnfbSettingsData) //
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

    final PrnfbSettings originalSettings = doGetPrnfbSettings();
    final PrnfbSettings updated =
        prnfbSettingsBuilder(originalSettings) //
            .withButton(prnfbButton) //
            .build();

    doSetPrnfbSettings(updated);
    return prnfbButton;
  }

  private PrnfbNotification doAddOrUpdateNotification(PrnfbNotification newNotification)
      throws ValidationException {
    final UUID notificationUuid = newNotification.getUuid();

    Optional<String> oldUser = Optional.absent();
    Optional<String> oldPassword = Optional.absent();
    Optional<String> oldProxyUser = Optional.absent();
    Optional<String> oldProxyPassword = Optional.absent();
    final Optional<PrnfbNotification> oldNotification = findNotification(notificationUuid);
    if (oldNotification.isPresent()) {
      oldUser = oldNotification.get().getUser();
      oldPassword = oldNotification.get().getPassword();
      oldProxyUser = oldNotification.get().getProxyUser();
      oldProxyPassword = oldNotification.get().getProxyPassword();
    }

    final String user = keepIfUnchanged(newNotification.getUser(), oldUser);
    final String password = keepIfUnchanged(newNotification.getPassword(), oldPassword);
    final String proxyUser = keepIfUnchanged(newNotification.getProxyUser(), oldProxyUser);
    final String proxyPassword =
        keepIfUnchanged(newNotification.getProxyPassword(), oldProxyPassword);
    newNotification =
        prnfbNotificationBuilder(newNotification) //
            .withUser(user) //
            .withPassword(password) //
            .withProxyUser(proxyUser) //
            .withProxyPassword(proxyPassword) //
            .build();

    if (oldNotification.isPresent()) {
      doDeleteNotification(notificationUuid);
    }

    final PrnfbSettings originalSettings = doGetPrnfbSettings();
    final PrnfbSettings updated =
        prnfbSettingsBuilder(originalSettings) //
            .withNotification(newNotification) //
            .build();

    doSetPrnfbSettings(updated);
    return newNotification;
  }

  private String keepIfUnchanged(Optional<String> newValue, Optional<String> oldValue) {
    final boolean isUnchanged = newValue.isPresent() && newValue.get().equals(UNCHANGED);
    if (isUnchanged) {
      return oldValue.orNull();
    }
    return newValue.orNull();
  }

  private void doDeleteButton(UUID uuid) {
    final PrnfbSettings originalSettings = doGetPrnfbSettings();
    final List<PrnfbButton> keep =
        newArrayList(filter(originalSettings.getButtons(), not(withUuid(uuid))));
    final PrnfbSettings withoutDeleted =
        prnfbSettingsBuilder(originalSettings) //
            .setButtons(keep) //
            .build();
    doSetPrnfbSettings(withoutDeleted);
  }

  private void doDeleteNotification(UUID uuid) {
    final PrnfbSettings originalSettings = doGetPrnfbSettings();
    final List<PrnfbNotification> keep =
        newArrayList(filter(originalSettings.getNotifications(), not(withUuid(uuid))));
    final PrnfbSettings withoutDeleted =
        prnfbSettingsBuilder(originalSettings) //
            .setNotifications(keep) //
            .build();
    doSetPrnfbSettings(withoutDeleted);
  }

  private PrnfbSettings doGetPrnfbSettings() {
    final Object storedSettings = this.pluginSettings.get(STORAGE_KEY);
    if (storedSettings == null) {
      this.logger.info("Creating new default settings.");
      return prnfbSettingsBuilder() //
          .setPrnfbSettingsData( //
              prnfbSettingsDataBuilder() //
                  .setAdminRestriction(USER_LEVEL.ADMIN) //
                  .build()) //
          .build();
    }
    return gson.fromJson(storedSettings.toString(), PrnfbSettings.class);
  }

  private void doSetPrnfbSettings(PrnfbSettings newSettings) {
    final PrnfbSettingsData oldSettingsData = doGetPrnfbSettings().getPrnfbSettingsData();
    final PrnfbSettingsData newSettingsData = newSettings.getPrnfbSettingsData();
    final String keyStorePassword =
        keepIfUnchanged(
            newSettingsData.getKeyStorePassword(), oldSettingsData.getKeyStorePassword());

    final PrnfbSettingsData adjustedSettingsData =
        prnfbSettingsDataBuilder(newSettingsData) //
            .setKeyStorePassword(keyStorePassword) //
            .build();

    final PrnfbSettings adjustedSettings =
        prnfbSettingsBuilder(newSettings) //
            .setPrnfbSettingsData(adjustedSettingsData) //
            .build();

    final String data = gson.toJson(adjustedSettings);
    this.pluginSettings.put(STORAGE_KEY, data);
  }

  private synchronized <T> T inSynchronizedTransaction(TransactionCallback<T> transactionCallback) {
    return this.securityService //
        .withPermission(ADMIN, "Getting config") //
        .call(
            new Operation<T, RuntimeException>() {
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
