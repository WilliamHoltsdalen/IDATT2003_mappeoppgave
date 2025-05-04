package edu.ntnu.idi.idatt.observer;

import edu.ntnu.idi.idatt.model.player.Player;
import edu.ntnu.idi.idatt.model.tile.TileAction;

/**
 * <h3>BoardGameSubject interface</h3>
 * 
 * <p>This interface defines the methods for a board game subject in the observer pattern.
 */
public interface BoardGameSubject {

  /**
   * Adds an observer to the subject.
   * 
   * @param observer the observer to add
   */
  void addObserver(BoardGameObserver observer);

  /**
   * Removes an observer from the subject.
   * 
   * @param observer the observer to remove
   */
  void removeObserver(BoardGameObserver observer);

  /**
   * Notifies the observers that a player has moved.
   * 
   * @param player the player that moved
   * @param diceRoll the dice roll
   * @param newTileId the new tile id
   */
  void notifyPlayerMoved(Player player, int diceRoll, int newTileId);

  /**
   * Notifies the observers that the round number has incremented.
   * 
   * @param roundNumber the new round number
   */
  void notifyRoundNumberIncremented(int roundNumber);

  /**
   * Notifies the observers that the current player has changed.
   * 
   * @param player the new current player
   */
  void notifyCurrentPlayerChanged(Player player);

  /**
   * Notifies the observers that a tile action has been performed.
   * 
   * @param player the player that performed the action
   * @param tileAction the tile action
   */
  void notifyTileActionPerformed(Player player, TileAction tileAction);

  /**
   * Notifies the observers that the game has finished.
   * 
   * @param winner the winner of the game
   */
  void notifyGameFinished(Player winner);
}
