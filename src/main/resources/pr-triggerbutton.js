define('plugin/prnfb/pr-triggerbutton', [
 'jquery',
 'aui',
 'bitbucket/util/state'
], function($, AJS, pageState) {

 var buttonsAdminUrl = AJS.contextPath() + "/rest/prnfb-admin/1.0/settings/buttons";

 var waiting = '<span class="aui-icon aui-icon-wait aui-icon-small">Wait</span>';

 var $buttonArea = $(".triggerManualNotification").closest('ul');
 var buttonTemplate = function(name) {
  return $('<li><button class="aui-button aui-button-link" role="menuitem">' + name + '</button></li>');
 };

 var presentResult = function(response) {
  var successTriggers = [];
  var failTriggers = [];
  if (response) {
   for (var i = 0; i < response.length; i++) {
    var notificationResponse = response[i];
    if (notificationResponse.status >= 200 && notificationResponse.status <= 299) {
     AJS.flag({
      close: 'auto',
      type: 'success',
      title: notificationResponse.notificationName.replace(/<script>/g, 'script'),
      body: '<p>You may check network tab in web browser for exact URL and response.</p>'
     });
    } else {
     AJS.flag({
      close: 'auto',
      type: 'error',
      title: notificationResponse.notificationName.replace(/<script>/g, 'script'),
      body: '<p>' + notificationResponse.status + ' ' + notificationResponse.uri + '</p>' +
       '<p>You may check network tab in web browser for exact URL and response.</p>'
     });
    }
   }
  }

  if (!response || response.length === 0) {
   AJS.flag({
    close: 'auto',
    type: 'warning',
    title: 'No triggers were invoked',
    body: '<p>No triggers were invoked when buttons was pressed.</p>'
   });
  }
 };

 if ($buttonArea.length === 0) {
  //Before 4.4.0
  $buttonArea = $(".triggerManualNotification").parent();
  buttonTemplate = function(name) {
   return $('<button class="aui-button">' + name + '</button>');
  };
 }

 $(".triggerManualNotification").remove();

 function loadSettingsAndShowButtons() {
  $.get(buttonsAdminUrl + '/repository/' + pageState.getRepository().id + '/pullrequest/' + pageState.getPullRequest().id, function(settings) {
   settings.forEach(function(item) {
    var $buttonDropdownItem = buttonTemplate(item.name.replace(/<script>/g, 'script'));
    $buttonDropdownItem.click(function() {
     var $this = $(this);
     $this.attr("disabled", "disabled");
     $this.attr("aria-disabled", "true");
     $this.prepend(waiting);

     $.ajax({
      "type": "POST",
      "url": buttonsAdminUrl + '/' + item.uuid + '/press/repository/' + pageState.getRepository().id + '/pullrequest/' + pageState.getPullRequest().id,
      "success": function(content) {
       setTimeout(function() {
        $this.removeAttr("disabled");
        $this.removeAttr("aria-disabled");
        $this.find("span").remove();

        if (content.confirmation == "on") {
         presentResult(content.notificationResponses);
        }
       }, 500);
      },
      "error": function(content) {
       AJS.flag({
        close: 'auto',
        type: 'error',
        title: "Unknown error",
        body: '<p>' + content.status + '</p>' + '<p>Check the Bitbucket Server log for more details.</p>'
       });
      }
     });

    });
    $buttonArea.append($buttonDropdownItem);
   });
  });
 }

 loadSettingsAndShowButtons();

 //If a reviewer approves the PR, then a button may become visible
 $('.aui-button.approve').click(function() {
  setTimeout(function() {
   loadSettingsAndShowButtons();
  }, 1000);
 });
});

AJS.$(document).ready(function() {
 require('plugin/prnfb/pr-triggerbutton');
});
