package edu.ntnu.idi.idatt.view.laddergame;

import edu.ntnu.idi.idatt.view.common.MenuView;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.kordamp.ikonli.javafx.FontIcon;

import edu.ntnu.idi.idatt.model.board.Board;
import edu.ntnu.idi.idatt.model.player.Player;
import edu.ntnu.idi.idatt.model.player.PlayerTokenType;
import edu.ntnu.idi.idatt.view.component.MainMenuPlayerRow;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class LadderGameMenuView extends MenuView {

  /**
   * Constructor for MainMenuView class.
   */
  public LadderGameMenuView() {
    super();
    super.setOnUpdateControls(this::updateControls);
    this.getStyleClass().add("main-menu-view");
  }

  public void initialize() {
    logger.debug("Initializing MainMenuView");

    Region menuSpacer = new Region();
    menuSpacer.getStyleClass().add("main-menu-spacer");

    this.playerSelectionBox = createPlayerSelectionBox();
    this.boardSelectionBox = createBoardSelectionBox();

    HBox hBox = new HBox(playerSelectionBox, menuSpacer, boardSelectionBox);
    hBox.getStyleClass().add("main-menu-h-box");
    this.getChildren().setAll(createHeaderBox(), hBox, startGameButton);
    logger.debug("MainMenuView initialized successfully");
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

    VBox vBox = new VBox(title, carousel);
    vBox.getStyleClass().add("main-menu-board-selection-v-box");
    vBox.getStyleClass().add("main-menu-board-selection");
    return vBox;
  }

  /**
   * Updates the controls of the main menu view based on the number of players in the main menu,
   * and disables the start game button if there are not enough players. The button is also disabled
   * if there are any players with the same color and token type.
   */
  public void updateControls() {
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
    logger.debug("Setting players: {}", players);
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
    logger.debug("Setting selected board: {}", board.getName());
    selectedBoard = board;
    boardStackPane.initialize(selectedBoard, selectedBoard.getBackground());
    boardStackPane.getBackgroundImageView().setFitWidth(250);
    boardStackPane.getStyleClass().add("main-menu-board-selection-board-view");
    boardTitle.setText(board.getName());
    boardDescription.setText(board.getDescription());
    Platform.runLater(() -> {
      // Update the board in the carousel
      VBox carousel = (VBox) boardSelectionBox.getChildren().get(1);
      carousel.getChildren().set(0, boardStackPane);
      logger.debug("Board stack pane in carousel updated successfully");
    });
  }
}
