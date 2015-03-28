package se.bjurr.prnfs.admin.utils;

import com.atlassian.stash.project.Project;
import com.atlassian.stash.project.ProjectType;
import com.atlassian.stash.project.ProjectVisitor;
import com.atlassian.stash.pull.PullRequestRef;
import com.atlassian.stash.repository.Repository;

public class PullRequestRefBuilder implements PullRequestRef {
 private String projectKey;
 private Integer projectId;
 private Integer repositoryId;
 private String repositoryName;
 private String hash;
 private String slug;
 private String id;

 private PullRequestRefBuilder() {
 }

 public static PullRequestRefBuilder pullRequestRefBuilder() {
  return new PullRequestRefBuilder();
 }

 public PullRequestRefBuilder withProjectId(Integer projectId) {
  this.projectId = projectId;
  return this;
 }

 public PullRequestRefBuilder withProjectKey(String projectKey) {
  this.projectKey = projectKey;
  return this;
 }

 public PullRequestRefBuilder withHash(String pullRequestHash) {
  this.hash = pullRequestHash;
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

 @Override
 public String getLatestChangeset() {
  return hash;
 }

 @Override
 public String getLatestCommit() {
  return hash;
 }

 @Override
 public String getDisplayId() {
  return null;
 }

 public PullRequestRefBuilder withId(String id) {
  this.id = id;
  return this;
 }

 @Override
 public String getId() {
  return id;
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
     public boolean isPublic() {
      return false;
     }

     @Override
     public ProjectType getType() {
      return null;
     }

     @Override
     public String getName() {
      return null;
     }

     @Override
     public String getKey() {
      return projectKey;
     }

     @Override
     public Integer getId() {
      return projectId;
     }

     @Override
     public String getDescription() {
      return null;
     }

     @Override
     public <T> T accept(ProjectVisitor<T> arg0) {
      return null;
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
}