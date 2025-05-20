package edu.ntnu.idi.idatt.view.laddergame;

import edu.ntnu.idi.idatt.controller.laddergame.LadderGameController;
import edu.ntnu.idi.idatt.model.board.Board;
import edu.ntnu.idi.idatt.model.board.LadderGameBoard;
import edu.ntnu.idi.idatt.model.player.Player;
import edu.ntnu.idi.idatt.view.common.GamePlayersBox;
import edu.ntnu.idi.idatt.view.common.GameStackPane;
import edu.ntnu.idi.idatt.view.common.GameView;
import edu.ntnu.idi.idatt.view.component.GameMenuBox;
import java.util.List;

/**
 * LadderGameView.
 *
 * <p>This class extends {@link GameView} to provide the specific user interface structure
 * for a Ladder Game. It is responsible for creating and assembling the main UI components tailored
 * for Ladder Game, such as the player information box, the game board display, and the game menu
 * controls.</p>
 *
 * <p>It overrides factory methods from {@link GameView} to instantiate Ladder Game-specific
 * UI components:
 * <ul>
 *   <li>{@link #createPlayersBox(List, int)}: Creates a {@link LadderGamePlayersBox}.</li>
 *   <li>{@link #createGameStackPane(Board, List)}: Creates a {@link LadderGameStackPane}.</li>
 *   <li>{@link #createGameMenuBox()}: Creates a {@link GameMenuBox} configured for two dice
 *       (standard for the implemented Ladder Game) and sets up its event handlers to notify
 *       observers (typically a {@link LadderGameController})
 *       of actions like restart, quit, or roll dice.</li>
 * </ul>
 *
 *
 * @see GameView
 * @see LadderGamePlayersBox
 * @see LadderGameStackPane
 * @see GameMenuBox
 * @see LadderGameBoard
 */
public class LadderGameView extends GameView {

  /**
   * Constructs a new {@code LadderGameView}. Calls the superclass constructor.
   */
  public LadderGameView() {
    super();
  }

  /**
   * Creates and returns a {@link LadderGamePlayersBox} to display player information (like name,
   * token, and current tile) and the current round number for the Ladder Game.
   *
   * @param players     The list of {@link Player}s participating in the game.
   * @param roundNumber The initial round number.
   * @return A new {@link LadderGamePlayersBox} instance.
   */
  @Override
  public GamePlayersBox createPlayersBox(List<Player> players, int roundNumber) {
    return new LadderGamePlayersBox(players, roundNumber);
  }

  /**
   * Creates and returns a {@link LadderGameStackPane} to display the visual representation of the
   * {@link LadderGameBoard}, including the grid, components (ladders, slides), and player tokens.
   *
   * @param board   The {@link Board} (expected to be a {@link LadderGameBoard}) for the game.
   * @param players The list of {@link Player}s in the game, used for placing their tokens.
   * @return A new {@link LadderGameStackPane} instance.
   */
  @Override
  public GameStackPane createGameStackPane(Board board, List<Player> players) {
    return new LadderGameStackPane((LadderGameBoard) board, players);
  }

  /**
   * Creates and returns a {@link GameMenuBox} configured for a Ladder Game. This menu typically
   * includes a dice display/roll button (configured for 2 dice), a restart game button, and a quit
   * game button. Event handlers for these buttons are set up to notify observers (e.g., the game
   * controller) of user actions.
   *
   * @return A new, configured {@link GameMenuBox} instance.
   */
  @Override
  public GameMenuBox createGameMenuBox() {
    GameMenuBox box = new GameMenuBox(2); // Use 2 dice for Ladder game
    box.setOnRestartGame(() -> notifyObservers("restart_game"));
    box.setOnQuitGame(() -> notifyObservers("quit_game"));
    box.setOnRollDice(() -> notifyObservers("roll_dice"));
    return box;
  }
}
