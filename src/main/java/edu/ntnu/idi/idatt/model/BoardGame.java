package edu.ntnu.idi.idatt.model;

import edu.ntnu.idi.idatt.model.interfaces.TileAction;
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
    this.roundNumber = 1;
    this.observers = new ArrayList<>();

    setBoard(board);
    setPlayers(players);
    createDice(diceCount);

    initializeGame();
  }

  private void initializeGame() {
    players.forEach(player -> player.placeOnTile(board.getTile(0)));
    setCurrentPlayer(players.getFirst());
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
   * Returns the list of observers.
   *
   * @return The list of observers.
   */
  public List<BoardGameObserver> getObservers() {
    return observers;
  }

  /**
   * Returns the winner of the game.
   *
   * @return The winner of the game.
   */
  public Player getWinner() {
    for (Player player : players) {
      if (player.getCurrentTile().getTileId() == board.getTileCount()) {
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

  private void checkWinCondition() {
    if (getWinner() != null) {
      notifyGameFinished(getWinner());
    }
  }

  /**
   * Increments the round number.
   */
  public void incrementRoundNumber() {
    roundNumber++;
    notifyRoundNumberIncremented(roundNumber);
  }

  /**
   * Checks if round number should be incremented, and if so, increments it.
   */
  private void handleRoundNumber() {
    if (currentPlayer == players.getFirst()) {
      incrementRoundNumber();
    }
  }

  /**
   * Finds the next tile for the given player based on the dice roll. There are two unique cases
   * for calculating the next tile.
   *
   * <p>In the following cases, the term 'expected tile' refers to the tile that the player is on,
   * plus the dice roll. (e.g. the player is on tile 20, with a dice roll of 3, the expected tile
   * is 23).
   * <ul>
   *   <li>If the id of the 'expected tile' is less than or equal to the board's tile count, the
   *       next tile is the 'expected tile'.
   *   <li>If the id of the 'expected tile' is greater than the board's tile count, the next tile
   *       is set as (tileCount - overshoot). E.g. if the player is on tile 85 with a dice roll of
   *       9 and the board has a tile count of 90, the next tile is 86.
   * </ul>
   *
   * @param player The player to find the next tile for
   * @param diceRoll The value of the dice roll to use in the calculation.
   * @return The next tile for the player, meaning the tile the specified player will move to.
   */
  private Tile findNextTile(Player player, int diceRoll) {
    int currentTileId = player.getCurrentTile().getTileId();
    int nextTileId = currentTileId + diceRoll;
    int tileCount = board.getTileCount();

    if (nextTileId <= tileCount) {
      return board.getTile(nextTileId);
    } else {
      return board.getTile(tileCount - (nextTileId - tileCount));
    }
  }

  /**
   * Rolls the dice for the current player and moves them to the new tile.
   *
   */
  public void rollDiceAndMovePlayer() {
    dice.rollDice();
    Tile nextTile = findNextTile(currentPlayer, dice.getTotalValue());
    currentPlayer.placeOnTile(nextTile);
    notifyPlayerMoved(currentPlayer, dice.getTotalValue(), nextTile.getTileId());
  }

  /**
   * Handles tile actions for the current player. Checks if the player's current tile has a tile action
   * and if so, perform the action.
   */
  private void handleTileAction() {
    TileAction landAction = currentPlayer.getCurrentTile().getLandAction();
    if (landAction == null) {
      return;
    }
    landAction.perform(currentPlayer, getBoard());
    notifyTileActionPerformed(currentPlayer, landAction);
  }

  /**
   * Updates the current player to the next player in the list of players. If the current player
   * is the last player in the list, the first player in the list becomes the current player.
   */
  public void updateCurrentPlayer() {
    int currentIndex = players.indexOf(currentPlayer);
    int nextIndex = (currentIndex + 1) % players.size();
    setCurrentPlayer(players.get(nextIndex));
    notifyCurrentPlayerChanged(currentPlayer);
  }

  /**
   * Performs a player turn by calling the appropriate methods to ensure the round number is updated,
   * the dice are rolled, and the current player is moved to the next calculated tile. After the move
   * the new tile for the player is checked for a tile action, and if present, the action is
   * performed. Finally, the current player is updated to the next in the list.
   */
  public void performPlayerTurn() {
    rollDiceAndMovePlayer();
    handleTileAction();
    checkWinCondition();
    updateCurrentPlayer();
    handleRoundNumber();
  }

  /**
   * Notifies all observers that a player has moved, along with the dice roll and the id of the new tile.
   *
   * @param player The player who moved.
   * @param diceRoll The value of the dice roll.
   * @param newTileId The ID of the new tile.
   */
  public void notifyPlayerMoved(Player player, int diceRoll, int newTileId) {
    observers.forEach(observer -> observer.onPlayerMoved(player, diceRoll, newTileId));
  }

  /**
   * Notifies all observers that the round number has been incremented.
   *
   * @param roundNumber The new round number.
   */
  public void notifyRoundNumberIncremented(int roundNumber) {
    observers.forEach(observer -> observer.onRoundNumberIncremented(roundNumber));
  }

  /**
   * Notifies all observers that the current player has changed.
   *
   * @param player The new current player.
   */
  public void notifyCurrentPlayerChanged(Player player) {
    observers.forEach(observer -> observer.onCurrentPlayerChanged(player));
  }

  /**
   * Notifies all observers that a tile action has been performed. The tile action object is passed
   * as argument, so the observers can get whatever info from the tile action they may need.
   *
   * @param tileAction the tile action that was performed.
   * @param player The player who performed the action.
   */
  public void notifyTileActionPerformed(Player player, TileAction tileAction) {
    observers.forEach(observer -> observer.onTileActionPerformed(player, tileAction));
  }

  /**
   * Notifies all observers that the game has finished.
   *
   * @param winner The player who won the game.
   */
  public void notifyGameFinished(Player winner) {
    observers.forEach(observer -> observer.onGameFinished(winner));
  }
}
