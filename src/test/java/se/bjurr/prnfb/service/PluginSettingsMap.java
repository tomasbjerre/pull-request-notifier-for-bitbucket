package se.bjurr.prnfb.service;

import static com.google.common.collect.Maps.newHashMap;

import com.atlassian.sal.api.pluginsettings.PluginSettings;
import java.util.Map;

public class PluginSettingsMap implements PluginSettings {

  private final Map<String, String> pluginSettingsMap;

  public PluginSettingsMap() {
    this.pluginSettingsMap = newHashMap();
  }

  @Override
  public Object get(String key) {
    return this.pluginSettingsMap.get(key);
  }

  public Map<String, String> getPluginSettingsMap() {
    return this.pluginSettingsMap;
  }

  @Override
  public Object put(String key, Object value) {
    this.pluginSettingsMap.put(key, (String) value);
    return value;
  }

  @Override
  public Object remove(String key) {
    this.pluginSettingsMap.remove(key);
    return key;
  }
}
