package se.bjurr.prnfb.presentation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static se.bjurr.prnfb.settings.PrnfbSettingsDataBuilder.prnfbSettingsDataBuilder;
import static se.bjurr.prnfb.settings.USER_LEVEL.ADMIN;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import se.bjurr.prnfb.presentation.dto.SettingsDataDTO;
import se.bjurr.prnfb.service.SettingsService;
import se.bjurr.prnfb.service.UserCheckService;
import se.bjurr.prnfb.settings.PrnfbSettingsData;

public class SettingsDataServletTest {

 @Mock
 private UserCheckService userCheckService;
 @Mock
 private SettingsService settingsService;
 private SettingsDataServlet sut;

 @Before
 public void before() {
  initMocks(this);
  when(userCheckService.isViewAllowed())//
    .thenReturn(true);
  when(userCheckService.isAdminAllowed())//
    .thenReturn(true);
  sut = new SettingsDataServlet(userCheckService, settingsService);
 }

 @Test
 public void testThatSettingsCanBeRead() throws Exception {
  SettingsDataDTO expected = new SettingsDataDTO();
  expected.setAdminRestriction(ADMIN);
  expected.setKeyStore("keyStore");
  expected.setKeyStorePassword("keyStorePassword");
  expected.setKeyStoreType("keyStoreType");
  expected.setShouldAcceptAnyCertificate(true);

  PrnfbSettingsData storedSettings = prnfbSettingsDataBuilder()//
    .setShouldAcceptAnyCertificate(true)//
    .setAdminRestriction(ADMIN)//
    .setKeyStore("keyStore")//
    .setKeyStorePassword("keyStorePassword")//
    .setKeyStoreType("keyStoreType")//
    .build();

  when(settingsService.getPrnfbSettingsData())//
    .thenReturn(storedSettings);

  SettingsDataDTO actual = (SettingsDataDTO) sut.get().getEntity();

  assertThat(actual)//
    .isEqualTo(expected);
 }

 @Test
 public void testThatSettingsCanBeStored() throws Exception {
  SettingsDataDTO incoming = new SettingsDataDTO();
  incoming.setAdminRestriction(ADMIN);
  incoming.setKeyStore("keyStore");
  incoming.setKeyStorePassword("keyStorePassword");
  incoming.setKeyStoreType("keyStoreType");
  incoming.setShouldAcceptAnyCertificate(true);

  PrnfbSettingsData storedSettings = prnfbSettingsDataBuilder()//
    .setShouldAcceptAnyCertificate(true)//
    .setAdminRestriction(ADMIN)//
    .setKeyStore("keyStore")//
    .setKeyStorePassword("keyStorePassword")//
    .setKeyStoreType("keyStoreType")//
    .build();

  sut.post(incoming);
  verify(settingsService)//
    .setPrnfbSettingsData(eq(storedSettings));
 }

}
