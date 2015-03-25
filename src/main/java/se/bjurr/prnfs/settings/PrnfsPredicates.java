package se.bjurr.prnfs.settings;

import static se.bjurr.prnfs.admin.AdminFormValues.NAME;

import java.util.Map;

import com.google.common.base.Predicate;

public class PrnfsPredicates {
 public static Predicate<Map<String, String>> predicate(final String name) {
  return new Predicate<Map<String, String>>() {
   @Override
   public boolean apply(Map<String, String> input) {
    return input.get(NAME).equals(name);
   }
  };
 }

 private PrnfsPredicates() {
 }

}
