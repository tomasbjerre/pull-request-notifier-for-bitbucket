package se.bjurr.prnfs.listener;

import static com.google.common.base.Optional.absent;
import static com.google.common.base.Optional.of;
import static com.google.common.collect.Maps.newHashMap;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.util.logging.Level.SEVERE;
import static java.util.logging.Logger.getLogger;
import static java.util.regex.Pattern.compile;
import static se.bjurr.prnfs.admin.AdminFormValues.TRIGGER_IF_MERGE.ALWAYS;
import static se.bjurr.prnfs.admin.AdminFormValues.TRIGGER_IF_MERGE.CONFLICTING;
import static se.bjurr.prnfs.admin.AdminFormValues.TRIGGER_IF_MERGE.NOT_CONFLICTING;
import static se.bjurr.prnfs.listener.PrnfsPullRequestAction.fromPullRequestEvent;
import static se.bjurr.prnfs.listener.PrnfsRenderer.PrnfsVariable.PULL_REQUEST_COMMENT_TEXT;
import static se.bjurr.prnfs.listener.UrlInvoker.urlInvoker;
import static se.bjurr.prnfs.settings.SettingsStorage.getPrnfsSettings;

import java.util.Map;
import java.util.logging.Logger;

import se.bjurr.prnfs.listener.PrnfsRenderer.PrnfsVariable;
import se.bjurr.prnfs.settings.Header;
import se.bjurr.prnfs.settings.PrnfsNotification;
import se.bjurr.prnfs.settings.PrnfsSettings;
import se.bjurr.prnfs.settings.ValidationException;

import com.atlassian.event.api.EventListener;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.stash.event.pull.PullRequestApprovedEvent;
import com.atlassian.stash.event.pull.PullRequestCommentAddedEvent;
import com.atlassian.stash.event.pull.PullRequestCommentEvent;
import com.atlassian.stash.event.pull.PullRequestCommentRepliedEvent;
import com.atlassian.stash.event.pull.PullRequestDeclinedEvent;
import com.atlassian.stash.event.pull.PullRequestEvent;
import com.atlassian.stash.event.pull.PullRequestMergedEvent;
import com.atlassian.stash.event.pull.PullRequestOpenedEvent;
import com.atlassian.stash.event.pull.PullRequestReopenedEvent;
import com.atlassian.stash.event.pull.PullRequestRescopedEvent;
import com.atlassian.stash.event.pull.PullRequestUnapprovedEvent;
import com.atlassian.stash.event.pull.PullRequestUpdatedEvent;
import com.atlassian.stash.pull.PullRequest;
import com.atlassian.stash.pull.PullRequestService;
import com.atlassian.stash.repository.RepositoryService;
import com.atlassian.stash.server.ApplicationPropertiesService;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Optional;
import com.google.common.base.Supplier;

public class PrnfsPullRequestEventListener {

 private final PluginSettingsFactory pluginSettingsFactory;
 private final RepositoryService repositoryService;
 private final ApplicationPropertiesService propertiesService;
 private final PullRequestService pullRequestService;
 private static final Logger logger = getLogger(PrnfsPullRequestEventListener.class.getName());

 private static Invoker invoker = new Invoker() {
  @Override
  public void invoke(UrlInvoker urlInvoker) {
   urlInvoker.invoke();
  }
 };

 @VisibleForTesting
 public static void setInvoker(Invoker invoker) {
  PrnfsPullRequestEventListener.invoker = invoker;
 }

 public PrnfsPullRequestEventListener(PluginSettingsFactory pluginSettingsFactory, RepositoryService repositoryService,
   ApplicationPropertiesService propertiesService, PullRequestService pullRequestService) {
  this.pluginSettingsFactory = pluginSettingsFactory;
  this.repositoryService = repositoryService;
  this.propertiesService = propertiesService;
  this.pullRequestService = pullRequestService;
 }

 @EventListener
 public void onEvent(PullRequestApprovedEvent e) {
  handleEvent(e);
 }

 @EventListener
 public void onEvent(PullRequestCommentAddedEvent e) {
  handleEvent(e);
 }

 @EventListener
 public void onEvent(PullRequestCommentRepliedEvent e) {
  handleEvent(e);
 }

 @EventListener
 public void onEvent(PullRequestDeclinedEvent e) {
  handleEvent(e);
 }

 @EventListener
 public void onEvent(PullRequestMergedEvent e) {
  handleEvent(e);
 }

 @EventListener
 public void onEvent(PullRequestOpenedEvent e) {
  handleEvent(e);
 }

 @EventListener
 public void onEvent(PullRequestReopenedEvent e) {
  handleEvent(e);
 }

 @EventListener
 public void onEvent(final PullRequestRescopedEvent e) {
  handleEvent(e);
 }

 @EventListener
 public void onEvent(PullRequestUnapprovedEvent e) {
  handleEvent(e);
 }

 @EventListener
 public void onEvent(PullRequestUpdatedEvent e) {
  handleEvent(e);
 }

 @VisibleForTesting
 public void handleEvent(final PullRequestEvent pullRequestEvent) {
  try {
   if (pullRequestEvent.getPullRequest().isClosed() && pullRequestEvent instanceof PullRequestCommentEvent) {
    return;
   }
   final PrnfsSettings settings = getPrnfsSettings(pluginSettingsFactory.createGlobalSettings());
   for (final PrnfsNotification notification : settings.getNotifications()) {
    PrnfsPullRequestAction action = fromPullRequestEvent(pullRequestEvent, notification);
    Map<PrnfsVariable, Supplier<String>> variables = newHashMap();
    if (pullRequestEvent instanceof PullRequestCommentAddedEvent) {
     variables.put(PULL_REQUEST_COMMENT_TEXT, new Supplier<String>() {
      @Override
      public String get() {
       return ((PullRequestCommentAddedEvent) pullRequestEvent).getComment().getText();
      }
     });
    }
    PrnfsRenderer renderer = new PrnfsRenderer(pullRequestEvent.getPullRequest(), action, pullRequestEvent.getUser(),
      repositoryService, propertiesService, notification, variables);
    notify(notification, action, pullRequestEvent.getPullRequest(), variables, renderer);
   }
  } catch (final ValidationException e) {
   logger.log(SEVERE, "", e);
  }
 }

 @SuppressWarnings("deprecation")
 public void notify(final PrnfsNotification notification, PrnfsPullRequestAction pullRequestAction,
   PullRequest pullRequest, Map<PrnfsVariable, Supplier<String>> variables, PrnfsRenderer renderer) {
  if (!notificationTriggeredByAction(notification, pullRequestAction, renderer, pullRequest)) {
   return;
  }

  Optional<String> postContent = absent();
  if (notification.getPostContent().isPresent()) {
   postContent = of(renderer.render(notification.getPostContent().get()));
  }
  String renderedUrl = renderer.render(notification.getUrl());
  logger.info(notification.getName() + " > " //
    + pullRequest.getFromRef().getId() + "(" + pullRequest.getFromRef().getLatestChangeset() + ") -> " //
    + pullRequest.getToRef().getId() + "(" + pullRequest.getToRef().getLatestChangeset() + ")" + " " //
    + renderedUrl);
  UrlInvoker urlInvoker = urlInvoker()//
    .withUrlParam(renderedUrl)//
    .withMethod(notification.getMethod())//
    .withPostContent(postContent)//
    .appendBasicAuth(notification);

  for (Header header : notification.getHeaders()) {
   urlInvoker//
     .withHeader(header.getName(), renderer.render(header.getValue()));
  }
  invoker.invoke(urlInvoker//
    .withProxyServer(notification.getProxyServer()) //
    .withProxyPort(notification.getProxyPort())//
    .withProxyUser(notification.getProxyUser())//
    .withProxyPassword(notification.getProxyPassword()));
 }

 public boolean notificationTriggeredByAction(PrnfsNotification notification, PrnfsPullRequestAction pullRequestAction,
   PrnfsRenderer renderer, PullRequest pullRequest) {
  if (!notification.getTriggers().contains(pullRequestAction)) {
   return FALSE;
  }
  if (notification.getFilterRegexp().isPresent()
    && notification.getFilterString().isPresent()
    && !compile(notification.getFilterRegexp().get()).matcher(renderer.render(notification.getFilterString().get()))
      .find()) {
   return FALSE;
  }

  if (notification.getTriggerIfCanMerge() != ALWAYS && pullRequest.isOpen()) {
   // Cannot perform canMerge unless PR is open
   boolean isConflicted = pullRequestService.canMerge(pullRequest.getToRef().getRepository().getId(),
     pullRequest.getId()).isConflicted();
   if (notification.getTriggerIfCanMerge() == NOT_CONFLICTING && isConflicted || //
     notification.getTriggerIfCanMerge() == CONFLICTING && !isConflicted) {
    return FALSE;
   }
  }

  return TRUE;
 }
}