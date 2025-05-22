package edu.ntnu.idi.idatt.controller.ludo;

import static java.lang.Thread.sleep;

import edu.ntnu.idi.idatt.controller.common.GameController;
import edu.ntnu.idi.idatt.model.board.Board;
import edu.ntnu.idi.idatt.model.board.LudoGameBoard;
import edu.ntnu.idi.idatt.model.game.LudoBoardGame;
import edu.ntnu.idi.idatt.model.player.LudoPlayer;
import edu.ntnu.idi.idatt.model.player.Player;
import edu.ntnu.idi.idatt.model.token.LudoToken;
import edu.ntnu.idi.idatt.view.ludo.LudoGameStackPane;
import edu.ntnu.idi.idatt.view.ludo.LudoGameView;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * LudoGameController.
 *
 * <p>This class extends {@link GameController} to manage the specific logic and interactions
 * for a {@link LudoBoardGame}. It acts as a controller in the MVC pattern, mediating between the
 * {@link LudoGameView} (the view) and the {@link LudoBoardGame} (the model).</p>
 *
 * <p>Responsibilities include:
 * <ul>
 *   <li>Initializing the {@link LudoBoardGame} with a given {@link Board} and list of
 *       {@link LudoPlayer}s.</li>
 *   <li>Handling player turns, which involves triggering a dice roll in the model, animating the
 *       roll in the view, and then executing the turn in the model.</li>
 *   <li>Updating the {@link LudoGameView} in response to game state changes from the model. This
 *       includes:
 *     <ul>
 *       <li>Token releases: Moving a token from the start area to the track.</li>
 *       <li>Token movements: Animating token movement on the board.</li>
 *       <li>Token captures: Moving a captured token back to its start area.</li>
 *       <li>Token finishes: Indicating a token has reached its goal.</li>
 *       <li>Turn skips: Logging when a player cannot make a move.</li>
 *       <li>Updating player information (e.g., number of finished tokens).</li>
 *       <li>Round progression and current player changes.</li>
 *       <li>Game completion announcements.</li>
 *     </ul>
 *   </li>
 *   <li>Processing user interactions from the view, such as clicking "roll dice", "restart game",
 *       or "quit game" buttons.</li>
 *   <li>Managing automatic turns for bot players if "roll for all players" is not selected.</li>
 * </ul>
 *
 *
 * @see GameController
 * @see LudoBoardGame
 * @see LudoGameView
 * @see LudoPlayer
 * @see LudoToken
 * @see LudoGameStackPane
 */
public class LudoGameController extends GameController {

  /**
   * Constructs a new {@code LudoGameController}.
   *
   * @param ludoGameView The {@link LudoGameView} associated with this controller.
   * @param board        The {@link Board} (specifically a {@link LudoGameBoard}) for the game.
   * @param players      The list of {@link Player}s (expected to be {@link LudoPlayer} instances)
   *                     participating in the game.
   */
  public LudoGameController(LudoGameView ludoGameView, Board board, List<Player> players) {
    super(ludoGameView, board, players);
  }

  /**
   * Initializes the {@link #boardGame} instance as a {@link LudoBoardGame}. It creates a new game
   * with the provided board and players, and sets the number of dice to 1 (standard for Ludo). This
   * controller is also added as an observer to the newly created game model.
   *
   * @param board   The {@link Board} for the Ludo game.
   * @param players The list of {@link Player}s for the Ludo game.
   */
  @Override
  public void initializeBoardGame(Board board, List<Player> players) {
    try {
      boardGame = new LudoBoardGame(board, players, 1);
      boardGame.addObserver(this);
    } catch (IllegalArgumentException e) {
      e.printStackTrace();
    }
  }

  /**
   * Performs turns for all players sequentially until it is the first player's turn again. This is
   * typically used when the "roll for all players" option is active in the view.
   */
  @Override
  protected void performPlayerTurnForAllPlayers() {
    do {
      performPlayerTurn();
    } while (!boardGame.getCurrentPlayer().equals(boardGame.getPlayers().getFirst()));
  }

  /**
   * Executes a single turn for the current player in the {@link LudoBoardGame}. This involves:
   * <ol>
   *   <li>Disabling the roll dice button in the view.</li>
   *   <li>Simulating a dice roll in the {@code LudoBoardGame}.</li>
   *   <li>Triggering an animation for the dice roll in the {@code LudoGameView}.</li>
   *   <li>Upon completion of the animation, performing the player's turn in the game model
   *       using the dice roll.</li>
   *   <li>Re-enabling the roll dice button in the view.</li>
   * </ol>
   */
  @Override
  protected void performPlayerTurn() {
    disableRollDiceButton();
    int diceRoll = ((LudoBoardGame) boardGame).rollDice();
    gameView.getGameMenuBox().animateDiceRoll(new int[]{diceRoll}, () -> {
      ((LudoBoardGame) boardGame).performPlayerTurn(diceRoll);
      enableRollDiceButton();
    });
  }

  /**
   * Updates the displayed information for a specific player in the player information panel of the
   * {@link LudoGameView}. For Ludo, this typically means updating the count of tokens that have
   * reached the {@link LudoToken.TokenStatus#FINISHED} state.
   *
   * @param player The {@link Player} (cast to {@link LudoPlayer}) whose information is to be
   *               updated.
   */
  private void setPlayerTileNumber(Player player) {
    int finishedTokens = (int) ((LudoPlayer) player).getTokens().stream()
        .filter(t -> t.getStatus() == LudoToken.TokenStatus.FINISHED).count();
    gameView.getPlayersBox().getPlayerRows().get(getPlayers().indexOf(player))
        .setTileNumber(player, String.valueOf(finishedTokens));
  }

  /**
   * Handles the {@code tokenReleased} event from the {@link LudoBoardGame} model. Adds an entry to
   * the game log in the view, triggers the visual release of the token on the
   * {@link LudoGameStackPane}, and updates the player's displayed information.
   *
   * @param player  The {@link Player} whose token was released.
   * @param tileId  The ID of the tile onto which the token was released.
   * @param tokenId The ID (index) of the released token within the player's list of tokens.
   */
  public void onTokenReleased(Player player, int tileId, int tokenId) {
    gameView.getGameMenuBox().addGameLogRoundBoxEntry(
        player.getName() + " rolled 6 and released token " + (tokenId + 1));
    ((LudoGameStackPane) gameView.getGameStackPane()).releaseToken(((LudoPlayer) player), tokenId);
    setPlayerTileNumber(player);
  }

  /**
   * Restarts the current Ludo game. This involves:
   * <ol>
   *   <li>Creating a new list of {@link LudoPlayer}s based on the existing players
   *       (preserving their names, colors, token types, and bot status).</li>
   *   <li>Re-initializing the {@link #boardGame} with the same board but the new list of
   *       player instances.</li>
   *   <li>Re-initializing the {@link LudoGameView} to reflect the new game state.</li>
   * </ol>
   */
  @Override
  protected void restartGame() {
    List<Player> players = new ArrayList<>();
    boardGame.getPlayers().forEach(player -> players.add(
        new LudoPlayer(player.getName(), player.getColorHex(), player.getPlayerTokenType(),
            player.isBot())));

    initializeBoardGame(boardGame.getBoard(), players);
    initializeGameView();
  }

  /**
   * Handles the {@code tokenMoved} event from the {@link LudoBoardGame} model. Adds an entry to the
   * game log, triggers the visual movement of the token on the {@link LudoGameStackPane}, and
   * updates the player's displayed information.
   *
   * @param player    The {@link Player} whose token moved.
   * @param token     The {@link LudoToken} that moved.
   * @param diceRoll  The result of the dice roll that caused the move.
   * @param oldTileId The ID of the tile the token moved from.
   * @param newTileId The ID of the tile the token moved to.
   */
  public void onTokenMoved(Player player, LudoToken token, int diceRoll, int oldTileId,
      int newTileId) {
    gameView.getGameMenuBox().addGameLogRoundBoxEntry(
        player.getName() + " rolled " + diceRoll + " and moved token " + (token.getTokenId()));

    Runnable onFinished = gameFinishedParams.isEmpty() ? null : () -> navigateToGameFinished(gameFinishedParams);
    ((LudoGameStackPane) gameView.getGameStackPane()).moveToken(token,
        getBoard().getTile(oldTileId), getBoard().getTile(newTileId), false, onFinished);
    setPlayerTileNumber(player);
  }

  /**
   * Handles the {@code roundNumberIncremented} event from the {@link LudoBoardGame} model. Updates
   * the displayed round number in the view's player information panel and adds a new round entry to
   * the game log.
   *
   * @param roundNumber The new round number.
   */
  @Override
  public void onRoundNumberIncremented(int roundNumber) {
    gameView.getPlayersBox().setRoundNumber(roundNumber);

    gameView.getGameMenuBox().addGameLogRoundBox(roundNumber);
  }

  /**
   * Handles the {@code currentPlayerChanged} event from the {@link LudoBoardGame} model. If "roll
   * for all players" is not selected in the view, it sets the focus on the current player in the
   * view's player information panel. If the new current player is a bot and "roll for all" is not
   * active, it automatically performs the bot's turn. If "roll for all players" is selected, focus
   * is removed from any specific player.
   *
   * @param player The new current {@link Player}.
   */
  @Override
  public void onCurrentPlayerChanged(Player player) {
    if (!gameView.getGameMenuBox().getRollForAllPlayersSelected()) {
      gameView.getPlayersBox().setFocusedPlayer(getPlayers().indexOf(player));

      // If it's a bot's turn and "roll for all" is not selected, automatically perform the turn.
      if (player.isBot() && !gameView.getGameMenuBox().getRollForAllPlayersSelected()) {
        performPlayerTurn();
      }
      return;
    }
    gameView.getPlayersBox().removeFocusedPlayer();
  }

  /**
   * Handles the {@code gameFinished} event from the {@link LudoBoardGame} model. Adds an entry to
   * the game log in the view announcing the winner.
   *
   * @param winner The {@link Player} who won the Ludo game.
   */
  @Override
  public void onGameFinished(Player winner) {
    gameView.getGameMenuBox().addGameLogRoundBoxEntry("Game finished! Winner: " + winner.getName());
    disableRollDiceButton();

    List<Player> rankedPlayers = getPlayers().stream().sorted(Comparator.comparingInt(
        (Player p) -> (int) ((LudoPlayer) p).getTokens().stream()
        .filter(t -> (t).getStatus() == LudoToken.TokenStatus.FINISHED)
        .count()).reversed()).toList();

    super.gameFinishedParams.put("rankedPlayers", rankedPlayers);
  }

  /**
   * Handles the {@code tokenCaptured} event from the {@link LudoBoardGame} model. Adds an entry to
   * the game log, triggers the visual movement of the captured token back to its start area on the
   * {@link LudoGameStackPane}, and updates the affected player's displayed information.
   *
   * @param player The {@link Player} whose token was captured.
   * @param token  The {@link LudoToken} that was captured.
   */
  public void onTokenCaptured(Player player, LudoToken token, int oldTileId) {
    gameView.getGameMenuBox().addGameLogRoundBoxEntry(
        player.getName() + "'s token " + token.getTokenId()
            + " was captured and sent back to start!");

    ((LudoGameStackPane) gameView.getGameStackPane()).moveTokenToStartArea(player, token);
    setPlayerTileNumber(player);
  }

  /**
   * Handles the {@code tokenFinished} event from the {@link LudoBoardGame} model. Adds an entry to
   * the game log and updates the player's displayed information (count of finished tokens).
   *
   * @param player The {@link Player} whose token reached the finish.
   * @param token  The {@link LudoToken} that finished.
   */
  public void onTokenFinished(Player player, LudoToken token) {
    gameView.getGameMenuBox().addGameLogRoundBoxEntry(
        player.getName() + "'s token " + token.getTokenId() + " finished!");
    setPlayerTileNumber(player);
  }

  /**
   * Handles the {@code turnSkipped} event from the {@link LudoBoardGame} model. Adds an entry to
   * the game log indicating that the player rolled but had no valid moves, thus skipping their
   * turn.
   *
   * @param player   The {@link Player} whose turn was skipped.
   * @param diceRoll The dice roll that resulted in the skipped turn.
   */
  public void onTurnSkipped(Player player, int diceRoll) {
    gameView.getGameMenuBox().addGameLogRoundBoxEntry(
        player.getName() + " rolled " + diceRoll + " but has no tokens to move. Turn skipped!");
  }

  /**
   * Handles button click events from the {@link LudoGameView}. Delegates actions based on the
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
   * {@code LudoGameController}.
   *
   * @param buttonId The ID of the clicked button.
   * @param params   A map of parameters associated with the button click.
   */
  @Override
  public void onButtonClickedWithParams(String buttonId, Map<String, Object> params) {
    // Not needed
  }

}
