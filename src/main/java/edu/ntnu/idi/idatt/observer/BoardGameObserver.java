package edu.ntnu.idi.idatt.observer;

import edu.ntnu.idi.idatt.model.player.Player;

/**
 * An interface for observers that wish to be notified about board game events.
 */
public interface BoardGameObserver {
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
   * Called when the game has finished.
   *
   * @param winner The player who won the game.
   */
  void onGameFinished(Player winner);
}