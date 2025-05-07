package edu.ntnu.idi.idatt.model.player;

import static edu.ntnu.idi.idatt.model.validator.ArgumentValidator.ludoPlayerMoveTokenValidator;

import edu.ntnu.idi.idatt.model.tile.Tile;
import edu.ntnu.idi.idatt.model.token.LudoToken;
import edu.ntnu.idi.idatt.model.token.LudoToken.TokenStatus;
import java.util.ArrayList;
import java.util.List;

public class LudoPlayer extends Player {

  private final List<LudoToken> tokens;

  public LudoPlayer(String name, String colorHex, PlayerTokenType playerTokenType) {
    super(name, colorHex, playerTokenType);
    this.tokens = new ArrayList<>();
    for (int i = 1; i <= 4; i++) {
      tokens.add(new LudoToken(i));
    }
  }

  public List<LudoToken> getTokens() {
    return tokens;
  }

  public LudoToken getToken(int tokenId) {
    return tokens.stream()
        .filter(token -> token.getTokenId() == tokenId)
        .findFirst()
        .orElseThrow(
            () -> new IllegalArgumentException("Token with tokenId " + tokenId + " not found"));
  }

  /**
   * Moves the token with the given id to a new tile and updates its status.
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
