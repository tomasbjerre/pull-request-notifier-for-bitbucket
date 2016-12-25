package se.bjurr.prnfb.settings;

import static com.google.common.base.MoreObjects.firstNonNull;
import static com.google.common.collect.Lists.newArrayList;
import static se.bjurr.prnfb.settings.PrnfbSettingsDataBuilder.prnfbSettingsDataBuilder;

import java.util.ArrayList;
import java.util.List;

public class PrnfbSettingsBuilder {
  public static PrnfbSettingsBuilder prnfbSettingsBuilder() {
    return new PrnfbSettingsBuilder();
  }

  public static PrnfbSettingsBuilder prnfbSettingsBuilder(PrnfbSettings settings) {
    return new PrnfbSettingsBuilder(settings);
  }

  private List<PrnfbButton> buttons;
  private List<PrnfbNotification> notifications;
  private PrnfbSettingsData prnfbSettingsData;

  private PrnfbSettingsBuilder() {
    this.notifications = newArrayList();
    this.buttons = newArrayList();
    this.prnfbSettingsData =
        prnfbSettingsDataBuilder() //
            .build();
  }

  private PrnfbSettingsBuilder(PrnfbSettings settings) {
    this.notifications =
        firstNonNull(settings.getNotifications(), new ArrayList<PrnfbNotification>());
    this.buttons = firstNonNull(settings.getButtons(), new ArrayList<PrnfbButton>());
    this.prnfbSettingsData = settings.getPrnfbSettingsData();
  }

  public PrnfbSettings build() {
    return new PrnfbSettings(this);
  }

  public List<PrnfbButton> getButtons() {
    return this.buttons;
  }

  public List<PrnfbNotification> getNotifications() {
    return this.notifications;
  }

  public PrnfbSettingsData getPrnfbSettingsData() {
    return this.prnfbSettingsData;
  }

  public PrnfbSettingsBuilder setButtons(List<PrnfbButton> buttons) {
    this.buttons = buttons;
    return this;
  }

  public PrnfbSettingsBuilder setNotifications(List<PrnfbNotification> notifications) {
    this.notifications = notifications;
    return this;
  }

  public PrnfbSettingsBuilder setPrnfbSettingsData(PrnfbSettingsData prnfbSettingsData) {
    this.prnfbSettingsData = prnfbSettingsData;
    return this;
  }

  public PrnfbSettingsBuilder withButton(PrnfbButton prnfbButton) {
    this.buttons.add(prnfbButton);
    return this;
  }

  public PrnfbSettingsBuilder withNotification(PrnfbNotification notification) {
    this.notifications.add(notification);
    return this;
  }
}
