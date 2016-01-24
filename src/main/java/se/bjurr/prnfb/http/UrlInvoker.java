package se.bjurr.prnfb.http;

import static com.google.common.base.Charsets.UTF_8;
import static com.google.common.base.Optional.absent;
import static com.google.common.base.Optional.fromNullable;
import static com.google.common.base.Throwables.propagate;
import static com.google.common.collect.Lists.newArrayList;
import static java.util.logging.Level.SEVERE;
import static java.util.logging.Logger.getLogger;
import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.xml.bind.DatatypeConverter.printBase64Binary;
import static se.bjurr.prnfb.http.UrlInvoker.HTTP_METHOD.GET;
import static se.bjurr.prnfb.http.UrlInvoker.HTTP_METHOD.POST;
import static se.bjurr.prnfb.http.UrlInvoker.HTTP_METHOD.PUT;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.logging.Logger;

import javax.net.ssl.SSLContext;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.ProxyAuthenticationStrategy;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.ssl.TrustStrategy;
import org.apache.http.util.EntityUtils;

import se.bjurr.prnfb.settings.Header;
import se.bjurr.prnfb.settings.PrnfbNotification;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Optional;

/**
 * If told to accept all certificates, an unsafe X509 trust manager is used.<br>
 * <br>
 * If setup of the "trust-all" HttpClient fails, a non-configured HttpClient is
 * returned.<br>
 * <br>
 * Inspired by:<br>
 * Philip Dodds (pdodds) https://github.com/pdodds<br>
 * Michael Irwin (mikesir87) https://github.com/Nerdwin15<br>
 */
public class UrlInvoker {

 public enum HTTP_METHOD {
  GET, PUT, POST, DELETE
 }

 private static final Logger logger = getLogger(UrlInvoker.class.getName());
 private String urlParam;
 private HTTP_METHOD method = GET;
 private Optional<String> postContent = absent();
 private final List<Header> headers = newArrayList();
 private Optional<String> proxyUser = absent();
 private Optional<String> proxyPassword = absent();
 private Optional<String> proxyHost = absent();
 private Optional<Integer> proxyPort = absent();
 private ClientKeyStore clientKeyStore;
 private String responseString;
 private boolean shouldAcceptAnyCertificate;

 UrlInvoker() {
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

 public UrlInvoker withClientKeyStore(ClientKeyStore clientKeyStore) {
  this.clientKeyStore = clientKeyStore;
  return this;
 }

 public void invoke() {
  logger.info("Url: \"" + urlParam + "\"");

  HttpRequestBase httpRequestBase = newHttpRequestBase();
  configureUrl(httpRequestBase);
  addHeaders(httpRequestBase);

  HttpClientBuilder builder = HttpClientBuilder.create();
  configureSsl(builder);
  configureProxy(builder);

  responseString = doInvoke(httpRequestBase, builder);
  logger.fine(responseString);
 }

 private void configureUrl(HttpRequestBase httpRequestBase) {
  try {
   httpRequestBase.setURI(new URI(urlParam));
  } catch (URISyntaxException e) {
   propagate(e);
  }
 }

 @VisibleForTesting
 String doInvoke(HttpRequestBase httpRequestBase, HttpClientBuilder builder) {
  CloseableHttpResponse httpResponse = null;
  try {
   httpResponse = builder//
     .build()//
     .execute(httpRequestBase);

   HttpEntity entity = httpResponse.getEntity();
   return EntityUtils.toString(entity, UTF_8);
  } catch (final Exception e) {
   logger.log(SEVERE, "", e);
  } finally {
   try {
    httpResponse.close();
   } catch (IOException e) {
    propagate(e);
   }
  }
  return null;
 }

 @VisibleForTesting
 HttpRequestBase newHttpRequestBase() {
  if (shouldPostContent()) {
   return newHttpEntityEnclosingRequestBase(method, postContent.get());
  }
  return newHttpRequestBase(method);
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
  return (method == POST || method == PUT) && postContent.isPresent();
 }

 public UrlInvoker shouldAcceptAnyCertificate(boolean shouldAcceptAnyCertificate) {
  this.shouldAcceptAnyCertificate = shouldAcceptAnyCertificate;
  return this;
 }

 public List<Header> getHeaders() {
  return headers;
 }

 public String getResponseString() {
  return responseString;
 }

 @VisibleForTesting
 public boolean shouldUseProxy() {
  return getProxyHost().isPresent() && getProxyPort().isPresent() && getProxyPort().get() > 0;
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

 public Optional<Integer> getProxyPort() {
  return proxyPort;
 }

 public UrlInvoker withProxyPort(Integer proxyPort) {
  this.proxyPort = fromNullable(proxyPort);
  return this;
 }

 public InputStream getResponseStringStream() {
  return new ByteArrayInputStream(getResponseString().getBytes(UTF_8));
 }

 public void setResponseString(String responseString) {
  this.responseString = responseString;
 }

 public UrlInvoker appendBasicAuth(PrnfbNotification notification) {
  if (notification.getUser().isPresent() && notification.getPassword().isPresent()) {
   final String userpass = notification.getUser().get() + ":" + notification.getPassword().get();
   final String basicAuth = "Basic " + new String(printBase64Binary(userpass.getBytes(UTF_8)));
   withHeader(AUTHORIZATION, basicAuth);
  }
  return this;
 }

 @VisibleForTesting
 HttpClientBuilder configureSsl(HttpClientBuilder builder) {
  if (shouldUseSsl()) {
   try {
    SSLContext s = newSslContext();
    SSLConnectionSocketFactory sslConnSocketFactory = new SSLConnectionSocketFactory(s);
    builder.setSSLSocketFactory(sslConnSocketFactory);

    Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory> create()
      .register("https", sslConnSocketFactory).build();

    HttpClientConnectionManager ccm = new BasicHttpClientConnectionManager(registry);

    builder.setConnectionManager(ccm);
   } catch (Exception e) {
    propagate(e);
   }
  }
  return builder;
 }

 private boolean shouldUseSsl() {
  return urlParam.startsWith("https");
 }

 @VisibleForTesting
 HttpClientBuilder configureProxy(HttpClientBuilder builder) {
  if (!shouldUseProxy()) {
   return builder;
  }

  if (proxyUser.isPresent() && proxyPassword.isPresent()) {
   String username = proxyUser.get();
   String password = proxyPassword.get();
   UsernamePasswordCredentials creds = new UsernamePasswordCredentials(username, password);
   CredentialsProvider credsProvider = new BasicCredentialsProvider();
   credsProvider.setCredentials(new AuthScope(proxyHost.get(), proxyPort.get()), creds);
   builder.setDefaultCredentialsProvider(credsProvider);
  }

  builder.useSystemProperties();
  builder.setProxy(new HttpHost(proxyHost.get(), proxyPort.get()));
  builder.setProxyAuthenticationStrategy(new ProxyAuthenticationStrategy());
  return builder;
 }

 @VisibleForTesting
 public boolean shouldAcceptAnyCertificate() {
  return shouldAcceptAnyCertificate;
 }

 private SSLContext newSslContext() throws Exception {
  SSLContextBuilder sslContextBuilder = SSLContexts.custom();
  if (shouldAcceptAnyCertificate) {
   doAcceptAnyCertificate(sslContextBuilder);
   if (clientKeyStore.getKeyStore().isPresent()) {
    sslContextBuilder.loadKeyMaterial(clientKeyStore.getKeyStore().get(), clientKeyStore.getPassword());
   }
  }

  return sslContextBuilder.build();
 }

 private SSLContextBuilder doAcceptAnyCertificate(SSLContextBuilder customContext) throws Exception {
  TrustStrategy easyStrategy = new TrustStrategy() {
   @Override
   public boolean isTrusted(X509Certificate[] chain, String authType) {
    return true;
   }
  };
  customContext = customContext.loadTrustMaterial(null, easyStrategy);

  return customContext;
 }

 private void addHeaders(HttpRequestBase httpRequestBase) {
  for (Header header : headers) {
   logger.fine("header: \"" + header.getName() + "\" value: \"" + header.getValue() + "\"");
   httpRequestBase.addHeader(header.getName(), getHeaderValue(header));
  }
 }

 @VisibleForTesting
 HttpRequestBase newHttpRequestBase(HTTP_METHOD method) {
  return new HttpRequestBase() {
   @Override
   public String getMethod() {
    return method.name();
   }
  };
 }

 @VisibleForTesting
 HttpEntityEnclosingRequestBase newHttpEntityEnclosingRequestBase(HTTP_METHOD method, String entity) {
  HttpEntityEnclosingRequestBase entityEnclosing = new HttpEntityEnclosingRequestBase() {
   @Override
   public String getMethod() {
    return method.name();
   }
  };
  if (entity != null) {
   entityEnclosing.setEntity(new ByteArrayEntity(entity.getBytes()));
  }
  return entityEnclosing;
 }

 public ClientKeyStore getClientKeyStore() {
  return clientKeyStore;
 }
}
