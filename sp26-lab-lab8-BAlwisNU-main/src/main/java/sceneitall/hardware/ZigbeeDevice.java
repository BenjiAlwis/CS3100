package sceneitall.hardware;

/**
 * Represents a device on the Zigbee network with its network address, PAN ID, and firmware version.
 */
public class ZigbeeDevice {
  private final String deviceId;
  private final String networkAddress;
  private final int panId;
  private final String firmwareVersion;

  /**
   * Creates a Zigbee device representation.
   *
   * @param deviceId the device identifier
   * @param networkAddress the Zigbee network address
   * @param panId the PAN (Personal Area Network) ID
   * @param firmwareVersion the firmware version string
   */
  public ZigbeeDevice(String deviceId, String networkAddress, int panId, String firmwareVersion) {
    this.deviceId = deviceId;
    this.networkAddress = networkAddress;
    this.panId = panId;
    this.firmwareVersion = firmwareVersion;
  }

  /** Returns the device identifier. */
  public String getDeviceId() {
    return deviceId;
  }

  /** Returns the Zigbee network address. */
  public String getNetworkAddress() {
    return networkAddress;
  }

  /** Returns the PAN (Personal Area Network) ID. */
  public int getPanId() {
    return panId;
  }

  /** Returns the firmware version. */
  public String getFirmwareVersion() {
    return firmwareVersion;
  }
}
