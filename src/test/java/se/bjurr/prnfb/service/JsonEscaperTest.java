package se.bjurr.prnfb.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class JsonEscaperTest {

  @Test
  public void testThatStringCanBeEscaped() {
    assertThat(JsonEscaper.jsonEscape("some'string")) //
        .isEqualTo("some\\u0027string");
  }
}
