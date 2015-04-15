package se.bjurr.prnfs.listener;

import static com.google.common.base.Charsets.UTF_8;
import static com.google.common.base.Optional.absent;
import static java.util.regex.Pattern.compile;
import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.xml.bind.DatatypeConverter.printBase64Binary;
import static se.bjurr.prnfs.listener.PrnfsPullRequestAction.fromPullRequestEvent;
import static se.bjurr.prnfs.listener.UrlInvoker.urlInvoker;
import static se.bjurr.prnfs.settings.SettingsStorage.getPrnfsSettings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.bjurr.prnfs.settings.Header;
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
import com.google.common.base.Optional;

public class PrnfsPullRequestEventListener {

 public interface Invoker {
  void invoke(UrlInvoker urlInvoker);
 }

 private final PluginSettingsFactory pluginSettingsFactory;
 private static final Logger logger = LoggerFactory.getLogger(PrnfsPullRequestEventListener.class);

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

 public PrnfsPullRequestEventListener(PluginSettingsFactory pluginSettingsFactory) {
  this.pluginSettingsFactory = pluginSettingsFactory;
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
     Optional<String> postContent = absent();
     if (n.getPostContent().isPresent()) {
      postContent = Optional.of(renderer.render(n.getPostContent().get()));
     }
     UrlInvoker urlInvoker = urlInvoker().withUrlParam(renderer.render(n.getUrl())).withMethod(n.getMethod())
       .withPostContent(postContent);
     if (n.getUser().isPresent() && n.getPassword().isPresent()) {
      final String userpass = n.getUser().get() + ":" + n.getPassword().get();
      final String basicAuth = "Basic " + new String(printBase64Binary(userpass.getBytes(UTF_8)));
      urlInvoker.withHeader(AUTHORIZATION, basicAuth);
     }
     for (Header header : n.getHeaders()) {
      urlInvoker.withHeader(header.getName(), renderer.render(header.getValue()));
     }
     urlInvoker.withProxyServer(n.getProxyServer());
     urlInvoker.withProxyPort(n.getProxyPort());
     urlInvoker.withProxyUser(n.getProxyUser());
     urlInvoker.withProxyPassword(n.getProxyPassword());
     invoker.invoke(urlInvoker);
    }
   }
  } catch (final ValidationException e) {
   logger.error("", e);
  }
 }
}