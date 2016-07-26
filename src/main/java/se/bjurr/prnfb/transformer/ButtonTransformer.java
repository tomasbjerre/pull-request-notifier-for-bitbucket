package se.bjurr.prnfb.transformer;

import static com.google.common.collect.Lists.newArrayList;

import java.util.List;
import java.util.Map;

import se.bjurr.prnfb.http.HttpResponse;
import se.bjurr.prnfb.presentation.dto.ButtonDTO;
import se.bjurr.prnfb.presentation.dto.TriggerResultDTO;
import se.bjurr.prnfb.settings.PrnfbButton;

public class ButtonTransformer {

	
 public static TriggerResultDTO toTriggerResultDto(PrnfbButton from, Map<String, HttpResponse> results) {
  TriggerResultDTO to = new TriggerResultDTO();
  to.setName(from.getName());
  to.setResults(results);
  return to;
}
	
 public static ButtonDTO toButtonDto(PrnfbButton from) {
  ButtonDTO to = new ButtonDTO();
  to.setName(from.getName());
  to.setUserLevel(from.getUserLevel());
  to.setUuid(from.getUuid());
  to.setProjectKey(from.getProjectKey().orNull());
  to.setRepositorySlug(from.getRepositorySlug().orNull());
  to.setConfirmation(from.getConfirmation());
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
    buttonDto.getConfirmation(),//
    buttonDto.getProjectKey().orNull(),//
    buttonDto.getRepositorySlug().orNull());//
 }

}
