package se.bjurr.prnfb.presentation.dto;

import static javax.xml.bind.annotation.XmlAccessType.FIELD;

import com.google.common.base.Optional;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import se.bjurr.prnfb.settings.Restricted;
import se.bjurr.prnfb.settings.USER_LEVEL;

@XmlRootElement
@XmlAccessorType(FIELD)
public class ButtonDTO implements Comparable<ButtonDTO>, Restricted {
  public static Type BUTTON_FORM_LIST_DTO_TYPE =
      new TypeToken<ArrayList<ButtonFormElementDTO>>() {}.getType();

  private List<ButtonFormElementDTO> buttonFormList;
  /**
   * Makes it easier to implement GUI. JSON String representation of {@link #buttonFormList}. If
   * {@link #buttonFormList} is not defined and {@link #buttonFormListString} is defined, then that
   * is parsed to {@link #buttonFormList}.
   */
  private String buttonFormListString;

  private ON_OR_OFF confirmation;
  private String name;
  private String projectKey;
  private String repositorySlug;
  private USER_LEVEL userLevel;
  private UUID uuid;
  private String confirmationText;
  private String redirectUrl;

  public void setConfirmationText(String confirmationText) {
    this.confirmationText = confirmationText;
  }

  public String getConfirmationText() {
    return confirmationText;
  }

  @Override
  public int compareTo(ButtonDTO o) {
    return this.name.compareTo(o.name);
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
    ButtonDTO other = (ButtonDTO) obj;
    if (buttonFormList == null) {
      if (other.buttonFormList != null) {
        return false;
      }
    } else if (!buttonFormList.equals(other.buttonFormList)) {
      return false;
    }
    if (buttonFormListString == null) {
      if (other.buttonFormListString != null) {
        return false;
      }
    } else if (!buttonFormListString.equals(other.buttonFormListString)) {
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

  public List<ButtonFormElementDTO> getButtonFormList() {
    return buttonFormList;
  }

  public ON_OR_OFF getConfirmation() {
    return this.confirmation;
  }

  public String getName() {
    return this.name;
  }

  @Override
  public Optional<String> getProjectKey() {
    return Optional.fromNullable(this.projectKey);
  }

  @Override
  public Optional<String> getRepositorySlug() {
    return Optional.fromNullable(this.repositorySlug);
  }

  public USER_LEVEL getUserLevel() {
    return this.userLevel;
  }

  public UUID getUuid() {
    return this.uuid;
  }

  public UUID getUUID() {
    return this.uuid;
  }

  public String getRedirectUrl() {
    return this.redirectUrl;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (buttonFormList == null ? 0 : buttonFormList.hashCode());
    result = prime * result + (buttonFormListString == null ? 0 : buttonFormListString.hashCode());
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

  public void setButtonFormListString(String buttonFormDtoListString) {
    this.buttonFormListString = buttonFormDtoListString;
  }

  public String getButtonFormListString() {
    return buttonFormListString;
  }

  public void setButtonFormList(List<ButtonFormElementDTO> buttonFormList) {
    this.buttonFormList = buttonFormList;
  }

  public void setConfirmation(ON_OR_OFF confirmation) {
    this.confirmation = confirmation;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setProjectKey(String projectKey) {
    this.projectKey = projectKey;
  }

  public void setRepositorySlug(String repositorySlug) {
    this.repositorySlug = repositorySlug;
  }

  public void setUserLevel(USER_LEVEL userLevel) {
    this.userLevel = userLevel;
  }

  public void setUuid(UUID uuid) {
    this.uuid = uuid;
  }

  public void setRedirectUrl(String redirectUrl) {
    this.redirectUrl = redirectUrl;
  }

  @Override
  public String toString() {
    return "ButtonDTO [buttonFormList="
        + buttonFormList
        + ", buttonFormListString="
        + buttonFormListString
        + ", confirmation="
        + confirmation
        + ", name="
        + name
        + ", projectKey="
        + projectKey
        + ", repositorySlug="
        + repositorySlug
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
