package edu.ntnu.idi.idatt.model;

import static edu.ntnu.idi.idatt.model.validators.ArgumentValidator.boardAddTileValidator;
import static edu.ntnu.idi.idatt.model.validators.ArgumentValidator.boardGetTileValidator;
import static edu.ntnu.idi.idatt.model.validators.ArgumentValidator.boardSetDescriptionValidator;
import static edu.ntnu.idi.idatt.model.validators.ArgumentValidator.boardSetnameValidator;

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
  private final Map<Integer, Tile> tiles;

  /**
   * Constructor for Board class.
   *
   * @param name The name of the board.
   * @param description The description of the board.
   */
  public Board(String name, String description) {
    setName(name);
    setDescription(description);

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

  private void setName(String name) {
    boardSetnameValidator(name);
    this.name = name;
  }

  private void setDescription(String description) {
    boardSetDescriptionValidator(description);
    this.description = description;
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
