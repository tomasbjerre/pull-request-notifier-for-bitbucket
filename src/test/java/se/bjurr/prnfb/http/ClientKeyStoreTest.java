package se.bjurr.prnfb.http;

import static org.assertj.core.api.Assertions.assertThat;
import static se.bjurr.prnfb.settings.PrnfbSettingsBuilder.prnfbSettingsBuilder;
import static se.bjurr.prnfb.settings.PrnfbSettingsDataBuilder.prnfbSettingsDataBuilder;

import org.junit.Test;

import se.bjurr.prnfb.settings.PrnfbSettings;

public class ClientKeyStoreTest {

 @Test
 public void testThatNoKeyStoreIsCreatedIfNoSettings() {
  PrnfbSettings settings = prnfbSettingsBuilder()//
    .build();

  ClientKeyStore clientKeyStore = new ClientKeyStore(settings.getPrnfbSettingsData());

  assertThat(clientKeyStore.getPassword())//
    .isNull();
  assertThat(clientKeyStore.getKeyStore().isPresent())//
    .isFalse();
 }

 @Test(expected = RuntimeException.class)
 public void testThatExceptionIsThrownIfKeyStoreNotFound() {
  PrnfbSettings settings = prnfbSettingsBuilder()//
    .setPrnfbSettingsData(//
      prnfbSettingsDataBuilder()//
        .setKeyStore("keyStore")//
        .build())//
    .build();

  ClientKeyStore clientKeyStore = new ClientKeyStore(settings.getPrnfbSettingsData());

  assertThat(clientKeyStore.getPassword())//
    .isNull();
  assertThat(clientKeyStore.getKeyStore().isPresent())//
    .isTrue();
 }

 @Test(expected = RuntimeException.class)
 public void testThatExceptionIsThrownIfKeyStoreNotFoundAndPasswordSet() {
  PrnfbSettings settings = prnfbSettingsBuilder()//
    .setPrnfbSettingsData(//
      prnfbSettingsDataBuilder()//
        .setKeyStore("keyStore")//
        .setKeyStorePassword("keyStorePassword")//
        .build())//
    .build();

  ClientKeyStore clientKeyStore = new ClientKeyStore(settings.getPrnfbSettingsData());

  assertThat(clientKeyStore.getPassword())//
    .isNull();
  assertThat(clientKeyStore.getKeyStore().isPresent())//
    .isTrue();
 }

}
