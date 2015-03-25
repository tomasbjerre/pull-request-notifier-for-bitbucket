package se.bjurr.prnfs.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.event.api.EventListener;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.stash.event.pull.PullRequestEvent;

public class PrnfsPullRequestEventListener {
 private static final Logger logger = LoggerFactory.getLogger(PrnfsPullRequestEventListener.class);

 public PrnfsPullRequestEventListener(ApplicationProperties applicationProperties) {
 }

 @EventListener
 public void anEvent(PullRequestEvent o) {
  logger.info(o.getAction().name());
  logger.info(o.getPullRequest().getTitle());
 }
}