package se.bjurr.prnfs.listener;

import com.atlassian.stash.event.pull.PullRequestEvent;

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
  }), PULL_REQUEST_ID(new Resolver() {
   @Override
   public String resolve(PullRequestEvent pullRequestEvent) {
    return pullRequestEvent.getPullRequest().getId() + "";
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
    try {
     string = string.replaceAll(regExpStr, variable.resolve(pullRequestEvent));
    } catch (final NullPointerException e) {
     // So that all values does not need to be set for all test cases
     return string;
    }
   }
  }
  return string;
 }
}
