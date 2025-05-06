package edu.ntnu.idi.idatt.model.game;

import java.util.List;

import edu.ntnu.idi.idatt.controller.laddergame.LadderGameController;
import edu.ntnu.idi.idatt.model.board.Board;
import edu.ntnu.idi.idatt.model.player.LadderGamePlayer;
import edu.ntnu.idi.idatt.model.player.Player;
import edu.ntnu.idi.idatt.model.tile.LadderGameTile;
import edu.ntnu.idi.idatt.model.tile.Tile;
import edu.ntnu.idi.idatt.model.tile.TileAction;

/**
 * <h3>LadderBoardGame class</h3>
 *
 * <p>Implementation of the Chutes and Ladders game. This class extends BoardGame and implements
 * game-specific logic.
 */
public class LadderBoardGame extends BoardGame {

  /**
   * Constructor for LadderBoardGame.
   *
   * @param board The game board
   * @param players The list of players
   * @param diceCount The number of dice to use
   */
  public LadderBoardGame(Board board, List<Player> players, int diceCount) {
    super(board, players, diceCount);
  }

  @Override
  public void initializeGame() {
    logger.info("Game started!");

    players.forEach(player -> ((LadderGamePlayer) player).placeOnTile(board.getTile(0)));
    setCurrentPlayer(players.getFirst());
  }

  /**
   * Gets the winner of the game.
   * @return The winning player, or null if there is no winner
   */
  @Override
  public Player getWinner() {
    for (Player player : players) {
      if (((LadderGamePlayer) player).getCurrentTile().getTileId() == board.getTileCount()) {
        return player;
      }
    }
    return null;
  }

  public int rollDice() {
    dice.rollDice();
    return dice.getTotalValue();
  }

  public void performPlayerTurn(int diceRoll) {
    movePlayer(diceRoll);
    handleTileAction();
    checkWinCondition();
    updateCurrentPlayer();
    handleRoundNumber();
  }

  /**
   * Handles tile actions for the current player.
   */
  protected void handleTileAction() {
    TileAction landAction = ((LadderGameTile) ((LadderGamePlayer) currentPlayer).getCurrentTile()).getLandAction();
    if (landAction == null) {
      return;
    }
    landAction.perform(currentPlayer, board);
    notifyTileActionPerformed(currentPlayer, landAction);
    logger.info("Performed tile action: {}", landAction.getDescription());
  }

  /**
   * Finds the next tile for the given player based on the dice roll. There are two unique cases
   * for calculating the next tile.
   *
   * <p>In the following cases, the term 'expected tile' refers to the tile that the player is on,
   * plus the dice roll. (e.g. the player is on tile 20, with a dice roll of 3, the expected tile
   * is 23).
   * <ul>
   *   <li>If the id of the 'expected tile' is less than or equal to the board's tile count, the
   *       next tile is the 'expected tile'.
   *   <li>If the id of the 'expected tile' is greater than the board's tile count, the next tile
   *       is set as (tileCount - overshoot). E.g. if the player is on tile 85 with a dice roll of
   *       9 and the board has a tile count of 90, the next tile is 86.
   * </ul>
   *
   * @param player The player to find the next tile for
   * @param diceRoll The value of the dice roll to use in the calculation.
   * @return The next tile for the player, meaning the tile the specified player will move to.
   */
  private Tile findNextTile(Player player, int diceRoll) {
    int currentTileId = ((LadderGamePlayer) player).getCurrentTile().getTileId();
    int nextTileId = currentTileId + diceRoll;
    int tileCount = board.getTileCount();

    if (nextTileId <= tileCount) {
      return board.getTile(nextTileId);
    } else {
      return board.getTile(tileCount - (nextTileId - tileCount));
    }
  }

  /**
   * Rolls the dice for the current player and moves them to the new tile.
   */
  public void movePlayer(int diceRoll) {
    Tile nextTile = findNextTile(currentPlayer, diceRoll);
    ((LadderGamePlayer) currentPlayer).placeOnTile(nextTile);
    notifyPlayerMoved(currentPlayer, diceRoll, nextTile.getTileId());
  }

  /**
   * Notifies the observers that a tile action has been performed.
   * 
   * @param player the player that performed the action
   * @param tileAction the tile action
   */
  public void notifyTileActionPerformed(Player player, TileAction tileAction) {
    observers.forEach(observer -> ((LadderGameController) observer).onTileActionPerformed(player, tileAction));
  }

  private void notifyPlayerMoved(Player player, int diceRoll, int newTileId) {
    observers.forEach(observer -> ((LadderGameController) observer).onPlayerMoved(player, diceRoll, newTileId));
  }
} 