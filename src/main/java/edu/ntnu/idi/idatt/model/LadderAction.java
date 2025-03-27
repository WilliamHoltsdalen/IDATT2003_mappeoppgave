package edu.ntnu.idi.idatt.model;

import static edu.ntnu.idi.idatt.model.validators.ArgumentValidator.ladderActionPerformValidator;
import static edu.ntnu.idi.idatt.model.validators.ArgumentValidator.ladderActionSetDescriptionValidator;
import static edu.ntnu.idi.idatt.model.validators.ArgumentValidator.ladderActionSetDestinationTileIdValidator;

import edu.ntnu.idi.idatt.model.interfaces.TileAction;

/**
 * <h3>LadderAction class</h3>
 *
 * <p>This class represents a ladder action, which is a tile action that moves the player to a
 * different tile. It contains a destination tile ID and a description.
 */
public class LadderAction implements TileAction {
  private int destinationTileId;
  private String description;

  /**
   * Constructor for LadderAction class.
   *
   * @param destinationTileId The ID of the destination tile.
   * @param description The description of the action.
   */
  public LadderAction(int destinationTileId, String description) {
    setDestinationTileId(destinationTileId);
    setDescription(description);
  }

  /**
   * Returns the destination tile ID.
   *
   * @return The destination tile ID.
   */
  @Override
  public int getDestinationTileId() {
    return destinationTileId;
  }

  /**
   * Returns the description of the action.
   *
   * @return The description of the action.
   */
  @Override
  public String getDescription() {
    return description;
  }

  /**
   * Sets the destination tile ID.
   *
   * @param destinationTileId The destination tile ID to set.
   */
  @Override
  public void setDestinationTileId(int destinationTileId) {
    ladderActionSetDestinationTileIdValidator(destinationTileId);

    this.destinationTileId = destinationTileId;
  }

  /**
   * Sets the description of the action.
   *
   * @param description The description to set.
   */
  @Override
  public void setDescription(String description) {
    ladderActionSetDescriptionValidator(description);

    this.description = description;
  }

  /**
   * Performs the action on the given player and board.
   *
   * @param player The player to perform the action on.
   * @param board The board to perform the action on.
   */
  @Override
  public void perform(Player player, Board board) {
    ladderActionPerformValidator(player, board);

    player.placeOnTile(board.getTile(this.destinationTileId));
  }
}
