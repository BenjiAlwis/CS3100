package sceneitall.viewmodel;

import java.util.Objects;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import org.jspecify.annotations.Nullable;

import sceneitall.model.Area;
import sceneitall.model.Device;
import sceneitall.model.Fan;
import sceneitall.model.FanSpeed;
import sceneitall.model.Light;
import sceneitall.model.Scene;
import sceneitall.model.Shade;

/**
 * ViewModel for the Area Dashboard — a complete worked example from L29/L30.
 *
 * <p>This class demonstrates the MVVM pattern: it wraps the plain-POJO {@link Area} model with
 * JavaFX observable properties so the Controller can bind UI widgets to them. All logic lives here,
 * not in the Controller.
 *
 * <p><b>Read this class as a reference.</b> Your task ViewModel follows the same pattern.
 */
public class AreaDashboardViewModel {

  // --- Observable properties (the Controller binds to these) ---

  private final StringProperty areaName = new SimpleStringProperty("");
  private final ObservableList<String> deviceDescriptions = FXCollections.observableArrayList();
  private final ObservableList<String> sceneNames = FXCollections.observableArrayList();
  private final StringProperty selectedSceneName = new SimpleStringProperty("");
  private final IntegerProperty selectedLightBrightness = new SimpleIntegerProperty(100);
  private final StringProperty statusMessage = new SimpleStringProperty("");

  // --- Backing model (not observable) ---

  // @Nullable because they are set via setArea(), not in the constructor.
  @Nullable private Area area;
  @Nullable private Light controlledLight; // the first Light found, for the brightness slider

  /** Creates a new AreaDashboardViewModel with default (empty) state. */
  public AreaDashboardViewModel() {
    // area and controlledLight are null until setArea() is called.
  }

  /**
   * Connects this ViewModel to an Area model. Populates all observable properties from the model.
   *
   * @param area the area to display
   */
  public void setArea(Area area) {
    this.area = area;
    areaName.set(area.getName());

    // Find the first Light for the brightness slider demo
    controlledLight = null;
    for (Device device : area.getDevices()) {
      if (device instanceof Light light) {
        controlledLight = light;
        selectedLightBrightness.set(light.getBrightness());
        break;
      }
    }

    // Populate the scene names list
    sceneNames.clear();
    for (Scene scene : area.getScenes()) {
      sceneNames.add(scene.getName());
    }

    refreshDeviceDescriptions();
    statusMessage.set("");
  }

  /**
   * Activates the currently selected scene. Applies each target value to the corresponding device,
   * then refreshes the device descriptions.
   */
  public void activateScene() {
    Area area = Objects.requireNonNull(this.area, "Call setArea() first");
    String sceneName = selectedSceneName.get();
    if (sceneName == null || sceneName.isEmpty()) {
      statusMessage.set("Select a scene first.");
      return;
    }

    Scene scene =
        area.getScenes().stream()
            .filter(s -> s.getName().equals(sceneName))
            .findFirst()
            .orElse(null);
    if (scene == null) {
      statusMessage.set("Scene not found: " + sceneName);
      return;
    }

    // Apply each target to the corresponding device
    for (var entry : scene.getTargets().entrySet()) {
      area.findDevice(entry.getKey()).ifPresent(device -> applyTarget(device, entry.getValue()));
    }

    // Update the brightness slider if the controlled light was affected
    if (controlledLight != null) {
      selectedLightBrightness.set(controlledLight.getBrightness());
    }

    refreshDeviceDescriptions();
    statusMessage.set("Scene '" + sceneName + "' activated!");
  }

  /**
   * Updates the brightness of the controlled light.
   *
   * @param value brightness 0-100
   */
  public void updateBrightness(int value) {
    if (controlledLight != null) {
      controlledLight.setBrightness(value);
      controlledLight.setOn(value > 0);
      refreshDeviceDescriptions();
    }
  }

  // --- Property accessors (used by the Controller for binding) ---

  public StringProperty areaNameProperty() {
    return areaName;
  }

  public ObservableList<String> getDeviceDescriptions() {
    return deviceDescriptions;
  }

  public ObservableList<String> getSceneNames() {
    return sceneNames;
  }

  public StringProperty selectedSceneNameProperty() {
    return selectedSceneName;
  }

  public IntegerProperty selectedLightBrightnessProperty() {
    return selectedLightBrightness;
  }

  public StringProperty statusMessageProperty() {
    return statusMessage;
  }

  // --- Private helpers ---

  /** Rebuilds the device description list from current model state. */
  private void refreshDeviceDescriptions() {
    Area area = Objects.requireNonNull(this.area, "Call setArea() first");
    deviceDescriptions.clear();
    for (Device device : area.getDevices()) {
      deviceDescriptions.add(describeDevice(device));
    }
  }

  /** Formats a one-line description of a device's current state. */
  private String describeDevice(Device device) {
    return switch (device) {
      case Light light ->
          light.getName()
              + ": "
              + (light.isOn() ? "ON" : "OFF")
              + ", brightness "
              + light.getBrightness()
              + "%";
      case Fan fan ->
          fan.getName() + ": " + (fan.isOn() ? "ON" : "OFF") + ", speed " + fan.getSpeed();
      case Shade shade -> shade.getName() + ": position " + shade.getPosition() + "%";
    };
  }

  /** Applies a target value to a device based on its type. */
  private void applyTarget(Device device, int targetValue) {
    switch (device) {
      case Light light -> {
        light.setBrightness(targetValue);
        light.setOn(targetValue > 0);
      }
      case Fan fan -> {
        fan.setSpeed(FanSpeed.fromOrdinal(targetValue));
        fan.setOn(true);
      }
      case Shade shade -> shade.setPosition(targetValue);
    }
  }
}
