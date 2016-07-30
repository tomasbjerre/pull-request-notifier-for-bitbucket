package se.bjurr.prnfb.service;

import static com.atlassian.bitbucket.permission.Permission.PROJECT_ADMIN;
import static com.atlassian.bitbucket.permission.Permission.REPO_ADMIN;
import static com.atlassian.bitbucket.permission.Permission.SYS_ADMIN;
import static com.google.common.base.Strings.emptyToNull;
import static com.google.common.base.Throwables.propagate;
import static com.google.common.collect.Iterables.filter;
import static se.bjurr.prnfb.settings.USER_LEVEL.ADMIN;
import static se.bjurr.prnfb.settings.USER_LEVEL.EVERYONE;

import java.util.List;

import javax.annotation.Nullable;

import se.bjurr.prnfb.settings.PrnfbButton;
import se.bjurr.prnfb.settings.USER_LEVEL;

import com.atlassian.bitbucket.permission.PermissionService;
import com.atlassian.bitbucket.project.Project;
import com.atlassian.bitbucket.project.ProjectService;
import com.atlassian.bitbucket.repository.Repository;
import com.atlassian.bitbucket.repository.RepositoryService;
import com.atlassian.bitbucket.user.SecurityService;
import com.atlassian.bitbucket.util.Operation;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.user.UserProfile;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Predicate;

public class UserCheckService {
 private final PermissionService permissionService;
 private final ProjectService projectService;
 private final RepositoryService repositoryService;
 private final SecurityService securityService;
 private final SettingsService settingsService;
 private final UserManager userManager;

 public UserCheckService(PermissionService permissionService, UserManager userManager, SettingsService settingsService,
   RepositoryService repositoryService, ProjectService projectService, SecurityService securityService) {
  this.userManager = userManager;
  this.settingsService = settingsService;
  this.permissionService = permissionService;
  this.projectService = projectService;
  this.repositoryService = repositoryService;
  this.securityService = securityService;
 }

 public Iterable<PrnfbButton> filterAllowed(List<PrnfbButton> buttons) {
  Iterable<PrnfbButton> allowedButtons = filter(buttons, new Predicate<PrnfbButton>() {
   @Override
   public boolean apply(PrnfbButton input) {
    return isAllowedUseButton(input);
   }
  });
  return allowedButtons;
 }

 public boolean isAdmin(UserKey userKey, String projectKey, String repositorySlug) {
  boolean isAdmin = this.userManager.isAdmin(userKey);
  if (isAdmin) {
   return isAdmin;
  }

  projectKey = emptyToNull(projectKey);
  repositorySlug = emptyToNull(repositorySlug);

  if (projectKey != null && repositorySlug == null) {
   Project project = getProject(projectKey);
   return this.permissionService.hasProjectPermission(project, PROJECT_ADMIN);
  } else if (repositorySlug != null) {
   Repository repository = getRepo(projectKey, repositorySlug);
   return this.permissionService.hasRepositoryPermission(repository, REPO_ADMIN);
  }
  return isAdmin;
 }

 /**
  * null if global.
  */
 public boolean isAdminAllowed(@Nullable String projectKey, @Nullable String repositorySlug) {
  final UserProfile user = this.userManager.getRemoteUser();
  if (user == null) {
   return false;
  }
  USER_LEVEL adminRestriction = this.settingsService.getPrnfbSettingsData().getAdminRestriction();
  return isAdminAllowed(adminRestriction, projectKey, repositorySlug);
 }

 public boolean isAllowedUseButton(PrnfbButton candidate) {
  return isAdminAllowed(//
    candidate.getUserLevel(), //
    candidate.getProjectKey().orNull(), //
    candidate.getRepositorySlug().orNull());
 }

 public boolean isSystemAdmin(UserKey userKey) {
  return this.userManager.isSystemAdmin(userKey);
 }

 public boolean isViewAllowed() {
  UserProfile user = this.userManager.getRemoteUser();
  if (user == null) {
   return false;
  }
  return true;
 }

 @VisibleForTesting
 private Project getProject(String projectKey) {
  try {
   return this.securityService//
     .withPermission(SYS_ADMIN, "Getting project")//
     .call(new Operation<Project, Exception>() {
      @Override
      public Project perform() throws Exception {
       return UserCheckService.this.projectService.getByKey(projectKey);
      }
     });
  } catch (Exception e) {
   throw propagate(e);
  }
 }

 private boolean isAdminAllowed(USER_LEVEL adminRestriction, @Nullable String projectKey,
   @Nullable String repositorySlug) {
  UserKey userKey = this.userManager.getRemoteUser().getUserKey();
  boolean isAdmin = isAdmin(userKey, projectKey, repositorySlug);
  boolean isSystemAdmin = isSystemAdmin(userKey);
  return isAdminAllowedCheck(adminRestriction, isAdmin, isSystemAdmin);
 }

 @VisibleForTesting
 Repository getRepo(String projectKey, String repositorySlug) {
  try {
   return this.securityService//
     .withPermission(SYS_ADMIN, "Getting repo")//
     .call(new Operation<Repository, Exception>() {
      @Override
      public Repository perform() throws Exception {
       return UserCheckService.this.repositoryService.getBySlug(projectKey, repositorySlug);
      }
     });
  } catch (Exception e) {
   throw propagate(e);
  }
 }

 boolean isAdminAllowedCheck(USER_LEVEL userLevel, boolean isAdmin, boolean isSystemAdmin) {
  return userLevel == EVERYONE //
    || isSystemAdmin //
    || isAdmin && userLevel == ADMIN;
 }

}
