package edu.ntnu.idi.idatt;

import edu.ntnu.idi.idatt.navigation.ViewNavigator;
import edu.ntnu.idi.idatt.navigation.ViewType;
import edu.ntnu.idi.idatt.view.container.AppView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <h3>Main class</h3>
 *
 * <p>This class is the main class of the application. It initializes the GUI and runs it.
 */
public class MainApp extends Application {
  private static final Logger logger = LoggerFactory.getLogger(MainApp.class);
  private AppView appView;
  private ViewNavigator viewNavigator;
  private BorderPane root;

  /**
   * Main method of the application.
   *
   * @param args Command line arguments.
   */
  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage primaryStage) {
    logger.info("start application");
    appView = new AppView();
    viewNavigator = new ViewNavigator(appView);
    root = new BorderPane();

    showMainMenu();

    Scene scene = new Scene(root, 1280, 720);
    scene.getStylesheets().add("stylesheets/styles.css");

    primaryStage.setTitle("Board Game Application"); // TODO: Find a better title for the app
    primaryStage.setScene(scene);
    primaryStage.show();
    logger.info("application started successfully");
  }

  public void showMainMenu() {
    root.setCenter(appView);
    viewNavigator.navigateTo(ViewType.MAIN_MENU);
  }
}