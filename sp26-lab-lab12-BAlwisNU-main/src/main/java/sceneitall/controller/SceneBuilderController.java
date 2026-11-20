package sceneitall.controller;

import javafx.fxml.FXML;

import org.jspecify.annotations.NullUnmarked;

/**
 * Controller for the Scene Builder view.
 *
 * <p>TODO: Add @FXML fields matching your fx:id values from the FXML. TODO: Create and wire the
 * ViewModel. TODO: Implement initialize() to set up bindings. TODO: Implement handler methods that
 * delegate to the ViewModel.
 *
 * <p>See {@link AreaDashboardController} for the pattern.
 */
// @NullUnmarked: @FXML fields are injected by FXMLLoader at runtime, not in the constructor.
// Static analysis can't model framework injection, so null analysis is disabled for this class.
@NullUnmarked
public class SceneBuilderController {

  // TODO: Add @FXML fields here (e.g., @FXML private TextField sceneNameField;)
  //       Each field name must match an fx:id in your FXML.

  // TODO: Create your ViewModel field (e.g., private SceneBuilderViewModel viewModel;)

  /**
   * Called by FXMLLoader after all @FXML fields are injected.
   *
   * <p>IMPORTANT: Implement your ViewModel TODO methods BEFORE wiring listeners here, otherwise
   * listeners that fire during initialization will throw UnsupportedOperationException.
   */
  @FXML
  private void initialize() {
    // TODO: Build sample model data — create an Area with devices, e.g.:
    //   Area area = new Area("Living Room");
    //   area.addDevice(new Light("Ceiling Light"));
    //   area.addDevice(new Fan("Standing Fan"));
    //   area.addDevice(new Shade("Window Shade"));

    // TODO: Create ViewModel and call setArea(area)

    // TODO: Bind widgets to ViewModel properties
    //   - Use bind() for display-only Labels
    //   - Use bindBidirectional() for TextFields (so user input flows to the ViewModel)
    //   - Use setItems() for ListViews and ComboBoxes

    // TODO: Set up listeners (e.g., ComboBox selection, slider changes)
    //   See AreaDashboardController for the ComboBox listener pattern.
  }
}
