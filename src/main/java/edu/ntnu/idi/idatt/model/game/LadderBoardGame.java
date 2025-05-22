package edu.ntnu.idi.idatt.model.game;

import edu.ntnu.idi.idatt.controller.laddergame.LadderGameController;
import edu.ntnu.idi.idatt.model.board.Board;
import edu.ntnu.idi.idatt.model.player.LadderGamePlayer;
import edu.ntnu.idi.idatt.model.player.Player;
import edu.ntnu.idi.idatt.model.tile.LadderGameTile;
import edu.ntnu.idi.idatt.model.tile.Tile;
import edu.ntnu.idi.idatt.model.tile.TileAction;
import java.util.List;

/**
 * LadderBoardGame.
 *
 * <p>Represents the game logic for a Chutes and Ladders type game.
 * This class extends {@link BoardGame} and implements game-specific rules for player movement,
 * tile actions (ladders/slides), and win conditions.</p>
 *
 * @see BoardGame
 * @see LadderGamePlayer
 * @see LadderGameTile
 * @see LadderGameController
 */
public class LadderBoardGame extends BoardGame {

  /**
   * Constructs a LadderBoardGame.
   *
   * @param board The {@link Board} instance for this game.
   * @param players A list of {@link Player}s (expected to be {@link LadderGamePlayer} instances).
   * @param diceCount The number of dice to be used in the game.
   */
  public LadderBoardGame(Board board, List<Player> players, int diceCount) {
    super(board, players, diceCount);
  }

  /**
   * Initializes the game state. Places all players on the starting tile (tile 0)
   * and sets the first player in the list as the current player.
   */
  @Override
  public void initializeGame() {
    logger.info("Game started!");

    players.forEach(player -> ((LadderGamePlayer) player).placeOnTile(board.getTile(0)));
    setCurrentPlayer(players.getFirst());
  }

  /**
   * Determines the winner of the game.
   * A player wins if their current tile ID is equal to the total number of tiles on the board.
   *
   * @return The winning {@link Player}, or null if no player has won yet.
   */
  @Override
  public Player getWinner() {
    for (Player player : players) {
      if (((LadderGamePlayer) player).getCurrentTile().getTileId() == board.getTileCount()) {
        return player;
      }
    }
    return null;
  }

  /**
   * Rolls all dice in the game and returns their total value.
   *
   * @return The sum of values from all rolled dice.
   */
  public int rollDice() {
    dice.rollDice();
    return dice.getTotalValue();
  }

  /**
   * Performs a complete turn for the current player based on a dice roll.
   * This involves moving the player, handling any tile action (ladder/slide),
   * checking for a win condition, updating to the next player, and handling the round number.
   *
   * @param diceRoll The total value rolled on the dice.
   */
  public void performPlayerTurn(int diceRoll) {
    movePlayer(diceRoll);
    handleTileAction();
    checkWinCondition();
    updateCurrentPlayer();
    handleRoundNumber();
  }

  /**
   * Checks if the current player has landed on a tile with a {@link TileAction} (ladder or slide).
   * If an action exists, it is performed, and observers are notified.
   */
  protected void handleTileAction() {
    TileAction landAction = ((LadderGameTile) ((LadderGamePlayer) currentPlayer).getCurrentTile())
        .getLandAction();
    if (landAction == null) {
      return;
    }
    landAction.perform(currentPlayer, board);
    notifyTileActionPerformed(currentPlayer, landAction);
    logger.info("{} performed tile action: {}", currentPlayer.getName(), landAction.getDescription());
  }

  /**
   * Calculates the destination tile for a player based on their current tile and a dice roll.
   * Implements the "exact landing" rule: if a roll overshoots the final tile,
   * the player moves back by the number of overshoot steps.
   *
   * @param player The {@link Player} (expected to be a {@link LadderGamePlayer}) making the move.
   * @param diceRoll The value of the dice roll.
   * @return The destination {@link Tile}.
   */
  private Tile findNextTile(Player player, int diceRoll) {
    int currentTileId = ((LadderGamePlayer) player).getCurrentTile().getTileId();
    int nextTileId = currentTileId + diceRoll;
    int tileCount = board.getTileCount();

    if (nextTileId <= tileCount) {
      return board.getTile(nextTileId);
    } else {
      return board.getTile(tileCount - (nextTileId - tileCount));
    }
  }

  /**
   * Moves the current player based on the provided dice roll value.
   * Finds the next tile using {@link #findNextTile(Player, int)} and updates the player's position.
   * Notifies observers of the player movement.
   *
   * @param diceRoll The total value rolled on the dice.
   */
  public void movePlayer(int diceRoll) {
    Tile nextTile = findNextTile(currentPlayer, diceRoll);
    ((LadderGamePlayer) currentPlayer).placeOnTile(nextTile);
    notifyPlayerMoved(currentPlayer, diceRoll, nextTile.getTileId());
  }

  /**
   * Notifies registered observers (specifically {@link LadderGameController} instances)
   * that a tile action has been performed.
   *
   * @param player The {@link Player} who triggered the action.
   * @param tileAction The {@link TileAction} that was performed.
   */
  public void notifyTileActionPerformed(Player player, TileAction tileAction) {
    observers.forEach(observer -> ((LadderGameController) observer)
        .onTileActionPerformed(player, tileAction));
  }

  /**
   * Notifies registered observers (specifically {@link LadderGameController} instances)
   * that a player has moved.
   *
   * @param player The {@link Player} who moved.
   * @param diceRoll The dice roll value that caused the movement.
   * @param newTileId The ID of the tile the player moved to.
   */
  private void notifyPlayerMoved(Player player, int diceRoll, int newTileId) {
    observers.forEach(observer -> ((LadderGameController) observer)
        .onPlayerMoved(player, diceRoll, newTileId));
  }
} 