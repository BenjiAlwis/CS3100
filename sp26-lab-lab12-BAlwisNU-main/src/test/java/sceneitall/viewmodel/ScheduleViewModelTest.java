package sceneitall.viewmodel;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;

import org.jspecify.annotations.NullUnmarked;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import sceneitall.model.Area;
import sceneitall.model.Scene;

/**
 * ViewModel tests for the Schedule &amp; Automation task.
 *
 * <p>See {@link AreaDashboardViewModelTest} for the testing pattern. No JavaFX runtime needed —
 * just create the ViewModel, call methods, and assert on property values.
 */
@NullUnmarked // fields are initialized in @BeforeEach, not the constructor
class ScheduleViewModelTest {

  private List<Area> testAreas;
  private ScheduleViewModel viewModel;

  @BeforeEach
  void setUp() {
    Area livingRoom = new Area("Living Room");
    livingRoom.addScene(new Scene("Evening", Map.of("Ceiling Light", 30)));
    livingRoom.addScene(new Scene("Movie Night", Map.of("Ceiling Light", 10)));

    Area bedroom = new Area("Bedroom");
    bedroom.addScene(new Scene("Sleep", Map.of("Bedside Lamp", 0)));

    testAreas = List.of(livingRoom, bedroom);

    viewModel = new ScheduleViewModel();
    viewModel.setModel(testAreas);
  }

  @Test
  @DisplayName("setModel populates scene and area options")
  void setModelPopulatesOptions() {
    assertThat(viewModel.getAreaOptions()).containsExactly("Living Room", "Bedroom");
    assertThat(viewModel.getSceneOptions()).containsExactly("Evening", "Movie Night", "Sleep");
  }

  // TODO: Write a test for one of the methods you implement.
  // Example: test that saveRule() adds a formatted rule to the list.
  //
  //   @Test
  //   @DisplayName("saveRule adds formatted rule to list")
  //   void saveRuleAddsToList() {
  //       viewModel.setTrigger("Sunset");
  //       viewModel.setAction("Evening", "Living Room");
  //
  //       viewModel.saveRule();
  //
  //       assertThat(viewModel.getRules()).hasSize(1);
  //       assertThat(viewModel.getRules().get(0)).contains("Sunset");
  //   }
  //
  // See AreaDashboardViewModelTest for more examples.
}
