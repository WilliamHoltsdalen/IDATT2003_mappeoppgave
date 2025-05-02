package edu.ntnu.idi.idatt.model.game;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.ntnu.idi.idatt.model.Board;
import edu.ntnu.idi.idatt.model.dice.Dice;
import edu.ntnu.idi.idatt.model.Player;
import edu.ntnu.idi.idatt.model.tile.TileAction;
import static edu.ntnu.idi.idatt.model.validators.ArgumentValidator.boardGameCreateDiceValidator;
import static edu.ntnu.idi.idatt.model.validators.ArgumentValidator.boardGameSetCurrentPlayerValidator;
import static edu.ntnu.idi.idatt.model.validators.ArgumentValidator.boardGameSetPlayersValidator;
import edu.ntnu.idi.idatt.observer.BoardGameObserver;
import edu.ntnu.idi.idatt.observer.BoardGameSubject;

/**
 * <h3>BoardGame class</h3>
 *
 * <p>Abstract class implementing common functionality for board games.
 * This class provides the base implementation for both Chutes and Ladders and Ludo games.
 */
public abstract class BoardGame implements Game, BoardGameSubject {
  private static final Logger logger = LoggerFactory.getLogger(BoardGame.class);
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

  @Override
  public void initializeGame() {
    logger.info("Game started!");
    players.forEach(player -> player.placeOnTile(board.getTile(0)));
    setCurrentPlayer(players.getFirst());
  }

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

  @Override
  public void setBoard(Board board) {
    this.board = board;
  }

  @Override
  public void setPlayers(List<Player> players) {
    boardGameSetPlayersValidator(players);

    this.players = players;
  }

  /**
   * Sets the current player.
   * @param player The player to set as current
   */
  @Override
  public void setCurrentPlayer(Player player) {
    boardGameSetCurrentPlayerValidator(player);

    this.currentPlayer = player;
    logger.info("Set current player to: {}", currentPlayer.getName());
  }

  @Override
  public void addObserver(BoardGameObserver observer) {
    observers.add(observer);
  }

  @Override
  public void removeObserver(BoardGameObserver observer) {
    observers.remove(observer);
  }

  /**
   * Checks if round number should be incremented, and if so, increments it.
   */
  @Override
  public void handleRoundNumber() {
    if (currentPlayer == players.getFirst()) {
      incrementRoundNumber();
    }
  }

  /**
   * Increments the round number.
   */
  protected void incrementRoundNumber() {
    roundNumber++;
    logger.info("Incremented round number to {}", roundNumber);
    notifyRoundNumberIncremented(roundNumber);
  }

  /**
   * Creates the given number of dice and adds them to the game.
   *
   * @param diceCount The number of dice to create
   */
  protected void createDice(int diceCount) {
    boardGameCreateDiceValidator(diceCount);

    this.dice = new Dice(diceCount);
  }

  /**
   * Handles tile actions for the current player.
   */
  protected void handleTileAction() {
    TileAction landAction = currentPlayer.getCurrentTile().getLandAction();
    if (landAction == null) {
      return;
    }
    landAction.perform(currentPlayer, board);
    notifyTileActionPerformed(currentPlayer, landAction);
    logger.info("Performed tile action: {}", landAction.getDescription());
  }

  /**
   * Updates the current player to the next player in the list.
   */
  protected void updateCurrentPlayer() {
    int currentIndex = players.indexOf(currentPlayer);
    int nextIndex = (currentIndex + 1) % players.size();
    setCurrentPlayer(players.get(nextIndex));
  }


  /**
   * Checks if the game has a winner and notifies observers if it does.
   */
  protected void checkWinCondition() {
    Player winner = getWinner();
    if (winner != null) {
      logger.info("Game finished! Winner: {}", winner.getName());
      notifyGameFinished(winner);
    }
  }

  @Override
  public void notifyPlayerMoved(Player player, int diceRoll, int newTileId) {
    observers.forEach(observer -> observer.onPlayerMoved(player, diceRoll, newTileId));
  }

  @Override
  public void notifyRoundNumberIncremented(int roundNumber) {
    observers.forEach(observer -> observer.onRoundNumberIncremented(roundNumber));
  }

  @Override
  public void notifyCurrentPlayerChanged(Player player) {
    observers.forEach(observer -> observer.onCurrentPlayerChanged(player));
  }

  @Override
  public void notifyTileActionPerformed(Player player, TileAction tileAction) {
    observers.forEach(observer -> observer.onTileActionPerformed(player, tileAction));
  }

  @Override
  public void notifyGameFinished(Player winner) {
    observers.forEach(observer -> observer.onGameFinished(winner));
  }
} 