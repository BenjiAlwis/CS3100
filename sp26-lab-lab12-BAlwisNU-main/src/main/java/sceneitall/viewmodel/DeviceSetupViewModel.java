package sceneitall.viewmodel;

import java.util.List;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import org.jspecify.annotations.Nullable;

import sceneitall.model.Area;
import sceneitall.model.Device;
import sceneitall.model.DeviceType;

/**
 * ViewModel for the Device Setup task — add a new device by discovering it on the network, naming
 * it, assigning it to a room, and saving.
 *
 * <p>Properties are declared for you. Implement the TODO methods to make the GUI functional. See
 * {@link AreaDashboardViewModel} for the pattern.
 */
public class DeviceSetupViewModel {

  // --- Observable properties ---

  private final ObservableList<String> discoveredDevices = FXCollections.observableArrayList();
  private final StringProperty selectedDeviceName = new SimpleStringProperty("");
  private final ObservableList<String> availableRooms = FXCollections.observableArrayList();
  private final StringProperty assignedRoom = new SimpleStringProperty("");
  private final ObjectProperty<DeviceType> deviceType =
      new SimpleObjectProperty<>(DeviceType.LIGHT);
  private final StringProperty statusMessage = new SimpleStringProperty("");

  // --- Backing model ---

  // @Nullable because it is set via setModel(), not in the constructor.
  // Use Objects.requireNonNull(areas, "Call setModel() first") in your implementations.
  @Nullable private List<Area> areas;

  public DeviceSetupViewModel() {
    // areas is null until setModel() is called.
  }

  /**
   * Connects this ViewModel to the home model. Populates the available rooms list.
   *
   * @param areas the list of areas (rooms) in the home
   */
  public void setModel(List<Area> areas) {
    this.areas = areas;
    availableRooms.clear();
    for (Area area : areas) {
      availableRooms.add(area.getName());
    }
    statusMessage.set("");
  }

  /**
   * Simulates scanning the network for new devices. Should populate {@link #getDiscoveredDevices()}
   * with simulated device names (e.g., "New Light 1", "New Fan 2").
   */
  public void scanForDevices() {
    // TODO: implement scanForDevices
    throw new UnsupportedOperationException("TODO: implement scanForDevices");
  }

  /**
   * Sets the device name for configuration. (Can be a discovered name or a custom name typed by the
   * user.)
   *
   * @param name the device name to set
   */
  public void selectDevice(String name) {
    // TODO: implement selectDevice
    throw new UnsupportedOperationException("TODO: implement selectDevice");
  }

  /**
   * Assigns the selected device to a room.
   *
   * @param roomName the room to assign the device to
   */
  public void assignToRoom(String roomName) {
    // TODO: implement assignToRoom
    throw new UnsupportedOperationException("TODO: implement assignToRoom");
  }

  /**
   * Saves the device configuration. Creates the appropriate {@link Device} subtype based on {@link
   * #deviceTypeProperty()}, adds it to the assigned room's {@link Area}, and updates the status
   * message.
   *
   * <p>Hint: use a switch on {@link DeviceType} to create the right subclass — {@code case LIGHT ->
   * new Light(name)}, etc.
   *
   * @return the created Device
   * @throws IllegalStateException if the device name is empty or no room is assigned
   */
  public Device saveDevice() {
    // TODO: implement saveDevice
    throw new UnsupportedOperationException("TODO: implement saveDevice");
  }

  // --- Property accessors ---

  public ObservableList<String> getDiscoveredDevices() {
    return discoveredDevices;
  }

  public StringProperty selectedDeviceNameProperty() {
    return selectedDeviceName;
  }

  public ObservableList<String> getAvailableRooms() {
    return availableRooms;
  }

  public StringProperty assignedRoomProperty() {
    return assignedRoom;
  }

  public ObjectProperty<DeviceType> deviceTypeProperty() {
    return deviceType;
  }

  public StringProperty statusMessageProperty() {
    return statusMessage;
  }
}
