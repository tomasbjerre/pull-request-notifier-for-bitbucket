package se.bjurr.prnfs.settings;

import se.bjurr.prnfs.admin.AdminFormValues.BUTTON_VISIBILITY;

public class PrnfsButton {

 private final String title;
 private final BUTTON_VISIBILITY visibility;
 private final String formIdentifier;

 public PrnfsButton(String title, BUTTON_VISIBILITY visibility, String formIdentifier) {
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
}
