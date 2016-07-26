package se.bjurr.prnfb.presentation.dto;

import static javax.xml.bind.annotation.XmlAccessType.FIELD;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.base.Optional;

import se.bjurr.prnfb.http.HttpResponse;

@XmlRootElement
@XmlAccessorType(FIELD)
public class TriggerResultDTO implements Comparable<TriggerResultDTO> {

 private String name;
 private Map<String, Map<String, Object>> results;

 @Override
 public int compareTo(TriggerResultDTO o) {
  return this.name.compareTo(o.name);
 }

 @Override
 public boolean equals(Object obj) {
  if (this == obj) {
   return true;
  }
  if (obj == null) {
   return false;
  }
  if (getClass() != obj.getClass()) {
   return false;
  }
  TriggerResultDTO other = (TriggerResultDTO) obj;
  if (this.results == null) {
   if (other.results != null) {
    return false;
   }
  } else if (!this.results.equals(other.results)) {
   return false;
  }
  if (this.name == null) {
   if (other.name != null) {
    return false;
   }
  } else if (!this.name.equals(other.name)) {
   return false;
  }
  return true;
 }

 public String getName() {
  return this.name;
 }

 public Optional<Map<String, Map<String, Object>>> getResults() {
  return Optional.fromNullable(this.results);
 }

 @Override
 public int hashCode() {
  final int prime = 31;
  int result = 1;
  result = prime * result + ((this.results == null) ? 0 : this.results.hashCode());
  result = prime * result + ((this.name == null) ? 0 : this.name.hashCode());
  return result;
 }

 public void setName(String name) {
  this.name = name;
 }

 public void setResults(Map<String, HttpResponse> results) {
  this.results = new HashMap<String, Map<String, Object>>();
  for(String key : results.keySet()) {
	  Map<String, Object> data = new HashMap<String, Object>();
	  data.put("status", results.get(key).getStatus());
	  data.put("response", results.get(key).getContent());
	  this.results.put(key, data);
  }
 }

 @Override
 public String toString() {
  return "TriggerResultDTO [name=" + this.name + ", results=" + this.results + "]";
 }

}
