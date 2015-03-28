package se.bjurr.prnfs.listener;

import static com.google.common.cache.CacheBuilder.newBuilder;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
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
import com.google.common.cache.Cache;

public class PrnfsPullRequestEventListener {

 private UrlInvoker urlInvoker = new UrlInvoker();
 private final PluginSettingsFactory pluginSettingsFactory;
 private static final Logger logger = LoggerFactory.getLogger(PrnfsPullRequestEventListener.class);
 private static Cache<Object, Object> duplicateEventCache;

 public PrnfsPullRequestEventListener(PluginSettingsFactory pluginSettingsFactory) {
  this.pluginSettingsFactory = pluginSettingsFactory;
  duplicateEventCache = newBuilder().maximumSize(1000).expireAfterWrite(50, MILLISECONDS).build();
 }

 @VisibleForTesting
 public void setUrlInvoker(UrlInvoker urlInvoker) {
  this.urlInvoker = urlInvoker;
 }

 @EventListener
 public void anEvent(PullRequestEvent o) {
  if (dublicateEventBug(o)) {
   return;
  }
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

 /**
  * Looks like there is a bug in Stash that causes events to be fired twice.
  */
 public static boolean dublicateEventBug(PullRequestEvent o) {
  final String footprint = o.getPullRequest().getId() + "_" + o.getAction().name();
  if (duplicateEventCache.asMap().containsKey(footprint)) {
   return TRUE;
  }
  duplicateEventCache.put(footprint, TRUE);
  return FALSE;
 }
}