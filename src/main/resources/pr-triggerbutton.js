require([
 '@atlassian/clientside-extensions-registry',
 'jquery',
 '@atlassian/aui',
 'underscore',
 'plugin/prnfb/3rdparty',
 'bitbucket/util/server',
 '@bitbucket/apps/pull-requests/initial-data'
], function(registry, $, AJS, _, thirdParty, srv, prData) {
 var prId = prData.pullRequest.id
 var repoId = prData.repository.id
 var buttonsAdminUrl = "/rest/prnfb-admin/1.0/settings/buttons";

 var waiting = '<span class="aui-icon aui-icon-wait aui-icon-small">Wait</span>';

 var dialogTemplate = function(name, content) {
  var escapedName = _.escape(name);
  return '<section role="dialog" id="confirm-dialog" class="aui-layer aui-dialog2 aui-dialog2-medium"' + //
   ' data-aui-remove-on-hide="true" aria-hidden="true">' + //
   '    <!-- Dialog header -->' + //
   '    <header class="aui-dialog2-header">' + //
   '        <h2 class="aui-dialog2-header-main">' + escapedName + '</h2>' + //
   '        <!-- Close icon -->' + //
   '        <a class="aui-dialog2-header-close">' + //
   '            <span class="aui-icon aui-icon-small aui-iconfont-close-dialog">Close</span>' + //
   '        </a>' + //
   '    </header>' + //
   '    <!-- Main dialog content -->' + //
   '    <div class="aui-dialog2-content">' + //
   content + //
   '    </div>' + //
   '    <!-- Dialog footer -->' + //
   '    <footer class="aui-dialog2-footer">' + //
   '        <!-- Actions to render on the right of the footer -->' + //
   '        <div class="aui-dialog2-footer-actions">' + //
   '            <button id="dialog-submit-button" class="aui-button aui-button-primary">' + name + '</button>' + //
   '            <button id="dialog-close-button" class="aui-button aui-button-link">Close</button>' + //
   '        </div>' + //
   '    </footer>' + //
   '</section>';
 };

 var inputTemplate = function(name, item) {
  var escapedName = _.escape(name);
  var escapedLabel = _.escape(item.label);
  var escapedValue = _.escape(item.defaultValue);
  var escapedDescription = _.escape(item.description);
  return $('' +
   '<div class="field-group">' + //
   '<label for="' + name + '">' + escapedLabel + //
   (!item.required ? '' : '<span class="aui-icon icon-required">(required)</span>') + '</label>' + //
   '<input class="text medium-field" type="text" ' + //
   'id="prnfb-form-' + name + '" name="' + name + '" placeholder="' + escapedValue + '" value="' + escapedValue + '">' + //
   '<div class="description">' + escapedDescription + '</div>' + //
   '</div>');
 };

 var textareaTemplate = function(name, item) {
  var escapedName = _.escape(name);
  var escapedLabel = _.escape(item.label);
  var escapedValue = _.escape(item.defaultValue);
  var escapedDescription = _.escape(item.description);
  return $('' +
   '<div class="field-group">' + //
   '<label for="' + escapedName + '">' + escapedLabel + //
   (!item.required ? '' : '<span class="aui-icon icon-required">(required)</span>') + '</label>' + //
   '<textarea class="textarea" id="prnfb-form-' + name + '" name="' + name + '" placeholder="' + escapedValue + '">' + //
   escapedValue + '</textarea>' + //
   '<div class="description">' + escapedDescription + '</div>' + //
   '</div>');
 };

 var checkboxTemplate = function(name, item) {
  var checkboxItemTemplate = function(nestedItem, num) {
   var isChecked = nestedItem.defaultValue;
   var outerName = _.escape(name);
   var itemName = _.escape(nestedItem.name);
   var itemLabel = _.escape(nestedItem.label);
   return $('<div class="checkbox">' + //
    '<input class="checkbox" value="' + itemName + '" type="checkbox" ' + (isChecked ? 'checked="checked"' : '') + //
    ' name="' + outerName + '[]" id="prnfb-form-' + outerName + '-' + num + '">' + //
    '<label for="prnfb-form-' + outerName + '-' + num + '"' + //
    '>' + itemLabel + '</label></div>');
  };

  var escapedLabel = _.escape(item.label);
  var escapedDescription = _.escape(item.description);
  var checkboxItems = [$('<legend><span>' + escapedLabel + '</span></legend>')];
  for (var i = 0; i < item.buttonFormElementOptionList.length; i++) {
   checkboxItems.push(checkboxItemTemplate(item.buttonFormElementOptionList[i], i));
  }
  checkboxItems.push($('<div class="description">' + escapedDescription + '</div>'));
  return $("<fieldset class='group'/>").append(checkboxItems);
 };

 var radioTemplate = function(name, item) {
  var radioItemTemplate = function(nestedItem, num) {
   var isChecked = nestedItem.name === item.defaultValue;
   var outerName = _.escape(name);
   var itemName = _.escape(nestedItem.name);
   var itemLabel = _.escape(nestedItem.label);
   return $('<div class="radio">' + //
    '<input class="radio" value="' + itemName + '" type="radio" ' + (isChecked ? 'checked="checked"' : '') + //
    'name="' + outerName + '" id="prnfb-form-' + outerName + '-' + num + '">' + //
    '<label for="prnfb-form-' + outerName + '-' + num + '">' + itemLabel + '</label></div>');
  };

  var escapedLabel = _.escape(item.label);
  var escapedDescription = _.escape(item.description);
  var radioItems = [$('<legend><span>' + escapedLabel + '</span></legend>')];
  for (var i = 0; i < item.buttonFormElementOptionList.length; i++) {
   radioItems.push(radioItemTemplate(item.buttonFormElementOptionList[i], i));
  }
  radioItems.push($('<div class="description">' + escapedDescription + '</div>'));
  return $("<fieldset class='group'/>").append(radioItems);
 };

 var confirmationTextTemplate = function(confirmationText) {
  if (!confirmationText) {
   return '';
  }

  var confirmationDiv = '<div class="description">' + confirmationText + '</div>';
  return confirmationDiv;
 };

 var formTemplate = function(formDescription) {
  if (!formDescription || formDescription.length === 0) {
   return '';
  }

  var formItems = [];
  for (var i = 0; i < formDescription.length; i++) {
   var item = formDescription[i];
   switch (item.type) {
    case "input":
     {
      formItems.push(inputTemplate(item.name, item));
      break;
     }
    case "textarea":
     {
      formItems.push(textareaTemplate(item.name, item));
      break;
     }
    case "checkbox":
     {
      formItems.push(checkboxTemplate(item.name, item));
      break;
     }
    case "radio":
     {
      formItems.push(radioTemplate(item.name, item));
      break;
     }
   }
  }
  var form = $("<form style='display:block;' class='aui'></form>");
  form.append(formItems);

  return form;
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
      close: 'manual',
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

 function submitButton(item, formResult) {
   srv.ajax({
     "type": "POST",
     "url": buttonsAdminUrl + '/' + item.uuid + '/press/repository/' + repoId +'/pullrequest/' + prId,
     "data": {
       "form": formResult
     },
     "success": function(content) {
       setTimeout(function() {
         if (content.confirmation == "on") {
           presentResult(content.notificationResponses);
         }
       }, 500);
     },
     "error": function(content) {
       AJS.flag({
         close: 'manual',
         type: 'error',
         title: "Unknown error",
         body: '<p>' + content.status + '</p>' + '<p>Check the Bitbucket Server log for more details.</p>'
       });
     }
   });

   if (item.redirectUrl) {
     window.location.replace(item.redirectUrl);
   }
 }

 function loadSettingsAndShowButtons() {
  srv.rest({
    url : buttonsAdminUrl + '/repository/' + repoId + '/pullrequest/' + prId,
    success : function(settings) {
     settings.forEach(function(item, index) {
       registry.registerExtension(
         'se.bjurr.prnfs.pull-request-notifier-for-stash:custom-buttons' + index,
         function buttonFactory(extensionAPI, context) {
           return {
             type: 'button',
             label: item.name,
             onAction : function() {
               if (item.confirmationText || item.buttonFormList && item.buttonFormList.length > 0) {
                 // Create the form and dialog
                 var confirmationText = confirmationTextTemplate(item.confirmationText);
                 var form = formTemplate(item.buttonFormList);
                 var formHtml = $("<div/>").append(confirmationText).append(form).html();
                 var $dialog = $(dialogTemplate(item.name, formHtml));
                 $dialog.appendTo($("body"));

                 var dialogRef = AJS.dialog2($dialog);

                 // When you submit the form, we will post to the server with all the form data.
                 AJS.$("#dialog-submit-button").click(function(e) {
                   var formResult = $dialog.find("form").serializeJSON();
                   e.preventDefault();
                   dialogRef.hide();
                   submitButton(item, formResult);
                 });
                 AJS.$("#dialog-close-button").click(function(e) {
                   e.preventDefault();
                   dialogRef.hide();
                 });
                 dialogRef.show();
               } else {
                 submitButton(item, null);
               }
             }
           };
         }
       );
     });
    }
  });
 }

 loadSettingsAndShowButtons();

});
