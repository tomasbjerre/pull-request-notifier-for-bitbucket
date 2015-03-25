package se.bjurr.prnfs.admin;

import static com.atlassian.stash.pull.PullRequestAction.OPENED;
import static se.bjurr.prnfs.admin.utils.AdminRequestBuilder.adminRequestBuilder;
import static se.bjurr.prnfs.admin.utils.NotificationBuilder.notificationBuilder;
import static se.bjurr.prnfs.settings.SettingsStorage.FORM_IDENTIFIER_NAME;

import org.junit.Test;

public class NotificationsStorageTest {
 @Test
 public void testThatANewNotificationCanBeStored() {
  adminRequestBuilder()
    .isLoggedInAsAdmin()
    .withNotification(
      notificationBuilder().withFieldValue("url", "http://bjurr.se/").withFieldValue("events", OPENED.name()).build())
    .store() //
    .hasNotifications(1) //
    .hasFieldValueAt("url", "http://bjurr.se/", "0").hasNoneEmptyFieldAt(FORM_IDENTIFIER_NAME, "0");
 }

 @Test
 public void testThatTwoNewNotificationsCanBeStored() {
  adminRequestBuilder()
    .isLoggedInAsAdmin()
    .withNotification(
      notificationBuilder().withFieldValue("url", "http://bjurr.se/").withFieldValue("events", OPENED.name()).build())
    .withNotification(
      notificationBuilder().withFieldValue("url", "http://bjurr.se/?2").withFieldValue("events", OPENED.name()).build())
    .store() //
    .hasNotifications(2) //
    .hasFieldValueAt("url", "http://bjurr.se/", "0").hasNoneEmptyFieldAt(FORM_IDENTIFIER_NAME, "0") //
    .hasFieldValueAt("url", "http://bjurr.se/?2", "1").hasNoneEmptyFieldAt(FORM_IDENTIFIER_NAME, "1");
 }

 @Test
 public void testThatTwoNewNotificationsCanBeStoredAndThenOneDeleted() {
  adminRequestBuilder()
    .isLoggedInAsAdmin()
    .withNotification(
      notificationBuilder().withFieldValue("url", "http://bjurr.se/").withFieldValue("events", OPENED.name()).build())
    .withNotification(
      notificationBuilder().withFieldValue("url", "http://bjurr.se/?2").withFieldValue("events", OPENED.name()).build())
    .store() //
    .hasNotifications(2) //
    .hasFieldValueAt("url", "http://bjurr.se/", "0").hasNoneEmptyFieldAt(FORM_IDENTIFIER_NAME, "0") //
    .hasFieldValueAt("url", "http://bjurr.se/?2", "1").hasNoneEmptyFieldAt(FORM_IDENTIFIER_NAME, "1") //
    .delete("0") //
    .hasNotifications(1) //
    .hasFieldValueAt("url", "http://bjurr.se/?2", "1").hasNoneEmptyFieldAt(FORM_IDENTIFIER_NAME, "1") //
    .delete("1") //
    .hasNotifications(0);
 }

 @Test
 public void testThatTwoNewNotificationsCanBeStoredAndThenOneUpdated() {
  adminRequestBuilder()
    .isLoggedInAsAdmin()
    .withNotification(
      notificationBuilder().withFieldValue("url", "http://bjurr.se/").withFieldValue("events", OPENED.name()).build())
    .withNotification(
      notificationBuilder().withFieldValue("url", "http://bjurr.se/?2").withFieldValue("events", OPENED.name()).build())
    .store()
    .hasNotifications(2)
    .hasFieldValueAt("url", "http://bjurr.se/", "0")
    .hasNoneEmptyFieldAt(FORM_IDENTIFIER_NAME, "0")
    .hasFieldValueAt("url", "http://bjurr.se/?2", "1")
    .hasNoneEmptyFieldAt(FORM_IDENTIFIER_NAME, "1")
    .withNotification(
      notificationBuilder().withFieldValue("url", "http://bjurr.se/?2upd").withFieldValue(FORM_IDENTIFIER_NAME, "1")
        .build())
    .store()
    .hasFieldValueAt("url", "http://bjurr.se/", "0")
    .hasNoneEmptyFieldAt(FORM_IDENTIFIER_NAME, "0")
    .hasFieldValueAt("url", "http://bjurr.se/?2upd", "1")
    .hasNoneEmptyFieldAt(FORM_IDENTIFIER_NAME, "1")
    .withNotification(
      notificationBuilder().withFieldValue("url", "http://bjurr.se/?upd").withFieldValue(FORM_IDENTIFIER_NAME, "0")
        .build()).store() //
    .hasFieldValueAt("url", "http://bjurr.se/?upd", "0").hasNoneEmptyFieldAt(FORM_IDENTIFIER_NAME, "0") //
    .hasFieldValueAt("url", "http://bjurr.se/?2upd", "1").hasNoneEmptyFieldAt(FORM_IDENTIFIER_NAME, "1");
 }

 @Test
 public void testThatUrlMustBeSet() {
  adminRequestBuilder().isLoggedInAsAdmin()
    .withNotification(notificationBuilder().withFieldValue("events", OPENED.name()).build()).store()
    .hasValidationError("url", "URL not set");
 }

 @Test
 public void testThatUrlMustBeValid() {
  adminRequestBuilder()
    .isLoggedInAsAdmin()
    .withNotification(
      notificationBuilder().withFieldValue("events", OPENED.name()).withFieldValue("url", "notcorrect").build())
    .store().hasValidationError("url", "URL not valid!");
 }

 @Test
 public void testThatValuesMustBeSet() {
  adminRequestBuilder().isLoggedInAsAdmin().withNotification(notificationBuilder().build()).store()
    .hasValidationError("url", "URL not set");
 }
}
