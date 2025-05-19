package edu.ntnu.idi.idatt.model.player;

import static edu.ntnu.idi.idatt.model.validator.ArgumentValidator.ludoPlayerMoveTokenValidator;

import edu.ntnu.idi.idatt.model.tile.Tile;
import edu.ntnu.idi.idatt.model.token.LudoToken;
import edu.ntnu.idi.idatt.model.token.LudoToken.TokenStatus;
import java.util.ArrayList;
import java.util.List;

/**
 * <h3>LudoPlayer.</h3>
 *
 * <p>Extends the {@link Player} class to represent a player in a Ludo game.
 * Each Ludo player has a set of four {@link LudoToken}s. This class provides
 * methods to access these tokens and to move them on the game board, updating
 * their status accordingly.</p>
 *
 * @see Player
 * @see LudoToken
 * @see Tile
 * @see TokenStatus
 */
public class LudoPlayer extends Player {

  private final List<LudoToken> tokens;

  /**
   * Constructs a {@code LudoPlayer} with the specified name, color, token type, and bot status.
   * Initializes the player with four {@link LudoToken}s, each with a unique ID from 1 to 4.
   *
   * @param name            The name of the player.
   * @param colorHex        The hexadecimal string representation of the player's color.
   * @param playerTokenType The type of token the player uses.
   * @param isBot           A boolean indicating whether the player is a "robot" or not.
   */
  public LudoPlayer(String name, String colorHex, PlayerTokenType playerTokenType, boolean isBot) {
    super(name, colorHex, playerTokenType, isBot);
    this.tokens = new ArrayList<>();
    for (int i = 1; i <= 4; i++) {
      tokens.add(new LudoToken(i));
    }
  }

  /**
   * Returns the list of {@link LudoToken}s belonging to this player.
   *
   * @return A list of {@link LudoToken}s.
   */
  public List<LudoToken> getTokens() {
    return tokens;
  }

  /**
   * Retrieves a specific {@link LudoToken} by its ID.
   *
   * @param tokenId The ID of the token to retrieve (1-4).
   * @return The {@link LudoToken} with the specified ID.
   * @throws IllegalArgumentException if no token with the given ID is found.
   */
  public LudoToken getToken(int tokenId) {
    return tokens.stream()
        .filter(token -> token.getTokenId() == tokenId)
        .findFirst()
        .orElseThrow(
            () -> new IllegalArgumentException("Token with tokenId " + tokenId + " not found"));
  }

  /**
   * Moves the token with the given id to a new tile and updates its status.
   * Input parameters are validated by
   * {@link ArgumentValidator#ludoPlayerMoveTokenValidator(int, Tile, TokenStatus)}.
   *
   * @param pieceId the ID of the token to move
   * @param tile    the new tile to move the token to
   * @param status  the new status of the token
   */
  public void moveToken(int pieceId, Tile tile, TokenStatus status) {
    ludoPlayerMoveTokenValidator(pieceId, tile, status);
    LudoToken token = getToken(pieceId);
    token.setCurrentTile(tile);
    token.setStatus(status);
  }
}
