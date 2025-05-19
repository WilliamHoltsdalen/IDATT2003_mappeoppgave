package edu.ntnu.idi.idatt.view.laddergame;

import edu.ntnu.idi.idatt.factory.view.PlayerTokenFactory;
import edu.ntnu.idi.idatt.model.player.LadderGamePlayer;
import edu.ntnu.idi.idatt.model.player.Player;
import edu.ntnu.idi.idatt.view.common.GamePlayerRow;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;

/**
 * <h3>LadderGamePlayerRow.</h3>
 *
 * <p>Represents a single row in the {@link LadderGamePlayersBox}, displaying information
 * for a specific {@link LadderGamePlayer}. This includes the player's token, name,
 * and current tile number.</p>
 *
 * @see GamePlayerRow
 * @see LadderGamePlayer
 * @see PlayerTokenFactory
 */
public class LadderGamePlayerRow extends GamePlayerRow {

  /**
   * Constructs a {@code LadderGamePlayerRow} for the given player.
   *
   * @param player The {@link Player} (expected to be a {@link LadderGamePlayer}) to display.
   */
  public LadderGamePlayerRow(Player player) {
    super(player);
  }

  /**
   * Initializes the visual components of the player row. This includes creating the player's
   * token, name label, and a label for their current tile number. A tooltip is also added to the
   * tile number label to show more detailed information on hover.
   *
   * @param player The {@link Player} (cast to {@link LadderGamePlayer}) for whom this row is being
   *               initialized.
   */
  @Override
  protected void initialize(Player player) {
    final Shape playerToken = PlayerTokenFactory.create(10, Color.web(player.getColorHex()),
        player.getPlayerTokenType());

    Text playerName = new Text(player.getName());
    playerName.getStyleClass().add("players-box-player-name");

    Region spacer = new Region();
    HBox.setHgrow(spacer, Priority.ALWAYS);

    playerTile = new Label(
        String.valueOf(((LadderGamePlayer) player).getCurrentTile().getTileId()));
    playerTile.getStyleClass().add("players-box-player-tile");
    playerTile.setTooltip(new Tooltip(player.getName() + " is on tile: "
        + ((LadderGamePlayer) player).getCurrentTile().getTileId()));

    this.getChildren().addAll(playerToken, playerName, spacer, playerTile);
  }
}
