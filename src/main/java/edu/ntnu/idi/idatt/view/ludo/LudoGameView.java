package edu.ntnu.idi.idatt.view.ludo;

import edu.ntnu.idi.idatt.model.board.Board;
import edu.ntnu.idi.idatt.model.board.LudoGameBoard;
import edu.ntnu.idi.idatt.model.player.Player;
import edu.ntnu.idi.idatt.view.common.GamePlayersBox;
import edu.ntnu.idi.idatt.view.common.GameStackPane;
import edu.ntnu.idi.idatt.view.common.GameView;
import java.util.List;

/**
 * <h3>LudoGameView.</h3>
 *
 * <p>Extends {@link GameView} to provide the main view for a Ludo game. This class is responsible
 * for creating and assembling the Ludo-specific UI components, namely the
 * {@link LudoGamePlayersBox} for displaying player information and the
 * {@link LudoGameStackPane} for rendering the game board and tokens.</p>
 *
 * @see GameView
 * @see LudoGamePlayersBox
 * @see LudoGameStackPane
 * @see LudoGameBoard
 * @see Player
 */
public class LudoGameView extends GameView {

  /**
   * Constructs a new {@code LudoGameView}.
   * Calls the superclass constructor to initialize common game view elements.
   */
  public LudoGameView() {
    super();
  }

  /**
   * Creates a {@link LudoGamePlayersBox} to display player information for the Ludo game.
   *
   * @param players     The list of {@link Player}s in the game.
   * @param roundNumber The initial round number of the game.
   * @return A new instance of {@link LudoGamePlayersBox}.
   */
  @Override
  public GamePlayersBox createPlayersBox(List<Player> players, int roundNumber) {
    return new LudoGamePlayersBox(players, roundNumber);
  }

  /**
   * Creates a {@link LudoGameStackPane} to render the Ludo game board and its interactive elements.
   *
   * @param board   The {@link Board} (expected to be a {@link LudoGameBoard}) for the game.
   * @param players The list of {@link Player}s in the game.
   * @return A new instance of {@link LudoGameStackPane}.
   */
  @Override
  public GameStackPane createGameStackPane(Board board, List<Player> players) {
    return new LudoGameStackPane((LudoGameBoard) board, players);
  }
}
