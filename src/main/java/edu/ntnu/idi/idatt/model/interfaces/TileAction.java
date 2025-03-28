package edu.ntnu.idi.idatt.model.interfaces;

import edu.ntnu.idi.idatt.model.Board;
import edu.ntnu.idi.idatt.model.Player;

/**
 * <h3>TileAction interface</h3>
 *
 * <p>This interface defines methods for performing actions on tiles.
 */
public interface TileAction {

  /**
   * Returns the destination tile ID.
   */
  int getDestinationTileId();

  /**
   * Returns the description of the action.
   */
  String getDescription();

  /**
   * Sets the destination tile ID.
   *
   * @param destinationTileId The destination tile ID to set.
   */
  void setDestinationTileId(int destinationTileId);

  /**
   * Sets the description of the action.
   *
   * @param description The description to set.
   */
  void setDescription(String description);

  /**
   * Performs the action on the given player and board.
   *
   * @param player The player to perform the action on.
   * @param board The board to perform the action on.
   */
  void perform(Player player, Board board);
}
