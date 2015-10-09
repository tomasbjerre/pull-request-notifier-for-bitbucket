package se.bjurr.prnfs.listener;

import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Lists.newArrayList;
import static java.util.logging.Logger.getLogger;
import static java.util.regex.Pattern.compile;
import static se.bjurr.prnfs.listener.PrnfsRenderer.REPO_PROTOCOL.http;
import static se.bjurr.prnfs.listener.PrnfsRenderer.REPO_PROTOCOL.ssh;
import static se.bjurr.prnfs.listener.UrlInvoker.urlInvoker;
import static se.bjurr.prnfs.listener.UrlInvoker.HTTP_METHOD.GET;

import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Matcher;

import se.bjurr.prnfs.settings.PrnfsNotification;

import com.atlassian.stash.pull.PullRequest;
import com.atlassian.stash.pull.PullRequestParticipant;
import com.atlassian.stash.repository.Repository;
import com.atlassian.stash.repository.RepositoryCloneLinksRequest;
import com.atlassian.stash.repository.RepositoryService;
import com.atlassian.stash.server.ApplicationPropertiesService;
import com.atlassian.stash.user.StashUser;
import com.atlassian.stash.util.NamedLink;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;

public class PrnfsRenderer {
 private static final Logger logger = getLogger(PrnfsRenderer.class.getName());
 private static Invoker invoker = new Invoker() {
  @Override
  public void invoke(UrlInvoker urlInvoker) {
   urlInvoker.invoke();
  }
 };

 @VisibleForTesting
 public static void setInvoker(Invoker invoker) {
  PrnfsRenderer.invoker = invoker;
 }

 public enum REPO_PROTOCOL {
  ssh, http
 }

 public enum PrnfsVariable {
  PULL_REQUEST_FROM_HASH(new Resolver() {
   @SuppressWarnings("deprecation")
   @Override
   public String resolve(PullRequest pullRequest, PrnfsPullRequestAction prnfsPullRequestAction, StashUser stashUser,
     RepositoryService repositoryService, ApplicationPropertiesService propertiesService,
     PrnfsNotification prnfsNotification, Map<PrnfsRenderer.PrnfsVariable, Supplier<String>> variables) {
    return pullRequest.getFromRef().getLatestChangeset();
   }
  }), PULL_REQUEST_FROM_ID(new Resolver() {
   @Override
   public String resolve(PullRequest pullRequest, PrnfsPullRequestAction prnfsPullRequestAction, StashUser stashUser,
     RepositoryService repositoryService, ApplicationPropertiesService propertiesService,
     PrnfsNotification prnfsNotification, Map<PrnfsRenderer.PrnfsVariable, Supplier<String>> variables) {
    return pullRequest.getFromRef().getId();
   }
  }), PULL_REQUEST_FROM_BRANCH(new Resolver() {
   @Override
   public String resolve(PullRequest pullRequest, PrnfsPullRequestAction prnfsPullRequestAction, StashUser stashUser,
     RepositoryService repositoryService, ApplicationPropertiesService propertiesService,
     PrnfsNotification prnfsNotification, Map<PrnfsRenderer.PrnfsVariable, Supplier<String>> variables) {
    return pullRequest.getFromRef().getDisplayId();
   }
  }), PULL_REQUEST_FROM_REPO_ID(new Resolver() {
   @Override
   public String resolve(PullRequest pullRequest, PrnfsPullRequestAction prnfsPullRequestAction, StashUser stashUser,
     RepositoryService repositoryService, ApplicationPropertiesService propertiesService,
     PrnfsNotification prnfsNotification, Map<PrnfsRenderer.PrnfsVariable, Supplier<String>> variables) {
    return pullRequest.getFromRef().getRepository().getId() + "";
   }
  }), PULL_REQUEST_FROM_REPO_NAME(new Resolver() {
   @Override
   public String resolve(PullRequest pullRequest, PrnfsPullRequestAction prnfsPullRequestAction, StashUser stashUser,
     RepositoryService repositoryService, ApplicationPropertiesService propertiesService,
     PrnfsNotification prnfsNotification, Map<PrnfsRenderer.PrnfsVariable, Supplier<String>> variables) {
    return pullRequest.getFromRef().getRepository().getName() + "";
   }
  }), PULL_REQUEST_FROM_REPO_PROJECT_ID(new Resolver() {
   @Override
   public String resolve(PullRequest pullRequest, PrnfsPullRequestAction prnfsPullRequestAction, StashUser stashUser,
     RepositoryService repositoryService, ApplicationPropertiesService propertiesService,
     PrnfsNotification prnfsNotification, Map<PrnfsRenderer.PrnfsVariable, Supplier<String>> variables) {
    return pullRequest.getFromRef().getRepository().getProject().getId() + "";
   }
  }), PULL_REQUEST_FROM_REPO_PROJECT_KEY(new Resolver() {
   @Override
   public String resolve(PullRequest pullRequest, PrnfsPullRequestAction prnfsPullRequestAction, StashUser stashUser,
     RepositoryService repositoryService, ApplicationPropertiesService propertiesService,
     PrnfsNotification prnfsNotification, Map<PrnfsRenderer.PrnfsVariable, Supplier<String>> variables) {
    return pullRequest.getFromRef().getRepository().getProject().getKey();
   }
  }), PULL_REQUEST_FROM_REPO_SLUG(new Resolver() {
   @Override
   public String resolve(PullRequest pullRequest, PrnfsPullRequestAction prnfsPullRequestAction, StashUser stashUser,
     RepositoryService repositoryService, ApplicationPropertiesService propertiesService,
     PrnfsNotification prnfsNotification, Map<PrnfsRenderer.PrnfsVariable, Supplier<String>> variables) {
    return pullRequest.getFromRef().getRepository().getSlug() + "";
   }
  }), PULL_REQUEST_FROM_SSH_CLONE_URL(new Resolver() {
   @Override
   public String resolve(PullRequest pullRequest, PrnfsPullRequestAction prnfsPullRequestAction, StashUser stashUser,
     RepositoryService repositoryService, ApplicationPropertiesService propertiesService,
     PrnfsNotification prnfsNotification, Map<PrnfsRenderer.PrnfsVariable, Supplier<String>> variables) {
    return cloneUrlFromRepository(ssh, pullRequest.getFromRef().getRepository(), repositoryService);
   }
  }), PULL_REQUEST_FROM_HTTP_CLONE_URL(new Resolver() {
   @Override
   public String resolve(PullRequest pullRequest, PrnfsPullRequestAction prnfsPullRequestAction, StashUser stashUser,
     RepositoryService repositoryService, ApplicationPropertiesService propertiesService,
     PrnfsNotification prnfsNotification, Map<PrnfsRenderer.PrnfsVariable, Supplier<String>> variables) {
    return cloneUrlFromRepository(http, pullRequest.getFromRef().getRepository(), repositoryService);
   }
  }), PULL_REQUEST_ACTION(new Resolver() {
   @Override
   public String resolve(PullRequest pullRequest, PrnfsPullRequestAction prnfsPullRequestAction, StashUser stashUser,
     RepositoryService repositoryService, ApplicationPropertiesService propertiesService,
     PrnfsNotification prnfsNotification, Map<PrnfsRenderer.PrnfsVariable, Supplier<String>> variables) {
    return prnfsPullRequestAction.getName();
   }
  }), PULL_REQUEST_URL(new Resolver() {
   @Override
   public String resolve(PullRequest pullRequest, PrnfsPullRequestAction prnfsPullRequestAction, StashUser stashUser,
     RepositoryService repositoryService, ApplicationPropertiesService propertiesService,
     PrnfsNotification prnfsNotification, Map<PrnfsRenderer.PrnfsVariable, Supplier<String>> variables) {
    return getPullRequestUrl(propertiesService, pullRequest);
   }
  }), PULL_REQUEST_ID(new Resolver() {
   @Override
   public String resolve(PullRequest pullRequest, PrnfsPullRequestAction prnfsPullRequestAction, StashUser stashUser,
     RepositoryService repositoryService, ApplicationPropertiesService propertiesService,
     PrnfsNotification prnfsNotification, Map<PrnfsRenderer.PrnfsVariable, Supplier<String>> variables) {
    return pullRequest.getId() + "";
   }
  }), PULL_REQUEST_VERSION(new Resolver() {
   @Override
   public String resolve(PullRequest pullRequest, PrnfsPullRequestAction prnfsPullRequestAction, StashUser stashUser,
     RepositoryService repositoryService, ApplicationPropertiesService propertiesService,
     PrnfsNotification prnfsNotification, Map<PrnfsRenderer.PrnfsVariable, Supplier<String>> variables) {
    return pullRequest.getVersion() + "";
   }
  }), PULL_REQUEST_AUTHOR_ID(new Resolver() {
   @Override
   public String resolve(PullRequest pullRequest, PrnfsPullRequestAction prnfsPullRequestAction, StashUser stashUser,
     RepositoryService repositoryService, ApplicationPropertiesService propertiesService,
     PrnfsNotification prnfsNotification, Map<PrnfsRenderer.PrnfsVariable, Supplier<String>> variables) {
    return pullRequest.getAuthor().getUser().getId() + "";
   }
  }), PULL_REQUEST_AUTHOR_DISPLAY_NAME(new Resolver() {
   @Override
   public String resolve(PullRequest pullRequest, PrnfsPullRequestAction prnfsPullRequestAction, StashUser stashUser,
     RepositoryService repositoryService, ApplicationPropertiesService propertiesService,
     PrnfsNotification prnfsNotification, Map<PrnfsRenderer.PrnfsVariable, Supplier<String>> variables) {
    return pullRequest.getAuthor().getUser().getDisplayName();
   }
  }), PULL_REQUEST_AUTHOR_NAME(new Resolver() {
   @Override
   public String resolve(PullRequest pullRequest, PrnfsPullRequestAction prnfsPullRequestAction, StashUser stashUser,
     RepositoryService repositoryService, ApplicationPropertiesService propertiesService,
     PrnfsNotification prnfsNotification, Map<PrnfsRenderer.PrnfsVariable, Supplier<String>> variables) {
    return pullRequest.getAuthor().getUser().getName();
   }
  }), PULL_REQUEST_AUTHOR_EMAIL(new Resolver() {
   @Override
   public String resolve(PullRequest pullRequest, PrnfsPullRequestAction prnfsPullRequestAction, StashUser stashUser,
     RepositoryService repositoryService, ApplicationPropertiesService propertiesService,
     PrnfsNotification prnfsNotification, Map<PrnfsRenderer.PrnfsVariable, Supplier<String>> variables) {
    return pullRequest.getAuthor().getUser().getEmailAddress();
   }
  }), PULL_REQUEST_AUTHOR_SLUG(new Resolver() {
   @Override
   public String resolve(PullRequest pullRequest, PrnfsPullRequestAction prnfsPullRequestAction, StashUser stashUser,
     RepositoryService repositoryService, ApplicationPropertiesService propertiesService,
     PrnfsNotification prnfsNotification, Map<PrnfsRenderer.PrnfsVariable, Supplier<String>> variables) {
    return pullRequest.getAuthor().getUser().getSlug();
   }
  }), PULL_REQUEST_TO_HASH(new Resolver() {
   @Override
   public String resolve(PullRequest pullRequest, PrnfsPullRequestAction prnfsPullRequestAction, StashUser stashUser,
     RepositoryService repositoryService, ApplicationPropertiesService propertiesService,
     PrnfsNotification prnfsNotification, Map<PrnfsRenderer.PrnfsVariable, Supplier<String>> variables) {
    return pullRequest.getToRef().getLatestChangeset();
   }
  }), PULL_REQUEST_TO_ID(new Resolver() {
   @Override
   public String resolve(PullRequest pullRequest, PrnfsPullRequestAction prnfsPullRequestAction, StashUser stashUser,
     RepositoryService repositoryService, ApplicationPropertiesService propertiesService,
     PrnfsNotification prnfsNotification, Map<PrnfsRenderer.PrnfsVariable, Supplier<String>> variables) {
    return pullRequest.getToRef().getId();
   }
  }), PULL_REQUEST_TO_BRANCH(new Resolver() {
   @Override
   public String resolve(PullRequest pullRequest, PrnfsPullRequestAction prnfsPullRequestAction, StashUser stashUser,
     RepositoryService repositoryService, ApplicationPropertiesService propertiesService,
     PrnfsNotification prnfsNotification, Map<PrnfsRenderer.PrnfsVariable, Supplier<String>> variables) {
    return pullRequest.getToRef().getDisplayId();
   }
  }), PULL_REQUEST_TO_REPO_ID(new Resolver() {
   @Override
   public String resolve(PullRequest pullRequest, PrnfsPullRequestAction prnfsPullRequestAction, StashUser stashUser,
     RepositoryService repositoryService, ApplicationPropertiesService propertiesService,
     PrnfsNotification prnfsNotification, Map<PrnfsRenderer.PrnfsVariable, Supplier<String>> variables) {
    return pullRequest.getToRef().getRepository().getId() + "";
   }
  }), PULL_REQUEST_TO_REPO_NAME(new Resolver() {
   @Override
   public String resolve(PullRequest pullRequest, PrnfsPullRequestAction prnfsPullRequestAction, StashUser stashUser,
     RepositoryService repositoryService, ApplicationPropertiesService propertiesService,
     PrnfsNotification prnfsNotification, Map<PrnfsRenderer.PrnfsVariable, Supplier<String>> variables) {
    return pullRequest.getToRef().getRepository().getName() + "";
   }
  }), PULL_REQUEST_TO_REPO_PROJECT_ID(new Resolver() {
   @Override
   public String resolve(PullRequest pullRequest, PrnfsPullRequestAction prnfsPullRequestAction, StashUser stashUser,
     RepositoryService repositoryService, ApplicationPropertiesService propertiesService,
     PrnfsNotification prnfsNotification, Map<PrnfsRenderer.PrnfsVariable, Supplier<String>> variables) {
    return pullRequest.getToRef().getRepository().getProject().getId() + "";
   }
  }), PULL_REQUEST_TO_REPO_PROJECT_KEY(new Resolver() {
   @Override
   public String resolve(PullRequest pullRequest, PrnfsPullRequestAction prnfsPullRequestAction, StashUser stashUser,
     RepositoryService repositoryService, ApplicationPropertiesService propertiesService,
     PrnfsNotification prnfsNotification, Map<PrnfsRenderer.PrnfsVariable, Supplier<String>> variables) {
    return pullRequest.getToRef().getRepository().getProject().getKey();
   }
  }), PULL_REQUEST_TO_REPO_SLUG(new Resolver() {
   @Override
   public String resolve(PullRequest pullRequest, PrnfsPullRequestAction prnfsPullRequestAction, StashUser stashUser,
     RepositoryService repositoryService, ApplicationPropertiesService propertiesService,
     PrnfsNotification prnfsNotification, Map<PrnfsRenderer.PrnfsVariable, Supplier<String>> variables) {
    return pullRequest.getToRef().getRepository().getSlug() + "";
   }
  }), PULL_REQUEST_TO_SSH_CLONE_URL(new Resolver() {
   @Override
   public String resolve(PullRequest pullRequest, PrnfsPullRequestAction prnfsPullRequestAction, StashUser stashUser,
     RepositoryService repositoryService, ApplicationPropertiesService propertiesService,
     PrnfsNotification prnfsNotification, Map<PrnfsRenderer.PrnfsVariable, Supplier<String>> variables) {
    return cloneUrlFromRepository(ssh, pullRequest.getToRef().getRepository(), repositoryService);
   }
  }), PULL_REQUEST_TO_HTTP_CLONE_URL(new Resolver() {
   @Override
   public String resolve(PullRequest pullRequest, PrnfsPullRequestAction prnfsPullRequestAction, StashUser stashUser,
     RepositoryService repositoryService, ApplicationPropertiesService propertiesService,
     PrnfsNotification prnfsNotification, Map<PrnfsRenderer.PrnfsVariable, Supplier<String>> variables) {
    return cloneUrlFromRepository(http, pullRequest.getToRef().getRepository(), repositoryService);
   }
  }), PULL_REQUEST_COMMENT_TEXT(new Resolver() {
   @Override
   public String resolve(PullRequest pullRequest, PrnfsPullRequestAction prnfsPullRequestAction, StashUser stashUser,
     RepositoryService repositoryService, ApplicationPropertiesService propertiesService,
     PrnfsNotification prnfsNotification, Map<PrnfsRenderer.PrnfsVariable, Supplier<String>> variables) {
    return getOrEmpty(variables, PULL_REQUEST_COMMENT_TEXT);
   }
  }), PULL_REQUEST_USER_DISPLAY_NAME(new Resolver() {
   @Override
   public String resolve(PullRequest pullRequest, PrnfsPullRequestAction prnfsPullRequestAction, StashUser stashUser,
     RepositoryService repositoryService, ApplicationPropertiesService propertiesService,
     PrnfsNotification prnfsNotification, Map<PrnfsRenderer.PrnfsVariable, Supplier<String>> variables) {
    return stashUser.getDisplayName();
   }
  }), PULL_REQUEST_USER_EMAIL_ADDRESS(new Resolver() {
   @Override
   public String resolve(PullRequest pullRequest, PrnfsPullRequestAction prnfsPullRequestAction, StashUser stashUser,
     RepositoryService repositoryService, ApplicationPropertiesService propertiesService,
     PrnfsNotification prnfsNotification, Map<PrnfsRenderer.PrnfsVariable, Supplier<String>> variables) {
    return stashUser.getEmailAddress();
   }
  }), PULL_REQUEST_USER_ID(new Resolver() {
   @Override
   public String resolve(PullRequest pullRequest, PrnfsPullRequestAction pullRequestAction, StashUser stashUser,
     RepositoryService repositoryService, ApplicationPropertiesService propertiesService,
     PrnfsNotification prnfsNotification, Map<PrnfsRenderer.PrnfsVariable, Supplier<String>> variables) {
    return stashUser.getId() + "";
   }
  }), PULL_REQUEST_USER_NAME(new Resolver() {
   @Override
   public String resolve(PullRequest pullRequest, PrnfsPullRequestAction pullRequestAction, StashUser stashUser,
     RepositoryService repositoryService, ApplicationPropertiesService propertiesService,
     PrnfsNotification prnfsNotification, Map<PrnfsRenderer.PrnfsVariable, Supplier<String>> variables) {
    return stashUser.getName();
   }
  }), PULL_REQUEST_USER_SLUG(new Resolver() {
   @Override
   public String resolve(PullRequest pullRequest, PrnfsPullRequestAction pullRequestAction, StashUser stashUser,
     RepositoryService repositoryService, ApplicationPropertiesService propertiesService,
     PrnfsNotification prnfsNotification, Map<PrnfsRenderer.PrnfsVariable, Supplier<String>> variables) {
    return stashUser.getSlug();
   }
  }), BUTTON_TRIGGER_TITLE(new Resolver() {
   @Override
   public String resolve(PullRequest pullRequest, PrnfsPullRequestAction pullRequestAction, StashUser stashUser,
     RepositoryService repositoryService, ApplicationPropertiesService propertiesService,
     PrnfsNotification prnfsNotification, Map<PrnfsRenderer.PrnfsVariable, Supplier<String>> variables) {
    return getOrEmpty(variables, BUTTON_TRIGGER_TITLE);
   }
  }), INJECTION_URL_VALUE(new Resolver() {
   @Override
   public String resolve(PullRequest pullRequest, PrnfsPullRequestAction pullRequestAction, StashUser stashUser,
     RepositoryService repositoryService, ApplicationPropertiesService propertiesService,
     PrnfsNotification prnfsNotification, Map<PrnfsRenderer.PrnfsVariable, Supplier<String>> variables) {
    if (!prnfsNotification.getInjectionUrl().isPresent()) {
     return "";
    }
    UrlInvoker urlInvoker = urlInvoker() //
      .withUrlParam(prnfsNotification.getInjectionUrl().get()) //
      .withMethod(GET)//
      .withProxyServer(prnfsNotification.getProxyServer()) //
      .withProxyPort(prnfsNotification.getProxyPort()) //
      .withProxyUser(prnfsNotification.getProxyUser()) //
      .withProxyPassword(prnfsNotification.getProxyPassword());
    PrnfsRenderer.invoker.invoke(urlInvoker);
    String rawResponse = urlInvoker.getResponseString().trim();
    if (prnfsNotification.getInjectionUrlRegexp().isPresent()) {
     Matcher m = compile(prnfsNotification.getInjectionUrlRegexp().get()).matcher(rawResponse);
     if (!m.find()) {
      logger.severe("Could not find \"" + prnfsNotification.getInjectionUrlRegexp().get() + "\" in:\n" + rawResponse);
      return "";
     }
     return m.group(1);
    } else {
     return rawResponse;
    }
   }
  }), PULL_REQUEST_TITLE(new Resolver() {
   @Override
   public String resolve(PullRequest pullRequest, PrnfsPullRequestAction pullRequestAction, StashUser stashUser,
     RepositoryService repositoryService, ApplicationPropertiesService propertiesService,
     PrnfsNotification prnfsNotification, Map<PrnfsVariable, Supplier<String>> variables) {
    return pullRequest.getTitle();
   }
  }), PULL_REQUEST_REVIEWERS_APPROVED_COUNT(new Resolver() {
   @Override
   public String resolve(PullRequest pullRequest, PrnfsPullRequestAction pullRequestAction, StashUser ApplicationUser,
     RepositoryService repositoryService, ApplicationPropertiesService propertiesService,
     PrnfsNotification prnfbNotification, Map<PrnfsVariable, Supplier<String>> variables) {
    return Integer.toString(newArrayList(filter(pullRequest.getReviewers(), isApproved)).size());
   }
  }), PULL_REQUEST_PARTICIPANTS_APPROVED_COUNT(new Resolver() {
   @Override
   public String resolve(PullRequest pullRequest, PrnfsPullRequestAction pullRequestAction, StashUser ApplicationUser,
     RepositoryService repositoryService, ApplicationPropertiesService propertiesService,
     PrnfsNotification prnfbNotification, Map<PrnfsVariable, Supplier<String>> variables) {
    return Integer.toString(newArrayList(filter(pullRequest.getParticipants(), isApproved)).size());
   }
  });
  ;
  private static final Predicate<PullRequestParticipant> isApproved = new Predicate<PullRequestParticipant>() {
   @Override
   public boolean apply(PullRequestParticipant input) {
    return input.isApproved();
   }
  };

  private Resolver resolver;

  private static String cloneUrlFromRepository(REPO_PROTOCOL protocol, Repository repository,
    RepositoryService repositoryService) {
   RepositoryCloneLinksRequest request = new RepositoryCloneLinksRequest.Builder().protocol(protocol.name())
     .repository(repository).build();
   final Set<NamedLink> cloneLinks = repositoryService.getCloneLinks(request);
   return cloneLinks.iterator().hasNext() ? cloneLinks.iterator().next().getHref() : "";
  }

  private static String getPullRequestUrl(ApplicationPropertiesService propertiesService, PullRequest pullRequest) {
   return propertiesService.getBaseUrl() + "/projects/" + pullRequest.getToRef().getRepository().getProject().getKey()
     + "/repos/" + pullRequest.getToRef().getRepository().getName() + "/pull-requests/" + pullRequest.getId();
  }

  PrnfsVariable(Resolver resolver) {
   this.resolver = resolver;
  }

  public String resolve(PullRequest pullRequest, PrnfsPullRequestAction pullRequestAction, StashUser stashUser,
    RepositoryService repositoryService, ApplicationPropertiesService propertiesService,
    PrnfsNotification prnfsNotification, Map<PrnfsVariable, Supplier<String>> variables) {
   return resolver.resolve(pullRequest, pullRequestAction, stashUser, repositoryService, propertiesService,
     prnfsNotification, variables);
  }

  private static String getOrEmpty(Map<PrnfsVariable, Supplier<String>> variables, PrnfsVariable variable) {
   if (variables.get(variable) == null) {
    return "";
   }
   return variables.get(variable).get();
  }
 }

 public interface Resolver {
  String resolve(PullRequest pullRequest, PrnfsPullRequestAction pullRequestAction, StashUser stashUser,
    RepositoryService repositoryService, ApplicationPropertiesService propertiesService,
    PrnfsNotification prnfsNotification, Map<PrnfsVariable, Supplier<String>> variables);
 }

 private final RepositoryService repositoryService;
 private final PrnfsNotification prnfsNotification;
 private final PullRequest pullRequest;
 private final PrnfsPullRequestAction pullRequestAction;
 private final StashUser stashUser;
 private final ApplicationPropertiesService propertiesService;
 /**
  * Contains special variables that are only available for specific events like
  * {@link PrnfsVariable#BUTTON_TRIGGER_TITLE} and
  * {@link PrnfsVariable#PULL_REQUEST_COMMENT_TEXT}.
  */
 private final Map<PrnfsVariable, Supplier<String>> variables;

 /**
  * @param variables
  *         {@link #variables}
  */
 public PrnfsRenderer(PullRequest pullRequest, PrnfsPullRequestAction pullRequestAction, StashUser stashUser,
   RepositoryService repositoryService, ApplicationPropertiesService propertiesService,
   PrnfsNotification prnfsNotification, Map<PrnfsVariable, Supplier<String>> variables) {
  this.pullRequest = pullRequest;
  this.pullRequestAction = pullRequestAction;
  this.stashUser = stashUser;
  this.repositoryService = repositoryService;
  this.prnfsNotification = prnfsNotification;
  this.propertiesService = propertiesService;
  this.variables = variables;
 }

 public String render(String string) {
  for (final PrnfsVariable variable : PrnfsVariable.values()) {
   final String regExpStr = "\\$\\{" + variable.name() + "\\}";
   if (string.contains(regExpStr.replaceAll("\\\\", ""))) {
    string = string.replaceAll(regExpStr, variable.resolve(pullRequest, pullRequestAction, stashUser,
      repositoryService, propertiesService, prnfsNotification, variables));
   }
  }
  return string;
 }
}
