package edu.ntnu.idi.idatt.view.common;

import edu.ntnu.idi.idatt.model.player.Player;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;

/**
 * GamePlayerRow.
 *
 * <p>Abstract JavaFX {@link HBox} representing a single player's information row
 * within an in-game player list (e.g., {@link GamePlayersBox}).</p>
 *
 * <p>It typically displays player-specific details like their name, token, and current tile number.
 * Subclasses must implement the initialization of these visual elements.</p>
 *
 * @see HBox
 * @see Player
 * @see GamePlayersBox
 */
public abstract class GamePlayerRow extends HBox {
  protected Label playerTile;

  /**
   * Constructs a GamePlayerRow for the given player.
   *
   * @param player The {@link Player} this row represents.
   */
  public GamePlayerRow(Player player) {
    this.getStyleClass().add("players-box-player-row");
    initialize(player);
  }

  /**
   * Initializes the visual components of this player row.
   * Subclasses must implement this to create and add elements like player name labels,
   * token displays, and the {@link #playerTile} label.
   *
   * @param player The {@link Player} data to use for initialization.
   */
  protected abstract void initialize(Player player);

  /**
   * Sets the text of the {@link #playerTile} label, which usually displays
   * the player's current tile number or status.
   * Also sets a tooltip with more detailed information.
   *
   * @param player The {@link Player} this row represents (used for tooltip text).
   * @param text The text to display on the tile label (e.g., tile number).
   */
  public void setTileNumber(Player player, String text) {
    playerTile.setText(text);
    playerTile.setTooltip(new Tooltip(player.getName() + " is on tile: " + text));
  }
}
