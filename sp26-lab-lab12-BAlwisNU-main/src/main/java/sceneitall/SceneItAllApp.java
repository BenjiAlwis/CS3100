package sceneitall;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Main application class for SceneItAll.
 *
 * <p>Change the FXML path below to load your chosen task's FXML.
 */
public class SceneItAllApp extends Application {

  // TODO: Change this to your task's FXML:
  //   "/scene-builder-task.fxml"
  //   "/device-setup-task.fxml"
  //   "/schedule-task.fxml"
  private static final String FXML_PATH = "/area-dashboard.fxml";

  @Override
  public void start(Stage stage) throws Exception {
    FXMLLoader loader = new FXMLLoader(getClass().getResource(FXML_PATH));
    Scene scene = new Scene(loader.load(), 800, 600);
    stage.setTitle("SceneItAll");
    stage.setScene(scene);
    stage.show();
  }

  public static void main(String[] args) {
    launch(args);
  }
}
