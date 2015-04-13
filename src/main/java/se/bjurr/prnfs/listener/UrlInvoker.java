package se.bjurr.prnfs.listener;

import static com.google.common.base.Charsets.UTF_8;
import static com.google.common.base.Joiner.on;
import static com.google.common.io.CharStreams.readLines;
import static java.lang.Boolean.TRUE;
import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.xml.bind.DatatypeConverter.printBase64Binary;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Optional;
import com.google.common.io.Closeables;

public class UrlInvoker {
 private static final Logger logger = LoggerFactory.getLogger(UrlInvoker.class);

 public void ivoke(String urlParam, Optional<String> user, Optional<String> password, String method,
   Optional<String> postContent) {
  InputStreamReader ir = null;
  DataOutputStream wr = null;
  try {
   logger.info("Url: \"" + urlParam + "\"");
   final URL url = new URL(urlParam);
   final HttpURLConnection uc = (HttpURLConnection) url.openConnection();
   uc.setRequestMethod(method);
   setAuthorization(uc, user, password);
   uc.setDoOutput(true);
   if (shouldPostContent(method, postContent)) {
    logger.debug("POST >\n" + postContent.get());
    uc.setDoInput(true);
    uc.setRequestProperty("Content-Length", postContent.get().length() + "");
    wr = new DataOutputStream(uc.getOutputStream());
    wr.write(postContent.get().getBytes(UTF_8));
   }
   ir = new InputStreamReader(uc.getInputStream(), UTF_8);
   logger.debug(on("\n").join(readLines(ir)));
  } catch (final Exception e) {
   try {
    Closeables.close(ir, TRUE);
    if (wr != null) {
     Closeables.close(wr, TRUE);
    }
   } catch (final IOException e1) {
   }
   logger.error("", e);
  }
 }

 @VisibleForTesting
 void setAuthorization(URLConnection uc, Optional<String> user, Optional<String> password)
   throws UnsupportedEncodingException {
  if (shouldUseBasicAuth(user, password)) {
   logger.info("user: \"" + user.or("") + "\" password: \"" + password.or("") + "\"");
   final String userpass = user.get() + ":" + password.get();
   final String basicAuth = "Basic " + new String(printBase64Binary(userpass.getBytes(UTF_8)));
   uc.setRequestProperty(AUTHORIZATION, basicAuth);
  }

 }

 @VisibleForTesting
 public static boolean shouldPostContent(String method, Optional<String> postContent) {
  return method.equals("POST") && postContent.isPresent();
 }

 @VisibleForTesting
 public static boolean shouldUseBasicAuth(Optional<String> user, Optional<String> password) {
  return user.isPresent() && password.isPresent();
 }
}
