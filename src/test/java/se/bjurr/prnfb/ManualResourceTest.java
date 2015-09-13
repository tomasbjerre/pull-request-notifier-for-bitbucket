package se.bjurr.prnfb;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static se.bjurr.prnfb.ManualResource.allowedUseButton;
import static se.bjurr.prnfb.admin.AdminFormValues.BUTTON_VISIBILITY.ADMIN;
import static se.bjurr.prnfb.admin.AdminFormValues.BUTTON_VISIBILITY.EVERYONE;
import static se.bjurr.prnfb.admin.AdminFormValues.BUTTON_VISIBILITY.NONE;
import static se.bjurr.prnfb.admin.AdminFormValues.BUTTON_VISIBILITY.SYSTEM_ADMIN;

import org.junit.Test;

import se.bjurr.prnfb.settings.PrnfbButton;

public class ManualResourceTest {

 @Test
 public void testThatButtonIsOnlyDisplayedForPrivilegedUsers() {
  PrnfbButton everyone = new PrnfbButton("", EVERYONE, "");
  PrnfbButton admin = new PrnfbButton("", ADMIN, "");
  PrnfbButton systemAdmin = new PrnfbButton("", SYSTEM_ADMIN, "");
  PrnfbButton none = new PrnfbButton("", NONE, "");

  assertTrue(allowedUseButton(everyone, TRUE, TRUE));
  assertTrue(allowedUseButton(everyone, TRUE, FALSE));
  assertTrue(allowedUseButton(everyone, FALSE, TRUE));

  assertTrue(allowedUseButton(admin, TRUE, TRUE));
  assertFalse(allowedUseButton(admin, FALSE, TRUE));
  assertTrue(allowedUseButton(admin, TRUE, FALSE));
  assertFalse(allowedUseButton(admin, FALSE, FALSE));

  assertTrue(allowedUseButton(systemAdmin, TRUE, TRUE));
  assertTrue(allowedUseButton(systemAdmin, FALSE, TRUE));
  assertTrue(allowedUseButton(systemAdmin, TRUE, FALSE));
  assertFalse(allowedUseButton(systemAdmin, FALSE, FALSE));

  assertFalse(allowedUseButton(none, FALSE, FALSE));
  assertFalse(allowedUseButton(none, FALSE, TRUE));
  assertFalse(allowedUseButton(none, TRUE, FALSE));
  assertFalse(allowedUseButton(none, FALSE, FALSE));
 }
}
