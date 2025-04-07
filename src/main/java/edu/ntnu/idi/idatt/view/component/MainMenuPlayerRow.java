package edu.ntnu.idi.idatt.view.component;

import edu.ntnu.idi.idatt.model.PlayerTokenType;
import edu.ntnu.idi.idatt.view.factory.PlayerTokenFactory;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import javafx.stage.Popup;
import org.kordamp.ikonli.javafx.FontIcon;

public class MainMenuPlayerRow extends HBox {
  TextField nameTextField;
  Button playerButton;
  Shape playerToken;
  Button deleteButton;
  Color color;
  PlayerTokenType tokenType;
  boolean removable;

  Runnable onUpdate;

  public MainMenuPlayerRow(String defaultName, Color color, PlayerTokenType playerTokenType, boolean removable) {
    this.color = color;
    this.tokenType = playerTokenType;
    this.removable = removable;
    this.getStyleClass().add("main-menu-player-row");
    initialize(defaultName, playerTokenType);
  }

  private void initialize(String defaultName, PlayerTokenType playerTokenType) {
    playerToken = PlayerTokenFactory.create(10, color, playerTokenType);

    playerButton = new Button();
    playerButton.setGraphic(playerToken);
    playerButton.getStyleClass().add("icon-only-button");
    playerButton.setOnMouseClicked(mouseEvent -> showPlayerColorPickerPopup(mouseEvent.getScreenX(), mouseEvent.getScreenY()));

    nameTextField = new TextField(defaultName);
    deleteButton = new Button();
    deleteButton.setGraphic(new FontIcon("fas-trash"));
    deleteButton.getStyleClass().add("icon-only-button");
    deleteButton.setVisible(removable);

    this.getChildren().addAll(playerButton, nameTextField, deleteButton);
  }

  private void showPlayerColorPickerPopup(double xPos, double yPos) {
    Popup popup = new Popup();
    popup.setHideOnEscape(true);
    popup.setAutoFix(true);

    Text popupPickColorText = new Text("Pick a color for " + getName());
    ColorPicker colorPicker = new ColorPicker(getColor());
    colorPicker.setOnAction(event -> {
      color = colorPicker.getValue();
      updatePlayerToken();
      popup.hide();
    });

    HorizontalDivider horizontalDivider = new HorizontalDivider();

    Text popupPickTokenTypeText = new Text("Pick a token for" + getName());
    ComboBox<PlayerTokenType> tokenTypeComboBox = new ComboBox<>();
    tokenTypeComboBox.getItems().addAll(PlayerTokenType.values());
    tokenTypeComboBox.setValue(tokenType);
    tokenTypeComboBox.setOnAction(event -> {
      tokenTypeComboBox.getSelectionModel().select(tokenTypeComboBox.getValue());
      tokenType = tokenTypeComboBox.getValue();
      updatePlayerToken();
      popup.hide();
    });

    Button closePopupButton = new Button();
    closePopupButton.setAlignment(Pos.TOP_RIGHT);
    closePopupButton.setGraphic(new FontIcon("fas-times"));
    closePopupButton.getStyleClass().add("icon-only-button");
    closePopupButton.setTooltip(new Tooltip("Close"));
    closePopupButton.setOnAction(event -> popup.hide());

    VBox innerPopupContent = new VBox(popupPickColorText, colorPicker, horizontalDivider,
        popupPickTokenTypeText, tokenTypeComboBox);
    innerPopupContent.setSpacing(10);

    HBox popupContent = new HBox(innerPopupContent, closePopupButton);
    popupContent.getStyleClass().add("main-menu-player-color-picker-popup");
    popup.getContent().setAll(popupContent);
    popup.show(this.getScene().getWindow(), xPos, yPos);
  }

  private void updatePlayerToken() {
    playerToken = PlayerTokenFactory.create(10, color, tokenType);
    playerButton.setGraphic(playerToken);

    if (onUpdate != null) {
      onUpdate.run();
    }
  }

  public void setOnDelete(Runnable deleteRowAction) {
    deleteButton.setOnAction(event -> deleteRowAction.run());
  }

  public void setOnUpdate(Runnable onUpdate) {
    this.onUpdate = onUpdate;
  }

  public String getName() {
    return nameTextField.getText();
  }

  public PlayerTokenType getPlayerTokenType() {
    return tokenType;
  }

  public Color getColor() {
    return color;
  }
}
