package edu.ntnu.idi.idatt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.ntnu.idi.idatt.navigation.ViewNavigator;
import edu.ntnu.idi.idatt.navigation.ViewType;
import edu.ntnu.idi.idatt.view.app.AppView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * <h3>Main class</h3>
 *
 * <p>This class is the main class of the application. It initializes the GUI and runs it.
 */
public class MainApp extends Application {
  private static final String APP_NAME = "Board Game Application";
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
    logger.info("Starting {}", APP_NAME);
    appView = new AppView();
    viewNavigator = new ViewNavigator(appView);
    root = new BorderPane();

    showMainMenu();

    Scene scene = new Scene(root, 1280, 720);
    scene.getStylesheets().add("stylesheets/styles.css");

    primaryStage.setTitle(APP_NAME);
    primaryStage.setScene(scene);
    primaryStage.show();
    logger.info("{} started successfully", APP_NAME);
  }

  public void showMainMenu() {
    logger.debug("Showing main menu");
    root.setCenter(appView);
    viewNavigator.navigateTo(ViewType.MAIN_MENU);
  }
}