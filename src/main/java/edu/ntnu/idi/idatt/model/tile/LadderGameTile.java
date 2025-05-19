package edu.ntnu.idi.idatt.model.tile;

import static edu.ntnu.idi.idatt.model.validator.ArgumentValidator.tileSetLandActionValidator;

/**
 * <h3>LadderGameTile.</h3>
 *
 * <p>Represents a specialized {@link Tile} for a ladder game. In addition to the standard
 * tile properties, a {@code LadderGameTile} can have a specific {@link TileAction}
 * associated with it, which is triggered when a player lands on this tile.</p>
 *
 * @see Tile
 * @see TileAction
 */
public class LadderGameTile extends Tile {

  private TileAction landAction;

  /**
   * Constructs a {@code LadderGameTile} with a tile ID, coordinates, and the ID of the next
   * tile in sequence. The land action is initially null.
   *
   * @param tileId      The unique identifier for this tile.
   * @param coordinates An array representing the (row, column) position of this tile on the board.
   * @param nextTileId  The ID of the tile that logically follows this one in the normal game flow.
   */
  public LadderGameTile(int tileId, int[] coordinates, int nextTileId) {
    super(tileId, coordinates, nextTileId);
  }

  /**
   * Constructs a {@code LadderGameTile} with a tile ID, coordinates, the ID of the next tile,
   * and a specific {@link TileAction} to be performed when a player lands on this tile.
   *
   * @param tileId      The unique identifier for this tile.
   * @param coordinates An array representing the (row, column) position of this tile on the board.
   * @param nextTileId  The ID of the tile that logically follows this one.
   * @param tileAction  The {@link TileAction} associated with landing on this tile.
   */
  public LadderGameTile(int tileId, int[] coordinates, int nextTileId, TileAction tileAction) {
    super(tileId, coordinates, nextTileId, tileAction);
    setLandAction(tileAction);
  }

  /**
   * Returns the {@link TileAction} that occurs when a player lands on this tile.
   * This could be, for example, moving to a different tile (ladder/slide) or another effect.
   *
   * @return The {@link TileAction} associated with this tile, or {@code null} if none.
   */
  public TileAction getLandAction() {
    return landAction;
  }

  /**
   * Sets the {@link TileAction} to be performed when a player lands on this tile.
   * The provided action is validated using {@code tileSetLandActionValidator}.
   *
   * @param landAction The {@link TileAction} to set. Must not be null.
   * @throws IllegalArgumentException if {@code landAction} is null.
   */
  public void setLandAction(TileAction landAction) {
    tileSetLandActionValidator(landAction);

    this.landAction = landAction;
  }
}

