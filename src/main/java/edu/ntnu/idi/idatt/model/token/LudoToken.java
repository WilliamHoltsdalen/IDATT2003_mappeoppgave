package edu.ntnu.idi.idatt.model.token;

import edu.ntnu.idi.idatt.model.tile.Tile;

/**
 * <h3>LudoToken.</h3>
 *
 * <p>Represents a single game piece (token) in a Ludo game. Each token has a unique ID
 * (within its player's set of tokens), a {@link TokenStatus} indicating its current state (e.g., in
 * the start area, on the track, or finished), and a reference to the {@link Tile} it currently
 * occupies (if any).</p>
 *
 * @see Tile
 * @see TokenStatus
 */
public class LudoToken {

  private final int tokenId;
  private TokenStatus status;
  private Tile currentTile;

  /**
   * Constructs a {@code LudoToken} with the given ID. Initially, the token's status is set to
   * {@link TokenStatus#NOT_RELEASED} and its current tile is {@code null}.
   *
   * @param tokenId The unique identifier for this token (typically 1-4 for Ludo).
   */
  public LudoToken(int tokenId) {
    this.tokenId = tokenId;
    this.status = TokenStatus.NOT_RELEASED;
    this.currentTile = null;
  }

  /**
   * Gets the unique ID of this token.
   *
   * @return The token ID.
   */
  public int getTokenId() {
    return tokenId;
  }

  /**
   * Gets the current status of this token.
   *
   * @return The {@link TokenStatus} of the token.
   */
  public TokenStatus getStatus() {
    return status;
  }

  /**
   * Gets the tile this token currently occupies.
   *
   * @return The current {@link Tile}, or {@code null} if the token is not on a tile (e.g., not
   *     released).
   */
  public Tile getCurrentTile() {
    return currentTile;
  }

  /**
   * Sets the status of this token.
   *
   * @param status The new {@link TokenStatus} for the token.
   */
  public void setStatus(TokenStatus status) {
    this.status = status;
  }

  /**
   * Sets the current tile for this token.
   *
   * @param currentTile The new {@link Tile} the token occupies.
   */
  public void setCurrentTile(Tile currentTile) {
    this.currentTile = currentTile;
  }

  /**
   * Enum representing the possible statuses of a {@link LudoToken}.
   * <ul>
   *   <li>{@code NOT_RELEASED}: The token is in the player's start area, not yet on the main
   *   track.</li>
   *   <li>{@code RELEASED}: The token is on the main track or a player's finish path.</li>
   *   <li>{@code FINISHED}: The token has reached the final home position.</li>
   * </ul>
   */
  public enum TokenStatus {
    NOT_RELEASED,
    RELEASED,
    FINISHED
  }
}