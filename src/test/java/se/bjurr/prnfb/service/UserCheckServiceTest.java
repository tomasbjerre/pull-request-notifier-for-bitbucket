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
import org.mockito.Mock;

import se.bjurr.prnfb.settings.PrnfbButton;

import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.user.UserProfile;

public class UserCheckServiceTest {

 @Mock
 private UserManager userManager;
 @Mock
 private SettingsService settingsService;
 @Mock
 private UserProfile user;
 private UserCheckService sut;
 private final UserKey userKey = new UserKey("userkey");

 @Before
 public void before() {
  initMocks(this);
  sut = new UserCheckService(userManager, settingsService);
 }

 @Test
 public void testThatAdminAllowedCanBeChecked() {
  sut.isAdminAllowed();
 }

 @Test
 public void testThatViewAllowedIsFalseWhenNotLoggedIn() {
  assertThat(sut.isViewAllowed())//
    .isFalse();
 }

 @Test
 public void testThatViewAllowedIsTrueWhenLoggedIn() {
  when(userManager.getRemoteUser())//
    .thenReturn(user);
  assertThat(sut.isViewAllowed())//
    .isTrue();
 }

 @Test
 public void testThatButtonAllowedCanBeChecked() {
  when(userManager.getRemoteUser())//
    .thenReturn(user);
  when(userManager.getRemoteUser().getUserKey())//
    .thenReturn(userKey);
  when(userManager.isSystemAdmin(userKey))//
    .thenReturn(true);
  PrnfbButton candidate = new PrnfbButton("title", ADMIN);
  assertThat(sut.isAllowedUseButton(candidate))//
    .isTrue();

  assertThat(sut.isAdminAllowedUseButton(ADMIN, true, false))//
    .isTrue();
  assertThat(sut.isAdminAllowedUseButton(EVERYONE, false, false))//
    .isTrue();
  assertThat(sut.isAdminAllowedUseButton(SYSTEM_ADMIN, false, true))//
    .isTrue();
 }

 @Test
 public void testThatAllowedButtonsCanBeFiltered() {
  when(userManager.getRemoteUser())//
    .thenReturn(user);
  when(userManager.getRemoteUser().getUserKey())//
    .thenReturn(userKey);
  when(userManager.isSystemAdmin(userKey))//
    .thenReturn(false);
  when(userManager.isAdmin(userKey))//
    .thenReturn(false);

  PrnfbButton button1 = new PrnfbButton("title1", ADMIN);
  PrnfbButton button2 = new PrnfbButton("title2", EVERYONE);
  PrnfbButton button3 = new PrnfbButton("title3", SYSTEM_ADMIN);
  List<PrnfbButton> buttons = newArrayList(button1, button2, button3);

  Iterable<PrnfbButton> onlyAllowed = sut.filterAllowed(buttons);

  assertThat(onlyAllowed)//
    .containsOnly(button2);
 }

}
