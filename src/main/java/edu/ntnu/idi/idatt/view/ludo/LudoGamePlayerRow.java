package edu.ntnu.idi.idatt.view.ludo;

import edu.ntnu.idi.idatt.factory.view.PlayerTokenFactory;
import edu.ntnu.idi.idatt.model.player.LudoPlayer;
import edu.ntnu.idi.idatt.model.player.Player;
import edu.ntnu.idi.idatt.model.token.LudoToken.TokenStatus;
import edu.ntnu.idi.idatt.view.common.GamePlayerRow;
import java.util.stream.Collectors;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;

/**
 * LudoGamePlayerRow.
 *
 * <p>Extends {@link GamePlayerRow} to represent a single player's row in the Ludo game's
 * players list. This class displays the player's token, name, and the number of their tokens
 * that have reached the finished state (e.g., "2/4").</p>
 *
 * @see GamePlayerRow
 * @see LudoPlayer
 * @see PlayerTokenFactory
 */
public class LudoGamePlayerRow extends GamePlayerRow {

  /**
   * Constructs a {@code LudoGamePlayerRow} for the given player.
   *
   * @param player The {@link Player} (expected to be a {@link LudoPlayer}) to display.
   */
  public LudoGamePlayerRow(Player player) {
    super(player);
  }

  /**
   * Initializes the visual components of the player row for a Ludo game.
   * This includes creating the player's colored token, their name label, a spacer,
   * and a label showing the count of finished tokens out of the total.
   *
   * @param player The {@link Player} (expected to be a {@link LudoPlayer}) whose information
   *               is being displayed.
   */
  @Override
  protected void initialize(Player player) {
    final Shape playerToken = PlayerTokenFactory.create(10, Color.web(player.getColorHex()),
        player.getPlayerTokenType());

    Text playerName = new Text(player.getName());
    playerName.getStyleClass().add("players-box-player-name");

    Region spacer = new Region();
    HBox.setHgrow(spacer, Priority.ALWAYS);

    LudoPlayer ludoPlayer = (LudoPlayer) player;
    int finishedTokens = ludoPlayer.getTokens().stream()
        .filter(token -> token.getStatus() == TokenStatus.FINISHED)
        .collect(Collectors.toList())
        .size();

    playerTile = new Label(finishedTokens + "/" + ludoPlayer.getTokens().size());
    playerTile.getStyleClass().add("players-box-player-tile");
    playerTile.setTooltip(
        new Tooltip(player.getName() + " has " + finishedTokens + " finished tokens"));

    this.getChildren().addAll(playerToken, playerName, spacer, playerTile);
  }

  /**
   * Updates the text displaying the number of finished tokens for the player.
   * The text is formatted as "finishedTokens/totalTokens" (e.g., "text/4").
   *
   * @param player The {@link Player} whose token count is being updated.
   * @param text   The string representing the number of finished tokens.
   */
  @Override
  public void setTileNumber(Player player, String text) {
    playerTile.setText(text + "/4");
    playerTile.setTooltip(new Tooltip(player.getName() + " has " + text + " finished tokens"));
  }
}
