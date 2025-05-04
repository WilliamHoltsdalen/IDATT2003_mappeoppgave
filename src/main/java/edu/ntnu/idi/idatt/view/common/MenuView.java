package edu.ntnu.idi.idatt.view.common;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.kordamp.ikonli.javafx.FontIcon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.ntnu.idi.idatt.model.board.Board;
import edu.ntnu.idi.idatt.model.player.Player;
import edu.ntnu.idi.idatt.model.player.PlayerTokenType;
import edu.ntnu.idi.idatt.observer.ButtonClickObserver;
import edu.ntnu.idi.idatt.observer.ButtonClickSubject;
import edu.ntnu.idi.idatt.view.component.BoardStackPane;
import edu.ntnu.idi.idatt.view.component.MainMenuPlayerRow;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.util.Duration;

/**
 * <h3>Abstract class for all menu views.</h3>
 * 
 * <p>This class provides a base implementation for all menu views.
 * It contains common functionality for all menu views, such as adding and removing player rows,
 * disabling and enabling the start game button, and creating the header box. 
 * 
 * <p>This class extends {@link VBox}, and implements {@link ButtonClickSubject}.
 * 
 * @see VBox
 * @see ButtonClickSubject
 */
public abstract class MenuView extends VBox implements ButtonClickSubject {
  protected static final Logger logger = LoggerFactory.getLogger(MenuView.class);
  private final List<ButtonClickObserver> observers;

  protected VBox playerSelectionBox;
  protected HBox playerSelectionHeader;
  protected final List<MainMenuPlayerRow> mainMenuPlayerRows;
  protected final HBox addPlayerButtonsBox;
  protected final VBox playerListBox;

  protected VBox boardSelectionBox;
  protected HBox boardSelectionHeader;
  protected final BoardStackPane boardStackPane;
  protected final Label boardTitle;
  protected final Label boardDescription;
  protected final Button startGameButton;
  protected Board selectedBoard;

  
  protected MenuView() {
    this.observers = new ArrayList<>();
    this.mainMenuPlayerRows = new ArrayList<>();
    this.playerSelectionHeader = new HBox();
    this.playerListBox = new VBox();
    this.addPlayerButtonsBox = new HBox();
    this.boardTitle = new Label();
    this.boardDescription = new Label();
    this.startGameButton = createStartGameButton();
    this.playerSelectionBox = new VBox();
    this.boardSelectionHeader = new HBox();
    this.boardSelectionBox = new VBox();
    this.boardStackPane = new BoardStackPane();
  }

  public abstract void initialize();

  protected abstract void updateControls();

  public abstract void setPlayers(List<Player> players);

  public abstract void setSelectedBoard(Board board);

  protected abstract VBox createPlayerSelectionBox();

  protected abstract VBox createBoardSelectionBox();

  /**
   * Returns the list of player rows in the main menu.
   *
   * @return The list of player rows in the main menu.
   */
  public List<MainMenuPlayerRow> getPlayerRows() {
    return mainMenuPlayerRows;
  }

  /**
   * Creates a start game button that notifies the observers when clicked. 
   *
   * @return The start game button.
   */
  private Button createStartGameButton() {
    Button button = new Button("Start Game");
    button.setOnAction(event -> notifyObservers("start_game"));
    return button;
  }

  /**
   * Creates a header box with a title, and buttons.
   *
   * @return The header HBox. 
   */
  protected HBox createHeaderBox(String title) {
    Button backButton = new Button("Game selection");
    backButton.setGraphic(new FontIcon("fas-chevron-left"));
    backButton.getStyleClass().add("main-menu-back-button");
    backButton.setOnAction(event -> notifyObservers("back_to_game_selection"));

    Text headerTitle = new Text(title);
    headerTitle.getStyleClass().add("main-menu-title");

    Region spacer = new Region();
    HBox.setHgrow(spacer, Priority.ALWAYS);
    Platform.runLater(() -> spacer.setMaxWidth(backButton.getWidth()));

    HBox headerBox = new HBox(backButton, headerTitle, spacer);
    headerBox.getStyleClass().add("main-menu-header-box");

    return headerBox;
  }

  /**
   * Adds a new player row to the main menu with the given name, color, and removable status.
   *
   * @param defaultName The default name of the player.
   * @param color The color of the player.
   * @param removable The removable status of the player.
   */
  protected void addPlayerRow(String defaultName, Color color, PlayerTokenType playerTokenType, boolean removable) {
    MainMenuPlayerRow mainMenuPlayerRow = new MainMenuPlayerRow(defaultName, color, playerTokenType, removable);
    mainMenuPlayerRow.setOnDelete(() -> removePlayerRow(mainMenuPlayerRow));
    mainMenuPlayerRow.setOnUpdate(this::updateControls);
    mainMenuPlayerRows.add(mainMenuPlayerRow);
    playerListBox.getChildren().add(mainMenuPlayerRow);

    logger.debug("Added player row: {}", mainMenuPlayerRow);
  
    updateControls();
  }

  /**
   * Removes a player row from the main menu.
   *
   * @param mainMenuPlayerRow The player row to remove.
   */
  protected void removePlayerRow(MainMenuPlayerRow mainMenuPlayerRow) {
    mainMenuPlayerRows.remove(mainMenuPlayerRow);
    playerListBox.getChildren().remove(mainMenuPlayerRow);

    logger.debug("Removed player row: {}", mainMenuPlayerRow);
    updateControls();
  }

  /**
   * Disables the 'start game' button and shows a tooltip when hovering over it that a minimum
   * of two players are required to start the game.
   */
  protected void disableStartGameButton(String toolTipText) {
    startGameButton.setOnAction(null);
    startGameButton.getStyleClass().add("button-disabled");
    Tooltip tooltip = new Tooltip(toolTipText);
    tooltip.setShowDelay(Duration.millis(0));
    Tooltip.install(startGameButton, tooltip);
  }

  /**
   * Enables the 'start game' button and removes the tooltip when hovering over it.
   */
  protected void enableStartGameButton() {
    startGameButton.setOnAction(event -> notifyObservers("start_game"));
    startGameButton.getStyleClass().remove("button-disabled");
    Tooltip.uninstall(startGameButton, startGameButton.getTooltip());
  }

  protected void handleSavePlayers() {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Save players");
    fileChooser.setInitialFileName("players.csv");

    File file = fileChooser.showSaveDialog(this.getScene().getWindow());
    if (file == null) {
      showErrorAlert("Could not save players", "Invalid file path");
      return;
    }
    notifyObserversWithParams("save_players", Map.of("file", file));
  }

  protected void handleImportPlayers() {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Import players");
    fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV files", "*.csv"));
    File file = fileChooser.showOpenDialog(this.getScene().getWindow());
    if (file == null) {
      showErrorAlert("Could not import players", "Invalid file path");
      return;
    }
    notifyObserversWithParams("import_players", Map.of("file", file));
  }

  protected void handleImportBoard() {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Import board");
    fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON files", "*.json"));
    File file = fileChooser.showOpenDialog(this.getScene().getWindow());
    if (file == null) {
      showErrorAlert("Could not import board", "Invalid file path");
      return;
    }
    notifyObserversWithParams("import_board", Map.of("file", file));
  } 

  protected void handleCreateBoard() {
    notifyObservers("create_board");
  }

  public void showInfoAlert(String headerText, String message) {
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle("Info");
    alert.setHeaderText(headerText);
    alert.setContentText(message);
    alert.showAndWait();
  }

  public void showErrorAlert(String headerText, String message) {
    Alert alert = new Alert(Alert.AlertType.ERROR);
    alert.setTitle("Error");
    alert.setHeaderText(headerText);
    alert.setContentText(message);
    alert.showAndWait();
  }

  /**
   * Adds an observer to the menu view.
   *
   * @param observer The observer to add.
   */
  @Override
  public void addObserver(ButtonClickObserver observer) {
    logger.debug("Adding observer to MainMenuView");
    observers.add(observer);
  }

  /**
   * Removes an observer from the menu view.
   *
   * @param observer The observer to remove.
   */
  @Override
  public void removeObserver(ButtonClickObserver observer) {
    logger.debug("Removing observer from MainMenuView");
    observers.remove(observer);
  }

  /**
   * Notifies the observers of a button click.
   *
   * @param buttonId The id of the button that was clicked.
   */
  @Override
  public void notifyObservers(String buttonId) {
    logger.debug("Notifying observers of button click: {}", buttonId);
    observers.forEach(observer ->
        observer.onButtonClicked(buttonId));
  }

  /**
   * Notifies the observers of a button click with params.
   *
   * @param buttonId The id of the button that was clicked.
   * @param params The params of the button that was clicked.
   */
  @Override
  public void notifyObserversWithParams(String buttonId, Map<String, Object> params) {
    logger.debug("Notifying observers of button click with params: {} {}", buttonId, params);
    observers.forEach(observer ->
        observer.onButtonClickedWithParams(buttonId, params));
  }
}
