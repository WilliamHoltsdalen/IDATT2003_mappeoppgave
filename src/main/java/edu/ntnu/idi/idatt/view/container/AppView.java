package edu.ntnu.idi.idatt.view.container;

import javafx.scene.Node;
import javafx.scene.layout.StackPane;

public class AppView extends StackPane {

  public AppView() {
    this.getStyleClass().add("app-view");
  }

  public void showView(Node view) {
    this.getChildren().setAll(view);
  }
}
