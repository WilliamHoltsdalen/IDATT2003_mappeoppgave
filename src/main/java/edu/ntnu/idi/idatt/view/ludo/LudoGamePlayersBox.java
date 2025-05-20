package edu.ntnu.idi.idatt.view.ludo;

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
 * LudoGamePlayersBox.
 *
 * <p>Extends {@link GamePlayersBox} to create a specialized container for displaying player
 * information in a Ludo game. It arranges multiple {@link LudoGamePlayerRow} instances vertically,
 * showing each player's status, and also displays the current round number of the game.</p>
 *
 * @see GamePlayersBox
 * @see LudoGamePlayerRow
 * @see Player
 */
public class LudoGamePlayersBox extends GamePlayersBox {

  /**
   * Constructs a {@code LudoGamePlayersBox} with the given list of players and initial
   * round number.
   *
   * @param players            The list of {@link Player}s participating in the game.
   * @param initialRoundNumber The starting round number for the game.
   */
  public LudoGamePlayersBox(List<Player> players, int initialRoundNumber) {
    super(players, initialRoundNumber);
  }

  /**
   * Initializes the players box by creating all the components and adding the players to the box.
   * For each player, a {@link LudoGamePlayerRow} is created and added. A round number display
   * and a horizontal divider are also included.
   *
   * @param players the list of players to add to the box
   */
  @Override
  protected void initialize(List<Player> players) {
    roundNumberText.getStyleClass().add("players-box-round-number");

    HorizontalDivider horizontalDivider = new HorizontalDivider();

    VBox playersBoxVbox = new VBox(roundNumberText, horizontalDivider);
    playersBoxVbox.getStyleClass().add("players-box");
    playersBoxVbox.maxHeightProperty().bind(playersBoxVbox.heightProperty());

    players.forEach(player -> {
      GamePlayerRow playerRow = new LudoGamePlayerRow(player);
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
