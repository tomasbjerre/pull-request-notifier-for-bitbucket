package se.bjurr.prnfb.admin;

import static com.atlassian.bitbucket.pull.PullRequestAction.OPENED;
import static se.bjurr.prnbb.admin.utils.NotificationBuilder.notificationBuilder;
import static se.bjurr.prnbb.admin.utils.PrnfbTestBuilder.prnfbTestBuilder;
import static se.bjurr.prnfb.admin.AdminFormValues.DEFAULT_NAME;
import static se.bjurr.prnfb.admin.AdminFormValues.FIELDS.FORM_IDENTIFIER;
import static se.bjurr.prnfb.admin.AdminFormValues.FIELDS.events;
import static se.bjurr.prnfb.admin.AdminFormValues.FIELDS.filter_regexp;
import static se.bjurr.prnfb.admin.AdminFormValues.FIELDS.filter_string;
import static se.bjurr.prnfb.admin.AdminFormValues.FIELDS.name;
import static se.bjurr.prnfb.admin.AdminFormValues.FIELDS.url;

import org.junit.Test;

public class NotificationsStorageTest {
 @Test
 public void testThatANewNotificationCanBeStored() throws Exception {
  prnfbTestBuilder() //
    .isLoggedInAsAdmin() //
    .withNotification( //
      notificationBuilder() //
        .withFieldValue(url, "http://bjurr.se/") //
        .withFieldValue(events, OPENED.name()) //
        .build() //
    ) //
    .store() //
    .hasNotifications(1) //
    .hasFieldValueAt(url, "http://bjurr.se/", "0") //
    .hasFieldValueAt(name, DEFAULT_NAME, "0") //
    .hasNoneEmptyFieldAt(FORM_IDENTIFIER, "0");
 }

 @Test
 public void testThatANewNotificationCanBeStoredWithWhiteSpaceInFormIdentifier() throws Exception {
  prnfbTestBuilder() //
    .isLoggedInAsAdmin() //
    .withNotification( //
      notificationBuilder() //
        .withFieldValue(url, "http://bjurr.se/") //
        .withFieldValue(events, OPENED.name()) //
        .withFieldValue(FORM_IDENTIFIER, " ") //
        .build() //
    ) //
    .store() //
    .hasNotifications(1) //
    .hasFieldValueAt(url, "http://bjurr.se/", "0") //
    .hasNoneEmptyFieldAt(FORM_IDENTIFIER, "0");
 }

 @Test
 public void testThatFilterRegExpMustBeValid() throws Exception {
  prnfbTestBuilder() //
    .isLoggedInAsAdmin() //
    .withNotification( //
      notificationBuilder() //
        .withFieldValue(events, OPENED.name()) //
        .withFieldValue(url, "http://bjurr.se/") //
        .withFieldValue(filter_regexp, "[") //
        .build() //
    ) //
    .store() //
    .hasValidationError(filter_regexp, "Filter regexp not valid! Unclosed character class near index 0 [ ^");
 }

 @Test
 public void testThatFilterTextCannotBeEmptyWhenRegExpMustIsSet() throws Exception {
  prnfbTestBuilder() //
    .isLoggedInAsAdmin() //
    .withNotification( //
      notificationBuilder() //
        .withFieldValue(events, OPENED.name()) //
        .withFieldValue(url, "http://bjurr.se/") //
        .withFieldValue(filter_regexp, "master") //
        .build() //
    ) //
    .store() //
    .hasValidationError(filter_string, "Filter string not set, nothing to match regexp against!");
 }

 @Test
 public void testThatTwoNewNotificationsCanBeStored() throws Exception {
  prnfbTestBuilder() //
    .isLoggedInAsAdmin() //
    .withNotification( //
      notificationBuilder() //
        .withFieldValue(url, "http://bjurr.se/") //
        .withFieldValue(events, OPENED.name()) //
        .build() //
    ) //
    .withNotification( //
      notificationBuilder() //
        .withFieldValue(url, "http://bjurr.se/?2") //
        .withFieldValue(events, OPENED.name()) //
        .build() //
    ) //
    .store() //
    .hasNotifications(2) //
    .hasFieldValueAt(url, "http://bjurr.se/", "0") //
    .hasNoneEmptyFieldAt(FORM_IDENTIFIER, "0") //
    .hasFieldValueAt(url, "http://bjurr.se/?2", "1") //
    .hasNoneEmptyFieldAt(FORM_IDENTIFIER, "1");
 }

 @Test
 public void testThatTwoNewNotificationsCanBeStoredAndThenOneDeleted() throws Exception {
  prnfbTestBuilder() //
    .isLoggedInAsAdmin() //
    .withNotification( //
      notificationBuilder() //
        .withFieldValue(url, "http://bjurr.se/") //
        .withFieldValue(events, OPENED.name()) //
        .build()).withNotification( //
      notificationBuilder() //
        .withFieldValue(url, "http://bjurr.se/?2") //
        .withFieldValue(events, OPENED.name()) //
        .build() //
    ).store() //
    .hasNotifications(2) //
    .hasFieldValueAt(url, "http://bjurr.se/", "0") //
    .hasNoneEmptyFieldAt(FORM_IDENTIFIER, "0") //
    .hasFieldValueAt(url, "http://bjurr.se/?2", "1") //
    .hasNoneEmptyFieldAt(FORM_IDENTIFIER, "1") //
    .delete("0") //
    .hasNotifications(1) //
    .hasFieldValueAt(url, "http://bjurr.se/?2", "1") //
    .hasNoneEmptyFieldAt(FORM_IDENTIFIER, "1") //
    .delete("1") //
    .hasNotifications(0);
 }

 @Test
 public void testThatTwoNewNotificationsCanBeStoredAndThenOneUpdated() throws Exception {
  prnfbTestBuilder().isLoggedInAsAdmin().withNotification( //
    notificationBuilder() //
      .withFieldValue(url, "http://bjurr.se/") //
      .withFieldValue(events, OPENED.name()) //
      .build() //
    ).withNotification( //
      notificationBuilder() //
        .withFieldValue(url, "http://bjurr.se/?2") //
        .withFieldValue(events, OPENED.name()) //
        .build() //
    ).store() //
    .hasNotifications(2) //
    .hasFieldValueAt(url, "http://bjurr.se/", "0") //
    .hasNoneEmptyFieldAt(FORM_IDENTIFIER, "0") //
    .hasFieldValueAt(url, "http://bjurr.se/?2", "1") //
    .hasNoneEmptyFieldAt(FORM_IDENTIFIER, "1") //
    .withNotification( //
      notificationBuilder() //
        .withFieldValue(url, "http://bjurr.se/?2upd") //
        .withFieldValue(FORM_IDENTIFIER, "1") //
        .build() //
    ) //
    .store() //
    .hasFieldValueAt(url, "http://bjurr.se/", "0") //
    .hasNoneEmptyFieldAt(FORM_IDENTIFIER, "0") //
    .hasFieldValueAt(url, "http://bjurr.se/?2upd", "1") //
    .hasNoneEmptyFieldAt(FORM_IDENTIFIER, "1") //
    .withNotification( //
      notificationBuilder() //
        .withFieldValue(url, "http://bjurr.se/?upd") //
        .withFieldValue(FORM_IDENTIFIER, "0") //
        .build() //
    ).store() //
    .hasFieldValueAt(url, "http://bjurr.se/?upd", "0") //
    .hasNoneEmptyFieldAt(FORM_IDENTIFIER, "0") //
    .hasFieldValueAt(url, "http://bjurr.se/?2upd", "1") //
    .hasNoneEmptyFieldAt(FORM_IDENTIFIER, "1");
 }

 @Test
 public void testThatUrlMustBeSet() throws Exception {
  prnfbTestBuilder() //
    .isLoggedInAsAdmin() //
    .withNotification( //
      notificationBuilder() //
        .withFieldValue(events, OPENED.name()) //
        .build() //
    ) //
    .store() //
    .hasValidationError(url, "URL not set");
 }

 @Test
 public void testThatUrlMustBeValid() throws Exception {
  prnfbTestBuilder() //
    .isLoggedInAsAdmin() //
    .withNotification( //
      notificationBuilder() //
        .withFieldValue(events, OPENED.name()) //
        .withFieldValue(url, "notcorrect") //
        .build() //
    ) //
    .store() //
    .hasValidationError(url, "URL not valid!");
 }

 @Test
 public void testThatValuesMustBeSet() throws Exception {
  prnfbTestBuilder() //
    .isLoggedInAsAdmin() //
    .withNotification( //
      notificationBuilder() //
        .build() //
    ) //
    .store() //
    .hasValidationError(url, "URL not set");
 }
}
