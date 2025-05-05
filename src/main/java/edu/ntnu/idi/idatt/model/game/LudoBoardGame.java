package edu.ntnu.idi.idatt.model.game;

import java.util.List;

import edu.ntnu.idi.idatt.controller.ludo.LudoGameController;
import edu.ntnu.idi.idatt.model.board.Board;
import edu.ntnu.idi.idatt.model.board.LudoGameBoard;
import edu.ntnu.idi.idatt.model.player.LudoPlayer;
import edu.ntnu.idi.idatt.model.player.Player;
import edu.ntnu.idi.idatt.model.tile.Tile;
import edu.ntnu.idi.idatt.model.tile.TileAction;
import edu.ntnu.idi.idatt.model.token.LudoToken;

public class LudoBoardGame extends BoardGame {

    
    public LudoBoardGame(Board board, List<Player> players, int diceCount) {
        super(board, players, diceCount);
    }

    @Override
    public void initializeGame() {
      logger.info("Game started!");
  
      players.forEach(player -> {
        ((LudoPlayer) player).getTokens().forEach(token -> {
          int startIndex = ((LudoGameBoard) board).getPlayerStartIndexes()[players.indexOf(player)];
          token.setCurrentTile(board.getTile(startIndex));
        });
      });
      setCurrentPlayer(players.getFirst());
    }

    @Override
    public Player getWinner() {
        for (Player player : players) {
            if (((LudoPlayer) player).getTokens().stream().anyMatch(token -> token.getCurrentTile().getTileId() == ((LudoGameBoard) board).getPlayerFinishIndexes()[players.indexOf(player)])) {
                return player;
            }
        }
        return null;
    }

    @Override
    protected void checkWinCondition() {
        if (getWinner() != null) {
            notifyGameFinished(getWinner());
        }
    }

    @Override
    protected void handleTileAction() {
        TileAction landAction = ((LudoPlayer) currentPlayer).getTokens().getFirst().getCurrentTile().getLandAction();
        if (landAction == null) {
            return;
        }
    }
    private boolean checkCurrentPlayerCanMove() {
        return ((LudoPlayer) currentPlayer).getTokens().stream().anyMatch(token -> token.getStatus() == LudoToken.TokenStatus.RELEASED);
    }

    public int rollDice() {
        dice.rollDice();
        logger.info("Dice rolled: {}", dice.getTotalValue());
        return dice.getTotalValue();
    }

    @Override
    public void performPlayerTurn() {
        int diceRoll = rollDice();
        if (checkCurrentPlayerCanMove()) {
            movePlayer(diceRoll);
        } else {
            tryToReleasePlayer(diceRoll);
        }
        handleTileAction();
        checkWinCondition();
        updateCurrentPlayer();
        handleRoundNumber();
    }

    private Tile findNextTile(LudoToken token, int diceRoll) {
        int nextTileId = token.getCurrentTile().getTileId();

        for (int i = 0; i < diceRoll; i++) {
            nextTileId = board.getTile(nextTileId).getNextTileId();
            if (board.getTile(nextTileId).getNextTileId() == ((LudoGameBoard) board).getPlayerTrackStartIndexes()[players.indexOf(currentPlayer)]) {
                nextTileId = ((LudoGameBoard) board).getPlayerFinishStartIndexes()[players.indexOf(currentPlayer)];
            }
            if (nextTileId == ((LudoGameBoard) board).getPlayerFinishIndexes()[players.indexOf(currentPlayer)]) {
                break; // If the player has reached the finish line, break the loop
            }
        }
        return board.getTile(nextTileId);
    }

    private void tryToReleasePlayer(int diceRoll) {
        if (diceRoll == 6) {
            int tileId = ((LudoGameBoard) board).getPlayerTrackStartIndexes()[players.indexOf(currentPlayer)];
            ((LudoPlayer) currentPlayer).getTokens().forEach(token -> {
                token.setCurrentTile(board.getTile(tileId));
            });
            notifyPlayerReleased(currentPlayer, tileId);
        }
    }

    public void movePlayer(int diceRoll) {
        ((LudoPlayer) currentPlayer).getTokens().forEach(token -> {
            Tile nextTile = findNextTile(token, diceRoll);
            token.setCurrentTile(nextTile);
        });
        notifyPlayerMoved(currentPlayer, diceRoll, ((LudoPlayer) currentPlayer).getTokens().getFirst().getCurrentTile().getTileId());
    }

    private void notifyPlayerReleased(Player player, int tileId) {
        observers.forEach(observer -> ((LudoGameController) observer).onPlayerReleased(player, tileId));
    }
}
