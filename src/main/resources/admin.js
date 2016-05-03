define('plugin/prnfb/admin', [
 'jquery',
 'aui',
 'plugin/prnfb/utils'
], function($, AJS, common) {

 $(document).ready(function() {
  var globalRepoAdminUrl = AJS.contextPath() + "/rest/prnfb-admin/1.0"; 
  $.getJSON(globalRepoAdminUrl, function(data) {
   common.setupRepoSettingsForm(data);
  });
 });
});

AJS.$(document).ready(function() {
 require('plugin/prnfb/admin');
});
