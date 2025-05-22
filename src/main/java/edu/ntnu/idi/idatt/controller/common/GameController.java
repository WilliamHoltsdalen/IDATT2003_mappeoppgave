package edu.ntnu.idi.idatt.controller.common;

import edu.ntnu.idi.idatt.model.board.Board;
import edu.ntnu.idi.idatt.model.game.BoardGame;
import edu.ntnu.idi.idatt.model.player.Player;
import edu.ntnu.idi.idatt.observer.BoardGameObserver;
import edu.ntnu.idi.idatt.observer.ButtonClickObserver;
import edu.ntnu.idi.idatt.view.common.GameView;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * GameController.
 *
 * <p>Abstract controller for managing game logic and interactions between the model
 * ({@link BoardGame})
 * and the view ({@link GameView}).</p>
 *
 * <p>It handles game initialization, player turns, and game state updates. It also acts as an
 * observer for both button clicks from the view and game events from the model.</p>
 *
 * @see GameView
 * @see BoardGame
 * @see ButtonClickObserver
 * @see BoardGameObserver
 */
public abstract class GameController implements ButtonClickObserver, BoardGameObserver  {
  protected final GameView gameView;
  protected final Logger logger = LoggerFactory.getLogger(GameController.class);
  protected BoardGame boardGame;
  /**
   * Runnable action to execute when the game is quit.
   */
  protected Runnable onQuitGame;

  /**
   * Consumer action to execute when the game is finished.
   */
  protected Consumer<Map<String, Object>> onNavigateToGameFinished;

  /**
   * Constructs a GameController.
   *
   * @param gameView The {@link GameView} associated with this controller.
   * @param board The {@link Board} for the game.
   * @param players A list of {@link Player}s participating in the game.
   */
  public GameController(GameView gameView, Board board, List<Player> players) {
    this.gameView = gameView;
    logger.debug("GameController initialized");
    initializeBoardGame(board, players);
    initializeGameView();
  }

  /**
   * Initializes the specific {@link BoardGame} instance for the game.
   * This method must be implemented by subclasses to create and configure
   * the appropriate game model.
   *
   * @param board The {@link Board} for the game.
   * @param players A list of {@link Player}s participating in the game.
   */
  public abstract void initializeBoardGame(Board board, List<Player> players);

  /**
   * Executes a full turn for all players in the game sequentially.
   * Subclasses must implement the logic for how multiple player turns are handled,
   * for example, in a "roll for all" scenario.
   */
  protected abstract void performPlayerTurnForAllPlayers();

  /**
   * Executes a single turn for the current player.
   * Subclasses must implement the specific actions that constitute a player's turn
   * (e.g., rolling dice, moving pieces, checking for game events).
   */
  protected abstract void performPlayerTurn();

  /**
   * Restarts the current game.
   * Subclasses must implement the logic to reset the game state to its initial configuration,
   * allowing players to start a new game with the same settings.
   */
  protected abstract void restartGame();

  /**
   * Initializes the {@link GameView} with the initial game state.
   * This typically involves setting up the display of players, round number, and the game board.
   */
  public void initializeGameView() {
    gameView.initialize(getPlayers(), getRoundNumber(), (Board) getBoard());
    logger.debug("Initialize game view");
  }

  /**
   * Gets the current round number of the game.
   *
   * @return The current round number.
   */
  protected int getRoundNumber() {
    return boardGame.getRoundNumber();
  }

  /**
   * Gets the list of players in the game.
   *
   * @return A list of {@link Player}s.
   */
  protected List<Player> getPlayers() {
    return boardGame.getPlayers();
  }

  /**
   * Gets the game board.
   *
   * @return The {@link Board} object.
   */
  protected Board getBoard() {
    return boardGame.getBoard();
  }

  /**
   * Sets the {@link Runnable} action to be executed when the game is quit.
   *
   * @param onQuitGame The action to perform on quitting the game.
   */
  public void setOnQuitGame(Runnable onQuitGame) {
    logger.debug("Setting quit game callback");
    this.onQuitGame = onQuitGame;
  }

  /**
   * Executes the quit game action, if one has been set.
   */
  protected void quitGame() {
    if (onQuitGame != null) {
      logger.info("Quitting the game");
      onQuitGame.run();
    }
  }

  /**
   * Sets the {@link Consumer} action to be executed when the game is finished.
   *
   * @param onNavigateToGameFinished The action to perform on navigating to the game finished view.
   */
  public void setOnNavigateToGameFinished(Consumer<Map<String, Object>> onNavigateToGameFinished) {
    logger.debug("Setting game finished callback");
    this.onNavigateToGameFinished = onNavigateToGameFinished;
  }

  /**
   * Navigates to the game finished view.
   *
   * @param params A map of parameters to pass to the game finished view.
   */
  protected void navigateToGameFinished(Map<String, Object> params) {
    logger.debug("Navigating to game finished view");
    if (onNavigateToGameFinished != null) {
      onNavigateToGameFinished.accept(params);
    }
  }

  /**
   * Handles the action triggered by the roll dice button.
   * Disables the button, then either performs a turn for all players or a single player
   * based on the view's selection.
   */
  protected void handleRollDiceButtonAction() {
    logger.debug("Roll dice button clicked");
    gameView.getGameMenuBox().disableRollDiceButton();
    if (gameView.getGameMenuBox().getRollForAllPlayersSelected()) {
      logger.debug("Performing turn for all players");
      performPlayerTurnForAllPlayers();
      return;
    }
    logger.debug("Performing turn for current player");
    performPlayerTurn();
  }

  /**
   * Enables the roll dice button in the game view.
   */
  protected void enableRollDiceButton() {
    logger.debug("Enabling roll dice button");
    gameView.getGameMenuBox().enableRollDiceButton();
  }

  /**
   * Disables the roll dice button in the game view.
   */
  protected void disableRollDiceButton() {
    logger.debug("Disabling roll dice button");
    gameView.getGameMenuBox().disableRollDiceButton();
  }

  /**
   * Handles button click events without parameters from the {@link GameView}.
   * Subclasses must implement this to define actions for specific button IDs.
   *
   * @param buttonId The ID of the button that was clicked.
   */
  @Override
  public abstract void onButtonClicked(String buttonId);

  /**
   * Handles button click events with parameters from the {@link GameView}.
   * Subclasses must implement this to define actions for specific button IDs that require
   * parameters.
   *
   * @param buttonId The ID of the button that was clicked.
   * @param params A map of parameters associated with the button click.
   */
  @Override
  public abstract void onButtonClickedWithParams(String buttonId, Map<String, Object> params);

  /**
   * Called when the round number in the {@link BoardGame} model is incremented.
   * Subclasses must implement this to update the view or perform other actions
   * in response to a new round starting.
   *
   * @param roundNumber The new round number.
   */
  @Override
  public abstract void onRoundNumberIncremented(int roundNumber);

  /**
   * Called when the current player in the {@link BoardGame} model changes.
   * Subclasses must implement this to update the view or perform other actions
   * related to the change of turn.
   *
   * @param player The new current {@link Player}.
   */
  @Override
  public abstract void onCurrentPlayerChanged(Player player);

  /**
   * Called when the {@link BoardGame} model signals that the game has finished.
   * Subclasses must implement this to handle the end of the game, such as displaying
   * a winner message or offering options to restart or quit.
   *
   * @param winner The {@link Player} who won the game.
   */
  @Override
  public abstract void onGameFinished(Player winner);
}
