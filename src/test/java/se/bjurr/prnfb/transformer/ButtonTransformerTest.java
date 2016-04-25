package se.bjurr.prnfb.transformer;

import static org.assertj.core.api.Assertions.assertThat;
import static se.bjurr.prnfb.transformer.SettingsTransformer.toDto;
import static se.bjurr.prnfb.transformer.SettingsTransformer.toPrnfbSettingsData;

import org.junit.Test;

import se.bjurr.prnfb.presentation.dto.SettingsDataDTO;
import se.bjurr.prnfb.settings.ValidationException;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

public class ButtonTransformerTest {
 @Test
 public void testTransformation() throws ValidationException {
  PodamFactory factory = new PodamFactoryImpl();
  SettingsDataDTO originalDto = factory.manufacturePojo(SettingsDataDTO.class);
  SettingsDataDTO retransformedDto = toDto(toPrnfbSettingsData(originalDto));

  assertThat(retransformedDto)//
    .isEqualTo(originalDto);
  assertThat(retransformedDto.toString())//
    .isEqualTo(originalDto.toString());
  assertThat(retransformedDto.hashCode())//
    .isEqualTo(originalDto.hashCode());
 }

}
