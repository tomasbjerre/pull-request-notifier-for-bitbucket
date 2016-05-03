define('plugin/prnfb/utils', [
 'jquery',
], function($) {

 function postForm(url, formId) {
  var data = $(formId).serializeArray().reduce(function(obj, v) {
   if (v.value === 'on') {
    obj[v.name] = true;
   } else {
    obj[v.name] = v.value;
   }
   return obj;
  }, {});
  var jsonString = JSON.stringify(data);
  $.ajax({
   url: url,
   type: "POST",
   contentType: "application/json; charset=utf-8",
   dataType: "json",
   data: jsonString
  });
 }

 function populateProjects(data, selected) {
  $('#fromProject').empty();
  for (var i = 0; i < data.values.length; i++) {
   var projectKey = data.values[i].key;
   if (projectKey === selected) {
    $('#fromProject').append('<option value="' + projectKey + '" selected>' + projectKey + '</option>');
   } else {
    $('#fromProject').append('<option value="' + projectKey + '">' + projectKey + '</option>');
   }
  }
 }

 function getProjects(whenDone) {
  var projectsUrl = AJS.contextPath() + "/rest/api/1.0/projects?limit=999999"; 
  $.getJSON(projectsUrl, function(data) {
   whenDone(data);
  });
 }

 function populateRepos(data, selected) {
  $('#fromRepo').empty();
  for (var i = 0; i < data.values.length; i++) {
   var repoSlug = data.values[i].slug;
   if (repoSlug === selected) {
    $('#fromRepo').append('<option value="' + repoSlug + '" selected>' + repoSlug + '</option>');
   } else {
    $('#fromRepo').append('<option value="' + repoSlug + '">' + repoSlug + '</option>');
   }
  }
 }

 function getRepos(projectKey, whenDone) {
  var reposUrl = AJS.contextPath() + "/rest/api/1.0/projects/" + projectKey + "/repos?limit=999999"; 
  $.getJSON(reposUrl, function(data) {
   whenDone(data);
  });
 }

 function setupRepoSettingsForm(repoSettings) {
  getProjects(function(data) {
   if (repoSettings) {
    populateProjects(data, repoSettings.projectKey);
   } else {
    populateProjects(data, undefined);
   }
   getRepos($('#projectKey').val(), function(data) {
    if (repoSettings) {
     populateRepos(data, repoSettings.repositorySlug);
    } else {
     populateRepos(data, undefined);
    }
   });

   if (repoSettings) {
    $('#repositoryDetails').attr('checked', repoSettings.repositoryDetails);
   }
  });

  $('#projectKey').change(function() {
   getRepos($('#fromProject').val(), function(data) {
    if (repoSettings) {
     populateRepos(data, repoSettings.fromRepo);
    } else {
     populateRepos(data, undefined);
    }
   });
  });
 }

 $(document)
  .ajaxStart(function() {
   $('.prnfb button').attr('aria-disabled', 'true');
  })
  .ajaxStop(function() {
   $('.prnfb button').attr('aria-disabled', 'false');
  });

 return {
  postForm: postForm,
  getRepos: getRepos,
  getProjects: getProjects,
  forAllRepos: forAllRepos,
  setupRepoSettingsForm: setupRepoSettingsForm
 }
});
