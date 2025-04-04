package edu.ntnu.idi.idatt.view.gui.container;

import edu.ntnu.idi.idatt.controller.GameController;
import edu.ntnu.idi.idatt.model.Player;
import edu.ntnu.idi.idatt.model.interfaces.TileAction;
import edu.ntnu.idi.idatt.observer.BoardGameObserver;
import edu.ntnu.idi.idatt.view.gui.component.GamePlayerRow;
import java.util.ArrayList;
import java.util.List;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class GameView extends HBox implements BoardGameObserver {
  private static final String ROUND_NUMBER_TEXT = "Round ";
  private final GameController gameController;
  private final List<GamePlayerRow> playersBoxRows;
  private final VBox playersBox;
  private final StackPane boardStackPane;
  private final VBox menuBox;
  private final ListView<Text> gameLogBox;
  private final CheckBox rollForAllPlayersCheckBox;
  private final Text roundNumberText;

  public GameView(GameController gameController) {
    this.gameController = gameController;
    gameController.addObserver(this);

    playersBoxRows = new ArrayList<>();
    roundNumberText = new Text();
    gameLogBox = new ListView<>();
    rollForAllPlayersCheckBox = new CheckBox();
    playersBox = getPlayersBox();
    boardStackPane = getBoardStackPane();
    menuBox = getMenuBox();

    initialize();
  }

  private void initialize() {
    this.getChildren().setAll(playersBox, getInfiniteSpacer(), boardStackPane, getInfiniteSpacer(), menuBox);
    this.getStyleClass().add("game-view");
  }

  public HBox getView() {
    return this;
  }

  private VBox getPlayersBox() {
    roundNumberText.getStyleClass().add("game-players-box-round-number");
    roundNumberText.setText(ROUND_NUMBER_TEXT + gameController.getRoundNumber());

    VBox playersBoxVbox = new VBox(roundNumberText, getHorizontalDivider());
    playersBoxVbox.getStyleClass().add("game-players-box");
    playersBoxVbox.maxHeightProperty().bind(playersBoxVbox.heightProperty());

    gameController.getPlayers().forEach(player -> {
      GamePlayerRow playerRow = new GamePlayerRow(player);
      HBox.setHgrow(playerRow, Priority.ALWAYS);
      playersBoxRows.add(playerRow);
      playersBoxVbox.getChildren().add(playerRow);
    });

    VBox vBox = new VBox(playersBoxVbox);
    VBox.setVgrow(playersBoxVbox, Priority.ALWAYS);
    HBox.setHgrow(vBox, Priority.NEVER);
    vBox.setAlignment(Pos.TOP_LEFT);
    return vBox;
  }

  private Region getHorizontalDivider() {
    Region divider = new Region();
    divider.getStyleClass().add("game-horizontal-divider");
    return divider;
  }

  private Region getInfiniteSpacer() {
    Region spacer = new Region();
    HBox.setHgrow(spacer, Priority.ALWAYS);
    VBox.setVgrow(spacer, Priority.ALWAYS);
    return spacer;
  }

  private StackPane getBoardStackPane() {
    ImageView imageView = new ImageView();
    imageView.setImage(new Image(gameController.getBoardImagePath()));
    imageView.getStyleClass().add("game-board-image-view");

    StackPane stackPane = new StackPane();
    stackPane.getChildren().add(imageView);
    stackPane.getStyleClass().add("game-board-stack-pane");
    stackPane.maxHeightProperty().bind(stackPane.heightProperty());
    return stackPane;
  }

  private VBox getMenuBox() {
    Button restartGameButton = new Button("Restart game");
    restartGameButton.getStyleClass().add("game-menu-restart-button");
    restartGameButton.setOnAction(event -> gameController.restartGame());

    Button quitGameButton = new Button("Quit game");
    quitGameButton.getStyleClass().add("game-menu-quit-button");
    quitGameButton.setOnAction(event -> gameController.quitGame());

    HBox hBox = new HBox(restartGameButton, quitGameButton);
    hBox.getStyleClass().add("game-menu-buttons-h-box");

    VBox menuTopBox = new VBox(hBox, getHorizontalDivider());
    menuTopBox.getStyleClass().add("game-menu-top-box");

    gameLogBox.getStyleClass().add("game-menu-game-log-box");

    Button rollDiceButton = new Button("Roll dice");
    rollDiceButton.getStyleClass().add("game-menu-roll-dice-button");
    rollDiceButton.setOnAction(event -> handleRollDiceButtonAction());

    Text rollForAllPlayersText = new Text("Roll for all players");
    rollForAllPlayersText.getStyleClass().add("game-menu-roll-for-all-players-text");

    rollForAllPlayersCheckBox.getStyleClass().add("game-menu-roll-for-all-players-check-box");

    HBox rollForAllPlayersHBox = new HBox(rollForAllPlayersText, rollForAllPlayersCheckBox);
    rollForAllPlayersHBox.getStyleClass().add("game-menu-roll-for-all-players-box");

    VBox menuBottomBox = new VBox(getHorizontalDivider(), rollDiceButton, rollForAllPlayersHBox);
    menuBottomBox.getStyleClass().add("game-menu-bottom-box");

    VBox menuVbox = new VBox(menuTopBox, gameLogBox, menuBottomBox);
    menuVbox.getStyleClass().add("game-menu-box");
    VBox.setVgrow(menuBottomBox, Priority.ALWAYS);
    return menuVbox;
  }

  private void addGameLogEntry(String text) {
    gameLogBox.getItems().add(new Text(text));
    gameLogBox.scrollTo(gameLogBox.getItems().size());
  }

  private void setPlayerTileNumber(Player player, int newTileId) {
    playersBoxRows.get(gameController.getPlayers().indexOf(player))
        .setTileNumber(player, newTileId);
  }

  private void handleRollDiceButtonAction() {
    if (rollForAllPlayersCheckBox.isSelected()) {
      gameController.performPlayerTurnForAllPlayers();
      return;
    }
    gameController.performPlayerTurn();
  }

  @Override
  public void onPlayerMoved(Player player, int diceRoll, int newTileId) {
    addGameLogEntry(player.getName() + " rolled " + diceRoll + " and moved to tile " + newTileId);

    setPlayerTileNumber(player, newTileId);
  }

  @Override
  public void onRoundNumberIncremented(int roundNumber) {
    roundNumberText.setText(ROUND_NUMBER_TEXT + roundNumber);
    addGameLogEntry("");
    addGameLogEntry(ROUND_NUMBER_TEXT + roundNumber);
  }

  @Override
  public void onCurrentPlayerChanged(Player player) {
    addGameLogEntry(player.getName() + " is now the current player.");
  }

  @Override
  public void onTileActionPerformed(Player player, TileAction tileAction) {
    addGameLogEntry(player.getName() + " activated " + tileAction.getDescription());
    setPlayerTileNumber(player, tileAction.getDestinationTileId());
  }

  @Override
  public void onGameFinished(Player winner) {
    addGameLogEntry("Game finished! Winner: " + winner.getName());
  }
}
