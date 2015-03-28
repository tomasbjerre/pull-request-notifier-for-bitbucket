(function ($) {
 var config_resource = AJS.contextPath() + "/rest/prnfs-admin/1.0/";
 $(document).ready(function() {
  function setEvents() {
   $('input[name="delete"]').click(function(e) {
     var $form = $(this).closest('form');
     var formIdentifier = $('input[name="FORM_IDENTIFIER"]',$form).val();
     $.ajax({
      url: config_resource + formIdentifier,
      dataType: "json",
      type: "DELETE",
      error: function(xhr, data, error) {
       console.log(xhr);
       console.log(data);
       console.log(error);
      },
      success: function(data, text, xhr) {
       $form.remove();
      }
     });
   });

   $('input[name="save"]').click(function(e) {
     var $form = $(this).closest('form');
     $(".post",$form).html("Saving...");
     $.ajax({
      url: config_resource,
      dataType: "json",
      type: "POST",
      contentType: "application/json",
      data: JSON.stringify($form.serializeArray(), null, 2),
      processData: false,
      error: function(xhr, data, error) {
       $.each(xhr.responseJSON, function(field,errorString) {
        $(".error."+field,$form).html(errorString);
        $(".post",$form).html("There were errors, form not saved!");
       });
      },
      success: function(data, text, xhr) {
       getAll();
      }
     });
   });
  }
  
  function addNewForm() {
   var $template = $(".prnfs-template").clone();
   $('input[name="delete"]',$template).remove();
   $(".prnfs").append($template.html());
  }

  function getAll() {
   $.ajax({
    url: config_resource,
    dataType: "json"
   }).done(function(configs) {
    $(".prnfs").html("");
    $.each(configs, function(index, config) {
     var $template = $(".prnfs-template").clone();
     $.each(config, function(fieldIndex,field_map) {
      $('input[type="text"][name="'+field_map.name+'"]', $template).attr('value', field_map.value);
      $('input[type="hidden"][name="'+field_map.name+'"]', $template).attr('value', field_map.value);
      $('input[type="checkbox"][name="'+field_map.name+'"][value="'+field_map.value+'"]', $template).attr('checked','checked');
     });
     $(".prnfs").append($template.html());
    });
    addNewForm();
    setEvents();
   });
  }
  
  getAll();
 });
})(AJS.$ || jQuery);