package se.bjurr.prnfb.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static se.bjurr.prnfb.settings.USER_LEVEL.ADMIN;
import static se.bjurr.prnfb.settings.USER_LEVEL.EVERYONE;
import static se.bjurr.prnfb.settings.USER_LEVEL.SYSTEM_ADMIN;

import com.atlassian.bitbucket.permission.Permission;
import com.atlassian.bitbucket.permission.PermissionService;
import com.atlassian.bitbucket.project.ProjectService;
import com.atlassian.bitbucket.repository.RepositoryService;
import com.atlassian.bitbucket.user.EscalatedSecurityContext;
import com.atlassian.bitbucket.user.SecurityService;
import com.atlassian.bitbucket.util.Operation;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.user.UserProfile;
import com.google.common.base.Optional;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import se.bjurr.prnfb.settings.Restricted;

public class UserCheckServiceTest {
  private final EscalatedSecurityContext escalatedSecurityContext =
      new EscalatedSecurityContext() {

        @Override
        public void applyToRequest() {}

        @Override
        public <T, E extends Throwable> T call(Operation<T, E> arg0) throws E {
          return arg0.perform();
        }

        @Override
        public EscalatedSecurityContext withPermission(Object arg0, Permission arg1) {
          return this;
        }

        @Override
        public EscalatedSecurityContext withPermission(Permission arg0) {
          return this;
        }

        @Override
        public EscalatedSecurityContext withPermissions(Set<Permission> arg0) {
          return this;
        }
      };

  @Mock private PermissionService permissionService;
  @Mock private ProjectService projectService;
  @Mock private RepositoryService repositoryService;
  @Mock private SecurityService securityService;
  @Mock private SettingsService settingsService;
  private UserCheckService sut;
  @Mock private UserProfile user;
  private final UserKey userKey = new UserKey("userkey");
  @Mock private UserManager userManager;

  @Before
  public void before() throws Exception {
    initMocks(this);
    this.sut =
        new UserCheckService(
            this.permissionService,
            this.userManager,
            this.settingsService,
            this.repositoryService,
            this.projectService,
            this.securityService);

    when(this.securityService.withPermission(Matchers.any(), Matchers.any())) //
        .thenReturn(this.escalatedSecurityContext);
  }

  @Test
  public void testThatAdminAllowedCanBeChecked() {
    final UserProfile remoteUser = mock(UserProfile.class);
    when(remoteUser.getUserKey()).thenReturn(userKey);
    when(userManager.getRemoteUser()).thenReturn(remoteUser);
    when(userManager.isAdmin(userKey)).thenReturn(false);
    when(userManager.isSystemAdmin(userKey)).thenReturn(false);
    final boolean actual =
        this.sut.isAdminAllowed(
            new Restricted() {
              @Override
              public Optional<String> getRepositorySlug() {
                return Optional.absent();
              }

              @Override
              public Optional<String> getProjectKey() {
                return Optional.absent();
              }
            },
            SYSTEM_ADMIN);

    assertThat(actual).isFalse();
  }

  @Test
  public void testThatAllowedCanBeChecked() {
    assertThat(this.sut.isAllowed(ADMIN, true, false)) //
        .isTrue();
    assertThat(this.sut.isAllowed(EVERYONE, false, false)) //
        .isTrue();
    assertThat(this.sut.isAllowed(SYSTEM_ADMIN, false, true)) //
        .isTrue();
  }

  @Test
  public void testThatViewAllowedIsFalseWhenNotLoggedIn() {
    assertThat(this.sut.isViewAllowed()) //
        .isFalse();
  }

  @Test
  public void testThatViewAllowedIsTrueWhenLoggedIn() {
    when(this.userManager.getRemoteUser()) //
        .thenReturn(this.user);
    assertThat(this.sut.isViewAllowed()) //
        .isTrue();
  }
}
