package edu.ntnu.idi.idatt.model.game;

import edu.ntnu.idi.idatt.controller.ludo.LudoGameController;
import edu.ntnu.idi.idatt.model.board.Board;
import edu.ntnu.idi.idatt.model.board.LudoGameBoard;
import edu.ntnu.idi.idatt.model.player.LudoPlayer;
import edu.ntnu.idi.idatt.model.player.Player;
import edu.ntnu.idi.idatt.model.tile.Tile;
import edu.ntnu.idi.idatt.model.token.LudoToken;
import java.util.List;

/**
 * LudoBoardGame.
 *
 * <p>This class extends {@link BoardGame} to implement the specific rules and logic for a Ludo
 * game. It manages the game state, player turns, token movements, and win conditions for Ludo.</p>
 *
 * <p>Key Ludo-specific functionalities include:
 * <ul>
 *   <li>Initializing player tokens to their starting positions.</li>
 *   <li>Determining the winner based on all tokens of a player reaching their finished state.</li>
 *   <li>Handling dice rolls (typically a single die for Ludo).</li>
 *   <li>Player turn logic: moving a token if possible, releasing a token from the start area on a
 *   roll of 6, or skipping the turn if no valid move can be made.</li>
 *   <li>Calculating the next tile for a token based on the dice roll, considering the Ludo track
 *   and finish paths.</li>
 *   <li>Releasing tokens from their start area to the main track.</li>
 *   <li>Moving tokens along the track and into their respective finish areas.</li>
 *   <li>Handling token captures: sending an opponent's token back to its start area if landed
 *   upon.</li>
 *   <li>Notifying observers (like {@link LudoGameController}) of game events such as token
 *   release, movement, capture, finish, and turn skips.</li>
 * </ul>
 *
 *
 * @see BoardGame
 * @see LudoGameBoard
 * @see LudoPlayer
 * @see LudoToken
 * @see LudoGameController
 */
public class LudoBoardGame extends BoardGame {

  /**
   * Constructs a new {@code LudoBoardGame}.
   *
   * @param board     The {@link Board} (expected to be a {@link LudoGameBoard}) for the game.
   * @param players   The list of {@link Player}s (expected to be {@link LudoPlayer} instances)
   *                  participating in the game.
   * @param diceCount The number of dice to use in the game (typically 1 for Ludo).
   */
  public LudoBoardGame(Board board, List<Player> players, int diceCount) {
    super(board, players, diceCount);
  }

  /**
   * Initializes the Ludo game. This involves placing all tokens of each {@link LudoPlayer} onto
   * their respective starting tiles on the {@link LudoGameBoard}. The first player in the list is
   * set as the current player.
   */
  @Override
  public void initializeGame() {
    logger.info("Game started!");

    players.forEach(player -> {
      ((LudoPlayer) player).getTokens().forEach(token -> {
        int startIndex = ((LudoGameBoard) board).getPlayerStartIndexes()[players.indexOf(player)];
        token.setCurrentTile(board.getTile(startIndex));
      });
    });
    setCurrentPlayer(players.getFirst());
  }

  /**
   * Determines if there is a winner in the Ludo game. A player wins if all of their
   * {@link LudoToken}s have reached the {@link LudoToken.TokenStatus#FINISHED} state.
   *
   * @return The winning {@link Player}, or {@code null} if no player has won yet.
   */
  @Override
  public Player getWinner() {
    for (Player player : players) {
      if (((LudoPlayer) player).getTokens().stream()
          .allMatch(token -> token.getStatus() == LudoToken.TokenStatus.FINISHED)) {
        return player;
      }
    }
    return null;
  }

  /**
   * Checks if the win condition for the Ludo game has been met. If a winner is found (via
   * {@link #getWinner()}), it notifies observers by calling {@link #notifyGameFinished(Player)}.
   */
  @Override
  protected void checkWinCondition() {
    if (getWinner() != null) {
      notifyGameFinished(getWinner());
    }
  }

  /**
   * Checks if the current {@link LudoPlayer} has any tokens that are currently released (i.e., on
   * the main track or finish path) and can therefore be moved.
   *
   * @return {@code true} if the current player has at least one released token, {@code false}
   *     otherwise.
   */
  private boolean checkCurrentPlayerCanMove() {
    return ((LudoPlayer) currentPlayer).getTokens().stream()
        .anyMatch(token -> token.getStatus() == LudoToken.TokenStatus.RELEASED);
  }

  /**
   * Checks if any of the current player's released tokens have reached their designated finish
   * tile. If a token reaches its finish tile, its status is set to
   * {@link LudoToken.TokenStatus#FINISHED} and observers are notified via
   * {@link #notifyTokenFinished(Player, LudoToken)}.
   */
  private void checkTokenFinished() {
    ((LudoPlayer) currentPlayer).getTokens().stream()
        .filter(token -> token.getStatus() == LudoToken.TokenStatus.RELEASED).forEach(token -> {
          if (token.getCurrentTile().getTileId()
              == ((LudoGameBoard) board).getPlayerFinishIndexes()[players.indexOf(currentPlayer)]) {
            token.setStatus(LudoToken.TokenStatus.FINISHED);
            notifyTokenFinished(currentPlayer, token);
          }
        });
  }

  /**
   * Rolls the game's dice (typically one die for Ludo) and returns the total value. Logs the dice
   * roll result.
   *
   * @return The total value rolled on the dice.
   */
  public int rollDice() {
    dice.rollDice();
    logger.info("Dice rolled: {}", dice.getTotalValue());
    return dice.getTotalValue();
  }

  /**
   * Performs a turn for the current player based on the given dice roll. The logic is as follows:
   * <ol>
   *   <li>If the player can move a token (i.e., has released tokens), {@link #moveToken(int)} is
   *       called.</li>
   *   <li>If the player cannot move but rolled a 6, {@link #releaseToken()} is called.</li>
   *   <li>Otherwise (cannot move and did not roll a 6), the turn is skipped, and observers are
   *       notified.</li>
   * </ol>
   * After the action, it checks for finished tokens, checks the win condition, updates the current
   * player, and handles round number incrementation.
   *
   * @param diceRoll The result of the dice roll for this turn.
   */
  public void performPlayerTurn(int diceRoll) {
    if (checkCurrentPlayerCanMove()) {
      moveToken(diceRoll);
      checkTokenFinished();
    } else if (diceRoll == 6) {
      releaseToken();
    } else {
      notifyTurnSkipped(currentPlayer, diceRoll);
    }
    checkWinCondition();
    updateCurrentPlayer();
    handleRoundNumber();
  }

  /**
   * Finds the destination {@link Tile} for a given {@link LudoToken} after moving by
   * {@code diceRoll} steps. This method traces the path from the token's current tile, moving
   * {@code diceRoll} times to the {@code nextTileId} of each tile. It correctly handles transitions
   * from the main track to the player's specific finish track. If the token reaches its final
   * finish tile during the move, the tracing stops there.
   *
   * @param token    The {@link LudoToken} to be moved.
   * @param diceRoll The number of steps to move the token.
   * @return The destination {@link Tile} after the move.
   */
  private Tile findNextTile(LudoToken token, int diceRoll) {
    int nextTileId = token.getCurrentTile().getTileId();
    for (int i = 0; i < diceRoll; i++) {
      nextTileId = board.getTile(nextTileId).getNextTileId();
      if (board.getTile(nextTileId).getNextTileId()
          == ((LudoGameBoard) board).getPlayerTrackStartIndexes()[players.indexOf(currentPlayer)]) {
        nextTileId = ((LudoGameBoard) board).getPlayerFinishStartIndexes()[players.indexOf(
            currentPlayer)];
      }
      if (nextTileId == ((LudoGameBoard) board).getPlayerFinishIndexes()[players.indexOf(
          currentPlayer)]) {
        break; // If the player has reached the finish line, break the loop
      }
    }
    return board.getTile(nextTileId);
  }

  /**
   * Releases a {@link LudoToken} of the current player from their start area onto the main track.
   * This action is typically performed when a player rolls a 6 and has tokens in their start area.
   * It finds the first available token with status {@link LudoToken.TokenStatus#NOT_RELEASED},
   * moves it to the player's track start tile, updates its status to
   * {@link LudoToken.TokenStatus#RELEASED}, and notifies observers. It then checks for and handles
   * any token captures at the destination tile.
   */
  private void releaseToken() {
    int tileId = ((LudoGameBoard) board).getPlayerTrackStartIndexes()[players.indexOf(
        currentPlayer)];
    LudoToken token = ((LudoPlayer) currentPlayer).getTokens().stream()
        .filter(t -> t.getStatus() == LudoToken.TokenStatus.NOT_RELEASED).findFirst().orElse(null);
    if (token == null) {
      return;
    }
    token.setCurrentTile(board.getTile(tileId));
    token.setStatus(LudoToken.TokenStatus.RELEASED);
    notifyTokenReleased(currentPlayer, tileId,
        ((LudoPlayer) currentPlayer).getTokens().indexOf(token));
    // Check for and handle token captures
    handleTokenCapture(board.getTile(tileId));
  }

  /**
   * Moves a {@link LudoToken} of the current player by the given dice roll. It selects the first
   * available token with status {@link LudoToken.TokenStatus#RELEASED}, calculates its new tile
   * using {@link #findNextTile(LudoToken, int)}, updates the token's current tile, and notifies
   * observers of the move. It then checks for and handles any token captures at the new tile.
   *
   * @param diceRoll The number of steps to move the token, determined by the dice roll.
   */
  public void moveToken(int diceRoll) {
    LudoToken token = ((LudoPlayer) currentPlayer).getTokens().stream()
        .filter(t -> t.getStatus() == LudoToken.TokenStatus.RELEASED).findFirst().orElse(null);
    if (token == null) {
      return;
    }
    int oldTileId = token.getCurrentTile().getTileId();
    Tile nextTile = findNextTile(token, diceRoll);
    token.setCurrentTile(nextTile);
    notifyTokenMoved(currentPlayer, token, diceRoll, oldTileId, nextTile.getTileId());

    // Check for and handle token captures
    handleTokenCapture(nextTile);
  }

  /**
   * Handles the capturing of tokens. If a player's token lands on a tile occupied by one or more
   * tokens of an opponent, the opponent's tokens are sent back to their respective starting
   * positions and their status is set to {@link LudoToken.TokenStatus#NOT_RELEASED}. Observers are
   * notified of each capture.
   *
   * @param destinationTile The {@link Tile} where the current player's token has landed.
   */
  private void handleTokenCapture(Tile destinationTile) {
    // Check all players except current player for tokens on the destination tile
    for (Player player : players) {
      if (player == currentPlayer) {
        continue; // Skip current player's tokens
      }

      ((LudoPlayer) player).getTokens().stream()
          .filter(token -> token.getCurrentTile().getTileId() == destinationTile.getTileId())
          .forEach(token -> {
            // Send token back to its starting position
            int startIndex = ((LudoGameBoard) board).getPlayerStartIndexes()[players.indexOf(
                player)];
            int oldTileId = token.getCurrentTile().getTileId();
            token.setCurrentTile(board.getTile(startIndex));
            token.setStatus(LudoToken.TokenStatus.NOT_RELEASED);
            notifyTokenCaptured(player, token, oldTileId);
          });
    }
  }

  /**
   * Notifies all registered observers (typically a {@link LudoGameController}) that a player's
   * token has been released from their start area.
   *
   * @param player  The {@link Player} whose token was released.
   * @param tileId  The ID of the tile onto which the token was released.
   * @param tokenId The ID (index) of the released token within the player's token list.
   */
  private void notifyTokenReleased(Player player, int tileId, int tokenId) {
    observers.forEach(
        observer -> ((LudoGameController) observer).onTokenReleased(player, tileId, tokenId));
  }

  /**
   * Notifies all registered observers that a player's token has moved.
   *
   * @param player    The {@link Player} whose token moved.
   * @param token     The {@link LudoToken} that moved.
   * @param diceRoll  The dice roll that resulted in this move.
   * @param oldTileId The ID of the tile the token moved from.
   * @param newTileId The ID of the tile the token moved to.
   */
  private void notifyTokenMoved(Player player, LudoToken token, int diceRoll, int oldTileId,
      int newTileId) {
    observers.forEach(
        observer -> ((LudoGameController) observer).onTokenMoved(player, token, diceRoll, oldTileId,
            newTileId));
  }

  /**
   * Notifies all registered observers that a player's token has been captured.
   *
   * @param player    The {@link Player} whose token was captured.
   * @param token     The {@link LudoToken} that was captured and sent back to its start.
   * @param oldTileId The ID of the tile the token was on before being captured.
   */
  private void notifyTokenCaptured(Player player, LudoToken token, int oldTileId) {
    observers.forEach(
        observer -> ((LudoGameController) observer).onTokenCaptured(player, token, oldTileId));
  }

  /**
   * Notifies all registered observers that a player's token has reached the finish.
   *
   * @param player The {@link Player} whose token finished.
   * @param token  The {@link LudoToken} that reached the finish.
   */
  private void notifyTokenFinished(Player player, LudoToken token) {
    observers.forEach(observer -> ((LudoGameController) observer).onTokenFinished(player, token));
  }

  /**
   * Notifies all registered observers that a player's turn has been skipped. This typically occurs
   * if the player rolls a number other than 6 and has no tokens on the track that can be moved.
   *
   * @param player   The {@link Player} whose turn was skipped.
   * @param diceRoll The dice roll that led to the turn being skipped.
   */
  private void notifyTurnSkipped(Player player, int diceRoll) {
    observers.forEach(observer -> ((LudoGameController) observer).onTurnSkipped(player, diceRoll));
  }
}
