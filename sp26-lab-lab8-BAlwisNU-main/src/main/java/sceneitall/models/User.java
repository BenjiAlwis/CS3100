package sceneitall.models;

import org.jspecify.annotations.Nullable;

/** Represents a user account with email and display name. */
public class User {
  @Nullable private String id;
  @Nullable private String email;
  @Nullable private String displayName;

  /** Creates an empty user. */
  public User() {}

  /**
   * Creates a user with the given fields.
   *
   * @param id the user identifier
   * @param email the email address
   * @param displayName the display name
   */
  public User(String id, String email, String displayName) {
    this.id = id;
    this.email = email;
    this.displayName = displayName;
  }

  /** Returns the user identifier. */
  @Nullable
  public String getId() {
    return id;
  }

  /** Sets the user identifier. */
  public void setId(String id) {
    this.id = id;
  }

  /** Returns the email address. */
  @Nullable
  public String getEmail() {
    return email;
  }

  /** Sets the email address. */
  public void setEmail(String email) {
    this.email = email;
  }

  /** Returns the display name. */
  @Nullable
  public String getDisplayName() {
    return displayName;
  }

  /** Sets the display name. */
  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }
}
