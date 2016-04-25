define('plugin/prnfb/pr-triggerbutton', [
 'jquery',
 'aui',
 'bitbucket/util/state'
], function($, AJS, pageState) {

 var getResourceUrl = function() {
  return AJS.contextPath() + '/rest/prnfb-admin/1.0/manual/?repositoryId=' + pageState.getRepository().id + '&pullRequestId=' + pageState.getPullRequest().id;
 };

 var waiting = '<span class="aui-icon aui-icon-wait aui-icon-small">Wait</span>';

 var $buttonArea = $(".triggerManualNotification").parent();
 var $buttonTemplate = $(".triggerManualNotification");
 $buttonTemplate.empty().remove();

 $buttonDropdownArea = $('<div id="triggerManualNotification-actions" class="aui-style-default aui-dropdown2"><ul class="aui-list-truncate"></ul></div>');
 $buttonDropdownItems = $buttonDropdownArea.find("ul");

 var $buttonDropdownParent = $buttonTemplate.clone();
 $buttonDropdownParent.html("Actions");
 $buttonDropdownParent.attr("aria-owns", "triggerManualNotification-actions");
 $buttonDropdownParent.attr("aria-haspopup", "true");
 $buttonDropdownParent.addClass("aui-style-default aui-dropdown2-trigger");
 $buttonArea.append($buttonDropdownParent);
 $buttonDropdownParent.hide();

 $("body").append($buttonDropdownArea)

 function loadSettingsAndShowButtons() {
  var hasButtons = false;
  $buttonDropdownItems.empty();
  $.get(getResourceUrl(), function(settings) {
   settings.forEach(function(item) {
    hasButtons = true;

    var $buttonDropdownItem = $('<li><a class="aui-icon-container" href="#">' + item.title + '</a></li>');
    $buttonDropdownItem.find("a").click(function() {
     var $this = $(this);
     $this.attr("disabled", "disabled");
     $this.attr("aria-disabled", "true");
     $this.prepend(waiting);

     $.post(getResourceUrl() + '&formIdentifier=' + item.formIdentifier, function() {
      setTimeout(function() {
       $this.removeAttr("disabled");
       $this.removeAttr("aria-disabled");
       $this.find("span").remove();
      }, 500);
     });
    });

    $buttonDropdownItems.append($buttonDropdownItem);
   });

   if (hasButtons) {
    $buttonDropdownParent.show();
   }
  });
 }

 loadSettingsAndShowButtons();

 //If a reviewer approves the PR, then a button may become visible
 $('.aui-button.approve').click(function() {
  setTimeout(function() {
   $buttonDropdownParent.hide();
   loadSettingsAndShowButtons();
  }, 1000);
 });
});

AJS.$(document).ready(function() {
 require('plugin/prnfb/pr-triggerbutton');
});
