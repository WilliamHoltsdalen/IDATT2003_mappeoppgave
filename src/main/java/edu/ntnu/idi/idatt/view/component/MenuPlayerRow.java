package edu.ntnu.idi.idatt.view.component;

import java.util.List;

import org.kordamp.ikonli.javafx.FontIcon;

import edu.ntnu.idi.idatt.factory.view.PlayerTokenFactory;
import edu.ntnu.idi.idatt.model.player.PlayerTokenType;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import javafx.stage.Popup;

public class MenuPlayerRow extends HBox {
  TextField nameTextField;
  Button playerButton;
  Shape playerToken;
  Button deleteButton;
  Color color;
  PlayerTokenType tokenType;
  List<PlayerTokenType> allowedPlayerTokenTypes;
  List<String> allowedPlayerColors;
  boolean removable;

  Runnable onUpdate;

  public MenuPlayerRow(String defaultName, Color color, PlayerTokenType playerTokenType, List<PlayerTokenType> allowedPlayerTokenTypes, List<String> allowedPlayerColors, boolean removable) {
    this.color = color;
    this.tokenType = playerTokenType;
    this.allowedPlayerTokenTypes = allowedPlayerTokenTypes;
    this.allowedPlayerColors = allowedPlayerColors;
    this.removable = removable;
    this.getStyleClass().add("main-menu-player-row");
    initialize(defaultName, playerTokenType);
  }

  private void initialize(String defaultName, PlayerTokenType playerTokenType) {
    playerToken = PlayerTokenFactory.create(7, color, playerTokenType);

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
    popup.setAutoHide(true);
    popup.setAutoFix(true);

    Text popupPickColorText = new Text("Pick a color for " + getName());

    final Node colorPickerNode;
    if (allowedPlayerColors.isEmpty()) {
      ColorPicker colorPicker = new ColorPicker(getColor());
      colorPicker.setOnAction(event -> {
        color = colorPicker.getValue();
        updatePlayerToken();
        popup.hide();
      });
      colorPickerNode = colorPicker;
    } else {
      MenuButton colorMenuButton = new MenuButton();
      allowedPlayerColors.forEach(allowedColor -> {
        MenuItem colorMenuItem = new MenuItem();
        colorMenuItem.setGraphic(new Rectangle(10, 10, Color.web(allowedColor)));
        colorMenuItem.setText(allowedColor);
        colorMenuButton.getItems().add(colorMenuItem);
        colorMenuItem.setOnAction(event -> {
          colorMenuButton.setText(colorMenuItem.getText());
          colorMenuButton.setGraphic(colorMenuItem.getGraphic());
          color = (Color) ((Rectangle) colorMenuItem.getGraphic()).getFill();
          updatePlayerToken();
          popup.hide();
        });
      });
      MenuItem initialColorMenuItem = colorMenuButton.getItems().stream().filter(item -> ((Rectangle) item.getGraphic()).getFill().equals(getColor())).findFirst().orElse(colorMenuButton.getItems().get(0));
      colorMenuButton.setText(initialColorMenuItem.getText());
      colorMenuButton.setGraphic(initialColorMenuItem.getGraphic());
      colorMenuButton.setOnAction(event -> {

      });
      colorPickerNode = colorMenuButton;
    }

    HorizontalDivider horizontalDivider = new HorizontalDivider();

    Text popupPickTokenTypeText = new Text("Pick a token for " + getName());
    ComboBox<PlayerTokenType> tokenTypeComboBox = new ComboBox<>();
    tokenTypeComboBox.getItems().addAll(allowedPlayerTokenTypes);
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

    VBox innerPopupContent;
    if (allowedPlayerTokenTypes.size() == 1) {
      innerPopupContent = new VBox(popupPickColorText, colorPickerNode);
    } else {
      innerPopupContent = new VBox(popupPickColorText, colorPickerNode, horizontalDivider,
          popupPickTokenTypeText, tokenTypeComboBox);
    }
    innerPopupContent.setSpacing(10);

    HBox popupContent = new HBox(innerPopupContent, closePopupButton);
    popupContent.getStyleClass().add("main-menu-player-color-picker-popup");
    popup.getContent().setAll(popupContent);
    popup.show(this.getScene().getWindow(), xPos, yPos);
  }

  private void updatePlayerToken() {
    playerToken = PlayerTokenFactory.create(7, color, tokenType);
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
