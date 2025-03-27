package edu.ntnu.idi.idatt.model.interfaces;

import edu.ntnu.idi.idatt.model.Board;
import edu.ntnu.idi.idatt.model.Player;

public interface TileAction {

  int getDestinationTileId();

  String getDescription();

  void setDestinationTileId(int destinationTileId);

  void setDescription(String description);

  void perform(Player player, Board board);
}
