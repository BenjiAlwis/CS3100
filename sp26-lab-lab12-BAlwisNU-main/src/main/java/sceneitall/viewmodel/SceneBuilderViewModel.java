package sceneitall.viewmodel;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import org.jspecify.annotations.Nullable;

import sceneitall.model.Area;
import sceneitall.model.Device;

/**
 * ViewModel for the Scene Builder task — create/edit a Scene by selecting devices and setting
 * target states.
 *
 * <p>Properties are declared for you. Implement the TODO methods to make the GUI functional. See
 * {@link AreaDashboardViewModel} for the pattern.
 */
public class SceneBuilderViewModel {

  // --- Observable properties (bind your Controller widgets to these) ---

  private final StringProperty sceneName = new SimpleStringProperty("");
  private final ObservableList<String> availableDevices = FXCollections.observableArrayList();
  private final ObservableList<String> sceneDevices = FXCollections.observableArrayList();
  private final StringProperty selectedDevice = new SimpleStringProperty("");
  private final IntegerProperty targetValue = new SimpleIntegerProperty(50);
  private final StringProperty statusMessage = new SimpleStringProperty("");

  // --- Backing model ---

  // @Nullable because it is set via setArea(), not in the constructor.
  // Use Objects.requireNonNull(area, "Call setArea() first") in your implementations.
  @Nullable private Area area;

  public SceneBuilderViewModel() {
    // area is null until setArea() is called.
  }

  /**
   * Connects this ViewModel to an Area model. Populates the available devices list.
   *
   * @param area the area whose devices can be added to the scene
   */
  public void setArea(Area area) {
    this.area = area;
    availableDevices.clear();
    for (Device device : area.getDevices()) {
      availableDevices.add(device.getName());
    }
    statusMessage.set("");
  }

  /**
   * Adds the currently selected device to the scene with the current target value.
   *
   * <p>Should read {@link #selectedDeviceProperty()} and {@link #targetValueProperty()}, format an
   * entry like "Ceiling Light: 30", add it to {@link #getSceneDevices()}, and update the status
   * message.
   */
  public void addDeviceToScene() {
    // TODO: implement addDeviceToScene
    throw new UnsupportedOperationException("TODO: implement addDeviceToScene");
  }

  /**
   * Removes a device entry from the scene.
   *
   * @param entry the formatted entry to remove (e.g., "Ceiling Light: 30")
   */
  public void removeDeviceFromScene(String entry) {
    // TODO: implement removeDeviceFromScene
    throw new UnsupportedOperationException("TODO: implement removeDeviceFromScene");
  }

  /**
   * Saves the current scene configuration. Creates a new {@link sceneitall.model.Scene} from the
   * scene name and device entries, and adds it to the area.
   *
   * <p>Validates that a scene name is provided and at least one device has been added. Parse each
   * entry in {@link #getSceneDevices()} (formatted as "Device Name: value") to build the targets
   * map.
   *
   * @return the created Scene
   * @throws IllegalStateException if the scene name is empty or no devices have been added
   */
  public sceneitall.model.Scene saveScene() {
    // TODO: implement saveScene
    throw new UnsupportedOperationException("TODO: implement saveScene");
  }

  // --- Property accessors ---

  public StringProperty sceneNameProperty() {
    return sceneName;
  }

  public ObservableList<String> getAvailableDevices() {
    return availableDevices;
  }

  public ObservableList<String> getSceneDevices() {
    return sceneDevices;
  }

  public StringProperty selectedDeviceProperty() {
    return selectedDevice;
  }

  public IntegerProperty targetValueProperty() {
    return targetValue;
  }

  public StringProperty statusMessageProperty() {
    return statusMessage;
  }
}
