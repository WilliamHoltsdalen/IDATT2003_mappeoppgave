package edu.ntnu.idi.idatt.controller.ludo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import edu.ntnu.idi.idatt.controller.common.GameController;
import edu.ntnu.idi.idatt.model.board.Board;
import edu.ntnu.idi.idatt.model.game.LudoBoardGame;
import edu.ntnu.idi.idatt.model.player.LudoPlayer;
import edu.ntnu.idi.idatt.model.player.Player;
import edu.ntnu.idi.idatt.model.tile.TileAction;
import edu.ntnu.idi.idatt.view.ludo.LudoGameView;

public class LudoGameController extends GameController {
    
    public LudoGameController(LudoGameView ludoGameView, Board board, List<Player> players) {
        super(ludoGameView, board, players);
    }
    
    @Override
    public void initializeBoardGame(Board board, List<Player> players) {
        try {
            boardGame = new LudoBoardGame(board, players, 1);
            boardGame.addObserver(this);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    protected void handleRollDiceButtonAction() {
        performPlayerTurn();
    }
    
    @Override
    protected void performPlayerTurn() {
        boardGame.performPlayerTurn();
    }
    
    /**
    * Sets the tile number of the player in the players box.
    *
    * @param player the player
    * @param newTileId the new tile id
    */
    private void setPlayerTileNumber(Player player, int newTileId) {
        gameView.getPlayersBox().getPlayerRows().get(getPlayers().indexOf(player))
        .setTileNumber(player, newTileId);
    }

    /**
     * Handles the player released event.
     *
     * @param player the player
     * @param tileId the tile id
     */
    public void onPlayerReleased(Player player, int tileId) {
        gameView.getGameMenuBox().addGameLogRoundBoxEntry(player.getName() + " released from tile " + tileId);
        setPlayerTileNumber(player, tileId);
        gameView.getGameStackPane().movePlayer(player, getBoard().getTile(tileId), true);
    }

    /**
    * Restarts the game by initializing a new BoardGame instance with the same board and players.
    */
    @Override
    protected void restartGame() {
        List<Player> players = new ArrayList<>();
        boardGame.getPlayers().forEach(player -> players.add(new LudoPlayer(player.getName(),
        player.getColorHex(), player.getPlayerTokenType())));
        
        initializeBoardGame(boardGame.getBoard(), players);
        initializeGameView();
    }
    
    @Override
    public void onPlayerMoved(Player player, int diceRoll, int newTileId) {
        gameView.getGameMenuBox().addGameLogRoundBoxEntry(player.getName() + " rolled " + diceRoll + " and moved to tile " + newTileId);
        
        setPlayerTileNumber(player, newTileId);

        gameView.getGameStackPane().movePlayer(player, getBoard().getTile(newTileId), false);
    }
    
    @Override
    public void onRoundNumberIncremented(int roundNumber) {
        gameView.getPlayersBox().setRoundNumber(roundNumber);
        
        gameView.getGameMenuBox().addGameLogRoundBox(roundNumber);
    }
    
    @Override
    public void onCurrentPlayerChanged(Player player) {
        gameView.getPlayersBox().setFocusedPlayer(getPlayers().indexOf(player));
    }

    @Override
    public void onTileActionPerformed(Player player, TileAction tileAction) {
        gameView.getGameMenuBox().addGameLogRoundBoxEntry(player.getName() + " activated " + tileAction.getDescription());
        setPlayerTileNumber(player, tileAction.getDestinationTileId());

        gameView.getGameStackPane().movePlayer(player, getBoard().getTile(tileAction.getDestinationTileId()), true);
    }

    @Override
    public void onGameFinished(Player winner) {
        gameView.getGameMenuBox().addGameLogRoundBoxEntry("Game finished! Winner: " + winner.getName());
    }

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

    @Override
    public void onButtonClickedWithParams(String buttonId, Map<String, Object> params) {
        // Not needed
    }
    
}
