package se.bjurr.prnfb.http;

import java.net.URI;

public class HttpResponse {
 private final String content;

 private final int status;

 private final URI uri;

 public HttpResponse(URI uri, int status, String content) {
  this.uri = uri;
  this.status = status;
  this.content = content;
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
  HttpResponse other = (HttpResponse) obj;
  if (this.content == null) {
   if (other.content != null) {
    return false;
   }
  } else if (!this.content.equals(other.content)) {
   return false;
  }
  if (this.status != other.status) {
   return false;
  }
  if (this.uri == null) {
   if (other.uri != null) {
    return false;
   }
  } else if (!this.uri.equals(other.uri)) {
   return false;
  }
  return true;
 }

 public String getContent() {
  return this.content;
 }

 public int getStatus() {
  return this.status;
 }

 public URI getUri() {
  return this.uri;
 }

 @Override
 public int hashCode() {
  final int prime = 31;
  int result = 1;
  result = prime * result + ((this.content == null) ? 0 : this.content.hashCode());
  result = prime * result + this.status;
  result = prime * result + ((this.uri == null) ? 0 : this.uri.hashCode());
  return result;
 }

 @Override
 public String toString() {
  return "HttpResponse [content=" + this.content + ", status=" + this.status + ", uri=" + this.uri + "]";
 }
}
