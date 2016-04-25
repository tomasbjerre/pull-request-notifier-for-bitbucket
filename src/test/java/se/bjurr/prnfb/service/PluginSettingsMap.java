package se.bjurr.prnfb.service;

import static com.google.common.collect.Maps.newHashMap;

import java.util.Map;

import com.atlassian.sal.api.pluginsettings.PluginSettings;

public class PluginSettingsMap implements PluginSettings {

 private final Map<String, String> pluginSettingsMap;

 public PluginSettingsMap() {
  pluginSettingsMap = newHashMap();
 }

 public Map<String, String> getPluginSettingsMap() {
  return pluginSettingsMap;
 }

 @Override
 public Object remove(String key) {
  pluginSettingsMap.remove(key);
  return key;
 }

 @Override
 public Object put(String key, Object value) {
  pluginSettingsMap.put(key, (String) value);
  return value;
 }

 @Override
 public Object get(String key) {
  return pluginSettingsMap.get(key);
 }
}
