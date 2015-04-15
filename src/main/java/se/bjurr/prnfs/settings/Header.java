package se.bjurr.prnfs.settings;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.emptyToNull;
import static com.google.common.base.Strings.nullToEmpty;

public class Header {

 private final String name;
 private final String value;

 public Header(String name, String value) {
  this.name = checkNotNull(emptyToNull(nullToEmpty(name).trim()));
  this.value = checkNotNull(emptyToNull(nullToEmpty(value).trim()));
 }

 public String getName() {
  return name;
 }

 public String getValue() {
  return value;
 }
}