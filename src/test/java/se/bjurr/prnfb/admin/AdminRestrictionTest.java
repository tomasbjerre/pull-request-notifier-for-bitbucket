package se.bjurr.prnfb.admin;

import static org.assertj.core.api.Assertions.assertThat;
import static se.bjurr.prnb.admin.utils.NotificationBuilder.notificationBuilder;
import static se.bjurr.prnb.admin.utils.PrnfbTestBuilder.prnfbTestBuilder;
import static se.bjurr.prnfb.admin.AdminFormValues.FIELDS.FORM_IDENTIFIER;
import static se.bjurr.prnfb.admin.AdminFormValues.FIELDS.FORM_TYPE;
import static se.bjurr.prnfb.admin.AdminFormValues.FIELDS.admin_allowed;
import static se.bjurr.prnfb.admin.AdminFormValues.FIELDS.user_allowed;
import static se.bjurr.prnfb.admin.AdminFormValues.FORM_TYPE.GLOBAL_SETTINGS;

import org.junit.Test;

import se.bjurr.prnfb.settings.PrnfbSettings;

public class AdminRestrictionTest {
 @Test
 public void testThatAdminsAndUsersCanBeGrantedAccessToAdminGUI() throws Exception {
  PrnfbSettings prnfbSettings = prnfbTestBuilder()//
    .isLoggedInAsAdmin()//
    .withNotification( //
      notificationBuilder()//
        .withFieldValue(FORM_IDENTIFIER, GLOBAL_SETTINGS.name())//
        .withFieldValue(FORM_TYPE, GLOBAL_SETTINGS.name())//
        .withFieldValue(admin_allowed, "true") //
        .withFieldValue(user_allowed, "true") //
        .build() //
    ) //
    .store()//
    .hasNoValidationErrors()//
    .hasFieldValueAt(GLOBAL_SETTINGS, admin_allowed, "true")//
    .hasFieldValueAt(GLOBAL_SETTINGS, user_allowed, "true")//
    .prnfbSettings();

  assertThat(prnfbSettings.isAdminsAllowed()).isTrue();
  assertThat(prnfbSettings.isUsersAllowed()).isTrue();
 }

 @Test
 public void testThatAdminsAndUsersCanBeDeniedAccessToAdminGUI() throws Exception {
  PrnfbSettings prnfbSettings = prnfbTestBuilder()//
    .isLoggedInAsAdmin()//
    .withNotification( //
      notificationBuilder()//
        .withFieldValue(FORM_IDENTIFIER, GLOBAL_SETTINGS.name())//
        .withFieldValue(FORM_TYPE, GLOBAL_SETTINGS.name())//
        .build() //
    ) //
    .store()//
    .hasNoValidationErrors()//
    .prnfbSettings();

  assertThat(prnfbSettings.isAdminsAllowed()).isFalse();
  assertThat(prnfbSettings.isUsersAllowed()).isFalse();
 }

}
