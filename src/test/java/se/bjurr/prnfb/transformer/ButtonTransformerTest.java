package se.bjurr.prnfb.transformer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static se.bjurr.prnfb.presentation.dto.ButtonFormType.radio;
import static se.bjurr.prnfb.presentation.dto.ButtonFormType.textarea;
import static se.bjurr.prnfb.settings.PrnfbSettings.UNCHANGED;
import static se.bjurr.prnfb.test.Podam.populatedInstanceOf;
import static se.bjurr.prnfb.transformer.ButtonTransformer.validateButtonFormDTOList;
import static se.bjurr.prnfb.transformer.SettingsTransformer.toDto;
import static se.bjurr.prnfb.transformer.SettingsTransformer.toPrnfbSettingsData;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import se.bjurr.prnfb.presentation.dto.ButtonFormElementDTO;
import se.bjurr.prnfb.presentation.dto.SettingsDataDTO;
import se.bjurr.prnfb.settings.ValidationException;

public class ButtonTransformerTest {
  @Test
  public void testTransformation() throws ValidationException {
    SettingsDataDTO originalDto = populatedInstanceOf(SettingsDataDTO.class);
    SettingsDataDTO retransformedDto = toDto(toPrnfbSettingsData(originalDto));
    originalDto.setKeyStorePassword(UNCHANGED);

    assertThat(retransformedDto) //
        .isEqualTo(originalDto);
    assertThat(retransformedDto.toString()) //
        .isEqualTo(originalDto.toString());
    assertThat(retransformedDto.hashCode()) //
        .isEqualTo(originalDto.hashCode());
  }

  @Test
  public void testThatButtonFormJsonStringCanFindProblemsInJsonNoName() {
    ButtonFormElementDTO button = new ButtonFormElementDTO();
    button.setDefaultValue("defaultValue");
    button.setDescription("descr");
    button.setLabel("label");
    button.setName("");
    button.setRequired(true);
    button.setType(textarea);
    List<ButtonFormElementDTO> buttonFormDtoList = new ArrayList<>();
    buttonFormDtoList.add(button);
    try {
      validateButtonFormDTOList(buttonFormDtoList);
    } catch (Error e) {
      assertThat(e.getMessage()) //
          .isEqualTo("The name must be set.");
      return;
    }
    fail("No error from: " + buttonFormDtoList);
  }

  @Test
  public void testThatButtonFormJsonStringCanFindProblemsInJsonNoRadioNoOptions() {
    ButtonFormElementDTO button = new ButtonFormElementDTO();
    button.setDefaultValue("defaultValue");
    button.setDescription("descr");
    button.setLabel("label");
    button.setName("name");
    button.setRequired(true);
    button.setType(radio);
    List<ButtonFormElementDTO> buttonFormDtoList = new ArrayList<>();
    buttonFormDtoList.add(button);
    try {
      validateButtonFormDTOList(buttonFormDtoList);
    } catch (Error e) {
      assertThat(e.getMessage()) //
          .isEqualTo("When adding radio buttons, options must also be defined.");
      return;
    }
    fail("No error from: " + buttonFormDtoList);
  }

  @Test
  public void testThatButtonFormJsonStringCanFindProblemsInJsonNoRadioEmptyOptions() {
    ButtonFormElementDTO button = new ButtonFormElementDTO();
    button.setDefaultValue("defaultValue");
    button.setDescription("descr");
    button.setLabel("label");
    button.setName("name");
    button.setRequired(true);
    button.setType(radio);
    button.setButtonFormElementOptionList(new ArrayList<>());
    List<ButtonFormElementDTO> buttonFormDtoList = new ArrayList<>();
    buttonFormDtoList.add(button);
    try {
      validateButtonFormDTOList(buttonFormDtoList);
    } catch (Error e) {
      assertThat(e.getMessage()) //
          .isEqualTo("When adding radio buttons, options must also be defined.");
      return;
    }
    fail("No error from: " + buttonFormDtoList);
  }
}
