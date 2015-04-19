package se.bjurr.prnfs.admin.utils;

import static com.google.common.collect.Sets.newHashSet;
import static org.mockito.Mockito.when;

import java.util.Set;

import org.mockito.Matchers;

import se.bjurr.prnfs.listener.PrnfsRenderer;
import se.bjurr.prnfs.listener.PrnfsRenderer.REPO_PROTOCOL;

import com.atlassian.stash.project.Project;
import com.atlassian.stash.project.ProjectType;
import com.atlassian.stash.project.ProjectVisitor;
import com.atlassian.stash.pull.PullRequestRef;
import com.atlassian.stash.repository.Repository;
import com.atlassian.stash.repository.RepositoryCloneLinksRequest;
import com.atlassian.stash.repository.RepositoryService;
import com.atlassian.stash.util.NamedLink;

public class PullRequestRefBuilder implements PullRequestRef {
 public static PullRequestRefBuilder pullRequestRefBuilder() {
  return new PullRequestRefBuilder(null);
 }

 private String hash;
 private String id;
 private Integer projectId;
 private String projectKey;
 private Integer repositoryId;
 private String repositoryName;

 private String slug;
 private PullRequestEventBuilder pullRequestEventBuilder;

 private PullRequestRefBuilder() {
 }

 private PullRequestRefBuilder(PullRequestEventBuilder pullRequestEventBuilder) {
  this.pullRequestEventBuilder = pullRequestEventBuilder;
 }

 public static PullRequestRefBuilder pullRequestRefBuilder(PullRequestEventBuilder pullRequestEventBuilder) {
  return new PullRequestRefBuilder(pullRequestEventBuilder);
 }

 public PullRequestEventBuilder build() {
  return pullRequestEventBuilder;
 }

 @Override
 public String getDisplayId() {
  return null;
 }

 @Override
 public String getId() {
  return id;
 }

 @Override
 public String getLatestChangeset() {
  return hash;
 }

 // Should not use @Override, since its only available from 3.7.0
 public String getLatestCommit() {
  return hash;
 }

 @Override
 public Repository getRepository() {
  return new Repository() {

   @Override
   public String getHierarchyId() {
    return null;
   }

   @Override
   public Integer getId() {
    return repositoryId;
   }

   @Override
   public String getName() {
    return repositoryName;
   }

   @Override
   public Repository getOrigin() {
    return null;
   }

   @Override
   public Project getProject() {
    return new Project() {

     @Override
     public <T> T accept(ProjectVisitor<T> arg0) {
      return null;
     }

     @Override
     public String getDescription() {
      return null;
     }

     @Override
     public Integer getId() {
      return projectId;
     }
     
     // Should not use @Override, since its only available until 3.12.0
     public boolean getIsPersonal() {
      return false;
     }

     @Override
     public String getKey() {
      // Avoid save action from auto remove unused method
      getIsPersonal();
      return projectKey;
     }

     @Override
     public String getName() {
      return null;
     }

     @Override
     public ProjectType getType() {
      return null;
     }

     @Override
     public boolean isPublic() {
      return false;
     }
    };
   }

   @Override
   public String getScmId() {
    return null;
   }

   @Override
   public String getSlug() {
    return slug;
   }

   @Override
   public State getState() {
    return null;
   }

   @Override
   public String getStatusMessage() {
    return null;
   }

   @Override
   public boolean isFork() {
    return false;
   }

   @Override
   public boolean isForkable() {
    return false;
   }

   @Override
   public boolean isPublic() {
    return false;
   }
  };
 }

 public PullRequestRefBuilder withHash(String pullRequestHash) {
  this.hash = pullRequestHash;
  return this;
 }

 public PullRequestRefBuilder withId(String id) {
  this.id = id;
  return this;
 }

 public PullRequestRefBuilder withProjectId(Integer projectId) {
  this.projectId = projectId;
  return this;
 }

 public PullRequestRefBuilder withProjectKey(String projectKey) {
  this.projectKey = projectKey;
  return this;
 }

 public PullRequestRefBuilder withRepositoryId(Integer repositoryId) {
  this.repositoryId = repositoryId;
  return this;
 }

 public PullRequestRefBuilder withRepositoryName(String repositoryName) {
  this.repositoryName = repositoryName;
  return this;
 }

 public PullRequestRefBuilder withRepositorySlug(String repositorySlug) {
  this.slug = repositorySlug;
  return this;
 }

 public PullRequestRefBuilder withCloneUrl(PrnfsRenderer.REPO_PROTOCOL protocol, String url) {
  mockCloneUrl(protocol, pullRequestEventBuilder.getPrnfsTestBuilder().getRepositoryService(), url);
  return this;
 }

 private void mockCloneUrl(REPO_PROTOCOL protocol, RepositoryService repositoryService, final String url) {
  NamedLink e = new NamedLink() {
   @Override
   public String getName() {
    return null;
   }

   @Override
   public String getHref() {
    return url;
   }
  };
  Set<NamedLink> value = newHashSet(e);
  when(repositoryService.getCloneLinks(Matchers.any(RepositoryCloneLinksRequest.class))).thenReturn(value);
 }
}