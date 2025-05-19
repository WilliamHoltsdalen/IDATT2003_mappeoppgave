package edu.ntnu.idi.idatt.view.laddergame;

import edu.ntnu.idi.idatt.model.player.Player;
import edu.ntnu.idi.idatt.view.common.GamePlayerRow;
import edu.ntnu.idi.idatt.view.common.GamePlayersBox;
import edu.ntnu.idi.idatt.view.component.HorizontalDivider;
import java.util.List;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * <h3>LadderGamePlayersBox.</h3>
 *
 * <p>This class extends {@link GamePlayersBox} to display a list of {@link Player}s
 * specifically for a ladder game. It arranges {@link LadderGamePlayerRow} instances
 * vertically, along with the current round number and a visual separator.</p>
 *
 * @see GamePlayersBox
 * @see LadderGamePlayerRow
 * @see Player
 */
public class LadderGamePlayersBox extends GamePlayersBox {
  
  /**
   * Constructs a {@code LadderGamePlayersBox}.
   *
   * @param players            The list of {@link Player}s to display.
   * @param initialRoundNumber The initial round number to display.
   */
  public LadderGamePlayersBox(List<Player> players, int initialRoundNumber) {
    super(players, initialRoundNumber);
  }
  
  /**
  * Initializes the players box by creating all the components and adding the players to the box.
  * It sets up the round number display, a horizontal divider, and then iterates through the
  * provided list of players, creating a {@link LadderGamePlayerRow} for each and adding it to
  * the vertical layout.
  *
  * @param players The list of {@link Player}s to add to the box.
  */
  @Override
  protected void initialize(List<Player> players) {
    roundNumberText.getStyleClass().add("players-box-round-number");
    
    HorizontalDivider horizontalDivider = new HorizontalDivider();
    
    VBox playersBoxVbox = new VBox(roundNumberText, horizontalDivider);
    playersBoxVbox.getStyleClass().add("players-box");
    playersBoxVbox.maxHeightProperty().bind(playersBoxVbox.heightProperty());
    
    players.forEach(player -> {
      GamePlayerRow playerRow = new LadderGamePlayerRow(player);
      HBox.setHgrow(playerRow, Priority.ALWAYS);
      playersBoxRows.add(playerRow);
      playersBoxVbox.getChildren().add(playerRow);
    });
    
    VBox vbox = new VBox(playersBoxVbox);
    VBox.setVgrow(playersBoxVbox, Priority.ALWAYS);
    HBox.setHgrow(vbox, Priority.NEVER);
    vbox.setAlignment(Pos.TOP_LEFT);
    
    this.getChildren().add(vbox);
  }
}
