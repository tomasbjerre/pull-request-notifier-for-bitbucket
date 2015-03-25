(function ($) {
  var url = AJS.contextPath() + "/rest/prnfs-admin/1.0/";
  $(document).ready(function() {
    $(".trigger").submit(function(e) {
      e.preventDefault();
      AJS.$.ajax({
        url: url,
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify(AJS.$(".trigger").serializeArray(), null, 2),
        processData: false
      });
    });

    $.ajax({
      url: url,
      dataType: "json"
    }).done(function(config) {
    
    });
  });
})(AJS.$ || jQuery);