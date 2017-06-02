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

import com.atlassian.bitbucket.event.pull.PullRequestCommentAddedEvent;
import com.atlassian.bitbucket.event.pull.PullRequestCommentDeletedEvent;
import com.atlassian.bitbucket.event.pull.PullRequestCommentEditedEvent;
import com.atlassian.bitbucket.event.pull.PullRequestCommentEvent;
import com.atlassian.bitbucket.event.pull.PullRequestCommentRepliedEvent;
import com.atlassian.bitbucket.event.pull.PullRequestDeclinedEvent;
import com.atlassian.bitbucket.event.pull.PullRequestEvent;
import com.atlassian.bitbucket.event.pull.PullRequestMergedEvent;
import com.atlassian.bitbucket.event.pull.PullRequestOpenedEvent;
import com.atlassian.bitbucket.event.pull.PullRequestParticipantStatusUpdatedEvent;
import com.atlassian.bitbucket.event.pull.PullRequestReopenedEvent;
import com.atlassian.bitbucket.event.pull.PullRequestRescopedEvent;
import com.atlassian.bitbucket.event.pull.PullRequestUpdatedEvent;
import com.atlassian.bitbucket.pull.PullRequest;
import com.atlassian.bitbucket.pull.PullRequestService;
import com.atlassian.bitbucket.user.SecurityService;
import com.atlassian.bitbucket.util.Operation;
import com.atlassian.event.api.EventListener;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Optional;

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
  private final SecurityService securityService;

  private final SettingsService settingsService;

  public PrnfbPullRequestEventListener(
      PrnfbRendererFactory prnfbRendererFactory,
      PullRequestService pullRequestService,
      ExecutorService executorService,
      SettingsService settingsService,
      SecurityService securityService) {
    this.prnfbRendererFactory = prnfbRendererFactory;
    this.pullRequestService = pullRequestService;
    this.executorService = executorService;
    this.settingsService = settingsService;
    this.securityService = securityService;
  }

  private Invoker createInvoker() {
    if (mockedInvoker != null) {
      return mockedInvoker;
    }
    return new Invoker() {
      @Override
      public HttpResponse invoke(UrlInvoker urlInvoker) {
        return urlInvoker.invoke();
      }
    };
  }

  private void handleEvent(final PullRequestEvent pullRequestEvent) {
    if (pullRequestEvent.getPullRequest().isClosed()
        && pullRequestEvent instanceof PullRequestCommentEvent) {
      return;
    }
    final PrnfbSettingsData settings = settingsService.getPrnfbSettingsData();
    ClientKeyStore clientKeyStore = new ClientKeyStore(settings);
    for (final PrnfbNotification notification : settingsService.getNotifications()) {
      PrnfbPullRequestAction action = fromPullRequestEvent(pullRequestEvent, notification);
      VariablesContext variables =
          new VariablesContextBuilder() //
              .setPullRequestEvent(pullRequestEvent) //
              .build();
      PrnfbRenderer renderer =
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
  boolean ignoreBecauseOfConflicting(TRIGGER_IF_MERGE triggerIfCanMerge, boolean isConflicted) {
    return triggerIfCanMerge == NOT_CONFLICTING && isConflicted
        || //
        triggerIfCanMerge == CONFLICTING && !isConflicted;
  }

  public boolean isNotificationTriggeredByAction(
      PrnfbNotification notification,
      PrnfbPullRequestAction pullRequestAction,
      PrnfbRenderer renderer,
      PullRequest pullRequest,
      ClientKeyStore clientKeyStore,
      Boolean shouldAcceptAnyCertificate) {
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

    if (notification.getTriggerIfCanMerge() != ALWAYS && pullRequest.isOpen()) {
      // Cannot perform canMerge unless PR is open
      boolean isConflicted =
          securityService //
              .withPermission(ADMIN, "Can merge") //
              .call(
                  new Operation<Boolean, RuntimeException>() {
                    @Override
                    public Boolean perform() throws RuntimeException {
                      return pullRequestService //
                          .canMerge(
                              pullRequest.getToRef().getRepository().getId(),
                              pullRequest.getId()) //
                          .isConflicted();
                    }
                  });
      if (ignoreBecauseOfConflicting(notification.getTriggerIfCanMerge(), isConflicted)) {
        return FALSE;
      }
    }

    return TRUE;
  }

  public NotificationResponse notify(
      final PrnfbNotification notification,
      PrnfbPullRequestAction pullRequestAction,
      PullRequest pullRequest,
      PrnfbRenderer renderer,
      ClientKeyStore clientKeyStore,
      Boolean shouldAcceptAnyCertificate) {
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
      ENCODE_FOR encodePostContentFor = notification.getPostContentEncoding();
      postContent =
          of(
              renderer.render(
                  notification.getPostContent().get(),
                  encodePostContentFor,
                  clientKeyStore,
                  shouldAcceptAnyCertificate));
    }
    String renderedUrl =
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
    UrlInvoker urlInvoker =
        urlInvoker() //
            .withClientKeyStore(clientKeyStore) //
            .withUrlParam(renderedUrl) //
            .withMethod(notification.getMethod()) //
            .withPostContent(postContent) //
            .appendBasicAuth(notification);
    for (PrnfbHeader header : notification.getHeaders()) {
      urlInvoker //
          .withHeader(
          header.getName(),
          renderer.render(
              header.getValue(), ENCODE_FOR.NONE, clientKeyStore, shouldAcceptAnyCertificate));
    }
    HttpResponse httpResponse =
        createInvoker()
            .invoke(
                urlInvoker //
                    .withProxyServer(notification.getProxyServer()) //
                    .withProxyPort(notification.getProxyPort()) //
                    .withProxySchema(notification.getProxySchema()) //
                    .withProxyUser(notification.getProxyUser()) //
                    .withProxyPassword(notification.getProxyPassword()) //
                    .shouldAcceptAnyCertificate(shouldAcceptAnyCertificate));

    return new NotificationResponse(notification.getUuid(), notification.getName(), httpResponse);
  }

  @EventListener
  public void onEvent(PullRequestParticipantStatusUpdatedEvent e) {
    handleEventAsync(e);
  }

  @EventListener
  public void onEvent(PullRequestCommentAddedEvent e) {
    handleEventAsync(e);
  }

  @EventListener
  public void onEvent(PullRequestCommentDeletedEvent e) {
    handleEventAsync(e);
  }

  @EventListener
  public void onEvent(PullRequestCommentEditedEvent e) {
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
  public void onEvent(PullRequestUpdatedEvent e) {
    handleEventAsync(e);
  }
}
