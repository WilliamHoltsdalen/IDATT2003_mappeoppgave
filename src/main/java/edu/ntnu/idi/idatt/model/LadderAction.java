package edu.ntnu.idi.idatt.model;

import edu.ntnu.idi.idatt.model.interfaces.TileAction;
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

  @Override
  public int getDestinationTileId() {
    return destinationTileId;
  }

  @Override
  public String getDescription() {
    return description;
  }

  @Override
  public void setDestinationTileId(int destinationTileId) {
    ladderActionSetDestinationTileIdValidator(destinationTileId);

    this.destinationTileId = destinationTileId;
  }

  @Override
  public void setDescription(String description) {
    ladderActionSetDescriptionValidator(description);

    this.description = description;
  }

  @Override
  public void perform(Player player, Board board) {
    ladderActionPerformValidator(player, board);

    player.placeOnTile(board.getTile(this.destinationTileId));
  }
}
