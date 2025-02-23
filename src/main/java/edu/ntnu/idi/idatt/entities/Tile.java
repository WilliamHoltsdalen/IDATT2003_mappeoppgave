package edu.ntnu.idi.idatt.entities;

public class Tile {
  private int tileId;
  private Tile nextTile;
  private TileAction landAction;

  public Tile(int tileId) {
    setTileId(tileId);
  }

  public int getTileId() {
    return tileId;
  }

  public Tile getNextTile() {
    return nextTile;
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

  public void setNextTile(Tile nextTile) throws IllegalArgumentException {
    if (nextTile == null) {
      throw new IllegalArgumentException("Next tile cannot be null");
    }
    this.nextTile = nextTile;
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
