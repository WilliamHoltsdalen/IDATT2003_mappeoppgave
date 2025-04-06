package edu.ntnu.idi.idatt.view.container;

import edu.ntnu.idi.idatt.controller.GameController;
import edu.ntnu.idi.idatt.model.Player;
import edu.ntnu.idi.idatt.model.interfaces.TileAction;
import edu.ntnu.idi.idatt.observer.BoardGameObserver;
import edu.ntnu.idi.idatt.view.component.GameBoardStackPane;
import edu.ntnu.idi.idatt.view.component.GameMenuBox;
import edu.ntnu.idi.idatt.view.component.GamePlayerRow;
import edu.ntnu.idi.idatt.view.component.HorizontalDivider;
import java.util.ArrayList;
import java.util.List;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class GameView extends HBox implements BoardGameObserver {
  private static final String ROUND_NUMBER_TEXT = "Round ";

  private final GameController gameController;
  private final List<GamePlayerRow> playersBoxRows;
  private final VBox playersBox;
  private final GameBoardStackPane boardStackPane;
  private final GameMenuBox gameMenuBox;

  private final Text roundNumberText;

  public GameView(GameController gameController) {
    this.gameController = gameController;
    gameController.addObserver(this);

    playersBoxRows = new ArrayList<>();
    roundNumberText = new Text();
    playersBox = getPlayersBox();
    boardStackPane = getBoardStackPane();
    gameMenuBox = getGameMenuBox();

    initialize();
  }

  private void initialize() {
    this.getChildren().setAll(playersBox, getInfiniteSpacer(), boardStackPane, getInfiniteSpacer(),
        gameMenuBox);
    this.getStyleClass().add("game-view");
  }

  private VBox getPlayersBox() {
    roundNumberText.getStyleClass().add("game-players-box-round-number");
    roundNumberText.setText(ROUND_NUMBER_TEXT + gameController.getRoundNumber());

    HorizontalDivider horizontalDivider = new HorizontalDivider();

    VBox playersBoxVbox = new VBox(roundNumberText, horizontalDivider);
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

  private Region getInfiniteSpacer() {
    Region spacer = new Region();
    HBox.setHgrow(spacer, Priority.ALWAYS);
    VBox.setVgrow(spacer, Priority.ALWAYS);
    return spacer;
  }

  private GameBoardStackPane getBoardStackPane() {
    return new GameBoardStackPane(gameController.getBoard(), gameController.getPlayers());
  }

  private GameMenuBox getGameMenuBox() {
    GameMenuBox box = new GameMenuBox();
    box.setOnRestartGame(gameController::restartGame);
    box.setOnQuitGame(gameController::quitGame);
    box.setOnRollDice(this::handleRollDiceButtonAction);
    return box;
  }

  private void setPlayerTileNumber(Player player, int newTileId) {
    playersBoxRows.get(gameController.getPlayers().indexOf(player))
        .setTileNumber(player, newTileId);
  }

  private void handleRollDiceButtonAction() {
    if (gameMenuBox.getRollForAllPlayersSelected()) {
      gameController.performPlayerTurnForAllPlayers();
      return;
    }
    gameController.performPlayerTurn();
  }

  @Override
  public void onPlayerMoved(Player player, int diceRoll, int newTileId) {
    gameMenuBox.addGameLogRoundBoxEntry(player.getName() + " rolled " + diceRoll + " and moved to tile " + newTileId);

    setPlayerTileNumber(player, newTileId);

    boardStackPane.movePlayer(player, gameController.getBoard().getTile(newTileId), false);
  }

  @Override
  public void onRoundNumberIncremented(int roundNumber) {
    roundNumberText.setText(ROUND_NUMBER_TEXT + roundNumber);

    gameMenuBox.addGameLogRoundBox(roundNumber);
  }

  @Override
  public void onCurrentPlayerChanged(Player player) {
    gameMenuBox.addGameLogRoundBoxEntry(player.getName() + " is now the current player.");
  }

  @Override
  public void onTileActionPerformed(Player player, TileAction tileAction) {
    gameMenuBox.addGameLogRoundBoxEntry(player.getName() + " activated " + tileAction.getDescription());
    setPlayerTileNumber(player, tileAction.getDestinationTileId());

    boardStackPane.movePlayer(player, gameController.getBoard().getTile(tileAction.getDestinationTileId()), true);
  }

  @Override
  public void onGameFinished(Player winner) {
    gameMenuBox.addGameLogRoundBoxEntry("Game finished! Winner: " + winner.getName());
  }
}
