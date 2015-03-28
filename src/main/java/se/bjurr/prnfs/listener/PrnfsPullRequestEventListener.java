package se.bjurr.prnfs.listener;

import static se.bjurr.prnfs.settings.SettingsStorage.getPrnfsSettings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.bjurr.prnfs.settings.PrnfsNotification;
import se.bjurr.prnfs.settings.PrnfsSettings;
import se.bjurr.prnfs.settings.ValidationException;

import com.atlassian.event.api.EventListener;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.stash.event.pull.PullRequestEvent;
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
 public void anEvent(PullRequestEvent o) {
  try {
   final PrnfsSettings settings = getPrnfsSettings(pluginSettingsFactory.createGlobalSettings());
   for (final PrnfsNotification n : settings.getNotifications()) {
    if (n.getTriggers().contains(o.getAction())) {
     urlInvoker.ivoke(new PrnfsRenderer(o).render(n.getUrl()), n.getUser(), n.getPassword());
    }
   }
  } catch (final ValidationException e) {
   logger.error("", e);
  }
 }
}