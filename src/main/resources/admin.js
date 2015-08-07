(function ($) {
 var config_resource = AJS.contextPath() + "/rest/prnfs-admin/1.0/";
 $(document).ready(function() {
  function getEmpties($headers) {
   var empties = [];
   $('.header', $headers).each(function(iheader,$header){
    var allValue = "";
    $('input[type="text"]',$header).each(function(iinput,$input){
     allValue += $input.value.trim();
    });
    if (allValue === "") {
     empties.push($header);
    }
   });
   return empties;
  }

  function adjustHeaders($headers) {
   var empties = getEmpties($headers)
   if (empties.length == 0) {
    $headers.append($(".prnfs-template .header")[0].outerHTML);
   }

   if (empties.length > 1) {
    empties[1].remove();
   }
  }
   
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
   
   $('.expandable').each(function(index, el) {
    var $element = $(el);
    $element.find('.toggle').click(function() {
     $element.toggleClass('expanded');
    });
   });
   //If there are only a few triggers configured, they can be expanded by default without confusion.
   if ($('.expandable').length < 4) {
    $('.expandable').addClass('expanded');
   }

   $('.headers').keyup(function(e) {
     var $headers = $(this);
     adjustHeaders($headers);
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
       $(".error."+xhr.responseJSON.field,$form).html(xhr.responseJSON.error);
       if (xhr.responseJSON.field) {
        $(".post",$form).html("There were errors, form not saved!");
       } else {  
        $(".post",$form).html(xhr.responseText);
       }
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
   $('input[name=method][value=GET]', $template).attr('checked','checked');
   $('.expandable',$template).addClass('expanded');
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
      $('.variable[data-variable="'+field_map.name+'"]', $template).html(field_map.value);
      $('input[type="text"][name="'+field_map.name+'"]', $template).attr('value', field_map.value);
      $('input[type="password"][name="'+field_map.name+'"]', $template).attr('value', field_map.value);
      $('textarea[name="'+field_map.name+'"]', $template).text(field_map.value);
      $('input[type="hidden"][name="'+field_map.name+'"]', $template).attr('value', field_map.value);
      $('input[type="checkbox"][name="'+field_map.name+'"][value="'+field_map.value+'"]', $template).attr('checked','checked');
      $('input[type="radio"][name="'+field_map.name+'"][value="'+field_map.value+'"]', $template).attr('checked','checked');
      $('.visibleif.'+field_map.name+'_'+field_map.value.replace(/[^a-zA-Z]/g,''), $template).show();
     });
     
     var header_names = [];
     var header_values = [];
     $.each(config, function(fieldIndex,field_map) {
      if (field_map.name == 'header_name') {
       header_names.push(field_map.value);
      } else if (field_map.name == 'header_value') {
       header_values.push(field_map.value);
      }
     });
     for (var i = 0; i < header_names.length; i++) {
      $('input[type="text"][name="header_name"]', $template).last().attr('value', header_names[i]);
      $('input[type="text"][name="header_value"]', $template).last().attr('value', header_values[i]);
      adjustHeaders($(".headers", $template));
     }
     
     if (!$('input[name=method]:checked', $template).val()) {
      $('input[name=method][value=GET]', $template).attr('checked','checked');
     }
     $(".prnfs").append($template.html());
    });
    addNewForm();
    setEvents();
   });
  }
  
  getAll();
 });
})(AJS.$ || jQuery);