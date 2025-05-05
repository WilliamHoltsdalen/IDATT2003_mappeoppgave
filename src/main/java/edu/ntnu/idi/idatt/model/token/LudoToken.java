package edu.ntnu.idi.idatt.model.token;

import edu.ntnu.idi.idatt.model.tile.Tile;

public class LudoToken {
  private final int tokenId;
  private TokenStatus status;
  private Tile currentTile;

  public LudoToken(int tokenId) {
    this.tokenId = tokenId;
    this.status = TokenStatus.NOT_RELEASED;
    this.currentTile = null;
  }

  public int getTokenId() {
    return tokenId;
  }

  public TokenStatus getStatus() {
    return status;
  }

  public Tile getCurrentTile() {
    return currentTile;
  }

  public void setStatus(TokenStatus status) {
    this.status = status;
  }

  public void setCurrentTile(Tile currentTile) {
    this.currentTile = currentTile;
  }

  public enum TokenStatus {
    NOT_RELEASED,
    RELEASED,
    FINISHED
  }
}