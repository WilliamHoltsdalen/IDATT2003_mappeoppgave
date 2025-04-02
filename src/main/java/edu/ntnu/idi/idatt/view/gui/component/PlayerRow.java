package edu.ntnu.idi.idatt.view.gui.component;

import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class PlayerRow extends HBox {
  TextField nameTextField;
  Circle playerCircle;
  Button deleteButton;
  boolean removable;

  public PlayerRow(String defaultName, Color color, boolean removable) {
    this.removable = removable;
    this.getStyleClass().add("player-row");
    initialize(defaultName, color);
  }

  private void initialize(String defaultName, Color color) {
    playerCircle = new Circle(14, color);
    nameTextField = new TextField(defaultName);
    deleteButton = new Button("🗑️");

    this.getChildren().addAll(playerCircle, nameTextField);

    if (removable) {
      this.getChildren().add(deleteButton);
    }
  }

  public void setRunnable(Runnable deleteRowAction) {
    deleteButton.setOnAction(event -> deleteRowAction.run());
  }

  public String getName() {
    return nameTextField.getText();
  }
}
