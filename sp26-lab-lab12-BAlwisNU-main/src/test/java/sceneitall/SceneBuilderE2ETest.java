package sceneitall;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;

import org.testfx.framework.junit5.ApplicationTest;

/**
 * End-to-end test for the Scene Builder task.
 *
 * <p>See {@link AreaDashboardE2ETest} for the testing pattern.
 */
class SceneBuilderE2ETest extends ApplicationTest {

  @Override
  public void start(Stage stage) throws Exception {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("/scene-builder-task.fxml"));
    stage.setScene(new Scene(loader.load(), 800, 600));
    stage.show();
  }

  /**
   * Finds a node by its accessibleText property. Use this instead of CSS selectors when possible.
   */
  private Node findByAccessibleText(String text) {
    return lookup(node -> text.equals(node.getAccessibleText())).query();
  }

  // TODO: Write one E2E test that interacts with your Scene Builder UI.
  // See AreaDashboardE2ETest for the pattern.
  //
  // Example:
  //   @Test
  //   void userCanAddDeviceToScene() {
  //       // 1. Find a widget by accessibleText
  //       // 2. interact() or clickOn() to use it
  //       // 3. Assert on a visible result
  //   }
}
