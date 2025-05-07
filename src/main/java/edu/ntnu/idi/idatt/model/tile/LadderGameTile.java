package edu.ntnu.idi.idatt.model.tile;

import static edu.ntnu.idi.idatt.model.validator.ArgumentValidator.tileSetLandActionValidator;

public class LadderGameTile extends Tile {

  private TileAction landAction;

  public LadderGameTile(int tileId, int[] coordinates, int nextTileId) {
    super(tileId, coordinates, nextTileId);
  }

  public LadderGameTile(int tileId, int[] coordinates, int nextTileId, TileAction tileAction) {
    super(tileId, coordinates, nextTileId, tileAction);
    setLandAction(tileAction);
  }

  /**
   * Returns the tile action.
   *
   * @return The tile action.
   */
  public TileAction getLandAction() {
    return landAction;
  }

  /**
   * Sets the tile action.
   *
   * @param landAction The tile action to set.
   */
  public void setLandAction(TileAction landAction) {
    tileSetLandActionValidator(landAction);

    this.landAction = landAction;
  }
}

