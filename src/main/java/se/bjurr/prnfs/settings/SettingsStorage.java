package se.bjurr.prnfs.settings;

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
import static se.bjurr.prnfs.admin.AdminFormValues.DEFAULT_NAME;
import static se.bjurr.prnfs.admin.AdminFormValues.NAME;
import static se.bjurr.prnfs.admin.AdminFormValues.VALUE;
import static se.bjurr.prnfs.settings.PrnfsNotification.isOfType;
import static se.bjurr.prnfs.settings.PrnfsNotificationBuilder.prnfsNotificationBuilder;
import static se.bjurr.prnfs.settings.PrnfsPredicates.predicate;
import static se.bjurr.prnfs.settings.PrnfsSettingsBuilder.prnfsSettingsBuilder;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.logging.Logger;

import se.bjurr.prnfs.admin.AdminFormValues;
import se.bjurr.prnfs.admin.AdminFormValues.BUTTON_VISIBILITY;
import se.bjurr.prnfs.listener.PrnfsPullRequestAction;

import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;

public class SettingsStorage {

 private static final Gson gson = new Gson();
 private static Logger logger = Logger.getLogger(SettingsStorage.class.getName());

 private static Random random = new Random(currentTimeMillis());

 public static final String STORAGE_KEY = AdminFormValues.class.getName() + "_2";

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

 private static Map<String, AdminFormValues> getNotificationsMap(PluginSettings pluginSettings) {
  final Map<String, AdminFormValues> allNotificationsMap = newTreeMap();
  for (final AdminFormValues a : getSettingsAsFormValues(pluginSettings)) {
   if (tryFind(a, predicate(AdminFormValues.FIELDS.FORM_IDENTIFIER.name())).isPresent()) {
    allNotificationsMap.put(find(a, predicate(AdminFormValues.FIELDS.FORM_IDENTIFIER.name())).get(VALUE), a);
   }
  }
  return allNotificationsMap;
 }

 public static PrnfsButton getPrnfsButton(AdminFormValues adminFormValues) throws ValidationException {
  final Optional<Map<String, String>> titleOpt = tryFind(adminFormValues,
    predicate(AdminFormValues.FIELDS.button_title.name()));
  String title = "Trigger Notification";
  if (titleOpt.isPresent()) {
   title = titleOpt.get().get(VALUE);
  }

  final Optional<Map<String, String>> visibilityOpt = tryFind(adminFormValues,
    predicate(AdminFormValues.FIELDS.button_visibility.name()));
  BUTTON_VISIBILITY visibility = AdminFormValues.BUTTON_VISIBILITY.NONE;
  if (visibilityOpt.isPresent()) {
   visibility = AdminFormValues.BUTTON_VISIBILITY.valueOf(visibilityOpt.get().get(VALUE));
  }
  return new PrnfsButton(title, visibility, find(adminFormValues,
    predicate(AdminFormValues.FIELDS.FORM_IDENTIFIER.name())).get(VALUE));
 }

 public static PrnfsNotification getPrnfsNotification(AdminFormValues adminFormValues) throws ValidationException {
  final Optional<Map<String, String>> urlOpt = tryFind(adminFormValues, predicate(AdminFormValues.FIELDS.url.name()));
  if (!urlOpt.isPresent()) {
   throw new ValidationException("url", "URL not set");
  }
  final PrnfsNotificationBuilder prnfsNotificationBuilder = prnfsNotificationBuilder().withUrl(urlOpt.get().get(VALUE));
  for (final Map<String, String> event : filter(adminFormValues, predicate(AdminFormValues.FIELDS.events.name()))) {
   prnfsNotificationBuilder.withTrigger(PrnfsPullRequestAction.valueOf(event.get(VALUE)));
  }
  Iterator<Map<String, String>> headerValues = filter(adminFormValues,
    predicate(AdminFormValues.FIELDS.header_value.name())).iterator();
  for (final Map<String, String> headerName : filter(adminFormValues,
    predicate(AdminFormValues.FIELDS.header_name.name()))) {
   if (headerName.get(VALUE).trim().isEmpty()) {
    continue;
   }
   String headerValue = headerValues.next().get(VALUE);
   if (isNullOrEmpty(headerValue)) {
    throw new ValidationException(AdminFormValues.FIELDS.header_value.name(), "Value cannot be null");
   }
   prnfsNotificationBuilder.withHeader(headerName.get(VALUE), headerValue);
  }
  if (tryFind(adminFormValues, predicate(AdminFormValues.FIELDS.proxy_server.name())).isPresent()) {
   prnfsNotificationBuilder
     .withProxyServer(find(adminFormValues, predicate(AdminFormValues.FIELDS.proxy_server.name())).get(VALUE));
  }
  if (tryFind(adminFormValues, predicate(AdminFormValues.FIELDS.proxy_port.name())).isPresent()) {
   prnfsNotificationBuilder.withProxyPort(find(adminFormValues, predicate(AdminFormValues.FIELDS.proxy_port.name()))
     .get(VALUE));
  }
  if (tryFind(adminFormValues, predicate(AdminFormValues.FIELDS.proxy_user.name())).isPresent()) {
   prnfsNotificationBuilder.withProxyUser(find(adminFormValues, predicate(AdminFormValues.FIELDS.proxy_user.name()))
     .get(VALUE));
  }
  if (tryFind(adminFormValues, predicate(AdminFormValues.FIELDS.proxy_password.name())).isPresent()) {
   prnfsNotificationBuilder.withProxyPassword(find(adminFormValues,
     predicate(AdminFormValues.FIELDS.proxy_password.name())).get(VALUE));
  }
  if (tryFind(adminFormValues, predicate(AdminFormValues.FIELDS.user.name())).isPresent()) {
   prnfsNotificationBuilder.withUser(find(adminFormValues, predicate(AdminFormValues.FIELDS.user.name())).get(VALUE));
  }
  if (tryFind(adminFormValues, predicate(AdminFormValues.FIELDS.password.name())).isPresent()) {
   prnfsNotificationBuilder.withPassword(find(adminFormValues, predicate(AdminFormValues.FIELDS.password.name())).get(
     VALUE));
  }
  if (tryFind(adminFormValues, predicate(AdminFormValues.FIELDS.filter_string.name())).isPresent()) {
   prnfsNotificationBuilder.withFilterString(find(adminFormValues,
     predicate(AdminFormValues.FIELDS.filter_string.name())).get(VALUE));
  }
  if (tryFind(adminFormValues, predicate(AdminFormValues.FIELDS.filter_regexp.name())).isPresent()) {
   prnfsNotificationBuilder.withFilterRegexp(find(adminFormValues,
     predicate(AdminFormValues.FIELDS.filter_regexp.name())).get(VALUE));
  }
  if (tryFind(adminFormValues, predicate(AdminFormValues.FIELDS.method.name())).isPresent()) {
   prnfsNotificationBuilder.withMethod(find(adminFormValues, predicate(AdminFormValues.FIELDS.method.name()))
     .get(VALUE));
  }
  if (tryFind(adminFormValues, predicate(AdminFormValues.FIELDS.post_content.name())).isPresent()) {
   prnfsNotificationBuilder
     .withPostContent(find(adminFormValues, predicate(AdminFormValues.FIELDS.post_content.name())).get(VALUE));
  }
  if (tryFind(adminFormValues, predicate(AdminFormValues.FIELDS.name.name())).isPresent()) {
   prnfsNotificationBuilder.withName(find(adminFormValues, predicate(AdminFormValues.FIELDS.name.name())).get(VALUE));
  }
  if (tryFind(adminFormValues, predicate(AdminFormValues.FIELDS.injection_url.name())).isPresent()) {
   prnfsNotificationBuilder.withInjectionUrl(find(adminFormValues,
     predicate(AdminFormValues.FIELDS.injection_url.name())).get(VALUE));
  }
  if (tryFind(adminFormValues, predicate(AdminFormValues.FIELDS.injection_url_xpath.name())).isPresent()) {
   prnfsNotificationBuilder.withInjectionUrlXPath(find(adminFormValues,
     predicate(AdminFormValues.FIELDS.injection_url_xpath.name())).get(VALUE));
  }
  if (tryFind(adminFormValues, predicate(AdminFormValues.FIELDS.injection_url_type.name())).isPresent()) {
   prnfsNotificationBuilder.withInjectionUrlType(AdminFormValues.INEJCTION_TYPE.valueOf(find(adminFormValues,
     predicate(AdminFormValues.FIELDS.injection_url_type.name())).get(VALUE)));
  }
  return prnfsNotificationBuilder.build();
 }

 public static void checkFieldsRecognized(AdminFormValues adminFormValues) throws ValidationException {
  for (final Map<String, String> m : adminFormValues) {
   for (final Entry<String, String> entry : m.entrySet()) {
    if (entry.getKey().equals(NAME)) {
     if (AdminFormValues.FIELDS.valueOf(entry.getValue()) == null) {
      throw new ValidationException(entry.getValue(), "Field not recognized!");
     }
    } else if (!entry.getKey().equals(VALUE)) {
     throw new ValidationException(entry.getKey(), "Key not recognized!");
    }
   }
  }
 }

 public static PrnfsSettings getPrnfsSettings(PluginSettings pluginSettings) throws ValidationException {
  final PrnfsSettingsBuilder prnfsSettingsBuilder = prnfsSettingsBuilder();
  for (final AdminFormValues adminFormValues : getSettingsAsFormValues(pluginSettings)) {
   if (isOfType(adminFormValues, AdminFormValues.FORM_TYPE.TRIGGER_CONFIG_FORM)) {
    prnfsSettingsBuilder.withNotification(getPrnfsNotification(adminFormValues));
   } else {
    prnfsSettingsBuilder.withButton(getPrnfsButton(adminFormValues));
   }
   prnfsSettingsBuilder
     .withUsersAllowed(tryFind(adminFormValues, predicate(AdminFormValues.FIELDS.user_allowed.name())).isPresent());
   prnfsSettingsBuilder.withAdminsAllowed(tryFind(adminFormValues,
     predicate(AdminFormValues.FIELDS.admin_allowed.name())).isPresent());
  }
  return prnfsSettingsBuilder.build();
 }

 public static List<AdminFormValues> getSettingsAsFormValues(PluginSettings settings) {
  final List<AdminFormValues> toReturn = newArrayList();
  try {
   if (!fromNullable(settings.get(STORAGE_KEY)).isPresent()) {
    return toReturn;
   }
   @SuppressWarnings("unchecked")
   final List<String> settingsList = newArrayList((List<String>) settings.get(STORAGE_KEY));
   for (final String storedJson : settingsList) {
    toReturn.add(injectConfigurationName(gson.fromJson(storedJson, AdminFormValues.class)));
   }
  } catch (final Exception e) {
   logger.log(SEVERE, "Unable to deserialize settings", e);
  }
  return toReturn;
 }

 /**
  * Inject a default name for the trigger. To make the plugin backwards
  * compatible.
  */
 private static AdminFormValues injectConfigurationName(AdminFormValues adminFormValues) {
  final Optional<Map<String, String>> nameMapOpt = tryFind(adminFormValues,
    predicate(AdminFormValues.FIELDS.name.name()));
  if (nameMapOpt.isPresent()) {
   return adminFormValues;
  }
  adminFormValues.add(ImmutableMap.<String, String> builder().put(NAME, AdminFormValues.FIELDS.name.name())
    .put(VALUE, DEFAULT_NAME).build());
  return adminFormValues;
 }

 @VisibleForTesting
 public static void setLogger(Logger loggerParam) {
  logger = loggerParam;
 }

 private static void storeNotificationsMap(PluginSettings pluginSettings,
   Map<String, AdminFormValues> allNotificationsMap) throws ValidationException {
  final List<String> toStore = newArrayList();
  for (final AdminFormValues adminFormValues : allNotificationsMap.values()) {
   final Optional<Map<String, String>> formIdOpt = tryFind(adminFormValues,
     predicate(AdminFormValues.FIELDS.FORM_IDENTIFIER.name()));
   if (!formIdOpt.isPresent() || formIdOpt.get().get(VALUE).trim().isEmpty()) {
    throw new ValidationException(AdminFormValues.FIELDS.FORM_IDENTIFIER.name(), "Not set!");
   }
   toStore.add(new Gson().toJson(adminFormValues));
  }
  pluginSettings.put(STORAGE_KEY, toStore);
 }

 public static void storeSettings(PluginSettings pluginSettings, final AdminFormValues config)
   throws ValidationException {
  injectFormIdentifierIfNotSet(config);
  final Map<String, AdminFormValues> allNotificationsMap = getNotificationsMap(pluginSettings);
  allNotificationsMap.put(find(config, predicate(AdminFormValues.FIELDS.FORM_IDENTIFIER.name())).get(VALUE), config);
  storeNotificationsMap(pluginSettings, allNotificationsMap);
 }

 public static void injectFormIdentifierIfNotSet(final AdminFormValues config) {
  final Optional<Map<String, String>> formIdOpt = tryFind(config,
    predicate(AdminFormValues.FIELDS.FORM_IDENTIFIER.name()));
  if (!formIdOpt.isPresent() || formIdOpt.get().get(VALUE).trim().isEmpty()) {
   final String generatedIdentifier = formIdentifierGnerator();
   removeIf(config, predicate(AdminFormValues.FIELDS.FORM_IDENTIFIER.name()));
   config.add(new ImmutableMap.Builder<String, String>().put(NAME, AdminFormValues.FIELDS.FORM_IDENTIFIER.name())
     .put(VALUE, generatedIdentifier).build());
  }
 }
}
