package se.bjurr.prnfb.transformer;

import static se.bjurr.prnfb.settings.PrnfbSettings.UNCHANGED;
import static se.bjurr.prnfb.settings.PrnfbSettingsDataBuilder.prnfbSettingsDataBuilder;

import se.bjurr.prnfb.presentation.dto.SettingsDataDTO;
import se.bjurr.prnfb.settings.PrnfbSettingsData;

public class SettingsTransformer {

  public static SettingsDataDTO toDto(PrnfbSettingsData settingsData) {
    SettingsDataDTO dto = new SettingsDataDTO();
    dto.setAdminRestriction(settingsData.getAdminRestriction());
    dto.setKeyStore(settingsData.getKeyStore().orNull());
    dto.setKeyStorePassword(UNCHANGED);
    dto.setKeyStoreType(settingsData.getKeyStoreType());
    dto.setShouldAcceptAnyCertificate(settingsData.isShouldAcceptAnyCertificate());
    return dto;
  }

  public static PrnfbSettingsData toPrnfbSettingsData(SettingsDataDTO settingsDataDto) {
    return prnfbSettingsDataBuilder() //
        .setAdminRestriction(settingsDataDto.getAdminRestriction()) //
        .setKeyStore(settingsDataDto.getKeyStore()) //
        .setKeyStorePassword(settingsDataDto.getKeyStorePassword()) //
        .setKeyStoreType(settingsDataDto.getKeyStoreType()) //
        .setShouldAcceptAnyCertificate(settingsDataDto.isShouldAcceptAnyCertificate()) //
        .build();
  }
}
