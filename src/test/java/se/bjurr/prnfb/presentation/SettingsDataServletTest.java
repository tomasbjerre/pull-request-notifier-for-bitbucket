package se.bjurr.prnfb.presentation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static se.bjurr.prnfb.settings.PrnfbSettings.UNCHANGED;
import static se.bjurr.prnfb.settings.PrnfbSettingsDataBuilder.prnfbSettingsDataBuilder;
import static se.bjurr.prnfb.settings.USER_LEVEL.ADMIN;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import se.bjurr.prnfb.presentation.dto.SettingsDataDTO;
import se.bjurr.prnfb.service.SettingsService;
import se.bjurr.prnfb.service.UserCheckService;
import se.bjurr.prnfb.settings.PrnfbSettingsData;

public class SettingsDataServletTest {

  @Mock private SettingsService settingsService;
  private SettingsDataServlet sut;
  @Mock private UserCheckService userCheckService;

  @Before
  public void before() {
    initMocks(this);
    when(this.userCheckService.isViewAllowed()) //
        .thenReturn(true);
    when(this.userCheckService.isAdminAllowed(Mockito.any())) //
        .thenReturn(true);
    this.sut = new SettingsDataServlet(this.userCheckService, this.settingsService);
  }

  @Test
  public void testThatSettingsCanBeRead() throws Exception {
    SettingsDataDTO expected = new SettingsDataDTO();
    expected.setAdminRestriction(ADMIN);
    expected.setKeyStore("keyStore");
    expected.setKeyStorePassword(UNCHANGED);
    expected.setKeyStoreType("keyStoreType");
    expected.setShouldAcceptAnyCertificate(true);

    PrnfbSettingsData storedSettings =
        prnfbSettingsDataBuilder() //
            .setShouldAcceptAnyCertificate(true) //
            .setAdminRestriction(ADMIN) //
            .setKeyStore("keyStore") //
            .setKeyStorePassword("keyStorePassword") //
            .setKeyStoreType("keyStoreType") //
            .build();

    when(this.settingsService.getPrnfbSettingsData()) //
        .thenReturn(storedSettings);

    SettingsDataDTO actual = (SettingsDataDTO) this.sut.get().getEntity();

    assertThat(actual) //
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

    PrnfbSettingsData storedSettings =
        prnfbSettingsDataBuilder() //
            .setShouldAcceptAnyCertificate(true) //
            .setAdminRestriction(ADMIN) //
            .setKeyStore("keyStore") //
            .setKeyStorePassword("keyStorePassword") //
            .setKeyStoreType("keyStoreType") //
            .build();

    this.sut.post(incoming);
    verify(this.settingsService) //
        .setPrnfbSettingsData(eq(storedSettings));
  }
}
