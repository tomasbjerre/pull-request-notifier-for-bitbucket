define('plugin/prnfb/admin', [
 'jquery',
 'plugin/prnfb/utils'
], function($, utils) {
 var settingsAdminUrlPostUrl = AJS.contextPath() + "/rest/prnfb-admin/1.0/settings";
 var settingsAdminUrl = settingsAdminUrlPostUrl;

 var notificationsAdminUrlPostUrl = AJS.contextPath() + "/rest/prnfb-admin/1.0/settings/notifications";
 var notificationsAdminUrl = notificationsAdminUrlPostUrl;

 var buttonsAdminUrlPostUrl = AJS.contextPath() + "/rest/prnfb-admin/1.0/settings/buttons";
 var buttonsAdminUrl = buttonsAdminUrlPostUrl;

 var projectKey;
 if ($('#prnfbRepositorySlug').length !== 0) {
  projectKey = $('#prnfbProjectKey').val();
  var repositorySlug = $('#prnfbRepositorySlug').val();

  notificationsAdminUrl = notificationsAdminUrlPostUrl + '/projectKey/' + projectKey + '/repositorySlug/' + repositorySlug;
  buttonsAdminUrl = buttonsAdminUrlPostUrl + '/projectKey/' + projectKey + '/repositorySlug/' + repositorySlug;
 } else if ($('#prnfbProjectKey').length !== 0) {
  projectKey = $('#prnfbProjectKey').val();

  notificationsAdminUrl = notificationsAdminUrlPostUrl + '/projectKey/' + projectKey;
  buttonsAdminUrl = buttonsAdminUrlPostUrl + '/projectKey/' + projectKey;
 }

 $(document)
  .ajaxStart(function() {
   $('.prnfb button').attr('aria-disabled', 'true');
  })
  .ajaxStop(function() {
   $('.prnfb button').attr('aria-disabled', 'false');
  });

 $(document).ready(function() {
  utils.setupForm('#prnfbsettingsadmin', settingsAdminUrl, settingsAdminUrlPostUrl);
  utils.setupForms('#prnfbbuttonadmin', buttonsAdminUrl, buttonsAdminUrlPostUrl);
  utils.setupForms('#prnfbnotificationadmin', notificationsAdminUrl, notificationsAdminUrlPostUrl);
 });
});

AJS.$(document).ready(function() {
 require('plugin/prnfb/admin');
});
