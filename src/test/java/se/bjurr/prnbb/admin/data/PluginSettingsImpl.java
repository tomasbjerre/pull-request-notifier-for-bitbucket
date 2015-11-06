package se.bjurr.prnbb.admin.data;

import static com.google.common.collect.Maps.newTreeMap;

import java.util.List;
import java.util.Map;

import com.atlassian.sal.api.pluginsettings.PluginSettings;

public class PluginSettingsImpl implements PluginSettings {
 private final Map<String, List<String>> map = newTreeMap();

 public PluginSettingsImpl() {

 }

 @Override
 public Object get(String key) {
  return map.get(key);
 }

 @SuppressWarnings("unchecked")
 @Override
 public Object put(String key, Object value) {
  return map.put(key, (List<String>) value);
 }

 @Override
 public Object remove(String key) {
  return map.remove(key);
 }
}
