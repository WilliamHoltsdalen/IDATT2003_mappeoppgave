package edu.ntnu.idi.idatt.view.common;

import edu.ntnu.idi.idatt.model.player.Player;
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
public abstract class GamePlayersBox extends VBox {
  protected static final String ROUND_NUMBER_TEXT = "Round ";

  protected final List<GamePlayerRow> playersBoxRows;
  protected final Text roundNumberText;

  /**
   * Constructor for GamePlayersBox class.
   *
   * @param players the plays to add to the box
   * @param initialRoundNumber the initial round number to set when the box is created
   */
  protected GamePlayersBox(List<Player> players, int initialRoundNumber) {
    playersBoxRows = new java.util.ArrayList<>();
    roundNumberText = new Text();

    HBox.setHgrow(this, Priority.NEVER);
    this.setAlignment(Pos.TOP_LEFT);

    this.getStylesheets().add("stylesheets/gameViewStyles.css");

    initialize(players);
    setRoundNumber(initialRoundNumber);
  }

  protected abstract void initialize(List<Player> players);

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
    playersBoxRows.get(playerIndex).getStyleClass()
        .add("players-box-current-player-row");
  }

  public void removeFocusedPlayer() {
    for (GamePlayerRow playerRow : playersBoxRows) {
      playerRow.getStyleClass().remove("players-box-current-player-row");
    }
  }
}
