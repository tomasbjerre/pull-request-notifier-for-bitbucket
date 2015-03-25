package se.bjurr.prnfs.admin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.annotations.VisibleForTesting;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public final class AdminFormValues extends ArrayList<Map<String, String>> {
 public static final String NAME = "name";
 private static final long serialVersionUID = 9084184120202816120L;
 public static final String VALUE = "value";

 @VisibleForTesting
 public void setSetting(String name, String value) {
  Map<String, String> map = new HashMap<String, String>();
  map.put(NAME, name);
  map.put(VALUE, value);
  add(map);
 }
}
