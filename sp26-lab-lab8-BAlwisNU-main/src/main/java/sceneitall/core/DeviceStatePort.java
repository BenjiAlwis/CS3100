
package sceneitall.core;

import sceneitall.models.DeviceState;

/** Port for reading the current state of a device (hardware abstraction). */
public interface DeviceStatePort {
  DeviceState readState(String deviceId);
}