package edu.ntnu.idi.idatt.model.player;

import edu.ntnu.idi.idatt.model.tile.Tile;
import static edu.ntnu.idi.idatt.model.validator.ArgumentValidator.ladderGamePlayerPlaceOnTileValidator;

public class LadderGamePlayer extends Player {
  private Tile currentTile;
  
  public LadderGamePlayer(String name, String colorHex, PlayerTokenType playerTokenType) {
    super(name, colorHex, playerTokenType);
    this.currentTile = null;
  }
  
  /**
  * Returns the current tile of the player.
  *
  * @return The current tile of the player.
  */
  public Tile getCurrentTile() {
    return currentTile;
  }
  
  /**
  * Places the player on the given tile.
  *
  * @param tile The tile to place the player on.
  */
  public void placeOnTile(Tile tile) {
    ladderGamePlayerPlaceOnTileValidator(tile);
    
    this.currentTile = tile;
  }
  
  
  
  
  
  
}
