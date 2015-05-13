package se.bjurr.prnfs.listener;

import static se.bjurr.prnfs.listener.PrnfsPullRequestAction.fromPullRequestEvent;
import static se.bjurr.prnfs.listener.PrnfsRenderer.REPO_PROTOCOL.http;
import static se.bjurr.prnfs.listener.PrnfsRenderer.REPO_PROTOCOL.ssh;

import java.util.Set;

import com.atlassian.stash.event.pull.PullRequestCommentAddedEvent;
import com.atlassian.stash.event.pull.PullRequestEvent;
import com.atlassian.stash.repository.Repository;
import com.atlassian.stash.repository.RepositoryCloneLinksRequest;
import com.atlassian.stash.repository.RepositoryService;
import com.atlassian.stash.util.NamedLink;

public class PrnfsRenderer {

 public enum REPO_PROTOCOL {
  ssh, http
 }

 public enum PrnfsVariable {
  PULL_REQUEST_FROM_HASH(new Resolver() {
   @SuppressWarnings("deprecation")
   @Override
   public String resolve(PullRequestEvent pullRequestEvent, RepositoryService repositoryService) {
    return pullRequestEvent.getPullRequest().getFromRef().getLatestChangeset();
   }
  }), PULL_REQUEST_FROM_ID(new Resolver() {
   @Override
   public String resolve(PullRequestEvent pullRequestEvent, RepositoryService repositoryService) {
    return pullRequestEvent.getPullRequest().getFromRef().getId();
   }
  }), PULL_REQUEST_FROM_BRANCH(new Resolver() {
   @Override
   public String resolve(PullRequestEvent pullRequestEvent, RepositoryService repositoryService) {
    return pullRequestEvent.getPullRequest().getFromRef().getDisplayId();
   }
  }), PULL_REQUEST_FROM_REPO_ID(new Resolver() {
   @Override
   public String resolve(PullRequestEvent pullRequestEvent, RepositoryService repositoryService) {
    return pullRequestEvent.getPullRequest().getFromRef().getRepository().getId() + "";
   }
  }), PULL_REQUEST_FROM_REPO_NAME(new Resolver() {
   @Override
   public String resolve(PullRequestEvent pullRequestEvent, RepositoryService repositoryService) {
    return pullRequestEvent.getPullRequest().getFromRef().getRepository().getName() + "";
   }
  }), PULL_REQUEST_FROM_REPO_PROJECT_ID(new Resolver() {
   @Override
   public String resolve(PullRequestEvent pullRequestEvent, RepositoryService repositoryService) {
    return pullRequestEvent.getPullRequest().getFromRef().getRepository().getProject().getId() + "";
   }
  }), PULL_REQUEST_FROM_REPO_PROJECT_KEY(new Resolver() {
   @Override
   public String resolve(PullRequestEvent pullRequestEvent, RepositoryService repositoryService) {
    return pullRequestEvent.getPullRequest().getFromRef().getRepository().getProject().getKey();
   }
  }), PULL_REQUEST_FROM_REPO_SLUG(new Resolver() {
   @Override
   public String resolve(PullRequestEvent pullRequestEvent, RepositoryService repositoryService) {
    return pullRequestEvent.getPullRequest().getFromRef().getRepository().getSlug() + "";
   }
  }), PULL_REQUEST_FROM_SSH_CLONE_URL(new Resolver() {
   @Override
   public String resolve(PullRequestEvent pullRequestEvent, RepositoryService repositoryService) {
    return cloneUrlFromRepository(ssh, pullRequestEvent.getPullRequest().getFromRef().getRepository(),
      repositoryService);
   }
  }), PULL_REQUEST_FROM_HTTP_CLONE_URL(new Resolver() {
   @Override
   public String resolve(PullRequestEvent pullRequestEvent, RepositoryService repositoryService) {
    return cloneUrlFromRepository(http, pullRequestEvent.getPullRequest().getFromRef().getRepository(),
      repositoryService);
   }
  }), PULL_REQUEST_ACTION(new Resolver() {
   @Override
   public String resolve(PullRequestEvent pullRequestEvent, RepositoryService repositoryService) {
    return fromPullRequestEvent(pullRequestEvent).getName();
   }
  }), PULL_REQUEST_ID(new Resolver() {
   @Override
   public String resolve(PullRequestEvent pullRequestEvent, RepositoryService repositoryService) {
    return pullRequestEvent.getPullRequest().getId() + "";
   }
  }), PULL_REQUEST_VERSION(new Resolver() {
   @Override
   public String resolve(PullRequestEvent pullRequestEvent, RepositoryService repositoryService) {
    return pullRequestEvent.getPullRequest().getVersion() + "";
   }
  }), PULL_REQUEST_AUTHOR_ID(new Resolver() {
   @Override
   public String resolve(PullRequestEvent pullRequestEvent, RepositoryService repositoryService) {
    return pullRequestEvent.getPullRequest().getAuthor().getUser().getId() + "";
   }
  }), PULL_REQUEST_AUTHOR_DISPLAY_NAME(new Resolver() {
   @Override
   public String resolve(PullRequestEvent pullRequestEvent, RepositoryService repositoryService) {
    return pullRequestEvent.getPullRequest().getAuthor().getUser().getDisplayName();
   }
  }), PULL_REQUEST_AUTHOR_NAME(new Resolver() {
   @Override
   public String resolve(PullRequestEvent pullRequestEvent, RepositoryService repositoryService) {
    return pullRequestEvent.getPullRequest().getAuthor().getUser().getName();
   }
  }), PULL_REQUEST_AUTHOR_EMAIL(new Resolver() {
   @Override
   public String resolve(PullRequestEvent pullRequestEvent, RepositoryService repositoryService) {
    return pullRequestEvent.getPullRequest().getAuthor().getUser().getEmailAddress();
   }
  }), PULL_REQUEST_AUTHOR_SLUG(new Resolver() {
   @Override
   public String resolve(PullRequestEvent pullRequestEvent, RepositoryService repositoryService) {
    return pullRequestEvent.getPullRequest().getAuthor().getUser().getSlug();
   }
  }), PULL_REQUEST_TO_HASH(new Resolver() {
   @Override
   public String resolve(PullRequestEvent pullRequestEvent, RepositoryService repositoryService) {
    return pullRequestEvent.getPullRequest().getToRef().getLatestChangeset();
   }
  }), PULL_REQUEST_TO_ID(new Resolver() {
   @Override
   public String resolve(PullRequestEvent pullRequestEvent, RepositoryService repositoryService) {
    return pullRequestEvent.getPullRequest().getToRef().getId();
   }
  }), PULL_REQUEST_TO_BRANCH(new Resolver() {
   @Override
   public String resolve(PullRequestEvent pullRequestEvent, RepositoryService repositoryService) {
    return pullRequestEvent.getPullRequest().getToRef().getDisplayId();
   }
  }), PULL_REQUEST_TO_REPO_ID(new Resolver() {
   @Override
   public String resolve(PullRequestEvent pullRequestEvent, RepositoryService repositoryService) {
    return pullRequestEvent.getPullRequest().getToRef().getRepository().getId() + "";
   }
  }), PULL_REQUEST_TO_REPO_NAME(new Resolver() {
   @Override
   public String resolve(PullRequestEvent pullRequestEvent, RepositoryService repositoryService) {
    return pullRequestEvent.getPullRequest().getToRef().getRepository().getName() + "";
   }
  }), PULL_REQUEST_TO_REPO_PROJECT_ID(new Resolver() {
   @Override
   public String resolve(PullRequestEvent pullRequestEvent, RepositoryService repositoryService) {
    return pullRequestEvent.getPullRequest().getToRef().getRepository().getProject().getId() + "";
   }
  }), PULL_REQUEST_TO_REPO_PROJECT_KEY(new Resolver() {
   @Override
   public String resolve(PullRequestEvent pullRequestEvent, RepositoryService repositoryService) {
    return pullRequestEvent.getPullRequest().getToRef().getRepository().getProject().getKey();
   }
  }), PULL_REQUEST_TO_REPO_SLUG(new Resolver() {
   @Override
   public String resolve(PullRequestEvent pullRequestEvent, RepositoryService repositoryService) {
    return pullRequestEvent.getPullRequest().getToRef().getRepository().getSlug() + "";
   }
  }), PULL_REQUEST_TO_SSH_CLONE_URL(new Resolver() {
   @Override
   public String resolve(PullRequestEvent pullRequestEvent, RepositoryService repositoryService) {
    return cloneUrlFromRepository(ssh, pullRequestEvent.getPullRequest().getToRef().getRepository(), repositoryService);
   }
  }), PULL_REQUEST_TO_HTTP_CLONE_URL(new Resolver() {
   @Override
   public String resolve(PullRequestEvent pullRequestEvent, RepositoryService repositoryService) {
    return cloneUrlFromRepository(http, pullRequestEvent.getPullRequest().getToRef().getRepository(), repositoryService);
   }
  }), PULL_REQUEST_COMMENT_TEXT(new Resolver() {
   @Override
   public String resolve(PullRequestEvent pullRequestEvent, RepositoryService repositoryService) {
    if (pullRequestEvent instanceof PullRequestCommentAddedEvent) {
     return ((PullRequestCommentAddedEvent) pullRequestEvent).getComment().getText();
    } else {
     return "";
    }
   }
  }), PULL_REQUEST_USER_DISPLAY_NAME(new Resolver() {
   @Override
   public String resolve(PullRequestEvent pullRequestEvent, RepositoryService repositoryService) {
    return pullRequestEvent.getUser().getDisplayName();
   }
  }), PULL_REQUEST_USER_EMAIL_ADDRESS(new Resolver() {
   @Override
   public String resolve(PullRequestEvent pullRequestEvent, RepositoryService repositoryService) {
    return pullRequestEvent.getUser().getEmailAddress();
   }
  }), PULL_REQUEST_USER_ID(new Resolver() {
   @Override
   public String resolve(PullRequestEvent pullRequestEvent, RepositoryService repositoryService) {
    return pullRequestEvent.getUser().getId() + "";
   }
  }), PULL_REQUEST_USER_NAME(new Resolver() {
   @Override
   public String resolve(PullRequestEvent pullRequestEvent, RepositoryService repositoryService) {
    return pullRequestEvent.getUser().getName();
   }
  }), PULL_REQUEST_USER_SLUG(new Resolver() {
   @Override
   public String resolve(PullRequestEvent pullRequestEvent, RepositoryService repositoryService) {
    return pullRequestEvent.getUser().getSlug();
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

  PrnfsVariable(Resolver resolver) {
   this.resolver = resolver;
  }

  public String resolve(PullRequestEvent pullRequestEvent, RepositoryService repositoryService) {
   return resolver.resolve(pullRequestEvent, repositoryService);
  }
 }

 public interface Resolver {
  String resolve(PullRequestEvent pullRequestEvent, RepositoryService repositoryService);
 }

 private final PullRequestEvent pullRequestEvent;
 private final RepositoryService repositoryService;

 public PrnfsRenderer(PullRequestEvent pullRequestEvent, RepositoryService repositoryService) {
  this.pullRequestEvent = pullRequestEvent;
  this.repositoryService = repositoryService;
 }

 public String render(String string) {
  for (final PrnfsVariable variable : PrnfsVariable.values()) {
   final String regExpStr = "\\$\\{" + variable.name() + "\\}";
   if (string.contains(regExpStr.replaceAll("\\\\", ""))) {
    string = string.replaceAll(regExpStr, variable.resolve(pullRequestEvent, repositoryService));
   }
  }
  return string;
 }
}
