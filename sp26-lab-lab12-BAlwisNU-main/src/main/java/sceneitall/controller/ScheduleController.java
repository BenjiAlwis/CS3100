package sceneitall.controller;

import javafx.fxml.FXML;

import org.jspecify.annotations.NullUnmarked;

/**
 * Controller for the Schedule & Automation view.
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
public class ScheduleController {

  // TODO: Add @FXML fields here
  //       Each field name must match an fx:id in your FXML.

  // TODO: Create your ViewModel field (e.g., private ScheduleViewModel viewModel;)

  /**
   * Called by FXMLLoader after all @FXML fields are injected.
   *
   * <p>IMPORTANT: Implement your ViewModel TODO methods BEFORE wiring listeners here, otherwise
   * listeners that fire during initialization will throw UnsupportedOperationException.
   */
  @FXML
  private void initialize() {
    // TODO: Build sample model data — create 2-3 Areas with scenes, e.g.:
    //   Area livingRoom = new Area("Living Room");
    //   livingRoom.addScene(new Scene("Evening", Map.of("Ceiling Light", 30)));
    //   Area bedroom = new Area("Bedroom");
    //   bedroom.addScene(new Scene("Sleep", Map.of("Bedside Lamp", 0)));
    //   List<Area> areas = List.of(livingRoom, bedroom);

    // TODO: Create ViewModel and call setModel(areas)

    // TODO: Bind widgets to ViewModel properties
    //   - Use setItems() for ComboBoxes and ListViews
    //   - Use bind() for the status Label

    // TODO: Set up listeners (e.g., ComboBox selection)
    //   See AreaDashboardController for the ComboBox listener pattern:
    //   comboBox.getSelectionModel().selectedItemProperty().addListener(...)
    //
    //   Note: setAction() takes 2 parameters (scene, area) but you have 2 separate
    //   ComboBoxes. Each listener should call setAction() passing the current value
    //   of the *other* ComboBox as the second argument, e.g.:
    //   sceneCombo.getSelectionModel().selectedItemProperty().addListener((obs, old, val) -> {
    //       if (val != null) viewModel.setAction(val, viewModel.selectedAreaProperty().get());
    //   });
  }
}
