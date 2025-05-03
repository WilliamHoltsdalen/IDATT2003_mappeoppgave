package edu.ntnu.idi.idatt.view.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.kordamp.ikonli.javafx.FontIcon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.ntnu.idi.idatt.model.board.Board;
import edu.ntnu.idi.idatt.model.player.PlayerTokenType;
import edu.ntnu.idi.idatt.observer.ButtonClickObserver;
import edu.ntnu.idi.idatt.observer.ButtonClickSubject;
import edu.ntnu.idi.idatt.view.component.BoardStackPane;
import edu.ntnu.idi.idatt.view.component.MainMenuPlayerRow;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
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
  private Runnable onUpdateControls;

  protected VBox playerSelectionBox;
  protected final Text playerSelectionTitle;
  protected final List<MainMenuPlayerRow> mainMenuPlayerRows;
  protected final HBox addPlayerButtonsBox;
  protected final VBox playerListBox;

  protected VBox boardSelectionBox;
  protected final BoardStackPane boardStackPane;
  protected final Label boardTitle;
  protected final Label boardDescription;
  protected final Button startGameButton;
  protected Board selectedBoard;

  
  protected MenuView() {
    this.observers = new ArrayList<>();
    this.mainMenuPlayerRows = new ArrayList<>();
    this.playerSelectionTitle = new Text();
    this.playerListBox = new VBox();
    this.addPlayerButtonsBox = new HBox();
    this.boardTitle = new Label();
    this.boardDescription = new Label();
    this.startGameButton = createStartGameButton();
    this.playerSelectionBox = new VBox();
    this.boardSelectionBox = new VBox();
    this.boardStackPane = new BoardStackPane();
  }
  /**
   * Returns the list of player rows in the main menu.
   *
   * @return The list of player rows in the main menu.
   */
  public List<MainMenuPlayerRow> getPlayerRows() {
    return mainMenuPlayerRows;
  }

  /**
   * Sets the callback for the 'update controls' event.
   *
   * @param onUpdateControls The callback to set.
   */
  protected void setOnUpdateControls(Runnable onUpdateControls) {
    this.onUpdateControls = onUpdateControls;
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
  protected HBox createHeaderBox() {
    Text title = new Text("Main Menu");
    title.getStyleClass().add("main-menu-title");

    MenuButton moreOptionsMenu = new MenuButton("Import");
    moreOptionsMenu.getStyleClass().add("main-menu-more-options-menu");

    MenuItem importPlayersMenuItem = new MenuItem("Import players");
    importPlayersMenuItem.setGraphic(new FontIcon("fas-file-import"));
    moreOptionsMenu.getItems().addAll(importPlayersMenuItem);
    importPlayersMenuItem.setOnAction(event -> notifyObservers("import_players"));

    MenuItem importBoardMenuItem = new MenuItem("Import board");
    importBoardMenuItem.setGraphic(new FontIcon("fas-file-import"));
    moreOptionsMenu.getItems().addAll(importBoardMenuItem);
    importBoardMenuItem.setOnAction(event -> notifyObservers("import_board"));

    Button createBoardButton = new Button("Create Board");
    createBoardButton.getStyleClass().add("main-menu-create-board-button");
    createBoardButton.setOnAction(event -> notifyObservers("create_board"));

    HBox headerBox = new HBox(title, moreOptionsMenu, createBoardButton);
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
    mainMenuPlayerRow.setOnUpdate(onUpdateControls);
    mainMenuPlayerRows.add(mainMenuPlayerRow);
    playerListBox.getChildren().add(mainMenuPlayerRow);

    logger.debug("Added player row: {}", mainMenuPlayerRow);

    onUpdateControls.run();
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
    onUpdateControls.run();
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
