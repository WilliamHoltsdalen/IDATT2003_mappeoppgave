package edu.ntnu.idi.idatt.model.interfaces;

import edu.ntnu.idi.idatt.model.Board;
import edu.ntnu.idi.idatt.model.Player;

public interface TileAction {

  public void perform(Player player, Board board);
}
