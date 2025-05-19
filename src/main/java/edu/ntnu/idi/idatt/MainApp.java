package edu.ntnu.idi.idatt;

import edu.ntnu.idi.idatt.navigation.ViewNavigator;
import edu.ntnu.idi.idatt.navigation.ViewType;
import edu.ntnu.idi.idatt.view.app.AppView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <h3>MainApp.</h3>
 *
 * <p>The main entry point for the board game application. This class extends
 * {@link Application} and is responsible for initializing the primary stage,
 * setting up the main scene with global styles, and managing the overall application flow through
 * a {@link ViewNavigator} and an {@link AppView} container.</p>
 *
 * <p>Upon starting, it configures the main window, loads the initial game selection screen,
 * and makes the application visible.</p>
 *
 * @see javafx.application.Application
 * @see ViewNavigator
 * @see AppView
 * @see Scene
 * @see Stage
 */
public class MainApp extends Application {

  private static final String APP_NAME = "Board Game Application";
  private static final Logger logger = LoggerFactory.getLogger(MainApp.class);
  private AppView appView;
  private ViewNavigator viewNavigator;
  private BorderPane root;

  /**
   * The main method, which serves as the entry point for the Java application.
   * It calls {@link #launch(String...)} to start the JavaFX application lifecycle.
   *
   * @param args Command line arguments passed to the application (not used in this app).
   */
  public static void main(String[] args) {
    launch(args);
  }

  /**
   * The main entry point. This method is called after the application has been launched by 
   * {@link #main(String[])} and the system is ready for the application to begin running.
   *
   * <p>It initializes the {@link AppView} and {@link ViewNavigator}, sets up the root layout
   * (a {@link BorderPane}), shows the initial game selection view, creates the main scene with
   * global stylesheets, and configures the primary stage (window).</p>
   *
   * @param primaryStage The primary stage for this application, onto which the application scene
   *                     can be set.
   */
  @Override
  public void start(Stage primaryStage) {
    logger.info("Starting {}", APP_NAME);
    appView = new AppView();
    viewNavigator = new ViewNavigator(appView);
    root = new BorderPane();

    showGameSelection();

    Scene scene = new Scene(root, 1280, 720);
    scene.getStylesheets().add("stylesheets/globalStyles.css");

    primaryStage.setTitle(APP_NAME);
    primaryStage.setScene(scene);
    primaryStage.show();
    logger.info("{} started successfully", APP_NAME);
  }

  /**
   * Navigates the application to display the game selection screen.
   * It sets the {@link AppView} as the center content of the root layout and then
   * uses the {@link ViewNavigator} to switch to the {@link ViewType#GAME_SELECTION} view.
   */
  public void showGameSelection() {
    logger.debug("Showing game selection");
    root.setCenter(appView);
    viewNavigator.navigateTo(ViewType.GAME_SELECTION);
  }
}