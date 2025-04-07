package edu.ntnu.idi.idatt.view.component;

import edu.ntnu.idi.idatt.model.Player;
import java.util.List;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

/**
 * <h3>GamePlayersBox class</h3>
 *
 * <p>This class extends the VBox class. It is used to display the players in the game view.
 * It contains a list of player rows, as well as a round number text.
 */
public class GamePlayersBox extends VBox {
  private static final String ROUND_NUMBER_TEXT = "Round ";

  private final List<GamePlayerRow> playersBoxRows;
  private final Text roundNumberText;

  /**
   * Constructor for GamePlayersBox class.
   *
   * @param players the plays to add to the box
   * @param initialRoundNumber the initial round number to set when the box is created
   */
  public GamePlayersBox(List<Player> players, int initialRoundNumber) {
    playersBoxRows = new java.util.ArrayList<>();
    roundNumberText = new Text();

    HBox.setHgrow(this, Priority.NEVER);
    this.setAlignment(Pos.TOP_LEFT);

    initialize(players);
    setRoundNumber(initialRoundNumber);
  }

  /**
   * Initializes the players box by creating all the components and adding the players to the box.
   *
   * @param players the list of players to add to the box
   */
  private void initialize(List<Player> players) {
    roundNumberText.getStyleClass().add("game-players-box-round-number");

    HorizontalDivider horizontalDivider = new HorizontalDivider();

    VBox playersBoxVbox = new VBox(roundNumberText, horizontalDivider);
    playersBoxVbox.getStyleClass().add("game-players-box");
    playersBoxVbox.maxHeightProperty().bind(playersBoxVbox.heightProperty());

    players.forEach(player -> {
      GamePlayerRow playerRow = new GamePlayerRow(player);
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

  /**
   * Sets the round number.
   *
   * @param roundNumber the round number to set
   */
  public void setRoundNumber(int roundNumber) {
    roundNumberText.setText(ROUND_NUMBER_TEXT + roundNumber);
  }

  /**
   * Returns the list of player rows.
   *
   * @return the list of player rows
   */
  public List<GamePlayerRow> getPlayerRows() {
    return playersBoxRows;
  }

  public void setFocusedPlayer(int playerIndex) {
    removeFocusedPlayer();
    playersBoxRows.get(playerIndex).getStyleClass().add("game-players-box-player-row-current-player");
  }

  public void removeFocusedPlayer() {
    for (GamePlayerRow playerRow : playersBoxRows) {
      playerRow.getStyleClass().remove("game-players-box-player-row-current-player");
    }
  }
}
