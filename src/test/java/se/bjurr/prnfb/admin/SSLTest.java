package se.bjurr.prnfb.admin;

import static com.atlassian.bitbucket.pull.PullRequestAction.OPENED;
import static org.assertj.core.api.Assertions.assertThat;
import static se.bjurr.prnb.admin.utils.NotificationBuilder.notificationBuilder;
import static se.bjurr.prnb.admin.utils.PrnfbTestBuilder.prnfbTestBuilder;
import static se.bjurr.prnb.admin.utils.PullRequestEventBuilder.pullRequestEventBuilder;
import static se.bjurr.prnfb.admin.AdminFormValues.FIELDS.FORM_IDENTIFIER;
import static se.bjurr.prnfb.admin.AdminFormValues.FIELDS.FORM_TYPE;
import static se.bjurr.prnfb.admin.AdminFormValues.FIELDS.accept_any_certificate;
import static se.bjurr.prnfb.admin.AdminFormValues.FIELDS.events;
import static se.bjurr.prnfb.admin.AdminFormValues.FIELDS.key_store;
import static se.bjurr.prnfb.admin.AdminFormValues.FIELDS.key_store_password;
import static se.bjurr.prnfb.admin.AdminFormValues.FIELDS.key_store_type;
import static se.bjurr.prnfb.admin.AdminFormValues.FIELDS.url;
import static se.bjurr.prnfb.admin.AdminFormValues.FORM_TYPE.GLOBAL_SETTINGS;

import org.junit.Test;

import se.bjurr.prnfb.settings.PrnfbSettings;

public class SSLTest {

 @Test
 public void testThatAnyCertificateCanBeAccepted() throws Exception {
  PrnfbSettings prnfbSettings = prnfbTestBuilder()//
    .isLoggedInAsAdmin() //
    .withNotification( //
      notificationBuilder() //
        .withFieldValue(url, "http://bjurr.se/") //
        .withFieldValue(events, OPENED.name()) //
        .withFieldValue(accept_any_certificate, "true") //
        .build() //
    ) //
    .store() //
    .trigger( //
      pullRequestEventBuilder() //
        .withPullRequestAction(OPENED) //
        .build() //
    ) //
    .invokedOnlyUrl("http://bjurr.se/") //
    .allowedAnyCertificate(true)//
    .prnfbSettings();

  assertThat(prnfbSettings.shouldAcceptAnyCertificate()).isTrue();
 }

 @Test
 public void testThatCertificateCanBeForced() throws Exception {
  PrnfbSettings prnfbSettings = prnfbTestBuilder()//
    .isLoggedInAsAdmin() //
    .withNotification( //
      notificationBuilder() //
        .withFieldValue(url, "http://bjurr.se/") //
        .withFieldValue(events, OPENED.name()) //
        .build() //
    ) //
    .store() //
    .trigger( //
      pullRequestEventBuilder() //
        .withPullRequestAction(OPENED) //
        .build() //
    ) //
    .invokedOnlyUrl("http://bjurr.se/") //
    .allowedAnyCertificate(false)//
    .prnfbSettings();

  assertThat(prnfbSettings.shouldAcceptAnyCertificate()).isFalse();
 }

 @Test
 public void testThatKeyStoreCanBeConfigured() throws Exception {
  PrnfbSettings prnfbSettings = prnfbTestBuilder()//
    .isLoggedInAsAdmin()//
    .withNotification( //
      notificationBuilder()//
        .withFieldValue(FORM_IDENTIFIER, GLOBAL_SETTINGS.name())//
        .withFieldValue(FORM_TYPE, GLOBAL_SETTINGS.name())//
        .withFieldValue(key_store, "ks") //
        .withFieldValue(key_store_type, "kst") //
        .withFieldValue(key_store_password, "ksp") //
        .build() //
    ) //
    .store()//
    .hasNoValidationErrors()//
    .hasFieldValueAt(GLOBAL_SETTINGS, key_store, "ks")//
    .hasFieldValueAt(GLOBAL_SETTINGS, key_store_type, "kst")//
    .hasFieldValueAt(GLOBAL_SETTINGS, key_store_password, "ksp")//
    .prnfbSettings();

  assertThat(prnfbSettings.getKeyStore().get()).isEqualTo("ks");
  assertThat(prnfbSettings.getKeyStoreType()).isEqualTo("kst");
  assertThat(prnfbSettings.getKeyStorePassword().get()).isEqualTo("ksp");
 }

}
