package sceneitall.controller;

import javafx.fxml.FXML;

import org.jspecify.annotations.NullUnmarked;

/**
 * Controller for the Device Setup view.
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
public class DeviceSetupController {

  // TODO: Add @FXML fields here
  //       Each field name must match an fx:id in your FXML.

  // TODO: Create your ViewModel field (e.g., private DeviceSetupViewModel viewModel;)

  /**
   * Called by FXMLLoader after all @FXML fields are injected.
   *
   * <p>IMPORTANT: Implement your ViewModel TODO methods BEFORE wiring listeners here, otherwise
   * listeners that fire during initialization will throw UnsupportedOperationException.
   */
  @FXML
  private void initialize() {
    // TODO: Build sample model data — create 2-3 Areas representing rooms, e.g.:
    //   List<Area> areas = List.of(
    //       new Area("Living Room"), new Area("Bedroom"), new Area("Kitchen"));

    // TODO: Create ViewModel and call setModel(areas)

    // TODO: Bind widgets to ViewModel properties
    //   - Use bind() for display-only Labels
    //   - Use bindBidirectional() for TextFields (so user input flows to the ViewModel)
    //   - Use setItems() for ListViews and ComboBoxes

    // TODO: Set up listeners (e.g., ComboBox selection, ListView selection)
    //   See AreaDashboardController for the ComboBox listener pattern:
    //   comboBox.getSelectionModel().selectedItemProperty().addListener(...)
  }
}
