package edu.ntnu.idi.idatt.view.gui.component;

import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import org.kordamp.ikonli.javafx.FontIcon;

public class PlayerRow extends HBox {
  TextField nameTextField;
  Circle playerCircle;
  Button deleteButton;
  boolean removable;

  public PlayerRow(String defaultName, Color color, boolean removable) {
    this.removable = removable;
    this.getStyleClass().add("main-menu-player-row");
    initialize(defaultName, color);
  }

  private void initialize(String defaultName, Color color) {
    playerCircle = new Circle(10, Color.TRANSPARENT);
    playerCircle.setStroke(color);
    playerCircle.setStrokeWidth(8);
    playerCircle.getStyleClass().add("main-menu-player-circle");
    nameTextField = new TextField(defaultName);
    deleteButton = new Button();
    deleteButton.setGraphic(new FontIcon("fas-trash"));
    deleteButton.getStyleClass().add("main-menu-delete-player-button");

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
