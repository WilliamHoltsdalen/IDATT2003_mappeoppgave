package edu.ntnu.idi.idatt.controller.laddergame;

import static java.lang.Thread.sleep;

import edu.ntnu.idi.idatt.controller.common.GameController;
import edu.ntnu.idi.idatt.model.board.Board;
import edu.ntnu.idi.idatt.model.board.LadderGameBoard;
import edu.ntnu.idi.idatt.model.game.LadderBoardGame;
import edu.ntnu.idi.idatt.model.player.LadderGamePlayer;
import edu.ntnu.idi.idatt.model.player.Player;
import edu.ntnu.idi.idatt.model.tile.TileAction;
import edu.ntnu.idi.idatt.view.laddergame.LadderGameStackPane;
import edu.ntnu.idi.idatt.view.laddergame.LadderGameView;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * LadderGameController.
 *
 * <p>This class extends {@link GameController} to manage the specific logic and interactions
 * for a {@link LadderBoardGame}. It acts as a controller in the MVC pattern, mediating between the
 * {@link LadderGameView} (the view) and the {@link LadderBoardGame} (the model).</p>
 *
 * <p>Responsibilities include:
 * <ul>
 *   <li>Initializing the {@link LadderBoardGame} with a given {@link Board} and list of
 *       {@link Player}s.</li>
 *   <li>Handling player turns, including dice rolling (with animation) and executing moves
 *       on the game board.</li>
 *   <li>Updating the {@link LadderGameView} in response to game state changes, such as player
 *       movement, tile actions, round progression, current player changes, and game
 *       completion.</li>
 *   <li>Processing user interactions from the view, like clicking the "roll dice", "restart game",
 *       or "quit game" buttons.</li>
 *   <li>Managing the display of player information (e.g., current tile) and game log entries.</li>
 * </ul>
 *
 *
 * @see GameController
 * @see LadderBoardGame
 * @see LadderGameView
 * @see LadderGamePlayer
 * @see LadderGameStackPane
 * @see Board
 * @see Player
 * @see TileAction
 */
public class LadderGameController extends GameController {

  /**
   * Constructs a new {@code LadderGameController}.
   *
   * @param ladderGameView The {@link LadderGameView} associated with this controller.
   * @param board          The {@link Board} (specifically a {@link LadderGameBoard}) for the game.
   * @param players        The list of {@link Player}s participating in the game.
   */
  public LadderGameController(LadderGameView ladderGameView, Board board, List<Player> players) {
    super(ladderGameView, board, players);
  }

  /**
   * Initializes the {@link #boardGame} instance as a {@link LadderBoardGame}. It creates a new game
   * with the provided board and players, and sets the number of dice to 2. This controller is also
   * added as an observer to the newly created game model. All players are initially placed on the
   * starting tile (tile 0).
   *
   * @param board   The {@link Board} for the game.
   * @param players The list of {@link Player}s.
   */
  @Override
  public void initializeBoardGame(Board board, List<Player> players) {
    try {
      boardGame = new LadderBoardGame(board, players, 2);
      boardGame.addObserver(this);
      logger.info("Game initialized with {} players on board '{}'", players.size(),
          board.getName());
    } catch (IllegalArgumentException e) {
      logger.error("Error initializing game with board '{}'", board.getName());
      e.printStackTrace();
    }
  }

  /**
   * Executes a single turn for the current player in the {@link LadderBoardGame}. This involves:
   * <ol>
   *   <li>Disabling the roll dice button in the view.</li>
   *   <li>Simulating a dice roll in the {@code LadderBoardGame}.</li>
   *   <li>Retrieving the individual dice values.</li>
   *   <li>Triggering an animation for the dice roll in the {@code LadderGameView}.</li>
   *   <li>Upon completion of the animation, performing the player's turn in the game model
   *       using the total dice roll.</li>
   *   <li>Re-enabling the roll dice button.</li>
   * </ol>
   */
  @Override
  protected void performPlayerTurn() {
    disableRollDiceButton();
    int diceRoll = ((LadderBoardGame) boardGame).rollDice();
    int[] diceValues = (boardGame).getDice().getDiceList().stream()
        .mapToInt(die -> die.getValue())
        .toArray();

    Player currentPlayer = boardGame.getCurrentPlayer();
    logger.debug("{} rolled: {} -> sum: {}", currentPlayer.getName(), diceRoll, diceValues);

    gameView.getGameMenuBox().animateDiceRoll(diceValues, () -> {
      ((LadderBoardGame) boardGame).performPlayerTurn(diceRoll);
      enableRollDiceButton();
    });
  }

  /**
   * Performs turns for all players sequentially until it is the first player's turn again. This is
   * typically used when the "roll for all players" option is active.
   */
  @Override
  protected void performPlayerTurnForAllPlayers() {
    logger.debug("Performing all players turn");
    do {
      performPlayerTurn();
    } while (!boardGame.getCurrentPlayer().equals(boardGame.getPlayers().getFirst()));
  }

  /**
   * Updates the displayed tile number for a specific player in the player information panel of the
   * {@link LadderGameView}.
   *
   * @param player    The {@link Player} whose tile number is to be updated.
   * @param newTileId The ID of the new tile the player is on.
   */
  private void setPlayerTileNumber(Player player, int newTileId) {
    gameView.getPlayersBox().getPlayerRows().get(getPlayers().indexOf(player))
        .setTileNumber(player, String.valueOf(newTileId));
  }

  /**
   * Restarts the current game. This involves:
   * <ol>
   *   <li>Creating a new list of {@link LadderGamePlayer}s based on the existing players
   *       (preserving their names, colors, token types, and bot status).</li>
   *   <li>Re-initializing the {@link #boardGame} with the same board but the new list of
   *       player instances.</li>
   *   <li>Re-initializing the {@link LadderGameView} to reflect the new game state.</li>
   * </ol>
   */
  @Override
  protected void restartGame() {
    logger.info("Restarting game");
    List<Player> players = new ArrayList<>();
    boardGame.getPlayers().forEach(player -> players.add(new LadderGamePlayer(player.getName(),
        player.getColorHex(), player.getPlayerTokenType(), player.isBot())));

    initializeBoardGame(boardGame.getBoard(), players);
    initializeGameView();
  }

  /**
   * Handles the {@code playerMoved} event from the {@link LadderBoardGame} model. Updates the game
   * log in the view with the player's move, updates the player's displayed tile number, and
   * triggers the visual movement of the player's token on the {@link LadderGameStackPane}.
   *
   * @param player    The {@link Player} who moved.
   * @param diceRoll  The result of the dice roll that caused the move.
   * @param newTileId The ID of the tile the player moved to.
   */
  public void onPlayerMoved(Player player, int diceRoll, int newTileId) {
    gameView.getGameMenuBox().addGameLogRoundBoxEntry(
        player.getName() + " rolled " + diceRoll + " and moved to tile " + newTileId);

    logger.debug("{} rolled: {} and moved to tile: {}", player.getName(), diceRoll, newTileId);

    setPlayerTileNumber(player, newTileId);

    Runnable onFinished = gameFinishedParams.isEmpty() ? null : () -> navigateToGameFinished(gameFinishedParams);
    ((LadderGameStackPane) gameView.getGameStackPane()).movePlayer(player,
        getBoard().getTile(newTileId), false, onFinished);
  }

  /**
   * Handles the {@code roundNumberIncremented} event from the {@link LadderBoardGame} model.
   * Updates the displayed round number in the view's player information panel and adds a new round
   * entry to the game log.
   *
   * @param roundNumber The new round number.
   */
  @Override
  public void onRoundNumberIncremented(int roundNumber) {
    gameView.getPlayersBox().setRoundNumber(roundNumber);
    logger.debug("Round number incremented: {}", roundNumber);
    gameView.getGameMenuBox().addGameLogRoundBox(roundNumber);
  }

  /**
   * Handles the {@code currentPlayerChanged} event from the {@link LadderBoardGame} model. If "roll
   * for all players" is not selected, it sets the focus on the current player in the view's player
   * information panel. If the new current player is a bot and "roll for all" is not active, it
   * automatically performs the bot's turn. If "roll for all players" is selected, focus is removed
   * from any specific player.
   *
   * @param player The new current {@link Player}.
   */
  @Override
  public void onCurrentPlayerChanged(Player player) {
    logger.debug("Current player changed: {}", player.getName());
    if (!gameView.getGameMenuBox().getRollForAllPlayersSelected()) {
      gameView.getPlayersBox().setFocusedPlayer(getPlayers().indexOf(player));

      // If it's a bot's turn and "roll for all" is not selected, automatically perform the turn.
      if (player.isBot() && !gameView.getGameMenuBox().getRollForAllPlayersSelected()) {
        performPlayerTurn();
        logger.debug("Automatic turn for Bot-Player: {}", player.getName());
      }
      return;
    }
    gameView.getPlayersBox().removeFocusedPlayer();
  }

  /**
   * Handles the {@code tileActionPerformed} event from the {@link LadderBoardGame} model. This
   * occurs when a player lands on a tile with a special action (e.g., ladder, slide). Updates the
   * game log with the action, updates the player's displayed tile number to the destination of the
   * action, and triggers the visual movement of the player's token on the
   * {@link LadderGameStackPane} to the action's destination tile.
   *
   * @param player     The {@link Player} who activated the tile action.
   * @param tileAction The {@link TileAction} that was performed.
   */
  public void onTileActionPerformed(Player player, TileAction tileAction) {
    gameView.getGameMenuBox()
        .addGameLogRoundBoxEntry(player.getName() + " activated " + tileAction.getDescription());
    logger.info("{} activated tile action: {}", player.getName(), tileAction.getDescription());
    setPlayerTileNumber(player, tileAction.getDestinationTileId());

    ((LadderGameStackPane) gameView.getGameStackPane()).movePlayer(player,
        getBoard().getTile(tileAction.getDestinationTileId()), true, null);
  }

  /**
   * Handles the {@code gameFinished} event from the {@link LadderBoardGame} model.
   * Navigates to the GameFinishedView, passing the winner and other ranked players.
   *
   * @param winner The {@link Player} who won the game.
   */
  @Override
  public void onGameFinished(Player winner) {
    logger.info("Game finished. Game winner is: {}", winner.getName());
    gameView.getGameMenuBox().addGameLogRoundBoxEntry("Game finished! Winner: " + winner.getName());
    disableRollDiceButton();

    List<Player> rankedPlayers = getPlayers().stream()
        .sorted(Comparator.comparingInt((Player p) -> ((LadderGamePlayer) p).getCurrentTile()
            .getTileId()).reversed()).toList();

    super.gameFinishedParams.put("rankedPlayers", rankedPlayers);
  }

  /**
   * Handles button click events from the {@link LadderGameView}. Delegates actions based on the
   * button ID:
   * <ul>
   *   <li>"roll_dice": Calls {@link #handleRollDiceButtonAction()}.</li>
   *   <li>"restart_game": Calls {@link #restartGame()}.</li>
   *   <li>"quit_game": Calls {@link #quitGame()}.</li>
   * </ul>
   *
   * @param buttonId The ID of the button that was clicked.
   */
  @Override
  public void onButtonClicked(String buttonId) {
    logger.debug("Button clicked: {}", buttonId);
    switch (buttonId) {
      case "roll_dice" -> handleRollDiceButtonAction();
      case "restart_game" -> restartGame();
      case "quit_game" -> quitGame();
      default -> {
        break;
      }
    }
  }

  /**
   * Handles button click events that may include parameters. This method is not currently used in
   * {@code LadderGameController}.
   *
   * @param buttonId The ID of the clicked button.
   * @param params   A map of parameters associated with the button click.
   */
  @Override
  public void onButtonClickedWithParams(String buttonId, Map<String, Object> params) {
    // Not needed
  }
}
