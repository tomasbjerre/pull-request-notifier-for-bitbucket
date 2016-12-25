package se.bjurr.prnfb.transformer;

import static org.assertj.core.api.Assertions.assertThat;
import static se.bjurr.prnfb.test.Podam.populatedInstanceOf;
import static se.bjurr.prnfb.transformer.SettingsTransformer.toDto;
import static se.bjurr.prnfb.transformer.SettingsTransformer.toPrnfbSettingsData;

import org.junit.Test;

import se.bjurr.prnfb.presentation.dto.SettingsDataDTO;
import se.bjurr.prnfb.settings.ValidationException;

public class ButtonTransformerTest {
  @Test
  public void testTransformation() throws ValidationException {
    SettingsDataDTO originalDto = populatedInstanceOf(SettingsDataDTO.class);
    SettingsDataDTO retransformedDto = toDto(toPrnfbSettingsData(originalDto));

    assertThat(retransformedDto) //
        .isEqualTo(originalDto);
    assertThat(retransformedDto.toString()) //
        .isEqualTo(originalDto.toString());
    assertThat(retransformedDto.hashCode()) //
        .isEqualTo(originalDto.hashCode());
  }
}
