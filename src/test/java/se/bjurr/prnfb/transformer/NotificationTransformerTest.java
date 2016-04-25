package se.bjurr.prnfb.transformer;

import static org.assertj.core.api.Assertions.assertThat;
import static se.bjurr.prnfb.transformer.NotificationTransformer.toNotificationDto;
import static se.bjurr.prnfb.transformer.NotificationTransformer.toPrnfbNotification;

import org.junit.Test;

import se.bjurr.prnfb.presentation.dto.NotificationDTO;
import se.bjurr.prnfb.settings.ValidationException;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

public class NotificationTransformerTest {
 @Test
 public void testTransformation() throws ValidationException {
  PodamFactory factory = new PodamFactoryImpl();
  NotificationDTO originalDto = factory.manufacturePojo(NotificationDTO.class);
  originalDto.setUrl("http://hej.com/");
  NotificationDTO retransformedDto = toNotificationDto(toPrnfbNotification(originalDto));

  assertThat(retransformedDto)//
    .isEqualTo(originalDto);
  assertThat(retransformedDto.toString())//
    .isEqualTo(originalDto.toString());
  assertThat(retransformedDto.hashCode())//
    .isEqualTo(originalDto.hashCode());
 }

}
