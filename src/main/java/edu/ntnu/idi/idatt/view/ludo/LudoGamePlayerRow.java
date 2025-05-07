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

public class LudoGamePlayerRow extends GamePlayerRow {

  public LudoGamePlayerRow(Player player) {
    super(player);
  }

  @Override
  protected void initialize(Player player) {
    Shape playerToken = PlayerTokenFactory.create(7, Color.web(player.getColorHex()),
        player.getPlayerTokenType());
    playerToken.getStyleClass().add("game-players-box-player-token");

    Text playerName = new Text(player.getName());
    playerName.getStyleClass().add("game-players-box-player-name");

    Region spacer = new Region();
    spacer.getStyleClass().add("game-players-box-player-spacer");
    HBox.setHgrow(spacer, Priority.ALWAYS);

    LudoPlayer ludoPlayer = (LudoPlayer) player;
    int finishedTokens = ludoPlayer.getTokens().stream()
        .filter(token -> token.getStatus() == TokenStatus.FINISHED)
        .collect(Collectors.toList())
        .size();

    playerTile = new Label(finishedTokens + "/" + ludoPlayer.getTokens().size());
    playerTile.getStyleClass().add("game-players-box-player-tile");
    playerTile.setTooltip(
        new Tooltip(player.getName() + " has " + finishedTokens + " finished tokens"));

    this.getChildren().addAll(playerToken, playerName, spacer, playerTile);
  }

  @Override
  public void setTileNumber(Player player, String text) {
    playerTile.setText(text + "/4");
    playerTile.setTooltip(new Tooltip(player.getName() + " has " + text + " finished tokens"));
  }
}
