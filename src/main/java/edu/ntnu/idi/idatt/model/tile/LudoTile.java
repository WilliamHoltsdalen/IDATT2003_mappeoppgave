package edu.ntnu.idi.idatt.model.tile;

/**
 * LudoTile.
 *
 * <p>Extends the {@link Tile} class to represent a tile in a Ludo game.
 * In addition to the properties inherited from {@link Tile} (ID, coordinates, next tile ID),
 * a {@code LudoTile} has a specific {@code type} (e.g., "track", "start", "finish", "home").
 * This type is used to determine special behaviors or rendering for the tile on a Ludo board.</p>
 *
 * @see Tile
 */
public class LudoTile extends Tile {

  private String type;

  /**
   * Constructs a {@code LudoTile} with the specified properties.
   *
   * @param tileId      The unique identifier for this tile.
   * @param coordinates An array {@code [row, col]} representing the tile's position on the board.
   * @param nextTileId  The ID of the tile that follows this one in the standard path.
   * @param type        A string describing the type of Ludo tile.
   */
  public LudoTile(int tileId, int[] coordinates, int nextTileId, String type) {
    super(tileId, coordinates, nextTileId);
    this.type = type;
  }

  /**
   * Gets the type of this Ludo tile.
   *
   * @return The string representing the tile type.
   */
  public String getType() {
    return type;
  }

  /**
   * Sets the type of this Ludo tile.
   *
   * @param type The new string representing the tile type.
   */
  public void setType(String type) {
    this.type = type;
  }
}
