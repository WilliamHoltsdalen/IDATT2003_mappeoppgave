package edu.ntnu.idi.idatt.view.component;

import edu.ntnu.idi.idatt.factory.view.PlayerTokenFactory;
import edu.ntnu.idi.idatt.model.player.Player;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;

public class GamePlayerRow extends HBox {
  private Label playerTile;

  public GamePlayerRow(Player player) {
    this.getStyleClass().add("game-players-box-player-row");
    initialize(player);
  }

  private void initialize(Player player) {
    Shape playerToken = PlayerTokenFactory.create(7, Color.web(player.getColorHex()),
        player.getPlayerTokenType());
    playerToken.getStyleClass().add("game-players-box-player-token");

    Text playerName = new Text(player.getName());
    playerName.getStyleClass().add("game-players-box-player-name");

    Region spacer = new Region();
    spacer.getStyleClass().add("game-players-box-player-spacer");
    HBox.setHgrow(spacer, Priority.ALWAYS);

    playerTile = new Label(String.valueOf(player.getCurrentTile().getTileId()));
    playerTile.getStyleClass().add("game-players-box-player-tile");
    playerTile.setTooltip(new Tooltip(player.getName() + " is on tile: "
        + player.getCurrentTile().getTileId()));

    this.getChildren().addAll(playerToken, playerName, spacer, playerTile);
  }

  public void setTileNumber(Player player, int tileNumber) {
    playerTile.setText(String.valueOf(tileNumber));
    playerTile.setTooltip(new Tooltip(player.getName() + " is on tile: " + tileNumber));
  }
}
