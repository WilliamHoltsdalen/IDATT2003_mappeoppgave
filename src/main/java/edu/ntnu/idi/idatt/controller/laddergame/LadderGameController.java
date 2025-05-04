package edu.ntnu.idi.idatt.controller.laddergame;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import edu.ntnu.idi.idatt.model.board.Board;
import edu.ntnu.idi.idatt.model.game.BoardGame;
import edu.ntnu.idi.idatt.model.game.LadderBoardGame;
import edu.ntnu.idi.idatt.model.player.Player;
import edu.ntnu.idi.idatt.model.tile.TileAction;
import edu.ntnu.idi.idatt.observer.BoardGameObserver;
import edu.ntnu.idi.idatt.observer.ButtonClickObserver;
import edu.ntnu.idi.idatt.view.laddergame.LadderGameView;

public class LadderGameController implements ButtonClickObserver, BoardGameObserver {
  private final LadderGameView ladderGameView;

  private BoardGame boardGame;
  private Runnable onQuitGame;

  public LadderGameController(LadderGameView ladderGameView, Board board, List<Player> players) {
    this.ladderGameView = ladderGameView;

    initializeBoardGame(board, players);
    initializeLadderGameView();
  }

  /**
   * Creates a new BoardGame instance with the given board and list of players. Finally, it
   * initializes the game by placing all players on the 0th tile and setting the current player to
   * the first player in the list.
   *
   * @param board the board to use in the game
   * @param players the list of players to use in the game
   */
  public void initializeBoardGame(Board board, List<Player> players) {
    try {
      boardGame = new LadderBoardGame(board, players, 2);
      boardGame.addObserver(this);
    } catch (IllegalArgumentException e) {
      e.printStackTrace();
    }
  }
  
  /**
   * Initializes the ladder game view. 
   */
  private void initializeLadderGameView() {
    ladderGameView.initialize(getPlayers(), getRoundNumber(), getBoard());
  }

  /**
   * Returns the round number of the game.
   *
   * @return The round number of the game.
   */
  private int getRoundNumber() {
    return boardGame.getRoundNumber();
  }

  /**
   * Returns the players of the game.
   *
   * @return The players of the game.
   */
  private List<Player> getPlayers() {
    return boardGame.getPlayers();
  }

  /**
   * Returns the board object of the game.
   *
   * @return The board of the game.
   */
  private Board getBoard() {
    return boardGame.getBoard();
  }

  private void handleRollDiceButtonAction() {
    if (ladderGameView.getGameMenuBox().getRollForAllPlayersSelected()) {
      performPlayerTurnForAllPlayers();
      return;
    }
    performPlayerTurn();
  }

  /**
   * Performs a player move by calling the player turn method in the board game.
   */
  private void performPlayerTurn() {
    boardGame.performPlayerTurn();
  }

  /**
   * Performs a player move for all players by calling the player turn method in the board game for
   * all players.
   */
  private void performPlayerTurnForAllPlayers() {
    do {
      boardGame.performPlayerTurn();
    } while (!boardGame.getCurrentPlayer().equals(boardGame.getPlayers().getFirst()));
  }

  /**
   * Restarts the game by initializing a new BoardGame instance with the same board and players.
   */
  public void restartGame() {
    List<Player> players = new ArrayList<>();
    boardGame.getPlayers().forEach(player -> players.add(new Player(player.getName(),
        player.getColorHex(), player.getPlayerTokenType())));

    initializeBoardGame(boardGame.getBoard(), players);
    initializeLadderGameView();
  }

  /**
   * Quits the game by running the on quit game event.
   */
  private void quitGame() {
    if (onQuitGame != null) {
      onQuitGame.run();
    }
  }

  /**
   * Sets the on quit game event.
   *
   * @param onQuitGame the on quit game event
   */
  public void setOnQuitGame(Runnable onQuitGame) {
    this.onQuitGame = onQuitGame;
  }

  /**
   * Sets the tile number of the player in the players box.
   *
   * @param player the player
   * @param newTileId the new tile id
   */
  private void setPlayerTileNumber(Player player, int newTileId) {
    ladderGameView.getPlayersBox().getPlayerRows().get(getPlayers().indexOf(player))
        .setTileNumber(player, newTileId);
  }

  /**
   * Handles the player moved event.
   *
   * @param player the player
   * @param diceRoll the dice roll
   * @param newTileId the new tile id
   */
  @Override
  public void onPlayerMoved(Player player, int diceRoll, int newTileId) {
    ladderGameView.getGameMenuBox().addGameLogRoundBoxEntry(player.getName() + " rolled " + diceRoll + " and moved to tile " + newTileId);

    setPlayerTileNumber(player, newTileId);

    ladderGameView.getBoardStackPane().movePlayer(player, getBoard().getTile(newTileId), false);
  }

  /**
   * Handles the round number incremented event.
   *
   * @param roundNumber the round number
   */
  @Override
  public void onRoundNumberIncremented(int roundNumber) {
    ladderGameView.getPlayersBox().setRoundNumber(roundNumber);

    ladderGameView.getGameMenuBox().addGameLogRoundBox(roundNumber);
  }

  /**
   * Handles the current player changed event.
   *
   * @param player the player
   */
  @Override
  public void onCurrentPlayerChanged(Player player) {
    if (!ladderGameView.getGameMenuBox().getRollForAllPlayersSelected()) {
      ladderGameView.getPlayersBox().setFocusedPlayer(getPlayers().indexOf(player));
      return;
    }
    ladderGameView.getPlayersBox().removeFocusedPlayer();

  }

  /**
   * Handles the tile action performed event.
   *
   * @param player the player
   * @param tileAction the tile action
   */
  @Override
  public void onTileActionPerformed(Player player, TileAction tileAction) {
    ladderGameView.getGameMenuBox().addGameLogRoundBoxEntry(player.getName() + " activated " + tileAction.getDescription());
    setPlayerTileNumber(player, tileAction.getDestinationTileId());

    ladderGameView.getBoardStackPane().movePlayer(player, getBoard().getTile(tileAction.getDestinationTileId()), true);
  }

  /**
   * Handles the game finished event.
   *
   * @param winner the winner
   */
  @Override
  public void onGameFinished(Player winner) {
    ladderGameView.getGameMenuBox().addGameLogRoundBoxEntry("Game finished! Winner: " + winner.getName());
  }

  /**
   * Handles button click events.
   *
   * @param buttonId the button id for the clicked button.
   */
  @Override
  public void onButtonClicked(String buttonId) {
    switch (buttonId) {
      case "roll_dice" -> handleRollDiceButtonAction();
      case "restart_game" -> restartGame();
      case "quit_game" -> quitGame();
      default -> {
        break;
      }
    }
  }

  /**
   * Handles button click events with parameters.
   *
   * @param buttonId the button id for the clicked button.
   * @param params the map of parameters for the clicked button.
   */
  @Override
  public void onButtonClickedWithParams(String buttonId, Map<String, Object> params) {
    // Not needed
  }
}
