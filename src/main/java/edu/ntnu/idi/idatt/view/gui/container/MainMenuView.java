package edu.ntnu.idi.idatt.view.gui.container;

import edu.ntnu.idi.idatt.factory.BoardFactory;
import edu.ntnu.idi.idatt.model.Board;
import edu.ntnu.idi.idatt.model.Player;
import edu.ntnu.idi.idatt.view.gui.component.MainMenuPlayerRow;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import javafx.scene.control.Button;
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
import javafx.stage.FileChooser;
import javafx.util.Duration;
import org.kordamp.ikonli.javafx.FontIcon;

public class MainMenuView extends VBox {
  private Consumer<String> onImportPlayers;
  private Consumer<String> onImportBoard;
  private Runnable onStartGame;
  private Runnable onNextBoard;
  private Runnable onPrevBoard;

  private VBox playerSelectionBox;
  private final Text playerSelectionTitle;
  private final List<MainMenuPlayerRow> mainMenuPlayerRows;
  private final HBox addPlayerButtonsBox;
  private final VBox playerListBox;

  private VBox boardSelectionBox;
  private final ImageView boardImageView;
  private final Text boardTitle;
  private Board selectedBoard;

  private final Button startGameButton;

  /**
   * Constructor for MainMenuView class.
   */
  public MainMenuView() {
    this.mainMenuPlayerRows = new ArrayList<>();
    this.playerSelectionTitle = new Text();
    this.playerListBox = new VBox();
    this.addPlayerButtonsBox = new HBox();
    this.boardImageView = new ImageView();
    this.boardTitle = new Text();
    this.startGameButton = getStartGameButton();
    this.playerSelectionBox = new VBox();
    this.boardSelectionBox = new VBox();

    this.getStyleClass().add("main-menu-view");
    initialize();
  }

  /**
   * Returns the main menu view.
   *
   * @return The main menu view.
   */
  public VBox getView() {
    return this;
  }

  /**
   * Returns the list of main menu player rows.
   *
   * @return The list of main menu player rows.
   */
  public List<MainMenuPlayerRow> getPlayerRows() {
    return mainMenuPlayerRows;
  }

  private void initialize() {
    setSelectedBoard(BoardFactory.createBoard("Classic"));

    Region menuSpacer = new Region();
    menuSpacer.getStyleClass().add("main-menu-spacer");

    this.playerSelectionBox = getPlayerSelectionBox();
    this.boardSelectionBox = getBoardSelectionBox();

    HBox hBox = new HBox(playerSelectionBox, menuSpacer, boardSelectionBox);
    hBox.getStyleClass().add("main-menu-h-box");
    this.getChildren().setAll(getHeaderBox(), hBox, startGameButton);
  }

  private HBox getHeaderBox() {
    Text title = new Text("Main Menu");
    title.getStyleClass().add("main-menu-title");

    MenuButton moreOptionsMenu = new MenuButton("Import");
    moreOptionsMenu.getStyleClass().add("main-menu-more-options-menu");

    MenuItem importPlayersMenuItem = new MenuItem("Import players");
    importPlayersMenuItem.setGraphic(new FontIcon("fas-file-import"));
    moreOptionsMenu.getItems().addAll(importPlayersMenuItem);
    importPlayersMenuItem.setOnAction(event -> handleImportPlayersButtonAction());

    MenuItem importBoardMenuItem = new MenuItem("Import board");
    importBoardMenuItem.setGraphic(new FontIcon("fas-file-import"));
    moreOptionsMenu.getItems().addAll(importBoardMenuItem);
    importBoardMenuItem.setOnAction(event -> handleImportBoardButtonAction());

    HBox headerBox = new HBox(title, moreOptionsMenu);
    headerBox.getStyleClass().add("main-menu-header-box");

    return headerBox;
  }

  private VBox getPlayerSelectionBox() {
    playerSelectionTitle.setText("Select players");
    playerSelectionTitle.getStyleClass().add("main-menu-selection-box-title");

    playerListBox.getStyleClass().add("main-menu-player-list-box");
    addPlayerRow("Player 1", Color.RED, false);
    addPlayerRow("Player 2", Color.GREEN, true);

    Button addPlayerButton = new Button("Add Player");
    addPlayerButton.setOnAction(event -> addPlayerRow(
        "Player " + (mainMenuPlayerRows.size() + 1), Color.BLUE, true));

    Button addBotButton = new Button("Add Bot");
    addBotButton.setOnAction(event -> addPlayerRow(
        "Bot " + (mainMenuPlayerRows.size() + 1), Color.PURPLE, true));

    addPlayerButtonsBox.getChildren().addAll(addPlayerButton, addBotButton);
    addPlayerButtonsBox.getChildren().forEach(button -> button.getStyleClass()
        .add("main-menu-add-player-button"));
    addPlayerButtonsBox.getStyleClass().add("main-menu-add-player-buttons-box");

    VBox vBox = new VBox(playerSelectionTitle, playerListBox, addPlayerButtonsBox, startGameButton);
    vBox.getStyleClass().add("main-menu-player-selection");

    return vBox;
  }

  private VBox getBoardSelectionBox() {
    Text title = new Text("Select a board");
    title.getStyleClass().add("main-menu-selection-box-title");

    boardImageView.getStyleClass().add("main-menu-board-selection-image-view");

    Button previousButton = new Button("", new FontIcon("fas-chevron-left"));
    previousButton.getStyleClass().add("icon-only-button");
    previousButton.setOnAction(event -> onPrevBoard.run());

    boardTitle.setText(selectedBoard.getName());

    Button nextButton = new Button("", new FontIcon("fas-chevron-right"));
    nextButton.getStyleClass().add("icon-only-button");
    nextButton.setOnAction(event -> onNextBoard.run());

    HBox carouselControls = new HBox(previousButton, boardTitle, nextButton);
    carouselControls.getStyleClass().add("main-menu-board-selection-carousel-controls");

    VBox carousel = new VBox(boardImageView, carouselControls);
    carousel.getStyleClass().add("main-menu-board-selection-carousel");

    VBox vBox = new VBox(title, carousel);
    vBox.getStyleClass().add("main-menu-board-selection");
    return vBox;
  }

  private Button getStartGameButton() {
    Button button = new Button("Start Game");
    button.setOnAction(event -> handleStartGameButtonAction());
    return button;
  }

  /**
   * Adds a new player row to the main menu with the given name, color, and removable status.
   *
   * @param defaultName The default name of the player.
   * @param color The color of the player.
   * @param removable The removable status of the player.
   */
  private void addPlayerRow(String defaultName, Color color, boolean removable) {
    MainMenuPlayerRow mainMenuPlayerRow = new MainMenuPlayerRow(defaultName, color, removable);
    mainMenuPlayerRow.setRunnable(() -> removePlayerRow(mainMenuPlayerRow));
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
   * Updates the controls of the main menu view based on the number of players in the main menu.
   */
  private void updateControls() {
    if (mainMenuPlayerRows.size() == 5) {
      playerSelectionBox.getChildren().remove(addPlayerButtonsBox);
    } else if (mainMenuPlayerRows.size() < 5) {
      playerSelectionBox.getChildren().setAll(playerSelectionTitle, playerListBox, addPlayerButtonsBox);
    }

    if (mainMenuPlayerRows.size() < 2) {
      disableStartGameButton();
    } else {
      enableStartGameButton();
    }
  }

  /**
   * Disables the 'start game' button and shows a tooltip when hovering over it that a minimum
   * of two players are required to start the game.
   */
  private void disableStartGameButton() {
    startGameButton.setOnAction(null);
    startGameButton.getStyleClass().add("button-disabled");
    Tooltip tooltip = new Tooltip("You need at least two players.");
    tooltip.setShowDelay(Duration.millis(0));
    Tooltip.install(startGameButton, tooltip);
  }

  /**
   * Enables the 'start game' button and removes the tooltip when hovering over it.
   */
  private void enableStartGameButton() {
    startGameButton.setOnAction(event -> handleStartGameButtonAction());
    startGameButton.getStyleClass().remove("button-disabled");
    Tooltip.uninstall(startGameButton, startGameButton.getTooltip());
  }

  /**
   * Sets the callback for the 'start game' button.
   *
   * @param onStartGame The callback to set.
   */
  public void setOnStartGame(Runnable onStartGame) {
    this.onStartGame = onStartGame;
  }

  /**
   * Handles the action of the 'start game' button by calling the onStartGame callback if it is
   * not null.
   */
  private void handleStartGameButtonAction() {
    if (onStartGame != null) {
      onStartGame.run();
    }
  }

  /**
   * Sets the callback for the 'import players' button.
   *
   * @param onImportPlayers The callback to set.
   */
  public void setOnImportPlayers(Consumer<String> onImportPlayers) {
    this.onImportPlayers = onImportPlayers;
  }

  /**
   * Handles the action of the 'import players' button by calling the onImportPlayers callback if
   * it is not null. If the chosen file is null, it does nothing.
   */
  private void handleImportPlayersButtonAction() {
    if (onImportPlayers == null) {
      return;
    }

    File file = new FileChooser().showOpenDialog(null);
    if (file == null) {
      return;
    }
    onImportPlayers.accept(file.getAbsolutePath());
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
        !mainMenuPlayerRows.isEmpty()));
  }

  /**
   * Sets the callback for the 'import board' button.
   *
   * @param onImportBoard The callback to set.
   */
  public void setOnImportBoard(Consumer<String> onImportBoard) {
    this.onImportBoard = onImportBoard;
  }

  /**
   * Handles the action of the 'import board' button by calling the onImportBoard callback if it is
   * not null. If the chosen file is null, it does nothing.
   */
  private void handleImportBoardButtonAction() {
    if (onImportBoard == null) {
      return;
    }
    File file = new FileChooser().showOpenDialog(null);
    if (file == null) {
      return;
    }
    onImportBoard.accept(file.getAbsolutePath());
  }

  /**
   * Sets the selected board in the main menu to the given board object.
   *
   * @param board The board object to set.
   */
  public void setSelectedBoard(Board board) {
    selectedBoard = board;
    boardImageView.setImage(new Image("/images/Classic90.png"));
    boardTitle.setText(board.getName());
  }

  /**
   * Sets the callback for the 'next board' button.
   *
   * @param onNextBoard The callback to set.
   */
  public void setOnNextBoard(Runnable onNextBoard) {
    this.onNextBoard = onNextBoard;
  }

  /**
   * Sets the callback for the 'previous board' button.
   *
   * @param onPrevBoard The callback to set.
   */
  public void setOnPrevBoard(Runnable onPrevBoard) {
    this.onPrevBoard = onPrevBoard;
  }
}
