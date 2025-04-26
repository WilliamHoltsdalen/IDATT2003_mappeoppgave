package edu.ntnu.idi.idatt.view.container;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.kordamp.ikonli.javafx.FontIcon;

import edu.ntnu.idi.idatt.factory.BoardFactory;
import edu.ntnu.idi.idatt.model.Board;
import edu.ntnu.idi.idatt.model.Player;
import edu.ntnu.idi.idatt.model.PlayerTokenType;
import edu.ntnu.idi.idatt.observer.ButtonClickObserver;
import edu.ntnu.idi.idatt.observer.ButtonClickSubject;
import edu.ntnu.idi.idatt.view.component.MainMenuPlayerRow;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class MainMenuView extends VBox implements ButtonClickSubject {
  private final List<ButtonClickObserver> observers;

  private VBox playerSelectionBox;
  private final Text playerSelectionTitle;
  private final List<MainMenuPlayerRow> mainMenuPlayerRows;
  private final HBox addPlayerButtonsBox;
  private final VBox playerListBox;

  private VBox boardSelectionBox;
  private final ImageView boardImageView;
  private final Label boardTitle;
  private final Label boardDescription;
  private Board selectedBoard;

  private final Button startGameButton;

  /**
   * Constructor for MainMenuView class.
   */
  public MainMenuView() {
    this.observers = new ArrayList<>();
    this.mainMenuPlayerRows = new ArrayList<>();
    this.playerSelectionTitle = new Text();
    this.playerListBox = new VBox();
    this.addPlayerButtonsBox = new HBox();
    this.boardImageView = new ImageView();
    this.boardTitle = new Label();
    this.boardDescription = new Label();
    this.startGameButton = createStartGameButton();
    this.playerSelectionBox = new VBox();
    this.boardSelectionBox = new VBox();

    this.getStyleClass().add("main-menu-view");
  }

  @Override
  public void addObserver(ButtonClickObserver observer) {
    observers.add(observer);
  }

  @Override
  public void removeObserver(ButtonClickObserver observer) {
    observers.remove(observer);
  }

  @Override
  public void notifyObservers(String buttonId) {
    observers.forEach(observer ->
        observer.onButtonClicked(buttonId));
  }

  @Override
  public void notifyObserversWithParams(String buttonId, Map<String, Object> params) {
    observers.forEach(observer ->
        observer.onButtonClickedWithParams(buttonId, params));
  }

  public List<MainMenuPlayerRow> getPlayerRows() {
    return mainMenuPlayerRows;
  }

  public void initialize() {
    setSelectedBoard(BoardFactory.createBoard("Classic"));

    Region menuSpacer = new Region();
    menuSpacer.getStyleClass().add("main-menu-spacer");

    this.playerSelectionBox = createPlayerSelectionBox();
    this.boardSelectionBox = createBoardSelectionBox();

    HBox hBox = new HBox(playerSelectionBox, menuSpacer, boardSelectionBox);
    hBox.getStyleClass().add("main-menu-h-box");
    this.getChildren().setAll(createHeaderBox(), hBox, startGameButton);
  }

  private HBox createHeaderBox() {
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

  private VBox createPlayerSelectionBox() {
    playerSelectionTitle.setText("Select players");
    playerSelectionTitle.getStyleClass().add("main-menu-selection-box-title");

    playerListBox.getStyleClass().add("main-menu-player-list-box");
    addPlayerRow("Player 1", Color.RED, PlayerTokenType.CIRCLE,false);
    addPlayerRow("Player 2", Color.GREEN, PlayerTokenType.DIAMOND, true);

    Button addPlayerButton = new Button("Add Player");
    addPlayerButton.setOnAction(event -> addPlayerRow(
        "Player " + (mainMenuPlayerRows.size() + 1), Color.BLUE, PlayerTokenType.HEXAGON, true));

    Button addBotButton = new Button("Add Bot");
    addBotButton.setOnAction(event -> addPlayerRow(
        "Bot " + (mainMenuPlayerRows.size() + 1), Color.PURPLE, PlayerTokenType.SQUARE, true));

    addPlayerButtonsBox.getChildren().addAll(addPlayerButton, addBotButton);
    addPlayerButtonsBox.getChildren().forEach(button -> button.getStyleClass()
        .add("main-menu-add-player-button"));
    addPlayerButtonsBox.getStyleClass().add("main-menu-add-player-buttons-box");

    VBox vBox = new VBox(playerSelectionTitle, playerListBox, addPlayerButtonsBox, startGameButton);
    vBox.getStyleClass().add("main-menu-player-selection");

    return vBox;
  }

  private VBox createBoardSelectionBox() {
    Text title = new Text("Select a board");
    title.getStyleClass().add("main-menu-selection-box-title");

    boardImageView.getStyleClass().add("main-menu-board-selection-image-view");

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

    VBox carousel = new VBox(boardImageView, carouselControls, boardDescription);
    carousel.getStyleClass().add("main-menu-board-selection-carousel");

    VBox vBox = new VBox(title, carousel);
    vBox.getStyleClass().add("main-menu-board-selection");
    return vBox;
  }

  private Button createStartGameButton() {
    Button button = new Button("Start Game");
    button.setOnAction(event -> notifyObservers("start_game"));
    return button;
  }

  /**
   * Adds a new player row to the main menu with the given name, color, and removable status.
   *
   * @param defaultName The default name of the player.
   * @param color The color of the player.
   * @param removable The removable status of the player.
   */
  private void addPlayerRow(String defaultName, Color color, PlayerTokenType playerTokenType, boolean removable) {
    MainMenuPlayerRow mainMenuPlayerRow = new MainMenuPlayerRow(defaultName, color, playerTokenType, removable);
    mainMenuPlayerRow.setOnDelete(() -> removePlayerRow(mainMenuPlayerRow));
    mainMenuPlayerRow.setOnUpdate(this::updateControls);
    mainMenuPlayerRows.add(mainMenuPlayerRow);
    playerListBox.getChildren().add(mainMenuPlayerRow);

    updateControls();
  }

  /**
   * Removes a player row from the main menu.
   *
   * @param mainMenuPlayerRow The player row to remove.
   */
  private void removePlayerRow(MainMenuPlayerRow mainMenuPlayerRow) {
    mainMenuPlayerRows.remove(mainMenuPlayerRow);
    playerListBox.getChildren().remove(mainMenuPlayerRow);

    updateControls();
  }


  /**
   * Disables the 'start game' button and shows a tooltip when hovering over it that a minimum
   * of two players are required to start the game.
   */
  private void disableStartGameButton(String toolTipText) {
    startGameButton.setOnAction(null);
    startGameButton.getStyleClass().add("button-disabled");
    Tooltip tooltip = new Tooltip(toolTipText);
    tooltip.setShowDelay(Duration.millis(0));
    Tooltip.install(startGameButton, tooltip);
  }

  /**
   * Enables the 'start game' button and removes the tooltip when hovering over it.
   */
  private void enableStartGameButton() {
    startGameButton.setOnAction(event -> notifyObservers("start_game"));
    startGameButton.getStyleClass().remove("button-disabled");
    Tooltip.uninstall(startGameButton, startGameButton.getTooltip());
  }

  /**
   * Updates the controls of the main menu view based on the number of players in the main menu,
   * and disables the start game button if there are not enough players. The button is also disabled
   * if there are any players with the same color and token type.
   */
  private void updateControls() {
    // Hide / show the add player buttons box based on the number of players in the main menu.
    if (mainMenuPlayerRows.size() == 5) {
      playerSelectionBox.getChildren().remove(addPlayerButtonsBox);
    } else if (mainMenuPlayerRows.size() < 5) {
      playerSelectionBox.getChildren().setAll(playerSelectionTitle, playerListBox, addPlayerButtonsBox);
    }

    // Disable the start game button if there are not enough players.
    if (mainMenuPlayerRows.size() < 2) {
      disableStartGameButton("You need at least two players.");
    } else {
      enableStartGameButton();
    }

    /* Find all the unique colors and token types in the main menu, and disable the start game button
     * if there are any duplicates.*/
    Set<Color> uniqueColors = new HashSet<>(
        mainMenuPlayerRows.stream().map(MainMenuPlayerRow::getColor).toList());
    Set<PlayerTokenType> uniqueTokenTypes = new HashSet<>(
        mainMenuPlayerRows.stream().map(MainMenuPlayerRow::getPlayerTokenType).toList());
    if (uniqueColors.size() != mainMenuPlayerRows.size()) {
      disableStartGameButton("You can't have two players with the same color.");
    } else if (uniqueTokenTypes.size() != mainMenuPlayerRows.size()) {
      disableStartGameButton("You can't have two players with the same token type.");
    }
  }

  /**
   * Sets the players in the menu to the given list of players.
   *
   * @param players The list of players to import.
   */
  public void setPlayers(List<Player> players) {
    mainMenuPlayerRows.clear();
    playerListBox.getChildren().clear();
    players.forEach(player -> addPlayerRow(player.getName(), Color.web(player.getColorHex()),
        player.getPlayerTokenType(), !mainMenuPlayerRows.isEmpty()));
  }

  /**
   * Sets the selected board in the main menu to the given board object.
   *
   * @param board The board object to set.
   */
  public void setSelectedBoard(Board board) {
    selectedBoard = board;
    boardImageView.setImage(new Image(board.getImagePath()));
    boardTitle.setText(board.getName());
    boardDescription.setText(board.getDescription());
  }
}
