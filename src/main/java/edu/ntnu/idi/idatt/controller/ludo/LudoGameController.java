package edu.ntnu.idi.idatt.controller.ludo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import edu.ntnu.idi.idatt.controller.common.GameController;
import edu.ntnu.idi.idatt.model.board.Board;
import edu.ntnu.idi.idatt.model.game.LudoBoardGame;
import edu.ntnu.idi.idatt.model.player.LudoPlayer;
import edu.ntnu.idi.idatt.model.player.Player;
import edu.ntnu.idi.idatt.model.token.LudoToken;
import edu.ntnu.idi.idatt.view.ludo.LudoGameStackPane;
import edu.ntnu.idi.idatt.view.ludo.LudoGameView;

public class LudoGameController extends GameController {

  public LudoGameController(LudoGameView ludoGameView, Board board, List<Player> players) {
    super(ludoGameView, board, players);
  }

  @Override
  public void initializeBoardGame(Board board, List<Player> players) {
    try {
      boardGame = new LudoBoardGame(board, players, 1);
      boardGame.addObserver(this);
    } catch (IllegalArgumentException e) {
      e.printStackTrace();
    }
  }

  @Override
  protected void performPlayerTurnForAllPlayers() {
    do {
      performPlayerTurn();
    } while (!boardGame.getCurrentPlayer().equals(boardGame.getPlayers().getFirst()));
  }

  @Override
  protected void performPlayerTurn() {
    disableRollDiceButton();
    int diceRoll = ((LudoBoardGame) boardGame).rollDice();
    gameView.getGameMenuBox().animateDiceRoll(new int[]{diceRoll}, () -> {
      ((LudoBoardGame) boardGame).performPlayerTurn(diceRoll);
      enableRollDiceButton();
    });
  }

  /**
   * Sets the tile number of the player in the players box.
   *
   * @param player    the player
   * @param newTileId the new tile id
   */
  private void setPlayerTileNumber(Player player) {
    int finishedTokens = (int) ((LudoPlayer) player).getTokens().stream()
        .filter(t -> t.getStatus() == LudoToken.TokenStatus.FINISHED)
        .count();
    gameView.getPlayersBox().getPlayerRows().get(getPlayers().indexOf(player))
        .setTileNumber(player, String.valueOf(finishedTokens));
  }

  /**
   * Handles the player released event.
   *
   * @param player the player
   * @param tileId the tile id
   */
  public void onTokenReleased(Player player, int tileId, int tokenId) {
    gameView.getGameMenuBox().addGameLogRoundBoxEntry(
        player.getName() + " rolled 6 and released token " + (tokenId + 1));
    ((LudoGameStackPane) gameView.getGameStackPane()).releaseToken(((LudoPlayer) player), tokenId);
    setPlayerTileNumber(player);
  }

  /**
   * Restarts the game by initializing a new BoardGame instance with the same board and players.
   */
  @Override
  protected void restartGame() {
    List<Player> players = new ArrayList<>();
    boardGame.getPlayers().forEach(player -> players.add(new LudoPlayer(player.getName(),
        player.getColorHex(), player.getPlayerTokenType(), player.isBot())));

    initializeBoardGame(boardGame.getBoard(), players);
    initializeGameView();
  }

  public void onTokenMoved(Player player, LudoToken token, int diceRoll, int oldTileId,
      int newTileId) {
    gameView.getGameMenuBox().addGameLogRoundBoxEntry(
        player.getName() + " rolled " + diceRoll + " and moved token " + (token.getTokenId()));

    ((LudoGameStackPane) gameView.getGameStackPane()).moveToken(token,
        getBoard().getTile(oldTileId), getBoard().getTile(newTileId), false);
    setPlayerTileNumber(player);
  }

  @Override
  public void onRoundNumberIncremented(int roundNumber) {
    gameView.getPlayersBox().setRoundNumber(roundNumber);

    gameView.getGameMenuBox().addGameLogRoundBox(roundNumber);
  }

  @Override
  public void onCurrentPlayerChanged(Player player) {
    if (!gameView.getGameMenuBox().getRollForAllPlayersSelected()) {
      gameView.getPlayersBox().setFocusedPlayer(getPlayers().indexOf(player));
      
      // If it's a bot's turn and "roll for all" is not selected, automatically perform the turn.
      if (player.isBot() && !gameView.getGameMenuBox().getRollForAllPlayersSelected()) {
        performPlayerTurn();
      }
      return;
    }
    gameView.getPlayersBox().removeFocusedPlayer();
  }

  @Override
  public void onGameFinished(Player winner) {
    gameView.getGameMenuBox().addGameLogRoundBoxEntry("Game finished! Winner: " + winner.getName());
  }

  public void onTokenCaptured(Player player, LudoToken token, int oldTileId) {
    gameView.getGameMenuBox().addGameLogRoundBoxEntry(
        player.getName() + "'s token " + token.getTokenId()
            + " was captured and sent back to start!");

    ((LudoGameStackPane) gameView.getGameStackPane()).moveTokenToStartArea(player, token);
    setPlayerTileNumber(player);
  }

  public void onTokenFinished(Player player, LudoToken token) {
    gameView.getGameMenuBox().addGameLogRoundBoxEntry(
        player.getName() + "'s token " + token.getTokenId() + " finished!");
    setPlayerTileNumber(player);
  }

  public void onTurnSkipped(Player player, int diceRoll) {
    gameView.getGameMenuBox().addGameLogRoundBoxEntry(
        player.getName() + " rolled " + diceRoll + " but has no tokens to move. Turn skipped!");
  }

  @Override
  public void onButtonClicked(String buttonId) {
    switch (buttonId) {
      case "roll_dice" -> handleRollDiceButtonAction();
      case "restart_game" -> restartGame();
      case "quit_game" -> quitGame();
      default -> {
        break;
      }
    }
  }

  @Override
  public void onButtonClickedWithParams(String buttonId, Map<String, Object> params) {
    // Not needed
  }

}
