define('plugin/prnfb/3rdparty', [
 'jquery'
], function($) {

 /**
  * jQuery serializeObject
  * @copyright 2014, macek <paulmacek@gmail.com>
  * @link https://github.com/macek/jquery-serialize-object
  * @license BSD
  * @version 2.5.0
  */
 ! function(e, i) {
  if ("function" == typeof define && define.amd) define(["exports", "jquery"], function(e, r) {
   return i(e, r)
  });
  else if ("undefined" != typeof exports) {
   var r = require("jquery");
   i(exports, r)
  } else i(e, e.jQuery || e.Zepto || e.ender || e.$)
 }(this, function(e, i) {
  function r(e, r) {
   function n(e, i, r) {
    return e[i] = r, e
   }

   function a(e, i) {
    for (var r, a = e.match(t.key); void 0 !== (r = a.pop());)
     if (t.push.test(r)) {
      var u = s(e.replace(/\[\]$/, ""));
      i = n([], u, i)
     } else t.fixed.test(r) ? i = n([], r, i) : t.named.test(r) && (i = n({}, r, i));
    return i
   }

   function s(e) {
    return void 0 === h[e] && (h[e] = 0), h[e]++
   }

   function u(e) {
    switch (i('[name="' + e.name + '"]', r).attr("type")) {
     case "checkbox":
      return "on" === e.value ? !0 : e.value;
     default:
      return e.value
    }
   }

   function f(i) {
    if (!t.validate.test(i.name)) return this;
    var r = a(i.name, u(i));
    return l = e.extend(!0, l, r), this
   }

   function d(i) {
    if (!e.isArray(i)) throw new Error("formSerializer.addPairs expects an Array");
    for (var r = 0, t = i.length; t > r; r++) this.addPair(i[r]);
    return this
   }

   function o() {
    return l
   }

   function c() {
    return JSON.stringify(o())
   }
   var l = {},
    h = {};
   this.addPair = f, this.addPairs = d, this.serialize = o, this.serializeJSON = c
  }
  var t = {
   validate: /^[a-z_][a-z0-9_]*(?:\[(?:\d*|[a-z0-9_]+)\])*$/i,
   key: /[a-z0-9_]+|(?=\[\])/gi,
   push: /^$/,
   fixed: /^\d+$/,
   named: /^[a-z0-9_]+$/i
  };
  return r.patterns = t, r.serializeObject = function() {
   return new r(i, this).addPairs(this.serializeArray()).serialize()
  }, r.serializeJSON = function() {
   return new r(i, this).addPairs(this.serializeArray()).serializeJSON()
  }, "undefined" != typeof i.fn && (i.fn.serializeObject = r.serializeObject, i.fn.serializeJSON = r.serializeJSON), e.FormSerializer = r, r
 });

 // http://davestewart.io/plugins/jquery/jquery-populate/
 $.fn.populate = function(obj, options) {


  // ------------------------------------------------------------------------------------------
  // JSON conversion function

  // convert 
  function parseJSON(obj, path) {
   // prepare
   path = path || '';

   // iteration (objects / arrays)
   if (obj == undefined) {
    // do nothing
   } else if (obj.constructor == Object) {
    for (var prop in obj) {
     var name = path + (path == '' ? prop : '[' + prop + ']');
     parseJSON(obj[prop], name);
    }
   } else if (obj.constructor == Array) {
    for (var i = 0; i < obj.length; i++) {
     var index = options.useIndices ? i : '';
     index = options.phpNaming ? '[' + index + ']' : index;
     var name = path + index;
     parseJSON(obj[i], name);
    }
   }

   // assignment (values)
   else {
    // if the element name hasn't yet been defined, create it as a single value
    if (arr[path] == undefined) {
     arr[path] = obj;
    }

    // if the element name HAS been defined, but it's a single value, convert to an array and add the new value
    else if (arr[path].constructor != Array) {
     arr[path] = [arr[path], obj];
    }

    // if the element name HAS been defined, and is already an array, push the single value on the end of the stack
    else {
     arr[path].push(obj);
    }
   }

  };


  // ------------------------------------------------------------------------------------------
  // population functions

  function debug(str) {
   if (window.console && console.log) {
    console.log(str);
   }
  }

  function getElementName(name) {
   if (!options.phpNaming) {
    name = name.replace(/\[\]$/, '');
   }
   return name;
  }

  function populateElement(parentElement, name, value) {
   var selector = options.identifier == 'id' ? '#' + name : '[' + options.identifier + '="' + name + '"]';
   var element = jQuery(selector, parentElement);
   value = value.toString();
   value = value == 'null' ? '' : value;
   element.html(value);
  }

  function populateFormElement(form, name, value) {

   // check that the named element exists in the form
   var name = getElementName(name); // handle non-php naming
   var element = form[name];

   // if the form element doesn't exist, check if there is a tag with that id
   if (element == undefined) {
    // look for the element
    element = jQuery('#' + name, form);
    if (element) {
     element.html(value);
     return true;
    }

    // nope, so exit
    if (options.debug) {
     debug('No such element as ' + name);
    }
    return false;
   }

   // debug options				
   if (options.debug) {
    _populate.elements.push(element);
   }

   // now, place any single elements in an array.
   // this is so that the next bit of code (a loop) can treat them the 
   // same as any array-elements passed, ie radiobutton or checkox arrays,
   // and the code will just work

   elements = element.type == undefined && element.length ? element : [element];


   // populate the element correctly

   for (var e = 0; e < elements.length; e++) {

    // grab the element
    var element = elements[e];

    // skip undefined elements or function objects (IE only)
    if (!element || typeof element == 'undefined' || typeof element == 'function') {
     continue;
    }

    // anything else, process
    switch (element.type || element.tagName) {

     case 'radio':
      // use the single value to check the radio button
      element.checked = (element.value != '' && value.toString() == element.value);

     case 'checkbox':
      // depends on the value.
      // if it's an array, perform a sub loop
      // if it's a value, just do the check

      var values = value.constructor == Array ? value : [value];
      for (var j = 0; j < values.length; j++) {
       var valuesj = values[j];
       if (valuesj == true) {
        valuesj = "true";
       }
       element.checked |= element.value == valuesj;
      }

      //element.checked = (element.value != '' && value.toString().toLowerCase() == element.value.toLowerCase());
      break;

     case 'select-multiple':
      var values = value.constructor == Array ? value : [value];
      for (var i = 0; i < element.options.length; i++) {
       for (var j = 0; j < values.length; j++) {
        element.options[i].selected |= element.options[i].value == values[j];
       }
      }
      break;

     case 'select':
     case 'select-one':
      element.value = value.toString() || value;
      break;

     case 'text':
     case 'button':
     case 'textarea':
     case 'submit':
     default:
      value = value == null ? '' : value;
      element.value = value;

    }

   }

  }



  // ------------------------------------------------------------------------------------------
  // options & setup

  // exit if no data object supplied
  if (obj === undefined) {
   return this;
  };

  // options
  var options = jQuery.extend({
    phpNaming: true,
    phpIndices: false,
    resetForm: true,
    identifier: 'id',
    debug: false
   },
   options
  );

  if (options.phpIndices) {
   options.phpNaming = true;
  }

  // ------------------------------------------------------------------------------------------
  // convert hierarchical JSON to flat array

  var arr = [];
  parseJSON(obj);

  if (options.debug) {
   _populate = {
    arr: arr,
    obj: obj,
    elements: []
   }
  }

  // ------------------------------------------------------------------------------------------
  // main process function

  this.each(
   function() {

    // variables
    var tagName = this.tagName.toLowerCase();
    var method = tagName == 'form' ? populateFormElement : populateElement;

    // reset form?
    if (tagName == 'form' && options.resetForm) {
     this.reset();
    }

    // update elements
    for (var i in arr) {
     method(this, i, arr[i]);
    }
   }

  );

  return this;
 };
});
