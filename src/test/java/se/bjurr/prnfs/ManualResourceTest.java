package se.bjurr.prnfs;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static se.bjurr.prnfs.ManualResource.canUseButton;
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

  assertTrue(canUseButton(everyone, TRUE, TRUE));
  assertTrue(canUseButton(everyone, TRUE, FALSE));
  assertTrue(canUseButton(everyone, FALSE, TRUE));

  assertTrue(canUseButton(admin, TRUE, TRUE));
  assertFalse(canUseButton(admin, FALSE, TRUE));
  assertTrue(canUseButton(admin, TRUE, FALSE));
  assertFalse(canUseButton(admin, FALSE, FALSE));

  assertTrue(canUseButton(systemAdmin, TRUE, TRUE));
  assertTrue(canUseButton(systemAdmin, FALSE, TRUE));
  assertTrue(canUseButton(systemAdmin, TRUE, FALSE));
  assertFalse(canUseButton(systemAdmin, FALSE, FALSE));

  assertFalse(canUseButton(none, FALSE, FALSE));
  assertFalse(canUseButton(none, FALSE, TRUE));
  assertFalse(canUseButton(none, TRUE, FALSE));
  assertFalse(canUseButton(none, FALSE, FALSE));
 }
}
