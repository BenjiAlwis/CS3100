package sceneitall.viewmodel;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.jspecify.annotations.NullUnmarked;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import sceneitall.model.Area;

/**
 * ViewModel tests for the Device Setup task.
 *
 * <p>See {@link AreaDashboardViewModelTest} for the testing pattern. No JavaFX runtime needed —
 * just create the ViewModel, call methods, and assert on property values.
 */
@NullUnmarked // fields are initialized in @BeforeEach, not the constructor
class DeviceSetupViewModelTest {

  private List<Area> testAreas;
  private DeviceSetupViewModel viewModel;

  @BeforeEach
  void setUp() {
    testAreas = List.of(new Area("Living Room"), new Area("Bedroom"), new Area("Kitchen"));

    viewModel = new DeviceSetupViewModel();
    viewModel.setModel(testAreas);
  }

  @Test
  @DisplayName("setModel populates available rooms")
  void setModelPopulatesRooms() {
    assertThat(viewModel.getAvailableRooms()).containsExactly("Living Room", "Bedroom", "Kitchen");
  }

  // TODO: Write a test for one of the methods you implement.
  // Example: test that assignToRoom() updates the assigned room property.
  //
  //   @Test
  //   @DisplayName("assignToRoom updates room property")
  //   void assignToRoomUpdatesProperty() {
  //       viewModel.selectDevice("New Light");
  //
  //       viewModel.assignToRoom("Bedroom");
  //
  //       assertThat(viewModel.assignedRoomProperty().get()).isEqualTo("Bedroom");
  //   }
  //
  // See AreaDashboardViewModelTest for more examples.
}
