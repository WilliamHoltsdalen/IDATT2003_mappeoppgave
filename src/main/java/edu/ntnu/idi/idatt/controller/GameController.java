package edu.ntnu.idi.idatt.controller;

import edu.ntnu.idi.idatt.model.Board;
import edu.ntnu.idi.idatt.model.BoardGame;
import edu.ntnu.idi.idatt.model.Dice;
import edu.ntnu.idi.idatt.model.Player;
import edu.ntnu.idi.idatt.observer.BoardGameObserver;
import java.util.List;
import java.util.function.BiConsumer;

public class GameController {
  private BoardGame boardGame;

  private BiConsumer<Board, List<Player>> onRestartGame;
  private Runnable onQuitGame;

  public GameController(Board board, List<Player> players) {
    initializeBoardGame(board, players);
  }

  /**
   * Returns the winner of the game.
   *
   * @return The winner of the game.
   */
  public Player getWinner() {
    return this.boardGame.getWinner();
  }

  /**
   * Returns the round number of the game.
   *
   * @return The round number of the game.
   */
  public int getRoundNumber() {
    return boardGame.getRoundNumber();
  }

  /**
   * Returns the players of the game.
   *
   * @return The players of the game.
   */
  public List<Player> getPlayers() {
    return boardGame.getPlayers();
  }

  /**
   * Returns the board object of the game.
   *
   * @return The board of the game.
   */
  public Board getBoard() {
    return boardGame.getBoard();
  }

  /**
   * Returns the dice object of the game.
   *
   * @return The dice of the game.
   */
  private Dice getDice() {
    return boardGame.getDice();
  }

  /**
   * Returns the current player of the game.
   *
   * @return The current player of the game.
   */
  public Player getCurrentPlayer() {
    return boardGame.getCurrentPlayer();
  }

  /**
   * Initializes the game by creating a new BoardGame instance with the given board and
   * list of players. Finally, it initializes the game by placing all players on the 0th tile and
   * setting the current player to the first player in the list.
   *
   * @param board the board to use in the game
   * @param players the list of players to use in the game
   */
  public void initializeBoardGame(Board board, List<Player> players) {
    try {
      boardGame = new BoardGame(board, players, 2);
    } catch (IllegalArgumentException e) {
      e.printStackTrace();
    }
  }

  /**
   * Adds an observer to the board game.
   *
   * @param observer The observer to add.
   */
  public void addObserver(BoardGameObserver observer) {
    boardGame.addObserver(observer);
  }

  /**
   * Performs a player move by calling the player turn method in the board game.
   */
  public void performPlayerTurn() {
    boardGame.performPlayerTurn();
  }

  public void restartGame() {
    if (onRestartGame != null) {
      onRestartGame.accept(boardGame.getBoard(), boardGame.getPlayers());
    }
  }

  public void quitGame() {
    if (onQuitGame != null) {
      onQuitGame.run();
    }
  }

  public void setOnRestartGame(BiConsumer<Board, List<Player>> onRestartGame) {
    this.onRestartGame = onRestartGame;
  }

  public void setOnQuitGame(Runnable onQuitGame) {
    this.onQuitGame = onQuitGame;
  }
}
