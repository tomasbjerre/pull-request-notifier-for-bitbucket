package se.bjurr.prnfb.presentation.dto;

import static javax.xml.bind.annotation.XmlAccessType.FIELD;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/** @see ButtonFormElementDTO */
@XmlRootElement
@XmlAccessorType(FIELD)
public class ButtonFormElementOptionDTO {
  private String label;
  private String name;
  private Boolean defaultValue;

  public void setDefaultValue(Boolean defaultValue) {
    this.defaultValue = defaultValue;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Boolean getDefaultValue() {
    return defaultValue;
  }

  public String getLabel() {
    return label;
  }

  public String getName() {
    return name;
  }

  @Override
  public String toString() {
    return "ButtonFormOptionDTO [label="
        + label
        + ", name="
        + name
        + ", defaultValue="
        + defaultValue
        + "]";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (defaultValue == null ? 0 : defaultValue.hashCode());
    result = prime * result + (label == null ? 0 : label.hashCode());
    result = prime * result + (name == null ? 0 : name.hashCode());
    return result;
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
    ButtonFormElementOptionDTO other = (ButtonFormElementOptionDTO) obj;
    if (defaultValue == null) {
      if (other.defaultValue != null) {
        return false;
      }
    } else if (!defaultValue.equals(other.defaultValue)) {
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
    return true;
  }
}
