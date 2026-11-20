package sceneitall.viewmodel;

import java.util.List;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import sceneitall.model.Area;
import sceneitall.model.Scene;

/**
 * ViewModel for the Schedule &amp; Automation task — create rules like "at sunset, activate Evening
 * in Living Room".
 *
 * <p>Properties are declared for you. Implement the TODO methods to make the GUI functional. See
 * {@link AreaDashboardViewModel} for the pattern.
 */
public class ScheduleViewModel {

  // --- Observable properties ---

  private final ObservableList<String> triggerOptions =
      FXCollections.observableArrayList("Sunrise", "Sunset", "Time...", "Device changes");
  private final ObservableList<String> sceneOptions = FXCollections.observableArrayList();
  private final ObservableList<String> areaOptions = FXCollections.observableArrayList();
  private final StringProperty selectedTrigger = new SimpleStringProperty("");
  private final StringProperty selectedScene = new SimpleStringProperty("");
  private final StringProperty selectedArea = new SimpleStringProperty("");
  private final ObservableList<String> rules = FXCollections.observableArrayList();
  private final StringProperty statusMessage = new SimpleStringProperty("");

  public ScheduleViewModel() {
    // No backing model needed until setModel is called
  }

  /**
   * Connects this ViewModel to the home model. Populates the scene and area option lists.
   *
   * @param areas the list of areas (rooms) in the home
   */
  public void setModel(List<Area> areas) {
    areaOptions.clear();
    sceneOptions.clear();
    for (Area area : areas) {
      areaOptions.add(area.getName());
      for (Scene scene : area.getScenes()) {
        sceneOptions.add(scene.getName());
      }
    }
    statusMessage.set("");
  }

  /**
   * Sets the trigger for the automation rule.
   *
   * @param trigger the trigger name (e.g., "Sunset", "Sunrise")
   */
  public void setTrigger(String trigger) {
    // TODO: implement setTrigger
    throw new UnsupportedOperationException("TODO: implement setTrigger");
  }

  /**
   * Sets the action for the automation rule — which scene to activate in which area.
   *
   * <p>Note: In the Controller, scene and area are selected via separate ComboBoxes. Each
   * ComboBox's listener should call this method with both values — passing the current value of the
   * other ComboBox (or ViewModel property) as the second argument.
   *
   * @param sceneName the scene to activate
   * @param areaName the area to activate it in
   */
  public void setAction(String sceneName, String areaName) {
    // TODO: implement setAction
    throw new UnsupportedOperationException("TODO: implement setAction");
  }

  /**
   * Saves the current rule. Validates that trigger, scene, and area are all selected. Formats a
   * rule string like "At Sunset: activate Evening in Living Room", adds it to {@link #getRules()},
   * and clears the selections.
   *
   * @throws IllegalStateException if trigger, scene, or area is not selected
   */
  public void saveRule() {
    // TODO: implement saveRule
    throw new UnsupportedOperationException("TODO: implement saveRule");
  }

  // --- Property accessors ---

  public ObservableList<String> getTriggerOptions() {
    return triggerOptions;
  }

  public ObservableList<String> getSceneOptions() {
    return sceneOptions;
  }

  public ObservableList<String> getAreaOptions() {
    return areaOptions;
  }

  public StringProperty selectedTriggerProperty() {
    return selectedTrigger;
  }

  public StringProperty selectedSceneProperty() {
    return selectedScene;
  }

  public StringProperty selectedAreaProperty() {
    return selectedArea;
  }

  public ObservableList<String> getRules() {
    return rules;
  }

  public StringProperty statusMessageProperty() {
    return statusMessage;
  }
}
