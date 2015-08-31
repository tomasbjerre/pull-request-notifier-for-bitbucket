package se.bjurr.prnfs.listener;

import static com.google.common.base.Charsets.UTF_8;
import static com.google.common.base.Joiner.on;
import static com.google.common.base.Optional.absent;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.io.CharStreams.readLines;
import static java.lang.Boolean.TRUE;
import static java.util.logging.Level.SEVERE;
import static java.util.logging.Logger.getLogger;
import static se.bjurr.prnfs.listener.UrlInvoker.HTTP_METHOD.GET;

import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.URL;
import java.util.List;
import java.util.logging.Logger;

import se.bjurr.prnfs.settings.Header;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Optional;
import com.google.common.io.Closeables;

public class UrlInvoker {

 public enum HTTP_METHOD {
  GET, PUT, POST, DELETE
 }

 private static final Logger logger = getLogger(UrlInvoker.class.getName());
 private String urlParam;
 private HTTP_METHOD method = GET;
 private Optional<String> postContent;
 private final List<Header> headers = newArrayList();
 private Optional<String> proxyUser = absent();
 private Optional<String> proxyPassword = absent();
 private Optional<String> proxyHost = absent();
 private Integer proxyPort;
 private String responseString;

 private UrlInvoker() {
 }

 public static UrlInvoker urlInvoker() {
  return new UrlInvoker();
 }

 public UrlInvoker withHeader(String name, String value) {
  headers.add(new Header(name, value));
  return this;
 }

 public UrlInvoker withMethod(HTTP_METHOD method) {
  this.method = method;
  return this;
 }

 public UrlInvoker withPostContent(Optional<String> postContent) {
  this.postContent = postContent;
  return this;
 }

 public UrlInvoker withUrlParam(String urlParam) {
  this.urlParam = urlParam.replaceAll("\\s", "%20");
  return this;
 }

 public void invoke() {
  InputStreamReader ir = null;
  DataOutputStream wr = null;
  try {
   logger.info("Url: \"" + urlParam + "\"");
   final URL url = new URL(urlParam);
   HttpURLConnection uc = null;
   if (shouldUseProxy()) {
    Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(getProxyHost().get(), getProxyPort()));
    if (shouldAuthenticateProxy()) {
     Authenticator authenticator = new Authenticator() {
      @Override
      public PasswordAuthentication getPasswordAuthentication() {
       return (new PasswordAuthentication(getProxyUser().get(), getProxyPassword().get().toCharArray()));
      }
     };
     Authenticator.setDefault(authenticator);
    }
    uc = (HttpURLConnection) url.openConnection(proxy);
   } else {
    uc = (HttpURLConnection) url.openConnection();
   }
   uc.setRequestMethod(method.name());
   for (Header header : headers) {
    logger.info("header: \"" + header.getName() + "\" value: \"" + header.getValue() + "\"");
    uc.setRequestProperty(header.getName(), getHeaderValue(header));
   }
   uc.setDoOutput(true);
   if (shouldPostContent()) {
    logger.fine(method + " >\n" + postContent.get());
    uc.setDoInput(true);
    uc.setRequestProperty("Content-Length", postContent.get().length() + "");
    wr = new DataOutputStream(uc.getOutputStream());
    wr.write(postContent.get().getBytes(UTF_8));
   }
   ir = new InputStreamReader(uc.getInputStream(), UTF_8);
   responseString = on("\n").join(readLines(ir));
   logger.fine(responseString);
  } catch (final Exception e) {
   logger.log(SEVERE, "", e);
  } finally {
   try {
    Closeables.close(ir, TRUE);
    if (wr != null) {
     Closeables.close(wr, TRUE);
    }
   } catch (final IOException e1) {
   }
  }
 }

 @VisibleForTesting
 public boolean shouldAuthenticateProxy() {
  return getProxyUser().isPresent() && getProxyPassword().isPresent();
 }

 @VisibleForTesting
 public static String getHeaderValue(Header header) {
  return header.getValue();
 }

 public HTTP_METHOD getMethod() {
  return method;
 }

 public Optional<String> getPostContent() {
  return postContent;
 }

 public String getUrlParam() {
  return urlParam;
 }

 @VisibleForTesting
 public boolean shouldPostContent() {
  return (method.equals("POST") || method.equals("PUT")) && postContent.isPresent();
 }

 public List<Header> getHeaders() {
  return headers;
 }

 public String getResponseString() {
  return responseString;
 }

 @VisibleForTesting
 public boolean shouldUseProxy() {
  return getProxyHost().isPresent() && getProxyPort() > 0;
 }

 public Optional<String> getProxyUser() {
  return proxyUser;
 }

 public UrlInvoker withProxyUser(Optional<String> proxyUser) {
  this.proxyUser = proxyUser;
  return this;
 }

 public Optional<String> getProxyPassword() {
  return proxyPassword;
 }

 public UrlInvoker withProxyPassword(Optional<String> proxyPassword) {
  this.proxyPassword = proxyPassword;
  return this;
 }

 public Optional<String> getProxyHost() {
  return proxyHost;
 }

 public UrlInvoker withProxyServer(Optional<String> proxyHost) {
  this.proxyHost = proxyHost;
  return this;
 }

 public Integer getProxyPort() {
  return proxyPort;
 }

 public UrlInvoker withProxyPort(Integer proxyPort) {
  this.proxyPort = proxyPort;
  return this;
 }

 public InputStream getResponseStringStream() {
  return new ByteArrayInputStream(getResponseString().getBytes(UTF_8));
 }

 public void setResponseString(String responseString) {
  this.responseString = responseString;
 }
}
