package edu.ntnu.idi.idatt;

import edu.ntnu.idi.idatt.navigation.ViewNavigator;
import edu.ntnu.idi.idatt.navigation.ViewType;
import edu.ntnu.idi.idatt.view.container.AppView;
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
    appView = new AppView();
    viewNavigator = new ViewNavigator(appView);
    root = new BorderPane();

    showMainMenu();

    Scene scene = new Scene(root, 1280, 720);
    scene.getStylesheets().add("stylesheets/styles.css");

    primaryStage.setTitle("Board Game Application"); // TODO: Find a better title for the app
    primaryStage.setScene(scene);
    primaryStage.show();
  }

  public void showMainMenu() {
    root.setCenter(appView);
    viewNavigator.navigateTo(ViewType.MAIN_MENU);
  }
}