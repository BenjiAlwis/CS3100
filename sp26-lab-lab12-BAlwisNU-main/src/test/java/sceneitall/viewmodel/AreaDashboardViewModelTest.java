package sceneitall.viewmodel;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;

import org.jspecify.annotations.NullUnmarked;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import sceneitall.model.Area;
import sceneitall.model.Fan;
import sceneitall.model.Light;
import sceneitall.model.Scene;
import sceneitall.model.Shade;

/**
 * ViewModel tests for the Area Dashboard — a complete worked example.
 *
 * <p>These tests demonstrate the key pattern: you can test ViewModel logic <b>without starting the
 * JavaFX runtime</b>. Just create the ViewModel, call methods, and assert on property values.
 *
 * <p><b>Read these tests as a reference.</b> Your task's ViewModel test follows the same pattern.
 */
@NullUnmarked // fields are initialized in @BeforeEach, not the constructor
class AreaDashboardViewModelTest {

  private Area testArea;
  private AreaDashboardViewModel viewModel;

  @BeforeEach
  void setUp() {
    // Build a test area with known devices and scenes
    testArea = new Area("Living Room");
    testArea.addDevice(new Light("Ceiling Light"));
    testArea.addDevice(new Fan("Standing Fan"));
    testArea.addDevice(new Shade("Window Shade"));

    testArea.addScene(new Scene("Movie Night", Map.of("Ceiling Light", 20, "Window Shade", 0)));
    testArea.addScene(
        new Scene("Morning", Map.of("Ceiling Light", 100, "Window Shade", 100, "Standing Fan", 2)));

    viewModel = new AreaDashboardViewModel();
    viewModel.setArea(testArea);
  }

  @Test
  @DisplayName("setArea populates the area name property")
  void setAreaPopulatesName() {
    assertEquals("Living Room", viewModel.areaNameProperty().get());
  }

  @Test
  @DisplayName("setArea populates the device descriptions list")
  void setAreaPopulatesDevices() {
    assertEquals(3, viewModel.getDeviceDescriptions().size());
  }

  @Test
  @DisplayName("setArea populates the scene names list")
  void setAreaPopulatesScenes() {
    assertThat(viewModel.getSceneNames()).containsExactly("Movie Night", "Morning");
  }

  @Test
  @DisplayName("activateScene applies target states to devices")
  void activateSceneAppliesTargets() {
    viewModel.selectedSceneNameProperty().set("Movie Night");

    viewModel.activateScene();

    Light light = (Light) testArea.findDevice("Ceiling Light").orElseThrow();
    assertEquals(20, light.getBrightness());

    Shade shade = (Shade) testArea.findDevice("Window Shade").orElseThrow();
    assertEquals(0, shade.getPosition());
  }

  @Test
  @DisplayName("activateScene updates the status message")
  void activateSceneUpdatesStatus() {
    viewModel.selectedSceneNameProperty().set("Movie Night");

    viewModel.activateScene();

    assertThat(viewModel.statusMessageProperty().get()).contains("Movie Night");
  }

  @Test
  @DisplayName("updateBrightness changes the controlled light")
  void updateBrightnessChangesLight() {
    viewModel.updateBrightness(42);

    Light light = (Light) testArea.findDevice("Ceiling Light").orElseThrow();
    assertEquals(42, light.getBrightness());
  }

  @Test
  @DisplayName("activateScene with no selection shows an error")
  void activateSceneWithNoSelection() {
    viewModel.selectedSceneNameProperty().set("");

    viewModel.activateScene();

    assertThat(viewModel.statusMessageProperty().get()).contains("Select a scene");
  }
}
