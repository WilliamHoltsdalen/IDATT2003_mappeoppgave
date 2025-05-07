package edu.ntnu.idi.idatt.view.common;

import edu.ntnu.idi.idatt.model.board.Board;
import edu.ntnu.idi.idatt.model.player.Player;
import edu.ntnu.idi.idatt.model.player.PlayerTokenType;
import edu.ntnu.idi.idatt.observer.ButtonClickObserver;
import edu.ntnu.idi.idatt.observer.ButtonClickSubject;
import edu.ntnu.idi.idatt.view.component.MenuPlayerRow;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import org.kordamp.ikonli.javafx.FontIcon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
  protected final List<ButtonClickObserver> observers;
  protected final List<PlayerTokenType> allowedPlayerTokenTypes;
  protected final List<String> allowedPlayerColors;
  protected int minimumPlayers;
  protected int maximumPlayers;

  protected VBox playerSelectionBox;
  protected HBox playerSelectionHeader;
  protected final List<MenuPlayerRow> mainMenuPlayerRows;
  protected final HBox addPlayerButtonsBox;
  protected final VBox playerListBox;

  protected VBox boardSelectionBox;
  protected HBox boardSelectionHeader;
  protected final BoardStackPane boardStackPane;
  protected final Label boardTitle;
  protected final Label boardDescription;
  protected final Button startGameButton;
  protected Board selectedBoard;

  /**
   * Constructor for MenuView class.
   */
  protected MenuView(BoardStackPane boardStackPane) {
    this.observers = new ArrayList<>();
    this.allowedPlayerTokenTypes = new ArrayList<>();
    this.allowedPlayerColors = new ArrayList<>();
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
    this.boardStackPane = boardStackPane;

    this.getStyleClass().add("main-menu-view");
  }

  /**
   * Validates the players in the menu view and disables the start game button if necessary.
   */
  protected abstract void validatePlayers();

  /**
   * Sets the selected board in the main menu to the given board object.
   *
   * @param board The board object to set.
   */
  public abstract void setSelectedBoard(Board board);

  /**
   * Initializes the menu view.
   *
   * @param title                   The title of the menu view.
   * @param allowedPlayerTokenTypes The allowed player token types.
   */
  public void initialize(String title, List<PlayerTokenType> allowedPlayerTokenTypes,
      List<String> allowedPlayerColors, int minimumPlayers, int maximumPlayers) {
    this.allowedPlayerTokenTypes.addAll(allowedPlayerTokenTypes);
    this.allowedPlayerColors.addAll(allowedPlayerColors);
    this.minimumPlayers = minimumPlayers;
    this.maximumPlayers = maximumPlayers;

    Region menuSpacer = new Region();
    menuSpacer.getStyleClass().add("main-menu-spacer");

    this.playerSelectionBox = createPlayerSelectionBox();
    this.boardSelectionBox = createBoardSelectionBox();

    HBox hBox = new HBox(playerSelectionBox, menuSpacer, boardSelectionBox);
    hBox.getStyleClass().add("main-menu-h-box");
    this.getChildren().setAll(createHeaderBox(title), hBox, startGameButton);
    logger.debug("MenuView initialized successfully");
  }

  /**
   * Returns the list of player rows in the main menu.
   *
   * @return The list of player rows in the main menu.
   */
  public List<MenuPlayerRow> getPlayerRows() {
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
   * Creates a player selection box with a title, options, and player list.
   *
   * @return The player selection VBox.
   */
  protected VBox createPlayerSelectionBox() {
    Text playerSelectionTitle = new Text("Select players");
    playerSelectionTitle.getStyleClass().add("main-menu-selection-box-title");

    MenuButton playerOptionsMenu = new MenuButton("Options");
    playerOptionsMenu.getStyleClass().add("main-menu-options-menu");

    MenuItem importPlayersMenuItem = new MenuItem("Import players");
    importPlayersMenuItem.setGraphic(new FontIcon("fas-file-import"));
    importPlayersMenuItem.setOnAction(event -> handleImportPlayers());

    MenuItem savePlayersMenuItem = new MenuItem("Save players");
    savePlayersMenuItem.setGraphic(new FontIcon("fas-file-export"));
    savePlayersMenuItem.setOnAction(event -> handleSavePlayers());

    playerOptionsMenu.getItems().addAll(importPlayersMenuItem, savePlayersMenuItem);

    playerSelectionHeader.getChildren().addAll(playerSelectionTitle, playerOptionsMenu);
    playerSelectionHeader.setSpacing(10);
    playerSelectionHeader.setAlignment(Pos.CENTER);

    playerListBox.getStyleClass().add("main-menu-player-list-box");
    if (!allowedPlayerColors.isEmpty() && allowedPlayerColors.size() <= maximumPlayers) {
      addPlayerRow("Player 1", Color.web(allowedPlayerColors.get(0)), false);
      addPlayerRow("Player 2", Color.web(allowedPlayerColors.get(1)), true);
    } else if (allowedPlayerColors.isEmpty()) {
      addPlayerRow("Player 1", Color.RED, false);
      addPlayerRow("Player 2", Color.GREEN, true);
    }

    Supplier<Color> getPlayerRowDefaultColor = () -> allowedPlayerColors.isEmpty() ? Color.BLUE
        : Color.web(allowedPlayerColors.get(mainMenuPlayerRows.size()));

    Button addPlayerButton = new Button("Add Player");
    addPlayerButton.setOnAction(event -> addPlayerRow(
        "Player " + (mainMenuPlayerRows.size() + 1), getPlayerRowDefaultColor.get(), true));

    Button addBotButton = new Button("Add Bot");
    addBotButton.setOnAction(event -> addPlayerRow(
        "Bot " + (mainMenuPlayerRows.size() + 1), getPlayerRowDefaultColor.get(), true));

    addPlayerButtonsBox.getChildren().addAll(addPlayerButton, addBotButton);
    addPlayerButtonsBox.getChildren().forEach(button -> button.getStyleClass()
        .add("main-menu-add-player-button"));
    addPlayerButtonsBox.getStyleClass().add("main-menu-add-player-buttons-box");

    VBox vBox = new VBox(playerSelectionHeader, playerListBox, addPlayerButtonsBox);
    vBox.getStyleClass().add("main-menu-player-selection");

    return vBox;
  }

  /**
   * Creates a board selection box with a title, options, and board carousel.
   *
   * @return The board selection VBox.
   */
  protected VBox createBoardSelectionBox() {
    Text boardSelectionTitle = new Text("Select a board");
    boardSelectionTitle.getStyleClass().add("main-menu-selection-box-title");

    MenuButton boardOptionsMenu = new MenuButton("Options");
    boardOptionsMenu.getStyleClass().add("main-menu-options-menu");

    MenuItem importBoardMenuItem = new MenuItem("Import board");
    importBoardMenuItem.setGraphic(new FontIcon("fas-file-import"));
    importBoardMenuItem.setOnAction(event -> handleImportBoard());

    MenuItem createBoardMenuItem = new MenuItem("Create board");
    createBoardMenuItem.setGraphic(new FontIcon("fas-plus"));
    createBoardMenuItem.setOnAction(event -> handleCreateBoard());

    boardOptionsMenu.getItems().addAll(importBoardMenuItem, createBoardMenuItem);

    boardSelectionHeader.getChildren().addAll(boardSelectionTitle, boardOptionsMenu);
    boardSelectionHeader.setSpacing(10);
    boardSelectionHeader.setAlignment(Pos.CENTER);

    Button previousButton = new Button("", new FontIcon("fas-chevron-left"));
    previousButton.getStyleClass().add("icon-only-button");
    previousButton.setOnAction(event -> notifyObservers("previous_board"));

    boardTitle.setText(selectedBoard.getName());

    Button nextButton = new Button("", new FontIcon("fas-chevron-right"));
    nextButton.getStyleClass().add("icon-only-button");
    nextButton.setOnAction(event -> notifyObservers("next_board"));

    HBox carouselControls = new HBox(previousButton, boardTitle, nextButton);
    carouselControls.getStyleClass().add("main-menu-board-selection-carousel-controls");

    boardDescription.setText(selectedBoard.getDescription());
    boardDescription.getStyleClass().add("main-menu-board-selection-description");
    boardDescription.prefWidthProperty().bind(carouselControls.widthProperty().multiply(0.8));
    boardDescription.setMinHeight(Region.USE_PREF_SIZE);
    boardDescription.setWrapText(true);

    VBox carousel = new VBox(boardStackPane, carouselControls, boardDescription);
    carousel.getStyleClass().add("main-menu-board-selection-carousel");

    VBox vBox = new VBox(boardSelectionHeader, carousel);
    vBox.getStyleClass().add("main-menu-board-selection-v-box");
    vBox.getStyleClass().add("main-menu-board-selection");
    return vBox;
  }

  /**
   * Sets the players in the menu to the given list of players.
   *
   * @param players The list of players to import.
   */
  public void setPlayers(List<Player> players) {
    logger.debug("Setting players: {}", players);
    mainMenuPlayerRows.clear();
    playerListBox.getChildren().clear();
    players.forEach(player -> addPlayerRow(player.getName(), Color.web(player.getColorHex()),
        !mainMenuPlayerRows.isEmpty()));
  }

  /**
   * Adds a new player row to the main menu with the given name, color, and removable status.
   *
   * @param defaultName The default name of the player.
   * @param color       The color of the player.
   * @param removable   The removable status of the player.
   */
  protected void addPlayerRow(String defaultName, Color color, boolean removable) {
    PlayerTokenType playerToken =
        allowedPlayerTokenTypes.size() >= minimumPlayers ? allowedPlayerTokenTypes.get(
            playerListBox.getChildren().size()) : allowedPlayerTokenTypes.get(0);
    MenuPlayerRow mainMenuPlayerRow = new MenuPlayerRow(defaultName, color, playerToken,
        allowedPlayerTokenTypes, allowedPlayerColors, removable);
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
  protected void removePlayerRow(MenuPlayerRow mainMenuPlayerRow) {
    mainMenuPlayerRows.remove(mainMenuPlayerRow);
    playerListBox.getChildren().remove(mainMenuPlayerRow);

    logger.debug("Removed player row: {}", mainMenuPlayerRow);
    updateControls();
  }

  /**
   * Updates the controls of the main menu view based on the number of players in the main menu, and
   * disables the start game button if there are not enough players. The button is also disabled if
   * there are any players with the same color and token type.
   */
  protected void updateControls() {
    // Hide / show the add player buttons box based on the number of players in the main menu.
    if (mainMenuPlayerRows.size() == maximumPlayers) {
      playerSelectionBox.getChildren().remove(addPlayerButtonsBox);
    } else if (mainMenuPlayerRows.size() < maximumPlayers) {
      playerSelectionBox.getChildren()
          .setAll(playerSelectionHeader, playerListBox, addPlayerButtonsBox);
    }

    validatePlayers();
  }

  /**
   * Disables the 'start game' button and shows a tooltip when hovering over it that a minimum of
   * two players are required to start the game.
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
   * @param params   The params of the button that was clicked.
   */
  @Override
  public void notifyObserversWithParams(String buttonId, Map<String, Object> params) {
    logger.debug("Notifying observers of button click with params: {} {}", buttonId, params);
    observers.forEach(observer ->
        observer.onButtonClickedWithParams(buttonId, params));
  }
}
