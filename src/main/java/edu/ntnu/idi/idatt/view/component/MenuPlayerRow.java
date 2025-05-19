package edu.ntnu.idi.idatt.view.component;

import edu.ntnu.idi.idatt.factory.view.PlayerTokenFactory;
import edu.ntnu.idi.idatt.model.player.PlayerTokenType;
import edu.ntnu.idi.idatt.view.common.MenuView;
import java.util.List;
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
import org.kordamp.ikonli.javafx.FontIcon;

/**
 * <h3>MenuPlayerRow.</h3>
 *
 * <p>A JavaFX component representing a single player's configuration row in a menu.</p>
 *
 * <p>It displays the player's name (editable), their token (clickable to change color/type),
 * and an optional delete button. This component is typically used within a {@link MenuView}.</p>
 *
 * @see HBox
 * @see PlayerTokenFactory
 * @see PlayerTokenType
 * @see MenuView
 */
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
  boolean isBot;

  /**
   * Runnable action to execute when player details (color, token type) are updated.
   */
  Runnable onUpdate;

  /**
   * Constructs a MenuPlayerRow.
   *
   * @param defaultName The initial name for the player.
   * @param color The initial color for the player's token.
   * @param playerTokenType The initial token type for the player.
   * @param allowedPlayerTokenTypes A list of {@link PlayerTokenType}s that the player can choose
   *                                from.
   * @param allowedPlayerColors A list of hex color strings that the player can choose from. If
   *                            empty, a full ColorPicker is shown.
   * @param removable Whether this player row can be deleted.
   * @param isBot Whether this player is a bot.
   */
  public MenuPlayerRow(String defaultName, Color color, PlayerTokenType playerTokenType,
      List<PlayerTokenType> allowedPlayerTokenTypes, List<String> allowedPlayerColors,
      boolean removable, boolean isBot) {
    this.color = color;
    this.tokenType = playerTokenType;
    this.allowedPlayerTokenTypes = allowedPlayerTokenTypes;
    this.allowedPlayerColors = allowedPlayerColors;
    this.removable = removable;
    this.isBot = isBot;

    this.getStylesheets().add("stylesheets/menuStyles.css");
    this.getStyleClass().add("menu-player-row");
    initialize(defaultName, playerTokenType);
  }

  /**
   * Initializes the components of the player row, including the token, name field, and delete
   * button.
   *
   * @param defaultName The default name to display in the text field.
   * @param playerTokenType The initial {@link PlayerTokenType} for the player's token.
   */
  private void initialize(String defaultName, PlayerTokenType playerTokenType) {
    playerToken = PlayerTokenFactory.create(7, color, playerTokenType);

    playerButton = new Button();
    playerButton.setGraphic(playerToken);
    playerButton.getStyleClass().add("icon-only-button");
    playerButton.setOnMouseClicked(
        mouseEvent -> showPlayerColorPickerPopup(mouseEvent.getScreenX(), mouseEvent.getScreenY()));

    nameTextField = new TextField(defaultName);
    deleteButton = new Button();
    deleteButton.setGraphic(new FontIcon("fas-trash"));
    deleteButton.getStyleClass().add("icon-only-button");
    deleteButton.setVisible(removable);

    this.getChildren().addAll(playerButton, nameTextField, deleteButton);
  }

  /**
   * Shows a popup dialog for picking the player's token color and type.
   * The popup is positioned near the mouse click that triggered it.
   *
   * @param xPos The screen x-coordinate for positioning the popup.
   * @param yPos The screen y-coordinate for positioning the popup.
   */
  private void showPlayerColorPickerPopup(double xPos, double yPos) {
    Popup popup = new Popup();
    popup.setHideOnEscape(true);
    popup.setAutoHide(true);
    popup.setAutoFix(true);

    final Text popupPickColorText = new Text("Pick a color for " + getName());

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
      MenuItem initialColorMenuItem = colorMenuButton.getItems().stream()
          .filter(item -> ((Rectangle) item.getGraphic()).getFill().equals(getColor())).findFirst()
          .orElse(colorMenuButton.getItems().get(0));
      colorMenuButton.setText(initialColorMenuItem.getText());
      colorMenuButton.setGraphic(initialColorMenuItem.getGraphic());
      colorMenuButton.setOnAction(event -> {

      });
      colorPickerNode = colorMenuButton;
    }

    final HorizontalDivider horizontalDivider = new HorizontalDivider();

    final Text popupPickTokenTypeText = new Text("Pick a token for " + getName());
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
    popupContent.getStyleClass().add("color-picker-popup");
    popup.getContent().setAll(popupContent);
    popup.show(this.getScene().getWindow(), xPos, yPos);
  }

  /**
   * Updates the player's token appearance based on the current {@link #color} and {@link #tokenType}.
   * Also triggers the {@link #onUpdate} runnable if it is set.
   */
  private void updatePlayerToken() {
    playerToken = PlayerTokenFactory.create(7, color, tokenType);
    playerButton.setGraphic(playerToken);

    if (onUpdate != null) {
      onUpdate.run();
    }
  }

  /**
   * Sets the action to be performed when the delete button for this row is clicked.
   *
   * @param deleteRowAction The {@link Runnable} to execute on delete.
   */
  public void setOnDelete(Runnable deleteRowAction) {
    deleteButton.setOnAction(event -> deleteRowAction.run());
  }

  /**
   * Sets a callback to be executed when the player's details (color or token type) are updated.
   *
   * @param onUpdate The {@link Runnable} to execute on update.
   */
  public void setOnUpdate(Runnable onUpdate) {
    this.onUpdate = onUpdate;
  }

  /**
   * Gets the current name of the player from the text field.
   *
   * @return The player's name.
   */
  public String getName() {
    return nameTextField.getText();
  }

  /**
   * Gets the currently selected {@link PlayerTokenType} for this player.
   *
   * @return The player's token type.
   */
  public PlayerTokenType getPlayerTokenType() {
    return tokenType;
  }

  /**
   * Gets the current {@link Color} selected for this player's token.
   *
   * @return The player's token color.
   */
  public Color getColor() {
    return color;
  }

  /**
   * Checks if this player is configured as a bot.
   *
   * @return True if the player is a bot, false otherwise.
   */
  public boolean isBot() {
    return isBot;
  }
}
