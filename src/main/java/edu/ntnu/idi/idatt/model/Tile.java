package edu.ntnu.idi.idatt.model;

import static edu.ntnu.idi.idatt.model.validators.ArgumentValidator.tileSetLandActionValidator;
import static edu.ntnu.idi.idatt.model.validators.ArgumentValidator.tileSetTileIdValidator;

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

  private void setTileId(int tileId) {
    tileSetTileIdValidator(tileId);

    this.tileId = tileId;
  }

  public void setLandAction(TileAction landAction) {
    tileSetLandActionValidator(landAction);

    this.landAction = landAction;
  }

  public void landPlayer(Player player) {

  }

  public void leavePlayer(Player player) {

  }
}
