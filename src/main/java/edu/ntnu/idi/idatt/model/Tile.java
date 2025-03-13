package edu.ntnu.idi.idatt.model;

import edu.ntnu.idi.idatt.model.interfaces.TileAction;
import static edu.ntnu.idi.idatt.model.validators.ArgumentValidator.tileSetLandActionValidator;
import static edu.ntnu.idi.idatt.model.validators.ArgumentValidator.tileSetNextTileIdValidator;
import static edu.ntnu.idi.idatt.model.validators.ArgumentValidator.tileSetTileIdValidator;

public class Tile {
  private int tileId;
  private int nextTileId;
  private TileAction landAction;

  public Tile(int tileId, int nextTileId) {
    setTileId(tileId);
    setNextTileId(nextTileId);
  }

  public int getTileId() {
    return tileId;
  }

  public int getNextTileId() {
    return nextTileId;
  }

  public TileAction getLandAction() {
    return landAction;
  }

  private void setTileId(int tileId) {
    tileSetTileIdValidator(tileId);

    this.tileId = tileId;
  }

  private void setNextTileId(int nextTileId) {
    tileSetNextTileIdValidator(this.tileId, nextTileId);

    this.nextTileId = nextTileId;
  }

  public void setLandAction(TileAction landAction) {
    tileSetLandActionValidator(landAction);

    this.landAction = landAction;
  }
}
