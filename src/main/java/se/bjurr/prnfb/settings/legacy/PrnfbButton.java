package se.bjurr.prnfb.settings.legacy;

import se.bjurr.prnfb.settings.legacy.AdminFormValues.BUTTON_VISIBILITY;

@Deprecated
public class PrnfbButton {

 private final String formIdentifier;
 private final String title;
 private final BUTTON_VISIBILITY visibility;

 public PrnfbButton(String title, BUTTON_VISIBILITY visibility, String formIdentifier) {
  this.title = title;
  this.visibility = visibility;
  this.formIdentifier = formIdentifier;
 }

 public String getFormIdentifier() {
  return this.formIdentifier;
 }

 public String getTitle() {
  return this.title;
 }

 public BUTTON_VISIBILITY getVisibility() {
  return this.visibility;
 }

 @Override
 public String toString() {
  return "Title: " + this.title;
 }
}
