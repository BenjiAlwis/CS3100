package sceneitall.hardware;

/**
 * Represents a low-level Zigbee protocol frame with payload, sequence number, and source address.
 */
public class ZigbeeFrame {
  private final byte[] payload;
  private final int sequenceNumber;
  private final String sourceAddress;

  /**
   * Creates a Zigbee frame.
   *
   * @param payload the frame payload
   * @param sequenceNumber the sequence number
   * @param sourceAddress the source device address
   */
  public ZigbeeFrame(byte[] payload, int sequenceNumber, String sourceAddress) {
    this.payload = payload != null ? payload.clone() : new byte[0];
    this.sequenceNumber = sequenceNumber;
    this.sourceAddress = sourceAddress;
  }

  /** Returns a copy of the frame payload. */
  public byte[] getPayload() {
    return payload.clone();
  }

  /** Returns the sequence number. */
  public int getSequenceNumber() {
    return sequenceNumber;
  }

  /** Returns the source device address. */
  public String getSourceAddress() {
    return sourceAddress;
  }
}
