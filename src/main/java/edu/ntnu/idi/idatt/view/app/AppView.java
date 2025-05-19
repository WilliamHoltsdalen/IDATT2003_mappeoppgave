package edu.ntnu.idi.idatt.view.app;

import javafx.scene.Node;
import javafx.scene.layout.StackPane;

/**
 * <h3>AppView.</h3>
 *
 * <p>The main view container for the application.</p>
 *
 * <p>It extends {@link StackPane} and is responsible for displaying
 * different views (screens) of the application by replacing its children.</p>
 *
 * @see StackPane
 */
public class AppView extends StackPane {

  /**
   * Constructs a new AppView.
   * Initializes the style class for the view.
   */
  public AppView() {
    this.getStyleClass().add("app-view");
  }

  /**
   * Shows the specified view within this AppView.
   * Any existing view will be replaced.
   *
   * @param view The {@link Node} representing the view to be displayed.
   */
  public void showView(Node view) {
    this.getChildren().setAll(view);
  }
}
