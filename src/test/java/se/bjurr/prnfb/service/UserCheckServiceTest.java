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
 private SettingsService settingsService;
 private UserCheckService sut;
 @Mock
 private UserProfile user;
 private final UserKey userKey = new UserKey("userkey");
 @Mock
 private UserManager userManager;

 @Before
 public void before() {
  initMocks(this);
  this.sut = new UserCheckService(this.userManager, this.settingsService);
 }

 @Test
 public void testThatAdminAllowedCanBeChecked() {
  this.sut.isAdminAllowed();
 }

 @Test
 public void testThatAllowedButtonsCanBeFiltered() {
  when(this.userManager.getRemoteUser())//
    .thenReturn(this.user);
  when(this.userManager.getRemoteUser().getUserKey())//
    .thenReturn(this.userKey);
  when(this.userManager.isSystemAdmin(this.userKey))//
    .thenReturn(false);
  when(this.userManager.isAdmin(this.userKey))//
    .thenReturn(false);

  PrnfbButton button1 = new PrnfbButton("title1", ADMIN);
  PrnfbButton button2 = new PrnfbButton("title2", EVERYONE);
  PrnfbButton button3 = new PrnfbButton("title3", SYSTEM_ADMIN);
  List<PrnfbButton> buttons = newArrayList(button1, button2, button3);

  Iterable<PrnfbButton> onlyAllowed = this.sut.filterAllowed(buttons);

  assertThat(onlyAllowed)//
    .containsOnly(button2);
 }

 @Test
 public void testThatButtonAllowedCanBeChecked() {
  when(this.userManager.getRemoteUser())//
    .thenReturn(this.user);
  when(this.userManager.getRemoteUser().getUserKey())//
    .thenReturn(this.userKey);
  when(this.userManager.isSystemAdmin(this.userKey))//
    .thenReturn(true);
  PrnfbButton candidate = new PrnfbButton("title", ADMIN);
  assertThat(this.sut.isAllowedUseButton(candidate))//
    .isTrue();

  assertThat(this.sut.isAdminAllowedUseButton(ADMIN, true, false))//
    .isTrue();
  assertThat(this.sut.isAdminAllowedUseButton(EVERYONE, false, false))//
    .isTrue();
  assertThat(this.sut.isAdminAllowedUseButton(SYSTEM_ADMIN, false, true))//
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
