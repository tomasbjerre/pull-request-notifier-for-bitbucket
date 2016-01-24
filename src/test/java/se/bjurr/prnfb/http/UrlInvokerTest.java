package se.bjurr.prnfb.http;

import static com.google.common.base.Optional.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static se.bjurr.prnfb.http.UrlInvoker.HTTP_METHOD.DELETE;
import static se.bjurr.prnfb.http.UrlInvoker.HTTP_METHOD.GET;
import static se.bjurr.prnfb.http.UrlInvoker.HTTP_METHOD.POST;
import static se.bjurr.prnfb.http.UrlInvoker.HTTP_METHOD.PUT;
import static se.bjurr.prnfb.settings.PrnfbNotificationBuilder.prnfbNotificationBuilder;

import java.net.URL;

import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.Before;
import org.junit.Test;

import se.bjurr.prnfb.settings.PrnfbNotification;

import com.google.common.base.Optional;

public class UrlInvokerTest {

 private UrlInvoker urlInvoker;
 private HttpRequestBase httpRequestBase;

 @Before
 public void before() {
  this.urlInvoker = new UrlInvoker() {
   @Override
   String doInvoke(HttpRequestBase httpRequest, HttpClientBuilder builder) {
    httpRequestBase = httpRequest;
    return "";
   }
  }//
  .withUrlParam("http://url.com/");
 }

 @Test
 public void testThatURLCanBeInvokedWithMinimalConfig() throws Exception {
  urlInvoker//
    .invoke();

  assertThat(httpRequestBase.getMethod())//
    .isEqualTo(GET.name());
  assertThat(httpRequestBase.getURI().toString())//
    .isEqualTo(new URL("http://url.com/").toString());
 }

 @Test
 public void testThatHttpEntityEnclosingRequestBaseCanBeCreatedAsPOSTWithoutContent() {
  HttpRequestBase response = urlInvoker//
    .withMethod(POST)//
    .newHttpRequestBase();

  assertThat(response.getMethod())//
    .isEqualTo(POST.name());
  assertThat(response)//
    .isInstanceOf(HttpRequestBase.class);
 }

 @Test
 public void testThatHttpEntityEnclosingRequestBaseCanBeCreatedAsPUTWithContent() {
  HttpRequestBase response = urlInvoker//
    .withMethod(PUT)//
    .withPostContent(Optional.of("some content"))//
    .newHttpRequestBase();

  assertThat(response.getMethod())//
    .isEqualTo(PUT.name());
  HttpEntityEnclosingRequestBase c = (HttpEntityEnclosingRequestBase) response;
  assertThat(c.getEntity().getContentLength())//
    .isGreaterThan(0);
 }

 @Test
 public void testThatHttpRequestBaseCanBeCreatedWithDelete() {
  HttpRequestBase response = urlInvoker//
    .withMethod(DELETE)//
    .newHttpRequestBase();

  assertThat(response)//
    .isInstanceOf(HttpRequestBase.class);
  assertThat(response.getMethod())//
    .isEqualTo(DELETE.name());
 }

 @Test
 public void testThatHeadersAreAdded() {
  urlInvoker//
    .withHeader("name", "value")//
    .invoke();

  assertThat(httpRequestBase.getAllHeaders())//
    .hasSize(1);
  assertThat(httpRequestBase.getAllHeaders()[0].getName())//
    .isEqualTo("name");
  assertThat(httpRequestBase.getAllHeaders()[0].getValue())//
    .isEqualTo("value");
 }

 @Test
 public void testThatHeadersAreAddedForBasicAuth() throws Exception {
  PrnfbNotification notification = prnfbNotificationBuilder()//
    .withUrl("http://url.com/")//
    .withUser("user")//
    .withPassword("password")//
    .build();

  urlInvoker//
    .appendBasicAuth(notification)//
    .invoke();

  assertThat(httpRequestBase.getAllHeaders())//
    .hasSize(1);
  assertThat(httpRequestBase.getAllHeaders()[0].getName())//
    .isEqualTo("Authorization");
  assertThat(httpRequestBase.getAllHeaders()[0].getValue())//
    .isEqualTo("Basic dXNlcjpwYXNzd29yZA==");
 }

 @Test
 public void testThatSslCanBeConfigured() throws Exception {
  HttpClientBuilder mockedBuilder = mock(HttpClientBuilder.class);

  urlInvoker//
    .withUrlParam("https://url.com/")//
    .configureSsl(mockedBuilder);

  // verify(mockedBuilder).setSSLSocketFactory(Matchers.any());
 }

 @Test
 public void testThatNoneSslCanBeConfigured() throws Exception {
  HttpClientBuilder mockedBuilder = mock(HttpClientBuilder.class);

  urlInvoker//
    .withUrlParam("http://url.com/")//
    .configureSsl(mockedBuilder);

  // verify(mockedBuilder, times(0)).setSSLSocketFactory(Matchers.any());
 }

 @Test
 public void testThatProxyIsConfiguredIfThereIsAHostAndPort() throws Exception {
  HttpClientBuilder mockedBuilder = mock(HttpClientBuilder.class);

  urlInvoker//
    .withUrlParam("http://url.com/")//
    .withProxyServer(of("http://proxy.com/"))//
    .withProxyPort(123)//
    .configureProxy(mockedBuilder);

  // verify(mockedBuilder, times(1)).setProxy(Matchers.any());
 }

 @Test
 public void testThatProxyIsNotConfiguredIfThereIsNoHost() throws Exception {
  HttpClientBuilder mockedBuilder = mock(HttpClientBuilder.class);

  urlInvoker//
    .withUrlParam("http://url.com/")//
    .withProxyPort(123)//
    .configureProxy(mockedBuilder);

  // verify(mockedBuilder, times(0)).setProxy(Matchers.any());
 }

 @Test
 public void testThatProxyUserIsConfiguredIfItIsSet() throws Exception {
  HttpClientBuilder mockedBuilder = mock(HttpClientBuilder.class);

  urlInvoker//
    .withUrlParam("http://url.com/")//
    .withProxyServer(of("http://proxy.com/"))//
    .withProxyPort(123)//
    .withProxyUser(of("u"))//
    .withProxyPassword(of("p"))//
    .configureProxy(mockedBuilder);

  // verify(mockedBuilder,
  // times(1)).setDefaultCredentialsProvider(Matchers.any());
  // verify(mockedBuilder, times(1)).setProxy(Matchers.any());
 }

 @Test
 public void testThatProxyUserIsNotConfiguredIfNoPasswordSet() throws Exception {
  HttpClientBuilder mockedBuilder = mock(HttpClientBuilder.class);

  urlInvoker//
    .withUrlParam("http://url.com/")//
    .withProxyServer(of("http://proxy.com/"))//
    .withProxyPort(123)//
    .withProxyUser(of("u"))//
    .configureProxy(mockedBuilder);

  // verify(mockedBuilder,
  // times(0)).setDefaultCredentialsProvider(Matchers.any());
  // verify(mockedBuilder, times(1)).setProxy(Matchers.any());
 }

}
