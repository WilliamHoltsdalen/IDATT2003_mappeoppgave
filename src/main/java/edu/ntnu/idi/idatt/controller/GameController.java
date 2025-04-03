package edu.ntnu.idi.idatt.controller;

import edu.ntnu.idi.idatt.model.Board;
import edu.ntnu.idi.idatt.model.BoardGame;
import edu.ntnu.idi.idatt.model.Dice;
import edu.ntnu.idi.idatt.model.Player;
import edu.ntnu.idi.idatt.observer.BoardGameObserver;
import java.util.List;

public class GameController {
  private BoardGame boardGame;

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
      return;
    }

    for (Player player : boardGame.getPlayers()) {
      player.placeOnTile(boardGame.getBoard().getTile(0));
    }

    this.boardGame.setCurrentPlayer(boardGame.getPlayers().getFirst());
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
}
