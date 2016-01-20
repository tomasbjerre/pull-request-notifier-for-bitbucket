package se.bjurr.prnb.admin.utils;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.atlassian.bitbucket.pull.PullRequest;
import com.atlassian.bitbucket.pull.PullRequestParticipant;
import com.atlassian.bitbucket.pull.PullRequestRole;
import com.atlassian.bitbucket.user.ApplicationUser;

public class PrnfbParticipantBuilder {

 private String slug;
 private String name;
 private Integer id;
 private String displayName;
 private String email;

 private PrnfbParticipantBuilder() {
 }

 public PrnfbParticipantBuilder withDisplayName(String string) {
  this.displayName = string;
  return this;
 }

 public PrnfbParticipantBuilder withEmail(String string) {
  this.email = string;
  return this;
 }

 public PrnfbParticipantBuilder withId(Integer id) {
  this.id = id;
  return this;
 }

 public PrnfbParticipantBuilder withName(String string) {
  this.name = string;
  return this;
 }

 public PrnfbParticipantBuilder withSlug(String string) {
  this.slug = string;
  return this;
 }

 public static PrnfbParticipantBuilder prnfbParticipantBuilder() {
  return new PrnfbParticipantBuilder();
 }

 public PullRequestParticipant build() {
  return new PullRequestParticipant() {

   @Override
   public boolean isApproved() {
    return false;
   }

   @Override
   public ApplicationUser getUser() {
    ApplicationUser mockedApplicationUser = mock(ApplicationUser.class);
    when(mockedApplicationUser.getName()).thenReturn(name);
    when(mockedApplicationUser.getEmailAddress()).thenReturn(email);
    when(mockedApplicationUser.getSlug()).thenReturn(slug);
    when(mockedApplicationUser.getId()).thenReturn(id);
    when(mockedApplicationUser.getDisplayName()).thenReturn(displayName);
    return mockedApplicationUser;
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
