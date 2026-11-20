package sceneitall.controller;

import java.util.Map;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;

import org.jspecify.annotations.NullUnmarked;

import sceneitall.model.Area;
import sceneitall.model.Fan;
import sceneitall.model.Light;
import sceneitall.model.Scene;
import sceneitall.model.Shade;
import sceneitall.viewmodel.AreaDashboardViewModel;

/**
 * Controller for the Area Dashboard — a complete worked example from L29.
 *
 * <p>This class demonstrates the Controller's role in MVVM:
 *
 * <ol>
 *   <li>Declare {@code @FXML} fields matching the {@code fx:id} values in the FXML
 *   <li>In {@code initialize()}, create and configure the ViewModel, then bind widgets to it
 *   <li>Handler methods (like {@link #handleActivateScene()}) delegate to the ViewModel
 * </ol>
 *
 * <p><b>Read this class as a reference.</b> Your task Controller follows the same pattern.
 */
// @NullUnmarked: @FXML fields are injected by FXMLLoader at runtime, not in the constructor.
// Static analysis can't model framework injection, so null analysis is disabled for this class.
@NullUnmarked
public class AreaDashboardController {

  // --- @FXML fields (injected by FXMLLoader from fx:id values) ---

  @FXML private Label areaNameLabel;
  @FXML private ListView<String> deviceListView;
  @FXML private ComboBox<String> sceneComboBox;
  @FXML private Slider brightnessSlider;
  @FXML private Label brightnessValueLabel;
  @FXML private Button activateButton;
  @FXML private Label statusLabel;

  // --- ViewModel ---

  private AreaDashboardViewModel viewModel;

  /**
   * Called by FXMLLoader AFTER all @FXML fields have been injected.
   *
   * <p>This is where you set up the ViewModel and bind widgets — NOT in the constructor (the @FXML
   * fields are null in the constructor).
   */
  @FXML
  private void initialize() {
    // 1. Build sample model data
    Area area = createSampleArea();

    // 2. Create ViewModel and connect it to the model
    viewModel = new AreaDashboardViewModel();
    viewModel.setArea(area);

    // 3. Bind widgets to ViewModel properties
    //    Use bind() for display-only Labels. For TextFields where user input should
    //    flow back to the ViewModel, use bindBidirectional() instead, e.g.:
    //        textField.textProperty().bindBidirectional(viewModel.someProperty());
    areaNameLabel.textProperty().bind(viewModel.areaNameProperty());
    deviceListView.setItems(viewModel.getDeviceDescriptions());
    sceneComboBox.setItems(viewModel.getSceneNames());
    statusLabel.textProperty().bind(viewModel.statusMessageProperty());

    // 4. Set up the brightness slider
    brightnessSlider.setMin(0);
    brightnessSlider.setMax(100);
    brightnessSlider.setValue(viewModel.selectedLightBrightnessProperty().get());
    brightnessValueLabel.setText(formatBrightness(brightnessSlider.getValue()));

    // When the slider moves, update the ViewModel
    brightnessSlider
        .valueProperty()
        .addListener(
            (obs, oldVal, newVal) -> {
              viewModel.updateBrightness(newVal.intValue());
              brightnessValueLabel.setText(formatBrightness(newVal.doubleValue()));
            });

    // When a scene changes the brightness, update the slider
    viewModel
        .selectedLightBrightnessProperty()
        .addListener(
            (obs, oldVal, newVal) -> {
              brightnessSlider.setValue(newVal.intValue());
              brightnessValueLabel.setText(formatBrightness(newVal.doubleValue()));
            });

    // When the user selects a scene in the ComboBox, update the ViewModel
    sceneComboBox
        .getSelectionModel()
        .selectedItemProperty()
        .addListener(
            (obs, oldVal, newVal) -> {
              if (newVal != null) {
                viewModel.selectedSceneNameProperty().set(newVal);
              }
            });
  }

  /** Handler for the "Activate Scene" button. Delegates to the ViewModel. */
  @FXML
  private void handleActivateScene() {
    viewModel.activateScene();
  }

  // --- Private helpers ---

  private String formatBrightness(double value) {
    return String.valueOf((int) value) + "%";
  }

  /** Creates a sample Area with devices and scenes for demonstration. */
  private Area createSampleArea() {
    Area area = new Area("Living Room");

    // Add some devices
    Light ceilingLight = new Light("Ceiling Light");
    ceilingLight.setOn(true);
    area.addDevice(ceilingLight);

    Fan standingFan = new Fan("Standing Fan");
    area.addDevice(standingFan);

    Shade windowShade = new Shade("Window Shade");
    windowShade.setPosition(50);
    area.addDevice(windowShade);

    // Add some preset scenes
    area.addScene(new Scene("Movie Night", Map.of("Ceiling Light", 20, "Window Shade", 0)));
    area.addScene(
        new Scene("Morning", Map.of("Ceiling Light", 100, "Window Shade", 100, "Standing Fan", 2)));

    return area;
  }
}
