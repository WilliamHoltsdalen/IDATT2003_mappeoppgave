package edu.ntnu.idi.idatt.entities;

public class Tile {
  private int tileId;
  private Tile nextTile;
  private TileAction landAction;

  public Tile(int tileId) {

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

  public void setNextTile(Tile nextTile) {
    if (nextTile == null) {
      throw new IllegalArgumentException("Next tile cannot be null");
    }
    this.nextTile = nextTile;
  }

  public void landPlayer(Player player) {

  }

  public void leavePlayer(Player player) {

  }
}
