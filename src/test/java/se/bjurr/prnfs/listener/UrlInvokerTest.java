package se.bjurr.prnfs.listener;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Optional;

public class UrlInvokerTest {
 private URLConnection uc;

 @Before
 public void before() throws MalformedURLException {
  uc = new URLConnection(new URL("http://bjurr.se/")) {
   @Override
   public void connect() throws IOException {

   }
  };
 }

 @Test
 public void testThatAuthenticationRequestPropertyIsAddedIfPasswordAndUserIsSet() throws UnsupportedEncodingException {
  new UrlInvoker().setAuthorization(uc, Optional.of("theuser"), Optional.of("thepassword"));
  assertEquals("Basic dGhldXNlcjp0aGVwYXNzd29yZA==", uc.getRequestProperty(AUTHORIZATION));
  assertEquals(1, uc.getRequestProperties().size());
 }

 @Test
 public void testThatAuthenticationRequestPropertyIsNotAddedIfPasswordIsMissing() throws UnsupportedEncodingException {
  new UrlInvoker().setAuthorization(uc, Optional.of("theuser"), Optional.<String> absent());
  assertNull(uc.getRequestProperty(AUTHORIZATION));
  assertEquals(0, uc.getHeaderFields().size());
 }

 @Test
 public void testThatAuthenticationRequestPropertyIsNotAddedIfUserIsMissing() throws UnsupportedEncodingException {
  new UrlInvoker().setAuthorization(uc, Optional.<String> absent(), Optional.of("thepassword"));
  assertEquals(null, uc.getRequestProperty(AUTHORIZATION));
  assertEquals(0, uc.getHeaderFields().size());
 }

}
