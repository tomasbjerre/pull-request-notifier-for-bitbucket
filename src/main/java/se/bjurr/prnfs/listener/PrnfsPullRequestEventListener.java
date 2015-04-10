package se.bjurr.prnfs.listener;

import static java.util.regex.Pattern.compile;
import static se.bjurr.prnfs.listener.PrnfsPullRequestAction.fromPullRequestEvent;
import static se.bjurr.prnfs.settings.SettingsStorage.getPrnfsSettings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.bjurr.prnfs.settings.PrnfsNotification;
import se.bjurr.prnfs.settings.PrnfsSettings;
import se.bjurr.prnfs.settings.ValidationException;

import com.atlassian.event.api.EventListener;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.stash.event.pull.PullRequestApprovedEvent;
import com.atlassian.stash.event.pull.PullRequestCommentAddedEvent;
import com.atlassian.stash.event.pull.PullRequestDeclinedEvent;
import com.atlassian.stash.event.pull.PullRequestEvent;
import com.atlassian.stash.event.pull.PullRequestMergedEvent;
import com.atlassian.stash.event.pull.PullRequestOpenedEvent;
import com.atlassian.stash.event.pull.PullRequestReopenedEvent;
import com.atlassian.stash.event.pull.PullRequestRescopedEvent;
import com.atlassian.stash.event.pull.PullRequestUnapprovedEvent;
import com.atlassian.stash.event.pull.PullRequestUpdatedEvent;
import com.google.common.annotations.VisibleForTesting;

public class PrnfsPullRequestEventListener {

 private UrlInvoker urlInvoker = new UrlInvoker();
 private final PluginSettingsFactory pluginSettingsFactory;
 private static final Logger logger = LoggerFactory.getLogger(PrnfsPullRequestEventListener.class);

 public PrnfsPullRequestEventListener(PluginSettingsFactory pluginSettingsFactory) {
  this.pluginSettingsFactory = pluginSettingsFactory;
 }

 @VisibleForTesting
 public void setUrlInvoker(UrlInvoker urlInvoker) {
  this.urlInvoker = urlInvoker;
 }

 @EventListener
 public void onEvent(PullRequestApprovedEvent e) {
  handleEvent(e, fromPullRequestEvent(e));
 }

 @EventListener
 public void onEvent(PullRequestCommentAddedEvent e) {
  handleEvent(e, fromPullRequestEvent(e));
 }

 @EventListener
 public void onEvent(PullRequestDeclinedEvent e) {
  handleEvent(e, fromPullRequestEvent(e));
 }

 @EventListener
 public void onEvent(PullRequestMergedEvent e) {
  handleEvent(e, fromPullRequestEvent(e));
 }

 @EventListener
 public void onEvent(PullRequestOpenedEvent e) {
  handleEvent(e, fromPullRequestEvent(e));
 }

 @EventListener
 public void onEvent(PullRequestReopenedEvent e) {
  handleEvent(e, fromPullRequestEvent(e));
 }

 @EventListener
 public void onEvent(final PullRequestRescopedEvent e) {
  handleEvent(e, fromPullRequestEvent(e));
 }

 @EventListener
 public void onEvent(PullRequestUnapprovedEvent e) {
  handleEvent(e, fromPullRequestEvent(e));
 }

 @EventListener
 public void onEvent(PullRequestUpdatedEvent e) {
  handleEvent(e, fromPullRequestEvent(e));
 }

 @VisibleForTesting
 public void handleEvent(PullRequestEvent o, PrnfsPullRequestAction action) {
  final PrnfsRenderer renderer = new PrnfsRenderer(o);
  try {
   final PrnfsSettings settings = getPrnfsSettings(pluginSettingsFactory.createGlobalSettings());
   for (final PrnfsNotification n : settings.getNotifications()) {
    if (n.getFilterRegexp().isPresent() && n.getFilterString().isPresent()
      && !compile(n.getFilterRegexp().get()).matcher(renderer.render(n.getFilterString().get())).find()) {
     continue;
    }
    if (n.getTriggers().contains(action)) {
     urlInvoker.ivoke(renderer.render(n.getUrl()), n.getUser(), n.getPassword());
    }
   }
  } catch (final ValidationException e) {
   logger.error("", e);
  }
 }
}