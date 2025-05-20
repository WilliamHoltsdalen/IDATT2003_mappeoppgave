package edu.ntnu.idi.idatt.view.common;

import edu.ntnu.idi.idatt.controller.common.MenuController;
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
 * MenuView.
 *
 * <p>Abstract base class for game menu views, providing common UI structure and functionality
 * for player selection, board selection, and navigation controls.</p>
 *
 * <p>It manages a list of {@link MenuPlayerRow}s, displays board information via a
 * {@link BoardStackPane},
 * and handles actions like adding/removing players, importing/saving configurations, and starting
 * the game. This class implements {@link ButtonClickSubject} to notify observers (typically a
 * {@link MenuController}) of user interactions.</p>
 *
 * @see VBox
 * @see ButtonClickSubject
 * @see MenuPlayerRow
 * @see BoardStackPane
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
   * Constructs a MenuView.
   *
   * @param boardStackPane The {@link BoardStackPane} used to display the selected board preview.
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

    this.getStylesheets().add("stylesheets/menuStyles.css");
    this.getStyleClass().add("menu-view");
  }

  /**
   * Validates the current player setup (e.g., number of players, unique colors/tokens) and
   * enables/disables the start game button accordingly. Subclasses must implement the specific
   * validation logic.
   */
  protected abstract void validatePlayers();

  /**
   * Sets the currently selected board and updates the view to display its information.
   *
   * @param board The {@link Board} to be displayed.
   */
  public abstract void setSelectedBoard(Board board);

  /**
   * Initializes the menu view with a title and configuration for player and board selection.
   *
   * @param title                   The title to be displayed at the top of the menu.
   * @param allowedPlayerTokenTypes A list of {@link PlayerTokenType}s allowed for players.
   * @param allowedPlayerColors     A list of hex color strings allowed for players.
   * @param minimumPlayers          The minimum number of players required to start the game.
   * @param maximumPlayers          The maximum number of players allowed in the game.
   */
  public void initialize(String title, List<PlayerTokenType> allowedPlayerTokenTypes,
      List<String> allowedPlayerColors, int minimumPlayers, int maximumPlayers) {
    this.allowedPlayerTokenTypes.addAll(allowedPlayerTokenTypes);
    this.allowedPlayerColors.addAll(allowedPlayerColors);
    this.minimumPlayers = minimumPlayers;
    this.maximumPlayers = maximumPlayers;

    Region menuSpacer = new Region();
    menuSpacer.getStyleClass().add("menu-spacer");

    this.playerSelectionBox = createPlayerSelectionBox();
    this.boardSelectionBox = createBoardSelectionBox();

    HBox hbox = new HBox(playerSelectionBox, menuSpacer, boardSelectionBox);
    hbox.getStyleClass().add("menu-h-box");
    this.getChildren().setAll(createHeaderBox(title), hbox, startGameButton);
    logger.debug("MenuView initialized successfully");
  }

  /**
   * Returns the list of {@link MenuPlayerRow}s currently displayed in the menu.
   *
   * @return A list of player rows.
   */
  public List<MenuPlayerRow> getPlayerRows() {
    return mainMenuPlayerRows;
  }

  /**
   * Creates the "Start Game" button and configures its action to notify observers.
   *
   * @return The configured {@link Button} for starting the game.
   */
  private Button createStartGameButton() {
    Button button = new Button("Start Game");
    button.setOnAction(event -> notifyObservers("start_game"));
    return button;
  }

  /**
   * Creates the header HBox for the menu view, containing a back button and the menu title.
   *
   * @param title The title text to display in the header.
   * @return The configured {@link HBox} for the header.
   */
  protected HBox createHeaderBox(String title) {
    Button backButton = new Button("Game selection");
    backButton.setGraphic(new FontIcon("fas-chevron-left"));
    backButton.getStyleClass().add("back-button");
    backButton.setOnAction(event -> notifyObservers("back_to_game_selection"));

    Text headerTitle = new Text(title);
    headerTitle.getStyleClass().add("menu-title");

    Region spacer = new Region();
    HBox.setHgrow(spacer, Priority.ALWAYS);
    Platform.runLater(() -> spacer.setMaxWidth(backButton.getWidth()));

    HBox headerBox = new HBox(backButton, headerTitle, spacer);
    headerBox.getStyleClass().add("header-box");

    return headerBox;
  }

  /**
   * Creates the player selection section of the menu, including a title, options menu
   * (import/save), the list of player rows, and buttons to add new players or bots.
   *
   * @return A {@link VBox} containing the player selection UI elements.
   */
  protected VBox createPlayerSelectionBox() {
    Text playerSelectionTitle = new Text("Select players");
    playerSelectionTitle.getStyleClass().add("selection-box-title");

    MenuButton playerOptionsMenu = new MenuButton("Options");
    playerOptionsMenu.getStyleClass().add("menu-options-menu");

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

    playerListBox.getStyleClass().add("player-list-box");
    if (!allowedPlayerColors.isEmpty() && allowedPlayerColors.size() <= maximumPlayers) {
      addPlayerRow("Player 1", Color.web(allowedPlayerColors.get(0)), false, false);
      addPlayerRow("Player 2", Color.web(allowedPlayerColors.get(1)), true, false);
    } else if (allowedPlayerColors.isEmpty()) {
      addPlayerRow("Player 1", Color.RED, false, false);
      addPlayerRow("Player 2", Color.GREEN, true, false);
    }

    Supplier<Color> getPlayerRowDefaultColor = () -> allowedPlayerColors.isEmpty() ? Color.BLUE
        : Color.web(allowedPlayerColors.get(mainMenuPlayerRows.size()));

    Button addPlayerButton = new Button("Add Player");
    addPlayerButton.setOnAction(event -> addPlayerRow(
        "Player " + (mainMenuPlayerRows.size() + 1), getPlayerRowDefaultColor.get(), true, false));

    Button addBotButton = new Button("Add Bot");
    addBotButton.setOnAction(event -> addPlayerRow(
        "Bot " + (mainMenuPlayerRows.size() + 1), getPlayerRowDefaultColor.get(), true, true));

    addPlayerButtonsBox.getChildren().addAll(addPlayerButton, addBotButton);
    addPlayerButtonsBox.getChildren().forEach(button -> button.getStyleClass()
        .add("add-player-button"));
    addPlayerButtonsBox.getStyleClass().add("add-player-buttons-box");

    VBox vbox = new VBox(playerSelectionHeader, playerListBox, addPlayerButtonsBox);
    vbox.getStyleClass().add("player-selection");

    return vbox;
  }

  /**
   * Creates the board selection section of the menu, including a title, options menu
   * (import/create), the board display/carousel, and board information (title, description).
   *
   * @return A {@link VBox} containing the board selection UI elements.
   */
  protected VBox createBoardSelectionBox() {
    Text boardSelectionTitle = new Text("Select a board");
    boardSelectionTitle.getStyleClass().add("selection-box-title");

    MenuButton boardOptionsMenu = new MenuButton("Options");
    boardOptionsMenu.getStyleClass().add("menu-options-menu");

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
    carouselControls.getStyleClass().add("board-carousel-controls");

    boardDescription.setText(selectedBoard.getDescription());
    boardDescription.getStyleClass().add("board-description");
    boardDescription.prefWidthProperty().bind(carouselControls.widthProperty().multiply(0.8));
    boardDescription.setMinHeight(Region.USE_PREF_SIZE);
    boardDescription.setWrapText(true);

    VBox carousel = new VBox(boardStackPane, carouselControls, boardDescription);
    carousel.getStyleClass().add("board-carousel");

    VBox vbox = new VBox(boardSelectionHeader, carousel);
    vbox.getStyleClass().add("board-selection");
    return vbox;
  }

  /**
   * Replaces the current player rows in the menu with new rows based on the provided list of
   * {@link Player}s.
   *
   * @param players The list of players to display.
   */
  public void setPlayers(List<Player> players) {
    logger.debug("Setting players: {}", players);
    mainMenuPlayerRows.clear();
    playerListBox.getChildren().clear();
    players.forEach(player -> addPlayerRow(player.getName(), Color.web(player.getColorHex()),
        !mainMenuPlayerRows.isEmpty(), player.isBot()));
  }

  /**
   * Adds a new {@link MenuPlayerRow} to the player list.
   *
   * @param defaultName The default name for the new player.
   * @param color       The initial color for the new player's token.
   * @param removable   Whether the new player row can be removed.
   * @param isBot       Whether the new player is a bot.
   */
  protected void addPlayerRow(String defaultName, Color color, boolean removable, boolean isBot) {
    PlayerTokenType playerToken =
        allowedPlayerTokenTypes.size() >= minimumPlayers ? allowedPlayerTokenTypes.get(
            playerListBox.getChildren().size()) : allowedPlayerTokenTypes.get(0);
    MenuPlayerRow mainMenuPlayerRow = new MenuPlayerRow(defaultName, color, playerToken,
        allowedPlayerTokenTypes, allowedPlayerColors, removable, isBot);
    mainMenuPlayerRow.setOnDelete(() -> removePlayerRow(mainMenuPlayerRow));
    mainMenuPlayerRow.setOnUpdate(this::updateControls);
    mainMenuPlayerRows.add(mainMenuPlayerRow);
    playerListBox.getChildren().add(mainMenuPlayerRow);

    logger.debug("Added player row: {}", mainMenuPlayerRow);

    updateControls();
  }

  /**
   * Removes the specified {@link MenuPlayerRow} from the player list.
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
   * Updates the visibility of add player/bot buttons and calls {@link #validatePlayers()} to
   * enable/disable the start game button based on the current player configuration.
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
   * Disables the "Start Game" button and sets a tooltip with the provided text.
   *
   * @param toolTipText The text to display in the tooltip when the button is hovered.
   */
  protected void disableStartGameButton(String toolTipText) {
    startGameButton.setOnAction(null);
    startGameButton.getStyleClass().add("button-disabled");
    Tooltip tooltip = new Tooltip(toolTipText);
    tooltip.setShowDelay(Duration.millis(0));
    Tooltip.install(startGameButton, tooltip);
  }

  /**
   * Enables the "Start Game" button and removes any associated tooltip.
   */
  protected void enableStartGameButton() {
    startGameButton.setOnAction(event -> notifyObservers("start_game"));
    startGameButton.getStyleClass().remove("button-disabled");
    Tooltip.uninstall(startGameButton, startGameButton.getTooltip());
  }

  /**
   * Handles the action to save the current player list to a file. Opens a {@link FileChooser} and
   * notifies observers with the selected file.
   */
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

  /**
   * Handles the action to import players from a file. Opens a {@link FileChooser} and notifies
   * observers with the selected file.
   */
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

  /**
   * Handles the action to import a board from a file. Opens a {@link FileChooser} and notifies
   * observers with the selected file.
   */
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

  /**
   * Handles the action to navigate to the board creation screen. Notifies observers with the
   * "create_board" action ID.
   */
  protected void handleCreateBoard() {
    notifyObservers("create_board");
  }

  /**
   * Displays an information alert dialog.
   *
   * @param headerText The header text for the alert.
   * @param message    The main message content for the alert.
   */
  public void showInfoAlert(String headerText, String message) {
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle("Info");
    alert.setHeaderText(headerText);
    alert.setContentText(message);
    alert.showAndWait();
  }

  /**
   * Displays an error alert dialog.
   *
   * @param headerText The header text for the alert.
   * @param message    The main message content for the alert.
   */
  public void showErrorAlert(String headerText, String message) {
    Alert alert = new Alert(Alert.AlertType.ERROR);
    alert.setTitle("Error");
    alert.setHeaderText(headerText);
    alert.setContentText(message);
    alert.showAndWait();
  }

  /**
   * Adds a {@link ButtonClickObserver} to be notified of button clicks in this menu.
   *
   * @param observer The observer to add.
   */
  @Override
  public void addObserver(ButtonClickObserver observer) {
    logger.debug("Adding observer to MainMenuView");
    observers.add(observer);
  }

  /**
   * Removes a {@link ButtonClickObserver} from this menu.
   *
   * @param observer The observer to remove.
   */
  @Override
  public void removeObserver(ButtonClickObserver observer) {
    logger.debug("Removing observer from MainMenuView");
    observers.remove(observer);
  }

  /**
   * Notifies all registered observers of a button click event.
   *
   * @param buttonId The ID of the button that was clicked.
   */
  @Override
  public void notifyObservers(String buttonId) {
    logger.debug("Notifying observers of button click: {}", buttonId);
    observers.forEach(observer ->
        observer.onButtonClicked(buttonId));
  }

  /**
   * Notifies all registered observers of a button click event that includes parameters.
   *
   * @param buttonId The ID of the button that was clicked.
   * @param params   A map of parameters associated with the button click.
   */
  @Override
  public void notifyObserversWithParams(String buttonId, Map<String, Object> params) {
    logger.debug("Notifying observers of button click with params: {} {}", buttonId, params);
    observers.forEach(observer ->
        observer.onButtonClickedWithParams(buttonId, params));
  }
}
