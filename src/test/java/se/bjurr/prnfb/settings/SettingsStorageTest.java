package se.bjurr.prnfb.settings;

import static com.google.common.collect.Lists.newArrayList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static se.bjurr.prnfb.settings.SettingsStorage.STORAGE_KEY;
import static se.bjurr.prnfb.settings.SettingsStorage.STORAGE_KEY_PRNFS;
import static se.bjurr.prnfb.settings.SettingsStorage.getSettingsAsFormValues;

import org.junit.Before;
import org.junit.Test;

import se.bjurr.prnfb.admin.AdminFormValues;

import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;

public class SettingsStorageTest {

 private String prnfs;
 private String prnfb;
 private PluginSettings settings;

 @Before
 public void before() {
  AdminFormValues prnfs = new AdminFormValues();
  prnfs.add(ImmutableMap.<String, String> builder().put("key", "PRNFS").build());
  this.prnfs = new Gson().toJson(prnfs);
  AdminFormValues prnfb = new AdminFormValues();
  prnfb.add(ImmutableMap.<String, String> builder().put("key", "PRNFB").build());
  this.prnfb = new Gson().toJson(prnfb);
  settings = mock(PluginSettings.class);
 }

 @Test
 public void testThatPrnfsSettingsAreUsedIfAvailableAnNoPrnfbSettingsAvailable() {
  when(settings.get(STORAGE_KEY_PRNFS)).thenReturn(newArrayList(prnfs));
  assertEquals("PRNFS", getSettingsAsFormValues(settings).get(0).get(0).get("key"));
 }

 @Test
 public void testThatPrnfbSettingsAreUsedIfAvailable() {
  when(settings.get(STORAGE_KEY)).thenReturn(newArrayList(prnfb));
  assertEquals("PRNFB", getSettingsAsFormValues(settings).get(0).get(0).get("key"));
 }

 @Test
 public void testThatPrnfbSettingsAreUsedIfAvailableEvenIfPrnfsAreAvailable() {
  when(settings.get(STORAGE_KEY)).thenReturn(newArrayList(prnfb));
  when(settings.get(STORAGE_KEY_PRNFS)).thenReturn(newArrayList(prnfs));
  assertEquals("PRNFB", getSettingsAsFormValues(settings).get(0).get(0).get("key"));
 }

}
