package se.bjurr.prnfb.transformer;

import static com.google.common.collect.Lists.newArrayList;
import static se.bjurr.prnfb.settings.PrnfbNotificationBuilder.prnfbNotificationBuilder;
import static se.bjurr.prnfb.settings.PrnfbSettings.UNCHANGED;

import com.atlassian.bitbucket.pull.PullRequestState;
import java.util.List;
import se.bjurr.prnfb.listener.PrnfbPullRequestAction;
import se.bjurr.prnfb.presentation.dto.NotificationDTO;
import se.bjurr.prnfb.settings.PrnfbNotification;
import se.bjurr.prnfb.settings.ValidationException;

public class NotificationTransformer {

  public static NotificationDTO toNotificationDto(final PrnfbNotification from) {
    final NotificationDTO to = new NotificationDTO();
    to.setProjectKey(from.getProjectKey().orNull());
    to.setRepositorySlug(from.getRepositorySlug().orNull());
    to.setFilterRegexp(from.getFilterRegexp().orNull());
    to.setFilterString(from.getFilterString().orNull());
    to.setInjectionUrl(from.getInjectionUrl().orNull());
    to.setInjectionUrlRegexp(from.getInjectionUrlRegexp().orNull());
    to.setVariableName(from.getVariableName().orNull());
    to.setVariableRegex(from.getVariableRegex().orNull());
    to.setMethod(from.getMethod());
    to.setName(from.getName());
    to.setPostContent(from.getPostContent().orNull());
    to.setPostContentEncoding(from.getPostContentEncoding());
    to.setProxyPort(from.getProxyPort());
    to.setProxyServer(from.getProxyServer().orNull());
    to.setProxySchema(from.getProxySchema().orNull());
    to.setProxyUser(UNCHANGED);
    to.setProxyPassword(UNCHANGED);
    to.setTriggerIfCanMerge(from.getTriggerIfCanMerge());
    to.setTriggerIgnoreStateList(toPullRequestStateStrings(from.getTriggerIgnoreStateList()));
    to.setTriggers(toStrings(from.getTriggers()));
    to.setUpdatePullRequestRefs(from.isUpdatePullRequestRefs());
    to.setUrl(from.getUrl());
    to.setUser(UNCHANGED);
    to.setPassword(UNCHANGED);
    to.setUuid(from.getUuid());
    to.setHttpVersion(from.getHttpVersion());
    return to;
  }

  public static List<NotificationDTO> toNotificationDtoList(
      final Iterable<PrnfbNotification> from) {
    final List<NotificationDTO> to = newArrayList();
    if (from != null) {
      for (final PrnfbNotification n : from) {
        to.add(toNotificationDto(n));
      }
    }
    return to;
  }

  public static PrnfbNotification toPrnfbNotification(final NotificationDTO from)
      throws ValidationException {
    return prnfbNotificationBuilder() //
        .withFilterRegexp(from.getFilterRegexp()) //
        .withFilterString(from.getFilterString()) //
        .withInjectionUrl(from.getInjectionUrl()) //
        .withInjectionUrlRegexp(from.getInjectionUrlRegexp()) //
        .withVariableName(from.getVariableName()) //
        .withVariableRegex(from.getVariableRegex()) //
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
        .withUpdatePullRequestRefs(from.isUpdatePullRequestRefs()) //
        .withTriggerIfCanMerge(from.getTriggerIfCanMerge()) //
        .setTriggerIgnoreState(toPullRequestStates(from.getTriggerIgnoreStateList())) //
        .withUrl(from.getUrl()) //
        .withUser(from.getUser()) //
        .withUuid(from.getUuid()) //
        .withRepositorySlug(from.getRepositorySlug().orNull()) //
        .withProjectKey(from.getProjectKey().orNull()) //
        .withHttpVersion(from.getHttpVersion())
        .build();
  }

  private static List<PrnfbPullRequestAction> toPrnfbPullRequestActions(
      final List<String> strings) {
    final List<PrnfbPullRequestAction> to = newArrayList();
    if (strings != null) {
      for (final String from : strings) {
        to.add(PrnfbPullRequestAction.valueOf(from));
      }
    }
    return to;
  }

  private static List<PullRequestState> toPullRequestStates(final List<String> strings) {
    final List<PullRequestState> to = newArrayList();
    if (strings != null) {
      for (final String from : strings) {
        to.add(PullRequestState.valueOf(from));
      }
    }
    return to;
  }

  private static List<String> toPullRequestStateStrings(final List<PullRequestState> list) {
    final List<String> to = newArrayList();
    if (list != null) {
      for (final Enum<?> e : list) {
        to.add(e.name());
      }
    }
    return to;
  }

  private static List<String> toStrings(final List<PrnfbPullRequestAction> list) {
    final List<String> to = newArrayList();
    if (list != null) {
      for (final Enum<?> e : list) {
        to.add(e.name());
      }
    }
    return to;
  }
}
