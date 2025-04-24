package edu.ntnu.idi.idatt.view.container;

import edu.ntnu.idi.idatt.controller.GameController;
import edu.ntnu.idi.idatt.model.Player;
import edu.ntnu.idi.idatt.model.interfaces.TileAction;
import edu.ntnu.idi.idatt.model.interfaces.BoardGameObserver;
import edu.ntnu.idi.idatt.view.component.GameBoardStackPane;
import edu.ntnu.idi.idatt.view.component.GameMenuBox;
import edu.ntnu.idi.idatt.view.component.GamePlayersBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class GameView extends HBox implements BoardGameObserver {
  private final GameController gameController;
  private final GamePlayersBox playersBox;
  private final GameBoardStackPane boardStackPane;
  private final GameMenuBox gameMenuBox;

  public GameView(GameController gameController) {
    this.gameController = gameController;
    gameController.addObserver(this);
    this.playersBox = getPlayersBox();
    this.boardStackPane = getBoardStackPane();
    this.gameMenuBox = getGameMenuBox();

    initialize();
  }

  private void initialize() {
    this.getChildren().setAll(playersBox, getInfiniteSpacer(), boardStackPane, getInfiniteSpacer(),
        gameMenuBox);
    this.getStyleClass().add("game-view");
  }

  private GamePlayersBox getPlayersBox() {
    return new GamePlayersBox(gameController.getPlayers(), gameController.getRoundNumber());
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
    playersBox.getPlayerRows().get(gameController.getPlayers().indexOf(player))
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
    playersBox.setRoundNumber(roundNumber);

    gameMenuBox.addGameLogRoundBox(roundNumber);
  }

  @Override
  public void onCurrentPlayerChanged(Player player) {
    if (!gameMenuBox.getRollForAllPlayersSelected()) {
      playersBox.setFocusedPlayer(gameController.getPlayers().indexOf(player));
      return;
    }
    playersBox.removeFocusedPlayer();

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
