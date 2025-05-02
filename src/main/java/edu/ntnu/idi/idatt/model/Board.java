package edu.ntnu.idi.idatt.model;

import edu.ntnu.idi.idatt.model.tile.Tile;
import static edu.ntnu.idi.idatt.model.validators.ArgumentValidator.boardAddTileValidator;
import static edu.ntnu.idi.idatt.model.validators.ArgumentValidator.boardGetTileValidator;
import static edu.ntnu.idi.idatt.model.validators.ArgumentValidator.boardSetDescriptionValidator;
import static edu.ntnu.idi.idatt.model.validators.ArgumentValidator.boardSetnameValidator;
import static edu.ntnu.idi.idatt.model.validators.ArgumentValidator.boardSetBackgroundValidator;
import static edu.ntnu.idi.idatt.model.validators.ArgumentValidator.boardSetPatternValidator;
import static edu.ntnu.idi.idatt.model.validators.ArgumentValidator.boardSetRowsAndColumnsValidator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <h3>Board class</h3>
 *
 * <p>This class represents a board in the game. It contains a name and a description, as well as a
 * map of tiles. The tiles are indexed by their IDs.
 */
public class Board {
  private String name;
  private String description;
  private int[] rowsAndColumns;
  private Map<Integer, Tile> tiles;
  private String background;
  private String pattern;

  /**
   * Constructor for Board class.
   *
   * @param name The name of the board.
   * @param description The description of the board.
   * @param rowsAndColumns The number of rows and columns in the board.
   * @param background The background of the board.
   * @param pattern The pattern of the board.
   */
  public Board(String name, String description, int[] rowsAndColumns, String background, String pattern) {
    this.tiles = new HashMap<>();

    setName(name);
    setDescription(description);
    setRowsAndColumns(rowsAndColumns);
    setBackground(background);
    setPattern(pattern);
  }

  /**
   * Returns the name of the board.
   *
   * @return The name of the board.
   */
  public String getName() {
    return name;
  }

  /**
   * Returns the description of the board.
   *
   * @return The description of the board.
   */
  public String getDescription() {
    return description;
  }

  /**
   * Returns the number of rows in the board.
   *
   * @return The number of rows in the board.
   */
  public int[] getRowsAndColumns() {
    return rowsAndColumns;
  }

  /**
   * Returns the tile with the given ID if it exists.
   *
   * @param tileId The ID of the tile to retrieve.
   * @return The tile with the given ID or null if it does not exist.
   */
  public Tile getTile(int tileId) {
    boardGetTileValidator(tileId, tiles.size());

    return tiles.get(tileId);
  }

  public List<Tile> getTiles() {
    return new ArrayList<>(tiles.values());
  }

  public int getTileCount() {
    return tiles.size() - 1;
  }

  /**
   * Returns the string representing the background of the board.
   *
   * @return The string representing the background of the board.
   */
  public String getBackground() {
    return background;
  }

  /**
   * Returns the string representing the pattern of the board.
   *
   * @return The string representing the pattern of the board.
   */
  public String getPattern() {
    return pattern;
  }

  /**
   * Sets the name of the board.
   *
   * @param name The name to set.
   */
  public void setName(String name) {
    boardSetnameValidator(name);
    this.name = name;
  }

  /**
   * Sets the description of the board.
   *
   * @param description The description to set.
   */
  public void setDescription(String description) {
    boardSetDescriptionValidator(description);
    this.description = description;
  }

  /**
   * Sets the number of rows and columns in the board.
   *
   * @param rowsAndColumns The number of rows and columns to set.
   */
  public void setRowsAndColumns(int[] rowsAndColumns) {
    boardSetRowsAndColumnsValidator(rowsAndColumns);
    this.rowsAndColumns = rowsAndColumns;
    this.tiles = createTiles(tiles, rowsAndColumns[0], rowsAndColumns[1]);
  }

  /**
   * Sets the string representing the background of the board.
   *
   * @param background The background to set.
   */
  public void setBackground(String background) {
    boardSetBackgroundValidator(background);
    this.background = background;
  }

  /**
   * Sets the string representing the pattern of the board.
   *
   * @param pattern The pattern to set.
   */
  public void setPattern(String pattern) {
    boardSetPatternValidator(pattern);
    this.pattern = pattern;
  }

  /**
   * Adds a tile to the board.
   *
   * @param tile The tile to add.
   */
  public void addTile(Tile tile) {
    boardAddTileValidator(tile);

    this.tiles.put(tile.getTileId(), tile);
  }

  /**
   * Creates a list of Tile objects that are arranged in a grid pattern with alternating directions
   * in each row. The first tile is in the lower left corner of the grid, and the direction of the
   * first row is left to right.
   *
   * @param rows The number of rows in the grid.
   * @param columns The number of columns in the grid.
   * @return The list of Tile objects.
   */
  private Map<Integer, Tile> createTiles(Map<Integer, Tile> oldTiles, int rows, int columns) {
    Map<Integer, Tile> newTiles = new HashMap<>();
    newTiles.put(0, new Tile(0, new int[]{0, -2}, 1)); // Add 0th tile outside of board

    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < columns; j++) {
        int baseId = i * columns;
        int tileId;
        int[] coordinates = new int[]{i, j};

        if (i % 2 == 0) { // Even numbered rows (left to right)
          tileId = baseId + j + 1;
        } else { // Odd numbered rows (right to left)
          tileId = baseId + (columns - j);
        }
        newTiles.put(tileId, new Tile(tileId, coordinates, tileId + 1));
        try {
          newTiles.get(tileId).setLandAction(oldTiles.get(tileId).getLandAction());
        } catch (NullPointerException | IllegalArgumentException e) {
          // Do nothing if the tile doesn't have an action
        }
      }
    }
    return newTiles;
  }
}
