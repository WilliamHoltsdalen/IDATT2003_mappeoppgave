package edu.ntnu.idi.idatt.model.game;

import java.util.List;

import edu.ntnu.idi.idatt.model.board.Board;
import edu.ntnu.idi.idatt.model.dice.Dice;
import edu.ntnu.idi.idatt.model.player.Player;

/**
 * <h3>Game interface</h3>
 *
 * <p>Interface defining common operations for all board games.
 */
public interface Game {
  /**
   * Initializes the game with the given players.
   *
   */
  void initializeGame();

  /**
   * Gets the game board.
   *
   * @return the game board
   */
  Board getBoard();

  /**
   * Gets the current round number.
   *
   * @return the current round number
   */
  int getRoundNumber();

  /**
   * Gets all players in the game.
   *
   * @return list of all players
   */
  List<Player> getPlayers();

  /**
   * Gets the current player.
   *
   * @return the current player
   */
  Player getCurrentPlayer();

  /**
   * Gets the game dice.
   *
   * @return the game dice
   */
  Dice getDice();

  /**
   * Sets the game board.
   *
   * @param board the game board
   */
  void setBoard(Board board);

  /**
   * Sets the game players.
   *
   * @param players the game players
   */
  void setPlayers(List<Player> players);

  /**
   * Sets the current player.
   *
   * @param player the current player
   */
  void setCurrentPlayer(Player player);

  /**
   * Checks if the game has a winner.
   *
   * @return the player that has won the game, or null if there is no winner
   */
  Player getWinner();

  /**
   * Handles the round number.
   */
  void handleRoundNumber();
}