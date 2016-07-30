package se.bjurr.prnfb.service;

import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static se.bjurr.prnfb.settings.USER_LEVEL.ADMIN;
import static se.bjurr.prnfb.settings.USER_LEVEL.EVERYONE;
import static se.bjurr.prnfb.settings.USER_LEVEL.SYSTEM_ADMIN;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import se.bjurr.prnfb.presentation.dto.ON_OR_OFF;
import se.bjurr.prnfb.settings.PrnfbButton;

import com.atlassian.bitbucket.permission.PermissionService;
import com.atlassian.bitbucket.project.ProjectService;
import com.atlassian.bitbucket.repository.RepositoryService;
import com.atlassian.bitbucket.user.EscalatedSecurityContext;
import com.atlassian.bitbucket.user.SecurityService;
import com.atlassian.bitbucket.util.Operation;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.user.UserProfile;

public class UserCheckServiceTest {

 @Mock
 private EscalatedSecurityContext escalatedSecurityContext;

 @Mock
 private PermissionService permissionService;
 private String projectKey;
 @Mock
 private ProjectService projectService;
 @Mock
 private RepositoryService repositoryService;
 private String repositorySlug;
 @Mock
 private SecurityService securityService;
 @Mock
 private SettingsService settingsService;
 private UserCheckService sut;
 @Mock
 private UserProfile user;
 private final UserKey userKey = new UserKey("userkey");
 @Mock
 private UserManager userManager;

 @Before
 public void before() throws Exception {
  initMocks(this);
  this.sut = new UserCheckService(this.permissionService, this.userManager, this.settingsService,
    this.repositoryService, this.projectService, this.securityService);

  when(this.securityService.withPermission(Matchers.any(), Matchers.any()))//
    .thenReturn(this.escalatedSecurityContext);
  when(this.escalatedSecurityContext.call(Matchers.any()))//
    .thenAnswer(new Answer<Boolean>() {
     @Override
     public Boolean answer(InvocationOnMock invocation) throws Throwable {
      Operation<?, ?> op = (Operation<?, ?>) invocation.getArguments()[0];
      return (Boolean) op.perform();
     }
    });
 }

 @Test
 public void testThatAdminAllowedCanBeChecked() {
  this.projectKey = null;
  this.repositorySlug = null;
  this.sut.isAdminAllowed(this.projectKey, this.repositorySlug);
 }

 @Test
 public void testThatAllowedButtonsCanBeFiltered() {
  this.projectKey = "p1";
  this.repositorySlug = "r1";

  when(this.userManager.getRemoteUser())//
    .thenReturn(this.user);
  when(this.userManager.getRemoteUser().getUserKey())//
    .thenReturn(this.userKey);
  when(this.userManager.isSystemAdmin(this.userKey))//
    .thenReturn(false);
  when(this.userManager.isAdmin(this.userKey))//
    .thenReturn(false);

  PrnfbButton button1 = new PrnfbButton(null, "title1", ADMIN, ON_OR_OFF.off, "p1", "r1");
  PrnfbButton button2 = new PrnfbButton(null, "title2", EVERYONE, ON_OR_OFF.off, "p1", "r1");
  PrnfbButton button3 = new PrnfbButton(null, "title3", SYSTEM_ADMIN, ON_OR_OFF.off, "p1", "r1");
  List<PrnfbButton> buttons = newArrayList(button1, button2, button3);

  Iterable<PrnfbButton> onlyAllowed = this.sut.filterAllowed(buttons);

  assertThat(onlyAllowed)//
    .containsOnly(button2);
 }

 @Test
 public void testThatAllowedCanBeChecked() {
  this.projectKey = "p1";
  this.repositorySlug = "r1";

  when(this.userManager.getRemoteUser())//
    .thenReturn(this.user);
  when(this.userManager.getRemoteUser().getUserKey())//
    .thenReturn(this.userKey);
  when(this.userManager.isSystemAdmin(this.userKey))//
    .thenReturn(true);

  PrnfbButton candidate = new PrnfbButton(null, "title", ADMIN, ON_OR_OFF.off, "p1", "r1");
  assertThat(this.sut.isAllowedUseButton(candidate))//
    .isTrue();

  assertThat(this.sut.isAdminAllowedCheck(ADMIN, true, false))//
    .isTrue();
  assertThat(this.sut.isAdminAllowedCheck(EVERYONE, false, false))//
    .isTrue();
  assertThat(this.sut.isAdminAllowedCheck(SYSTEM_ADMIN, false, true))//
    .isTrue();
 }

 @Test
 public void testThatViewAllowedIsFalseWhenNotLoggedIn() {
  assertThat(this.sut.isViewAllowed())//
    .isFalse();
 }

 @Test
 public void testThatViewAllowedIsTrueWhenLoggedIn() {
  when(this.userManager.getRemoteUser())//
    .thenReturn(this.user);
  assertThat(this.sut.isViewAllowed())//
    .isTrue();
 }

}
