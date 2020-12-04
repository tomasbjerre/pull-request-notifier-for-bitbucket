define('plugin/prnfb/utils', [
 'jquery',
 'plugin/prnfb/3rdparty',
 'bitbucket/util/server'
], function($, trdparty, srv) {

 function postForm(url, formSelector, whenDone) {
  $('.statusresponse').empty();
  var jsonString = $(formSelector).serializeJSON();
  srv.ajax({
   url: url,
   type: "POST",
   contentType: "application/json; charset=utf-8",
   dataType: "json",
   data: jsonString,
   success: function(data) {
    whenDone(function() {
     if (data && data.uuid) {
      doSetupForm(formSelector, url + '/' + data.uuid);
     }
     AJS.flag({
      close: 'auto',
      type: 'success',
      title: 'Saved',
      body: '<p>=)</p>'
     });
    });
   },
   error: function(xhr, status, error) {
    AJS.messages.error(".statusresponse", {
     title: 'Error',
     body: '<p>' +
      'Sent POST ' + url + ':<br/><code>' + jsonString.replace(/<script>/g, 'script') + '</code><br/><br/>' +
      'Got:<br/><code>' + xhr.responseText.replace(/<script>/g, 'script') + '</code><br/><br/>' +
      '</p>'
    });
    $("html, body").animate({
     scrollTop: 0
    }, "slow");
   }
  });
 }

 function populateProjects($form, data, selected) {
  $('[name=projectKey]', $form).empty();
  if (!selected) {
   $('[name=projectKey]', $form).append('<option value="" selected>Any</option>');
  } else {
   $('[name=projectKey]', $form).append('<option value="">Any</option>');
  }

  for (var i = 0; i < data.values.length; i++) {
   var projectKey = data.values[i].key;
   if (projectKey === selected) {
    $('[name=projectKey]', $form).append('<option value="' + projectKey + '" selected>' + projectKey + '</option>');
   } else {
    $('[name=projectKey]', $form).append('<option value="' + projectKey + '">' + projectKey + '</option>');
   }
  }
 }

 function getProjects(whenDone) {
  var projectsUrl = "/rest/api/1.0/projects?limit=999999";
  srv.rest ({
    url : projectsUrl,
    success: function(data) {
        whenDone(data);
    }
  });
 }

 function populateRepos($form, data, selected) {
  $('[name=repositorySlug]', $form).empty();
  if (!selected) {
   $('[name=repositorySlug]', $form).append('<option value="" selected>Any</option>');
  } else {
   $('[name=repositorySlug]', $form).append('<option value="">Any</option>');
  }

  if (data) {
   for (var i = 0; i < data.values.length; i++) {
    var repoSlug = data.values[i].slug;
    if (repoSlug === selected) {
     $('[name=repositorySlug]', $form).append('<option value="' + repoSlug + '" selected>' + repoSlug + '</option>');
    } else {
     $('[name=repositorySlug]', $form).append('<option value="' + repoSlug + '">' + repoSlug + '</option>');
    }
   }
  }
 }

 function getRepos(projectKey, whenDone) {
  if (!projectKey) {
   whenDone();
   return;
  }
  var reposUrl = "/rest/api/1.0/projects/" + projectKey + "/repos?limit=999999";
  srv.rest ({
      url : reposUrl,
      success: function(data) {
          whenDone(data);
      }
    });
 }

 function setupProjectAndRepoSettingsInForm($form, hasProjectAndRepo) {
  getProjects(function(data) {
   if (hasProjectAndRepo && hasProjectAndRepo.projectKey) {
    populateProjects($form, data, hasProjectAndRepo.projectKey);
   } else {
    populateProjects($form, data, undefined);
   }
   getRepos($('[name=projectKey]', $form).val(), function(data) {
    if (hasProjectAndRepo && hasProjectAndRepo.repositorySlug) {
     populateRepos($form, data, hasProjectAndRepo.repositorySlug);
    } else {
     populateRepos($form, data, undefined);
    }
   });
  });
 }

 function isAllFieldsEmpty($within) {
  var inputs = $within.find('input');
  for (var i = 0; i < inputs.length; i++) {
   if ($(inputs[i]).val()) {
    return false;
   }
  }
  var textareas = $within.find('textarea');
  for (var j = 0; j < textareas.length; j++) {
   if ($(textareas[j]).val()) {
    return false;
   }
  }
  return true;
 }

 function listFieldChanged($changedField) {
  var $listFieldsDiv = $changedField.closest('.listfields');
  var $listField = $changedField.closest('.listfield');
  var $listFieldsList = $listFieldsDiv.find('.listfield');
  var empties = [];
  $listFieldsList.each(function(index, listFieldEl) {
   if (isAllFieldsEmpty($(listFieldEl))) {
    empties.push($(listFieldEl));
   }
  });

  if (empties.length > 1) {
   for (var i = 1; i < empties.length; i++) {
    empties[i].remove();
   }
  }

  if (empties.length === 0) {
   $empty = $listField.clone();
   $empty.find('input, textarea').val('');
   $listFieldsDiv.append($empty);
  }
 }

 function populateForm(formSelector, data) {
  if (data.buttonFormList) {
   data.buttonFormList = undefined;
  }
  $(formSelector).populate(data);

  $(formSelector).find('.template').each(function(index, el) {
   var template = $(el).data('template');
   var field = $(el).data('field');
   var target = $(el).data('target');
   var emptyJson = $(el).data('empty').replace(/\'/g, '"');
   var empty = JSON.parse(emptyJson);
   var rendered = "";
   if (data[field]) {
    for (var i = 0; i < data[field].length; i++) {
     rendered += AJS.template.load(template).fill(data[field][i]);
    }
   }
   rendered += AJS.template.load(template).fill(empty);
   $(target).html(rendered);
  });
 }

 function clearForm(formSelector) {
  $(formSelector).find('input[type=text], textarea, select').val('');
  $(formSelector).find('input[type=checkbox]').removeAttr('checked');
  $(formSelector).find('input[type=radio]').prop('checked', false);
 }

 function setupForm(formSelector, url, postUrl) {
  $(formSelector).submit(function(e) {
   e.preventDefault();
   postForm(postUrl, formSelector, function(f) {
    f();
   });
  });

  doSetupForm(formSelector, url);
 }

 function doSetupForm(formSelector, url) {
  $.getJSON(url, function(data) {
   setupProjectAndRepoSettingsInForm($(formSelector), data);
   populateForm(formSelector, data);
  });
 }

 function setupForms(formSelector, restResource, postUrl) {

  setupProjectAndRepoSettingsInForm($(formSelector));

  $(formSelector + ' [name=projectKey]').change(function() {
   var $form = $(this).closest('form');
   getRepos(this.value, function(data) {
    populateRepos($form, data, undefined);
   });
  });

  function populateSelect(whenDone) {
   clearForm(formSelector);
   $.getJSON(restResource, function(data) {
    $(formSelector + ' [name=uuid]').empty();
    $(formSelector + ' [name=uuid]').append('<option value="">New</option>');
    for (var i = 0; i < data.length; i++) {
     var name = data[i].name;
     name = name.replace(/<script>/g, 'script');
     $(formSelector + ' [name=uuid]').append('<option value="' + data[i].uuid + '">' + (data[i].projectKey || '') + ' ' + (data[i].repositorySlug || '') + ' ' + name + '</option>');
    }
    if (whenDone) {
     whenDone();
    }
   });
  }
  populateSelect();

  $(formSelector + ' [name=uuid]').change(function() {
   var changedTo = $(this).val();
   if (changedTo) {
    doSetupForm(formSelector, postUrl + '/' + changedTo);
   } else {
    populateSelect();
   }
  });

  populateForm(formSelector, {});

  $(formSelector).submit(function(e) {
   e.preventDefault();
   postForm(postUrl, formSelector, populateSelect);
  });

  $(formSelector + ' button[name=delete]').click(function(e) {
   e.preventDefault();
   var uuid = $(formSelector).find('[name=uuid]').val();
   if (uuid) {
    srv.ajax({
     url: postUrl + '/' + uuid,
     type: 'DELETE',
     success: function(result) {
      populateSelect();
     }
    });
   }
  });
 }

 $(document).ready(function() {
  listFieldChanged($(this));
  $('.listfield').find('input, textarea').live('keyup', function() {
   listFieldChanged($(this));
  });
 });

 return {
  setupForm: setupForm,
  setupForms: setupForms
 };
});
