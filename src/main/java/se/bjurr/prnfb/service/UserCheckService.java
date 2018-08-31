package se.bjurr.prnfb.service;

import static com.atlassian.bitbucket.permission.Permission.PROJECT_ADMIN;
import static com.atlassian.bitbucket.permission.Permission.REPO_ADMIN;
import static com.atlassian.bitbucket.permission.Permission.SYS_ADMIN;
import static com.google.common.base.Strings.emptyToNull;
import static com.google.common.collect.Iterables.filter;
import static org.slf4j.LoggerFactory.getLogger;
import static se.bjurr.prnfb.settings.USER_LEVEL.ADMIN;
import static se.bjurr.prnfb.settings.USER_LEVEL.EVERYONE;

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
import java.util.List;
import javax.annotation.Nullable;
import org.slf4j.Logger;
import se.bjurr.prnfb.settings.Restricted;
import se.bjurr.prnfb.settings.USER_LEVEL;

public class UserCheckService {
  private static final Logger LOG = getLogger(UserCheckService.class);
  private final PermissionService permissionService;
  private final ProjectService projectService;
  private final RepositoryService repositoryService;
  private final SecurityService securityService;
  private final SettingsService settingsService;
  private final UserManager userManager;

  public UserCheckService(
      PermissionService permissionService,
      UserManager userManager,
      SettingsService settingsService,
      RepositoryService repositoryService,
      ProjectService projectService,
      SecurityService securityService) {
    this.userManager = userManager;
    this.settingsService = settingsService;
    this.permissionService = permissionService;
    this.projectService = projectService;
    this.repositoryService = repositoryService;
    this.securityService = securityService;
  }

  public <R extends Restricted> Iterable<R> filterAllowed(USER_LEVEL adminRestriction, List<R> r) {
    return filter(
        r,
        (c) ->
            isAllowed( //
                adminRestriction, //
                c.getProjectKey().orNull(), //
                c.getRepositorySlug().orNull()));
  }

  public <R extends Restricted> Iterable<R> filterAdminAllowed(List<R> list) {
    final USER_LEVEL adminRestriction =
        settingsService.getPrnfbSettingsData().getAdminRestriction();
    return filter(list, (r) -> isAdminAllowed(r, adminRestriction));
  }

  @VisibleForTesting
  private Project getProject(String projectKey) {
    try {
      return securityService //
          .withPermission(SYS_ADMIN, "Getting project") //
          .call(
              new Operation<Project, Exception>() {
                @Override
                public Project perform() throws Exception {
                  return projectService.getByKey(projectKey);
                }
              });
    } catch (final Exception e) {
      LOG.error(e.getMessage(), e);
      return null;
    }
  }

  @VisibleForTesting
  Repository getRepo(String projectKey, String repositorySlug) {
    try {
      return securityService //
          .withPermission(SYS_ADMIN, "Getting repo") //
          .call(
              new Operation<Repository, Exception>() {
                @Override
                public Repository perform() throws Exception {
                  return repositoryService.getBySlug(projectKey, repositorySlug);
                }
              });
    } catch (final Exception e) {
      LOG.error(e.getMessage(), e);
      return null;
    }
  }

  public boolean isAdmin(UserKey userKey, String projectKey, String repositorySlug) {
    final boolean isAdmin = userManager.isAdmin(userKey);
    if (isAdmin) {
      return isAdmin;
    }

    projectKey = emptyToNull(projectKey);
    repositorySlug = emptyToNull(repositorySlug);

    if (projectKey != null && repositorySlug == null) {
      final Project project = getProject(projectKey);
      if (project == null) {
        LOG.error(
            "Project "
                + projectKey
                + " configured. But no such project exists! Allowing anyone to admin.");
        return true;
      }
      final boolean isAllowed = permissionService.hasProjectPermission(project, PROJECT_ADMIN);
      if (isAllowed) {
        return true;
      }
    }

    if (projectKey != null && repositorySlug != null) {
      final Repository repository = getRepo(projectKey, repositorySlug);
      if (repository == null) {
        LOG.error(
            "Project "
                + projectKey
                + " and repo "
                + repositorySlug
                + " configured. But no such repo exists! Allowing anyone to admin.");
        return true;
      }
      return permissionService.hasRepositoryPermission(repository, REPO_ADMIN);
    }
    return false;
  }

  public boolean isAdminAllowed(Restricted restricted, USER_LEVEL adminRestriction) {
    final String projectKey = restricted.getProjectKey().orNull();
    final String repositorySlug = restricted.getRepositorySlug().orNull();
    return isAllowed(adminRestriction, projectKey, repositorySlug);
  }

  public boolean isAllowed(
      USER_LEVEL userLevel, @Nullable String projectKey, @Nullable String repositorySlug) {
    final UserKey userKey = userManager.getRemoteUser().getUserKey();
    final boolean isAdmin = isAdmin(userKey, projectKey, repositorySlug);
    final boolean isSystemAdmin = isSystemAdmin(userKey);
    return isAllowed(userLevel, isAdmin, isSystemAdmin);
  }

  boolean isAllowed(USER_LEVEL userLevel, boolean isAdmin, boolean isSystemAdmin) {
    return userLevel == EVERYONE //
        || isSystemAdmin //
        || isAdmin && userLevel == ADMIN;
  }

  public boolean isSystemAdmin(UserKey userKey) {
    return userManager.isSystemAdmin(userKey);
  }

  public boolean isViewAllowed() {
    final UserProfile user = userManager.getRemoteUser();
    if (user == null) {
      return false;
    }
    return true;
  }
}
