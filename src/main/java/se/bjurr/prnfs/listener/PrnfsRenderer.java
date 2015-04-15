package se.bjurr.prnfs.listener;

import static se.bjurr.prnfs.listener.PrnfsPullRequestAction.fromPullRequestEvent;

import com.atlassian.stash.event.pull.PullRequestEvent;
import com.atlassian.stash.pull.PullRequestRef;

public class PrnfsRenderer {

 public enum PrnfsVariable {
  PULL_REQUEST_FROM_HASH(new Resolver() {
   @Override
   public String resolve(PullRequestEvent pullRequestEvent) {
    return pullRequestEvent.getPullRequest().getFromRef().getLatestChangeset();
   }
  }), PULL_REQUEST_FROM_ID(new Resolver() {
   @Override
   public String resolve(PullRequestEvent pullRequestEvent) {
    return pullRequestEvent.getPullRequest().getFromRef().getId();
   }
  }), PULL_REQUEST_FROM_BRANCH(new Resolver() {
   @Override
   public String resolve(PullRequestEvent pullRequestEvent) {
    return branchNameFromId(pullRequestEvent.getPullRequest().getFromRef());
   }
  }), PULL_REQUEST_FROM_REPO_ID(new Resolver() {
   @Override
   public String resolve(PullRequestEvent pullRequestEvent) {
    return pullRequestEvent.getPullRequest().getFromRef().getRepository().getId() + "";
   }
  }), PULL_REQUEST_FROM_REPO_NAME(new Resolver() {
   @Override
   public String resolve(PullRequestEvent pullRequestEvent) {
    return pullRequestEvent.getPullRequest().getFromRef().getRepository().getName() + "";
   }
  }), PULL_REQUEST_FROM_REPO_PROJECT_ID(new Resolver() {
   @Override
   public String resolve(PullRequestEvent pullRequestEvent) {
    return pullRequestEvent.getPullRequest().getFromRef().getRepository().getProject().getId() + "";
   }
  }), PULL_REQUEST_FROM_REPO_PROJECT_KEY(new Resolver() {
   @Override
   public String resolve(PullRequestEvent pullRequestEvent) {
    return pullRequestEvent.getPullRequest().getFromRef().getRepository().getProject().getKey();
   }
  }), PULL_REQUEST_FROM_REPO_SLUG(new Resolver() {
   @Override
   public String resolve(PullRequestEvent pullRequestEvent) {
    return pullRequestEvent.getPullRequest().getFromRef().getRepository().getSlug() + "";
   }
  }), PULL_REQUEST_ACTION(new Resolver() {
   @Override
   public String resolve(PullRequestEvent pullRequestEvent) {
    return fromPullRequestEvent(pullRequestEvent).getName();
   }
  }), PULL_REQUEST_ID(new Resolver() {
   @Override
   public String resolve(PullRequestEvent pullRequestEvent) {
    return pullRequestEvent.getPullRequest().getId() + "";
   }
  }), PULL_REQUEST_AUTHOR_ID(new Resolver() {
   @Override
   public String resolve(PullRequestEvent pullRequestEvent) {
    return pullRequestEvent.getPullRequest().getAuthor().getUser().getId() + "";
   }
  }), PULL_REQUEST_AUTHOR_DISPLAY_NAME(new Resolver() {
   @Override
   public String resolve(PullRequestEvent pullRequestEvent) {
    return pullRequestEvent.getPullRequest().getAuthor().getUser().getDisplayName();
   }
  }), PULL_REQUEST_AUTHOR_NAME(new Resolver() {
   @Override
   public String resolve(PullRequestEvent pullRequestEvent) {
    return pullRequestEvent.getPullRequest().getAuthor().getUser().getName();
   }
  }), PULL_REQUEST_AUTHOR_EMAIL(new Resolver() {
   @Override
   public String resolve(PullRequestEvent pullRequestEvent) {
    return pullRequestEvent.getPullRequest().getAuthor().getUser().getEmailAddress();
   }
  }), PULL_REQUEST_AUTHOR_SLUG(new Resolver() {
   @Override
   public String resolve(PullRequestEvent pullRequestEvent) {
    return pullRequestEvent.getPullRequest().getAuthor().getUser().getSlug();
   }
  }), PULL_REQUEST_TO_HASH(new Resolver() {
   @Override
   public String resolve(PullRequestEvent pullRequestEvent) {
    return pullRequestEvent.getPullRequest().getToRef().getLatestChangeset();
   }
  }), PULL_REQUEST_TO_ID(new Resolver() {
   @Override
   public String resolve(PullRequestEvent pullRequestEvent) {
    return pullRequestEvent.getPullRequest().getToRef().getId();
   }
  }), PULL_REQUEST_TO_BRANCH(new Resolver() {
   @Override
   public String resolve(PullRequestEvent pullRequestEvent) {
    return branchNameFromId(pullRequestEvent.getPullRequest().getToRef());
   }
  }), PULL_REQUEST_TO_REPO_ID(new Resolver() {
   @Override
   public String resolve(PullRequestEvent pullRequestEvent) {
    return pullRequestEvent.getPullRequest().getToRef().getRepository().getId() + "";
   }
  }), PULL_REQUEST_TO_REPO_NAME(new Resolver() {
   @Override
   public String resolve(PullRequestEvent pullRequestEvent) {
    return pullRequestEvent.getPullRequest().getToRef().getRepository().getName() + "";
   }
  }), PULL_REQUEST_TO_REPO_PROJECT_ID(new Resolver() {
   @Override
   public String resolve(PullRequestEvent pullRequestEvent) {
    return pullRequestEvent.getPullRequest().getToRef().getRepository().getProject().getId() + "";
   }
  }), PULL_REQUEST_TO_REPO_PROJECT_KEY(new Resolver() {
   @Override
   public String resolve(PullRequestEvent pullRequestEvent) {
    return pullRequestEvent.getPullRequest().getToRef().getRepository().getProject().getKey();
   }
  }), PULL_REQUEST_TO_REPO_SLUG(new Resolver() {
   @Override
   public String resolve(PullRequestEvent pullRequestEvent) {
    return pullRequestEvent.getPullRequest().getToRef().getRepository().getSlug() + "";
   }
  });

  private Resolver resolver;

  private static String branchNameFromId(PullRequestRef pullRequestRef) {
   String branchId = pullRequestRef.getId();
   int lastSlash = branchId.lastIndexOf('/');
   return branchId.substring(lastSlash + 1);
  }

  private PrnfsVariable(Resolver resolver) {
   this.resolver = resolver;
  }

  public String resolve(PullRequestEvent pullRequestEvent) {
   return resolver.resolve(pullRequestEvent);
  }
 }

 public interface Resolver {
  public String resolve(PullRequestEvent pullRequestEvent);
 }

 private final PullRequestEvent pullRequestEvent;

 public PrnfsRenderer(PullRequestEvent pullRequestEvent) {
  this.pullRequestEvent = pullRequestEvent;
 }

 public String render(String string) {
  for (final PrnfsVariable variable : PrnfsVariable.values()) {
   final String regExpStr = "\\$\\{" + variable.name() + "\\}";
   if (string.contains(regExpStr.replaceAll("\\\\", ""))) {
    string = string.replaceAll(regExpStr, variable.resolve(pullRequestEvent));
   }
  }
  return string;
 }
}
