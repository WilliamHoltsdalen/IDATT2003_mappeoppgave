package edu.ntnu.idi.idatt.controllers;

import edu.ntnu.idi.idatt.model.Board;
import edu.ntnu.idi.idatt.model.BoardGame;
import edu.ntnu.idi.idatt.model.Player;
import edu.ntnu.idi.idatt.model.Tile;
import edu.ntnu.idi.idatt.model.Dice;
import edu.ntnu.idi.idatt.model.TileAction;
import edu.ntnu.idi.idatt.utils.PlayerFileHandler;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GameController {
  private BoardGame boardGame;
  PlayerFileHandler playerFileHandler = new PlayerFileHandler();

  /**
   * Constructor for GameController. Initializes the controller with the given player file path.
   */
  public GameController() {
    initController("src/main/resources/textfiles/players.csv");
  }

  public Player getWinner() {
    return this.boardGame.getWinner();
  }

  public int getRoundNumber() {
    return this.boardGame.getRoundNumber();
  }

  public List<Player> getPlayers() {
    return this.boardGame.getPlayers();
  }

  private Board getBoard() {
    return this.boardGame.getBoard();
  }

  private Dice getDice() {
    return this.boardGame.getDice();
  }

  private Player getCurrentPlayer() {
    return this.boardGame.getCurrentPlayer();
  }

  /**
   * Initializes the controller by creating a new BoardGame instance with the players from the
   * player file at the given path.
   *
   * <p>All players are placed on the 0th tile of the board, and the current player is set to the
   * first player. Number of players must be in interval [2, 5].
   *
   * @param playerFilePath The path to the player file.
   */
  private void initController(String playerFilePath) {
    List<Player> players = new ArrayList<>();
    try {
      players =  playerFileHandler.readFile(playerFilePath);
    } catch (IOException e) {
      e.printStackTrace();
    }

    if (players.size() < 2 || players.size() >5) {
      throw new IllegalArgumentException("Player count must be between 2 and 5");
    }

    // Create a new board game with the given players.
    this.boardGame = new BoardGame(10, 9, players, 2);

    // Place all players on the 0th tile of the board.
    for (Player player : this.boardGame.getPlayers()) {
      player.placeOnTile(this.boardGame.getBoard().getTile(0));
    }

    // Set the current player to the first player.
    this.boardGame.setCurrentPlayer(this.boardGame.getPlayers().getFirst());
  }

  /**
   * Updates the current player to the next player in the list of players. If the current player
   * is the last player in the list, the first player in the list becomes the current player.
   */
  public void updateCurrentPlayer() {
    List<Player> players = getPlayers();
    int currentIndex = players.indexOf(getCurrentPlayer());
    int nextIndex = (currentIndex + 1) % players.size();
    this.boardGame.setCurrentPlayer(players.get(nextIndex));
  }

  /**
   * Finds the next tile for the given player based on the dice roll. There are two unique cases
   * for calculating the next tile.
   * <p>
   * In the following cases, the term 'expected tile' refers to the tile that the player is on, plus
   * the dice roll. (e.g. the player is on tile 20, with a dice roll of 3, the expected tile is 23)
   * <ul>
   *   <li>If the id of the 'expected tile' is less than or equal to the board's tile count, the next tile is
   *       the 'expected tile'.
   *   <li>If the id of the 'expected tile' is greater than the board's tile count, the next tile
   *       is set as (tileCount - overshoot). E.g. if the player is on tile 85 with a dice roll of
   *       9 and the board has a tile count of 90, the next tile is 86.
   * </ul>
   * @param player The player to find the next tile for
   * @param diceRoll The value of the dice roll to use in the calculation.
   * @return The next tile for the player, meaning the tile the specified player will move to.
   */
  private Tile findNextTile(Player player, int diceRoll) {
    Board board = getBoard();
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
   * Handles tile actions for the given player. Check if the player's current tile has a tile action
   * and if so, perform the action.
   * @param player The player to handle the tile action for.
   */
  private void handleTileAction(Player player) {
    TileAction landAction = player.getCurrentTile().getLandAction();
    if (landAction == null) {
      return;
    }

    landAction.perform(player, getBoard());
  }

  /**
   * Rolls the dice for the given player and moves them to the new tile.
   *
   * @param player The player to roll the dice for.
   */
  public void rollDiceAndMovePlayer(Player player) {
    Dice dice = getDice();
    dice.rollDice();
    int diceRoll = dice.getTotalValue();
    Tile nextTile = findNextTile(player, diceRoll);

    player.placeOnTile(nextTile);
  }

  /**
   * Checks if round number should be incremented, and if so, increments it.
   */
  private void handleRoundNumber() {
    if (getCurrentPlayer() == this.boardGame.getPlayers().getFirst()) {
      this.boardGame.incrementRoundNumber();
    }
  }

  /**
   * Performs a player move by calling methods to roll dice and move current player. After the move
   * the new tile for the player is checked for a tile action, and if present, the action is
   * performed. Finally, the current player is updated to the next in the list.
   */
  public void performPlayerMove() {
    Player currentPlayer = getCurrentPlayer();
    handleRoundNumber();
    rollDiceAndMovePlayer(currentPlayer);
    handleTileAction(currentPlayer);
    updateCurrentPlayer();
  }
}
