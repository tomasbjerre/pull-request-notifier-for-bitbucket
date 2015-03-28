package se.bjurr.prnfs.settings;

import static com.google.common.base.Optional.fromNullable;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.find;
import static com.google.common.collect.Iterables.removeIf;
import static com.google.common.collect.Iterables.tryFind;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newTreeMap;
import static java.lang.System.currentTimeMillis;
import static se.bjurr.prnfs.admin.AdminFormValues.NAME;
import static se.bjurr.prnfs.admin.AdminFormValues.VALUE;
import static se.bjurr.prnfs.settings.PrnfsNotificationBuilder.prnfsNotificationBuilder;
import static se.bjurr.prnfs.settings.PrnfsPredicates.predicate;
import static se.bjurr.prnfs.settings.PrnfsSettingsBuilder.prnfsSettingsBuilder;

import java.util.List;
import java.util.Map;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.bjurr.prnfs.admin.AdminFormValues;

import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.stash.pull.PullRequestAction;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.gson.Gson;

public class SettingsStorage {
 /**
  * Every form should have a field with this name, with a unique value.
  */
 public static final String FORM_IDENTIFIER_NAME = "FORM_IDENTIFIER";

 private static final Gson gson = new Gson();
 private static final Logger logger = LoggerFactory.getLogger(SettingsStorage.class);

 private static final String STORAGE_KEY = AdminFormValues.class.getName() + "_2";

 private static Random random = new Random(currentTimeMillis());

 public static void deleteSettings(PluginSettings pluginSettings, String id) {
  final Map<String, AdminFormValues> map = getNotificationsMap(pluginSettings);
  map.remove(id);
  try {
   storeNotificationsMap(pluginSettings, map);
  } catch (final ValidationException e) {
   logger.error("", e);
  }
 }

 @VisibleForTesting
 public static void fakeRandom(Random random) {
  SettingsStorage.random = random;
 }

 public static String formIdentifierGnerator() {
  return random.nextLong() + "";
 }

 private static Map<String, AdminFormValues> getNotificationsMap(PluginSettings pluginSettings) {
  final Map<String, AdminFormValues> allNotificationsMap = newTreeMap();
  for (final AdminFormValues a : getSettingsAsFormValues(pluginSettings)) {
   if (tryFind(a, predicate(FORM_IDENTIFIER_NAME)).isPresent()) {
    allNotificationsMap.put(find(a, predicate(FORM_IDENTIFIER_NAME)).get(VALUE), a);
   }
  }
  return allNotificationsMap;
 }

 public static PrnfsNotification getPrnfsNotification(AdminFormValues a) throws ValidationException {
  final Optional<Map<String, String>> urlOpt = Iterables.tryFind(a, predicate("url"));
  if (!urlOpt.isPresent()) {
   throw new ValidationException("url", "URL not set");
  }
  final PrnfsNotificationBuilder prnfsNotificationBuilder = prnfsNotificationBuilder().withUser("").withPassword("")
    .withUrl(urlOpt.get().get(VALUE));
  final Iterable<Map<String, String>> events = filter(a, predicate("events"));
  for (final Map<String, String> event : events) {
   prnfsNotificationBuilder.withTrigger(PullRequestAction.valueOf(event.get(VALUE)));
  }
  return prnfsNotificationBuilder.build();
 }

 public static PrnfsSettings getPrnfsSettings(PluginSettings pluginSettings) throws ValidationException {
  final PrnfsSettingsBuilder prnfsSettingsBuilder = prnfsSettingsBuilder();
  for (final AdminFormValues a : getSettingsAsFormValues(pluginSettings)) {
   prnfsSettingsBuilder.withNotification(getPrnfsNotification(a));
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
    toReturn.add(gson.fromJson(storedJson, AdminFormValues.class));
   }
  } catch (final Exception e) {
   logger.error("Unable to deserialize settings", e);
  }
  return toReturn;
 }

 private static void storeNotificationsMap(PluginSettings pluginSettings,
   Map<String, AdminFormValues> allNotificationsMap) throws ValidationException {
  final List<String> toStore = newArrayList();
  for (final AdminFormValues adminFormValues : allNotificationsMap.values()) {
   final Optional<Map<String, String>> formIdOpt = tryFind(adminFormValues, predicate(FORM_IDENTIFIER_NAME));
   if (!formIdOpt.isPresent() || formIdOpt.get().get(VALUE).trim().isEmpty()) {
    throw new ValidationException(FORM_IDENTIFIER_NAME, "Not set!");
   }
   toStore.add(new Gson().toJson(adminFormValues));
  }
  pluginSettings.put(STORAGE_KEY, toStore);
 }

 public static void storeSettings(PluginSettings pluginSettings, final AdminFormValues config)
   throws ValidationException {
  final Map<String, AdminFormValues> allNotificationsMap = getNotificationsMap(pluginSettings);

  final Optional<Map<String, String>> formIdOpt = tryFind(config, predicate(FORM_IDENTIFIER_NAME));
  if (!formIdOpt.isPresent() || formIdOpt.get().get(VALUE).trim().isEmpty()) {
   final String generatedIdentifier = formIdentifierGnerator();
   removeIf(config, predicate(FORM_IDENTIFIER_NAME));
   config.add(new ImmutableMap.Builder<String, String>().put(NAME, FORM_IDENTIFIER_NAME)
     .put(VALUE, generatedIdentifier).build());
  }

  allNotificationsMap.put(find(config, predicate(FORM_IDENTIFIER_NAME)).get(VALUE), config);

  storeNotificationsMap(pluginSettings, allNotificationsMap);
 }
}
