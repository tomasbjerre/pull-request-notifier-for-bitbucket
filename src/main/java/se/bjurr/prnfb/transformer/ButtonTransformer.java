package se.bjurr.prnfb.transformer;

import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

import se.bjurr.prnfb.presentation.dto.ButtonDTO;
import se.bjurr.prnfb.settings.PrnfbButton;

public class ButtonTransformer {

 public static PrnfbButton toPrnfbButton(ButtonDTO buttonDto) {
  return new PrnfbButton(//
    buttonDto.getUUID(), //
    buttonDto.getTitle(), //
    buttonDto.getUserLevel());//
 }

 public static ButtonDTO toButtonDto(PrnfbButton from) {
  ButtonDTO to = new ButtonDTO();
  to.setTitle(from.getTitle());
  to.setUserLevel(from.getUserLevel());
  to.setUuid(from.getUuid());
  return to;
 }

 public static List<ButtonDTO> toButtonDtoList(Iterable<PrnfbButton> allowedButtons) {
  List<ButtonDTO> to = newArrayList();
  for (PrnfbButton from : allowedButtons) {
   to.add(toButtonDto(from));
  }
  return to;
 }

}
