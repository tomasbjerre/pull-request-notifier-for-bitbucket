package se.bjurr.prnfb.service;

import static com.google.common.collect.Iterables.filter;
import static se.bjurr.prnfb.settings.USER_LEVEL.ADMIN;
import static se.bjurr.prnfb.settings.USER_LEVEL.EVERYONE;

import java.util.List;

import se.bjurr.prnfb.settings.PrnfbButton;
import se.bjurr.prnfb.settings.USER_LEVEL;

import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.user.UserProfile;
import com.google.common.base.Predicate;

public class UserCheckService {
 private final UserManager userManager;
 private final SettingsService settingsService;

 public UserCheckService(UserManager userManager, SettingsService settingsService) {
  this.userManager = userManager;
  this.settingsService = settingsService;
 }

 public boolean isViewAllowed() {
  UserProfile user = userManager.getRemoteUser();
  if (user == null) {
   return false;
  }
  return true;
 }

 public boolean isAdminAllowed() {
  final UserProfile user = userManager.getRemoteUser();
  if (user == null) {
   return false;
  }
  USER_LEVEL adminRestriction = settingsService.getPrnfbSettingsData().getAdminRestriction();
  return isAdminAllowedUseButton(adminRestriction);
 }

 public boolean isAllowedUseButton(PrnfbButton candidate) {
  return isAdminAllowedUseButton(candidate.getUserLevel());
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

 private boolean isAdminAllowedUseButton(USER_LEVEL adminRestriction) {
  UserKey userKey = userManager.getRemoteUser().getUserKey();
  boolean isAdmin = userManager.isAdmin(userKey);
  boolean isSystemAdmin = userManager.isSystemAdmin(userKey);
  return isAdminAllowedUseButton(adminRestriction, isAdmin, isSystemAdmin);
 }

 boolean isAdminAllowedUseButton(USER_LEVEL userLevel, boolean isAdmin, boolean isSystemAdmin) {
  return userLevel == EVERYONE //
    || isSystemAdmin //
    || isAdmin && userLevel == ADMIN;
 }

}
