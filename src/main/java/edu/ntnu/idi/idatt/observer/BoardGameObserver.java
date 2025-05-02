package edu.ntnu.idi.idatt.observer;

import edu.ntnu.idi.idatt.model.Player;
import edu.ntnu.idi.idatt.model.tile.TileAction;

/**
 * An interface for observers that wish to be notified about board game events.
 */
public interface BoardGameObserver {
  /**
   * Called when a player moves to a new tile.
   *
   * @param player The player who moved.
   * @param diceRoll The value of the dice roll the player made.
   * @param newTileId The ID of the new tile.
   */
  void onPlayerMoved(Player player, int diceRoll, int newTileId);

  /**
   * Called when the round number has been incremented.
   *
   * @param roundNumber The new round number.
   */
  void onRoundNumberIncremented(int roundNumber);

  /**
   * Called when the current player changes.
   *
   * @param player The new current player.
   */
  void onCurrentPlayerChanged(Player player);

  /**
   * Called when a tile action, like a ladderAction, has been activated/performed.
   *
   * @param tileAction the tile action that was performed.
   * @param player The player who performed the action.
   */
  void onTileActionPerformed(Player player, TileAction tileAction);

  /**
   * Called when the game has finished.
   *
   * @param winner The player who won the game.
   */
  void onGameFinished(Player winner);
}