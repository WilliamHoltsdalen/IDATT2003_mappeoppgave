package edu.ntnu.idi.idatt.view.gui.container;

import javafx.scene.layout.HBox;

public class GameView extends HBox {

  public GameView() {

    initialize();
  }

  private void initialize() {

    this.getChildren().setAll();
  }

  public HBox getView() {
    return this;
  }

}
