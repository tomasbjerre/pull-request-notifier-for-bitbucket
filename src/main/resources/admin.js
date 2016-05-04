define('plugin/prnfb/admin', [
 'jquery',
 'aui',
 'plugin/prnfb/utils'
], function($, AJS, utils) {
 var settingsAdminUrl = AJS.contextPath() + "/rest/prnfb-admin/1.0/settings"; 
 var notificationsAdminUrl = AJS.contextPath() + "/rest/prnfb-admin/1.0/settings/notifications"; 
 var buttonsAdminUrl = AJS.contextPath() + "/rest/prnfb-admin/1.0/settings/buttons"; 


 $(document)
  .ajaxStart(function() {
   $('.prnfb button').attr('aria-disabled', 'true');
  })
  .ajaxStop(function() {
   $('.prnfb button').attr('aria-disabled', 'false');
  });

 $(document).ready(function() {
  utils.setupForm('#prnfbsettingsadmin', settingsAdminUrl);
  utils.setupForms('#prnfbbuttonadmin', buttonsAdminUrl);
  utils.setupForms('#prnfbnotificationadmin', notificationsAdminUrl);
 });
});

AJS.$(document).ready(function() {
 require('plugin/prnfb/admin');
});
