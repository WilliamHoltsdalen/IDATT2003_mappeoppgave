package edu.ntnu.idi.idatt.model.tile;

import static edu.ntnu.idi.idatt.model.validator.ArgumentValidator.tileSetCoordinatesValidator;
import static edu.ntnu.idi.idatt.model.validator.ArgumentValidator.tileSetNextTileIdValidator;
import static edu.ntnu.idi.idatt.model.validator.ArgumentValidator.tileSetTileIdValidator;

/**
 * <h3>Tile class</h3>
 *
 * <p>This class represents a tile in the game. It contains a tile ID, a next tile ID, and a tile
 * action.
 *
 * @see TileAction
 */
public abstract class Tile {
  private int tileId;
  private int[] coordinates;
  private int nextTileId;

  /**
   * Constructor for Tile class.
   *
   * @param tileId The ID of the tile.
   * @param nextTileId The ID of the next tile.
   */
  public Tile(int tileId, int[] coordinates, int nextTileId) {
    setTileId(tileId);
    setCoordinates(coordinates);
    setNextTileId(nextTileId);
  }

  /**
   * Constructor for Tile class.
   *
   * @param tileId The ID of the tile.
   * @param nextTileId The ID of the next tile.
   * @param tileAction The tile action to perform on the tile.
   */
  public Tile(int tileId, int[] coordinates, int nextTileId, TileAction tileAction) {
    setTileId(tileId);
    setCoordinates(coordinates);
    setNextTileId(nextTileId);
  }

  /**
   * Returns the ID of the tile.
   *
   * @return The ID of the tile.
   */
  public int getTileId() {
    return tileId;
  }

  /**
   * Returns the coordinates of the tile.
   *
   * @return The coordinates of the tile.
   */
  public int[] getCoordinates() {
    return coordinates;
  }

  /**
   * Returns the ID of the next tile.
   *
   * @return The ID of the next tile.
   */
  public int getNextTileId() {
    return nextTileId;
  }

  /**
   * Sets the ID of the tile.
   *
   * @param tileId The ID to set.
   */
  private void setTileId(int tileId) {
    tileSetTileIdValidator(tileId);

    this.tileId = tileId;
  }

  /**
   * Sets the coordinates of the tile.
   *
   * @param coordinates The coordinates to set.
   */
  private void setCoordinates(int[] coordinates) {
    tileSetCoordinatesValidator(coordinates);

    this.coordinates = coordinates;
  }

  /**
   * Sets the ID of the next tile.
   *
   * @param nextTileId The ID to set.
   */
  private void setNextTileId(int nextTileId) {
    tileSetNextTileIdValidator(this.tileId, nextTileId);

    this.nextTileId = nextTileId;
  }
}
