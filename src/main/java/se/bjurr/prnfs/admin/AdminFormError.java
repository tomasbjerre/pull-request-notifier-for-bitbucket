package se.bjurr.prnfs.admin;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class AdminFormError {
 private String field;
 private String error;

 public AdminFormError() {
 }

 public AdminFormError(String field, String error) {
  this.field = field;
  this.error = error;
 }

 public String getField() {
  return field;
 }

 public String getValue() {
  return error;
 }
}
