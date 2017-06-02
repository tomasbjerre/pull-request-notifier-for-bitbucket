package se.bjurr.prnfb.service;

import static com.google.common.base.Strings.isNullOrEmpty;
import static se.bjurr.prnfb.service.PrnfbVariable.BUTTON_FORM_DATA;
import static se.bjurr.prnfb.service.PrnfbVariable.BUTTON_TRIGGER_TITLE;
import static se.bjurr.prnfb.service.PrnfbVariable.PULL_REQUEST_COMMENT_ACTION;
import static se.bjurr.prnfb.service.PrnfbVariable.PULL_REQUEST_COMMENT_TEXT;
import static se.bjurr.prnfb.service.PrnfbVariable.PULL_REQUEST_MERGE_COMMIT;
import static se.bjurr.prnfb.service.PrnfbVariable.PULL_REQUEST_PREVIOUS_FROM_HASH;
import static se.bjurr.prnfb.service.PrnfbVariable.PULL_REQUEST_PREVIOUS_TO_HASH;
import static se.bjurr.prnfb.service.PrnfbVariable.PULL_REQUEST_USER_GROUPS;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import se.bjurr.prnfb.settings.PrnfbButton;

import com.atlassian.bitbucket.event.pull.PullRequestCommentEvent;
import com.atlassian.bitbucket.event.pull.PullRequestEvent;
import com.atlassian.bitbucket.event.pull.PullRequestMergedEvent;
import com.atlassian.bitbucket.event.pull.PullRequestRescopedEvent;
import com.google.common.base.Joiner;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;

/**
 * {@link PrnfbVariable} is becoming a bit messy with a lot of parameters to resolve different
 * variables. This is intended to replace all those parameters.
 */
public class VariablesContext {

  public static class VariablesContextBuilder {
    public PrnfbButton button;
    public PullRequestEvent pullRequestEvent;
    public String formData;
    public List<String> groups;

    public VariablesContextBuilder setButton(PrnfbButton button) {
      this.button = button;
      return this;
    }

    public VariablesContextBuilder setFormData(String formData) {
      this.formData = formData;
      return this;
    }

    public VariablesContextBuilder setGroups(List<String> groups) {
      this.groups = groups;
      return this;
    }

    public VariablesContextBuilder setPullRequestEvent(PullRequestEvent pullRequestEvent) {
      this.pullRequestEvent = pullRequestEvent;
      return this;
    }

    public VariablesContextBuilder() {}

    public VariablesContext build() {
      return new VariablesContext(this);
    }
  }

  private final PrnfbButton button;
  private final PullRequestEvent pullRequestEvent;
  private final String formData;
  private final List<String> groups;

  public VariablesContext(VariablesContextBuilder b) {
    this.button = b.button;
    this.pullRequestEvent = b.pullRequestEvent;
    this.formData = b.formData;
    this.groups = b.groups;
  }

  public List<String> getGroups() {
    return groups;
  }

  public Map<PrnfbVariable, Supplier<String>> getVariables() {
    Map<PrnfbVariable, Supplier<String>> variables = new HashMap<>();

    if (groups != null) {
      variables.put(PULL_REQUEST_USER_GROUPS, Suppliers.ofInstance(Joiner.on(',').join(groups)));
    }

    if (button != null) {
      variables.put(BUTTON_TRIGGER_TITLE, Suppliers.ofInstance(button.getName()));
    }

    if (!isNullOrEmpty(formData)) {
      variables.put(BUTTON_FORM_DATA, Suppliers.ofInstance(formData));
    }

    if (pullRequestEvent != null) {
      if (pullRequestEvent instanceof PullRequestCommentEvent) {
        PullRequestCommentEvent pullRequestCommentEvent =
            (PullRequestCommentEvent) pullRequestEvent;
        variables.put(
            PULL_REQUEST_COMMENT_TEXT,
            () -> {
              return pullRequestCommentEvent.getComment().getText();
            });
        variables.put(
            PULL_REQUEST_COMMENT_ACTION,
            () -> {
              return pullRequestCommentEvent.getCommentAction().name();
            });
      } else if (pullRequestEvent instanceof PullRequestRescopedEvent) {
        PullRequestRescopedEvent pullRequestRescopedEvent =
            (PullRequestRescopedEvent) pullRequestEvent;
        variables.put(
            PULL_REQUEST_PREVIOUS_FROM_HASH,
            () -> {
              if (pullRequestEvent instanceof PullRequestRescopedEvent) {
                return pullRequestRescopedEvent.getPreviousFromHash();
              }
              return "";
            });
        variables.put(
            PULL_REQUEST_PREVIOUS_TO_HASH,
            () -> {
              if (pullRequestEvent instanceof PullRequestRescopedEvent) {
                return pullRequestRescopedEvent.getPreviousToHash();
              }
              return "";
            });
      } else if (pullRequestEvent instanceof PullRequestMergedEvent) {
        variables.put(
            PULL_REQUEST_MERGE_COMMIT,
            new Supplier<String>() {
              @Override
              public String get() {
                return ((PullRequestMergedEvent) pullRequestEvent).getCommit().getId();
              }
            });
      }
    }

    return variables;
  }
}
