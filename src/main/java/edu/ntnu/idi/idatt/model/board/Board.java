package edu.ntnu.idi.idatt.model.board;

import edu.ntnu.idi.idatt.model.tile.Tile;
import java.util.List;

/**
 * Board interface
 *
 * <p>Interface defining common operations for all board game boards.
 */
public interface Board {
  /**
   * Gets the name of the board.
   *
   * @return the name of the board
   */
  String getName();

  /**
   * Gets the description of the board.
   *
   * @return the description of the board
   */
  String getDescription();

  /**
   * Gets the background of the board.
   *
   * @return the background of the board
   */
  String getBackground();

  /**
   * Gets a tile by its ID.
   *
   * @param tileId the ID of the tile to get
   * @return the tile with the given ID
   */
  Tile getTile(int tileId);

  /**
   * Gets all tiles on the board.
   *
   * @return a list of all tiles
   */
  List<Tile> getTiles();

  /**
   * Gets the total number of tiles on the board.
   *
   * @return the number of tiles
   */
  int getTileCount();

  /**
   * Sets the name of the board.
   *
   * @param name the name to set
   */
  void setName(String name);

  /**
   * Sets the description of the board.
   *
   * @param description the description to set
   */
  void setDescription(String description);

  /**
   * Sets the background of the board.
   *
   * @param background the background to set
   */
  void setBackground(String background);

  /**
   * Adds a tile to the board.
   *
   * @param tile the tile to add
   */
  void addTile(Tile tile);

  /**
   * Creates the tiles for the board.
   *
   * @param rows the number of rows
   * @param columns the number of columns
   */
  void createTiles(int rows, int columns);
}
