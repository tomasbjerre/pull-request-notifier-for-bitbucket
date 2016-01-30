package se.bjurr.prnfb.settings;

import se.bjurr.prnfb.admin.AdminFormValues.BUTTON_VISIBILITY;

public class PrnfbButton {

 private final String title;
 private final BUTTON_VISIBILITY visibility;
 private final String formIdentifier;

 public PrnfbButton(String title, BUTTON_VISIBILITY visibility, String formIdentifier) {
  this.title = title;
  this.visibility = visibility;
  this.formIdentifier = formIdentifier;
 }

 public String getTitle() {
  return title;
 }

 public BUTTON_VISIBILITY getVisibility() {
  return visibility;
 }

 public String getFormIdentifier() {
  return formIdentifier;
 }

 @Override
 public String toString() {
  return "Title: " + title;
 }
}
