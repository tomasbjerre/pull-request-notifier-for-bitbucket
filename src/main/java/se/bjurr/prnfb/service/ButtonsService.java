package se.bjurr.prnfb.service;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Ordering.usingToString;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static se.bjurr.prnfb.listener.PrnfbPullRequestAction.BUTTON_TRIGGER;

import java.util.List;
import java.util.UUID;

import com.atlassian.bitbucket.pull.PullRequest;
import com.atlassian.bitbucket.pull.PullRequestService;
import com.atlassian.bitbucket.repository.Repository;
import com.google.common.annotations.VisibleForTesting;

import se.bjurr.prnfb.http.ClientKeyStore;
import se.bjurr.prnfb.http.NotificationResponse;
import se.bjurr.prnfb.listener.PrnfbPullRequestAction;
import se.bjurr.prnfb.listener.PrnfbPullRequestEventListener;
import se.bjurr.prnfb.service.VariablesContext.VariablesContextBuilder;
import se.bjurr.prnfb.settings.PrnfbButton;
import se.bjurr.prnfb.settings.PrnfbNotification;
import se.bjurr.prnfb.settings.PrnfbSettingsData;

public class ButtonsService {

  private final PrnfbPullRequestEventListener prnfbPullRequestEventListener;
  private final PrnfbRendererFactory prnfbRendererFactory;
  private final PullRequestService pullRequestService;
  private final SettingsService settingsService;
  private final UserCheckService userCheckService;

  public ButtonsService(
      PullRequestService pullRequestService,
      PrnfbPullRequestEventListener prnfbPullRequestEventListener,
      PrnfbRendererFactory prnfbRendererFactory,
      SettingsService settingsService,
      UserCheckService userCheckService) {
    this.pullRequestService = pullRequestService;
    this.prnfbPullRequestEventListener = prnfbPullRequestEventListener;
    this.prnfbRendererFactory = prnfbRendererFactory;
    this.settingsService = settingsService;
    this.userCheckService = userCheckService;
  }

  @VisibleForTesting
  List<PrnfbButton> doGetButtons(
      List<PrnfbNotification> notifications,
      ClientKeyStore clientKeyStore,
      final PullRequest pullRequest,
      boolean shouldAcceptAnyCertificate) {

    String projectKey = pullRequest.getToRef().getRepository().getProject().getKey();
    String repositoryKey = pullRequest.getToRef().getRepository().getSlug();
    List<PrnfbButton> allFoundButtons = newArrayList();
    for (PrnfbButton candidate : settingsService.getButtons()) {
      PrnfbButton button = settingsService.getButton(candidate.getUuid());
      VariablesContext variables =
          new VariablesContextBuilder() //
              .setButton(button) //
              .build();

      PrnfbPullRequestAction pullRequestAction = BUTTON_TRIGGER;
      if (userCheckService.isAllowed(candidate.getUserLevel(), projectKey, repositoryKey) //
          && isTriggeredByAction(
              clientKeyStore,
              notifications,
              shouldAcceptAnyCertificate,
              pullRequestAction,
              pullRequest,
              variables) //
          && isVisibleOnPullRequest(candidate, pullRequest)) {
        allFoundButtons.add(candidate);
      }
    }
    allFoundButtons = usingToString().sortedCopy(allFoundButtons);
    return allFoundButtons;
  }

  @VisibleForTesting
  List<NotificationResponse> doHandlePressed(
      UUID buttonUuid,
      ClientKeyStore clientKeyStore,
      boolean shouldAcceptAnyCertificate,
      final PullRequest pullRequest,
      final String formData) {
    PrnfbButton button = settingsService.getButton(buttonUuid);
    VariablesContext variables =
        new VariablesContextBuilder() //
            .setButton(button) //
            .setFormData(formData) //
            .build();

    List<NotificationResponse> successes = newArrayList();
    for (PrnfbNotification prnfbNotification : settingsService.getNotifications()) {
      PrnfbPullRequestAction pullRequestAction = BUTTON_TRIGGER;
      PrnfbRenderer renderer =
          prnfbRendererFactory.create(pullRequest, pullRequestAction, prnfbNotification, variables);
      if (prnfbPullRequestEventListener.isNotificationTriggeredByAction(
          prnfbNotification,
          pullRequestAction,
          renderer,
          pullRequest,
          clientKeyStore,
          shouldAcceptAnyCertificate)) {
        NotificationResponse response =
            prnfbPullRequestEventListener.notify(
                prnfbNotification,
                pullRequestAction,
                pullRequest,
                renderer,
                clientKeyStore,
                shouldAcceptAnyCertificate);
        if (response != null) {
          successes.add(response);
        }
      }
    }

    return successes;
  }

  public List<PrnfbButton> getButtons(Integer repositoryId, Long pullRequestId) {
    final PrnfbSettingsData settings = settingsService.getPrnfbSettingsData();
    List<PrnfbNotification> notifications = settingsService.getNotifications();
    ClientKeyStore clientKeyStore = new ClientKeyStore(settings);
    final PullRequest pullRequest = pullRequestService.getById(repositoryId, pullRequestId);
    boolean shouldAcceptAnyCertificate = settings.isShouldAcceptAnyCertificate();
    return doGetButtons(notifications, clientKeyStore, pullRequest, shouldAcceptAnyCertificate);
  }

  public PrnfbRendererWrapper getRenderer(
      Integer repositoryId, Long pullRequestId, UUID buttonUuid) {
    final PrnfbSettingsData settings = settingsService.getPrnfbSettingsData();
    ClientKeyStore clientKeyStore = new ClientKeyStore(settings);
    final PullRequest pullRequest = pullRequestService.getById(repositoryId, pullRequestId);
    boolean shouldAcceptAnyCertificate = settings.isShouldAcceptAnyCertificate();

    PrnfbButton button = settingsService.getButton(buttonUuid);
    VariablesContext variables =
        new VariablesContextBuilder() //
            .setButton(button) //
            .build();

    PrnfbPullRequestAction pullRequestAction = BUTTON_TRIGGER;

    PrnfbRendererWrapper renderer =
        prnfbRendererFactory.create(
            pullRequest, pullRequestAction, variables, clientKeyStore, shouldAcceptAnyCertificate);
    return renderer;
  }

  public List<NotificationResponse> handlePressed(
      Integer repositoryId, Long pullRequestId, UUID buttonUuid, String formData) {
    final PrnfbSettingsData prnfbSettingsData = settingsService.getPrnfbSettingsData();
    ClientKeyStore clientKeyStore = new ClientKeyStore(prnfbSettingsData);
    boolean shouldAcceptAnyCertificate = prnfbSettingsData.isShouldAcceptAnyCertificate();
    final PullRequest pullRequest = pullRequestService.getById(repositoryId, pullRequestId);
    return doHandlePressed(
        buttonUuid, clientKeyStore, shouldAcceptAnyCertificate, pullRequest, formData);
  }

  private boolean isTriggeredByAction(
      ClientKeyStore clientKeyStore,
      List<PrnfbNotification> notifications,
      boolean shouldAcceptAnyCertificate,
      PrnfbPullRequestAction pullRequestAction,
      PullRequest pullRequest,
      VariablesContext variables) {
    for (PrnfbNotification prnfbNotification : notifications) {
      PrnfbRenderer renderer =
          prnfbRendererFactory.create(pullRequest, pullRequestAction, prnfbNotification, variables);
      if (prnfbPullRequestEventListener.isNotificationTriggeredByAction(
          prnfbNotification,
          pullRequestAction,
          renderer,
          pullRequest,
          clientKeyStore,
          shouldAcceptAnyCertificate)) {
        return TRUE;
      }
    }
    return FALSE;
  }

  /**
   * Checks if the given button is visible on the pull request by either the from or to repository.
   */
  private boolean isVisibleOnPullRequest(PrnfbButton button, PullRequest pullRequest) {
    return pullRequest.getFromRef() != null
            && isVisibleOnRepository(button, pullRequest.getFromRef().getRepository())
        || pullRequest.getToRef() != null
            && isVisibleOnRepository(button, pullRequest.getToRef().getRepository());
  }

  /**
   * Checks if the given button is visible in the given repository.
   *
   * @param button Button under test
   * @param repository Repository to check for
   * @return True if the button is either globally visible or matches with the given repository
   */
  @VisibleForTesting
  boolean isVisibleOnRepository(PrnfbButton button, Repository repository) {
    boolean projectOk = false;
    boolean repoOk = false;

    do {
      if (button.getProjectKey().isPresent()) {
        projectOk |= button.getProjectKey().get().equals(repository.getProject().getKey());
      } else {
        projectOk = true;
      }
      if (button.getRepositorySlug().isPresent()) {
        repoOk |= button.getRepositorySlug().get().equals(repository.getSlug());
      } else {
        repoOk = true;
      }
    } while (!(projectOk && repoOk) && (repository = repository.getOrigin()) != null);

    return projectOk && repoOk;
  }
}
