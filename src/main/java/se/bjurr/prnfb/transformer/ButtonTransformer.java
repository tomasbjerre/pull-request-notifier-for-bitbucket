package se.bjurr.prnfb.transformer;

import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

import se.bjurr.prnfb.presentation.dto.ButtonDTO;
import se.bjurr.prnfb.settings.PrnfbButton;

public class ButtonTransformer {

 public static ButtonDTO toButtonDto(PrnfbButton from) {
  ButtonDTO to = new ButtonDTO();
  to.setName(from.getName());
  to.setUserLevel(from.getUserLevel());
  to.setUuid(from.getUuid());
  to.setProjectKey(from.getProjectKey().orNull());
  to.setRepositorySlug(from.getRepositorySlug().orNull());
  return to;
 }

 public static List<ButtonDTO> toButtonDtoList(Iterable<PrnfbButton> allowedButtons) {
  List<ButtonDTO> to = newArrayList();
  for (PrnfbButton from : allowedButtons) {
   to.add(toButtonDto(from));
  }
  return to;
 }

 public static PrnfbButton toPrnfbButton(ButtonDTO buttonDto) {
  return new PrnfbButton(//
    buttonDto.getUUID(), //
    buttonDto.getName(), //
    buttonDto.getUserLevel(),//
    buttonDto.getProjectKey().orNull(),//
    buttonDto.getRepositorySlug().orNull());//
 }

}
