package edu.ntnu.idi.idatt.controller.laddergame;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import edu.ntnu.idi.idatt.controller.common.GameController;
import edu.ntnu.idi.idatt.model.board.Board;
import edu.ntnu.idi.idatt.model.game.LadderBoardGame;
import edu.ntnu.idi.idatt.model.player.LadderGamePlayer;
import edu.ntnu.idi.idatt.model.player.Player;
import edu.ntnu.idi.idatt.model.tile.TileAction;
import edu.ntnu.idi.idatt.view.laddergame.LadderGameStackPane;
import edu.ntnu.idi.idatt.view.laddergame.LadderGameView;

public class LadderGameController extends GameController {

  public LadderGameController(LadderGameView ladderGameView, Board board, List<Player> players) {
    super(ladderGameView, board, players);
  }

  /**
   * Creates a new BoardGame instance with the given board and list of players. Finally, it
   * initializes the game by placing all players on the 0th tile and setting the current player to
   * the first player in the list.
   *
   * @param board   the board to use in the game
   * @param players the list of players to use in the game
   */
  @Override
  public void initializeBoardGame(Board board, List<Player> players) {
    try {
      boardGame = new LadderBoardGame(board, players, 2);
      boardGame.addObserver(this);
      logger.info("Game initialized with {} players on board '{}'", players.size(), board.getName());
    } catch (IllegalArgumentException e) {
      logger.error("Error initializing game with board '{}'", board.getName(), e);
      e.printStackTrace();
    }
  }

  /**
   * Performs a player move by calling the player turn method in the board game.
   */
  @Override
  protected void performPlayerTurn() {
    disableRollDiceButton();
    int diceRoll = ((LadderBoardGame) boardGame).rollDice();
    int[] diceValues = ((LadderBoardGame) boardGame).getDice().getDiceList().stream()
        .mapToInt(die -> die.getValue())
        .toArray();

    Player currentPlayer = boardGame.getCurrentPlayer();
    logger.debug("{} rolled: {} -> sum: {}", currentPlayer.getName(), diceRoll, diceValues);

    gameView.getGameMenuBox().animateDiceRoll(diceValues, () -> {
      ((LadderBoardGame) boardGame).performPlayerTurn(diceRoll);
      enableRollDiceButton();
    });
  }

  /**
   * Performs a player move for all players by calling the player turn method in the board game for
   * all players.
   */
  @Override
  protected void performPlayerTurnForAllPlayers() {
    logger.debug("Performing all players turn");
    do {
      performPlayerTurn();
    } while (!boardGame.getCurrentPlayer().equals(boardGame.getPlayers().getFirst()));
  }

  /**
   * Sets the tile number of the player in the players box.
   *
   * @param player    the player
   * @param newTileId the new tile id
   */
  private void setPlayerTileNumber(Player player, int newTileId) {
    gameView.getPlayersBox().getPlayerRows().get(getPlayers().indexOf(player))
        .setTileNumber(player, String.valueOf(newTileId));
  }

  /**
   * Restarts the game by initializing a new BoardGame instance with the same board and players.
   */
  @Override
  protected void restartGame() {
    logger.info("Restarting game");
    List<Player> players = new ArrayList<>();
    boardGame.getPlayers().forEach(player -> players.add(new LadderGamePlayer(player.getName(),
        player.getColorHex(), player.getPlayerTokenType(), player.isBot())));

    initializeBoardGame(boardGame.getBoard(), players);
    initializeGameView();
  }

  /**
   * Handles the player moved event.
   *
   * @param player    the player
   * @param diceRoll  the dice roll
   * @param newTileId the new tile id
   */
  public void onPlayerMoved(Player player, int diceRoll, int newTileId) {
    gameView.getGameMenuBox().addGameLogRoundBoxEntry(
        player.getName() + " rolled " + diceRoll + " and moved to tile " + newTileId);

    logger.debug("{} rolled: {} and moved to tile: {}", player.getName(), diceRoll, newTileId);

    setPlayerTileNumber(player, newTileId);

    ((LadderGameStackPane) gameView.getGameStackPane()).movePlayer(player,
        getBoard().getTile(newTileId), false);
  }

  /**
   * Handles the round number incremented event.
   *
   * @param roundNumber the round number
   */
  @Override
  public void onRoundNumberIncremented(int roundNumber) {
    gameView.getPlayersBox().setRoundNumber(roundNumber);
    logger.debug("Round number incremented: {}", roundNumber);
    gameView.getGameMenuBox().addGameLogRoundBox(roundNumber);
  }

  /**
   * Handles the current player changed event.
   *
   * @param player the player
   */
  @Override
  public void onCurrentPlayerChanged(Player player) {
    logger.debug("Current player changed: {}", player.getName());
    if (!gameView.getGameMenuBox().getRollForAllPlayersSelected()) {
      gameView.getPlayersBox().setFocusedPlayer(getPlayers().indexOf(player));
      
      // If it's a bot's turn and "roll for all" is not selected, automatically perform the turn.
      if (player.isBot() && !gameView.getGameMenuBox().getRollForAllPlayersSelected()) {
        performPlayerTurn();
        logger.debug("Automatic turn for Bot-Player: {}", player.getName());
      }
      return;
    }
    gameView.getPlayersBox().removeFocusedPlayer();
  }

  /**
   * Handles the tile action performed event.
   *
   * @param player     the player
   * @param tileAction the tile action
   */
  public void onTileActionPerformed(Player player, TileAction tileAction) {
    gameView.getGameMenuBox()
        .addGameLogRoundBoxEntry(player.getName() + " activated " + tileAction.getDescription());
    logger.info("{} activated tile action: {}", player.getName(), tileAction.getDescription());
    setPlayerTileNumber(player, tileAction.getDestinationTileId());

    ((LadderGameStackPane) gameView.getGameStackPane()).movePlayer(player,
        getBoard().getTile(tileAction.getDestinationTileId()), true);
  }

  /**
   * Handles the game finished event.
   *
   * @param winner the winner
   */
  @Override
  public void onGameFinished(Player winner) {
    logger.info("Game finished. Game winner is: {}", winner.getName());
    gameView.getGameMenuBox().addGameLogRoundBoxEntry("Game finished! Winner: " + winner.getName());
  }

  /**
   * Handles button click events.
   *
   * @param buttonId the button id for the clicked button.
   */
  @Override
  public void onButtonClicked(String buttonId) {
    logger.debug("Button clicked: {}", buttonId);
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
   * @param params   the map of parameters for the clicked button.
   */
  @Override
  public void onButtonClickedWithParams(String buttonId, Map<String, Object> params) {
    // Not needed
  }
}
