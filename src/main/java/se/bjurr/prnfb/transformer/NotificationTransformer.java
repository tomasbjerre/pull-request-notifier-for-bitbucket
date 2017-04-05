package se.bjurr.prnfb.transformer;

import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.collect.Lists.newArrayList;
import static se.bjurr.prnfb.settings.PrnfbNotificationBuilder.prnfbNotificationBuilder;

import java.util.List;

import com.atlassian.bitbucket.pull.PullRequestState;

import se.bjurr.prnfb.listener.PrnfbPullRequestAction;
import se.bjurr.prnfb.presentation.dto.HeaderDTO;
import se.bjurr.prnfb.presentation.dto.NotificationDTO;
import se.bjurr.prnfb.settings.PrnfbHeader;
import se.bjurr.prnfb.settings.PrnfbNotification;
import se.bjurr.prnfb.settings.ValidationException;

public class NotificationTransformer {

  public static NotificationDTO toNotificationDto(PrnfbNotification from) {
    NotificationDTO to = new NotificationDTO();
    to.setProjectKey(from.getProjectKey().orNull());
    to.setRepositorySlug(from.getRepositorySlug().orNull());
    to.setFilterRegexp(from.getFilterRegexp().orNull());
    to.setFilterString(from.getFilterString().orNull());
    to.setInjectionUrl(from.getInjectionUrl().orNull());
    to.setInjectionUrlRegexp(from.getInjectionUrlRegexp().orNull());
    to.setMethod(from.getMethod());
    to.setName(from.getName());
    to.setHeaders(toHeaders(from.getHeaders()));
    to.setPassword(from.getPassword().orNull());
    to.setPostContent(from.getPostContent().orNull());
    to.setPostContentEncoding(from.getPostContentEncoding());
    to.setProxyPassword(from.getProxyPassword().orNull());
    to.setProxyPort(from.getProxyPort());
    to.setProxyServer(from.getProxyServer().orNull());
    to.setProxySchema(from.getProxySchema().orNull());
    to.setProxyUser(from.getProxyUser().orNull());
    to.setTriggerIfCanMerge(from.getTriggerIfCanMerge());
    to.setTriggerIgnoreStateList(toPullRequestStateStrings(from.getTriggerIgnoreStateList()));
    to.setTriggers(toStrings(from.getTriggers()));
    to.setUrl(from.getUrl());
    to.setUser(from.getUser().orNull());
    to.setUuid(from.getUuid());
    return to;
  }

  public static List<NotificationDTO> toNotificationDtoList(Iterable<PrnfbNotification> from) {
    List<NotificationDTO> to = newArrayList();
    if (from != null) {
      for (PrnfbNotification n : from) {
        to.add(toNotificationDto(n));
      }
    }
    return to;
  }

  public static PrnfbNotification toPrnfbNotification(NotificationDTO from)
      throws ValidationException {
    return prnfbNotificationBuilder() //
        .withFilterRegexp(from.getFilterRegexp()) //
        .withFilterString(from.getFilterString()) //
        .setHeaders(toHeaders(from)) //
        .withInjectionUrl(from.getInjectionUrl()) //
        .withInjectionUrlRegexp(from.getInjectionUrlRegexp()) //
        .withMethod(from.getMethod()) //
        .withName(from.getName()) //
        .withPassword(from.getPassword()) //
        .withPostContent(from.getPostContent()) //
        .withPostContentEncoding(from.getPostContentEncoding()) //
        .withProxyPassword(from.getProxyPassword()) //
        .withProxyPort(from.getProxyPort()) //
        .withProxyServer(from.getProxyServer()) //
        .withProxySchema(from.getProxySchema()) //
        .withProxyUser(from.getProxyUser()) //
        .setTriggers(toPrnfbPullRequestActions(from.getTriggers())) //
        .withTriggerIfCanMerge(from.getTriggerIfCanMerge()) //
        .setTriggerIgnoreState(toPullRequestStates(from.getTriggerIgnoreStateList())) //
        .withUrl(from.getUrl()) //
        .withUser(from.getUser()) //
        .withUuid(from.getUuid()) //
        .withRepositorySlug(from.getRepositorySlug().orNull()) //
        .withProjectKey(from.getProjectKey().orNull()) //
        .build();
  }

  private static List<HeaderDTO> toHeaders(List<PrnfbHeader> headers) {
    List<HeaderDTO> to = newArrayList();
    if (headers != null) {
      for (PrnfbHeader h : headers) {
        HeaderDTO t = new HeaderDTO();
        t.setName(h.getName());
        t.setValue(h.getValue());
        to.add(t);
      }
    }
    return to;
  }

  private static List<PrnfbHeader> toHeaders(NotificationDTO from) {
    List<PrnfbHeader> to = newArrayList();
    if (from.getHeaders() != null) {
      for (HeaderDTO headerDto : from.getHeaders()) {
        if (!isNullOrEmpty(headerDto.getName()) && !isNullOrEmpty(headerDto.getValue())) {
          to.add(new PrnfbHeader(headerDto.getName(), headerDto.getValue()));
        }
      }
    }
    return to;
  }

  private static List<PrnfbPullRequestAction> toPrnfbPullRequestActions(List<String> strings) {
    List<PrnfbPullRequestAction> to = newArrayList();
    if (strings != null) {
      for (String from : strings) {
        to.add(PrnfbPullRequestAction.valueOf(from));
      }
    }
    return to;
  }

  private static List<PullRequestState> toPullRequestStates(List<String> strings) {
    List<PullRequestState> to = newArrayList();
    if (strings != null) {
      for (String from : strings) {
        to.add(PullRequestState.valueOf(from));
      }
    }
    return to;
  }

  private static List<String> toPullRequestStateStrings(List<PullRequestState> list) {
    List<String> to = newArrayList();
    if (list != null) {
      for (Enum<?> e : list) {
        to.add(e.name());
      }
    }
    return to;
  }

  private static List<String> toStrings(List<PrnfbPullRequestAction> list) {
    List<String> to = newArrayList();
    if (list != null) {
      for (Enum<?> e : list) {
        to.add(e.name());
      }
    }
    return to;
  }
}
