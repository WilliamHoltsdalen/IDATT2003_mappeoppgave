package edu.ntnu.idi.idatt.entities;

import java.util.HashMap;
import java.util.Map;

public class Board {
  private final Map<Integer, Tile> tiles;

  public Board(int rows, int columns) {
    this.tiles = new HashMap<>();
    populateTiles(rows, columns);
  }

  public Tile getTile(int tileId) throws IllegalArgumentException {
    if (tileId < 0) {
      throw new IllegalArgumentException("Tile id must be greater than 0");
    }
    return tiles.get(tileId);
  }

  private void populateTiles(int rows, int columns) throws IllegalArgumentException {
    if (rows < 1 || columns < 1) {
      throw new IllegalArgumentException("Board must have at least 1 row and 1 column");
    }
    for (int i = 0; i < rows * columns; i++) {
      addTile(new Tile(i));
    }
  }

  public void addTile(Tile tile) throws IllegalArgumentException {
    if (tile == null) {
      throw new IllegalArgumentException("Tile cannot be null");
    }
    if (tile.getTileId() < 0) {
      throw new IllegalArgumentException("Tile id must be greater than 0");
    }
    this.tiles.put(tile.getTileId(), tile);
  }
}
