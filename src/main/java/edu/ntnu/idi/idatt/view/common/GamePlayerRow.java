package edu.ntnu.idi.idatt.view.common;

import edu.ntnu.idi.idatt.model.player.Player;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;

public abstract class GamePlayerRow extends HBox {
  protected Label playerTile;

  public GamePlayerRow(Player player) {
    this.getStyleClass().add("game-players-box-player-row");
    initialize(player);
  }

  protected abstract void initialize(Player player);

  public void setTileNumber(Player player, String text) {
    playerTile.setText(text);
    playerTile.setTooltip(new Tooltip(player.getName() + " is on tile: " + text));
  }
}
