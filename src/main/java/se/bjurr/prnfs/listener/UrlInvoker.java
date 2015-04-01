package se.bjurr.prnfs.listener;

import static com.google.common.base.Charsets.UTF_8;
import static com.google.common.base.Joiner.on;
import static com.google.common.io.CharStreams.readLines;
import static java.lang.Boolean.TRUE;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.bind.DatatypeConverter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;
import com.google.common.io.Closeables;

public class UrlInvoker {
 private static final Logger logger = LoggerFactory.getLogger(UrlInvoker.class);

 public void ivoke(String urlParam, Optional<String> user, Optional<String> password) {
  InputStreamReader ir = null;
  try {
   logger.info("Url: \"" + urlParam + "\" user: \"" + user.or("") + "\" password: \"" + password.or("") + "\"");
   final URL url = new URL(urlParam);
   final URLConnection uc = url.openConnection();
   if (user.isPresent() && password.isPresent()) {
    final String userpass = user.get() + ":" + password.get();
    final String basicAuth = "Basic " + new String(DatatypeConverter.printBase64Binary(userpass.getBytes("UTF-8")));
    uc.setRequestProperty("Authorization", basicAuth);
   }
   ir = new InputStreamReader(uc.getInputStream(), UTF_8);
   logger.debug(on("\n").join(readLines(ir)));
  } catch (final Exception e) {
   try {
    Closeables.close(ir, TRUE);
   } catch (final IOException e1) {
   }
   logger.error("", e);
  }
 }
}
