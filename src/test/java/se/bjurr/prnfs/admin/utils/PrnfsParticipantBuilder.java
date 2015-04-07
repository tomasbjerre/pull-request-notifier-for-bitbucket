package se.bjurr.prnfs.admin.utils;

import com.atlassian.stash.pull.PullRequest;
import com.atlassian.stash.pull.PullRequestParticipant;
import com.atlassian.stash.pull.PullRequestRole;
import com.atlassian.stash.user.StashUser;
import com.atlassian.stash.user.StashUserVisitor;
import com.atlassian.stash.user.UserType;

public class PrnfsParticipantBuilder {

 private String slug;
 private String name;
 private Integer id;
 private String displayName;
 private String email;

 private PrnfsParticipantBuilder() {
 }

 public PrnfsParticipantBuilder withDisplayName(String string) {
  this.displayName = string;
  return this;
 }

 public PrnfsParticipantBuilder withEmail(String string) {
  this.email = string;
  return this;
 }

 public PrnfsParticipantBuilder withId(Integer id) {
  this.id = id;
  return this;
 }

 public PrnfsParticipantBuilder withName(String string) {
  this.name = string;
  return this;
 }

 public PrnfsParticipantBuilder withSlug(String string) {
  this.slug = string;
  return this;
 }

 public static PrnfsParticipantBuilder prnfsParticipantBuilder() {
  return new PrnfsParticipantBuilder();
 }

 public PullRequestParticipant build() {
  return new PullRequestParticipant() {

   @Override
   public boolean isApproved() {
    return false;
   }

   @Override
   public StashUser getUser() {
    return new StashUser() {
     @Override
     public String getName() {
      return name;
     }

     @Override
     public String getEmailAddress() {
      return email;
     }

     @Override
     public boolean isActive() {
      return false;
     }

     @Override
     public UserType getType() {
      return null;
     }

     @Override
     public String getSlug() {
      return slug;
     }

     @Override
     public Integer getId() {
      return id;
     }

     @Override
     public String getDisplayName() {
      return displayName;
     }

     @Override
     public <T> T accept(StashUserVisitor<T> arg0) {
      return null;
     }
    };
   }

   @Override
   public PullRequestRole getRole() {
    return null;
   }

   @Override
   public PullRequest getPullRequest() {
    return null;
   }
  };
 }
}
