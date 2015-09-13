package se.bjurr.prnfb.listener;

import static com.google.common.base.Charsets.UTF_8;
import static com.google.common.base.Optional.absent;
import static com.google.common.collect.Maps.newHashMap;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.util.logging.Level.SEVERE;
import static java.util.logging.Logger.getLogger;
import static java.util.regex.Pattern.compile;
import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.xml.bind.DatatypeConverter.printBase64Binary;
import static se.bjurr.prnfb.admin.AdminFormValues.TRIGGER_IF_MERGE.ALWAYS;
import static se.bjurr.prnfb.admin.AdminFormValues.TRIGGER_IF_MERGE.CONFLICTING;
import static se.bjurr.prnfb.admin.AdminFormValues.TRIGGER_IF_MERGE.NOT_CONFLICTING;
import static se.bjurr.prnfb.listener.PrnfbPullRequestAction.fromPullRequestEvent;
import static se.bjurr.prnfb.listener.PrnfbRenderer.PrnfbVariable.PULL_REQUEST_COMMENT_TEXT;
import static se.bjurr.prnfb.listener.UrlInvoker.urlInvoker;
import static se.bjurr.prnfb.settings.SettingsStorage.getPrnfbSettings;

import java.util.Map;
import java.util.logging.Logger;

import se.bjurr.prnfb.listener.PrnfbRenderer.PrnfbVariable;
import se.bjurr.prnfb.settings.Header;
import se.bjurr.prnfb.settings.PrnfbNotification;
import se.bjurr.prnfb.settings.PrnfbSettings;
import se.bjurr.prnfb.settings.ValidationException;

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
import com.atlassian.bitbucket.repository.RepositoryService;
import com.atlassian.bitbucket.server.ApplicationPropertiesService;
import com.atlassian.event.api.EventListener;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Optional;
import com.google.common.base.Supplier;

public class PrnfbPullRequestEventListener {

 private final PluginSettingsFactory pluginSettingsFactory;
 private final RepositoryService repositoryService;
 private final ApplicationPropertiesService propertiesService;
 private final PullRequestService pullRequestService;
 private static final Logger logger = getLogger(PrnfbPullRequestEventListener.class.getName());

 private static Invoker invoker = urlInvoker -> urlInvoker.invoke();

 @VisibleForTesting
 public static void setInvoker(Invoker invoker) {
  PrnfbPullRequestEventListener.invoker = invoker;
 }

 public PrnfbPullRequestEventListener(PluginSettingsFactory pluginSettingsFactory, RepositoryService repositoryService,
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
   final PrnfbSettings settings = getPrnfbSettings(pluginSettingsFactory.createGlobalSettings());
   for (final PrnfbNotification notification : settings.getNotifications()) {
    PrnfbPullRequestAction action = fromPullRequestEvent(pullRequestEvent, notification);
    Map<PrnfbVariable, Supplier<String>> variables = newHashMap();
    if (pullRequestEvent instanceof PullRequestCommentAddedEvent) {
     variables.put(PULL_REQUEST_COMMENT_TEXT, () -> ((PullRequestCommentAddedEvent) pullRequestEvent).getComment()
       .getText());
    }
    PrnfbRenderer renderer = new PrnfbRenderer(pullRequestEvent.getPullRequest(), action, pullRequestEvent.getUser(),
      repositoryService, propertiesService, notification, variables);
    notify(notification, action, pullRequestEvent.getPullRequest(), variables, renderer);
   }
  } catch (final ValidationException e) {
   logger.log(SEVERE, "", e);
  }
 }

 public void notify(final PrnfbNotification notification, PrnfbPullRequestAction pullRequestAction,
   PullRequest pullRequest, Map<PrnfbVariable, Supplier<String>> variables, PrnfbRenderer renderer) {
  if (!notificationTriggeredByAction(notification, pullRequestAction, renderer, pullRequest)) {
   return;
  }

  Optional<String> postContent = absent();
  if (notification.getPostContent().isPresent()) {
   postContent = Optional.of(renderer.render(notification.getPostContent().get()));
  }
  String renderedUrl = renderer.render(notification.getUrl());
  logger.info(notification.getName() + " > " //
    + pullRequest.getFromRef().getId() + "(" + pullRequest.getFromRef().getLatestCommit() + ") -> " //
    + pullRequest.getToRef().getId() + "(" + pullRequest.getToRef().getLatestCommit() + ")" + " " //
    + renderedUrl);
  UrlInvoker urlInvoker = urlInvoker().withUrlParam(renderedUrl).withMethod(notification.getMethod())
    .withPostContent(postContent);
  if (notification.getUser().isPresent() && notification.getPassword().isPresent()) {
   final String userpass = notification.getUser().get() + ":" + notification.getPassword().get();
   final String basicAuth = "Basic " + new String(printBase64Binary(userpass.getBytes(UTF_8)));
   urlInvoker.withHeader(AUTHORIZATION, basicAuth);
  }
  for (Header header : notification.getHeaders()) {
   urlInvoker.withHeader(header.getName(), renderer.render(header.getValue()));
  }
  invoker.invoke(urlInvoker.withProxyServer(notification.getProxyServer()) //
    .withProxyPort(notification.getProxyPort())//
    .withProxyUser(notification.getProxyUser())//
    .withProxyPassword(notification.getProxyPassword()));
 }

 public boolean notificationTriggeredByAction(PrnfbNotification notification, PrnfbPullRequestAction pullRequestAction,
   PrnfbRenderer renderer, PullRequest pullRequest) {
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