define('plugin/prnfs/pr-triggerbutton', [
  'jquery',
  'aui',
  'model/page-state'
], function($, AJS, pageState) {
  
  var getResourceUrl = function() {
    return AJS.contextPath() + '/rest/prnfs-admin/1.0/manual/?repositoryId=' + pageState.getRepository().getId() + '&pullRequestId=' + pageState.getPullRequest().getId();
  };

  var waiting = '<span class="aui-icon aui-icon-wait">Wait</span>';

  var $buttonArea = $(".triggerManualNotification").parent();
  var $buttonTemplate = $(".triggerManualNotification");
  $buttonTemplate.empty().remove();
  
  $.get(getResourceUrl(), function(settings) {
  	settings.forEach(function(item) {
  	  var $button = $buttonTemplate.clone();
  	  $button.html(item.title);
  	  $button.click(function() {
        var $this = $(this);
        var text = $this.text();

        $this.attr("disabled", "disabled").html(waiting + " " + text);

        $.post(getResourceUrl()+'&formIdentifier='+item.formIdentifier, function() {
          setTimeout(function() {  
              $this.removeAttr("disabled").text(text);
          }, 500);
        });
        
        return false;
      });
      
      $buttonArea.append($button);
  	});
  });

});

AJS.$(document).ready(function() {
    require('plugin/prnfs/pr-triggerbutton');
});
