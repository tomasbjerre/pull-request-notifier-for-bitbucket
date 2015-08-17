package se.bjurr.prnfs.admin;

import java.util.ArrayList;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public final class AdminFormValues extends ArrayList<Map<String, String>> {
 public static final String NAME = "name";
 private static final long serialVersionUID = 9084184120202816120L;
 public static final String VALUE = "value";

 public static final String DEFAULT_NAME = "Unnamed trigger";

 public enum FORM_TYPE {
  BUTTON_CONFIG_FORM, TRIGGER_CONFIG_FORM, GLOBAL_SETTINGS
 };

 public enum BUTTON_VISIBILITY {
  NONE, SYSTEM_ADMIN, ADMIN, EVERYONE
 };

 public enum FIELDS {
  user, password, events, FORM_IDENTIFIER, FORM_TYPE, url, filter_string, filter_regexp, method, post_content, proxy_user, proxy_password, proxy_server, proxy_port, header_name, header_value, name, button_title, button_visibility, admin_allowed, user_allowed
 }
}
