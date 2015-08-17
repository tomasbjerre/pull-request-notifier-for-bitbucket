package se.bjurr.prnfs.admin;

import static com.atlassian.stash.pull.PullRequestAction.OPENED;
import static se.bjurr.prnfs.admin.AdminFormValues.DEFAULT_NAME;
import static se.bjurr.prnfs.admin.utils.NotificationBuilder.notificationBuilder;
import static se.bjurr.prnfs.admin.utils.PrnfsTestBuilder.prnfsTestBuilder;

import org.junit.Test;

public class NotificationsStorageTest {
 @Test
 public void testThatANewNotificationCanBeStored() throws Exception {
  prnfsTestBuilder()
    .isLoggedInAsAdmin()
    .withNotification(
      notificationBuilder().withFieldValue(AdminFormValues.FIELDS.url, "http://bjurr.se/")
        .withFieldValue(AdminFormValues.FIELDS.events, OPENED.name()).build()).store().hasNotifications(1)
    .hasFieldValueAt(AdminFormValues.FIELDS.url, "http://bjurr.se/", "0")
    .hasFieldValueAt(AdminFormValues.FIELDS.name, DEFAULT_NAME, "0")
    .hasNoneEmptyFieldAt(AdminFormValues.FIELDS.FORM_IDENTIFIER, "0");
 }

 @Test
 public void testThatANewNotificationCanBeStoredWithWhiteSpaceInFormIdentifier() throws Exception {
  prnfsTestBuilder()
    .isLoggedInAsAdmin()
    .withNotification(
      notificationBuilder().withFieldValue(AdminFormValues.FIELDS.url, "http://bjurr.se/")
        .withFieldValue(AdminFormValues.FIELDS.events, OPENED.name())
        .withFieldValue(AdminFormValues.FIELDS.FORM_IDENTIFIER, " ").build()).store().hasNotifications(1)
    .hasFieldValueAt(AdminFormValues.FIELDS.url, "http://bjurr.se/", "0")
    .hasNoneEmptyFieldAt(AdminFormValues.FIELDS.FORM_IDENTIFIER, "0");
 }

 @Test
 public void testThatFilterRegExpMustBeValid() throws Exception {
  prnfsTestBuilder()
    .isLoggedInAsAdmin()
    .withNotification(
      notificationBuilder().withFieldValue(AdminFormValues.FIELDS.events, OPENED.name())
        .withFieldValue(AdminFormValues.FIELDS.url, "http://bjurr.se/")
        .withFieldValue(AdminFormValues.FIELDS.filter_regexp, "[").build())
    .store()
    .hasValidationError(AdminFormValues.FIELDS.filter_regexp,
      "Filter regexp not valid! Unclosed character class near index 0 [ ^");
 }

 @Test
 public void testThatFilterTextCannotBeEmptyWhenRegExpMustIsSet() throws Exception {
  prnfsTestBuilder()
    .isLoggedInAsAdmin()
    .withNotification(
      notificationBuilder().withFieldValue(AdminFormValues.FIELDS.events, OPENED.name())
        .withFieldValue(AdminFormValues.FIELDS.url, "http://bjurr.se/")
        .withFieldValue(AdminFormValues.FIELDS.filter_regexp, "master").build())
    .store()
    .hasValidationError(AdminFormValues.FIELDS.filter_string, "Filter string not set, nothing to match regexp against!");
 }

 @Test
 public void testThatTwoNewNotificationsCanBeStored() throws Exception {
  prnfsTestBuilder()
    .isLoggedInAsAdmin()
    .withNotification(
      notificationBuilder().withFieldValue(AdminFormValues.FIELDS.url, "http://bjurr.se/")
        .withFieldValue(AdminFormValues.FIELDS.events, OPENED.name()).build())
    .withNotification(
      notificationBuilder().withFieldValue(AdminFormValues.FIELDS.url, "http://bjurr.se/?2")
        .withFieldValue(AdminFormValues.FIELDS.events, OPENED.name()).build()).store().hasNotifications(2)
    .hasFieldValueAt(AdminFormValues.FIELDS.url, "http://bjurr.se/", "0")
    .hasNoneEmptyFieldAt(AdminFormValues.FIELDS.FORM_IDENTIFIER, "0")
    .hasFieldValueAt(AdminFormValues.FIELDS.url, "http://bjurr.se/?2", "1")
    .hasNoneEmptyFieldAt(AdminFormValues.FIELDS.FORM_IDENTIFIER, "1");
 }

 @Test
 public void testThatTwoNewNotificationsCanBeStoredAndThenOneDeleted() throws Exception {
  prnfsTestBuilder()
    .isLoggedInAsAdmin()
    .withNotification(
      notificationBuilder().withFieldValue(AdminFormValues.FIELDS.url, "http://bjurr.se/")
        .withFieldValue(AdminFormValues.FIELDS.events, OPENED.name()).build())
    .withNotification(
      notificationBuilder().withFieldValue(AdminFormValues.FIELDS.url, "http://bjurr.se/?2")
        .withFieldValue(AdminFormValues.FIELDS.events, OPENED.name()).build()).store().hasNotifications(2)
    .hasFieldValueAt(AdminFormValues.FIELDS.url, "http://bjurr.se/", "0")
    .hasNoneEmptyFieldAt(AdminFormValues.FIELDS.FORM_IDENTIFIER, "0")
    .hasFieldValueAt(AdminFormValues.FIELDS.url, "http://bjurr.se/?2", "1")
    .hasNoneEmptyFieldAt(AdminFormValues.FIELDS.FORM_IDENTIFIER, "1").delete("0").hasNotifications(1)
    .hasFieldValueAt(AdminFormValues.FIELDS.url, "http://bjurr.se/?2", "1")
    .hasNoneEmptyFieldAt(AdminFormValues.FIELDS.FORM_IDENTIFIER, "1") //
    .delete("1") //
    .hasNotifications(0);
 }

 @Test
 public void testThatTwoNewNotificationsCanBeStoredAndThenOneUpdated() throws Exception {
  prnfsTestBuilder()
    .isLoggedInAsAdmin()
    .withNotification(
      notificationBuilder().withFieldValue(AdminFormValues.FIELDS.url, "http://bjurr.se/")
        .withFieldValue(AdminFormValues.FIELDS.events, OPENED.name()).build())
    .withNotification(
      notificationBuilder().withFieldValue(AdminFormValues.FIELDS.url, "http://bjurr.se/?2")
        .withFieldValue(AdminFormValues.FIELDS.events, OPENED.name()).build())
    .store()
    .hasNotifications(2)
    .hasFieldValueAt(AdminFormValues.FIELDS.url, "http://bjurr.se/", "0")
    .hasNoneEmptyFieldAt(AdminFormValues.FIELDS.FORM_IDENTIFIER, "0")
    .hasFieldValueAt(AdminFormValues.FIELDS.url, "http://bjurr.se/?2", "1")
    .hasNoneEmptyFieldAt(AdminFormValues.FIELDS.FORM_IDENTIFIER, "1")
    .withNotification(
      notificationBuilder().withFieldValue(AdminFormValues.FIELDS.url, "http://bjurr.se/?2upd")
        .withFieldValue(AdminFormValues.FIELDS.FORM_IDENTIFIER, "1").build())
    .store()
    .hasFieldValueAt(AdminFormValues.FIELDS.url, "http://bjurr.se/", "0")
    .hasNoneEmptyFieldAt(AdminFormValues.FIELDS.FORM_IDENTIFIER, "0")
    .hasFieldValueAt(AdminFormValues.FIELDS.url, "http://bjurr.se/?2upd", "1")
    .hasNoneEmptyFieldAt(AdminFormValues.FIELDS.FORM_IDENTIFIER, "1")
    .withNotification(
      notificationBuilder().withFieldValue(AdminFormValues.FIELDS.url, "http://bjurr.se/?upd")
        .withFieldValue(AdminFormValues.FIELDS.FORM_IDENTIFIER, "0").build()).store()
    .hasFieldValueAt(AdminFormValues.FIELDS.url, "http://bjurr.se/?upd", "0")
    .hasNoneEmptyFieldAt(AdminFormValues.FIELDS.FORM_IDENTIFIER, "0")
    .hasFieldValueAt(AdminFormValues.FIELDS.url, "http://bjurr.se/?2upd", "1")
    .hasNoneEmptyFieldAt(AdminFormValues.FIELDS.FORM_IDENTIFIER, "1");
 }

 @Test
 public void testThatUrlMustBeSet() throws Exception {
  prnfsTestBuilder().isLoggedInAsAdmin()
    .withNotification(notificationBuilder().withFieldValue(AdminFormValues.FIELDS.events, OPENED.name()).build())
    .store().hasValidationError(AdminFormValues.FIELDS.url, "URL not set");
 }

 @Test
 public void testThatUrlMustBeValid() throws Exception {
  prnfsTestBuilder()
    .isLoggedInAsAdmin()
    .withNotification(
      notificationBuilder().withFieldValue(AdminFormValues.FIELDS.events, OPENED.name())
        .withFieldValue(AdminFormValues.FIELDS.url, "notcorrect").build()).store()
    .hasValidationError(AdminFormValues.FIELDS.url, "URL not valid!");
 }

 @Test
 public void testThatValuesMustBeSet() throws Exception {
  prnfsTestBuilder().isLoggedInAsAdmin().withNotification(notificationBuilder().build()).store()
    .hasValidationError(AdminFormValues.FIELDS.url, "URL not set");
 }
}
