package se.bjurr.prnfb.listener;

import static com.google.common.base.Optional.absent;
import static com.google.common.base.Optional.of;
import static com.google.common.collect.Maps.newHashMap;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.util.regex.Pattern.compile;
import static org.slf4j.LoggerFactory.getLogger;
import static se.bjurr.prnfb.http.UrlInvoker.urlInvoker;
import static se.bjurr.prnfb.listener.PrnfbPullRequestAction.fromPullRequestEvent;
import static se.bjurr.prnfb.service.PrnfbVariable.PULL_REQUEST_COMMENT_TEXT;
import static se.bjurr.prnfb.service.PrnfbVariable.PULL_REQUEST_MERGE_COMMIT;
import static se.bjurr.prnfb.settings.TRIGGER_IF_MERGE.ALWAYS;
import static se.bjurr.prnfb.settings.TRIGGER_IF_MERGE.CONFLICTING;
import static se.bjurr.prnfb.settings.TRIGGER_IF_MERGE.NOT_CONFLICTING;

import java.util.Map;
import java.util.concurrent.ExecutorService;

import org.slf4j.Logger;

import se.bjurr.prnfb.http.ClientKeyStore;
import se.bjurr.prnfb.http.Invoker;
import se.bjurr.prnfb.http.UrlInvoker;
import se.bjurr.prnfb.service.PrnfbRenderer;
import se.bjurr.prnfb.service.PrnfbRendererFactory;
import se.bjurr.prnfb.service.PrnfbVariable;
import se.bjurr.prnfb.service.SettingsService;
import se.bjurr.prnfb.settings.PrnfbHeader;
import se.bjurr.prnfb.settings.PrnfbNotification;
import se.bjurr.prnfb.settings.PrnfbSettingsData;
import se.bjurr.prnfb.settings.TRIGGER_IF_MERGE;

import com.atlassian.bitbucket.event.pull.PullRequestApprovedEvent;
import com.atlassian.bitbucket.event.pull.PullRequestCommentAddedEvent;
import com.atlassian.bitbucket.event.pull.PullRequestCommentEvent;
import com.atlassian.bitbucket.event.pull.PullRequestCommentRepliedEvent;
import com.atlassian.bitbucket.event.pull.PullRequestDeclinedEvent;
import com.atlassian.bitbucket.event.pull.PullRequestEvent;
import com.atlassian.bitbucket.event.pull.PullRequestMergedEvent;
import com.atlassian.bitbucket.event.pull.PullRequestOpenedEvent;
import com.atlassian.bitbucket.event.pull.PullRequestReopenedEvent;
import com.atlassian.bitbucket.event.pull.PullRequestRescopedEvent;
import com.atlassian.bitbucket.event.pull.PullRequestUnapprovedEvent;
import com.atlassian.bitbucket.event.pull.PullRequestUpdatedEvent;
import com.atlassian.bitbucket.pull.PullRequest;
import com.atlassian.bitbucket.pull.PullRequestService;
import com.atlassian.event.api.EventListener;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Optional;
import com.google.common.base.Supplier;

public class PrnfbPullRequestEventListener {

 private static final Logger LOG = getLogger(PrnfbPullRequestEventListener.class);
 private static Invoker mockedInvoker = null;

 @VisibleForTesting
 public static void setInvoker(Invoker invoker) {
  PrnfbPullRequestEventListener.mockedInvoker = invoker;
 }

 private final ExecutorService executorService;
 private final PrnfbRendererFactory prnfbRendererFactory;
 private final PullRequestService pullRequestService;

 private final SettingsService settingsService;

 public PrnfbPullRequestEventListener(PrnfbRendererFactory prnfbRendererFactory, PullRequestService pullRequestService,
   ExecutorService executorService, SettingsService settingsService) {
  this.prnfbRendererFactory = prnfbRendererFactory;
  this.pullRequestService = pullRequestService;
  this.executorService = executorService;
  this.settingsService = settingsService;
 }

 @VisibleForTesting
 public void handleEventAsync(final PullRequestEvent pullRequestEvent) {
  this.executorService.execute(new Runnable() {
   @Override
   public void run() {
    handleEvent(pullRequestEvent);
   }
  });
 }

 public boolean isNotificationTriggeredByAction(PrnfbNotification notification,
   PrnfbPullRequestAction pullRequestAction, PrnfbRenderer renderer, PullRequest pullRequest,
   ClientKeyStore clientKeyStore, Boolean shouldAcceptAnyCertificate) {
  if (!notification.getTriggers().contains(pullRequestAction)) {
   return FALSE;
  }

  if (notification.getProjectKey().isPresent()) {
   if (!notification.getProjectKey().get().equals(pullRequest.getToRef().getRepository().getProject().getKey())) {
    return FALSE;
   }
  }

  if (notification.getRepositorySlug().isPresent()) {
   if (!notification.getRepositorySlug().get().equals(pullRequest.getToRef().getRepository().getSlug())) {
    return FALSE;
   }
  }

  if (notification.getFilterRegexp().isPresent()
    && notification.getFilterString().isPresent()
    && !compile(notification.getFilterRegexp().get()).matcher(
      renderer.render(notification.getFilterString().get(), FALSE, clientKeyStore, shouldAcceptAnyCertificate)).find()) {
   return FALSE;
  }

  if (notification.getTriggerIgnoreStateList().contains(pullRequest.getState())) {
   return FALSE;
  }

  if (notification.getTriggerIfCanMerge() != ALWAYS && pullRequest.isOpen()) {
   // Cannot perform canMerge unless PR is open
   boolean isConflicted = this.pullRequestService.canMerge(pullRequest.getToRef().getRepository().getId(),
     pullRequest.getId()).isConflicted();
   if (ignoreBecauseOfConflicting(notification.getTriggerIfCanMerge(), isConflicted)) {
    return FALSE;
   }
  }

  return TRUE;
 }

 public void notify(final PrnfbNotification notification, PrnfbPullRequestAction pullRequestAction,
   PullRequest pullRequest, PrnfbRenderer renderer, ClientKeyStore clientKeyStore, Boolean shouldAcceptAnyCertificate) {
  if (!isNotificationTriggeredByAction(notification, pullRequestAction, renderer, pullRequest, clientKeyStore,
    shouldAcceptAnyCertificate)) {
   return;
  }

  Optional<String> postContent = absent();
  if (notification.getPostContent().isPresent()) {
   postContent = of(renderer.render(notification.getPostContent().get(), FALSE, clientKeyStore,
     shouldAcceptAnyCertificate));
  }
  String renderedUrl = renderer.render(notification.getUrl(), TRUE, clientKeyStore, shouldAcceptAnyCertificate);
  LOG.info(notification.getName() + " > " //
    + pullRequest.getFromRef().getId() + "(" + pullRequest.getFromRef().getLatestCommit() + ") -> " //
    + pullRequest.getToRef().getId() + "(" + pullRequest.getToRef().getLatestCommit() + ")" + " " //
    + renderedUrl);
  UrlInvoker urlInvoker = urlInvoker()//
    .withClientKeyStore(clientKeyStore)//
    .withUrlParam(renderedUrl)//
    .withMethod(notification.getMethod())//
    .withPostContent(postContent)//
    .appendBasicAuth(notification);
  for (PrnfbHeader header : notification.getHeaders()) {
   urlInvoker//
     .withHeader(header.getName(),
       renderer.render(header.getValue(), FALSE, clientKeyStore, shouldAcceptAnyCertificate));
  }
  createInvoker().invoke(urlInvoker//
    .withProxyServer(notification.getProxyServer()) //
    .withProxyPort(notification.getProxyPort())//
    .withProxyUser(notification.getProxyUser())//
    .withProxyPassword(notification.getProxyPassword())//
    .shouldAcceptAnyCertificate(shouldAcceptAnyCertificate));
 }

 @EventListener
 public void onEvent(@SuppressWarnings("deprecation") PullRequestApprovedEvent e) {
  handleEventAsync(e);
 }

 @EventListener
 public void onEvent(PullRequestCommentAddedEvent e) {
  handleEventAsync(e);
 }

 @EventListener
 public void onEvent(PullRequestCommentRepliedEvent e) {
  handleEventAsync(e);
 }

 @EventListener
 public void onEvent(PullRequestDeclinedEvent e) {
  handleEventAsync(e);
 }

 @EventListener
 public void onEvent(PullRequestMergedEvent e) {
  handleEventAsync(e);
 }

 @EventListener
 public void onEvent(PullRequestOpenedEvent e) {
  handleEventAsync(e);
 }

 @EventListener
 public void onEvent(PullRequestReopenedEvent e) {
  handleEventAsync(e);
 }

 @EventListener
 public void onEvent(final PullRequestRescopedEvent e) {
  handleEventAsync(e);
 }

 @EventListener
 public void onEvent(@SuppressWarnings("deprecation") PullRequestUnapprovedEvent e) {
  handleEventAsync(e);
 }

 @EventListener
 public void onEvent(PullRequestUpdatedEvent e) {
  handleEventAsync(e);
 }

 private Invoker createInvoker() {
  if (mockedInvoker != null) {
   return mockedInvoker;
  }
  return new Invoker() {
   @Override
   public void invoke(UrlInvoker urlInvoker) {
    urlInvoker.invoke();
   }
  };
 }

 private void handleEvent(final PullRequestEvent pullRequestEvent) {
  if (pullRequestEvent.getPullRequest().isClosed() && pullRequestEvent instanceof PullRequestCommentEvent) {
   return;
  }
  final PrnfbSettingsData settings = this.settingsService.getPrnfbSettingsData();
  ClientKeyStore clientKeyStore = new ClientKeyStore(settings);
  for (final PrnfbNotification notification : this.settingsService.getNotifications()) {
   PrnfbPullRequestAction action = fromPullRequestEvent(pullRequestEvent, notification);
   Map<PrnfbVariable, Supplier<String>> variables = populateVariables(pullRequestEvent);
   PrnfbRenderer renderer = this.prnfbRendererFactory.create(pullRequestEvent.getPullRequest(), action, notification,
     variables, pullRequestEvent.getUser());
   notify(notification, action, pullRequestEvent.getPullRequest(), renderer, clientKeyStore,
     settings.isShouldAcceptAnyCertificate());
  }
 }

 @VisibleForTesting
 boolean ignoreBecauseOfConflicting(TRIGGER_IF_MERGE triggerIfCanMerge, boolean isConflicted) {
  return triggerIfCanMerge == NOT_CONFLICTING && isConflicted || //
    triggerIfCanMerge == CONFLICTING && !isConflicted;
 }

 @VisibleForTesting
 Map<PrnfbVariable, Supplier<String>> populateVariables(final PullRequestEvent pullRequestEvent) {
  Map<PrnfbVariable, Supplier<String>> variables = newHashMap();
  if (pullRequestEvent instanceof PullRequestCommentAddedEvent) {
   variables.put(PULL_REQUEST_COMMENT_TEXT, () -> ((PullRequestCommentAddedEvent) pullRequestEvent).getComment()
     .getText());
  } else if (pullRequestEvent instanceof PullRequestMergedEvent) {
   variables.put(PULL_REQUEST_MERGE_COMMIT, new Supplier<String>() {
    @Override
    public String get() {
     return ((PullRequestMergedEvent) pullRequestEvent).getCommit().getId();
    }
   });
  }
  return variables;
 }

}