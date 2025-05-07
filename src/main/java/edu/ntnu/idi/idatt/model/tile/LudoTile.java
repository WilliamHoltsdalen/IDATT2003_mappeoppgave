package edu.ntnu.idi.idatt.model.tile;

public class LudoTile extends Tile {

  private String type;

  public LudoTile(int tileId, int[] coordinates, int nextTileId, String type) {
    super(tileId, coordinates, nextTileId);
    this.type = type;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }
}
