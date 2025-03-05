package edu.ntnu.idi.idatt.model;

public class Tile {
  private int tileId;
  private TileAction landAction;

  public Tile(int tileId) {
    setTileId(tileId);
  }

  public int getTileId() {
    return tileId;
  }

  public TileAction getLandAction() {
    return landAction;
  }

  private void setTileId(int tileId) throws IllegalArgumentException {
    if (tileId < 0) {
      throw new IllegalArgumentException("Tile id must be greater than 0");
    }
    this.tileId = tileId;
  }

  public void setLandAction(TileAction landAction) throws IllegalArgumentException {
    if (landAction == null) {
      throw new IllegalArgumentException("Land action cannot be null");
    }
    this.landAction = landAction;
  }

  public void landPlayer(Player player) {

  }

  public void leavePlayer(Player player) {

  }
}
