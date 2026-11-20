package sceneitall.services;

import org.jspecify.annotations.Nullable;

import sceneitall.DatabaseConnection;
import sceneitall.models.User;

/** Manages user accounts. */
public class UserService {

  /**
   * Retrieves a user by ID.
   *
   * @param userId the user identifier
   * @return the user, or null if not found
   */
  @Nullable
  public User getUser(String userId) {
    return DatabaseConnection.getInstance().loadUser(userId);
  }

  /**
   * Creates a new user account.
   *
   * @param user the user to create
   */
  public void createUser(User user) {
    DatabaseConnection.getInstance().saveUser(user);
  }
}
