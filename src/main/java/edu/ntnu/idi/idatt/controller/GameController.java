package edu.ntnu.idi.idatt.controller;

import edu.ntnu.idi.idatt.factory.BoardFactory;
import edu.ntnu.idi.idatt.factory.PlayerFactory;
import edu.ntnu.idi.idatt.model.Board;
import edu.ntnu.idi.idatt.model.BoardGame;
import edu.ntnu.idi.idatt.model.Player;
import edu.ntnu.idi.idatt.model.Tile;
import edu.ntnu.idi.idatt.model.Dice;
import edu.ntnu.idi.idatt.model.interfaces.TileAction;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameController {
  private BoardGame boardGame;
  private final Map<Integer, Board> boardVariants;

  /**
   * Constructor for GameController.
   *
   * <p>Initializes the controller by calling the {@link #initController()} method. The available
   * board variants are loaded from the json files at the given paths, by calling the
   * {@link #loadBoardVariants(List)} method.
   */
  public GameController() {
    this.boardVariants = new HashMap<>();

    initController();
    loadBoardVariants(List.of("src/main/resources/textfiles/ladderBoard.json"));
  }

  /**
   * Returns a map of available board variants, where the key is the index of the board variant and
   * the value is the board object itself.
   *
   * @return A map of available board variants.
   */
  public Map<Integer, Board> getBoardVariants() {
    return this.boardVariants;
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
    return this.boardGame.getRoundNumber();
  }

  /**
   * Returns the players of the game.
   *
   * @return The players of the game.
   */
  public List<Player> getPlayers() {
    return this.boardGame.getPlayers();
  }

  /**
   * Returns the board object of the game.
   *
   * @return The board of the game.
   */
  private Board getBoard() {
    return this.boardGame.getBoard();
  }

  /**
   * Returns the dice object of the game.
   *
   * @return The dice of the game.
   */
  private Dice getDice() {
    return this.boardGame.getDice();
  }

  /**
   * Returns the current player of the game.
   *
   * @return The current player of the game.
   */
  private Player getCurrentPlayer() {
    return this.boardGame.getCurrentPlayer();
  }

  /**
   * Initializes the controller by creating a new BoardGame instance. The players are set to an
   * empty list, and the board variant is set to the classic 90 tile chutes and ladders board from
   * the {@link BoardFactory} class.
   *
   * @see BoardFactory#createBoard(String)
   */
  public void initController() {
    try {
      Board board = BoardFactory.createBoard("classic");
      List<Player> players = List.of();
      this.boardGame = new BoardGame(board, players, 2);
    } catch (IllegalArgumentException e) {
      e.printStackTrace();
    }
  }

  /**
   * Initializes the game by placing all the players on the 0th tile, and setting the current player
   * as the first player in the list.
   */
  public void initGame() {
    for (Player player : this.boardGame.getPlayers()) {
      player.placeOnTile(this.boardGame.getBoard().getTile(0));
    }

    this.boardGame.setCurrentPlayer(this.boardGame.getPlayers().getFirst());
  }

  /**
   * Loads the available board variants from the given json files, and adds them to the
   * {@link #boardVariants} map.
   *
   * @see BoardFactory#createBoard(String)
   * @param boardFilePaths The paths to the json files containing the board variants.
   */
  private void loadBoardVariants(List<String> boardFilePaths) {
    this.boardVariants.put(1, BoardFactory.createBoard("Classic"));
    this.boardVariants.put(2, BoardFactory.createBoard("Teleporting"));

    if (boardFilePaths.isEmpty()) {
      return;
    }

    try {
      for (String boardFilePath : boardFilePaths) {
        int variantIndex = this.boardVariants.size() + 1;
        Board board = BoardFactory.createBoardFromFile(boardFilePath);
        this.boardVariants.put(variantIndex, board);
      }
    } catch (IOException | IllegalArgumentException e) {
      e.printStackTrace();
    }
  }

  /**
   * Sets the board variant to the one with the given index, by calling the appropriate method in
   * the {@link BoardGame} class.
   *
   * @see BoardGame#setBoard(Board)
   * @param variantIndex The index of the board variant in the {@link #boardVariants} map to set.
   */
  public void setBoardVariant(int variantIndex) {
    if (!this.boardVariants.containsKey(variantIndex)) {
      return;
    }

    this.boardGame.setBoard(this.boardVariants.get(variantIndex));
  }

  /**
   * Loads the players from the file at the given path, and sets them in the {@link #boardGame}
   * object. If an error occurs while reading the file, the method returns false,
   * and the game is not initialized.
   *
   * @see PlayerFactory#createPlayersFromFile(String)
   * @param filePath The path to the file containing the players.
   * @return True if the players were loaded successfully, false otherwise.
   */
  public boolean loadPlayersFromFile(String filePath) {
    try {
      setPlayers(PlayerFactory.createPlayersFromFile(filePath));
    } catch (IOException e) {
      return false;
    }
    initGame();
    return true;
  }

  /**
   * Sets the players in the {@link #boardGame} object to the given list of players. If the list
   * is null or empty, an {@link IllegalArgumentException} is thrown. After setting the players,
   * the game is initialized using the {@link #initGame()} method.
   *
   * @see BoardGame#setPlayers(List)
   * @param players The list of players to set.
   */
  public void setPlayers(List<Player> players) {
    if (players == null) {
      throw new IllegalArgumentException("List of players cannot be null");
    }
    this.boardGame.setPlayers(players);
    initGame();
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
