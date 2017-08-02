package se.bjurr.prnfb.presentation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
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
    when(this.userCheckService.isAdminAllowed(Mockito.any(), Mockito.any())) //
        .thenReturn(true);
    this.sut = new SettingsDataServlet(this.userCheckService, this.settingsService);
  }

  @Test
  public void testThatSettingsCanBeRead() throws Exception {
    final SettingsDataDTO expected = new SettingsDataDTO();
    expected.setAdminRestriction(ADMIN);
    expected.setKeyStore("keyStore");
    expected.setKeyStorePassword(UNCHANGED);
    expected.setKeyStoreType("keyStoreType");
    expected.setShouldAcceptAnyCertificate(true);

    final PrnfbSettingsData storedSettings =
        prnfbSettingsDataBuilder() //
            .setShouldAcceptAnyCertificate(true) //
            .setAdminRestriction(ADMIN) //
            .setKeyStore("keyStore") //
            .setKeyStorePassword("keyStorePassword") //
            .setKeyStoreType("keyStoreType") //
            .build();

    when(this.settingsService.getPrnfbSettingsData()) //
        .thenReturn(storedSettings);

    final SettingsDataDTO actual = (SettingsDataDTO) this.sut.get().getEntity();

    assertThat(actual) //
        .isEqualTo(expected);
  }

  @Test
  public void testThatSettingsCanBeStored() throws Exception {
    final PrnfbSettingsData prnfbSettingsData = mock(PrnfbSettingsData.class);
    when(settingsService.getPrnfbSettingsData()).thenReturn(prnfbSettingsData);
    when(settingsService.getPrnfbSettingsData().getAdminRestriction()).thenReturn(ADMIN);

    final SettingsDataDTO incoming = new SettingsDataDTO();
    incoming.setAdminRestriction(ADMIN);
    incoming.setKeyStore("keyStore");
    incoming.setKeyStorePassword("keyStorePassword");
    incoming.setKeyStoreType("keyStoreType");
    incoming.setShouldAcceptAnyCertificate(true);

    final PrnfbSettingsData storedSettings =
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
