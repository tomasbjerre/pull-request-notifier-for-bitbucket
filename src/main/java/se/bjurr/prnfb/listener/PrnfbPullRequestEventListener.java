package se.bjurr.prnfb.listener;

import static com.atlassian.bitbucket.permission.Permission.ADMIN;
import static com.google.common.base.Optional.absent;
import static com.google.common.base.Optional.of;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.util.regex.Pattern.compile;
import static org.slf4j.LoggerFactory.getLogger;
import static se.bjurr.prnfb.http.UrlInvoker.urlInvoker;
import static se.bjurr.prnfb.listener.PrnfbPullRequestAction.fromPullRequestEvent;
import static se.bjurr.prnfb.settings.TRIGGER_IF_MERGE.ALWAYS;
import static se.bjurr.prnfb.settings.TRIGGER_IF_MERGE.CONFLICTING;
import static se.bjurr.prnfb.settings.TRIGGER_IF_MERGE.NOT_CONFLICTING;

import java.util.concurrent.ExecutorService;

import org.slf4j.Logger;

import se.bjurr.prnfb.http.ClientKeyStore;
import se.bjurr.prnfb.http.HttpResponse;
import se.bjurr.prnfb.http.Invoker;
import se.bjurr.prnfb.http.NotificationResponse;
import se.bjurr.prnfb.http.UrlInvoker;
import se.bjurr.prnfb.service.PrnfbRenderer;
import se.bjurr.prnfb.service.PrnfbRenderer.ENCODE_FOR;
import se.bjurr.prnfb.service.PrnfbRendererFactory;
import se.bjurr.prnfb.service.SettingsService;
import se.bjurr.prnfb.service.VariablesContext;
import se.bjurr.prnfb.service.VariablesContext.VariablesContextBuilder;
import se.bjurr.prnfb.settings.PrnfbHeader;
import se.bjurr.prnfb.settings.PrnfbNotification;
import se.bjurr.prnfb.settings.PrnfbSettingsData;
import se.bjurr.prnfb.settings.TRIGGER_IF_MERGE;

import com.atlassian.bitbucket.ServiceException;
import com.atlassian.bitbucket.event.pull.PullRequestCommentAddedEvent;
import com.atlassian.bitbucket.event.pull.PullRequestCommentDeletedEvent;
import com.atlassian.bitbucket.event.pull.PullRequestCommentEditedEvent;
import com.atlassian.bitbucket.event.pull.PullRequestCommentEvent;
import com.atlassian.bitbucket.event.pull.PullRequestCommentRepliedEvent;
import com.atlassian.bitbucket.event.pull.PullRequestDeclinedEvent;
import com.atlassian.bitbucket.event.pull.PullRequestDeletedEvent;
import com.atlassian.bitbucket.event.pull.PullRequestEvent;
import com.atlassian.bitbucket.event.pull.PullRequestMergedEvent;
import com.atlassian.bitbucket.event.pull.PullRequestOpenedEvent;
import com.atlassian.bitbucket.event.pull.PullRequestParticipantStatusUpdatedEvent;
import com.atlassian.bitbucket.event.pull.PullRequestReopenedEvent;
import com.atlassian.bitbucket.event.pull.PullRequestRescopedEvent;
import com.atlassian.bitbucket.event.pull.PullRequestUpdatedEvent;
import com.atlassian.bitbucket.pull.PullRequest;
import com.atlassian.bitbucket.pull.PullRequestAction;
import com.atlassian.bitbucket.pull.PullRequestService;
import com.atlassian.bitbucket.scm.Command;
import com.atlassian.bitbucket.scm.ScmService;
import com.atlassian.bitbucket.scm.pull.ScmPullRequestCommandFactory;
import com.atlassian.bitbucket.user.SecurityService;
import com.atlassian.bitbucket.util.Operation;
import com.atlassian.event.api.EventListener;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Optional;

public class PrnfbPullRequestEventListener {

  private static final Logger LOG = getLogger(PrnfbPullRequestEventListener.class);
  private static Invoker mockedInvoker = null;

  @VisibleForTesting
  public static void setInvoker(final Invoker invoker) {
    PrnfbPullRequestEventListener.mockedInvoker = invoker;
  }

  private final ExecutorService executorService;
  private final PrnfbRendererFactory prnfbRendererFactory;
  private final PullRequestService pullRequestService;
  private final SecurityService securityService;
  private final ScmService scmService;

  private final SettingsService settingsService;

  public PrnfbPullRequestEventListener(
      final PrnfbRendererFactory prnfbRendererFactory,
      final PullRequestService pullRequestService,
      final ExecutorService executorService,
      final SettingsService settingsService,
      final SecurityService securityService,
      final ScmService scmService) {
    this.prnfbRendererFactory = prnfbRendererFactory;
    this.pullRequestService = pullRequestService;
    this.executorService = executorService;
    this.settingsService = settingsService;
    this.securityService = securityService;
    this.scmService = scmService;
  }

  private Invoker createInvoker() {
    if (mockedInvoker != null) {
      return mockedInvoker;
    }
    return new Invoker() {
      @Override
      public HttpResponse invoke(final UrlInvoker urlInvoker) {
        return urlInvoker.invoke();
      }
    };
  }

  private void handleEvent(final PullRequestEvent pullRequestEvent) {

    final PullRequest pullRequest = pullRequestEvent.getPullRequest();
    final PrnfbSettingsData settings = settingsService.getPrnfbSettingsData();
    final ClientKeyStore clientKeyStore = new ClientKeyStore(settings);

    if (pullRequest.isClosed() && pullRequestEvent instanceof PullRequestCommentEvent) {
      return;
    }

    for (final PrnfbNotification notification : settingsService.getNotifications()) {
      boolean mergePerformed = false;
      try {
        if (!pullRequest.isClosed()
            && pullRequestEvent.getAction().equals(PullRequestAction.RESCOPED)
            && notification.isUpdatePullRequestRefs()
            && !mergePerformed) {
          mergePerformed = true;
          try {
            final ScmPullRequestCommandFactory scmPullRequestCommandFactory =
                scmService.getPullRequestCommandFactory(pullRequest);
            Command<?> command;
            command = scmPullRequestCommandFactory.tryMerge(pullRequest);
            command.call();
          } catch (final ServiceException se) {
            LOG.warn("Merge check failed " + pullRequest, se);
          }
        }

        handleEventNotification(pullRequestEvent, settings, clientKeyStore, notification);
      } catch (final Exception e) {
        LOG.error(
            "Unable to handle notification "
                + notification.getUuid()
                + " "
                + notification.getName(),
            e);
      }
    }
  }

  private void handleEventNotification(
      final PullRequestEvent pullRequestEvent,
      final PrnfbSettingsData settings,
      final ClientKeyStore clientKeyStore,
      final PrnfbNotification notification) {
    final PrnfbPullRequestAction action = fromPullRequestEvent(pullRequestEvent, notification);
    final VariablesContext variables =
        new VariablesContextBuilder() //
            .setPullRequestEvent(pullRequestEvent) //
            .build();
    final PrnfbRenderer renderer =
        prnfbRendererFactory.create(
            pullRequestEvent.getPullRequest(),
            action,
            notification,
            variables,
            pullRequestEvent.getUser());
    notify(
        notification,
        action,
        pullRequestEvent.getPullRequest(),
        renderer,
        clientKeyStore,
        settings.isShouldAcceptAnyCertificate());
  }

  @VisibleForTesting
  public void handleEventAsync(final PullRequestEvent pullRequestEvent) {
    executorService.execute(
        new Runnable() {
          @Override
          public void run() {
            handleEvent(pullRequestEvent);
          }
        });
  }

  @VisibleForTesting
  boolean ignoreBecauseOfConflicting(
      final TRIGGER_IF_MERGE triggerIfCanMerge, final boolean isConflicted) {
    return triggerIfCanMerge == NOT_CONFLICTING && isConflicted
        || //
        triggerIfCanMerge == CONFLICTING && !isConflicted;
  }

  public boolean isNotificationTriggeredByAction(
      final PrnfbNotification notification,
      final PrnfbPullRequestAction pullRequestAction,
      final PrnfbRenderer renderer,
      final PullRequest pullRequest,
      final ClientKeyStore clientKeyStore,
      final Boolean shouldAcceptAnyCertificate) {
    if (!notification.getTriggers().contains(pullRequestAction)) {
      return FALSE;
    }

    if (notification.getProjectKey().isPresent()) {
      if (!notification
          .getProjectKey()
          .get()
          .equals(pullRequest.getToRef().getRepository().getProject().getKey())) {
        return FALSE;
      }
    }

    if (notification.getRepositorySlug().isPresent()) {
      if (!notification
          .getRepositorySlug()
          .get()
          .equals(pullRequest.getToRef().getRepository().getSlug())) {
        return FALSE;
      }
    }

    if (notification.getFilterRegexp().isPresent()
        && notification.getFilterString().isPresent()
        && !compile(notification.getFilterRegexp().get())
            .matcher(
                renderer.render(
                    notification.getFilterString().get(),
                    ENCODE_FOR.NONE,
                    clientKeyStore,
                    shouldAcceptAnyCertificate))
            .find()) {
      return FALSE;
    }

    if (notification.getTriggerIgnoreStateList().contains(pullRequest.getState())) {
      return FALSE;
    }

    if (notification.getTriggerIfCanMerge() != ALWAYS) {
      // Cannot perform canMerge unless PR is open
      final boolean notYetMerged = pullRequest.isOpen();
      final boolean isConflicted = notYetMerged && hasConflicts(pullRequest);
      if (ignoreBecauseOfConflicting(notification.getTriggerIfCanMerge(), isConflicted)) {
        return FALSE;
      }
    }

    return TRUE;
  }

  private boolean hasConflicts(final PullRequest pullRequest) {
    return securityService //
        .withPermission(ADMIN, "Can merge") //
        .call(
            new Operation<Boolean, RuntimeException>() {
              @Override
              public Boolean perform() throws RuntimeException {
                return pullRequestService //
                    .canMerge(
                        pullRequest.getToRef().getRepository().getId(), pullRequest.getId()) //
                    .isConflicted();
              }
            });
  }

  public NotificationResponse notify(
      final PrnfbNotification notification,
      final PrnfbPullRequestAction pullRequestAction,
      final PullRequest pullRequest,
      final PrnfbRenderer renderer,
      final ClientKeyStore clientKeyStore,
      final Boolean shouldAcceptAnyCertificate) {
    if (!isNotificationTriggeredByAction(
        notification,
        pullRequestAction,
        renderer,
        pullRequest,
        clientKeyStore,
        shouldAcceptAnyCertificate)) {
      return null;
    }

    Optional<String> postContent = absent();
    if (notification.getPostContent().isPresent()) {
      final ENCODE_FOR encodePostContentFor = notification.getPostContentEncoding();
      postContent =
          of(
              renderer.render(
                  notification.getPostContent().get(),
                  encodePostContentFor,
                  clientKeyStore,
                  shouldAcceptAnyCertificate));
    }
    final String renderedUrl =
        renderer.render(
            notification.getUrl(), ENCODE_FOR.URL, clientKeyStore, shouldAcceptAnyCertificate);
    LOG.info(
        notification.getName()
            + " > " //
            + pullRequest.getFromRef().getId()
            + "("
            + pullRequest.getFromRef().getLatestCommit()
            + ") -> " //
            + pullRequest.getToRef().getId()
            + "("
            + pullRequest.getToRef().getLatestCommit()
            + ")"
            + " " //
            + renderedUrl);
    final UrlInvoker urlInvoker =
        urlInvoker() //
            .withClientKeyStore(clientKeyStore) //
            .withUrlParam(renderedUrl) //
            .withMethod(notification.getMethod()) //
            .withPostContent(postContent) //
            .appendBasicAuth(notification);
    for (final PrnfbHeader header : notification.getHeaders()) {
      urlInvoker //
          .withHeader(
          header.getName(),
          renderer.render(
              header.getValue(), ENCODE_FOR.NONE, clientKeyStore, shouldAcceptAnyCertificate));
    }
    final HttpResponse httpResponse =
        createInvoker()
            .invoke(
                urlInvoker //
                    .withProxyServer(notification.getProxyServer()) //
                    .withProxyPort(notification.getProxyPort()) //
                    .withProxySchema(notification.getProxySchema()) //
                    .withProxyUser(notification.getProxyUser()) //
                    .withProxyPassword(notification.getProxyPassword()) //
                    .shouldAcceptAnyCertificate(shouldAcceptAnyCertificate) //
                    .setHttpVersion(notification.getHttpVersion()) //
                );

    return new NotificationResponse(notification.getUuid(), notification.getName(), httpResponse);
  }

  @EventListener
  public void onEvent(final PullRequestParticipantStatusUpdatedEvent e) {
    handleEventAsync(e);
  }

  @EventListener
  public void onEvent(final PullRequestCommentAddedEvent e) {
    handleEventAsync(e);
  }

  @EventListener
  public void onEvent(final PullRequestCommentDeletedEvent e) {
    handleEventAsync(e);
  }

  @EventListener
  public void onEvent(final PullRequestCommentEditedEvent e) {
    handleEventAsync(e);
  }

  @EventListener
  public void onEvent(final PullRequestCommentRepliedEvent e) {
    handleEventAsync(e);
  }

  @EventListener
  public void onEvent(final PullRequestDeletedEvent e) {
    handleEventAsync(e);
  }

  @EventListener
  public void onEvent(final PullRequestDeclinedEvent e) {
    handleEventAsync(e);
  }

  @EventListener
  public void onEvent(final PullRequestMergedEvent e) {
    handleEventAsync(e);
  }

  @EventListener
  public void onEvent(final PullRequestOpenedEvent e) {
    handleEventAsync(e);
  }

  @EventListener
  public void onEvent(final PullRequestReopenedEvent e) {
    handleEventAsync(e);
  }

  @EventListener
  public void onEvent(final PullRequestRescopedEvent e) {
    handleEventAsync(e);
  }

  @EventListener
  public void onEvent(final PullRequestUpdatedEvent e) {
    handleEventAsync(e);
  }
}
