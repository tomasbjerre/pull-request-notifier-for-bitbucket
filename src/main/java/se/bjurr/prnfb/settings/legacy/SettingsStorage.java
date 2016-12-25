package se.bjurr.prnfb.settings.legacy;

import static com.google.common.base.Optional.fromNullable;
import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.find;
import static com.google.common.collect.Iterables.removeIf;
import static com.google.common.collect.Iterables.tryFind;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newTreeMap;
import static java.lang.System.currentTimeMillis;
import static java.util.logging.Level.SEVERE;
import static se.bjurr.prnfb.settings.legacy.AdminFormValues.DEFAULT_NAME;
import static se.bjurr.prnfb.settings.legacy.AdminFormValues.NAME;
import static se.bjurr.prnfb.settings.legacy.AdminFormValues.VALUE;
import static se.bjurr.prnfb.settings.legacy.AdminFormValues.BUTTON_VISIBILITY.NONE;
import static se.bjurr.prnfb.settings.legacy.AdminFormValues.FIELDS.FORM_IDENTIFIER;
import static se.bjurr.prnfb.settings.legacy.AdminFormValues.FIELDS.accept_any_certificate;
import static se.bjurr.prnfb.settings.legacy.AdminFormValues.FIELDS.admin_allowed;
import static se.bjurr.prnfb.settings.legacy.AdminFormValues.FIELDS.button_title;
import static se.bjurr.prnfb.settings.legacy.AdminFormValues.FIELDS.button_visibility;
import static se.bjurr.prnfb.settings.legacy.AdminFormValues.FIELDS.events;
import static se.bjurr.prnfb.settings.legacy.AdminFormValues.FIELDS.filter_regexp;
import static se.bjurr.prnfb.settings.legacy.AdminFormValues.FIELDS.filter_string;
import static se.bjurr.prnfb.settings.legacy.AdminFormValues.FIELDS.header_name;
import static se.bjurr.prnfb.settings.legacy.AdminFormValues.FIELDS.header_value;
import static se.bjurr.prnfb.settings.legacy.AdminFormValues.FIELDS.injection_url;
import static se.bjurr.prnfb.settings.legacy.AdminFormValues.FIELDS.injection_url_regexp;
import static se.bjurr.prnfb.settings.legacy.AdminFormValues.FIELDS.key_store;
import static se.bjurr.prnfb.settings.legacy.AdminFormValues.FIELDS.key_store_password;
import static se.bjurr.prnfb.settings.legacy.AdminFormValues.FIELDS.key_store_type;
import static se.bjurr.prnfb.settings.legacy.AdminFormValues.FIELDS.method;
import static se.bjurr.prnfb.settings.legacy.AdminFormValues.FIELDS.name;
import static se.bjurr.prnfb.settings.legacy.AdminFormValues.FIELDS.password;
import static se.bjurr.prnfb.settings.legacy.AdminFormValues.FIELDS.post_content;
import static se.bjurr.prnfb.settings.legacy.AdminFormValues.FIELDS.proxy_password;
import static se.bjurr.prnfb.settings.legacy.AdminFormValues.FIELDS.proxy_port;
import static se.bjurr.prnfb.settings.legacy.AdminFormValues.FIELDS.proxy_server;
import static se.bjurr.prnfb.settings.legacy.AdminFormValues.FIELDS.proxy_user;
import static se.bjurr.prnfb.settings.legacy.AdminFormValues.FIELDS.trigger_if_isconflicting;
import static se.bjurr.prnfb.settings.legacy.AdminFormValues.FIELDS.trigger_ignore_state;
import static se.bjurr.prnfb.settings.legacy.AdminFormValues.FIELDS.url;
import static se.bjurr.prnfb.settings.legacy.AdminFormValues.FIELDS.user;
import static se.bjurr.prnfb.settings.legacy.AdminFormValues.FIELDS.user_allowed;
import static se.bjurr.prnfb.settings.legacy.AdminFormValues.FIELDS.valueOf;
import static se.bjurr.prnfb.settings.legacy.AdminFormValues.FORM_TYPE.TRIGGER_CONFIG_FORM;
import static se.bjurr.prnfb.settings.legacy.PrnfbNotification.isOfType;
import static se.bjurr.prnfb.settings.legacy.PrnfbNotificationBuilder.prnfbNotificationBuilder;
import static se.bjurr.prnfb.settings.legacy.PrnfbPredicates.predicate;
import static se.bjurr.prnfb.settings.legacy.PrnfbSettingsBuilder.prnfbSettingsBuilder;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.logging.Logger;

import se.bjurr.prnfb.listener.PrnfbPullRequestAction;
import se.bjurr.prnfb.settings.legacy.AdminFormValues.BUTTON_VISIBILITY;

import com.atlassian.bitbucket.pull.PullRequestState;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;

@Deprecated
public class SettingsStorage {

  public static final String STORAGE_KEY = "se.bjurr.prnfb.admin.AdminFormValues_2";
  public static final String STORAGE_KEY_PRNFS = "se.bjurr.prnfs.admin.AdminFormValues_2";

  private static final Gson gson = new Gson();

  private static Logger logger = Logger.getLogger(SettingsStorage.class.getName());
  private static Random random = new Random(currentTimeMillis());

  public static void checkFieldsRecognized(AdminFormValues adminFormValues)
      throws ValidationException {
    for (final Map<String, String> m : adminFormValues) {
      for (final Entry<String, String> entry : m.entrySet()) {
        if (entry.getKey().equals(NAME)) {
          if (valueOf(entry.getValue()) == null) {
            throw new ValidationException(entry.getValue(), "Field not recognized!");
          }
        } else if (!entry.getKey().equals(VALUE)) {
          throw new ValidationException(entry.getKey(), "Key not recognized!");
        }
      }
    }
  }

  public static void deleteSettings(PluginSettings pluginSettings, String id) {
    final Map<String, AdminFormValues> map = getNotificationsMap(pluginSettings);
    map.remove(id);
    try {
      storeNotificationsMap(pluginSettings, map);
    } catch (final ValidationException e) {
      logger.log(SEVERE, "", e);
    }
  }

  @VisibleForTesting
  public static void fakeRandom(Random random) {
    SettingsStorage.random = random;
  }

  public static String formIdentifierGnerator() {
    return random.nextLong() + "";
  }

  @VisibleForTesting
  public static Logger getLogger() {
    return logger;
  }

  public static PrnfbButton getPrnfbButton(AdminFormValues adminFormValues)
      throws ValidationException {
    final Optional<Map<String, String>> titleOpt =
        tryFind(adminFormValues, predicate(button_title.name()));
    String title = "Trigger Notification";
    if (titleOpt.isPresent()) {
      title = titleOpt.get().get(VALUE);
    }

    final Optional<Map<String, String>> visibilityOpt =
        tryFind(adminFormValues, predicate(button_visibility.name()));
    BUTTON_VISIBILITY visibility = NONE;
    if (visibilityOpt.isPresent()) {
      visibility = BUTTON_VISIBILITY.valueOf(visibilityOpt.get().get(VALUE));
    }
    return new PrnfbButton(
        title, visibility, find(adminFormValues, predicate(FORM_IDENTIFIER.name())).get(VALUE));
  }

  public static PrnfbNotification getPrnfbNotification(AdminFormValues adminFormValues)
      throws ValidationException {
    final Optional<Map<String, String>> urlOpt = tryFind(adminFormValues, predicate(url.name()));
    if (!urlOpt.isPresent()) {
      throw new ValidationException("url", "URL not set");
    }
    final PrnfbNotificationBuilder prnfbNotificationBuilder =
        prnfbNotificationBuilder().withUrl(urlOpt.get().get(VALUE));
    for (final Map<String, String> event : filter(adminFormValues, predicate(events.name()))) {
      prnfbNotificationBuilder.withTrigger(PrnfbPullRequestAction.valueOf(event.get(VALUE)));
    }
    Iterator<Map<String, String>> headerValues =
        filter(adminFormValues, predicate(header_value.name())).iterator();
    for (final Map<String, String> headerName :
        filter(adminFormValues, predicate(header_name.name()))) {
      if (headerName.get(VALUE).trim().isEmpty()) {
        continue;
      }
      String headerValue = headerValues.next().get(VALUE);
      if (isNullOrEmpty(headerValue)) {
        throw new ValidationException(header_value.name(), "Value cannot be null");
      }
      prnfbNotificationBuilder.withHeader(headerName.get(VALUE), headerValue);
    }
    if (tryFind(adminFormValues, predicate(proxy_server.name())).isPresent()) {
      prnfbNotificationBuilder.withProxyServer(
          find(adminFormValues, predicate(proxy_server.name())).get(VALUE));
    }
    if (tryFind(adminFormValues, predicate(proxy_port.name())).isPresent()) {
      prnfbNotificationBuilder.withProxyPort(
          find(adminFormValues, predicate(proxy_port.name())).get(VALUE));
    }
    if (tryFind(adminFormValues, predicate(proxy_user.name())).isPresent()) {
      prnfbNotificationBuilder.withProxyUser(
          find(adminFormValues, predicate(proxy_user.name())).get(VALUE));
    }
    if (tryFind(adminFormValues, predicate(proxy_password.name())).isPresent()) {
      prnfbNotificationBuilder.withProxyPassword(
          find(adminFormValues, predicate(proxy_password.name())).get(VALUE));
    }
    if (tryFind(adminFormValues, predicate(user.name())).isPresent()) {
      prnfbNotificationBuilder.withUser(find(adminFormValues, predicate(user.name())).get(VALUE));
    }
    if (tryFind(adminFormValues, predicate(password.name())).isPresent()) {
      prnfbNotificationBuilder.withPassword(
          find(adminFormValues, predicate(password.name())).get(VALUE));
    }
    if (tryFind(adminFormValues, predicate(filter_string.name())).isPresent()) {
      prnfbNotificationBuilder.withFilterString(
          find(adminFormValues, predicate(filter_string.name())).get(VALUE));
    }
    if (tryFind(adminFormValues, predicate(filter_regexp.name())).isPresent()) {
      prnfbNotificationBuilder.withFilterRegexp(
          find(adminFormValues, predicate(filter_regexp.name())).get(VALUE));
    }
    if (tryFind(adminFormValues, predicate(method.name())).isPresent()) {
      prnfbNotificationBuilder.withMethod(
          find(adminFormValues, predicate(method.name())).get(VALUE));
    }
    if (tryFind(adminFormValues, predicate(post_content.name())).isPresent()) {
      prnfbNotificationBuilder.withPostContent(
          find(adminFormValues, predicate(post_content.name())).get(VALUE));
    }
    if (tryFind(adminFormValues, predicate(name.name())).isPresent()) {
      prnfbNotificationBuilder.withName(find(adminFormValues, predicate(name.name())).get(VALUE));
    }
    if (tryFind(adminFormValues, predicate(injection_url.name())).isPresent()) {
      prnfbNotificationBuilder.withInjectionUrl(
          find(adminFormValues, predicate(injection_url.name())).get(VALUE));
    }
    if (tryFind(adminFormValues, predicate(injection_url_regexp.name())).isPresent()) {
      prnfbNotificationBuilder.withInjectionUrlRegexp(
          find(adminFormValues, predicate(injection_url_regexp.name())).get(VALUE));
    }
    if (tryFind(adminFormValues, predicate(trigger_if_isconflicting.name())).isPresent()) {
      prnfbNotificationBuilder.withTriggerIfCanMerge(
          find(adminFormValues, predicate(trigger_if_isconflicting.name())).get(VALUE));
    }
    for (final Map<String, String> event :
        filter(adminFormValues, predicate(trigger_ignore_state.name()))) {
      prnfbNotificationBuilder.withTriggerIgnoreState(PullRequestState.valueOf(event.get(VALUE)));
    }
    return prnfbNotificationBuilder.build();
  }

  public static PrnfbSettings getPrnfbSettings(PluginSettings pluginSettings)
      throws ValidationException {
    final PrnfbSettingsBuilder prnfbSettingsBuilder = prnfbSettingsBuilder();
    for (final AdminFormValues adminFormValues : getSettingsAsFormValues(pluginSettings)) {
      if (isOfType(adminFormValues, TRIGGER_CONFIG_FORM)) {
        prnfbSettingsBuilder.withNotification(getPrnfbNotification(adminFormValues));
      } else {
        prnfbSettingsBuilder.withButton(getPrnfbButton(adminFormValues));
      }

      prnfbSettingsBuilder.withUsersAllowed(
          tryFind(adminFormValues, predicate(user_allowed.name())).isPresent());
      prnfbSettingsBuilder.withAdminsAllowed(
          tryFind(adminFormValues, predicate(admin_allowed.name())).isPresent());

      prnfbSettingsBuilder //
          .withShouldAcceptAnyCertificate(
          tryFind(adminFormValues, predicate(accept_any_certificate.name())).isPresent());
      if (tryFind(adminFormValues, predicate(key_store.name())).isPresent()) {
        prnfbSettingsBuilder.setKeyStore(
            tryFind(adminFormValues, predicate(key_store.name())).get().get(VALUE));
      }
      if (tryFind(adminFormValues, predicate(key_store_type.name())).isPresent()) {
        prnfbSettingsBuilder.setKeyStoreType(
            tryFind(adminFormValues, predicate(key_store_type.name())).get().get(VALUE));
      }
      if (tryFind(adminFormValues, predicate(key_store_password.name())).isPresent()) {
        prnfbSettingsBuilder.setKeyStorePassword(
            tryFind(adminFormValues, predicate(key_store_password.name())).get().get(VALUE));
      }
    }
    return prnfbSettingsBuilder.build();
  }

  public static List<AdminFormValues> getSettingsAsFormValues(PluginSettings settings) {
    final List<AdminFormValues> toReturn = newArrayList();
    try {
      /**
       * The storage key was accidently changed when migrating to Bitbucket 4. This is an attempt to
       * load 1.x settings if they exist.
       */
      Optional<Object> settingsToUse = fromNullable(settings.get(STORAGE_KEY));
      if (!settingsToUse.isPresent()) {
        settingsToUse = fromNullable(settings.get(STORAGE_KEY_PRNFS));
        if (!settingsToUse.isPresent()) {
          return toReturn;
        }
      }
      @SuppressWarnings("unchecked")
      final List<String> settingsList = newArrayList((List<String>) settingsToUse.get());
      for (final String storedJson : settingsList) {
        toReturn.add(injectConfigurationName(gson.fromJson(storedJson, AdminFormValues.class)));
      }
    } catch (final Exception e) {
      logger.log(SEVERE, "Unable to deserialize settings", e);
    }
    return toReturn;
  }

  public static void injectFormIdentifierIfNotSet(final AdminFormValues config) {
    final Optional<Map<String, String>> formIdOpt =
        tryFind(config, predicate(FORM_IDENTIFIER.name()));
    if (!formIdOpt.isPresent() || formIdOpt.get().get(VALUE).trim().isEmpty()) {
      final String generatedIdentifier = formIdentifierGnerator();
      removeIf(config, predicate(FORM_IDENTIFIER.name()));
      config.add(
          new ImmutableMap.Builder<String, String>()
              .put(NAME, FORM_IDENTIFIER.name())
              .put(VALUE, generatedIdentifier)
              .build());
    }
  }

  @VisibleForTesting
  public static void setLogger(Logger loggerParam) {
    logger = loggerParam;
  }

  public static void storeSettings(PluginSettings pluginSettings, final AdminFormValues config)
      throws ValidationException {
    injectFormIdentifierIfNotSet(config);
    final Map<String, AdminFormValues> allNotificationsMap = getNotificationsMap(pluginSettings);
    allNotificationsMap.put(find(config, predicate(FORM_IDENTIFIER.name())).get(VALUE), config);
    storeNotificationsMap(pluginSettings, allNotificationsMap);
  }

  private static Map<String, AdminFormValues> getNotificationsMap(PluginSettings pluginSettings) {
    final Map<String, AdminFormValues> allNotificationsMap = newTreeMap();
    for (final AdminFormValues a : getSettingsAsFormValues(pluginSettings)) {
      if (tryFind(a, predicate(FORM_IDENTIFIER.name())).isPresent()) {
        allNotificationsMap.put(find(a, predicate(FORM_IDENTIFIER.name())).get(VALUE), a);
      }
    }
    return allNotificationsMap;
  }

  /** Inject a default name for the trigger. To make the plugin backwards compatible. */
  private static AdminFormValues injectConfigurationName(AdminFormValues adminFormValues) {
    final Optional<Map<String, String>> nameMapOpt =
        tryFind(adminFormValues, predicate(name.name()));
    if (nameMapOpt.isPresent()) {
      return adminFormValues;
    }
    adminFormValues.add(
        ImmutableMap.<String, String>builder() //
            .put(NAME, name.name()) //
            .put(VALUE, DEFAULT_NAME) //
            .build());
    return adminFormValues;
  }

  private static void storeNotificationsMap(
      PluginSettings pluginSettings, Map<String, AdminFormValues> allNotificationsMap)
      throws ValidationException {
    final List<String> toStore = newArrayList();
    for (final AdminFormValues adminFormValues : allNotificationsMap.values()) {
      final Optional<Map<String, String>> formIdOpt =
          tryFind(adminFormValues, predicate(FORM_IDENTIFIER.name()));
      if (!formIdOpt.isPresent() || formIdOpt.get().get(VALUE).trim().isEmpty()) {
        throw new ValidationException(FORM_IDENTIFIER.name(), "Not set!");
      }
      toStore.add(new Gson().toJson(adminFormValues));
    }
    pluginSettings.put(STORAGE_KEY, toStore);
  }
}
