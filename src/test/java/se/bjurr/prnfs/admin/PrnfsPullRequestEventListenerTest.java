package se.bjurr.prnfs.admin;

import static com.atlassian.stash.pull.PullRequestAction.COMMENTED;
import static com.atlassian.stash.pull.PullRequestAction.MERGED;
import static com.atlassian.stash.pull.PullRequestAction.OPENED;
import static com.atlassian.stash.pull.PullRequestAction.RESCOPED;
import static com.google.common.base.Charsets.UTF_8;
import static com.google.common.base.Joiner.on;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.io.Resources.getResource;
import static java.util.Collections.sort;
import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static se.bjurr.prnfs.admin.utils.NotificationBuilder.notificationBuilder;
import static se.bjurr.prnfs.admin.utils.PrnfsParticipantBuilder.prnfsParticipantBuilder;
import static se.bjurr.prnfs.admin.utils.PrnfsTestBuilder.prnfsTestBuilder;
import static se.bjurr.prnfs.admin.utils.PullRequestEventBuilder.PREVIOUS_FROM_HASH;
import static se.bjurr.prnfs.admin.utils.PullRequestEventBuilder.PREVIOUS_TO_HASH;
import static se.bjurr.prnfs.admin.utils.PullRequestEventBuilder.pullRequestEventBuilder;
import static se.bjurr.prnfs.admin.utils.PullRequestRefBuilder.pullRequestRefBuilder;
import static se.bjurr.prnfs.listener.PrnfsPullRequestAction.BUTTON_TRIGGER;
import static se.bjurr.prnfs.listener.PrnfsPullRequestAction.RESCOPED_FROM;
import static se.bjurr.prnfs.listener.PrnfsPullRequestAction.RESCOPED_TO;
import static se.bjurr.prnfs.listener.PrnfsRenderer.PrnfsVariable.BUTTON_TRIGGER_TITLE;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.regex.Pattern;

import org.junit.Test;

import se.bjurr.prnfs.admin.AdminFormValues.FIELDS;
import se.bjurr.prnfs.admin.utils.PullRequestEventBuilder;
import se.bjurr.prnfs.admin.utils.PullRequestRefBuilder;
import se.bjurr.prnfs.listener.PrnfsPullRequestAction;
import se.bjurr.prnfs.listener.PrnfsRenderer;
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
    .trigger(
      pullRequestEventBuilder().withFromRef(pullRequestRefBuilder().withHash("cde456").withRepositorySlug("fromslug"))
        .withToRef(pullRequestRefBuilder().withHash("asd123").withRepositorySlug("toslug")).withId(10L)
        .withPullRequestAction(OPENED).build())
    .invokedUrl(
      0,
      "http://bjurr.se/?PULL_REQUEST_FROM_HASH=cde456&PULL_REQUEST_TO_HASH=asd123&PULL_REQUEST_FROM_REPO_SLUG=fromslug&PULL_REQUEST_TO_REPO_SLUG=toslug")
    .invokedMethod("GET");
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
 public void testThatClosedPullRequestsAreIgnored() {
  prnfsTestBuilder()
    .isLoggedInAsAdmin()
    .withNotification(
      notificationBuilder().withFieldValue(AdminFormValues.FIELDS.url, "http://bjurr.se/")
        .withFieldValue(AdminFormValues.FIELDS.events, OPENED.name()).build()).store()
    .trigger(pullRequestEventBuilder() //
      .beingClosed().withToRef(pullRequestRefBuilder()).withPullRequestAction(OPENED).build()).invokedNoUrl();
 }

 @Test
 public void testThatAUrlWithoutVariablesCanBeInvoked() {
  prnfsTestBuilder()
    .isLoggedInAsAdmin()
    .withNotification(
      notificationBuilder().withFieldValue(AdminFormValues.FIELDS.url, "http://bjurr.se/")
        .withFieldValue(AdminFormValues.FIELDS.events, OPENED.name()).build()).store()
    .trigger(pullRequestEventBuilder().withPullRequestAction(OPENED).build()).invokedUrl(0, "http://bjurr.se/");
 }

 @Test
 public void testThatAUrlWithVariablesFromCanBeInvoked() {
  for (final PrnfsVariable prnfsVariable : PrnfsVariable.values()) {
   if (!prnfsVariable.name().contains("_FROM_") && !prnfsVariable.name().contains("_TO_")) {
    continue;
   }
   PullRequestEventBuilder builder = prnfsTestBuilder()
     .isLoggedInAsAdmin()
     .withNotification(
       notificationBuilder()
         .withFieldValue(AdminFormValues.FIELDS.url, "http://bjurr.se/${" + prnfsVariable.name() + "}")
         .withFieldValue(AdminFormValues.FIELDS.events, OPENED.name()).build()).store()
     .triggerPullRequestEventBuilder();

   PullRequestRefBuilder refBuilder;
   if (prnfsVariable.name().contains("_FROM_")) {
    refBuilder = builder.withFromRefPullRequestRefBuilder();
   } else {
    refBuilder = builder.withToRefPullRequestRefBuilder();
   }

   refBuilder.withHash("10").withId("10").withProjectId(10).withProjectKey("10").withRepositoryId(10)
     .withRepositoryName("10").withRepositorySlug("10").withCloneUrl(PrnfsRenderer.REPO_PROTOCOL.http, "10")
     .withCloneUrl(PrnfsRenderer.REPO_PROTOCOL.ssh, "10").withDisplayId("10").build().withId(10L)
     .withPullRequestAction(OPENED).triggerEvent().invokedUrl(0, "http://bjurr.se/10");
  }
 }

 @Test
 public void testThatRepoUrlReturnsEmptyIfThereIsNotUrlWithThatProtocol() {
  prnfsTestBuilder()
    .isLoggedInAsAdmin()
    .withNotification(
      notificationBuilder()
        .withFieldValue(AdminFormValues.FIELDS.url,
          "http://bjurr.se/${" + PrnfsRenderer.PrnfsVariable.PULL_REQUEST_FROM_SSH_CLONE_URL + "}")
        .withFieldValue(AdminFormValues.FIELDS.events, OPENED.name()).build()).store()
    .trigger(pullRequestEventBuilder() //
      .withFromRef(pullRequestRefBuilder()) //
      .withId(10L).withPullRequestAction(OPENED).build()).invokedUrl(0, "http://bjurr.se/");
 }

 @Test
 public void testThatAUrlWithCommentVariableHasSpacesReplaced() {
  prnfsTestBuilder()
    .isLoggedInAsAdmin()
    .withNotification(
      notificationBuilder()
        .withFieldValue(AdminFormValues.FIELDS.url,
          "http://bjurr.se/${" + PrnfsVariable.PULL_REQUEST_COMMENT_TEXT.name() + "}")
        .withFieldValue(AdminFormValues.FIELDS.events, COMMENTED.name()).build())
    .store()
    .trigger(pullRequestEventBuilder().withPullRequestAction(COMMENTED).withCommentText("a text with\nnewline").build())
    .invokedUrl(0, "http://bjurr.se/a%20text%20with%20newline");
 }

 @Test
 public void testThatAUrlWithVariableFromBranchCanBeInvokedWhenBranchIdContainsSlashes() {
  prnfsTestBuilder()
    .isLoggedInAsAdmin()
    .withNotification(
      notificationBuilder()
        .withFieldValue(AdminFormValues.FIELDS.url,
          "http://bjurr.se/${" + PrnfsVariable.PULL_REQUEST_FROM_BRANCH.name() + "}")
        .withFieldValue(AdminFormValues.FIELDS.events, OPENED.name()).build())
    .store()
    .trigger(
      pullRequestEventBuilder()
        .withFromRef(
          pullRequestRefBuilder().withId("refs/heads/feature/branchmodmerge").withDisplayId("feature/branchmodmerge"))
        .withId(10L).withPullRequestAction(OPENED).build()).invokedUrl(0, "http://bjurr.se/feature/branchmodmerge");
 }

 @Test
 public void testThatAUrlWithVariableFromBranchCanBeInvokedWhenBranchIdContainsOnlyName() {
  prnfsTestBuilder()
    .isLoggedInAsAdmin()
    .withNotification(
      notificationBuilder()
        .withFieldValue(AdminFormValues.FIELDS.url,
          "http://bjurr.se/${" + PrnfsVariable.PULL_REQUEST_FROM_BRANCH.name() + "}")
        .withFieldValue(AdminFormValues.FIELDS.events, OPENED.name()).build())
    .store()
    .trigger(
      pullRequestEventBuilder()
        .withFromRef(pullRequestRefBuilder().withId("branchmodmerge").withDisplayId("branchmodmerge")).withId(10L)
        .withPullRequestAction(OPENED).build()).invokedUrl(0, "http://bjurr.se/branchmodmerge");
 }

 @Test
 public void testThatAUrlWithVariablesExceptFromAndToCanBeInvoked() throws Exception {
  prnfsTestBuilder()
    .isLoggedInAsAdmin()
    .withBaseUrl("http://stash.server")
    .withNotification(
      notificationBuilder()
        .withFieldValue(
          AdminFormValues.FIELDS.url,
          "http://bjurr.se/id=${" + PrnfsVariable.PULL_REQUEST_ID.name() + "}&action=${"
            + PrnfsVariable.PULL_REQUEST_ACTION.name() + "}&displayName=${"
            + PrnfsVariable.PULL_REQUEST_AUTHOR_DISPLAY_NAME.name() + "}&authorEmail=${"
            + PrnfsVariable.PULL_REQUEST_AUTHOR_EMAIL.name() + "}&authorId=${"
            + PrnfsVariable.PULL_REQUEST_AUTHOR_ID.name() + "}&authorName=${"
            + PrnfsVariable.PULL_REQUEST_AUTHOR_NAME.name() + "}&authorSlug=${"
            + PrnfsVariable.PULL_REQUEST_AUTHOR_SLUG.name() + "}&pullRequestUrl=${"
            + PrnfsVariable.PULL_REQUEST_URL.name() + "}").withFieldValue(AdminFormValues.FIELDS.events, OPENED.name())
        .build())
    .store()
    .trigger(
      pullRequestEventBuilder()
        .withId(10L)
        .withToRef(pullRequestRefBuilder().withProjectKey("theProject").withRepositoryName("theRepoName"))
        .withPullRequestAction(OPENED)
        .withAuthor(
          prnfsParticipantBuilder().withDisplayName("authorDisplayName").withEmail("authorEmail").withId(100)
            .withName("authorName").withSlug("authorSlug").build()).build())
    .invokedUrl(
      0,
      "http://bjurr.se/id=10&action=OPENED&displayName=authorDisplayName&authorEmail=authorEmail&authorId=100&authorName=authorName&authorSlug=authorSlug&pullRequestUrl=http://stash.server/projects/theProject/repos/theRepoName/pull-requests/10");
 }

 @Test
 public void testThatPostContentIsNotSentIfMethodIsNotSet() {
  prnfsTestBuilder()
    .isLoggedInAsAdmin()
    .withNotification(
      notificationBuilder().withFieldValue(AdminFormValues.FIELDS.url, "http://bjurr.se/")
        .withFieldValue(AdminFormValues.FIELDS.post_content, "should not be sent") //
        .withFieldValue(AdminFormValues.FIELDS.events, OPENED.name()) //
        .build()).store().trigger(pullRequestEventBuilder().withPullRequestAction(OPENED).build())
    .invokedUrl(0, "http://bjurr.se/").invokedMethod("GET").didNotSendPostContentAt(0);
 }

 @Test
 public void testThatPostContentIsNotSentIfMethodIsPOSTButThereIsNotPostContent() {
  prnfsTestBuilder()
    .isLoggedInAsAdmin()
    .withNotification(
      notificationBuilder().withFieldValue(AdminFormValues.FIELDS.url, "http://bjurr.se/")
        .withFieldValue(AdminFormValues.FIELDS.events, OPENED.name()) //
        .withFieldValue(AdminFormValues.FIELDS.post_content, " ") //
        .withFieldValue(AdminFormValues.FIELDS.method, "POST") //
        .build()).store().trigger(pullRequestEventBuilder().withPullRequestAction(OPENED).build())
    .invokedUrl(0, "http://bjurr.se/").invokedMethod("POST").didNotSendPostContentAt(0);
 }

 @Test
 public void testThatPostContentIsNotSentIfMethodIsGETAndThereIsPostContent() {
  prnfsTestBuilder()
    .isLoggedInAsAdmin()
    .withNotification(
      notificationBuilder().withFieldValue(AdminFormValues.FIELDS.url, "http://bjurr.se/")
        .withFieldValue(AdminFormValues.FIELDS.events, OPENED.name()) //
        .withFieldValue(AdminFormValues.FIELDS.post_content, "some content") //
        .withFieldValue(AdminFormValues.FIELDS.method, "GET") //
        .build()).store().trigger(pullRequestEventBuilder().withPullRequestAction(OPENED).build())
    .invokedUrl(0, "http://bjurr.se/").invokedMethod("GET").didNotSendPostContentAt(0);
 }

 @Test
 public void testThatPostContentIsNotSentIfMethodIsDELETEAndThereIsPostContent() {
  prnfsTestBuilder()
    .isLoggedInAsAdmin()
    .withNotification(
      notificationBuilder().withFieldValue(AdminFormValues.FIELDS.url, "http://bjurr.se/")
        .withFieldValue(AdminFormValues.FIELDS.events, OPENED.name()) //
        .withFieldValue(AdminFormValues.FIELDS.post_content, "some content") //
        .withFieldValue(AdminFormValues.FIELDS.method, "DELETE") //
        .build()).store().trigger(pullRequestEventBuilder().withPullRequestAction(OPENED).build())
    .invokedUrl(0, "http://bjurr.se/").invokedMethod("DELETE").didNotSendPostContentAt(0);
 }

 @Test
 public void testThatPostContentIsSentIfMethodIsPOSTAndThereIsPostContent() {
  prnfsTestBuilder()
    .isLoggedInAsAdmin()
    .withNotification(
      notificationBuilder().withFieldValue(AdminFormValues.FIELDS.url, "http://bjurr.se/")
        .withFieldValue(AdminFormValues.FIELDS.events, OPENED.name()) //
        .withFieldValue(AdminFormValues.FIELDS.post_content, "some content") //
        .withFieldValue(AdminFormValues.FIELDS.method, "POST") //
        .build()).store().trigger(pullRequestEventBuilder().withPullRequestAction(OPENED).build())
    .invokedUrl(0, "http://bjurr.se/").didSendPostContentAt(0, "some content");
 }

 @Test
 public void testThatPostContentIsSentIfMethodIsPUTAndThereIsPostContent() {
  prnfsTestBuilder()
    .isLoggedInAsAdmin()
    .withNotification(
      notificationBuilder().withFieldValue(AdminFormValues.FIELDS.url, "http://bjurr.se/")
        .withFieldValue(AdminFormValues.FIELDS.events, OPENED.name()) //
        .withFieldValue(AdminFormValues.FIELDS.post_content, "some content") //
        .withFieldValue(AdminFormValues.FIELDS.method, "PUT") //
        .build()).store().trigger(pullRequestEventBuilder().withPullRequestAction(OPENED).build())
    .invokedUrl(0, "http://bjurr.se/").didSendPostContentAt(0, "some content");
 }

 @Test
 public void testThatPostContentIsSentAndRenderedIfMethodIsPOSTAndThereIsPostContent() {
  prnfsTestBuilder()
    .isLoggedInAsAdmin()
    .withNotification(
      notificationBuilder()
        .withFieldValue(AdminFormValues.FIELDS.url, "http://bjurr.se/")
        .withFieldValue(AdminFormValues.FIELDS.events, OPENED.name())
        .withFieldValue(AdminFormValues.FIELDS.post_content,
          "some ${" + PrnfsVariable.PULL_REQUEST_ACTION.name() + "} content") //
        .withFieldValue(AdminFormValues.FIELDS.method, "POST") //
        .build()).store().trigger(pullRequestEventBuilder().withPullRequestAction(OPENED).build())
    .invokedUrl(0, "http://bjurr.se/").didSendPostContentAt(0, "some OPENED content");
 }

 @Test
 public void testThatCustomHeaderCanBeSent() {
  prnfsTestBuilder()
    .isLoggedInAsAdmin()
    .withNotification(
      notificationBuilder().withFieldValue(AdminFormValues.FIELDS.url, "http://bjurr.se/")
        .withFieldValue(AdminFormValues.FIELDS.events, OPENED.name())
        .withFieldValue(AdminFormValues.FIELDS.header_name, "CustomHeader")
        .withFieldValue(AdminFormValues.FIELDS.header_value, "custom value").build()).store()
    .trigger(pullRequestEventBuilder().withPullRequestAction(OPENED).build()).invokedUrl(0, "http://bjurr.se/")
    .usedHeader(0, "CustomHeader", "custom value");
 }

 @Test
 public void testThatCustomHeaderCanBeSentWithVariables() {
  prnfsTestBuilder()
    .isLoggedInAsAdmin()
    .withNotification(
      notificationBuilder()
        .withFieldValue(AdminFormValues.FIELDS.url, "http://bjurr.se/")
        .withFieldValue(AdminFormValues.FIELDS.events, OPENED.name())
        .withFieldValue(AdminFormValues.FIELDS.header_name, "CustomHeader")
        .withFieldValue(AdminFormValues.FIELDS.header_value,
          "custom ${" + PrnfsRenderer.PrnfsVariable.PULL_REQUEST_ACTION.name() + "} value").build()).store()
    .trigger(pullRequestEventBuilder().withPullRequestAction(OPENED).build()).invokedUrl(0, "http://bjurr.se/")
    .usedHeader(0, "CustomHeader", "custom OPENED value");
 }

 @Test
 public void testThatEmptyHeaderNameIsIgnored() {
  prnfsTestBuilder()
    .isLoggedInAsAdmin()
    .withNotification(
      notificationBuilder().withFieldValue(AdminFormValues.FIELDS.url, "http://bjurr.se/")
        .withFieldValue(AdminFormValues.FIELDS.events, OPENED.name())
        .withFieldValue(AdminFormValues.FIELDS.header_name, " ")
        .withFieldValue(AdminFormValues.FIELDS.header_value, "custom value").build()).store()
    .trigger(pullRequestEventBuilder().withPullRequestAction(OPENED).build()).invokedUrl(0, "http://bjurr.se/")
    .didNotSendHeaders();
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
    .trigger(pullRequestEventBuilder().withPullRequestAction(OPENED).build()).invokedUrl(0, "http://bjurr.se/")
    .usedHeader(0, AUTHORIZATION, "Basic dGhldXNlcjp0aGVwYXNzd29yZA==");
 }

 @Test
 public void testThatBasicAuthenticationHeaderIsSentAlongWithCustomHeaders() {
  prnfsTestBuilder()
    .isLoggedInAsAdmin()
    .withNotification(
      notificationBuilder().withFieldValue(AdminFormValues.FIELDS.url, "http://bjurr.se/")
        .withFieldValue(AdminFormValues.FIELDS.events, OPENED.name())
        .withFieldValue(AdminFormValues.FIELDS.user, "theuser")
        .withFieldValue(AdminFormValues.FIELDS.password, "thepassword")
        .withFieldValue(AdminFormValues.FIELDS.header_name, "CustomHeader1")
        .withFieldValue(AdminFormValues.FIELDS.header_value, "custom value1")
        .withFieldValue(AdminFormValues.FIELDS.header_name, "CustomHeader2")
        .withFieldValue(AdminFormValues.FIELDS.header_value, "theuser:thepassword").build()).store()
    .trigger(pullRequestEventBuilder().withPullRequestAction(OPENED).build()).invokedUrl(0, "http://bjurr.se/")
    .usedHeader(0, AUTHORIZATION, "Basic dGhldXNlcjp0aGVwYXNzd29yZA==").usedHeader(0, "CustomHeader1", "custom value1")
    .usedHeader(0, "CustomHeader2", "theuser:thepassword");
 }

 @Test
 public void testThatBasicAuthenticationHeaderIsNotSentIfThereIsNoUser() {
  prnfsTestBuilder()
    .isLoggedInAsAdmin()
    .withNotification(
      notificationBuilder().withFieldValue(AdminFormValues.FIELDS.url, "http://bjurr.se/")
        .withFieldValue(AdminFormValues.FIELDS.events, OPENED.name()).withFieldValue(AdminFormValues.FIELDS.user, "")
        .withFieldValue(AdminFormValues.FIELDS.password, "thepassword").build()).store()
    .trigger(pullRequestEventBuilder().withPullRequestAction(OPENED).build()).invokedUrl(0, "http://bjurr.se/")
    .didNotSendHeaders();
 }

 @Test
 public void testThatBasicAuthenticationHeaderIsNotSentIfThereIsNoPassword() {
  prnfsTestBuilder()
    .isLoggedInAsAdmin()
    .withNotification(
      notificationBuilder().withFieldValue(AdminFormValues.FIELDS.url, "http://bjurr.se/")
        .withFieldValue(AdminFormValues.FIELDS.events, OPENED.name())
        .withFieldValue(AdminFormValues.FIELDS.user, "theuser").withFieldValue(AdminFormValues.FIELDS.password, "")
        .build()).store().trigger(pullRequestEventBuilder().withPullRequestAction(OPENED).build())
    .invokedUrl(0, "http://bjurr.se/").didNotSendHeaders();
 }

 @Test
 public void testThatBasicAuthenticationHeaderIsNotSentIfTheUserContainsOnlySpace() {
  prnfsTestBuilder()
    .isLoggedInAsAdmin()
    .withNotification(
      notificationBuilder().withFieldValue(AdminFormValues.FIELDS.url, "http://bjurr.se/")
        .withFieldValue(AdminFormValues.FIELDS.events, OPENED.name()).withFieldValue(AdminFormValues.FIELDS.user, " ")
        .withFieldValue(AdminFormValues.FIELDS.password, "thepassword").build()).store()
    .trigger(pullRequestEventBuilder().withPullRequestAction(OPENED).build()).invokedUrl(0, "http://bjurr.se/")
    .didNotSendHeaders();
 }

 @Test
 public void testThatBasicAuthenticationHeaderIsNotSentIfThePasswordContainsOnlySpace() {
  prnfsTestBuilder()
    .isLoggedInAsAdmin()
    .withNotification(
      notificationBuilder().withFieldValue(AdminFormValues.FIELDS.url, "http://bjurr.se/")
        .withFieldValue(AdminFormValues.FIELDS.events, OPENED.name())
        .withFieldValue(AdminFormValues.FIELDS.user, "theuser").withFieldValue(AdminFormValues.FIELDS.password, " ")
        .build()).store().trigger(pullRequestEventBuilder().withPullRequestAction(OPENED).build())
    .invokedUrl(0, "http://bjurr.se/").didNotSendHeaders();
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
        .withFieldValue(FIELDS.filter_regexp, "EXP").build())
    .store()
    .trigger(
      pullRequestEventBuilder().withFromRef(pullRequestRefBuilder().withProjectKey("ABC")).withId(10L)
        .withPullRequestAction(OPENED).build()).invokedNoUrl();
 }

 @Test
 public void testThatFilterCanIncludeRescopedFrom() {
  prnfsTestBuilder()
    .isLoggedInAsAdmin()
    .withNotification(
      notificationBuilder().withFieldValue(AdminFormValues.FIELDS.url, "http://bjurr.se/")
        .withFieldValue(AdminFormValues.FIELDS.events, RESCOPED_FROM)
        .withFieldValue(FIELDS.filter_string, "${" + PrnfsRenderer.PrnfsVariable.PULL_REQUEST_ACTION.name() + "}")
        .withFieldValue(FIELDS.filter_regexp, RESCOPED_FROM).build())
    .store()
    .trigger(
      pullRequestEventBuilder().withFromRef(pullRequestRefBuilder().withHash("from"))
        .withToRef(pullRequestRefBuilder().withHash(PREVIOUS_TO_HASH)).withId(10L).withPullRequestAction(RESCOPED)
        .build()).invokedUrl(0, "http://bjurr.se/");
 }

 @Test
 public void testThatFilterCanBeUsedWithComments() {
  prnfsTestBuilder()
    .isLoggedInAsAdmin()
    .withNotification(
      notificationBuilder()
        .withFieldValue(
          AdminFormValues.FIELDS.url,
          "http://bjurr.se/?comment=${" + PrnfsRenderer.PrnfsVariable.PULL_REQUEST_COMMENT_TEXT + "}&version=${"
            + PrnfsRenderer.PrnfsVariable.PULL_REQUEST_VERSION + "}")
        .withFieldValue(AdminFormValues.FIELDS.events, COMMENTED.name())
        .withFieldValue(
          FIELDS.filter_string,
          "${" + PrnfsRenderer.PrnfsVariable.PULL_REQUEST_TO_ID.name() + "}:${"
            + PrnfsRenderer.PrnfsVariable.PULL_REQUEST_COMMENT_TEXT.name() + "}:")
        .withFieldValue(FIELDS.filter_regexp, ".*:.*?keyword.*?:.*").build())
    .store()
    .trigger(
      pullRequestEventBuilder().withFromRef(pullRequestRefBuilder().withHash("from"))
        .withToRef(pullRequestRefBuilder().withId("123")).withCommentText("keyword A nice comment").withId(10L)
        .withPullRequestAction(COMMENTED).build())
    .invokedUrl(0, "http://bjurr.se/?comment=keyword%20A%20nice%20comment&version=0");
 }

 @Test
 public void testThatFilterCanBeUsedWithCommentsAndSpecialEscapedChars() {
  prnfsTestBuilder()
    .isLoggedInAsAdmin()
    .withNotification(
      notificationBuilder()
        .withFieldValue(
          AdminFormValues.FIELDS.url,
          "http://bjurr.se/?comment=${" + PrnfsRenderer.PrnfsVariable.PULL_REQUEST_COMMENT_TEXT + "}&version=${"
            + PrnfsRenderer.PrnfsVariable.PULL_REQUEST_VERSION + "}")
        .withFieldValue(AdminFormValues.FIELDS.events, COMMENTED.name())
        .withFieldValue(
          FIELDS.filter_string,
          "${" + PrnfsRenderer.PrnfsVariable.PULL_REQUEST_TO_ID.name() + "}:${"
            + PrnfsRenderer.PrnfsVariable.PULL_REQUEST_COMMENT_TEXT.name() + "}:")
        .withFieldValue(FIELDS.filter_regexp, ".*:\\skeyword\\s:.*").build())
    .store()
    .trigger(
      pullRequestEventBuilder().withFromRef(pullRequestRefBuilder().withHash("from"))
        .withToRef(pullRequestRefBuilder().withId("123")).withCommentText(" keyword ").withId(10L)
        .withPullRequestAction(COMMENTED).build()).invokedUrl(0, "http://bjurr.se/?comment=%20keyword%20&version=0");
 }

 @Test
 public void testThatFilterCanBeUsedWithCommentsIgnore() {
  prnfsTestBuilder()
    .isLoggedInAsAdmin()
    .withNotification(
      notificationBuilder()
        .withFieldValue(
          AdminFormValues.FIELDS.url,
          "http://bjurr.se/?comment=${" + PrnfsRenderer.PrnfsVariable.PULL_REQUEST_COMMENT_TEXT + "}&version=${"
            + PrnfsRenderer.PrnfsVariable.PULL_REQUEST_VERSION + "}")
        .withFieldValue(AdminFormValues.FIELDS.events, COMMENTED.name())
        .withFieldValue(
          FIELDS.filter_string,
          "${" + PrnfsRenderer.PrnfsVariable.PULL_REQUEST_TO_ID.name() + "}:${"
            + PrnfsRenderer.PrnfsVariable.PULL_REQUEST_COMMENT_TEXT.name() + "}:")
        .withFieldValue(FIELDS.filter_regexp, ".*:.*?keyword.*?:.*").build())
    .store()
    .trigger(
      pullRequestEventBuilder().withFromRef(pullRequestRefBuilder().withHash("from"))
        .withToRef(pullRequestRefBuilder().withId("123")).withCommentText("notkw A nice comment").withId(10L)
        .withPullRequestAction(COMMENTED).build()).invokedNoUrl();
 }

 @Test
 public void testThatStringWithVariableCommentIsEmptyIfNotACommentEvent() {
  prnfsTestBuilder()
    .isLoggedInAsAdmin()
    .withNotification(
      notificationBuilder()
        .withFieldValue(AdminFormValues.FIELDS.url,
          "http://bjurr.se/?comment=${" + PrnfsRenderer.PrnfsVariable.PULL_REQUEST_COMMENT_TEXT + "}")
        .withFieldValue(AdminFormValues.FIELDS.events, OPENED.name()).build()).store()
    .trigger(pullRequestEventBuilder() //
      .withId(10L).withPullRequestAction(OPENED).build()).invokedUrl(0, "http://bjurr.se/?comment=");
 }

 @Test
 public void testThatURLCanIncludeRescopedFrom() {
  prnfsTestBuilder()
    .isLoggedInAsAdmin()
    .withNotification(
      notificationBuilder()
        .withFieldValue(AdminFormValues.FIELDS.url,
          "http://bjurr.se/${" + PrnfsRenderer.PrnfsVariable.PULL_REQUEST_ACTION.name() + "}")
        .withFieldValue(AdminFormValues.FIELDS.events, RESCOPED_FROM).build())
    .store()
    .trigger(
      pullRequestEventBuilder().withFromRef(pullRequestRefBuilder().withHash("from"))
        .withToRef(pullRequestRefBuilder().withHash(PREVIOUS_TO_HASH)) //
        .withPullRequestAction(RESCOPED).build()).invokedUrl(0, "http://bjurr.se/RESCOPED_FROM");
 }

 @Test
 public void testThatURLCanIncludeRescopedTo() {
  prnfsTestBuilder()
    .isLoggedInAsAdmin()
    .withNotification(
      notificationBuilder()
        .withFieldValue(AdminFormValues.FIELDS.url,
          "http://bjurr.se/${" + PrnfsRenderer.PrnfsVariable.PULL_REQUEST_ACTION.name() + "}")
        .withFieldValue(AdminFormValues.FIELDS.events, RESCOPED_TO).build())
    .store()
    .trigger(
      pullRequestEventBuilder().withFromRef(pullRequestRefBuilder().withHash(PREVIOUS_FROM_HASH))
        .withToRef(pullRequestRefBuilder().withHash("toHash")).withPullRequestAction(RESCOPED).build())
    .invokedOnlyUrl("http://bjurr.se/RESCOPED_TO");
 }

 @Test
 public void testThatURLCanIncludeRescopedFromWhenBothFromAndToChanges() {
  prnfsTestBuilder()
    .isLoggedInAsAdmin()
    .withNotification(
      notificationBuilder()
        .withFieldValue(AdminFormValues.FIELDS.url,
          "http://bjurr.se/${" + PrnfsRenderer.PrnfsVariable.PULL_REQUEST_ACTION.name() + "}")
        .withFieldValue(AdminFormValues.FIELDS.events, RESCOPED_FROM).build())
    .store()
    .trigger(
      pullRequestEventBuilder().withFromRef(pullRequestRefBuilder().withHash("fromHash"))
        .withToRef(pullRequestRefBuilder().withHash("toHash")).withPullRequestAction(RESCOPED).build())
    .invokedOnlyUrl("http://bjurr.se/RESCOPED_FROM");
 }

 @Test
 public void testThatAllURLsMatchingEventsAreTriggered() {
  prnfsTestBuilder()
    .isLoggedInAsAdmin()
    .withNotification(
      notificationBuilder()
        .withFieldValue(AdminFormValues.FIELDS.url,
          "http://bjurr.se/${" + PrnfsRenderer.PrnfsVariable.PULL_REQUEST_ACTION.name() + "}")
        .withFieldValue(AdminFormValues.FIELDS.events, RESCOPED_FROM).build())
    .store()
    .withNotification(
      notificationBuilder()
        .withFieldValue(AdminFormValues.FIELDS.url,
          "http://bjurr.se/${" + PrnfsRenderer.PrnfsVariable.PULL_REQUEST_ACTION.name() + "}")
        .withFieldValue(AdminFormValues.FIELDS.events, RESCOPED_TO).build())
    .store()
    .withNotification(
      notificationBuilder()
        .withFieldValue(AdminFormValues.FIELDS.url,
          "http://bjurr.se/${" + PrnfsRenderer.PrnfsVariable.PULL_REQUEST_ACTION.name() + "}")
        .withFieldValue(AdminFormValues.FIELDS.events, RESCOPED_FROM)
        .withFieldValue(AdminFormValues.FIELDS.events, RESCOPED_TO).build())
    .store()
    .trigger(
      pullRequestEventBuilder().withFromRef(pullRequestRefBuilder().withHash("fromHash"))
        .withToRef(pullRequestRefBuilder().withHash("toHash")) //
        .withPullRequestAction(RESCOPED).build()).invokedUrl(0, "http://bjurr.se/RESCOPED_FROM")
    .invokedUrl(1, "http://bjurr.se/RESCOPED_TO").invokedUrl(2, "http://bjurr.se/RESCOPED_FROM");
 }

 @Test
 public void testThatURLCanIncludeRescopedFromWhenBothFromAndToChangesAndBothFromAndToAreConfigured() {
  prnfsTestBuilder()
    .isLoggedInAsAdmin()
    .withNotification(
      notificationBuilder()
        .withFieldValue(AdminFormValues.FIELDS.url,
          "http://bjurr.se/${" + PrnfsRenderer.PrnfsVariable.PULL_REQUEST_ACTION.name() + "}")
        .withFieldValue(AdminFormValues.FIELDS.events, RESCOPED_FROM)
        .withFieldValue(AdminFormValues.FIELDS.events, RESCOPED_TO).build())
    .store()
    .trigger(
      pullRequestEventBuilder().withFromRef(pullRequestRefBuilder().withHash("fromHash"))
        .withToRef(pullRequestRefBuilder().withHash("toHash")).withPullRequestAction(RESCOPED).build())
    .invokedUrl(0, "http://bjurr.se/RESCOPED_FROM");
 }

 @Test
 public void testThatURLCanIncludeRescopedToWhenBothFromAndToChanges() {
  prnfsTestBuilder()
    .isLoggedInAsAdmin()
    .withNotification(
      notificationBuilder()
        .withFieldValue(AdminFormValues.FIELDS.url,
          "http://bjurr.se/${" + PrnfsRenderer.PrnfsVariable.PULL_REQUEST_ACTION.name() + "}")
        .withFieldValue(AdminFormValues.FIELDS.events, RESCOPED_TO).build())
    .store()
    .trigger(
      pullRequestEventBuilder().withFromRef(pullRequestRefBuilder().withHash("fromHash"))
        .withToRef(pullRequestRefBuilder().withHash("toHash")).withPullRequestAction(RESCOPED).build())
    .invokedUrl(0, "http://bjurr.se/RESCOPED_TO");
 }

 @Test
 public void testThatFilterCanBeUsedToIgnoreEventsThatAreOnAnotherProjectAnBranch() {
  prnfsTestBuilder()
    .isLoggedInAsAdmin()
    .withNotification(
      notificationBuilder().withFieldValue(AdminFormValues.FIELDS.url, "http://bjurr.se/")
        .withFieldValue(AdminFormValues.FIELDS.events, OPENED.name())
        .withFieldValue(FIELDS.filter_string, "${PULL_REQUEST_FROM_REPO_PROJECT_KEY} ${PULL_REQUEST_FROM_ID}")
        .withFieldValue(FIELDS.filter_regexp, "EXP my_branch").build())
    .store()
    .trigger(
      pullRequestEventBuilder().withFromRef(pullRequestRefBuilder().withProjectKey("ABC").withId("my_therbranch"))
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
        .withFieldValue(FIELDS.filter_regexp, "EXP").build())
    .store()
    .trigger(
      pullRequestEventBuilder().withFromRef(pullRequestRefBuilder().withProjectKey("EXP")).withId(10L)
        .withPullRequestAction(OPENED).build()).invokedUrl(0, "http://bjurr.se/");
 }

 @Test
 public void testThatFilterCanBeUsedToTriggerOnEventsThatAreOnAnotherProjectAnBranch() {
  prnfsTestBuilder()
    .isLoggedInAsAdmin()
    .withNotification(
      notificationBuilder().withFieldValue(AdminFormValues.FIELDS.url, "http://bjurr.se/")
        .withFieldValue(AdminFormValues.FIELDS.events, OPENED.name())
        .withFieldValue(FIELDS.filter_string, "${PULL_REQUEST_FROM_REPO_PROJECT_KEY} ${PULL_REQUEST_FROM_ID}")
        .withFieldValue(FIELDS.filter_regexp, "EXP my_branch").build())
    .store()
    .trigger(
      pullRequestEventBuilder().withFromRef(pullRequestRefBuilder().withProjectKey("EXP").withId("my_branch"))
        .withId(10L).withPullRequestAction(OPENED).build()).invokedUrl(0, "http://bjurr.se/");
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
      .withId(10L).withPullRequestAction(MERGED).build()).invokedOnlyUrl("http://merged.se/").didNotSendHeaders();
 }

 @Test
 public void testThatProxyCanBeUsedWhenInvokingUrl() {
  prnfsTestBuilder()
    .isLoggedInAsAdmin()
    .withNotification(
      notificationBuilder().withFieldValue(AdminFormValues.FIELDS.url, "http://bjurr.se/")
        .withFieldValue(AdminFormValues.FIELDS.events, OPENED.name())
        .withFieldValue(AdminFormValues.FIELDS.proxy_server, "proxyhost")
        .withFieldValue(AdminFormValues.FIELDS.proxy_port, " 1234 ").build()).store()
    .trigger(pullRequestEventBuilder().withPullRequestAction(OPENED).build()).invokedUrl(0, "http://bjurr.se/")
    .usedNoProxyUser(0).usedNoProxyPassword(0).usedProxyHost(0, "proxyhost").usedProxyPort(0, 1234);
 }

 @Test
 public void testThatProxyPortIsNeeded() {
  prnfsTestBuilder()
    .isLoggedInAsAdmin()
    .withNotification(
      notificationBuilder().withFieldValue(AdminFormValues.FIELDS.url, "http://bjurr.se/")
        .withFieldValue(AdminFormValues.FIELDS.events, OPENED.name())
        .withFieldValue(AdminFormValues.FIELDS.proxy_server, "proxyhost")
        .withFieldValue(AdminFormValues.FIELDS.proxy_port, " ").build()).store()
    .trigger(pullRequestEventBuilder().withPullRequestAction(OPENED).build()).invokedUrl(0, "http://bjurr.se/")
    .usedNoProxy(0);
 }

 @Test
 public void testThatProxyHostIsNeeded() {
  prnfsTestBuilder()
    .isLoggedInAsAdmin()
    .withNotification(
      notificationBuilder().withFieldValue(AdminFormValues.FIELDS.url, "http://bjurr.se/")
        .withFieldValue(AdminFormValues.FIELDS.events, OPENED.name())
        .withFieldValue(AdminFormValues.FIELDS.proxy_server, "")
        .withFieldValue(AdminFormValues.FIELDS.proxy_port, "123").build()).store()
    .trigger(pullRequestEventBuilder().withPullRequestAction(OPENED).build()).invokedUrl(0, "http://bjurr.se/")
    .usedNoProxy(0);
 }

 @Test
 public void testThatProxyCanUseUserAndPassword() {
  prnfsTestBuilder()
    .isLoggedInAsAdmin()
    .withNotification(
      notificationBuilder().withFieldValue(AdminFormValues.FIELDS.url, "http://bjurr.se/")
        .withFieldValue(AdminFormValues.FIELDS.events, OPENED.name())
        .withFieldValue(AdminFormValues.FIELDS.proxy_server, "proxyhost")
        .withFieldValue(AdminFormValues.FIELDS.proxy_port, "123")
        .withFieldValue(AdminFormValues.FIELDS.proxy_user, "proxyuser")
        .withFieldValue(AdminFormValues.FIELDS.proxy_password, "proxypassword").build()).store()
    .trigger(pullRequestEventBuilder().withPullRequestAction(OPENED).build()).invokedUrl(0, "http://bjurr.se/")
    .usedProxyUser(0, "proxyuser").usedProxyPassword(0, "proxypassword");
 }

 @Test
 public void testThatProxyDoesNotAuthenticateIfNoUser() {
  prnfsTestBuilder()
    .isLoggedInAsAdmin()
    .withNotification(
      notificationBuilder().withFieldValue(AdminFormValues.FIELDS.url, "http://bjurr.se/")
        .withFieldValue(AdminFormValues.FIELDS.events, OPENED.name())
        .withFieldValue(AdminFormValues.FIELDS.proxy_server, "proxyhost")
        .withFieldValue(AdminFormValues.FIELDS.proxy_port, "123")
        .withFieldValue(AdminFormValues.FIELDS.proxy_user, " ")
        .withFieldValue(AdminFormValues.FIELDS.proxy_password, "proxypassword").build()).store()
    .trigger(pullRequestEventBuilder().withPullRequestAction(OPENED).build()).invokedUrl(0, "http://bjurr.se/")
    .usedNoProxyAuthentication(0);
 }

 @Test
 public void testThatProxyDoesNotAuthenticateIfNoPassword() {
  prnfsTestBuilder()
    .isLoggedInAsAdmin()
    .withNotification(
      notificationBuilder().withFieldValue(AdminFormValues.FIELDS.url, "http://bjurr.se/")
        .withFieldValue(AdminFormValues.FIELDS.events, OPENED.name())
        .withFieldValue(AdminFormValues.FIELDS.proxy_server, "proxyhost")
        .withFieldValue(AdminFormValues.FIELDS.proxy_port, "123")
        .withFieldValue(AdminFormValues.FIELDS.proxy_user, "user")
        .withFieldValue(AdminFormValues.FIELDS.proxy_password, " ").build()).store()
    .trigger(pullRequestEventBuilder().withPullRequestAction(OPENED).build()).invokedUrl(0, "http://bjurr.se/")
    .usedNoProxyAuthentication(0);
 }

 @Test
 public void testThatButtonCanBeUsedForTriggeringEvent() throws Exception {
  prnfsTestBuilder()
    .isLoggedInAsAdmin()
    .withNotification(
      notificationBuilder()
        .withFieldValue(AdminFormValues.FIELDS.url, "http://bjurr.se/${" + BUTTON_TRIGGER_TITLE + "}")
        .withFieldValue(AdminFormValues.FIELDS.events, BUTTON_TRIGGER)
        .withFieldValue(AdminFormValues.FIELDS.FORM_TYPE, AdminFormValues.FORM_TYPE.TRIGGER_CONFIG_FORM.name()).build())
    .store()
    .withNotification(
      notificationBuilder().withFieldValue(AdminFormValues.FIELDS.FORM_IDENTIFIER, "Button Form")
        .withFieldValue(AdminFormValues.FIELDS.FORM_TYPE, AdminFormValues.FORM_TYPE.BUTTON_CONFIG_FORM.name())
        .withFieldValue(AdminFormValues.FIELDS.button_title, "Trigger notification")
        .withFieldValue(AdminFormValues.FIELDS.button_visibility, AdminFormValues.BUTTON_VISIBILITY.EVERYONE.name())
        .build()).store().triggerButton("Button Form").invokedOnlyUrl("http://bjurr.se/Trigger%20notification");
 }

 @Test
 public void testThatEventTriggeredByButtonCanBeFiltered() throws Exception {
  prnfsTestBuilder()
    .isLoggedInAsAdmin()
    .withNotification(
      notificationBuilder()
        .withFieldValue(AdminFormValues.FIELDS.url, "http://bjurr.se/?123=${" + BUTTON_TRIGGER_TITLE + "}")
        .withFieldValue(AdminFormValues.FIELDS.events, BUTTON_TRIGGER)
        .withFieldValue(AdminFormValues.FIELDS.filter_string, "${" + BUTTON_TRIGGER_TITLE + "}")
        .withFieldValue(AdminFormValues.FIELDS.filter_regexp, "123")
        .withFieldValue(AdminFormValues.FIELDS.FORM_TYPE, AdminFormValues.FORM_TYPE.TRIGGER_CONFIG_FORM.name()).build())
    .store()
    .withNotification(
      notificationBuilder().withFieldValue(AdminFormValues.FIELDS.FORM_IDENTIFIER, "Button Form")
        .withFieldValue(AdminFormValues.FIELDS.FORM_TYPE, AdminFormValues.FORM_TYPE.BUTTON_CONFIG_FORM.name())
        .withFieldValue(AdminFormValues.FIELDS.button_title, "button text 123")
        .withFieldValue(AdminFormValues.FIELDS.button_visibility, AdminFormValues.BUTTON_VISIBILITY.EVERYONE.name())
        .build()).store().triggerButton("Button Form").invokedOnlyUrl("http://bjurr.se/?123=button%20text%20123");
 }

 @Test
 public void testThatEventTriggeredByButtonCanBeIgnoredByFilterWhileAnotherIsNotIgnored() throws Exception {
  prnfsTestBuilder()
    .isLoggedInAsAdmin()
    .withNotification(
      notificationBuilder()
        .withFieldValue(AdminFormValues.FIELDS.url, "http://bjurr.se/?123=${" + BUTTON_TRIGGER_TITLE + "}")
        .withFieldValue(AdminFormValues.FIELDS.events, BUTTON_TRIGGER)
        .withFieldValue(AdminFormValues.FIELDS.filter_string, "${" + BUTTON_TRIGGER_TITLE + "}")
        .withFieldValue(AdminFormValues.FIELDS.filter_regexp, "123")
        .withFieldValue(AdminFormValues.FIELDS.FORM_TYPE, AdminFormValues.FORM_TYPE.TRIGGER_CONFIG_FORM.name()).build())
    .store()
    .withNotification(
      notificationBuilder()
        .withFieldValue(AdminFormValues.FIELDS.url, "http://bjurr.se/?456=${" + BUTTON_TRIGGER_TITLE + "}")
        .withFieldValue(AdminFormValues.FIELDS.events, BUTTON_TRIGGER)
        .withFieldValue(AdminFormValues.FIELDS.filter_string, "${" + BUTTON_TRIGGER_TITLE + "}")
        .withFieldValue(AdminFormValues.FIELDS.filter_regexp, "456")
        .withFieldValue(AdminFormValues.FIELDS.FORM_TYPE, AdminFormValues.FORM_TYPE.TRIGGER_CONFIG_FORM.name()).build())
    .store()
    .withNotification(
      notificationBuilder().withFieldValue(AdminFormValues.FIELDS.FORM_IDENTIFIER, "Button Form")
        .withFieldValue(AdminFormValues.FIELDS.FORM_TYPE, AdminFormValues.FORM_TYPE.BUTTON_CONFIG_FORM.name())
        .withFieldValue(AdminFormValues.FIELDS.button_title, "button text 123")
        .withFieldValue(AdminFormValues.FIELDS.button_visibility, AdminFormValues.BUTTON_VISIBILITY.EVERYONE.name())
        .build()).store().triggerButton("Button Form").invokedOnlyUrl("http://bjurr.se/?123=button%20text%20123");
 }

 @Test
 public void testThatEventTriggeredByButtonCanResultInSeveralNotifications() throws Exception {
  prnfsTestBuilder()
    .isLoggedInAsAdmin()
    .withNotification(
      notificationBuilder()
        .withFieldValue(AdminFormValues.FIELDS.url, "http://bjurr.se/?123=${" + BUTTON_TRIGGER_TITLE + "}")
        .withFieldValue(AdminFormValues.FIELDS.events, BUTTON_TRIGGER)
        .withFieldValue(AdminFormValues.FIELDS.filter_string, "${" + BUTTON_TRIGGER_TITLE + "}")
        .withFieldValue(AdminFormValues.FIELDS.filter_regexp, "button")
        .withFieldValue(AdminFormValues.FIELDS.FORM_TYPE, AdminFormValues.FORM_TYPE.TRIGGER_CONFIG_FORM.name()).build())
    .store()
    .withNotification(
      notificationBuilder()
        .withFieldValue(AdminFormValues.FIELDS.url, "http://bjurr.se/?456=${" + BUTTON_TRIGGER_TITLE + "}")
        .withFieldValue(AdminFormValues.FIELDS.events, BUTTON_TRIGGER)
        .withFieldValue(AdminFormValues.FIELDS.filter_string, "${" + BUTTON_TRIGGER_TITLE + "}")
        .withFieldValue(AdminFormValues.FIELDS.filter_regexp, "button")
        .withFieldValue(AdminFormValues.FIELDS.FORM_TYPE, AdminFormValues.FORM_TYPE.TRIGGER_CONFIG_FORM.name()).build())
    .store()
    .withNotification(
      notificationBuilder().withFieldValue(AdminFormValues.FIELDS.FORM_IDENTIFIER, "Button Form")
        .withFieldValue(AdminFormValues.FIELDS.FORM_TYPE, AdminFormValues.FORM_TYPE.BUTTON_CONFIG_FORM.name())
        .withFieldValue(AdminFormValues.FIELDS.button_title, "button text 123")
        .withFieldValue(AdminFormValues.FIELDS.button_visibility, AdminFormValues.BUTTON_VISIBILITY.EVERYONE.name())
        .build()).store().triggerButton("Button Form").invokedUrl(0, "http://bjurr.se/?123=button%20text%20123")
    .invokedUrl(1, "http://bjurr.se/?456=button%20text%20123");
 }

 @Test
 public void testThatThereCanBeSeveralButtons() throws Exception {
  prnfsTestBuilder()
    .isLoggedInAsAdmin()
    .withNotification(
      notificationBuilder()
        .withFieldValue(AdminFormValues.FIELDS.url, "http://bjurr.se/?${" + BUTTON_TRIGGER_TITLE + "}")
        .withFieldValue(AdminFormValues.FIELDS.events, BUTTON_TRIGGER)
        .withFieldValue(AdminFormValues.FIELDS.FORM_TYPE, AdminFormValues.FORM_TYPE.TRIGGER_CONFIG_FORM.name()).build())
    .store()
    .withNotification(
      notificationBuilder().withFieldValue(AdminFormValues.FIELDS.FORM_IDENTIFIER, "Button Form 1")
        .withFieldValue(AdminFormValues.FIELDS.FORM_TYPE, AdminFormValues.FORM_TYPE.BUTTON_CONFIG_FORM.name())
        .withFieldValue(AdminFormValues.FIELDS.button_title, "button text 1")
        .withFieldValue(AdminFormValues.FIELDS.button_visibility, AdminFormValues.BUTTON_VISIBILITY.EVERYONE.name())
        .build())
    .store()
    .withNotification(
      notificationBuilder().withFieldValue(AdminFormValues.FIELDS.FORM_IDENTIFIER, "Button Form 2")
        .withFieldValue(AdminFormValues.FIELDS.FORM_TYPE, AdminFormValues.FORM_TYPE.BUTTON_CONFIG_FORM.name())
        .withFieldValue(AdminFormValues.FIELDS.button_title, "button text 2")
        .withFieldValue(AdminFormValues.FIELDS.button_visibility, AdminFormValues.BUTTON_VISIBILITY.EVERYONE.name())
        .build()).store().triggerButton("Button Form 1").invokedUrl(0, "http://bjurr.se/?button%20text%201")
    .triggerButton("Button Form 2").invokedUrl(1, "http://bjurr.se/?button%20text%202");
 }

 @Test
 public void testThatProxyMayNotBeUsedWhenInvokingUrl() {
  prnfsTestBuilder()
    .isLoggedInAsAdmin()
    .withNotification(
      notificationBuilder().withFieldValue(AdminFormValues.FIELDS.url, "http://bjurr.se/")
        .withFieldValue(AdminFormValues.FIELDS.events, OPENED.name()).build()).store()
    .trigger(pullRequestEventBuilder().withPullRequestAction(OPENED).build()).invokedUrl(0, "http://bjurr.se/")
    .usedNoProxy(0);
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

 @Test
 public void testThatEventsAreMentionedInAdminGUI() throws IOException {
  final URL resource = getResource("admin.vm");
  final String adminVmContent = Resources.toString(resource, UTF_8);
  for (final PrnfsPullRequestAction prnfsAction : PrnfsPullRequestAction.values()) {
   assertTrue(prnfsAction.getName() + " in " + resource.toString(), adminVmContent.contains(prnfsAction.getName()));
  }
 }
}
