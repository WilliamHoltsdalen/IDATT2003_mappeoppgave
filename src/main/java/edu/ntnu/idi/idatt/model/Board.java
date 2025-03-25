package edu.ntnu.idi.idatt.model;

import static edu.ntnu.idi.idatt.model.validators.ArgumentValidator.boardAddTileValidator;
import static edu.ntnu.idi.idatt.model.validators.ArgumentValidator.boardGetTileValidator;
import static edu.ntnu.idi.idatt.model.validators.ArgumentValidator.boardPopulateTilesValidator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Board {
  private final Map<Integer, Tile> tiles;

  public Board(int rows, int columns) {
    this.tiles = new HashMap<>();
    populateTiles(rows, columns);
  }

  public Board() {
    this.tiles = new HashMap<>();
  }

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

  private void populateTiles(int rows, int columns) {
    boardPopulateTilesValidator(rows, columns);

    for (int i = 0; i <= rows * columns; i++) {
      addTile(new Tile(i, i+1));
    }
  }

  public void addTile(Tile tile) {
    boardAddTileValidator(tile);

    this.tiles.put(tile.getTileId(), tile);
  }
}
