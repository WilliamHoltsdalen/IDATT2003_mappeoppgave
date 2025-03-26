package edu.ntnu.idi.idatt.observer;

import edu.ntnu.idi.idatt.model.Player;

/**
 * An interface for observers that wish to receive notifications about board game events.
 */
public interface BoardGameObserver {
  /**
   * Called when a player moves to a new tile.
   *
   * @param player The player who moved.
   * @param newTileId The ID of the new tile.
   */
  void onPlayerMoved(Player player, int newTileId);

  /**
   * Called when the game state changes (e.g., round number increments, special events occur).
   *
   * @param stateUpdate A description of the state change.
   */
  void onGameStateChanged(String stateUpdate);

  /**
   * Called when the game has finished.
   *
   * @param winner The player who won the game.
   */
  void onGameFinished(Player winner);
}