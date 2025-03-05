package edu.ntnu.idi.idatt.model;

import static edu.ntnu.idi.idatt.model.validators.ArgumentValidator.ladderActionPerformValidator;
import static edu.ntnu.idi.idatt.model.validators.ArgumentValidator.ladderActionSetDescriptionValidator;
import static edu.ntnu.idi.idatt.model.validators.ArgumentValidator.ladderActionSetDestinationTileIdValidator;

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

  private void setDestinationTileId(int destinationTileId) {
    ladderActionSetDestinationTileIdValidator(destinationTileId);

    this.destinationTileId = destinationTileId;
  }

  private void setDescription(String description) {
    ladderActionSetDescriptionValidator(description);

    this.description = description;
  }

  public void perform(Player player, Board board) {
    ladderActionPerformValidator(player, board);

    player.placeOnTile(board.getTile(this.destinationTileId));
  }
}
