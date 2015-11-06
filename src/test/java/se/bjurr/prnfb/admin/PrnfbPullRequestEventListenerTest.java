package se.bjurr.prnfb.admin;

import static com.atlassian.bitbucket.pull.PullRequestAction.COMMENTED;
import static com.atlassian.bitbucket.pull.PullRequestAction.MERGED;
import static com.atlassian.bitbucket.pull.PullRequestAction.OPENED;
import static com.atlassian.bitbucket.pull.PullRequestAction.RESCOPED;
import static com.atlassian.bitbucket.pull.PullRequestRole.AUTHOR;
import static com.atlassian.bitbucket.pull.PullRequestRole.PARTICIPANT;
import static com.atlassian.bitbucket.pull.PullRequestState.DECLINED;
import static com.google.common.base.Charsets.UTF_8;
import static com.google.common.base.Joiner.on;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.io.Resources.getResource;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.util.Collections.sort;
import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static se.bjurr.prnbb.admin.utils.NotificationBuilder.notificationBuilder;
import static se.bjurr.prnbb.admin.utils.PrnfbParticipantBuilder.prnfbParticipantBuilder;
import static se.bjurr.prnbb.admin.utils.PrnfbTestBuilder.prnfbTestBuilder;
import static se.bjurr.prnbb.admin.utils.PullRequestEventBuilder.PREVIOUS_FROM_HASH;
import static se.bjurr.prnbb.admin.utils.PullRequestEventBuilder.PREVIOUS_TO_HASH;
import static se.bjurr.prnbb.admin.utils.PullRequestEventBuilder.pullRequestEventBuilder;
import static se.bjurr.prnbb.admin.utils.PullRequestRefBuilder.pullRequestRefBuilder;
import static se.bjurr.prnfb.admin.AdminFormValues.BUTTON_VISIBILITY.EVERYONE;
import static se.bjurr.prnfb.admin.AdminFormValues.FIELDS.FORM_IDENTIFIER;
import static se.bjurr.prnfb.admin.AdminFormValues.FIELDS.FORM_TYPE;
import static se.bjurr.prnfb.admin.AdminFormValues.FIELDS.button_title;
import static se.bjurr.prnfb.admin.AdminFormValues.FIELDS.button_visibility;
import static se.bjurr.prnfb.admin.AdminFormValues.FIELDS.events;
import static se.bjurr.prnfb.admin.AdminFormValues.FIELDS.filter_regexp;
import static se.bjurr.prnfb.admin.AdminFormValues.FIELDS.filter_string;
import static se.bjurr.prnfb.admin.AdminFormValues.FIELDS.header_name;
import static se.bjurr.prnfb.admin.AdminFormValues.FIELDS.header_value;
import static se.bjurr.prnfb.admin.AdminFormValues.FIELDS.injection_url;
import static se.bjurr.prnfb.admin.AdminFormValues.FIELDS.injection_url_regexp;
import static se.bjurr.prnfb.admin.AdminFormValues.FIELDS.method;
import static se.bjurr.prnfb.admin.AdminFormValues.FIELDS.password;
import static se.bjurr.prnfb.admin.AdminFormValues.FIELDS.post_content;
import static se.bjurr.prnfb.admin.AdminFormValues.FIELDS.proxy_password;
import static se.bjurr.prnfb.admin.AdminFormValues.FIELDS.proxy_port;
import static se.bjurr.prnfb.admin.AdminFormValues.FIELDS.proxy_server;
import static se.bjurr.prnfb.admin.AdminFormValues.FIELDS.proxy_user;
import static se.bjurr.prnfb.admin.AdminFormValues.FIELDS.trigger_if_isconflicting;
import static se.bjurr.prnfb.admin.AdminFormValues.FIELDS.trigger_ignore_state;
import static se.bjurr.prnfb.admin.AdminFormValues.FIELDS.url;
import static se.bjurr.prnfb.admin.AdminFormValues.FIELDS.user;
import static se.bjurr.prnfb.admin.AdminFormValues.FORM_TYPE.BUTTON_CONFIG_FORM;
import static se.bjurr.prnfb.admin.AdminFormValues.FORM_TYPE.TRIGGER_CONFIG_FORM;
import static se.bjurr.prnfb.admin.AdminFormValues.TRIGGER_IF_MERGE.ALWAYS;
import static se.bjurr.prnfb.admin.AdminFormValues.TRIGGER_IF_MERGE.CONFLICTING;
import static se.bjurr.prnfb.admin.AdminFormValues.TRIGGER_IF_MERGE.NOT_CONFLICTING;
import static se.bjurr.prnfb.listener.PrnfbPullRequestAction.BUTTON_TRIGGER;
import static se.bjurr.prnfb.listener.PrnfbPullRequestAction.RESCOPED_FROM;
import static se.bjurr.prnfb.listener.PrnfbPullRequestAction.RESCOPED_TO;
import static se.bjurr.prnfb.listener.PrnfbRenderer.PrnfbVariable.BUTTON_TRIGGER_TITLE;
import static se.bjurr.prnfb.listener.PrnfbRenderer.PrnfbVariable.INJECTION_URL_VALUE;
import static se.bjurr.prnfb.listener.PrnfbRenderer.PrnfbVariable.PULL_REQUEST_ACTION;
import static se.bjurr.prnfb.listener.PrnfbRenderer.PrnfbVariable.PULL_REQUEST_AUTHOR_DISPLAY_NAME;
import static se.bjurr.prnfb.listener.PrnfbRenderer.PrnfbVariable.PULL_REQUEST_AUTHOR_EMAIL;
import static se.bjurr.prnfb.listener.PrnfbRenderer.PrnfbVariable.PULL_REQUEST_AUTHOR_ID;
import static se.bjurr.prnfb.listener.PrnfbRenderer.PrnfbVariable.PULL_REQUEST_AUTHOR_NAME;
import static se.bjurr.prnfb.listener.PrnfbRenderer.PrnfbVariable.PULL_REQUEST_AUTHOR_SLUG;
import static se.bjurr.prnfb.listener.PrnfbRenderer.PrnfbVariable.PULL_REQUEST_COMMENT_TEXT;
import static se.bjurr.prnfb.listener.PrnfbRenderer.PrnfbVariable.PULL_REQUEST_FROM_BRANCH;
import static se.bjurr.prnfb.listener.PrnfbRenderer.PrnfbVariable.PULL_REQUEST_FROM_SSH_CLONE_URL;
import static se.bjurr.prnfb.listener.PrnfbRenderer.PrnfbVariable.PULL_REQUEST_ID;
import static se.bjurr.prnfb.listener.PrnfbRenderer.PrnfbVariable.PULL_REQUEST_TO_ID;
import static se.bjurr.prnfb.listener.PrnfbRenderer.PrnfbVariable.PULL_REQUEST_URL;
import static se.bjurr.prnfb.listener.PrnfbRenderer.PrnfbVariable.PULL_REQUEST_VERSION;
import static se.bjurr.prnfb.listener.PrnfbRenderer.REPO_PROTOCOL.http;
import static se.bjurr.prnfb.listener.PrnfbRenderer.REPO_PROTOCOL.ssh;
import static se.bjurr.prnfb.listener.UrlInvoker.HTTP_METHOD.DELETE;
import static se.bjurr.prnfb.listener.UrlInvoker.HTTP_METHOD.GET;
import static se.bjurr.prnfb.listener.UrlInvoker.HTTP_METHOD.POST;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.regex.Pattern;

import org.junit.Test;

import se.bjurr.prnbb.admin.utils.PrnfbTestBuilder;
import se.bjurr.prnbb.admin.utils.PullRequestEventBuilder;
import se.bjurr.prnbb.admin.utils.PullRequestRefBuilder;
import se.bjurr.prnfb.listener.PrnfbPullRequestAction;
import se.bjurr.prnfb.listener.PrnfbRenderer.PrnfbVariable;

import com.atlassian.bitbucket.pull.PullRequestState;
import com.google.common.io.Resources;

public class PrnfbPullRequestEventListenerTest {

 @Test
 public void testThatAdminFormFieldsAreUsedInAdminGUI() throws IOException {
  final URL resource = getResource("admin.vm");
  final String adminVmContent = Resources.toString(resource, UTF_8);
  for (final AdminFormValues.FIELDS field : AdminFormValues.FIELDS.values()) {
   assertTrue(field.name() + " in " + resource.toString(), adminVmContent.contains("name=\"" + field.name() + "\""));
  }
 }

 @Test
 public void testThatAUrlCanHaveSeveralVariables() throws Exception {
  prnfbTestBuilder()
    .isLoggedInAsAdmin()
    .withNotification( //
      notificationBuilder() //
        .withFieldValue(
          url,
          "http://bjurr.se/?PULL_REQUEST_FROM_HASH=${PULL_REQUEST_FROM_HASH}&PULL_REQUEST_TO_HASH=${PULL_REQUEST_TO_HASH}&PULL_REQUEST_FROM_REPO_SLUG=${PULL_REQUEST_FROM_REPO_SLUG}&PULL_REQUEST_TO_REPO_SLUG=${PULL_REQUEST_TO_REPO_SLUG}&revapp=${PULL_REQUEST_REVIEWERS_APPROVED_COUNT}&partapp=${PULL_REQUEST_PARTICIPANTS_APPROVED_COUNT}") //
        .withFieldValue(events, OPENED.name()) //
        .build() //
    ) //
    .store() //
    .trigger( //
      pullRequestEventBuilder().withFromRef( //
        pullRequestRefBuilder() //
          .withHash("cde456") //
          .withRepositorySlug("fromslug") //
        ).withToRef( //
          pullRequestRefBuilder() //
            .withHash("asd123") //
            .withRepositorySlug("toslug") //
        ) //
        .withPullRequestId(10L) //
        .withPullRequestAction(OPENED) //
        .withParticipant(PARTICIPANT, TRUE) //
        .withParticipant(PARTICIPANT, TRUE) //
        .withParticipant(PARTICIPANT, TRUE) //
        .withParticipant(PARTICIPANT, FALSE) //
        .withParticipant(AUTHOR, TRUE) //
        .withParticipant(AUTHOR, TRUE) //
        .withParticipant(AUTHOR, FALSE) //
        .build() //
    ) //
    .invokedUrl(
      0,
      "http://bjurr.se/?PULL_REQUEST_FROM_HASH=cde456&PULL_REQUEST_TO_HASH=asd123&PULL_REQUEST_FROM_REPO_SLUG=fromslug&PULL_REQUEST_TO_REPO_SLUG=toslug&revapp=2&partapp=3") //
    .invokedMethod(GET);
 }

 @Test
 public void testThatAUrlIsOnlyInvokedForConfiguredEvents() throws Exception {
  prnfbTestBuilder() //
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
        .withToRef( //
          pullRequestRefBuilder() //
        ) //
        .withPullRequestId(10L) //
        .withPullRequestAction(MERGED) //
        .build() //
    ) //
    .invokedNoUrl();
 }

 @Test
 public void testThatClosedPullRequestsAreNotIgnoredForMergedEvent() throws Exception {
  prnfbTestBuilder() //
    .isLoggedInAsAdmin() //
    .withNotification( //
      notificationBuilder() //
        .withFieldValue(url, "http://bjurr.se/") //
        .withFieldValue(events, MERGED.name()) //
        .build() //
    ) //
    .store() //
    .trigger( //
      pullRequestEventBuilder() //
        .beingClosed() //
        .withToRef( //
          pullRequestRefBuilder() //
        ) //
        .withPullRequestAction(MERGED) //
        .build() //
    ) //
    .invokedOnlyUrl("http://bjurr.se/");
 }

 @Test
 public void testThatClosedPullRequestsAreIgnoredForCommentEvent() throws Exception {
  prnfbTestBuilder() //
    .isLoggedInAsAdmin() //
    .withNotification( //
      notificationBuilder() //
        .withFieldValue(url, "http://bjurr.se/") //
        .withFieldValue(events, COMMENTED.name()) //
        .build() //
    ) //
    .store() //
    .trigger( //
      pullRequestEventBuilder() //
        .beingClosed() //
        .withToRef( //
          pullRequestRefBuilder() //
        ) //
        .withPullRequestAction(COMMENTED) //
        .build() //
    ) //
    .invokedNoUrl();
 }

 @Test
 public void testThatAUrlWithoutVariablesCanBeInvoked() throws Exception {
  prnfbTestBuilder() //
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
    .invokedUrl(0, "http://bjurr.se/");
 }

 @Test
 public void testThatAUrlWithVariablesFromAndToCanBeInvoked() throws Exception {
  for (final PrnfbVariable prnfbVariable : PrnfbVariable.values()) {
   if (!prnfbVariable.name().contains("_FROM_") && !prnfbVariable.name().contains("_TO_")) {
    continue;
   }
   PrnfbTestBuilder builder = prnfbTestBuilder() //
     .isLoggedInAsAdmin() //
     .withNotification( //
       notificationBuilder() //
         .withFieldValue(FORM_IDENTIFIER, "The Button") //
         .withFieldValue(FORM_TYPE, BUTTON_CONFIG_FORM.name()) //
         .withFieldValue(button_title, "The Button Text") //
         .withFieldValue(button_visibility, EVERYONE.name()) //
         .build() //
     ) //
     .withNotification( //
       notificationBuilder() //
         .withFieldValue(url, "http://bjurr.se/${" + prnfbVariable.name() + "}${" + BUTTON_TRIGGER_TITLE.name() + "}") //
         .withFieldValue(events, OPENED.name()) //
         .withFieldValue(events, BUTTON_TRIGGER) //
         .build() //
     ) //
     .store();
   PullRequestEventBuilder eventBuilder = builder.triggerPullRequestEventBuilder();

   PullRequestRefBuilder refBuilder;
   if (prnfbVariable.name().contains("_FROM_")) {
    refBuilder = eventBuilder.withFromRefPullRequestRefBuilder();
   } else {
    refBuilder = eventBuilder.withToRefPullRequestRefBuilder();
   }

   PullRequestEventBuilder pullRequestEventBuilder = refBuilder //
     .withHash("10") //
     .withId("10") //
     .withProjectId(10) //
     .withProjectKey("10") //
     .withRepositoryId(10) //
     .withRepositoryName("10") //
     .withRepositorySlug("10") //
     .withCloneUrl(http, "10") //
     .withCloneUrl(ssh, "10") //
     .withDisplayId("10") //
     .build() //
     .withPullRequestId(10L);
   pullRequestEventBuilder //
     .withPullRequestAction(OPENED) //
     .triggerEvent() //
     .invokedOnlyUrl("http://bjurr.se/10");

   builder //
     .withPullRequest( //
       pullRequestEventBuilder //
         .build() //
         .getPullRequest() //
     ) //
     .triggerButton("The Button") //
     .invokedUrl(1, "http://bjurr.se/10The+Button+Text");
  }
 }

 @Test
 public void testThatRepoUrlReturnsEmptyIfThereIsNotUrlWithThatProtocol() throws Exception {
  prnfbTestBuilder() //
    .isLoggedInAsAdmin() //
    .withNotification( //
      notificationBuilder() //
        .withFieldValue(url, "http://bjurr.se/${" + PULL_REQUEST_FROM_SSH_CLONE_URL + "}") //
        .withFieldValue(events, OPENED.name()) //
        .build() //
    ) //
    .store() //
    .trigger( //
      pullRequestEventBuilder() //
        .withFromRef( //
          pullRequestRefBuilder() //
        ) //
        .withPullRequestId(10L) //
        .withPullRequestAction(OPENED) //
        .build() //
    ) //
    .invokedUrl(0, "http://bjurr.se/");
 }

 @Test
 public void testThatAUrlWithCommentVariableHasSpacesReplaced() throws Exception {
  prnfbTestBuilder() //
    .isLoggedInAsAdmin() //
    .withNotification( //
      notificationBuilder() //
        .withFieldValue(url, "http://bjurr.se/${" + PULL_REQUEST_COMMENT_TEXT.name() + "}") //
        .withFieldValue(events, COMMENTED.name()) //
        .build() //
    ) //
    .store() //
    .trigger( //
      pullRequestEventBuilder() //
        .withPullRequestAction(COMMENTED) //
        .withCommentText("a text with\nnewline") //
        .build() //
    ) //
    .invokedUrl(0, "http://bjurr.se/a+text+with%0Anewline");
 }

 @Test
 public void testThatAUrlWithVariableFromBranchCanBeInvokedWhenBranchIdContainsSlashes() throws Exception {
  prnfbTestBuilder() //
    .isLoggedInAsAdmin() //
    .withNotification( //
      notificationBuilder() //
        .withFieldValue(url, "http://bjurr.se/${" + PULL_REQUEST_FROM_BRANCH.name() + "}") //
        .withFieldValue(events, OPENED.name()) //
        .build() //
    ) //
    .store() //
    .trigger( //
      pullRequestEventBuilder() //
        .withFromRef( //
          pullRequestRefBuilder() //
            .withId("refs/heads/feature/branchmodmerge") //
            .withDisplayId("feature/branchmodmerge") //
        ) //
        .withPullRequestId(10L) //
        .withPullRequestAction(OPENED) //
        .build() //
    ) //
    .invokedUrl(0, "http://bjurr.se/feature%2Fbranchmodmerge");
 }

 @Test
 public void testThatAUrlWithVariableFromBranchCanBeInvokedWhenBranchIdContainsOnlyName() throws Exception {
  prnfbTestBuilder() //
    .isLoggedInAsAdmin() //
    .withNotification( //
      notificationBuilder() //
        .withFieldValue(url, "http://bjurr.se/${" + PULL_REQUEST_FROM_BRANCH.name() + "}") //
        .withFieldValue(events, OPENED.name()) //
        .build() //
    ) //
    .store() //
    .trigger( //
      pullRequestEventBuilder() //
        .withFromRef( //
          pullRequestRefBuilder() //
            .withId("branchmodmerge") //
            .withDisplayId("branchmodmerge") //
        ) //
        .withPullRequestId(10L) //
        .withPullRequestAction(OPENED) //
        .build() //
    ) //
    .invokedUrl(0, "http://bjurr.se/branchmodmerge");
 }

 @Test
 public void testThatAUrlWithVariablesExceptFromAndToCanBeInvoked() throws Exception {
  prnfbTestBuilder()
    .isLoggedInAsAdmin()
    .withBaseUrl("http://bitbucket.server")
    .withNotification( //
      notificationBuilder() //
        .withFieldValue(
          url,
          "http://bjurr.se/id=${" + PULL_REQUEST_ID.name() + "}&action=${" + PULL_REQUEST_ACTION.name()
            + "}&displayName=${" + PULL_REQUEST_AUTHOR_DISPLAY_NAME.name() + "}&authorEmail=${"
            + PULL_REQUEST_AUTHOR_EMAIL.name() + "}&authorId=${" + PULL_REQUEST_AUTHOR_ID.name() + "}&authorName=${"
            + PULL_REQUEST_AUTHOR_NAME.name() + "}&authorSlug=${" + PULL_REQUEST_AUTHOR_SLUG.name()
            + "}&pullRequestUrl=${" + PULL_REQUEST_URL.name() + "}") //
        .withFieldValue(events, OPENED.name()) //
        .build() //
    ) //
    .store() //
    .trigger( //
      pullRequestEventBuilder() //
        .withPullRequestId(10L) //
        .withToRef( //
          pullRequestRefBuilder() //
            .withProjectKey("theProject") //
            .withRepositoryName("the Repo Name") //
        ) //
        .withPullRequestAction(OPENED) //
        .withAuthor( //
          prnfbParticipantBuilder() //
            .withDisplayName("authorDisplayName") //
            .withEmail("authorEmail") //
            .withId(100) //
            .withName("authorName") //
            .withSlug("authorSlug") //
            .build() //
        ) //
        .build() //
    ) //
    .invokedUrl(
      0,
      "http://bjurr.se/id=10&action=OPENED&displayName=authorDisplayName&authorEmail=authorEmail&authorId=100&authorName=authorName&authorSlug=authorSlug&pullRequestUrl=http%3A%2F%2Fbitbucket.server%2Fprojects%2FtheProject%2Frepos%2Fthe-Repo-Name%2Fpull-requests%2F10" //
    );
 }

 @Test
 public void testThatPostContentIsNotSentIfMethodIsNotSet() throws Exception {
  prnfbTestBuilder() //
    .isLoggedInAsAdmin() //
    .withNotification( //
      notificationBuilder() //
        .withFieldValue(url, "http://bjurr.se/") //
        .withFieldValue(post_content, "should not be sent") //
        .withFieldValue(events, OPENED.name()) //
        .build() //
    ) //
    .store() //
    .trigger( //
      pullRequestEventBuilder() //
        .withPullRequestAction(OPENED) //
        .build() //
    ) //
    .invokedUrl(0, "http://bjurr.se/") //
    .invokedMethod(GET) //
    .didNotSendPostContentAt(0);
 }

 @Test
 public void testThatPostContentIsNotSentIfMethodIsPOSTButThereIsNotPostContent() throws Exception {
  prnfbTestBuilder() //
    .isLoggedInAsAdmin() //
    .withNotification( //
      notificationBuilder() //
        .withFieldValue(url, "http://bjurr.se/") //
        .withFieldValue(events, OPENED.name()) //
        .withFieldValue(post_content, " ") //
        .withFieldValue(method, "POST") //
        .build() //
    ) //
    .store() //
    .trigger( //
      pullRequestEventBuilder() //
        .withPullRequestAction(OPENED) //
        .build() //
    ) //
    .invokedUrl(0, "http://bjurr.se/") //
    .invokedMethod(POST) //
    .didNotSendPostContentAt(0);
 }

 @Test
 public void testThatPostContentIsNotSentIfMethodIsGETAndThereIsPostContent() throws Exception {
  prnfbTestBuilder() //
    .isLoggedInAsAdmin() //
    .withNotification( //
      notificationBuilder() //
        .withFieldValue(url, "http://bjurr.se/") //
        .withFieldValue(events, OPENED.name()) //
        .withFieldValue(post_content, "some content") //
        .withFieldValue(method, GET.name()) //
        .build() //
    ) //
    .store() //
    .trigger( //
      pullRequestEventBuilder() //
        .withPullRequestAction(OPENED) //
        .build() //
    ) //
    .invokedUrl(0, "http://bjurr.se/") //
    .invokedMethod(GET) //
    .didNotSendPostContentAt(0);
 }

 @Test
 public void testThatPostContentIsNotSentIfMethodIsDELETEAndThereIsPostContent() throws Exception {
  prnfbTestBuilder() //
    .isLoggedInAsAdmin() //
    .withNotification( //
      notificationBuilder() //
        .withFieldValue(url, "http://bjurr.se/") //
        .withFieldValue(events, OPENED.name()) //
        .withFieldValue(post_content, "some content") //
        .withFieldValue(method, "DELETE") //
        .build() //
    ) //
    .store() //
    .trigger( //
      pullRequestEventBuilder() //
        .withPullRequestAction(OPENED) //
        .build() //
    ) //
    .invokedUrl(0, "http://bjurr.se/") //
    .invokedMethod(DELETE) //
    .didNotSendPostContentAt(0);
 }

 @Test
 public void testThatPostContentIsSentIfMethodIsPOSTAndThereIsPostContent() throws Exception {
  prnfbTestBuilder() //
    .isLoggedInAsAdmin() //
    .withNotification( //
      notificationBuilder() //
        .withFieldValue(url, "http://bjurr.se/") //
        .withFieldValue(events, OPENED.name()) //
        .withFieldValue(post_content, "some content") //
        .withFieldValue(method, "POST") //
        .build() //
    ) //
    .store() //
    .trigger( //
      pullRequestEventBuilder() //
        .withPullRequestAction(OPENED) //
        .build() //
    ) //
    .invokedUrl(0, "http://bjurr.se/") //
    .didSendPostContentAt(0, "some content");
 }

 @Test
 public void testThatPostContentIsSentIfMethodIsPUTAndThereIsPostContent() throws Exception {
  prnfbTestBuilder() //
    .isLoggedInAsAdmin() //
    .withNotification( //
      notificationBuilder() //
        .withFieldValue(url, "http://bjurr.se/") //
        .withFieldValue(events, OPENED.name()) //
        .withFieldValue(post_content, "some content") //
        .withFieldValue(method, "PUT") //
        .build() //
    ) //
    .store() //
    .trigger( //
      pullRequestEventBuilder() //
        .withPullRequestAction(OPENED) //
        .build() //
    ) //
    .invokedUrl(0, "http://bjurr.se/") //
    .didSendPostContentAt(0, "some content");
 }

 @Test
 public void testThatPostContentIsSentAndRenderedIfMethodIsPOSTAndThereIsPostContent() throws Exception {
  prnfbTestBuilder() //
    .isLoggedInAsAdmin() //
    .withNotification( //
      notificationBuilder() //
        .withFieldValue(url, "http://bjurr.se/") //
        .withFieldValue(events, OPENED.name()) //
        .withFieldValue(post_content, "some ${" + PULL_REQUEST_ACTION.name() + "} content") //
        .withFieldValue(method, "POST") //
        .build() //
    ) //
    .store() //
    .trigger( //
      pullRequestEventBuilder() //
        .withPullRequestAction(OPENED) //
        .build() //
    ) //
    .invokedUrl(0, "http://bjurr.se/") //
    .didSendPostContentAt(0, "some OPENED content");
 }

 @Test
 public void testThatCustomHeaderCanBeSent() throws Exception {
  prnfbTestBuilder() //
    .isLoggedInAsAdmin() //
    .withNotification( //
      notificationBuilder() //
        .withFieldValue(url, "http://bjurr.se/") //
        .withFieldValue(events, OPENED.name()) //
        .withFieldValue(header_name, "CustomHeader") //
        .withFieldValue(header_value, "custom value") //
        .build() //
    ) //
    .store() //
    .trigger( //
      pullRequestEventBuilder() //
        .withPullRequestAction(OPENED) //
        .build() //
    ) //
    .invokedUrl(0, "http://bjurr.se/") //
    .usedHeader(0, "CustomHeader", "custom value");
 }

 @Test
 public void testThatCustomHeaderCanBeSentWithVariables() throws Exception {
  prnfbTestBuilder() //
    .isLoggedInAsAdmin() //
    .withNotification( //
      notificationBuilder() //
        .withFieldValue(url, "http://bjurr.se/") //
        .withFieldValue(events, OPENED.name()) //
        .withFieldValue(header_name, "CustomHeader") //
        .withFieldValue(header_value, "custom ${" + PULL_REQUEST_ACTION.name() + "} value") //
        .build() //
    ) //
    .store() //
    .trigger( //
      pullRequestEventBuilder() //
        .withPullRequestAction(OPENED) //
        .build() //
    ) //
    .invokedUrl(0, "http://bjurr.se/") //
    .usedHeader(0, "CustomHeader", "custom OPENED value");
 }

 @Test
 public void testThatEmptyHeaderNameIsIgnored() throws Exception {
  prnfbTestBuilder() //
    .isLoggedInAsAdmin() //
    .withNotification( //
      notificationBuilder() //
        .withFieldValue(url, "http://bjurr.se/") //
        .withFieldValue(events, OPENED.name()) //
        .withFieldValue(header_name, " ") //
        .withFieldValue(header_value, "custom value") //
        .build() //
    ) //
    .store() //
    .trigger( //
      pullRequestEventBuilder() //
        .withPullRequestAction(OPENED) //
        .build() //
    ) //
    .invokedUrl(0, "http://bjurr.se/") //
    .didNotSendHeaders();
 }

 @Test
 public void testThatBasicAuthenticationHeaderIsSentIfThereIsAUser() throws Exception {
  prnfbTestBuilder() //
    .isLoggedInAsAdmin() //
    .withNotification( //
      notificationBuilder() //
        .withFieldValue(url, "http://bjurr.se/") //
        .withFieldValue(events, OPENED.name()) //
        .withFieldValue(user, "theuser") //
        .withFieldValue(password, "thepassword") //
        .build() //
    ) //
    .store() //
    .trigger( //
      pullRequestEventBuilder() //
        .withPullRequestAction(OPENED) //
        .build() //
    ) //
    .invokedUrl(0, "http://bjurr.se/") //
    .usedHeader(0, AUTHORIZATION, "Basic dGhldXNlcjp0aGVwYXNzd29yZA==");
 }

 @Test
 public void testThatBasicAuthenticationHeaderIsSentAlongWithCustomHeaders() throws Exception {
  prnfbTestBuilder() //
    .isLoggedInAsAdmin() //
    .withNotification( //
      notificationBuilder() //
        .withFieldValue(url, "http://bjurr.se/") //
        .withFieldValue(events, OPENED.name()) //
        .withFieldValue(user, "theuser") //
        .withFieldValue(password, "thepassword") //
        .withFieldValue(header_name, "CustomHeader1") //
        .withFieldValue(header_value, "custom value1") //
        .withFieldValue(header_name, "CustomHeader2") //
        .withFieldValue(header_value, "theuser:thepassword") //
        .build() //
    ) //
    .store() //
    .trigger( //
      pullRequestEventBuilder() //
        .withPullRequestAction(OPENED) //
        .build() //
    ) //
    .invokedUrl(0, "http://bjurr.se/") //
    .usedHeader(0, AUTHORIZATION, "Basic dGhldXNlcjp0aGVwYXNzd29yZA==") //
    .usedHeader(0, "CustomHeader1", "custom value1") //
    .usedHeader(0, "CustomHeader2", "theuser:thepassword");
 }

 @Test
 public void testThatBasicAuthenticationHeaderIsNotSentIfThereIsNoUser() throws Exception {
  prnfbTestBuilder() //
    .isLoggedInAsAdmin() //
    .withNotification( //
      notificationBuilder() //
        .withFieldValue(url, "http://bjurr.se/") //
        .withFieldValue(events, OPENED.name()) //
        .withFieldValue(user, "") //
        .withFieldValue(password, "thepassword") //
        .build() //
    ) //
    .store() //
    .trigger( //
      pullRequestEventBuilder() //
        .withPullRequestAction(OPENED) //
        .build() //
    ) //
    .invokedUrl(0, "http://bjurr.se/") //
    .didNotSendHeaders();
 }

 @Test
 public void testThatBasicAuthenticationHeaderIsNotSentIfThereIsNoPassword() throws Exception {
  prnfbTestBuilder() //
    .isLoggedInAsAdmin() //
    .withNotification( //
      notificationBuilder() //
        .withFieldValue(url, "http://bjurr.se/") //
        .withFieldValue(events, OPENED.name()) //
        .withFieldValue(user, "theuser") //
        .withFieldValue(password, "") //
        .build() //
    ) //
    .store() //
    .trigger( //
      pullRequestEventBuilder() //
        .withPullRequestAction(OPENED) //
        .build() //
    ) //
    .invokedUrl(0, "http://bjurr.se/") //
    .didNotSendHeaders();
 }

 @Test
 public void testThatBasicAuthenticationHeaderIsNotSentIfTheUserContainsOnlySpace() throws Exception {
  prnfbTestBuilder() //
    .isLoggedInAsAdmin() //
    .withNotification( //
      notificationBuilder() //
        .withFieldValue(url, "http://bjurr.se/") //
        .withFieldValue(events, OPENED.name()) //
        .withFieldValue(user, " ") //
        .withFieldValue(password, "thepassword") //
        .build() //
    ) //
    .store() //
    .trigger( //
      pullRequestEventBuilder() //
        .withPullRequestAction(OPENED) //
        .build() //
    ) //
    .invokedUrl(0, "http://bjurr.se/") //
    .didNotSendHeaders();
 }

 @Test
 public void testThatBasicAuthenticationHeaderIsNotSentIfThePasswordContainsOnlySpace() throws Exception {
  prnfbTestBuilder() //
    .isLoggedInAsAdmin() //
    .withNotification( //
      notificationBuilder() //
        .withFieldValue(url, "http://bjurr.se/") //
        .withFieldValue(events, OPENED.name()) //
        .withFieldValue(user, "theuser") //
        .withFieldValue(password, " ") //
        .build() //
    ) //
    .store() //
    .trigger( //
      pullRequestEventBuilder() //
        .withPullRequestAction(OPENED) //
        .build() //
    ) //
    .invokedUrl(0, "http://bjurr.se/") //
    .didNotSendHeaders();
 }

 @Test
 public void testThatFieldsUsedInAdminGUIArePresentInAdminFormFields() throws IOException {
  final URL resource = getResource("admin.vm");
  final String adminVmContent = Resources.toString(resource, UTF_8);
  final java.util.regex.Matcher m = Pattern //
    .compile("<input [^n]*name=\"([^\\\"]*)\"") //
    .matcher(adminVmContent);
  while (m.find()) {
   assertTrue(m.group(1) + " found at " + resource.toString(), AdminFormValues.FIELDS.valueOf(m.group(1)) != null);
  }
 }

 @Test
 public void testThatFilterCanBeUsedToIgnoreEventsThatAreOnAnotherProject() throws Exception {
  prnfbTestBuilder() //
    .isLoggedInAsAdmin() //
    .withNotification( //
      notificationBuilder() //
        .withFieldValue(url, "http://bjurr.se/") //
        .withFieldValue(events, OPENED.name()) //
        .withFieldValue(filter_string, "${PULL_REQUEST_FROM_REPO_PROJECT_KEY}") //
        .withFieldValue(filter_regexp, "EXP") //
        .build() //
    ) //
    .store() //
    .trigger( //
      pullRequestEventBuilder() //
        .withFromRef( //
          pullRequestRefBuilder() //
            .withProjectKey("ABC") //
        ) //
        .withPullRequestId(10L) //
        .withPullRequestAction(OPENED) //
        .build() //
    ) //
    .invokedNoUrl();
 }

 @Test
 public void testThatFilterCanIncludeRescopedFrom() throws Exception {
  prnfbTestBuilder() //
    .isLoggedInAsAdmin() //
    .withNotification( //
      notificationBuilder() //
        .withFieldValue(url, "http://bjurr.se/") //
        .withFieldValue(events, RESCOPED_FROM) //
        .withFieldValue(filter_string, "${" + PULL_REQUEST_ACTION.name() + "}") //
        .withFieldValue(filter_regexp, RESCOPED_FROM) //
        .build() //
    ) //
    .store() //
    .trigger( //
      pullRequestEventBuilder() //
        .withFromRef( //
          pullRequestRefBuilder() //
            .withHash("from") //
        ) //
        .withToRef( //
          pullRequestRefBuilder() //
            .withHash(PREVIOUS_TO_HASH) //
        ) //
        .withPullRequestId(10L) //
        .withPullRequestAction(RESCOPED) //
        .build() //
    ) //
    .invokedUrl(0, "http://bjurr.se/");
 }

 @Test
 public void testThatFilterCanBeUsedWithComments() throws Exception {
  prnfbTestBuilder()
    .isLoggedInAsAdmin()
    .withNotification( //
      notificationBuilder()
        .withFieldValue(url,
          "http://bjurr.se/?comment=${" + PULL_REQUEST_COMMENT_TEXT + "}&version=${" + PULL_REQUEST_VERSION + "}")
        .withFieldValue(events, COMMENTED.name())
        .withFieldValue(filter_string,
          "${" + PULL_REQUEST_TO_ID.name() + "}:${" + PULL_REQUEST_COMMENT_TEXT.name() + "}:") //
        .withFieldValue(filter_regexp, ".*:.*?keyword.*?:.*") //
        .build() //
    ) //
    .store() //
    .trigger( //
      pullRequestEventBuilder() //
        .withFromRef( //
          pullRequestRefBuilder() //
            .withHash("from") //
        ) //
        .withToRef( //
          pullRequestRefBuilder() //
            .withId("123") //
        ) //
        .withCommentText("keyword A nice comment") //
        .withPullRequestId(10L) //
        .withPullRequestAction(COMMENTED) //
        .build() //
    ) //
    .invokedUrl(0, "http://bjurr.se/?comment=keyword+A+nice+comment&version=0"); //
 }

 @Test
 public void testThatFilterCanBeUsedWithCommentsAndSpecialEscapedChars() throws Exception {
  prnfbTestBuilder()
    .isLoggedInAsAdmin()
    .withNotification( //
      notificationBuilder()
        .withFieldValue(url,
          "http://bjurr.se/?comment=${" + PULL_REQUEST_COMMENT_TEXT + "}&version=${" + PULL_REQUEST_VERSION + "}")
        .withFieldValue(events, COMMENTED.name())
        .withFieldValue(filter_string,
          "${" + PULL_REQUEST_TO_ID.name() + "}:${" + PULL_REQUEST_COMMENT_TEXT.name() + "}:") //
        .withFieldValue(filter_regexp, ".*:\\skeyword\\s:.*") //
        .build() //
    ) //
    .store() //
    .trigger( //
      pullRequestEventBuilder() //
        .withFromRef( //
          pullRequestRefBuilder() //
            .withHash("from") //
        ) //
        .withToRef( //
          pullRequestRefBuilder() //
            .withId("123") //
        ) //
        .withCommentText(" keyword ") //
        .withPullRequestId(10L) //
        .withPullRequestAction(COMMENTED) //
        .build() //
    ) //
    .invokedOnlyUrl("http://bjurr.se/?comment=+keyword+&version=0");
 }

 @Test
 public void testThatFilterCanBeUsedWithCommentsIgnore() throws Exception {
  prnfbTestBuilder()
    .isLoggedInAsAdmin()
    .withNotification(
      notificationBuilder()
        .withFieldValue(url,
          "http://bjurr.se/?comment=${" + PULL_REQUEST_COMMENT_TEXT + "}&version=${" + PULL_REQUEST_VERSION + "}")
        .withFieldValue(events, COMMENTED.name())
        .withFieldValue(filter_string,
          "${" + PULL_REQUEST_TO_ID.name() + "}:${" + PULL_REQUEST_COMMENT_TEXT.name() + "}:")
        .withFieldValue(filter_regexp, ".*:.*?keyword.*?:.*") //
        .build() //
    ) //
    .store() //
    .trigger( //
      pullRequestEventBuilder() //
        .withFromRef( //
          pullRequestRefBuilder() //
            .withHash("from") //
        ) //
        .withToRef( //
          pullRequestRefBuilder() //
            .withId("123") //
        ) //
        .withCommentText("notkw A nice comment") //
        .withPullRequestId(10L) //
        .withPullRequestAction(COMMENTED) //
        .build() //
    ) //
    .invokedNoUrl();
 }

 @Test
 public void testThatStringWithVariableCommentIsEmptyIfNotACommentEvent() throws Exception {
  prnfbTestBuilder()
    .isLoggedInAsAdmin()
    .withNotification( //
      notificationBuilder()
        //
        .withFieldValue(url, "http://bjurr.se/?comment=${" + PULL_REQUEST_COMMENT_TEXT + "}")
        .withFieldValue(events, OPENED.name()) //
        .build() //
    ) //
    .store() //
    .trigger( //
      pullRequestEventBuilder() //
        .withPullRequestId(10L) //
        .withPullRequestAction(OPENED) //
        .build() //
    ) //
    .invokedUrl(0, "http://bjurr.se/?comment=");
 }

 @Test
 public void testThatURLCanIncludeRescopedFrom() throws Exception {
  prnfbTestBuilder() //
    .isLoggedInAsAdmin() //
    .withNotification( //
      notificationBuilder() //
        .withFieldValue(url, "http://bjurr.se/${" + PULL_REQUEST_ACTION.name() + "}") //
        .withFieldValue(events, RESCOPED_FROM) //
        .build() //
    ) //
    .store() //
    .trigger( //
      pullRequestEventBuilder() //
        .withFromRef( //
          pullRequestRefBuilder() //
            .withHash("from") //
        ) //
        .withToRef( //
          pullRequestRefBuilder() //
            .withHash(PREVIOUS_TO_HASH) //
        ) //
        .withPullRequestAction(RESCOPED) //
        .build() //
    ) //
    .invokedUrl(0, "http://bjurr.se/RESCOPED_FROM");
 }

 @Test
 public void testThatURLCanIncludeRescopedTo() throws Exception {
  prnfbTestBuilder() //
    .isLoggedInAsAdmin() //
    .withNotification( //
      notificationBuilder() //
        .withFieldValue(url, "http://bjurr.se/${" + PULL_REQUEST_ACTION.name() + "}") //
        .withFieldValue(events, RESCOPED_TO) //
        .build() //
    ) //
    .store() //
    .trigger( //
      pullRequestEventBuilder() //
        .withFromRef( //
          pullRequestRefBuilder() //
            .withHash(PREVIOUS_FROM_HASH) //
        ) //
        .withToRef( //
          pullRequestRefBuilder() //
            .withHash("toHash") //
        ) //
        .withPullRequestAction(RESCOPED) //
        .build() //
    ) //
    .invokedOnlyUrl("http://bjurr.se/RESCOPED_TO");
 }

 @Test
 public void testThatURLCanIncludeRescopedFromWhenBothFromAndToChanges() throws Exception {
  prnfbTestBuilder() //
    .isLoggedInAsAdmin() //
    .withNotification( //
      notificationBuilder() //
        .withFieldValue(url, "http://bjurr.se/${" + PULL_REQUEST_ACTION.name() + "}") //
        .withFieldValue(events, RESCOPED_FROM) //
        .build() //
    ) //
    .store() //
    .trigger( //
      pullRequestEventBuilder() //
        .withFromRef( //
          pullRequestRefBuilder() //
            .withHash("fromHash") //
        ) //
        .withToRef( //
          pullRequestRefBuilder() //
            .withHash("toHash") //
        ) //
        .withPullRequestAction(RESCOPED) //
        .build() //
    ) //
    .invokedOnlyUrl("http://bjurr.se/RESCOPED_FROM");
 }

 @Test
 public void testThatAllURLsMatchingEventsAreTriggered() throws Exception {
  prnfbTestBuilder() //
    .isLoggedInAsAdmin() //
    .withNotification( //
      notificationBuilder() //
        .withFieldValue(url, "http://bjurr.se/${" + PULL_REQUEST_ACTION.name() + "}") //
        .withFieldValue(events, RESCOPED_FROM) //
        .build() //
    ) //
    .store() //
    .withNotification( //
      notificationBuilder() //
        .withFieldValue(url, "http://bjurr.se/${" + PULL_REQUEST_ACTION.name() + "}") //
        .withFieldValue(events, RESCOPED_TO) //
        .build() //
    ) //
    .store() //
    .withNotification( //
      notificationBuilder() //
        .withFieldValue(url, "http://bjurr.se/${" + PULL_REQUEST_ACTION.name() + "}") //
        .withFieldValue(events, RESCOPED_FROM) //
        .withFieldValue(events, RESCOPED_TO) //
        .build() //
    ) //
    .store() //
    .trigger( //
      pullRequestEventBuilder() //
        .withFromRef( //
          pullRequestRefBuilder() //
            .withHash("fromHash") //
        ) //
        .withToRef( //
          pullRequestRefBuilder() //
            .withHash("toHash") //
        ) //
        .withPullRequestAction(RESCOPED) //
        .build() //
    ) //
    .invokedUrl(0, "http://bjurr.se/RESCOPED_FROM") //
    .invokedUrl(1, "http://bjurr.se/RESCOPED_TO") //
    .invokedUrl(2, "http://bjurr.se/RESCOPED_FROM");
 }

 @Test
 public void testThatURLCanIncludeRescopedFromWhenBothFromAndToChangesAndBothFromAndToAreConfigured() throws Exception {
  prnfbTestBuilder() //
    .isLoggedInAsAdmin() //
    .withNotification( //
      notificationBuilder() //
        .withFieldValue(url, "http://bjurr.se/${" + PULL_REQUEST_ACTION.name() + "}") //
        .withFieldValue(events, RESCOPED_FROM) //
        .withFieldValue(events, RESCOPED_TO) //
        .build() //
    ) //
    .store() //
    .trigger( //
      pullRequestEventBuilder() //
        .withFromRef( //
          pullRequestRefBuilder() //
            .withHash("fromHash") //
        ) //
        .withToRef( //
          pullRequestRefBuilder() //
            .withHash("toHash") //
        ) //
        .withPullRequestAction(RESCOPED) //
        .build() //
    ) //
    .invokedUrl(0, "http://bjurr.se/RESCOPED_FROM");
 }

 @Test
 public void testThatURLCanIncludeRescopedToWhenBothFromAndToChanges() throws Exception {
  prnfbTestBuilder() //
    .isLoggedInAsAdmin() //
    .withNotification( //
      notificationBuilder() //
        .withFieldValue(url, "http://bjurr.se/${" + PULL_REQUEST_ACTION.name() + "}") //
        .withFieldValue(events, RESCOPED_TO) //
        .build() //
    ) //
    .store() //
    .trigger( //
      pullRequestEventBuilder() //
        .withFromRef( //
          pullRequestRefBuilder() //
            .withHash("fromHash") //
        ) //
        .withToRef( //
          pullRequestRefBuilder() //
            .withHash("toHash") //
        ) //
        .withPullRequestAction(RESCOPED) //
        .build() //
    ) //
    .invokedUrl(0, "http://bjurr.se/RESCOPED_TO");
 }

 @Test
 public void testThatFilterCanBeUsedToIgnoreEventsThatAreOnAnotherProjectAnBranch() throws Exception {
  prnfbTestBuilder()//
    .isLoggedInAsAdmin()//
    .withNotification(//
      notificationBuilder()//
        .withFieldValue(url, "http://bjurr.se/")//
        .withFieldValue(events, OPENED.name())//
        .withFieldValue(filter_string, "${PULL_REQUEST_FROM_REPO_PROJECT_KEY} ${PULL_REQUEST_FROM_ID}")//
        .withFieldValue(filter_regexp, "EXP my_branch")//
        .build()//
    )//
    .store()//
    .trigger(//
      pullRequestEventBuilder()//
        .withFromRef(//
          pullRequestRefBuilder()//
            .withProjectKey("ABC")//
            .withId("my_therbranch")//
        )//
        .withPullRequestId(10L)//
        .withPullRequestAction(OPENED)//
        .build()//
    )//
    .invokedNoUrl();
 }

 @Test
 public void testThatFilterCanBeUsedToTriggerEventsThatAreOnAnotherProject() throws Exception {
  prnfbTestBuilder() //
    .isLoggedInAsAdmin() //
    .withNotification( //
      notificationBuilder() //
        .withFieldValue(url, "http://bjurr.se/") //
        .withFieldValue(events, OPENED.name()) //
        .withFieldValue(filter_string, "${PULL_REQUEST_FROM_REPO_PROJECT_KEY}") //
        .withFieldValue(filter_regexp, "EXP") //
        .build() //
    ) //
    .store() //
    .trigger( //
      pullRequestEventBuilder() //
        .withFromRef( //
          pullRequestRefBuilder() //
            .withProjectKey("EXP") //
        ) //
        .withPullRequestId(10L) //
        .withPullRequestAction(OPENED) //
        .build() //
    ) //
    .invokedUrl(0, "http://bjurr.se/");
 }

 @Test
 public void testThatFilterCanBeUsedToTriggerOnEventsThatAreOnAnotherProjectAnBranch() throws Exception {
  prnfbTestBuilder() //
    .isLoggedInAsAdmin() //
    .withNotification( //
      notificationBuilder() //
        .withFieldValue(url, "http://bjurr.se/") //
        .withFieldValue(events, OPENED.name()) //
        .withFieldValue(filter_string, "${PULL_REQUEST_FROM_REPO_PROJECT_KEY} ${PULL_REQUEST_FROM_ID}") //
        .withFieldValue(filter_regexp, "EXP my_branch") //
        .build() //
    ) //
    .store() //
    .trigger( //
      pullRequestEventBuilder() //
        .withFromRef( //
          pullRequestRefBuilder() //
            .withProjectKey("EXP") //
            .withId("my_branch") //
        ) //
        .withPullRequestId(10L) //
        .withPullRequestAction(OPENED) //
        .build() //
    ) //
    .invokedUrl(0, "http://bjurr.se/");
 }

 @Test
 public void testThatMultipleUrlsCanBeInvoked() throws Exception {
  prnfbTestBuilder() //
    .isLoggedInAsAdmin() //
    .withNotification( //
      notificationBuilder() //
        .withFieldValue(url, "http://merged.se/") //
        .withFieldValue(events, MERGED.name()) //
        .build() //
    ) //
    .withNotification( //
      notificationBuilder() //
        .withFieldValue(url, "http://opened.se/") //
        .withFieldValue(events, OPENED.name()) //
        .build() //
    ) //
    .store() //
    .trigger( //
      pullRequestEventBuilder() //
        .withPullRequestId(10L) //
        .withPullRequestAction(MERGED) //
        .build() //
    ) //
    .invokedOnlyUrl("http://merged.se/") //
    .didNotSendHeaders();
 }

 @Test
 public void testThatProxyCanBeUsedWhenInvokingUrl() throws Exception {
  prnfbTestBuilder() //
    .isLoggedInAsAdmin() //
    .withNotification( //
      notificationBuilder() //
        .withFieldValue(url, "http://bjurr.se/") //
        .withFieldValue(events, OPENED.name()) //
        .withFieldValue(proxy_server, "proxyhost") //
        .withFieldValue(proxy_port, " 1234 ") //
        .build() //
    ) //
    .store() //
    .trigger( //
      pullRequestEventBuilder() //
        .withPullRequestAction(OPENED) //
        .build() //
    ) //
    .invokedUrl(0, "http://bjurr.se/") //
    .usedNoProxyUser(0) //
    .usedNoProxyPassword(0) //
    .usedProxyHost(0, "proxyhost") //
    .usedProxyPort(0, 1234);
 }

 @Test
 public void testThatProxyPortIsNeeded() throws Exception {
  prnfbTestBuilder() //
    .isLoggedInAsAdmin() //
    .withNotification( //
      notificationBuilder() //
        .withFieldValue(url, "http://bjurr.se/") //
        .withFieldValue(events, OPENED.name()) //
        .withFieldValue(proxy_server, "proxyhost") //
        .withFieldValue(proxy_port, " ") //
        .build() //
    ) //
    .store() //
    .trigger( //
      pullRequestEventBuilder() //
        .withPullRequestAction(OPENED) //
        .build() //
    ) //
    .invokedUrl(0, "http://bjurr.se/") //
    .usedNoProxy(0);
 }

 @Test
 public void testThatProxyHostIsNeeded() throws Exception {
  prnfbTestBuilder() //
    .isLoggedInAsAdmin() //
    .withNotification( //
      notificationBuilder() //
        .withFieldValue(url, "http://bjurr.se/") //
        .withFieldValue(events, OPENED.name()) //
        .withFieldValue(proxy_server, "") //
        .withFieldValue(proxy_port, "123") //
        .build() //
    ) //
    .store() //
    .trigger( //
      pullRequestEventBuilder() //
        .withPullRequestAction(OPENED) //
        .build() //
    ) //
    .invokedUrl(0, "http://bjurr.se/") //
    .usedNoProxy(0);
 }

 @Test
 public void testThatProxyCanUseUserAndPassword() throws Exception {
  prnfbTestBuilder() //
    .isLoggedInAsAdmin() //
    .withNotification( //
      notificationBuilder() //
        .withFieldValue(url, "http://bjurr.se/") //
        .withFieldValue(events, OPENED.name()) //
        .withFieldValue(proxy_server, "proxyhost") //
        .withFieldValue(proxy_port, "123") //
        .withFieldValue(proxy_user, "proxyuser") //
        .withFieldValue(proxy_password, "proxypassword") //
        .build() //
    ) //
    .store() //
    .trigger( //
      pullRequestEventBuilder() //
        .withPullRequestAction(OPENED) //
        .build() //
    ) //
    .invokedUrl(0, "http://bjurr.se/") //
    .usedProxyUser(0, "proxyuser") //
    .usedProxyPassword(0, "proxypassword");
 }

 @Test
 public void testThatProxyDoesNotAuthenticateIfNoUser() throws Exception {
  prnfbTestBuilder() //
    .isLoggedInAsAdmin() //
    .withNotification( //
      notificationBuilder() //
        .withFieldValue(url, "http://bjurr.se/") //
        .withFieldValue(events, OPENED.name()) //
        .withFieldValue(proxy_server, "proxyhost") //
        .withFieldValue(proxy_port, "123") //
        .withFieldValue(proxy_user, " ") //
        .withFieldValue(proxy_password, "proxypassword") //
        .build() //
    ) //
    .store() //
    .trigger( //
      pullRequestEventBuilder() //
        .withPullRequestAction(OPENED) //
        .build() //
    ) //
    .invokedUrl(0, "http://bjurr.se/") //
    .usedNoProxyAuthentication(0);
 }

 @Test
 public void testThatProxyDoesNotAuthenticateIfNoPassword() throws Exception {
  prnfbTestBuilder() //
    .isLoggedInAsAdmin() //
    .withNotification( //
      notificationBuilder() //
        .withFieldValue(url, "http://bjurr.se/") //
        .withFieldValue(events, OPENED.name()) //
        .withFieldValue(proxy_server, "proxyhost") //
        .withFieldValue(proxy_port, "123") //
        .withFieldValue(proxy_user, "user") //
        .withFieldValue(proxy_password, " ") //
        .build() //
    ) //
    .store() //
    .trigger( //
      pullRequestEventBuilder() //
        .withPullRequestAction(OPENED) //
        .build() //
    ) //
    .invokedUrl(0, "http://bjurr.se/") //
    .usedNoProxyAuthentication(0);
 }

 @Test
 public void testThatButtonCanBeUsedForTriggeringEvent() throws Exception {
  prnfbTestBuilder() //
    .isLoggedInAsAdmin() //
    .withNotification( //
      notificationBuilder() //
        .withFieldValue(url, "http://bjurr.se/${" + BUTTON_TRIGGER_TITLE + "}") //
        .withFieldValue(events, BUTTON_TRIGGER) //
        .withFieldValue(FORM_TYPE, TRIGGER_CONFIG_FORM.name()) //
        .build() //
    ) //
    .store() //
    .withNotification( //
      notificationBuilder() //
        .withFieldValue(FORM_IDENTIFIER, "Button Form") //
        .withFieldValue(FORM_TYPE, BUTTON_CONFIG_FORM.name()) //
        .withFieldValue(button_title, "Trigger notification") //
        .withFieldValue(button_visibility, EVERYONE.name()) //
        .build() //
    ) //
    .store() //
    .triggerButton("Button Form") //
    .invokedOnlyUrl("http://bjurr.se/Trigger+notification") //
    .hasButtonsEnabled("Button Form");
 }

 @Test
 public void testThatButtonIsHiddenIfNoConfiguredNotificationForItWhenNotificationSetToOpened() throws Exception {
  prnfbTestBuilder() //
    .isLoggedInAsAdmin() //
    .withNotification( //
      notificationBuilder() //
        .withFieldValue(url, "http://bjurr.se/${" + BUTTON_TRIGGER_TITLE + "}") //
        .withFieldValue(events, OPENED.name()) //
        .withFieldValue(FORM_TYPE, TRIGGER_CONFIG_FORM.name()) //
        .build() //
    ) //
    .store() //
    .withNotification( //
      notificationBuilder() //
        .withFieldValue(FORM_IDENTIFIER, "Button Form") //
        .withFieldValue(FORM_TYPE, BUTTON_CONFIG_FORM.name()) //
        .withFieldValue(button_title, "Trigger notification") //
        .withFieldValue(button_visibility, EVERYONE.name()) //
        .build() //
    ) //
    .store() //
    .triggerButton("Button Form") //
    .hasNoButtonsEnabled();
 }

 @Test
 public void testThatButtonIsHiddenIfNoConfiguredNotificationForItWhenNotificationSetToButtonTriggered()
   throws Exception {
  prnfbTestBuilder() //
    .isLoggedInAsAdmin() //
    .withNotification( //
      notificationBuilder() //
        .withFieldValue(url, "http://bjurr.se/${" + BUTTON_TRIGGER_TITLE + "}") //
        .withFieldValue(events, BUTTON_TRIGGER) //
        .withFieldValue(filter_string, "${" + BUTTON_TRIGGER_TITLE + "}") //
        .withFieldValue(filter_regexp, "123") //
        .withFieldValue(FORM_TYPE, TRIGGER_CONFIG_FORM.name()) //
        .build() //
    ) //
    .store() //
    .withNotification( //
      notificationBuilder() //
        .withFieldValue(FORM_IDENTIFIER, "Button Form") //
        .withFieldValue(FORM_TYPE, BUTTON_CONFIG_FORM.name()) //
        .withFieldValue(button_title, "Trigger notification") //
        .withFieldValue(button_visibility, EVERYONE.name()) //
        .build() //
    ) //
    .store() //
    .triggerButton("Button Form") //
    .hasNoButtonsEnabled();
 }

 @Test
 public void testThatEventTriggeredByButtonCanBeFiltered() throws Exception {
  prnfbTestBuilder() //
    .isLoggedInAsAdmin() //
    .withNotification( //
      notificationBuilder() //
        .withFieldValue(url, "http://bjurr.se/?123=${" + BUTTON_TRIGGER_TITLE + "}") //
        .withFieldValue(events, BUTTON_TRIGGER) //
        .withFieldValue(filter_string, "${" + BUTTON_TRIGGER_TITLE + "}") //
        .withFieldValue(filter_regexp, "123") //
        .withFieldValue(FORM_TYPE, TRIGGER_CONFIG_FORM.name() //
        ) //
        .build() //
    ) //
    .store() //
    .withNotification( //
      notificationBuilder() //
        .withFieldValue(FORM_IDENTIFIER, "Button Form") //
        .withFieldValue(FORM_TYPE, BUTTON_CONFIG_FORM.name()) //
        .withFieldValue(button_title, "button text 123") //
        .withFieldValue(button_visibility, EVERYONE.name()) //
        .build() //
    ) //
    .store() //
    .triggerButton("Button Form") //
    .invokedOnlyUrl("http://bjurr.se/?123=button+text+123");
 }

 @Test
 public void testThatEventTriggeredByButtonCanBeIgnoredByFilterWhileAnotherIsNotIgnored() throws Exception {
  prnfbTestBuilder() //
    .isLoggedInAsAdmin() //
    .withNotification( //
      notificationBuilder() //
        .withFieldValue(url, "http://bjurr.se/?123=${" + BUTTON_TRIGGER_TITLE + "}") //
        .withFieldValue(events, BUTTON_TRIGGER) //
        .withFieldValue(filter_string, "${" + BUTTON_TRIGGER_TITLE + "}") //
        .withFieldValue(filter_regexp, "123") //
        .withFieldValue(FORM_TYPE, TRIGGER_CONFIG_FORM.name()) //
        .build() //
    )//
    .store()//
    .withNotification( //
      notificationBuilder() //
        .withFieldValue(url, "http://bjurr.se/?456=${" + BUTTON_TRIGGER_TITLE + "}") //
        .withFieldValue(events, BUTTON_TRIGGER) //
        .withFieldValue(filter_string, "${" + BUTTON_TRIGGER_TITLE + "}").withFieldValue(filter_regexp, "456") //
        .withFieldValue(FORM_TYPE, TRIGGER_CONFIG_FORM.name()) //
        .build() //
    ) //
    .store() //
    .withNotification(notificationBuilder() //
      .withFieldValue(FORM_IDENTIFIER, "Button Form") //
      .withFieldValue(FORM_TYPE, BUTTON_CONFIG_FORM.name()) //
      .withFieldValue(button_title, "button text 123") //
      .withFieldValue(button_visibility, EVERYONE.name()) //
      .build() //
    ) //
    .store() //
    .triggerButton("Button Form") //
    .invokedOnlyUrl("http://bjurr.se/?123=button+text+123");
 }

 @Test
 public void testThatEventTriggeredByButtonCanResultInSeveralNotifications() throws Exception {
  prnfbTestBuilder() //
    .isLoggedInAsAdmin() //
    .withNotification( //
      notificationBuilder() //
        .withFieldValue(url, "http://bjurr.se/?123=${" + BUTTON_TRIGGER_TITLE + "}") //
        .withFieldValue(events, BUTTON_TRIGGER) //
        .withFieldValue(filter_string, "${" + BUTTON_TRIGGER_TITLE + "}")//
        .withFieldValue(filter_regexp, "button") //
        .withFieldValue(FORM_TYPE, TRIGGER_CONFIG_FORM.name()) //
        .build() //
    ) //
    .store().withNotification( //
      notificationBuilder() //
        .withFieldValue(url, "http://bjurr.se/?456=${" + BUTTON_TRIGGER_TITLE + "}") //
        .withFieldValue(events, BUTTON_TRIGGER) //
        .withFieldValue(filter_string, "${" + BUTTON_TRIGGER_TITLE + "}") //
        .withFieldValue(filter_regexp, "button") //
        .withFieldValue(FORM_TYPE, TRIGGER_CONFIG_FORM.name()).build()) //
    .store().withNotification( //
      notificationBuilder() //
        .withFieldValue(FORM_IDENTIFIER, "Button Form") //
        .withFieldValue(FORM_TYPE, BUTTON_CONFIG_FORM.name()) //
        .withFieldValue(button_title, "button text 123") //
        .withFieldValue(button_visibility, EVERYONE.name()).build()) //
    .store() //
    .triggerButton("Button Form") //
    .invokedUrl(0, "http://bjurr.se/?123=button+text+123") //
    .invokedUrl(1, "http://bjurr.se/?456=button+text+123");
 }

 @Test
 public void testThatThereCanBeSeveralButtons() throws Exception {
  prnfbTestBuilder() //
    .isLoggedInAsAdmin() //
    .withNotification( //
      notificationBuilder() //
        .withFieldValue(url, "http://bjurr.se/?${" + BUTTON_TRIGGER_TITLE + "}") //
        .withFieldValue(events, BUTTON_TRIGGER) //
        .withFieldValue(FORM_TYPE, TRIGGER_CONFIG_FORM.name()) //
        .build() //
    ) //
    .store() //
    .withNotification( //
      notificationBuilder() //
        .withFieldValue(FORM_IDENTIFIER, "Button Form 1") //
        .withFieldValue(FORM_TYPE, BUTTON_CONFIG_FORM.name()) //
        .withFieldValue(button_title, "button text 1") //
        .withFieldValue(button_visibility, EVERYONE.name()) //
        .build() //
    ) //
    .store() //
    .withNotification( //
      notificationBuilder() //
        .withFieldValue(FORM_IDENTIFIER, "Button Form 2") //
        .withFieldValue(FORM_TYPE, BUTTON_CONFIG_FORM.name()) //
        .withFieldValue(button_title, "button text 2") //
        .withFieldValue(button_visibility, EVERYONE.name()) //
        .build() //
    ) //
    .store() //
    .triggerButton("Button Form 1") //
    .invokedUrl(0, "http://bjurr.se/?button+text+1") //
    .triggerButton("Button Form 2") //
    .invokedUrl(1, "http://bjurr.se/?button+text+2");
 }

 @Test
 public void testThatValueFromUrlCanBeUsedInInvocation() throws Exception {
  prnfbTestBuilder() //
    .isLoggedInAsAdmin() //
    .withNotification( //
      notificationBuilder() //
        .withFieldValue(url, "http://bjurr.se/?${" + INJECTION_URL_VALUE + "}") //
        .withFieldValue(events, OPENED.name()) //
        .withFieldValue(FORM_TYPE, TRIGGER_CONFIG_FORM.name()) //
        .withFieldValue(injection_url, "http://bjurr.se/get") //
        .build() //
    ) //
    .store() //
    .withResponse("http://bjurr.se/get", " \n some content \n ") //
    .trigger( //
      pullRequestEventBuilder() //
        .withPullRequestAction(OPENED) //
        .build() //
    ) //
    .invokedOnlyUrl("http://bjurr.se/?some+content");
 }

 @Test
 public void testThatValueFromUrlAndRegexpCanBeUsedInInvocation() throws Exception {
  prnfbTestBuilder() //
    .isLoggedInAsAdmin() //
    .withNotification( //
      notificationBuilder() //
        .withFieldValue(url, "http://bjurr.se/?${" + INJECTION_URL_VALUE + "}") //
        .withFieldValue(events, OPENED.name()) //
        .withFieldValue(FORM_TYPE, TRIGGER_CONFIG_FORM.name()) //
        .withFieldValue(injection_url, "http://bjurr.se/get") //
        .withFieldValue(injection_url_regexp, "<crumb>([^<]*)</crumb>") //
        .build() //
    ) //
    .store() //
    .withResponse(
      "http://bjurr.se/get",
      " \n <defaultCrumbIssuer><crumb>986a2d65b7987258434da5583c9f5337</crumb><crumbRequestField>.crumb</crumbRequestField></defaultCrumbIssuer> \n ") //
    .trigger( //
      pullRequestEventBuilder() //
        .withPullRequestAction(OPENED) //
        .build() //
    ) //
    .invokedOnlyUrl("http://bjurr.se/?986a2d65b7987258434da5583c9f5337");
 }

 @Test
 public void testThatProxyMayNotBeUsedWhenInvokingUrl() throws Exception {
  prnfbTestBuilder()//
    .isLoggedInAsAdmin()//
    .withNotification(//
      notificationBuilder()//
        .withFieldValue(url, "http://bjurr.se/")//
        .withFieldValue(events, OPENED.name())//
        .build()//
    )//
    .store()//
    .trigger(//
      pullRequestEventBuilder()//
        .withPullRequestAction(OPENED)//
        .build()//
    )//
    .invokedUrl(0, "http://bjurr.se/")//
    .usedNoProxy(0);
 }

 @Test
 public void testThatUrlMayBeInvokedWhenIsConflicting() throws Exception {
  prnfbTestBuilder()//
    .isLoggedInAsAdmin()//
    .withNotification(//
      notificationBuilder()//
        .withFieldValue(url, "http://bjurr.se/")//
        .withFieldValue(events, OPENED.name())//
        .withFieldValue(trigger_if_isconflicting, CONFLICTING.name())//
        .build()//
    )//
    .store()//
    .isConflicting()//
    .trigger(//
      pullRequestEventBuilder()//
        .withPullRequestAction(OPENED)//
        .build()//
    )//
    .invokedUrl(0, "http://bjurr.se/");
 }

 @Test
 public void testThatUrlMayNotBeInvokedWhenIsConflicting() throws Exception {
  prnfbTestBuilder()//
    .isLoggedInAsAdmin()//
    .withNotification(//
      notificationBuilder()//
        .withFieldValue(url, "http://bjurr.se/")//
        .withFieldValue(events, OPENED.name())//
        .withFieldValue(trigger_if_isconflicting, NOT_CONFLICTING.name())//
        .build()//
    )//
    .store()//
    .isConflicting()//
    .trigger(//
      pullRequestEventBuilder()//
        .withPullRequestAction(OPENED)//
        .build()//
    )//
    .invokedNoUrl();
 }

 @Test
 public void testThatUrlMayBeInvokedWhenIsNotConflicting() throws Exception {
  prnfbTestBuilder()//
    .isLoggedInAsAdmin()//
    .withNotification(//
      notificationBuilder()//
        .withFieldValue(url, "http://bjurr.se/")//
        .withFieldValue(events, OPENED.name())//
        .withFieldValue(trigger_if_isconflicting, NOT_CONFLICTING.name())//
        .build()//
    )//
    .store()//
    .isNotConflicting()//
    .trigger(//
      pullRequestEventBuilder()//
        .withPullRequestAction(OPENED)//
        .withToRefPullRequestRefBuilder()//
        .build()//
        .build() //
    )//
    .invokedUrl(0, "http://bjurr.se/");
 }

 @Test
 public void testThatUrlMayNotBeInvokedWhenIsNotConflicting() throws Exception {
  prnfbTestBuilder()//
    .isLoggedInAsAdmin()//
    .withNotification(//
      notificationBuilder()//
        .withFieldValue(url, "http://bjurr.se/")//
        .withFieldValue(events, OPENED.name())//
        .withFieldValue(trigger_if_isconflicting, CONFLICTING.name())//
        .build()//
    )//
    .store()//
    .isNotConflicting()//
    .trigger(//
      pullRequestEventBuilder()//
        .withPullRequestAction(OPENED)//
        .build()//
    )//
    .invokedNoUrl();
 }

 @Test
 public void testThatUrlMayBeInvokedWhenIsOrIsNotConflicting() throws Exception {
  prnfbTestBuilder()//
    .isLoggedInAsAdmin()//
    .withNotification(//
      notificationBuilder()//
        .withFieldValue(url, "http://bjurr.se/")//
        .withFieldValue(events, OPENED.name())//
        .withFieldValue(trigger_if_isconflicting, ALWAYS.name())//
        .build()//
    )//
    .store()//
    .trigger(//
      pullRequestEventBuilder()//
        .withPullRequestAction(OPENED)//
        .build()//
    )//
    .invokedUrl(0, "http://bjurr.se/");
 }

 @Test
 public void testThatUrlMayNotBeInvokedWhenIsDeclined() throws Exception {
  prnfbTestBuilder()//
    .isLoggedInAsAdmin()//
    .withNotification(//
      notificationBuilder()//
        .withFieldValue(url, "http://bjurr.se/")//
        .withFieldValue(events, COMMENTED.name())//
        .withFieldValue(trigger_ignore_state, DECLINED.name())//
        .build()//
    )//
    .store()//
    .trigger(//
      pullRequestEventBuilder()//
        .withPullRequestAction(COMMENTED)//
        .withPullRequestInState(DECLINED)//
        .build()//
    )//
    .invokedNoUrl();
 }

 @Test
 public void testThatUrlMayNotBeInvokedWhenIsMerged() throws Exception {
  prnfbTestBuilder()//
    .isLoggedInAsAdmin()//
    .withNotification(//
      notificationBuilder()//
        .withFieldValue(url, "http://bjurr.se/")//
        .withFieldValue(events, COMMENTED.name())//
        .withFieldValue(trigger_ignore_state, PullRequestState.MERGED.name())//
        .build()//
    )//
    .store()//
    .trigger(//
      pullRequestEventBuilder()//
        .withPullRequestAction(COMMENTED)//
        .withPullRequestInState(PullRequestState.MERGED)//
        .build()//
    )//
    .invokedNoUrl();
 }

 @Test
 public void testThatUrlMayBeInvokedWhenIsMergedAndDeclinedIsIgnored() throws Exception {
  prnfbTestBuilder()//
    .isLoggedInAsAdmin()//
    .withNotification(//
      notificationBuilder()//
        .withFieldValue(url, "http://bjurr.se/")//
        .withFieldValue(events, COMMENTED.name())//
        .withFieldValue(trigger_ignore_state, DECLINED.name())//
        .build()//
    )//
    .store()//
    .trigger(//
      pullRequestEventBuilder()//
        .withPullRequestAction(COMMENTED)//
        .withPullRequestInState(PullRequestState.MERGED)//
        .build()//
    )//
    .invokedUrl(0, "http://bjurr.se/");
 }

 @Test
 public void testThatVariablesAreImplementedForBothFromAndTo() throws Exception {
  final List<String> from = newArrayList();
  final List<String> to = newArrayList();
  for (final PrnfbVariable prnfbVariable : PrnfbVariable.values()) {
   if (prnfbVariable.name().contains("_FROM_")) {
    from.add(prnfbVariable.name());
   } else if (prnfbVariable.name().contains("_TO_")) {
    to.add(prnfbVariable.name());
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
  for (final PrnfbVariable prnfbVariable : PrnfbVariable.values()) {
   assertTrue(prnfbVariable.name() + " in " + resource.toString(), adminVmContent.contains(prnfbVariable.name()));
  }
 }

 @Test
 public void testThatEventsAreMentionedInAdminGUI() throws IOException {
  final URL resource = getResource("admin.vm");
  final String adminVmContent = Resources.toString(resource, UTF_8);
  for (final PrnfbPullRequestAction prnfbAction : PrnfbPullRequestAction.values()) {
   assertTrue(prnfbAction.getName() + " in " + resource.toString(), adminVmContent.contains(prnfbAction.getName()));
  }
 }
}
