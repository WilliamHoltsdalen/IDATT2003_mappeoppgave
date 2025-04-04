package edu.ntnu.idi.idatt.view.gui.component;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Popup;
import org.kordamp.ikonli.javafx.FontIcon;

public class MainMenuPlayerRow extends HBox {
  TextField nameTextField;
  Button playerButton;
  Circle playerButtonCircle;
  Button deleteButton;
  Color defaultColor;
  boolean removable;

  public MainMenuPlayerRow(String defaultName, Color defaultColor, boolean removable) {
    this.defaultColor = defaultColor;
    this.removable = removable;
    this.getStyleClass().add("main-menu-player-row");
    initialize(defaultName);
  }

  private void initialize(String defaultName) {
    playerButtonCircle = new Circle(10, Color.TRANSPARENT);
    playerButtonCircle.setStroke(defaultColor);
    playerButtonCircle.setStrokeWidth(8);

    playerButton = new Button();
    playerButton.setGraphic(playerButtonCircle);
    playerButton.getStyleClass().add("icon-only-button");
    playerButton.setOnMouseClicked(mouseEvent -> showPlayerColorPickerPopup(mouseEvent.getScreenX(), mouseEvent.getScreenY()));

    nameTextField = new TextField(defaultName);
    deleteButton = new Button();
    deleteButton.setGraphic(new FontIcon("fas-trash"));
    deleteButton.getStyleClass().add("icon-only-button");

    this.getChildren().addAll(playerButton, nameTextField);

    if (removable) {
      this.getChildren().add(deleteButton);
    }
  }

  private void showPlayerColorPickerPopup(double xPos, double yPos) {
    Popup popup = new Popup();
    popup.setHideOnEscape(true);
    popup.setAutoFix(true);

    Text popupText = new Text("Pick a color for " + getName());
    ColorPicker colorPicker = new ColorPicker(getColor());
    colorPicker.setOnAction(event -> {
      playerButtonCircle.setStroke(colorPicker.getValue());
      popup.hide();
    });

    Button closePopupButton = new Button();
    closePopupButton.setAlignment(Pos.TOP_RIGHT);
    closePopupButton.setGraphic(new FontIcon("fas-times"));
    closePopupButton.getStyleClass().add("icon-only-button");
    closePopupButton.setTooltip(new Tooltip("Close"));
    closePopupButton.setOnAction(event -> popup.hide());

    VBox innerPopupContent = new VBox(popupText, colorPicker);
    innerPopupContent.setSpacing(10);

    HBox popupContent = new HBox(innerPopupContent, closePopupButton);
    popupContent.getStyleClass().add("main-menu-player-color-picker-popup");
    popup.getContent().setAll(popupContent);
    popup.show(this.getScene().getWindow(), xPos, yPos);
  }

  public void setOnDelete(Runnable deleteRowAction) {
    deleteButton.setOnAction(event -> deleteRowAction.run());
  }

  public String getName() {
    return nameTextField.getText();
  }

  public Color getColor() {
    return (Color) playerButtonCircle.getStroke();
  }
}
