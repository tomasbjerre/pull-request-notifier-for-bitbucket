package se.bjurr.prnfb.service;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Ordering.usingToString;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static se.bjurr.prnfb.listener.PrnfbPullRequestAction.BUTTON_TRIGGER;
import static se.bjurr.prnfb.service.PrnfbVariable.BUTTON_TRIGGER_TITLE;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import se.bjurr.prnfb.http.ClientKeyStore;
import se.bjurr.prnfb.listener.PrnfbPullRequestAction;
import se.bjurr.prnfb.listener.PrnfbPullRequestEventListener;
import se.bjurr.prnfb.settings.PrnfbButton;
import se.bjurr.prnfb.settings.PrnfbNotification;
import se.bjurr.prnfb.settings.PrnfbSettingsData;

import com.atlassian.bitbucket.pull.PullRequest;
import com.atlassian.bitbucket.pull.PullRequestService;
import com.atlassian.bitbucket.repository.Repository;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;

public class ButtonsService {

 private final PrnfbPullRequestEventListener prnfbPullRequestEventListener;
 private final PrnfbRendererFactory prnfbRendererFactory;
 private final PullRequestService pullRequestService;
 private final SettingsService settingsService;
 private final UserCheckService userCheckService;

 public ButtonsService(PullRequestService pullRequestService,
   PrnfbPullRequestEventListener prnfbPullRequestEventListener, PrnfbRendererFactory prnfbRendererFactory,
   SettingsService settingsService, UserCheckService userCheckService) {
  this.pullRequestService = pullRequestService;
  this.prnfbPullRequestEventListener = prnfbPullRequestEventListener;
  this.prnfbRendererFactory = prnfbRendererFactory;
  this.settingsService = settingsService;
  this.userCheckService = userCheckService;
 }

 public List<PrnfbButton> getButtons(Integer repositoryId, Long pullRequestId) {
  final PrnfbSettingsData settings = this.settingsService.getPrnfbSettingsData();
  List<PrnfbNotification> notifications = this.settingsService.getNotifications();
  ClientKeyStore clientKeyStore = new ClientKeyStore(settings);
  final PullRequest pullRequest = this.pullRequestService.getById(repositoryId, pullRequestId);
  boolean shouldAcceptAnyCertificate = settings.isShouldAcceptAnyCertificate();
  return doGetButtons(notifications, clientKeyStore, pullRequest, shouldAcceptAnyCertificate);
 }

 public void handlePressed(Integer repositoryId, Long pullRequestId, UUID buttonUuid) {
  final PrnfbSettingsData prnfbSettingsData = this.settingsService.getPrnfbSettingsData();
  ClientKeyStore clientKeyStore = new ClientKeyStore(prnfbSettingsData);
  boolean shouldAcceptAnyCertificate = prnfbSettingsData.isShouldAcceptAnyCertificate();
  final PullRequest pullRequest = this.pullRequestService.getById(repositoryId, pullRequestId);
  doHandlePressed(buttonUuid, clientKeyStore, shouldAcceptAnyCertificate, pullRequest);
 }

 private boolean isTriggeredByAction(ClientKeyStore clientKeyStore, List<PrnfbNotification> notifications,
   boolean shouldAcceptAnyCertificate, PrnfbPullRequestAction pullRequestAction, PullRequest pullRequest,
   Map<PrnfbVariable, Supplier<String>> variables) {
  for (PrnfbNotification prnfbNotification : notifications) {
   PrnfbRenderer renderer = this.prnfbRendererFactory.create(pullRequest, pullRequestAction, prnfbNotification,
     variables);
   if (this.prnfbPullRequestEventListener.isNotificationTriggeredByAction(prnfbNotification, pullRequestAction,
     renderer, pullRequest, clientKeyStore, shouldAcceptAnyCertificate)) {
    return TRUE;
   }
  }
  return FALSE;
 }

 /**
  * Checks if the given button is visible in the given repository.
  * 
  * @param button Button under test
  * @param repository Repository to check for
  * @return True if the button is either globally visible or matches with the given repository
  */
 private boolean isVisibleOnRepository(PrnfbButton button, Repository repository) {
  if(button.getRepositorySlug().isPresent()) {
   boolean visible = false;
   do {
    visible |= button.getProjectKey().get().equals(repository.getProject().getKey())
      && button.getRepositorySlug().get().equals(repository.getSlug());
   } while(!visible && (repository = repository.getOrigin()) != null);
   return visible;
  } else {
   return TRUE;
  }
 }

 /**
  * Checks if the given button is visible on the pull request by either the from or to repository.
  */
 private boolean isVisibleOnPullRequest(PrnfbButton button, PullRequest pullRequest) {
  return 
    (pullRequest.getFromRef() != null && isVisibleOnRepository(button, pullRequest.getFromRef().getRepository()))
    || (pullRequest.getToRef() != null && isVisibleOnRepository(button, pullRequest.getToRef().getRepository()));
 }

 @VisibleForTesting
 List<PrnfbButton> doGetButtons(List<PrnfbNotification> notifications, ClientKeyStore clientKeyStore,
   final PullRequest pullRequest, boolean shouldAcceptAnyCertificate) {
  List<PrnfbButton> allFoundButtons = newArrayList();
  for (PrnfbButton candidate : this.settingsService.getButtons()) {
   Map<PrnfbVariable, Supplier<String>> variables = getVariables(candidate.getUuid());
   PrnfbPullRequestAction pullRequestAction = BUTTON_TRIGGER;
   if (this.userCheckService.isAllowedUseButton(candidate)
     && isTriggeredByAction(clientKeyStore, notifications, shouldAcceptAnyCertificate, pullRequestAction, pullRequest,
       variables)
     && (isVisibleOnPullRequest(candidate, pullRequest))) {
    allFoundButtons.add(candidate);
   }
  }
  allFoundButtons = usingToString().sortedCopy(allFoundButtons);
  return allFoundButtons;
 }

 @VisibleForTesting
 void doHandlePressed(UUID buttonUuid, ClientKeyStore clientKeyStore, boolean shouldAcceptAnyCertificate,
   final PullRequest pullRequest) {
  Map<PrnfbVariable, Supplier<String>> variables = getVariables(buttonUuid);
  for (PrnfbNotification prnfbNotification : this.settingsService.getNotifications()) {
   PrnfbPullRequestAction pullRequestAction = BUTTON_TRIGGER;
   PrnfbRenderer renderer = this.prnfbRendererFactory.create(pullRequest, pullRequestAction, prnfbNotification,
     variables);
   if (this.prnfbPullRequestEventListener.isNotificationTriggeredByAction(prnfbNotification, pullRequestAction,
     renderer, pullRequest, clientKeyStore, shouldAcceptAnyCertificate)) {
    this.prnfbPullRequestEventListener.notify(prnfbNotification, pullRequestAction, pullRequest, renderer,
      clientKeyStore, shouldAcceptAnyCertificate);
   }
  }
 }

 @VisibleForTesting
 Map<PrnfbVariable, Supplier<String>> getVariables(final UUID uuid) {
  Map<PrnfbVariable, Supplier<String>> variables = new HashMap<PrnfbVariable, Supplier<String>>();
  PrnfbButton button = this.settingsService.getButton(uuid);
  variables.put(BUTTON_TRIGGER_TITLE, Suppliers.ofInstance(button.getName()));
  return variables;
 }

}
