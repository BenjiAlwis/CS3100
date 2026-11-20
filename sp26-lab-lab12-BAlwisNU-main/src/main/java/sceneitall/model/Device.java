package sceneitall.model;

/** A smart home device. All devices have a name and a type. */
public sealed interface Device permits Light, Fan, Shade {

  /** Returns the human-readable name of this device. */
  String getName();

  /** Returns the type of this device. */
  DeviceType getType();
}
