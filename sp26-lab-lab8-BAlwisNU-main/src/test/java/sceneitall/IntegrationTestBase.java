package sceneitall;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;

/** Base class for integration tests. Initializes shared infrastructure before tests. */
@Disabled("Requires real infrastructure")
abstract class IntegrationTestBase {

  @BeforeAll
  static void setupSharedInfrastructure() {
    DatabaseConnection.getInstance();
  }
}
