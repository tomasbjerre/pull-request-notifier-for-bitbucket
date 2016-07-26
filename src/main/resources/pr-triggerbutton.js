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
 }
 var dialogTemplate = function(name, content) {
   return '<section role="dialog" id="confirm-dialog" class="aui-layer aui-dialog2 aui-dialog2-medium" data-aui-remove-on-hide="true" aria-hidden="true">'
      + '    <!-- Dialog header -->'
      + '    <header class="aui-dialog2-header">'
      + '        <h2 class="aui-dialog2-header-main">' + name + ' Confirmation</h2>'
      + '        <!-- Close icon -->'
      + '        <a class="aui-dialog2-header-close">'
      + '            <span class="aui-icon aui-icon-small aui-iconfont-close-dialog">Close</span>'
      + '        </a>'
      + '    </header>'
      + '    <!-- Main dialog content -->'
      + '    <div class="aui-dialog2-content">'
      + '    ' + content
      + '    </div>'
      + '    <!-- Dialog footer -->'
      + '    <footer class="aui-dialog2-footer">'
      + '        <!-- Actions to render on the right of the footer -->'
      + '        <div class="aui-dialog2-footer-actions">'
      + '            <button id="dialog-close-button" class="aui-button aui-button-link">Close</button>'
      + '        </div>'
      + '    </footer>'
      + '</section>'
 }
 
 var dialogContentTemplate = function(response) {
  var successTriggers = [];
  var failTriggers = [];
  if (response) {
   for(var key in response.results) {
    if (response.results.hasOwnProperty(key)) {
     var val = response.results[key];
     if (val.status >= 200 && val.status <= 299) {
       successTriggers.push("<li>"+key+"</li>");
     } else {
       failTriggers.push("<li>"+key+"</li>");
     }
    }
   }
  } 
  result = "";
  if (successTriggers.length > 0) {
   result += "<p>Succeeded Triggers:<ul>" + successTriggers.join("") + "</ul></p>";
  }
  if (failTriggers.length > 0) {
   result += "<p>Failed Triggers:<ul>" + failTriggers.join("") + "</ul></p>";
  }
  if ((successTriggers.length + failTriggers.length) == 0) {
   result += "<p>No triggers were invoked</p>";
  }
  return result;
 }
 
 if ($buttonArea.length === 0) {
  //Before 4.4.0
  $buttonArea = $(".triggerManualNotification").parent();
  buttonTemplate = function(name) {
   return $('<button class="aui-button">' + name + '</button>'); 
  }
 }

 $(".triggerManualNotification").remove();

 function loadSettingsAndShowButtons() {
  $.get(buttonsAdminUrl + '/repository/' + pageState.getRepository().id + '/pullrequest/' + pageState.getPullRequest().id, function(settings) {
   settings.forEach(function(item) {
    var $buttonDropdownItem = buttonTemplate(item.name);
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
         
         if (item.confirmation == "on") {
          var $dialog = $(dialogTemplate(item.name, dialogContentTemplate(content)));
          $dialog.appendTo($("body"));
          
          var dialogRef = AJS.dialog2($dialog);
          AJS.$("#dialog-close-button").click(function(e) {
              e.preventDefault();
              dialogRef.hide();
          });
          dialogRef.show();
         }
        }, 500);
      },
      "error": function(content) {
        if (item.confirmation == "on") {
         var $dialog = $(dialogTemplate(item.name, "<p>There was an error triggering the notification</p>"));
         $dialog.appendTo($("body"));
         
         var dialogRef = AJS.dialog2($dialog);
         AJS.$("#dialog-close-button").click(function(e) {
             e.preventDefault();
             dialogRef.hide();
         });
         dialogRef.show();
       }
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
