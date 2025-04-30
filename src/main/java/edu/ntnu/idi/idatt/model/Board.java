package edu.ntnu.idi.idatt.model;

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
  private final Map<Integer, Tile> tiles;
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
    setName(name);
    setDescription(description);
    setRowsAndColumns(rowsAndColumns);
    setBackground(background);
    setPattern(pattern);

    this.tiles = new HashMap<>();
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

  private void setName(String name) {
    boardSetnameValidator(name);
    this.name = name;
  }

  private void setDescription(String description) {
    boardSetDescriptionValidator(description);
    this.description = description;
  }

  private void setRowsAndColumns(int[] rowsAndColumns) {
    boardSetRowsAndColumnsValidator(rowsAndColumns);
    this.rowsAndColumns = rowsAndColumns;
  }

  private void setBackground(String background) {
    boardSetBackgroundValidator(background);
    this.background = background;
  }

  private void setPattern(String pattern) {
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
}
