package edu.ntnu.idi.idatt.view.common;

import edu.ntnu.idi.idatt.model.player.Player;
import java.util.List;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

/**
 * <h3>GamePlayersBox.</h3>
 *
 * <p>Abstract JavaFX {@link VBox} that displays a list of players in an active game,
 * typically showing their names, tokens, and current status (e.g., tile number).
 * It also displays the current round number.</p>
 *
 * <p>Subclasses are responsible for creating specific {@link GamePlayerRow} instances
 * and adding them to this container.</p>
 *
 * @see VBox
 * @see GamePlayerRow
 * @see Player
 */
public abstract class GamePlayersBox extends VBox {
  protected static final String ROUND_NUMBER_TEXT = "Round ";

  protected final List<GamePlayerRow> playersBoxRows;
  protected final Text roundNumberText;

  /**
   * Constructs a GamePlayersBox.
   *
   * @param players A list of {@link Player}s to display initially.
   * @param initialRoundNumber The starting round number to display.
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

  /**
   * Initializes the player box by creating and adding {@link GamePlayerRow}s for each player
   * and setting up the round number display.
   * Subclasses must implement this to populate {@link #playersBoxRows} and add them to the VBox children.
   *
   * @param players A list of {@link Player}s to display.
   */
  protected abstract void initialize(List<Player> players);

  /**
   * Sets the displayed round number.
   *
   * @param roundNumber The round number to display.
   */
  public void setRoundNumber(int roundNumber) {
    roundNumberText.setText(ROUND_NUMBER_TEXT + roundNumber);
  }

  /**
   * Gets the list of {@link GamePlayerRow}s currently in this box.
   *
   * @return A list of player rows.
   */
  public List<GamePlayerRow> getPlayerRows() {
    return playersBoxRows;
  }

  /**
   * Applies a visual focus style to the player row at the specified index
   * and removes it from any previously focused row.
   *
   * @param playerIndex The 0-based index of the player row to focus.
   */
  public void setFocusedPlayer(int playerIndex) {
    removeFocusedPlayer();
    playersBoxRows.get(playerIndex).getStyleClass()
        .add("players-box-current-player-row");
  }

  /**
   * Removes the visual focus style from all player rows in this box.
   */
  public void removeFocusedPlayer() {
    for (GamePlayerRow playerRow : playersBoxRows) {
      playerRow.getStyleClass().remove("players-box-current-player-row");
    }
  }
}
