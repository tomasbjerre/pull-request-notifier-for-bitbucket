package se.bjurr.prnfb.settings;

import static com.google.common.base.MoreObjects.firstNonNull;
import static com.google.common.base.Optional.fromNullable;
import static com.google.common.base.Strings.emptyToNull;
import static java.util.UUID.randomUUID;

import com.google.common.base.Optional;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import se.bjurr.prnfb.presentation.dto.ON_OR_OFF;

public class PrnfbButton implements HasUuid, Restricted {

  private final ON_OR_OFF confirmation;
  private final String name;
  private final String projectKey;
  private final String repositorySlug;
  private final List<PrnfbButtonFormElement> buttonFormElementList;
  private final USER_LEVEL userLevel;
  private final UUID uuid;
  private final String confirmationText;
  private final String redirectUrl;

  public PrnfbButton(
      UUID uuid,
      String name,
      USER_LEVEL userLevel,
      ON_OR_OFF confirmation,
      String projectKey,
      String repositorySlug,
      String confirmationText,
      String redirectUrl,
      List<PrnfbButtonFormElement> buttonFormElementList) {
    this.uuid = firstNonNull(uuid, randomUUID());
    this.name = name;
    this.userLevel = userLevel;
    this.confirmation = confirmation;
    this.repositorySlug = emptyToNull(repositorySlug);
    this.projectKey = emptyToNull(projectKey);
    this.confirmationText = emptyToNull(confirmationText);
    this.redirectUrl = emptyToNull(redirectUrl);
    this.buttonFormElementList =
        firstNonNull(buttonFormElementList, new ArrayList<PrnfbButtonFormElement>());
  }

  public String getConfirmationText() {
    return confirmationText;
  }

  public ON_OR_OFF getConfirmation() {
    return this.confirmation;
  }

  public String getName() {
    return this.name;
  }

  public List<PrnfbButtonFormElement> getButtonFormElementList() {
    return buttonFormElementList;
  }

  @Override
  public Optional<String> getProjectKey() {
    return fromNullable(this.projectKey);
  }

  @Override
  public Optional<String> getRepositorySlug() {
    return fromNullable(this.repositorySlug);
  }

  public USER_LEVEL getUserLevel() {
    return this.userLevel;
  }

  @Override
  public UUID getUuid() {
    return this.uuid;
  }

  public String getRedirectUrl() {
    return redirectUrl;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    PrnfbButton other = (PrnfbButton) obj;
    if (buttonFormElementList == null) {
      if (other.buttonFormElementList != null) {
        return false;
      }
    } else if (!buttonFormElementList.equals(other.buttonFormElementList)) {
      return false;
    }
    if (confirmation != other.confirmation) {
      return false;
    }
    if (confirmationText == null) {
      if (other.confirmationText != null) {
        return false;
      }
    } else if (!confirmationText.equals(other.confirmationText)) {
      return false;
    }
    if (name == null) {
      if (other.name != null) {
        return false;
      }
    } else if (!name.equals(other.name)) {
      return false;
    }
    if (projectKey == null) {
      if (other.projectKey != null) {
        return false;
      }
    } else if (!projectKey.equals(other.projectKey)) {
      return false;
    }
    if (repositorySlug == null) {
      if (other.repositorySlug != null) {
        return false;
      }
    } else if (!repositorySlug.equals(other.repositorySlug)) {
      return false;
    }
    if (userLevel != other.userLevel) {
      return false;
    }
    if (uuid == null) {
      if (other.uuid != null) {
        return false;
      }
    } else if (!uuid.equals(other.uuid)) {
      return false;
    }
    if (redirectUrl == null) {
      if (other.redirectUrl != null) {
        return false;
      }
    } else if (!redirectUrl.equals(other.redirectUrl)) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result =
        prime * result + (buttonFormElementList == null ? 0 : buttonFormElementList.hashCode());
    result = prime * result + (confirmation == null ? 0 : confirmation.hashCode());
    result = prime * result + (confirmationText == null ? 0 : confirmationText.hashCode());
    result = prime * result + (name == null ? 0 : name.hashCode());
    result = prime * result + (projectKey == null ? 0 : projectKey.hashCode());
    result = prime * result + (repositorySlug == null ? 0 : repositorySlug.hashCode());
    result = prime * result + (userLevel == null ? 0 : userLevel.hashCode());
    result = prime * result + (uuid == null ? 0 : uuid.hashCode());
    result = prime * result + (redirectUrl == null ? 0 : redirectUrl.hashCode());
    return result;
  }

  @Override
  public String toString() {
    return "PrnfbButton [confirmation="
        + confirmation
        + ", name="
        + name
        + ", projectKey="
        + projectKey
        + ", repositorySlug="
        + repositorySlug
        + ", buttonFormElementList="
        + buttonFormElementList
        + ", userLevel="
        + userLevel
        + ", uuid="
        + uuid
        + ", confirmationText="
        + confirmationText
        + ", redirectUrl="
        + redirectUrl
        + "]";
  }
}
