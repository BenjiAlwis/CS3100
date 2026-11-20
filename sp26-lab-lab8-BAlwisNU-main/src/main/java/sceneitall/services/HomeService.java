package sceneitall.services;

import org.jspecify.annotations.Nullable;

import sceneitall.DatabaseConnection;
import sceneitall.models.Home;

/** Manages home and location configurations. */
public class HomeService {

  /**
   * Retrieves a home by ID.
   *
   * @param homeId the home identifier
   * @return the home, or null if not found
   */
  @Nullable
  public Home getHome(String homeId) {
    return DatabaseConnection.getInstance().loadHome(homeId);
  }

  /**
   * Creates a new home.
   *
   * @param home the home to create
   */
  public void createHome(Home home) {
    DatabaseConnection.getInstance().saveHome(home);
  }
}
