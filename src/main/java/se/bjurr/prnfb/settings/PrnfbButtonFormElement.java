package se.bjurr.prnfb.settings;

import static com.google.common.base.MoreObjects.firstNonNull;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.emptyToNull;

import java.util.ArrayList;
import java.util.List;
import se.bjurr.prnfb.presentation.dto.ButtonFormType;

public class PrnfbButtonFormElement {
  private final String defaultValue;
  private final String description;
  private final String label;
  private final String name;
  private final List<PrnfbButtonFormElementOption> buttonFormElementOptionList;
  private final boolean required;
  private final ButtonFormType type;

  public PrnfbButtonFormElement(
      String defaultValue,
      String description,
      String label,
      String name,
      List<PrnfbButtonFormElementOption> options,
      boolean required,
      ButtonFormType type) {
    this.defaultValue = emptyToNull(defaultValue);
    this.description = emptyToNull(description);
    this.label = checkNotNull(label, "label");
    this.name = checkNotNull(name, "name");
    this.buttonFormElementOptionList =
        firstNonNull(options, new ArrayList<PrnfbButtonFormElementOption>());
    this.required = checkNotNull(required, required);
    this.type = checkNotNull(type, type);
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
    PrnfbButtonFormElement other = (PrnfbButtonFormElement) obj;
    if (defaultValue == null) {
      if (other.defaultValue != null) {
        return false;
      }
    } else if (!defaultValue.equals(other.defaultValue)) {
      return false;
    }
    if (description == null) {
      if (other.description != null) {
        return false;
      }
    } else if (!description.equals(other.description)) {
      return false;
    }
    if (label == null) {
      if (other.label != null) {
        return false;
      }
    } else if (!label.equals(other.label)) {
      return false;
    }
    if (name == null) {
      if (other.name != null) {
        return false;
      }
    } else if (!name.equals(other.name)) {
      return false;
    }
    if (buttonFormElementOptionList == null) {
      if (other.buttonFormElementOptionList != null) {
        return false;
      }
    } else if (!buttonFormElementOptionList.equals(other.buttonFormElementOptionList)) {
      return false;
    }
    if (required != other.required) {
      return false;
    }
    if (type != other.type) {
      return false;
    }
    return true;
  }

  public String getDefaultValue() {
    return defaultValue;
  }

  public String getDescription() {
    return description;
  }

  public String getLabel() {
    return label;
  }

  public String getName() {
    return name;
  }

  public List<PrnfbButtonFormElementOption> getOptions() {
    return buttonFormElementOptionList;
  }

  public boolean getRequired() {
    return required;
  }

  public ButtonFormType getType() {
    return type;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (defaultValue == null ? 0 : defaultValue.hashCode());
    result = prime * result + (description == null ? 0 : description.hashCode());
    result = prime * result + (label == null ? 0 : label.hashCode());
    result = prime * result + (name == null ? 0 : name.hashCode());
    result =
        prime * result
            + (buttonFormElementOptionList == null ? 0 : buttonFormElementOptionList.hashCode());
    result = prime * result + (required ? 1231 : 1237);
    result = prime * result + (type == null ? 0 : type.hashCode());
    return result;
  }

  @Override
  public String toString() {
    return "ButtonFormDTO [options="
        + buttonFormElementOptionList
        + ", name="
        + name
        + ", label="
        + label
        + ", defaultValue="
        + defaultValue
        + ", type="
        + type
        + ", required="
        + required
        + ", description="
        + description
        + "]";
  }
}
