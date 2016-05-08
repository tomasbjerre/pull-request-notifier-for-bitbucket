package se.bjurr.prnfb.settings.legacy;

import java.util.ArrayList;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@Deprecated
public final class AdminFormValues extends ArrayList<Map<String, String>> {
 public enum BUTTON_VISIBILITY {
  ADMIN, EVERYONE, NONE, SYSTEM_ADMIN
 }

 public enum FIELDS {
  accept_any_certificate, //
  admin_allowed, //
  button_title, //
  button_visibility, //
  events, //
  filter_regexp, //
  filter_string, //
  FORM_IDENTIFIER, //
  FORM_TYPE, //
  header_name, //
  header_value, //
  injection_url, //
  injection_url_regexp, //
  key_store, //
  key_store_password, //
  key_store_type, //
  method, //
  name, //
  password, //
  post_content, //
  proxy_password, //
  proxy_port, //
  proxy_server, //
  proxy_user, //
  trigger_if_isconflicting, //
  trigger_ignore_state, //
  url, //
  user, //
  user_allowed;
 }

 public enum FORM_TYPE {
  BUTTON_CONFIG_FORM, GLOBAL_SETTINGS, TRIGGER_CONFIG_FORM
 }

 public enum TRIGGER_IF_MERGE {
  ALWAYS, CONFLICTING, NOT_CONFLICTING
 }

 public static final String DEFAULT_NAME = "Unnamed trigger";;

 public static final String NAME = "name";;

 public static final String VALUE = "value";;

 private static final long serialVersionUID = 9084184120202816120L;
}
