package edu.ntnu.idi.idatt.view.ludo;

import java.util.List;

import edu.ntnu.idi.idatt.model.player.Player;
import edu.ntnu.idi.idatt.view.common.GamePlayerRow;
import edu.ntnu.idi.idatt.view.common.GamePlayersBox;
import edu.ntnu.idi.idatt.view.component.HorizontalDivider;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class LudoGamePlayersBox extends GamePlayersBox {
  
  public LudoGamePlayersBox(List<Player> players, int initialRoundNumber) {
    super(players, initialRoundNumber);
  }
  
  /**
  * Initializes the players box by creating all the components and adding the players to the box.
  *
  * @param players the list of players to add to the box
  */
  @Override
  protected void initialize(List<Player> players) {
    roundNumberText.getStyleClass().add("game-players-box-round-number");
    
    HorizontalDivider horizontalDivider = new HorizontalDivider();
    
    VBox playersBoxVbox = new VBox(roundNumberText, horizontalDivider);
    playersBoxVbox.getStyleClass().add("game-players-box");
    playersBoxVbox.maxHeightProperty().bind(playersBoxVbox.heightProperty());
    
    players.forEach(player -> {
      GamePlayerRow playerRow = new LudoGamePlayerRow(player);
      HBox.setHgrow(playerRow, Priority.ALWAYS);
      playersBoxRows.add(playerRow);
      playersBoxVbox.getChildren().add(playerRow);
    });
    
    VBox vBox = new VBox(playersBoxVbox);
    VBox.setVgrow(playersBoxVbox, Priority.ALWAYS);
    HBox.setHgrow(vBox, Priority.NEVER);
    vBox.setAlignment(Pos.TOP_LEFT);
    
    this.getChildren().add(vBox);
  }
}
