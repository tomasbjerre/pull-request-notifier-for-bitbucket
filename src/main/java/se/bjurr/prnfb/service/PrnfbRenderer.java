package se.bjurr.prnfb.service;

import static com.google.common.base.Charsets.UTF_8;
import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.base.Throwables.propagate;
import static java.net.URLEncoder.encode;
import static org.slf4j.LoggerFactory.getLogger;
import static se.bjurr.prnfb.service.JsonEscaper.jsonEscape;
import static se.bjurr.prnfb.service.PrnfbRenderer.ENCODE_FOR.HTML;
import static se.bjurr.prnfb.service.PrnfbRenderer.ENCODE_FOR.JSON;
import static se.bjurr.prnfb.service.PrnfbRenderer.ENCODE_FOR.URL;
import static se.bjurr.prnfb.service.PrnfbVariable.EVERYTHING_URL;

import com.atlassian.bitbucket.pull.PullRequest;
import com.atlassian.bitbucket.repository.RepositoryService;
import com.atlassian.bitbucket.server.ApplicationPropertiesService;
import com.atlassian.bitbucket.user.ApplicationUser;
import com.atlassian.bitbucket.user.SecurityService;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Supplier;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.regex.Matcher;
import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import se.bjurr.prnfb.http.ClientKeyStore;
import se.bjurr.prnfb.listener.PrnfbPullRequestAction;
import se.bjurr.prnfb.settings.PrnfbNotification;

public class PrnfbRenderer {
  public enum ENCODE_FOR {
    NONE,
    URL,
    HTML,
    JSON
  }

  private static final Logger LOG = getLogger(PrnfbRenderer.class);
  private final ApplicationUser applicationUser;
  private final PrnfbNotification prnfbNotification;
  private final ApplicationPropertiesService propertiesService;
  private final PullRequest pullRequest;
  private final PrnfbPullRequestAction pullRequestAction;
  private final RepositoryService repositoryService;
  private final SecurityService securityService;

  /**
   * Contains special variables that are only available for specific events like {@link
   * PrnfbVariable#BUTTON_TRIGGER_TITLE} and {@link PrnfbVariable#PULL_REQUEST_COMMENT_TEXT}.
   */
  private final Map<PrnfbVariable, Supplier<String>> variables;

  PrnfbRenderer(
      PullRequest pullRequest,
      PrnfbPullRequestAction pullRequestAction,
      ApplicationUser applicationUser,
      RepositoryService repositoryService,
      ApplicationPropertiesService propertiesService,
      PrnfbNotification prnfbNotification,
      Map<PrnfbVariable, Supplier<String>> variables,
      SecurityService securityService) {
    this.pullRequest = pullRequest;
    this.pullRequestAction = pullRequestAction;
    this.applicationUser = applicationUser;
    this.repositoryService = repositoryService;
    this.prnfbNotification = prnfbNotification;
    this.propertiesService = propertiesService;
    this.variables = variables;
    this.securityService = securityService;
  }

  private boolean containsVariable(String string, final String regExpStr) {
    if (isNullOrEmpty(string)) {
      return false;
    }
    return string.contains(regExpStr.replaceAll("\\\\", ""));
  }

  @VisibleForTesting
  String getRenderedStringResolved(
      String string, ENCODE_FOR encodeFor, final String regExpStr, String resolved) {
    String replaceWith = null;
    if (encodeFor == URL) {
      try {
        replaceWith = encode(resolved, UTF_8.name());
      } catch (final UnsupportedEncodingException e) {
        propagate(e);
      }
    } else if (encodeFor == HTML) {
      replaceWith = StringEscapeUtils.escapeHtml4(resolved).replaceAll("(\r\n|\n)", "<br />");
    } else if (encodeFor == JSON) {
      replaceWith = jsonEscape(resolved);
    } else {
      replaceWith = resolved;
    }
    try {
      replaceWith = Matcher.quoteReplacement(replaceWith);
      string = string.replaceAll(regExpStr, replaceWith);
    } catch (final IllegalArgumentException e) {
      throw new RuntimeException("Tried to replace " + regExpStr + " with " + replaceWith, e);
    }
    return string;
  }

  @VisibleForTesting
  String regexp(PrnfbVariable variable) {
    return "\\$\\{" + variable.name() + "\\}";
  }

  public String render(
      String string,
      ENCODE_FOR encodeFor,
      ClientKeyStore clientKeyStore,
      Boolean shouldAcceptAnyCertificate) {
    string =
        renderVariable(
            string, ENCODE_FOR.NONE, clientKeyStore, shouldAcceptAnyCertificate, EVERYTHING_URL);

    for (final PrnfbVariable variable : PrnfbVariable.values()) {
      string =
          renderVariable(string, encodeFor, clientKeyStore, shouldAcceptAnyCertificate, variable);
    }
    return string;
  }

  private String renderVariable(
      String string,
      ENCODE_FOR encodeFor,
      ClientKeyStore clientKeyStore,
      Boolean shouldAcceptAnyCertificate,
      final PrnfbVariable variable) {
    final String regExpStr = regexp(variable);
    if (containsVariable(string, regExpStr)) {
      String resolved = "";
      try {
        resolved =
            variable.resolve(
                pullRequest,
                pullRequestAction,
                applicationUser,
                repositoryService,
                propertiesService,
                prnfbNotification,
                variables,
                clientKeyStore,
                shouldAcceptAnyCertificate,
                securityService);
        if (resolved == null) {
          resolved = "";
        }
      } catch (final Exception e) {
        LOG.error("Error when resolving " + variable, e);
      }
      return getRenderedStringResolved(string, encodeFor, regExpStr, resolved);
    }
    return string;
  }
}
