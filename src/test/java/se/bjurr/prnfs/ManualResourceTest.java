package se.bjurr.prnfs;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static se.bjurr.prnfs.ManualResource.allowedUseButton;
import static se.bjurr.prnfs.admin.AdminFormValues.BUTTON_VISIBILITY.ADMIN;
import static se.bjurr.prnfs.admin.AdminFormValues.BUTTON_VISIBILITY.EVERYONE;
import static se.bjurr.prnfs.admin.AdminFormValues.BUTTON_VISIBILITY.NONE;
import static se.bjurr.prnfs.admin.AdminFormValues.BUTTON_VISIBILITY.SYSTEM_ADMIN;

import org.junit.Test;

import se.bjurr.prnfs.settings.PrnfsButton;

public class ManualResourceTest {

 @Test
 public void testThatButtonIsOnlyDisplayedForPrivilegedUsers() {
  PrnfsButton everyone = new PrnfsButton("", EVERYONE, "");
  PrnfsButton admin = new PrnfsButton("", ADMIN, "");
  PrnfsButton systemAdmin = new PrnfsButton("", SYSTEM_ADMIN, "");
  PrnfsButton none = new PrnfsButton("", NONE, "");

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
