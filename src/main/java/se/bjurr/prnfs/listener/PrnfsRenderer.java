package se.bjurr.prnfs.listener;

import static se.bjurr.prnfs.listener.PrnfsRenderer.REPO_PROTOCOL.http;
import static se.bjurr.prnfs.listener.PrnfsRenderer.REPO_PROTOCOL.ssh;

import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import se.bjurr.prnfs.settings.PrnfsNotification;

import com.atlassian.stash.event.pull.PullRequestCommentAddedEvent;
import com.atlassian.stash.event.pull.PullRequestEvent;
import com.atlassian.stash.pull.PullRequest;
import com.atlassian.stash.repository.Repository;
import com.atlassian.stash.repository.RepositoryCloneLinksRequest;
import com.atlassian.stash.repository.RepositoryService;
import com.atlassian.stash.server.ApplicationPropertiesService;
import com.atlassian.stash.user.StashUser;
import com.atlassian.stash.util.NamedLink;
import com.google.common.base.Supplier;

public class PrnfsRenderer {

 public enum REPO_PROTOCOL {
  ssh, http
 }

 public enum PrnfsVariable {
  PULL_REQUEST_FROM_HASH(new Resolver() {
   @SuppressWarnings("deprecation")
   @Override
   public String resolve(PullRequest pullRequest, PrnfsPullRequestAction prnfsPullRequestAction, StashUser stashUser,
     RepositoryService repositoryService, ApplicationPropertiesService propertiesService,
     PrnfsNotification prnfsNotification, PullRequestEvent pullRequestEvent) {
    return pullRequest.getFromRef().getLatestChangeset();
   }
  }), PULL_REQUEST_FROM_ID(new Resolver() {
   @Override
   public String resolve(PullRequest pullRequest, PrnfsPullRequestAction prnfsPullRequestAction, StashUser stashUser,
     RepositoryService repositoryService, ApplicationPropertiesService propertiesService,
     PrnfsNotification prnfsNotification, PullRequestEvent pullRequestEvent) {
    return pullRequest.getFromRef().getId();
   }
  }), PULL_REQUEST_FROM_BRANCH(new Resolver() {
   @Override
   public String resolve(PullRequest pullRequest, PrnfsPullRequestAction prnfsPullRequestAction, StashUser stashUser,
     RepositoryService repositoryService, ApplicationPropertiesService propertiesService,
     PrnfsNotification prnfsNotification, PullRequestEvent pullRequestEvent) {
    return pullRequest.getFromRef().getDisplayId();
   }
  }), PULL_REQUEST_FROM_REPO_ID(new Resolver() {
   @Override
   public String resolve(PullRequest pullRequest, PrnfsPullRequestAction prnfsPullRequestAction, StashUser stashUser,
     RepositoryService repositoryService, ApplicationPropertiesService propertiesService,
     PrnfsNotification prnfsNotification, PullRequestEvent pullRequestEvent) {
    return pullRequest.getFromRef().getRepository().getId() + "";
   }
  }), PULL_REQUEST_FROM_REPO_NAME(new Resolver() {
   @Override
   public String resolve(PullRequest pullRequest, PrnfsPullRequestAction prnfsPullRequestAction, StashUser stashUser,
     RepositoryService repositoryService, ApplicationPropertiesService propertiesService,
     PrnfsNotification prnfsNotification, PullRequestEvent pullRequestEvent) {
    return pullRequest.getFromRef().getRepository().getName() + "";
   }
  }), PULL_REQUEST_FROM_REPO_PROJECT_ID(new Resolver() {
   @Override
   public String resolve(PullRequest pullRequest, PrnfsPullRequestAction prnfsPullRequestAction, StashUser stashUser,
     RepositoryService repositoryService, ApplicationPropertiesService propertiesService,
     PrnfsNotification prnfsNotification, PullRequestEvent pullRequestEvent) {
    return pullRequest.getFromRef().getRepository().getProject().getId() + "";
   }
  }), PULL_REQUEST_FROM_REPO_PROJECT_KEY(new Resolver() {
   @Override
   public String resolve(PullRequest pullRequest, PrnfsPullRequestAction prnfsPullRequestAction, StashUser stashUser,
     RepositoryService repositoryService, ApplicationPropertiesService propertiesService,
     PrnfsNotification prnfsNotification, PullRequestEvent pullRequestEvent) {
    return pullRequest.getFromRef().getRepository().getProject().getKey();
   }
  }), PULL_REQUEST_FROM_REPO_SLUG(new Resolver() {
   @Override
   public String resolve(PullRequest pullRequest, PrnfsPullRequestAction prnfsPullRequestAction, StashUser stashUser,
     RepositoryService repositoryService, ApplicationPropertiesService propertiesService,
     PrnfsNotification prnfsNotification, PullRequestEvent pullRequestEvent) {
    return pullRequest.getFromRef().getRepository().getSlug() + "";
   }
  }), PULL_REQUEST_FROM_SSH_CLONE_URL(new Resolver() {
   @Override
   public String resolve(PullRequest pullRequest, PrnfsPullRequestAction prnfsPullRequestAction, StashUser stashUser,
     RepositoryService repositoryService, ApplicationPropertiesService propertiesService,
     PrnfsNotification prnfsNotification, PullRequestEvent pullRequestEvent) {
    return cloneUrlFromRepository(ssh, pullRequest.getFromRef().getRepository(), repositoryService);
   }
  }), PULL_REQUEST_FROM_HTTP_CLONE_URL(new Resolver() {
   @Override
   public String resolve(PullRequest pullRequest, PrnfsPullRequestAction prnfsPullRequestAction, StashUser stashUser,
     RepositoryService repositoryService, ApplicationPropertiesService propertiesService,
     PrnfsNotification prnfsNotification, PullRequestEvent pullRequestEvent) {
    return cloneUrlFromRepository(http, pullRequest.getFromRef().getRepository(), repositoryService);
   }
  }), PULL_REQUEST_ACTION(new Resolver() {
   @Override
   public String resolve(PullRequest pullRequest, PrnfsPullRequestAction prnfsPullRequestAction, StashUser stashUser,
     RepositoryService repositoryService, ApplicationPropertiesService propertiesService,
     PrnfsNotification prnfsNotification, PullRequestEvent pullRequestEvent) {
    return prnfsPullRequestAction.getName();
   }
  }), PULL_REQUEST_URL(new Resolver() {
   @Override
   public String resolve(PullRequest pullRequest, PrnfsPullRequestAction prnfsPullRequestAction, StashUser stashUser,
     RepositoryService repositoryService, ApplicationPropertiesService propertiesService,
     PrnfsNotification prnfsNotification, PullRequestEvent pullRequestEvent) {
    return getPullRequestUrl(propertiesService, pullRequestEvent.getPullRequest());
   }
  }), PULL_REQUEST_ID(new Resolver() {
   @Override
   public String resolve(PullRequest pullRequest, PrnfsPullRequestAction prnfsPullRequestAction, StashUser stashUser,
     RepositoryService repositoryService, ApplicationPropertiesService propertiesService,
     PrnfsNotification prnfsNotification, PullRequestEvent pullRequestEvent) {
    return pullRequest.getId() + "";
   }
  }), PULL_REQUEST_VERSION(new Resolver() {
   @Override
   public String resolve(PullRequest pullRequest, PrnfsPullRequestAction prnfsPullRequestAction, StashUser stashUser,
     RepositoryService repositoryService, ApplicationPropertiesService propertiesService,
     PrnfsNotification prnfsNotification, PullRequestEvent pullRequestEvent) {
    return pullRequest.getVersion() + "";
   }
  }), PULL_REQUEST_AUTHOR_ID(new Resolver() {
   @Override
   public String resolve(PullRequest pullRequest, PrnfsPullRequestAction prnfsPullRequestAction, StashUser stashUser,
     RepositoryService repositoryService, ApplicationPropertiesService propertiesService,
     PrnfsNotification prnfsNotification, PullRequestEvent pullRequestEvent) {
    return pullRequest.getAuthor().getUser().getId() + "";
   }
  }), PULL_REQUEST_AUTHOR_DISPLAY_NAME(new Resolver() {
   @Override
   public String resolve(PullRequest pullRequest, PrnfsPullRequestAction prnfsPullRequestAction, StashUser stashUser,
     RepositoryService repositoryService, ApplicationPropertiesService propertiesService,
     PrnfsNotification prnfsNotification, PullRequestEvent pullRequestEvent) {
    return pullRequest.getAuthor().getUser().getDisplayName();
   }
  }), PULL_REQUEST_AUTHOR_NAME(new Resolver() {
   @Override
   public String resolve(PullRequest pullRequest, PrnfsPullRequestAction prnfsPullRequestAction, StashUser stashUser,
     RepositoryService repositoryService, ApplicationPropertiesService propertiesService,
     PrnfsNotification prnfsNotification, PullRequestEvent pullRequestEvent) {
    return pullRequest.getAuthor().getUser().getName();
   }
  }), PULL_REQUEST_AUTHOR_EMAIL(new Resolver() {
   @Override
   public String resolve(PullRequest pullRequest, PrnfsPullRequestAction prnfsPullRequestAction, StashUser stashUser,
     RepositoryService repositoryService, ApplicationPropertiesService propertiesService,
     PrnfsNotification prnfsNotification, PullRequestEvent pullRequestEvent) {
    return pullRequest.getAuthor().getUser().getEmailAddress();
   }
  }), PULL_REQUEST_AUTHOR_SLUG(new Resolver() {
   @Override
   public String resolve(PullRequest pullRequest, PrnfsPullRequestAction prnfsPullRequestAction, StashUser stashUser,
     RepositoryService repositoryService, ApplicationPropertiesService propertiesService,
     PrnfsNotification prnfsNotification, PullRequestEvent pullRequestEvent) {
    return pullRequest.getAuthor().getUser().getSlug();
   }
  }), PULL_REQUEST_TO_HASH(new Resolver() {
   @Override
   public String resolve(PullRequest pullRequest, PrnfsPullRequestAction prnfsPullRequestAction, StashUser stashUser,
     RepositoryService repositoryService, ApplicationPropertiesService propertiesService,
     PrnfsNotification prnfsNotification, PullRequestEvent pullRequestEvent) {
    return pullRequest.getToRef().getLatestChangeset();
   }
  }), PULL_REQUEST_TO_ID(new Resolver() {
   @Override
   public String resolve(PullRequest pullRequest, PrnfsPullRequestAction prnfsPullRequestAction, StashUser stashUser,
     RepositoryService repositoryService, ApplicationPropertiesService propertiesService,
     PrnfsNotification prnfsNotification, PullRequestEvent pullRequestEvent) {
    return pullRequest.getToRef().getId();
   }
  }), PULL_REQUEST_TO_BRANCH(new Resolver() {
   @Override
   public String resolve(PullRequest pullRequest, PrnfsPullRequestAction prnfsPullRequestAction, StashUser stashUser,
     RepositoryService repositoryService, ApplicationPropertiesService propertiesService,
     PrnfsNotification prnfsNotification, PullRequestEvent pullRequestEvent) {
    return pullRequest.getToRef().getDisplayId();
   }
  }), PULL_REQUEST_TO_REPO_ID(new Resolver() {
   @Override
   public String resolve(PullRequest pullRequest, PrnfsPullRequestAction prnfsPullRequestAction, StashUser stashUser,
     RepositoryService repositoryService, ApplicationPropertiesService propertiesService,
     PrnfsNotification prnfsNotification, PullRequestEvent pullRequestEvent) {
    return pullRequest.getToRef().getRepository().getId() + "";
   }
  }), PULL_REQUEST_TO_REPO_NAME(new Resolver() {
   @Override
   public String resolve(PullRequest pullRequest, PrnfsPullRequestAction prnfsPullRequestAction, StashUser stashUser,
     RepositoryService repositoryService, ApplicationPropertiesService propertiesService,
     PrnfsNotification prnfsNotification, PullRequestEvent pullRequestEvent) {
    return pullRequest.getToRef().getRepository().getName() + "";
   }
  }), PULL_REQUEST_TO_REPO_PROJECT_ID(new Resolver() {
   @Override
   public String resolve(PullRequest pullRequest, PrnfsPullRequestAction prnfsPullRequestAction, StashUser stashUser,
     RepositoryService repositoryService, ApplicationPropertiesService propertiesService,
     PrnfsNotification prnfsNotification, PullRequestEvent pullRequestEvent) {
    return pullRequest.getToRef().getRepository().getProject().getId() + "";
   }
  }), PULL_REQUEST_TO_REPO_PROJECT_KEY(new Resolver() {
   @Override
   public String resolve(PullRequest pullRequest, PrnfsPullRequestAction prnfsPullRequestAction, StashUser stashUser,
     RepositoryService repositoryService, ApplicationPropertiesService propertiesService,
     PrnfsNotification prnfsNotification, PullRequestEvent pullRequestEvent) {
    return pullRequest.getToRef().getRepository().getProject().getKey();
   }
  }), PULL_REQUEST_TO_REPO_SLUG(new Resolver() {
   @Override
   public String resolve(PullRequest pullRequest, PrnfsPullRequestAction prnfsPullRequestAction, StashUser stashUser,
     RepositoryService repositoryService, ApplicationPropertiesService propertiesService,
     PrnfsNotification prnfsNotification, PullRequestEvent pullRequestEvent) {
    return pullRequest.getToRef().getRepository().getSlug() + "";
   }
  }), PULL_REQUEST_TO_SSH_CLONE_URL(new Resolver() {
   @Override
   public String resolve(PullRequest pullRequest, PrnfsPullRequestAction prnfsPullRequestAction, StashUser stashUser,
     RepositoryService repositoryService, ApplicationPropertiesService propertiesService,
     PrnfsNotification prnfsNotification, PullRequestEvent pullRequestEvent) {
    return cloneUrlFromRepository(ssh, pullRequest.getToRef().getRepository(), repositoryService);
   }
  }), PULL_REQUEST_TO_HTTP_CLONE_URL(new Resolver() {
   @Override
   public String resolve(PullRequest pullRequest, PrnfsPullRequestAction prnfsPullRequestAction, StashUser stashUser,
     RepositoryService repositoryService, ApplicationPropertiesService propertiesService,
     PrnfsNotification prnfsNotification, PullRequestEvent pullRequestEvent) {
    return cloneUrlFromRepository(http, pullRequest.getToRef().getRepository(), repositoryService);
   }
  }), PULL_REQUEST_COMMENT_TEXT(new Resolver() {
   @Override
   public String resolve(PullRequest pullRequest, PrnfsPullRequestAction prnfsPullRequestAction, StashUser stashUser,
     RepositoryService repositoryService, ApplicationPropertiesService propertiesService,
     PrnfsNotification prnfsNotification, PullRequestEvent pullRequestEvent) {
    if (pullRequestEvent instanceof PullRequestCommentAddedEvent) {
     return ((PullRequestCommentAddedEvent) pullRequestEvent).getComment().getText();
    } else {
     return "";
    }
   }
  }), PULL_REQUEST_USER_DISPLAY_NAME(new Resolver() {
   @Override
   public String resolve(PullRequest pullRequest, PrnfsPullRequestAction prnfsPullRequestAction, StashUser stashUser,
     RepositoryService repositoryService, ApplicationPropertiesService propertiesService,
     PrnfsNotification prnfsNotification, PullRequestEvent pullRequestEvent) {
    return stashUser.getDisplayName();
   }
  }), PULL_REQUEST_USER_EMAIL_ADDRESS(new Resolver() {
   @Override
   public String resolve(PullRequest pullRequest, PrnfsPullRequestAction prnfsPullRequestAction, StashUser stashUser,
     RepositoryService repositoryService, ApplicationPropertiesService propertiesService,
     PrnfsNotification prnfsNotification, PullRequestEvent pullRequestEvent) {
    return stashUser.getEmailAddress();
   }
  }), PULL_REQUEST_USER_ID(new Resolver() {
   @Override
   public String resolve(PullRequest pullRequest, PrnfsPullRequestAction pullRequestAction, StashUser stashUser,
     RepositoryService repositoryService, ApplicationPropertiesService propertiesService,
     PrnfsNotification prnfsNotification, PullRequestEvent pullRequestEvent) {
    return stashUser.getId() + "";
   }
  }), PULL_REQUEST_USER_NAME(new Resolver() {
   @Override
   public String resolve(PullRequest pullRequest, PrnfsPullRequestAction pullRequestAction, StashUser stashUser,
     RepositoryService repositoryService, ApplicationPropertiesService propertiesService,
     PrnfsNotification prnfsNotification, PullRequestEvent pullRequestEvent) {
    return stashUser.getName();
   }
  }), PULL_REQUEST_USER_SLUG(new Resolver() {
   @Override
   public String resolve(PullRequest pullRequest, PrnfsPullRequestAction pullRequestAction, StashUser stashUser,
     RepositoryService repositoryService, ApplicationPropertiesService propertiesService,
     PrnfsNotification prnfsNotification, PullRequestEvent pullRequestEvent) {
    return stashUser.getSlug();
   }
  }), BUTTON_TRIGGER_TITLE(new Resolver() {
   @Override
   public String resolve(PullRequest pullRequest, PrnfsPullRequestAction pullRequestAction, StashUser stashUser,
     RepositoryService repositoryService, ApplicationPropertiesService propertiesService,
     PrnfsNotification prnfsNotification, PullRequestEvent pullRequestEvent) {
    return "";
   }
  });

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
    PrnfsNotification prnfsNotification, PullRequestEvent pullRequestEvent) {
   return resolver.resolve(pullRequest, pullRequestAction, stashUser, repositoryService, propertiesService,
     prnfsNotification, pullRequestEvent);
  }
 }

 public interface Resolver {
  String resolve(PullRequest pullRequest, PrnfsPullRequestAction pullRequestAction, StashUser stashUser,
    RepositoryService repositoryService, ApplicationPropertiesService propertiesService,
    PrnfsNotification prnfsNotification, PullRequestEvent pullRequestEvent);
 }

 private final RepositoryService repositoryService;
 private final PrnfsNotification prnfsNotification;
 private final PullRequest pullRequest;
 private final PrnfsPullRequestAction pullRequestAction;
 private final StashUser stashUser;
 private final PullRequestEvent pullRequestEvent;
 private final ApplicationPropertiesService propertiesService;
 private final Map<PrnfsVariable, Supplier<String>> variables;

 /**
  * @param prnfsNotification
  *         May be null, if triggered via trigger button in Pull Request view
  * @param pullRequestEvent
  *         May be null, if triggered via trigger button in Pull Request view
  */
 public PrnfsRenderer(PullRequest pullRequest, PrnfsPullRequestAction pullRequestAction, StashUser stashUser,
   RepositoryService repositoryService, ApplicationPropertiesService propertiesService,
   @Nullable PrnfsNotification prnfsNotification, @Nullable PullRequestEvent pullRequestEvent,
   Map<PrnfsVariable, Supplier<String>> variables) {
  this.pullRequest = pullRequest;
  this.pullRequestAction = pullRequestAction;
  this.pullRequestEvent = pullRequestEvent;
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
    if (variables.containsKey(variable)) {
     string = string.replaceAll(regExpStr, variables.get(variable).get());
    } else {
     string = string.replaceAll(regExpStr, variable.resolve(pullRequest, pullRequestAction, stashUser,
       repositoryService, propertiesService, prnfsNotification, pullRequestEvent));
    }
   }
  }
  return string;
 }
}
