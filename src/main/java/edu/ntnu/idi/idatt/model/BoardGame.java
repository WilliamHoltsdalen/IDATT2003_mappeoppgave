package edu.ntnu.idi.idatt.model;

import static edu.ntnu.idi.idatt.model.validators.ArgumentValidator.boardGameAddPlayersValidator;
import static edu.ntnu.idi.idatt.model.validators.ArgumentValidator.boardGameCreateDiceValidator;
import static edu.ntnu.idi.idatt.model.validators.ArgumentValidator.boardGameSetBoardValidator;
import static edu.ntnu.idi.idatt.model.validators.ArgumentValidator.boardGameSetCurrentPlayerValidator;
import static edu.ntnu.idi.idatt.model.validators.ArgumentValidator.boardGameSetPlayersValidator;

import edu.ntnu.idi.idatt.observer.BoardGameObserver;
import java.util.ArrayList;
import java.util.List;

/**
 * <h3>BoardGame class</h3>
 *
 * <p>This class represents a game of chutes and ladders. It contains a board, a list of players,
 * Dice, and the current player. It also has a round number and a list of observers.
 */
public class BoardGame {
  private Board board;
  private List<Player> players;
  private Dice dice;
  private Player currentPlayer;
  private int roundNumber;
  private final List<BoardGameObserver> observers;

  /**
   * Constructor for BoardGame class.
   *
   * @param board The board to use in the game.
   * @param players The list of players to use in the game.
   * @param diceCount The number of dice to use in the game.
   */
  public BoardGame(Board board, List<Player> players, int diceCount) {
    this.players = new ArrayList<>();
    this.roundNumber = 0;
    this.observers = new ArrayList<>();

    setBoard(board);
    addPlayers(players);
    createDice(diceCount);
  }

  /**
   * Adds an observer to the list of observers.
   *
   * @param observer The observer to add.
   */
  public void addObserver(BoardGameObserver observer) {
    observers.add(observer);
  }

  /**
   * Removes an observer from the list of observers.
   *
   * @param observer The observer to remove.
   */
  public void removeObserver(BoardGameObserver observer) {
    observers.remove(observer);
  }

  /**
   * Returns the list of observers.
   *
   * @return The list of observers.
   */
  public List<BoardGameObserver> getObservers() {
    return observers;
  }

  /**
   * Notifies all observers that a player has moved.
   *
   * @param player The player who moved.
   * @param newTileId The ID of the new tile.
   */
  public void notifyPlayerMoved(Player player, int newTileId) {
    for (BoardGameObserver observer : observers) {
      observer.onPlayerMoved(player, newTileId);
    }
  }

  /**
   * Notifies all observers that the game state has changed.
   *
   * @param stateUpdate The updated game state.
   */
  public void notifyGameStateChanged(String stateUpdate) {
    for (BoardGameObserver observer : observers) {
      observer.onGameStateChanged(stateUpdate);
    }
  }

  /**
   * Notifies all observers that the game has finished.
   *
   * @param winner The player who won the game.
   */
  public void notifyGameFinished(Player winner) {
    for (BoardGameObserver observer : observers) {
      observer.onGameFinished(winner);
    }
  }

  /**
   * Returns the board.
   *
   * @return The board.
   */
  public Board getBoard() {
    return board;
  }

  /**
   * Returns the list of players.
   *
   * @return The list of players.
   */
  public List<Player> getPlayers() {
    return players;
  }

  /**
   * Returns the dice.
   *
   * @return The dice.
   */
  public Dice getDice() {
    return dice;
  }

  /**
   * Returns the current player.
   *
   * @return The current player.
   */
  public Player getCurrentPlayer() {
    return currentPlayer;
  }

  /**
   * Returns the round number.
   *
   * @return The round number.
   */
  public int getRoundNumber() {
    return roundNumber;
  }

  /**
   * Returns the winner of the game.
   *
   * @return The winner of the game.
   */
  public Player getWinner() {
    for (Player player : players) {
      if (player.getCurrentTile().getTileId() == this.board.getTileCount()) {
        return player;
      }
    }
    return null;
  }

  /**
   * Sets the board.
   *
   * @param board The board to set.
   */
  public void setBoard(Board board) {
    boardGameSetBoardValidator(board);
    this.board = board;
  }

  /**
   * Sets the list of players.
   *
   * @param players The list of players to set.
   */
  public void setPlayers(List<Player> players) {
    boardGameSetPlayersValidator(players);
    this.players = players;
  }

  /**
   * Adds players to the list of players.
   *
   * @param players The players to add.
   */
  private void addPlayers(List<Player> players) {
    boardGameAddPlayersValidator(players, this.players.size());

    this.players.addAll(players);
  }

  /**
   * Creates the dice.
   *
   * @param diceCount The number of dice to create.
   */
  private void createDice(int diceCount) {
    boardGameCreateDiceValidator(diceCount);

    this.dice = new Dice(diceCount);
  }

  /**
   * Sets the current player.
   *
   * @param player The player to set as current.
   */
  public void setCurrentPlayer(Player player) {
    boardGameSetCurrentPlayerValidator(player);

    this.currentPlayer = player;
  }

  /**
   * Increments the round number.
   */
  public void incrementRoundNumber() {
    roundNumber++;
  }
}
