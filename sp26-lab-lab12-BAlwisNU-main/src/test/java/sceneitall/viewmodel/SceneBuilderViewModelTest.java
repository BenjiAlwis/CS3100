package sceneitall.viewmodel;

import static org.assertj.core.api.Assertions.assertThat;

import org.jspecify.annotations.NullUnmarked;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import sceneitall.model.Area;
import sceneitall.model.Fan;
import sceneitall.model.Light;
import sceneitall.model.Shade;

/**
 * ViewModel tests for the Scene Builder task.
 *
 * <p>See {@link AreaDashboardViewModelTest} for the testing pattern. No JavaFX runtime needed —
 * just create the ViewModel, call methods, and assert on property values.
 */
@NullUnmarked // fields are initialized in @BeforeEach, not the constructor
class SceneBuilderViewModelTest {

  private Area testArea;
  private SceneBuilderViewModel viewModel;

  @BeforeEach
  void setUp() {
    testArea = new Area("Living Room");
    testArea.addDevice(new Light("Ceiling Light"));
    testArea.addDevice(new Fan("Standing Fan"));
    testArea.addDevice(new Shade("Window Shade"));

    viewModel = new SceneBuilderViewModel();
    viewModel.setArea(testArea);
  }

  @Test
  @DisplayName("setArea populates available devices")
  void setAreaPopulatesDevices() {
    assertThat(viewModel.getAvailableDevices())
        .containsExactly("Ceiling Light", "Standing Fan", "Window Shade");
  }

  // TODO: Write a test for one of the methods you implement.
  // Example: test that addDeviceToScene() adds an entry to sceneDevices.
  //
  //   @Test
  //   @DisplayName("addDeviceToScene adds entry to scene devices list")
  //   void addDeviceToSceneAddsEntry() {
  //       viewModel.selectedDeviceProperty().set("Ceiling Light");
  //       viewModel.targetValueProperty().set(30);
  //
  //       viewModel.addDeviceToScene();
  //
  //       assertThat(viewModel.getSceneDevices()).hasSize(1);
  //       assertThat(viewModel.getSceneDevices().get(0)).contains("Ceiling Light");
  //   }
  //
  // See AreaDashboardViewModelTest for more examples.
}
