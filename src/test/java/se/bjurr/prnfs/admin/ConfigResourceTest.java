package se.bjurr.prnfs.admin;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static se.bjurr.prnfs.admin.ConfigResource.isAdminAllowed;
import static se.bjurr.prnfs.settings.PrnfsSettingsBuilder.prnfsSettingsBuilder;

import javax.servlet.http.HttpServletRequest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import se.bjurr.prnfs.settings.PrnfsSettings;

import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.user.UserProfile;
import com.atlassian.stash.user.EscalatedSecurityContext;
import com.atlassian.stash.user.Permission;
import com.atlassian.stash.user.SecurityService;
import com.atlassian.stash.util.Operation;

@RunWith(MockitoJUnitRunner.class)
public class ConfigResourceTest {

 @Mock
 private UserManager userManager;
 @Mock
 private HttpServletRequest request;
 @Mock
 private SecurityService securityService;
 @Mock
 private PluginSettingsFactory pluginSettingsFactory;
 @Mock
 private UserProfile userProfile;
 private final UserKey userKey = new UserKey("userKey");
 @Mock
 private EscalatedSecurityContext escalatedSecurityContext;

 @Test
 public void testLoggedOutUsersAreNotAllowed() throws Throwable {
  when(userManager.getRemoteUser(request)).thenReturn(null);
  assertFalse(isAdminAllowed(userManager, request, securityService, pluginSettingsFactory));
 }

 @Test
 public void testLoggedInUsersAreNotAllowedWhenConfigured() throws Throwable {
  loggedInWith(prnfsSettingsBuilder().withAdminsAllowed(FALSE).withUsersAllowed(FALSE).build());
  assertFalse(isAdminAllowed(userManager, request, securityService, pluginSettingsFactory));
 }

 @Test
 public void testLoggedInUsersAreAllowedWhenConfigured() throws Throwable {
  loggedInWith(prnfsSettingsBuilder().withAdminsAllowed(FALSE).withUsersAllowed(TRUE).build());
  assertTrue(isAdminAllowed(userManager, request, securityService, pluginSettingsFactory));
 }

 @Test
 public void testLoggedInAdminsAreAllowedWhenConfigured() throws Throwable {
  when(userManager.isAdmin(userKey)).thenReturn(TRUE);
  loggedInWith(prnfsSettingsBuilder().withAdminsAllowed(TRUE).withUsersAllowed(FALSE).build());
  assertTrue(isAdminAllowed(userManager, request, securityService, pluginSettingsFactory));
 }

 @SuppressWarnings("unchecked")
 private void loggedInWith(PrnfsSettings prnfsSettings) throws Throwable {
  when(securityService.withPermission(Matchers.any(Permission.class), Matchers.anyString())).thenReturn(
    escalatedSecurityContext);
  when(escalatedSecurityContext.call(Matchers.any(Operation.class))).thenReturn(prnfsSettings);
  when(userManager.getRemoteUser(request)).thenReturn(userProfile);
  when(userProfile.getUserKey()).thenReturn(userKey);
 }
}
