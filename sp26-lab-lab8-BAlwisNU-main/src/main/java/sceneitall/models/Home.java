package sceneitall.models;

import org.jspecify.annotations.Nullable;

/** Represents a home or location in SceneItAll, with a name and owner. */
public class Home {
  @Nullable private String id;
  @Nullable private String name;
  @Nullable private String ownerUserId;

  /** Creates an empty home. */
  public Home() {}

  /**
   * Creates a home with the given fields.
   *
   * @param id the home identifier
   * @param name the home name
   * @param ownerUserId the owner's user ID
   */
  public Home(String id, String name, String ownerUserId) {
    this.id = id;
    this.name = name;
    this.ownerUserId = ownerUserId;
  }

  /** Returns the home identifier. */
  @Nullable
  public String getId() {
    return id;
  }

  /** Sets the home identifier. */
  public void setId(String id) {
    this.id = id;
  }

  /** Returns the home name. */
  @Nullable
  public String getName() {
    return name;
  }

  /** Sets the home name. */
  public void setName(String name) {
    this.name = name;
  }

  /** Returns the owner's user ID. */
  @Nullable
  public String getOwnerUserId() {
    return ownerUserId;
  }

  /** Sets the owner's user ID. */
  public void setOwnerUserId(String ownerUserId) {
    this.ownerUserId = ownerUserId;
  }
}
