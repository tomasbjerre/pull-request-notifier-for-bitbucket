package se.bjurr.prnfs.admin;

import static com.atlassian.stash.pull.PullRequestAction.APPROVED;
import static com.atlassian.stash.pull.PullRequestAction.MERGED;
import static com.atlassian.stash.pull.PullRequestAction.OPENED;
import static com.google.common.base.Charsets.UTF_8;
import static com.google.common.base.Joiner.on;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.io.Resources.getResource;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.lang.Thread.sleep;
import static java.util.Collections.sort;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static se.bjurr.prnfs.admin.utils.NotificationBuilder.notificationBuilder;
import static se.bjurr.prnfs.admin.utils.PrnfsTestBuilder.prnfsTestBuilder;
import static se.bjurr.prnfs.admin.utils.PullRequestEventBuilder.pullRequestEventBuilder;
import static se.bjurr.prnfs.admin.utils.PullRequestRefBuilder.pullRequestRefBuilder;
import static se.bjurr.prnfs.listener.PrnfsPullRequestEventListener.dublicateEventBug;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.regex.Pattern;

import org.junit.Test;

import se.bjurr.prnfs.admin.AdminFormValues.FIELDS;
import se.bjurr.prnfs.listener.PrnfsRenderer.PrnfsVariable;

import com.google.common.io.Resources;

public class PrnfsPullRequestEventListenerTest {

 @Test
 public void testThatAdminFormFieldsAreUsedInAdminGUI() throws IOException {
  final URL resource = getResource("admin.vm");
  final String adminVmContent = Resources.toString(resource, UTF_8);
  for (final AdminFormValues.FIELDS field : AdminFormValues.FIELDS.values()) {
   assertTrue(field.name() + " in " + resource.toString(), adminVmContent.contains("name=\"" + field.name() + "\""));
  }
 }

 @Test
 public void testThatAUrlCanHaveSeveralVariables() {
  prnfsTestBuilder()
    .isLoggedInAsAdmin()
    .withNotification(
      notificationBuilder()
        .withFieldValue(
          AdminFormValues.FIELDS.url,
          "http://bjurr.se/?PULL_REQUEST_FROM_HASH=${PULL_REQUEST_FROM_HASH}&PULL_REQUEST_TO_HASH=${PULL_REQUEST_TO_HASH}&PULL_REQUEST_FROM_REPO_SLUG=${PULL_REQUEST_FROM_REPO_SLUG}&PULL_REQUEST_TO_REPO_SLUG=${PULL_REQUEST_TO_REPO_SLUG}")
        .withFieldValue(AdminFormValues.FIELDS.events, OPENED.name()).build())
    .store()
    .trigger(pullRequestEventBuilder() //
      .withFromRef(pullRequestRefBuilder().withHash("cde456").withRepositorySlug("fromslug")) //
      .withToRef(pullRequestRefBuilder().withHash("asd123").withRepositorySlug("toslug")) //
      .withId(10L).withPullRequestAction(OPENED).build())
    .invokedUrl(
      "http://bjurr.se/?PULL_REQUEST_FROM_HASH=cde456&PULL_REQUEST_TO_HASH=asd123&PULL_REQUEST_FROM_REPO_SLUG=fromslug&PULL_REQUEST_TO_REPO_SLUG=toslug");
 }

 @Test
 public void testThatAUrlIsOnlyInvokedForConfiguredEvents() {
  prnfsTestBuilder()
    .isLoggedInAsAdmin()
    .withNotification(
      notificationBuilder().withFieldValue(AdminFormValues.FIELDS.url, "http://bjurr.se/")
        .withFieldValue(AdminFormValues.FIELDS.events, OPENED.name()).build()).store()
    .trigger(pullRequestEventBuilder() //
      .withToRef(pullRequestRefBuilder()) //
      .withId(10L).withPullRequestAction(MERGED).build()).invokedNoUrl();
 }

 @Test
 public void testThatAUrlWithoutVariablesCanBeInvoked() {
  prnfsTestBuilder()
    .isLoggedInAsAdmin()
    .withNotification(
      notificationBuilder().withFieldValue(AdminFormValues.FIELDS.url, "http://bjurr.se/")
        .withFieldValue(AdminFormValues.FIELDS.events, OPENED.name()).build()).store()
    .trigger(pullRequestEventBuilder().withPullRequestAction(OPENED).build()).invokedUrl("http://bjurr.se/");
 }

 @Test
 public void testThatAUrlWithVariablesCanBeInvokedFrom() {
  for (final PrnfsVariable prnfsVariable : PrnfsVariable.values()) {
   if (prnfsVariable.name().contains("_TO_")) {
    continue;
   }
   prnfsTestBuilder()
     .isLoggedInAsAdmin()
     .withNotification(
       notificationBuilder()
         .withFieldValue(AdminFormValues.FIELDS.url, "http://bjurr.se/${" + prnfsVariable.name() + "}")
         .withFieldValue(AdminFormValues.FIELDS.events, OPENED.name()).build())
     .store()
     .trigger(
       pullRequestEventBuilder() //
         .withFromRef(
           pullRequestRefBuilder().withHash("10").withId("10").withProjectId(10).withProjectKey("10")
             .withRepositoryId(10).withRepositoryName("10").withRepositorySlug("10")) //
         .withId(10L).withPullRequestAction(OPENED).build()).invokedUrl("http://bjurr.se/10");
  }
 }

 @Test
 public void testThatAUrlWithVariablesCanBeInvokedTo() {
  for (final PrnfsVariable prnfsVariable : PrnfsVariable.values()) {
   if (prnfsVariable.name().contains("_FROM_")) {
    continue;
   }
   prnfsTestBuilder()
     .isLoggedInAsAdmin()
     .withNotification(
       notificationBuilder()
         .withFieldValue(AdminFormValues.FIELDS.url, "http://bjurr.se/${" + prnfsVariable.name() + "}")
         .withFieldValue(AdminFormValues.FIELDS.events, OPENED.name()).build())
     .store()
     .trigger(
       pullRequestEventBuilder() //
         .withToRef(
           pullRequestRefBuilder().withHash("10").withId("10").withProjectId(10).withProjectKey("10")
             .withRepositoryId(10).withRepositoryName("10").withRepositorySlug("10")) //
         .withId(10L).withPullRequestAction(OPENED).build()).invokedUrl("http://bjurr.se/10");
  }
 }

 @Test
 public void testThatBasicAuthenticationHeaderIsSentIfThereIsAUser() {
  prnfsTestBuilder()
    .isLoggedInAsAdmin()
    .withNotification(
      notificationBuilder().withFieldValue(AdminFormValues.FIELDS.url, "http://bjurr.se/")
        .withFieldValue(AdminFormValues.FIELDS.events, OPENED.name())
        .withFieldValue(AdminFormValues.FIELDS.user, "theuser")
        .withFieldValue(AdminFormValues.FIELDS.password, "thepassword").build()).store()
    .trigger(pullRequestEventBuilder().withPullRequestAction(OPENED).build()).invokedUrl("http://bjurr.se/")
    .invokedUser("theuser").invokedPassword("thepassword");
 }

 @Test
 public void testThatDuplicateEventsFiredInStashAreIgnored() throws InterruptedException {
  assertEquals(FALSE, dublicateEventBug(pullRequestEventBuilder().withId(100L).withPullRequestAction(APPROVED).build()));
  assertEquals(TRUE, dublicateEventBug(pullRequestEventBuilder().withId(100L).withPullRequestAction(APPROVED).build()));
  sleep(100);
  assertEquals(FALSE, dublicateEventBug(pullRequestEventBuilder().withId(100L).withPullRequestAction(APPROVED).build()));
 }

 @Test
 public void testThatFieldsUsedInAdminGUIArePresentInAdminFormFields() throws IOException {
  final URL resource = getResource("admin.vm");
  final String adminVmContent = Resources.toString(resource, UTF_8);
  final java.util.regex.Matcher m = Pattern.compile("<input [^n]*name=\"([^\\\"]*)\"").matcher(adminVmContent);
  while (m.find()) {
   assertTrue(m.group(1) + " found at " + resource.toString(), AdminFormValues.FIELDS.valueOf(m.group(1)) != null);
  }
 }

 @Test
 public void testThatFilterCanBeUsedToIgnoreEventsThatAreOnAnotherProject() {
  prnfsTestBuilder()
    .isLoggedInAsAdmin()
    .withNotification(
      notificationBuilder().withFieldValue(AdminFormValues.FIELDS.url, "http://bjurr.se/")
        .withFieldValue(AdminFormValues.FIELDS.events, OPENED.name())
        .withFieldValue(FIELDS.filter_string, "${PULL_REQUEST_FROM_REPO_PROJECT_KEY}")
        .withFieldValue(FIELDS.filter_regexp, "EXP").build()).store().trigger(pullRequestEventBuilder() //
      .withFromRef(pullRequestRefBuilder().withProjectKey("ABC")) //
      .withId(10L).withPullRequestAction(OPENED).build()).invokedNoUrl();
 }

 @Test
 public void testThatFilterCanBeUsedToIgnoreEventsThatAreOnAnotherProjectAnBranch() {
  prnfsTestBuilder()
    .isLoggedInAsAdmin()
    .withNotification(
      notificationBuilder().withFieldValue(AdminFormValues.FIELDS.url, "http://bjurr.se/")
        .withFieldValue(AdminFormValues.FIELDS.events, OPENED.name())
        .withFieldValue(FIELDS.filter_string, "${PULL_REQUEST_FROM_REPO_PROJECT_KEY} ${PULL_REQUEST_FROM_ID}")
        .withFieldValue(FIELDS.filter_regexp, "EXP my_branch").build()).store().trigger(pullRequestEventBuilder() //
      .withFromRef(pullRequestRefBuilder().withProjectKey("ABC").withId("my_therbranch")) //
      .withId(10L).withPullRequestAction(OPENED).build()).invokedNoUrl();
 }

 @Test
 public void testThatFilterCanBeUsedToTriggerEventsThatAreOnAnotherProject() {
  prnfsTestBuilder()
    .isLoggedInAsAdmin()
    .withNotification(
      notificationBuilder().withFieldValue(AdminFormValues.FIELDS.url, "http://bjurr.se/")
        .withFieldValue(AdminFormValues.FIELDS.events, OPENED.name())
        .withFieldValue(FIELDS.filter_string, "${PULL_REQUEST_FROM_REPO_PROJECT_KEY}")
        .withFieldValue(FIELDS.filter_regexp, "EXP").build()).store().trigger(pullRequestEventBuilder() //
      .withFromRef(pullRequestRefBuilder().withProjectKey("EXP")) //
      .withId(10L).withPullRequestAction(OPENED).build()).invokedUrl("http://bjurr.se/");
 }

 @Test
 public void testThatFilterCanBeUsedToTriggerOnEventsThatAreOnAnotherProjectAnBranch() {
  prnfsTestBuilder()
    .isLoggedInAsAdmin()
    .withNotification(
      notificationBuilder().withFieldValue(AdminFormValues.FIELDS.url, "http://bjurr.se/")
        .withFieldValue(AdminFormValues.FIELDS.events, OPENED.name())
        .withFieldValue(FIELDS.filter_string, "${PULL_REQUEST_FROM_REPO_PROJECT_KEY} ${PULL_REQUEST_FROM_ID}")
        .withFieldValue(FIELDS.filter_regexp, "EXP my_branch").build()).store().trigger(pullRequestEventBuilder() //
      .withFromRef(pullRequestRefBuilder().withProjectKey("EXP").withId("my_branch")) //
      .withId(10L).withPullRequestAction(OPENED).build()).invokedUrl("http://bjurr.se/");
 }

 @Test
 public void testThatMultipleUrlsCanBeInvoked() {
  prnfsTestBuilder()
    .isLoggedInAsAdmin()
    .withNotification(
      notificationBuilder().withFieldValue(AdminFormValues.FIELDS.url, "http://merged.se/")
        .withFieldValue(AdminFormValues.FIELDS.events, MERGED.name()).build())
    .withNotification(
      notificationBuilder().withFieldValue(AdminFormValues.FIELDS.url, "http://opened.se/")
        .withFieldValue(AdminFormValues.FIELDS.events, OPENED.name()).build()).store()
    .trigger(pullRequestEventBuilder() //
      .withToRef(pullRequestRefBuilder()) //
      .withId(10L).withPullRequestAction(MERGED).build()).invokedOnlyUrl("http://merged.se/").didNotUseBasicAuth();
 }

 @Test
 public void testThatVariablesAreImplementedForBothFromAndTo() {
  final List<String> from = newArrayList();
  final List<String> to = newArrayList();
  for (final PrnfsVariable prnfsVariable : PrnfsVariable.values()) {
   if (prnfsVariable.name().contains("_FROM_")) {
    from.add(prnfsVariable.name());
   } else if (prnfsVariable.name().contains("_TO_")) {
    to.add(prnfsVariable.name());
   }
  }
  sort(from);
  sort(to);
  assertEquals(on(" ").join(from) + " != " + on(" ").join(to), from.size(), to.size());
 }

 @Test
 public void testThatVariablesAreMentionedInAdminGUI() throws IOException {
  final URL resource = getResource("admin.vm");
  final String adminVmContent = Resources.toString(resource, UTF_8);
  for (final PrnfsVariable prnfsVariable : PrnfsVariable.values()) {
   assertTrue(prnfsVariable.name() + " in " + resource.toString(), adminVmContent.contains(prnfsVariable.name()));
  }
 }
}
