package edu.ntnu.idi.idatt.view.gui.component;

import edu.ntnu.idi.idatt.model.Player;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

public class GamePlayerRow extends HBox {
  private Label playerTile;

  public GamePlayerRow(Player player) {
    this.getStyleClass().add("game-players-box-player-row");
    initialize(player);
  }

  private void initialize(Player player) {
    Circle playerCircle = new Circle(10, Color.TRANSPARENT);
    playerCircle.setStroke(Color.web(player.getColorHex()));
    playerCircle.setStrokeWidth(8);

    Text playerName = new Text(player.getName());
    playerName.getStyleClass().add("game-players-box-player-name");

    Region spacer = new Region();
    spacer.getStyleClass().add("game-players-box-player-spacer");
    HBox.setHgrow(spacer, Priority.ALWAYS);

    playerTile = new Label(String.valueOf(player.getCurrentTile().getTileId()));
    playerTile.getStyleClass().add("game-players-box-player-tile");
    playerTile.setTooltip(new Tooltip(player.getName() + " is on tile: "
        + player.getCurrentTile().getTileId()));

    this.getChildren().addAll(playerCircle, playerName, spacer, playerTile);
  }

  public HBox getView() {
    return this;
  }

  public Label getTileLabel() {
    return playerTile;
  }
}
