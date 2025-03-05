package edu.ntnu.idi.idatt.model;

public class LadderAction implements TileAction {
  private int destinationTileId;
  private String description;

  public LadderAction(int destinationTileId, String description) {
    setDestinationTileId(destinationTileId);
    setDescription(description);
  }

  public int getDestinationTileId() {
    return destinationTileId;
  }

  public String getDescription() {
    return description;
  }

  private void setDestinationTileId(int destinationTileId) throws IllegalArgumentException {
    if (destinationTileId < 0) {
      throw new IllegalArgumentException("Destination tile id must be greater than 0");
    }
    this.destinationTileId = destinationTileId;
  }

  private void setDescription(String description) throws IllegalArgumentException {
    if (description == null || description.isBlank()) {
      throw new IllegalArgumentException("Description cannot be null or blank");
    }
    this.description = description;
  }


  public void perform(Player player, Board board) throws IllegalArgumentException {
    if (player == null) {
      throw new IllegalArgumentException("Player cannot be null");
    }
    player.placeOnTile(board.getTile(this.destinationTileId));
  }
}
