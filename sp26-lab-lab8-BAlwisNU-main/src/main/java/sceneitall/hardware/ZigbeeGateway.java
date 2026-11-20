package sceneitall.hardware;

import org.jspecify.annotations.Nullable;

import sceneitall.models.DeviceState;
import sceneitall.models.RuleCondition;

/**
 * Provides access to the Zigbee network for reading device states and sending commands to IoT
 * devices.
 */
public final class ZigbeeGateway {
  @Nullable private static ZigbeeGateway globalInstance;

  /** Returns the shared Zigbee gateway instance. */
  public static ZigbeeGateway getGlobalInstance() {
    if (globalInstance == null) {
      globalInstance = new ZigbeeGateway();
    }
    return globalInstance;
  }

  private ZigbeeGateway() {}

  /**
   * Reads the current state of a device from the Zigbee network.
   *
   * @param deviceId the device identifier
   * @return the device state
   */
  public DeviceState readState(String deviceId) {
    ZigbeeFrame frame = sendCommand(deviceId, new byte[] {0x01, 0x02});
    return parseState(frame);
  }

  /**
   * Sends a raw command to a device and returns the response frame.
   *
   * @param deviceId the device identifier
   * @param command the command bytes
   * @return the Zigbee response frame
   */
  public ZigbeeFrame sendCommand(String deviceId, byte[] command) {
    return new ZigbeeFrame(new byte[] {0x01}, 1, deviceId);
  }

  private DeviceState parseState(ZigbeeFrame frame) {
    String deviceId = frame.getSourceAddress();
    byte[] payload = frame.getPayload();

    if (payload == null || payload.length == 0) {
      return new DeviceState(deviceId, RuleCondition.MOTION_DETECTED, false);
    }

    RuleCondition stateType =
        switch (payload[0]) {
          case 0 -> RuleCondition.MOTION_DETECTED;
          case 1 -> RuleCondition.TEMPERATURE_ABOVE;
          case 2 -> RuleCondition.DOOR_OPENED;
          default -> RuleCondition.MOTION_DETECTED;
        };

    boolean triggered = payload.length > 1 && payload[1] != 0;

    return new DeviceState(deviceId, stateType, triggered);
  }
}
