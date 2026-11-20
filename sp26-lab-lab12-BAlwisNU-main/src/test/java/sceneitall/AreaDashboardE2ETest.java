package sceneitall;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.stage.Stage;

import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

/**
 * End-to-end test for the Area Dashboard — a complete worked example.
 *
 * <p>This test demonstrates the TestFX pattern: extend {@link ApplicationTest}, load the FXML in
 * {@link #start(Stage)}, and interact with widgets using {@link #clickOn} and {@link #lookup}.
 *
 * <p>The {@link #findByAccessibleText(String)} helper finds widgets by their {@code accessibleText}
 * property, which you set in Scene Builder's Properties panel. This is more stable than looking up
 * by CSS selector or fx:id because it survives layout changes.
 *
 * <p><b>Read this test as a reference.</b> Your task's E2E test follows the same pattern.
 */
class AreaDashboardE2ETest extends ApplicationTest {

  @Override
  public void start(Stage stage) throws Exception {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("/area-dashboard.fxml"));
    stage.setScene(new Scene(loader.load(), 800, 600));
    stage.show();
  }

  /**
   * Finds a node by its accessibleText property. Use this instead of CSS selectors when possible —
   * it's more resilient to layout changes.
   */
  private Node findByAccessibleText(String text) {
    return lookup(node -> text.equals(node.getAccessibleText())).query();
  }

  @Test
  void activatingSceneUpdatesDeviceList() {
    // Select "Movie Night" from the scene ComboBox and fire the activate button
    @SuppressWarnings("unchecked")
    ComboBox<String> sceneCombo = (ComboBox<String>) findByAccessibleText("Select scene");
    javafx.scene.control.Button activateBtn =
        (javafx.scene.control.Button) findByAccessibleText("Activate scene");

    interact(
        () -> {
          sceneCombo.getSelectionModel().select("Movie Night");
          activateBtn.fire();
        });
    WaitForAsyncUtils.waitForFxEvents();

    // Verify the device list shows updated brightness for Ceiling Light
    @SuppressWarnings("unchecked")
    ListView<String> deviceList = (ListView<String>) findByAccessibleText("Device list");
    assertThat(
            deviceList.getItems().stream()
                .filter(s -> s.contains("Ceiling Light"))
                .findFirst()
                .orElse(""))
        .contains("20");
  }

  @Test
  void sliderUpdatesBrightnessLabel() {
    // Move the brightness slider and verify the label updates
    Slider slider = (Slider) findByAccessibleText("Light brightness");
    interact(() -> slider.setValue(50));
    WaitForAsyncUtils.waitForFxEvents();

    Label brightnessLabel = lookup("#brightnessValueLabel").query();
    assertEquals("50%", brightnessLabel.getText());
  }
}
