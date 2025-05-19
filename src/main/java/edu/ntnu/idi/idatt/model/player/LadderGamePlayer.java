package edu.ntnu.idi.idatt.model.player;

import static edu.ntnu.idi.idatt.model.validator.ArgumentValidator.ladderGamePlayerPlaceOnTileValidator;

import edu.ntnu.idi.idatt.model.tile.LadderGameTile;
import edu.ntnu.idi.idatt.model.tile.Tile;

/**
 * <h3>LadderGamePlayer.</h3>
 *
 * <p>Represents a player specifically in a Ladder Game. This class extends the general
 * {@link Player} class and adds functionality relevant to ladder games, primarily tracking
 * the player's current {@link LadderGameTile} on the board.</p>
 *
 * @see Player
 * @see LadderGameTile
 */
public class LadderGamePlayer extends Player {
  private LadderGameTile currentTile;
  
  /**
   * Constructs a new {@code LadderGamePlayer}.
   *
   * @param name             The name of the player.
   * @param colorHex         The hexadecimal string representation of the player's color.
   * @param playerTokenType  The {@link PlayerTokenType} chosen by the player.
   * @param isBot            A boolean indicating whether this player is a bot (AI-controlled).
   */
  public LadderGamePlayer(String name, String colorHex, PlayerTokenType playerTokenType,
      boolean isBot) {
    super(name, colorHex, playerTokenType, isBot);
    this.currentTile = null;
  }
  
  /**
  * Returns the {@link LadderGameTile} the player is currently on.
  *
  * @return The current {@link LadderGameTile} of the player, or {@code null} if not yet placed.
  */
  public Tile getCurrentTile() {
    return currentTile;
  }
  
  /**
  * Places the player on the specified {@link Tile}. The tile is validated to ensure it is
  * a {@link LadderGameTile} and not null.
  *
  * @param tile The {@link Tile} (must be a {@link LadderGameTile}) to place the player on.
  * @throws IllegalArgumentException if the provided tile is null or not an instance of
  *                                  {@code LadderGameTile}.
  */
  public void placeOnTile(Tile tile) {
    ladderGamePlayerPlaceOnTileValidator(tile);
    
    this.currentTile = (LadderGameTile) tile;
  }
}
