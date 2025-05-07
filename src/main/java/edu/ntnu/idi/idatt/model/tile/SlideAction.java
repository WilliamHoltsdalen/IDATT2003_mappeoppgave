package edu.ntnu.idi.idatt.model.tile;

import static edu.ntnu.idi.idatt.model.validator.ArgumentValidator.tileActionPerformValidator;
import static edu.ntnu.idi.idatt.model.validator.ArgumentValidator.tileActionSetDescriptionValidator;
import static edu.ntnu.idi.idatt.model.validator.ArgumentValidator.tileActionSetDestinationTileIdValidator;
import static edu.ntnu.idi.idatt.model.validator.ArgumentValidator.tileActionSetIdentifierValidator;

import edu.ntnu.idi.idatt.model.board.Board;
import edu.ntnu.idi.idatt.model.player.LadderGamePlayer;
import edu.ntnu.idi.idatt.model.player.Player;

/**
 * <h3>SlideAction class</h3>
 *
 * <p>This class represents a slide action, which is a tile action that moves the player to a
 * different tile. It contains a destination tile ID and a description.
 */
public class SlideAction implements TileAction {

  private String identifier;
  private int destinationTileId;
  private String description;

  /**
   * Constructor for SlideAction class.
   *
   * @param identifier        The identifier of the action.
   * @param destinationTileId The ID of the destination tile.
   * @param description       The description of the action.
   */
  public SlideAction(String identifier, int destinationTileId, String description) {
    setIdentifier(identifier);
    setDestinationTileId(destinationTileId);
    setDescription(description);
  }

  /**
   * Returns the identifier of the action.
   *
   * @return The identifier of the action.
   */
  @Override
  public String getIdentifier() {
    return identifier;
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
   * Sets the identifier of the action.
   *
   * @param identifier The identifier to set.
   */
  @Override
  public void setIdentifier(String identifier) {
    tileActionSetIdentifierValidator(identifier);

    this.identifier = identifier;
  }

  /**
   * Sets the destination tile ID.
   *
   * @param destinationTileId The destination tile ID to set.
   */
  @Override
  public void setDestinationTileId(int destinationTileId) {
    tileActionSetDestinationTileIdValidator(destinationTileId);

    this.destinationTileId = destinationTileId;
  }

  /**
   * Sets the description of the action.
   *
   * @param description The description to set.
   */
  @Override
  public void setDescription(String description) {
    tileActionSetDescriptionValidator(description);

    this.description = description;
  }

  /**
   * Performs the action on the given player and board.
   *
   * @param player The player to perform the action on.
   * @param board  The board to perform the action on.
   */
  @Override
  public void perform(Player player, Board board) {
    tileActionPerformValidator(player, board);

    ((LadderGamePlayer) player).placeOnTile(board.getTile(this.destinationTileId));
  }
}
