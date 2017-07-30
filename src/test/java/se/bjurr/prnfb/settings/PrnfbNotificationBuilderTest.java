package se.bjurr.prnfb.settings;

import static org.assertj.core.api.Assertions.assertThat;
import static se.bjurr.prnfb.settings.PrnfbNotificationBuilder.prnfbNotificationBuilder;
import static se.bjurr.prnfb.test.Podam.populatedInstanceOf;

import org.junit.Test;

public class PrnfbNotificationBuilderTest {

  @Test
  public void testBuild() throws ValidationException {
    final PrnfbNotificationBuilder originalBuilder =
        populatedInstanceOf(PrnfbNotificationBuilder.class);
    originalBuilder.withUrl("http://bjurr.com/");
    final PrnfbNotification original = originalBuilder.build();
    final PrnfbNotificationBuilder builder = prnfbNotificationBuilder(original);
    final PrnfbNotification built = builder.build();
    assertThat(built).isEqualTo(original);
  }
}
