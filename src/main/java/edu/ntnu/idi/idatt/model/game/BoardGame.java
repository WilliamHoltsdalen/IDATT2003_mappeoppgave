package edu.ntnu.idi.idatt.model.game;

import static edu.ntnu.idi.idatt.model.validator.ArgumentValidator.boardGameCreateDiceValidator;
import static edu.ntnu.idi.idatt.model.validator.ArgumentValidator.boardGameSetCurrentPlayerValidator;
import static edu.ntnu.idi.idatt.model.validator.ArgumentValidator.boardGameSetPlayersValidator;

import edu.ntnu.idi.idatt.model.board.Board;
import edu.ntnu.idi.idatt.model.dice.Dice;
import edu.ntnu.idi.idatt.model.player.Player;
import edu.ntnu.idi.idatt.observer.BoardGameObserver;
import edu.ntnu.idi.idatt.observer.BoardGameSubject;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * BoardGame class
 *
 * <p>Abstract class implementing common functionality for board games.
 * This class provides the base implementation for both Chutes and Ladders and Ludo games.
 */
public abstract class BoardGame implements Game, BoardGameSubject {
  protected static final Logger logger = LoggerFactory.getLogger(BoardGame.class);
  protected final List<BoardGameObserver> observers;
  protected Board board;
  protected List<Player> players;
  protected Player currentPlayer;
  protected Dice dice;
  protected int roundNumber;

  /**
   * Constructor for BoardGame.
   *
   * @param board The game board
   * @param players The list of players
   * @param diceCount The number of dice to use
   */
  protected BoardGame(Board board, List<Player> players, int diceCount) {
    this.observers = new ArrayList<>();

    this.roundNumber = 1;
    setBoard(board);
    setPlayers(players);
    createDice(diceCount);

    initializeGame();
  }

  /**
   * Initializes the game by setting the initial state of the game.
   * This method is implemented by the subclasses to define game-specific setup,
   * such as placing pieces, determining the starting player, or configuring initial game
   * parameters.
   */
  public abstract void initializeGame();

  @Override
  public Board getBoard() {
    return board;
  }

  @Override
  public Dice getDice() {
    return dice;
  }

  @Override
  public List<Player> getPlayers() {
    return players;
  }

  @Override
  public Player getCurrentPlayer() {
    return currentPlayer;
  }


  @Override
  public int getRoundNumber() {
    return roundNumber;
  }

  /**
   * Sets the game board.
   *
   * @param board The {@link Board} to be used for the game.
   */
  @Override
  public void setBoard(Board board) {
    this.board = board;
  }

  /**
   * Sets the list of players for the game.
   *
   * @param players A list of {@link Player} objects participating in the game.
   */
  @Override
  public void setPlayers(List<Player> players) {
    boardGameSetPlayersValidator(players);

    this.players = players;
  }

  /**
   * Sets the current player.
   *
   * @param player The player to set as current
   */
  @Override
  public void setCurrentPlayer(Player player) {
    boardGameSetCurrentPlayerValidator(player);

    this.currentPlayer = player;
    logger.debug("Set current player to: {}", currentPlayer.getName());
  }

  /**
   * Adds an observer to be notified of game events.
   *
   * @param observer The {@link BoardGameObserver} to add.
   */
  @Override
  public void addObserver(BoardGameObserver observer) {
    observers.add(observer);
  }

  /**
   * Removes an observer from the list of observers.
   *
   * @param observer The {@link BoardGameObserver} to remove.
   */
  @Override
  public void removeObserver(BoardGameObserver observer) {
    observers.remove(observer);
  }

  /**
   * Handles the progression of game rounds. This involves checking if the current player
   * is the first player in the turn order, and if so, incrementing the round number.
   */
  @Override
  public void handleRoundNumber() {
    if (currentPlayer == players.getFirst()) {
      incrementRoundNumber();
    }
  }

  /**
   * Increments the round number and notifies observers.
   */
  protected void incrementRoundNumber() {
    roundNumber++;
    logger.info("Incremented round number to {}", roundNumber);
    notifyRoundNumberIncremented(roundNumber);
  }

  /**
   * Creates the dice to be used in the game.
   *
   * @param diceCount The number of dice to create.
   */
  protected void createDice(int diceCount) {
    boardGameCreateDiceValidator(diceCount);

    this.dice = new Dice(diceCount);
  }

  /**
   * Updates the current player to the next player in the turn order
   * and notifies observers of the change.
   */
  protected void updateCurrentPlayer() {
    int currentIndex = players.indexOf(currentPlayer);
    int nextIndex = (currentIndex + 1) % players.size();
    setCurrentPlayer(players.get(nextIndex));
    notifyCurrentPlayerChanged(currentPlayer);
  }

  /**
   * Checks if a win condition has been met. If a winner is found,
   * it notifies observers that the game has finished.
   */
  protected void checkWinCondition() {
    Player winner = getWinner();
    if (winner != null) {
      logger.info("Game finished! Winner: {}", winner.getName());
      notifyGameFinished(winner);
    }
  }

  /**
   * Notifies all registered observers that the round number has incremented.
   *
   * @param roundNumber The new round number.
   */
  @Override
  public void notifyRoundNumberIncremented(int roundNumber) {
    observers.forEach(observer -> observer.onRoundNumberIncremented(roundNumber));
  }

  /**
   * Notifies all registered observers that the current player has changed.
   *
   * @param player The new current {@link Player}.
   */
  @Override
  public void notifyCurrentPlayerChanged(Player player) {
    observers.forEach(observer -> observer.onCurrentPlayerChanged(player));
  }

  /**
   * Notifies all registered observers that the game has finished.
   *
   * @param winner The {@link Player} who won the game.
   */
  @Override
  public void notifyGameFinished(Player winner) {
    observers.forEach(observer -> observer.onGameFinished(winner));
  }
} 