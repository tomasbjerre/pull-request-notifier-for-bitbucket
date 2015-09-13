package se.bjurr.prnfb.admin;

import static com.google.common.collect.Lists.newArrayList;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static se.bjurr.prnfb.settings.SettingsStorage.getSettingsAsFormValues;

import java.util.List;
import java.util.logging.Logger;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;

import se.bjurr.prnfb.admin.AdminFormValues;
import se.bjurr.prnfb.settings.SettingsStorage;

import com.atlassian.sal.api.pluginsettings.PluginSettings;

public class FaultyStoredSettingsIsHandledTest {
 private Logger beforeLogger;
 private PluginSettings settings;

 @After
 public void after() {
  SettingsStorage.setLogger(beforeLogger);
 }

 private void assertEmpty() {
  List<AdminFormValues> adminFormValues = getSettingsAsFormValues(settings);
  assertNotNull(adminFormValues);
  if (adminFormValues.isEmpty()) {
   return;
  }
  throw new AssertionError(adminFormValues.get(0));
 }

 @Before
 public void before() {
  beforeLogger = SettingsStorage.getLogger();
  Logger mockLogger = mock(Logger.class);
  SettingsStorage.setLogger(mockLogger);
  this.settings = mock(PluginSettings.class);
 }

 @Test
 public void whenItIsAListOfNoneJson() {
  when(settings.get(Matchers.anyString())).thenReturn(newArrayList("this is not json"));
  assertEmpty();
 }

 @Test
 public void whenItIsAListOfUnrecognizedJson() {
  when(settings.get(Matchers.anyString())).thenReturn(newArrayList("{\"this\": \"that\"}"));
  assertEmpty();
 }

 @Test
 public void whenItIsNotAList() {
  when(settings.get(Matchers.anyString())).thenReturn("this is not a list!");
  assertEmpty();
 }
}
