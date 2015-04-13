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

 public enum FIELDS {
  user, password, events, FORM_IDENTIFIER, url, filter_string, filter_regexp, method, post_content
 }
}
