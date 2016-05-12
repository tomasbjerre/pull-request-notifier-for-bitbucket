package se.bjurr.prnfb.transformer;

import static org.assertj.core.api.Assertions.assertThat;
import static se.bjurr.prnfb.test.Podam.populatedInstanceOf;
import static se.bjurr.prnfb.transformer.ButtonTransformer.toButtonDto;
import static se.bjurr.prnfb.transformer.ButtonTransformer.toPrnfbButton;

import org.junit.Test;

import se.bjurr.prnfb.presentation.dto.ButtonDTO;
import se.bjurr.prnfb.settings.ValidationException;

public class SettingsDataTransformerTest {
 @Test
 public void testTransformation() throws ValidationException {
  ButtonDTO originalDto = populatedInstanceOf(ButtonDTO.class);
  ButtonDTO retransformedDto = toButtonDto(toPrnfbButton(originalDto));

  assertThat(retransformedDto)//
    .isEqualTo(originalDto);
  assertThat(retransformedDto.toString())//
    .isEqualTo(originalDto.toString());
  assertThat(retransformedDto.hashCode())//
    .isEqualTo(originalDto.hashCode());
 }

}
