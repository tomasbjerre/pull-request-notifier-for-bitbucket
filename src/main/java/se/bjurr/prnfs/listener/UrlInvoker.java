package se.bjurr.prnfs.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;

public class UrlInvoker {
 private static final Logger logger = LoggerFactory.getLogger(UrlInvoker.class);

 public void ivoke(String url, Optional<String> user, Optional<String> password) {
  logger.info("Url: \"" + url + "\" user: \"" + user.or("") + "\" password: \"" + password.or("") + "\"");
 }
}
