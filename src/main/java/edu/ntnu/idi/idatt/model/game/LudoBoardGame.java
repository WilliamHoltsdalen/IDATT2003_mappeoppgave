package edu.ntnu.idi.idatt.model.game;

import java.util.List;

import edu.ntnu.idi.idatt.controller.ludo.LudoGameController;
import edu.ntnu.idi.idatt.model.board.Board;
import edu.ntnu.idi.idatt.model.board.LudoGameBoard;
import edu.ntnu.idi.idatt.model.player.LudoPlayer;
import edu.ntnu.idi.idatt.model.player.Player;
import edu.ntnu.idi.idatt.model.tile.Tile;
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
            if (((LudoPlayer) player).getTokens().stream().allMatch(token -> token.getStatus() == LudoToken.TokenStatus.FINISHED)) {
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
    
    private boolean checkCurrentPlayerCanMove() {
        return ((LudoPlayer) currentPlayer).getTokens().stream().anyMatch(token -> token.getStatus() == LudoToken.TokenStatus.RELEASED);
    }

    private void checkTokenFinished() {
        ((LudoPlayer) currentPlayer).getTokens().stream().filter(token -> token.getStatus() == LudoToken.TokenStatus.RELEASED).forEach(token -> {
            if (token.getCurrentTile().getTileId() == ((LudoGameBoard) board).getPlayerFinishIndexes()[players.indexOf(currentPlayer)]) {
                token.setStatus(LudoToken.TokenStatus.FINISHED);
                notifyTokenFinished(currentPlayer, token);
            }
        });
    }
    
    public int rollDice() {
        dice.rollDice();
        logger.info("Dice rolled: {}", dice.getTotalValue());
        return dice.getTotalValue();
    }
    
    public void performPlayerTurn(int diceRoll) {
        if (checkCurrentPlayerCanMove()) {
            moveToken(diceRoll);
            checkTokenFinished();
        } else if (diceRoll == 6) {
            releaseToken();
        } else {
            notifyTurnSkipped(currentPlayer, diceRoll);
        }
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
    
    private void releaseToken() {
        int tileId = ((LudoGameBoard) board).getPlayerTrackStartIndexes()[players.indexOf(currentPlayer)];
        LudoToken token = ((LudoPlayer) currentPlayer).getTokens().stream().filter(t -> t.getStatus() == LudoToken.TokenStatus.NOT_RELEASED).findFirst().orElse(null);
        if (token == null) {
            return;
        }
        token.setCurrentTile(board.getTile(tileId));
        token.setStatus(LudoToken.TokenStatus.RELEASED);
        notifyTokenReleased(currentPlayer, tileId, ((LudoPlayer) currentPlayer).getTokens().indexOf(token));
        // Check for and handle token captures
        handleTokenCapture(board.getTile(tileId));
    }
    
    public void moveToken(int diceRoll) {
        LudoToken token = ((LudoPlayer) currentPlayer).getTokens().stream().filter(t -> t.getStatus() == LudoToken.TokenStatus.RELEASED).findFirst().orElse(null);
        if (token == null) {
            return;
        }
        int oldTileId = token.getCurrentTile().getTileId();
        Tile nextTile = findNextTile(token, diceRoll);
        token.setCurrentTile(nextTile);
        notifyTokenMoved(currentPlayer, token, diceRoll, oldTileId, nextTile.getTileId());

        // Check for and handle token captures
        handleTokenCapture(nextTile);
    }
    
    private void handleTokenCapture(Tile destinationTile) {
        // Check all players except current player for tokens on the destination tile
        for (Player player : players) {
            if (player == currentPlayer) continue; // Skip current player's tokens
            
            ((LudoPlayer) player).getTokens().stream()
                .filter(token -> token.getCurrentTile().getTileId() == destinationTile.getTileId())
                .forEach(token -> {
                    // Send token back to its starting position
                    int startIndex = ((LudoGameBoard) board).getPlayerStartIndexes()[players.indexOf(player)];
                    int oldTileId = token.getCurrentTile().getTileId();
                    token.setCurrentTile(board.getTile(startIndex));
                    token.setStatus(LudoToken.TokenStatus.NOT_RELEASED);
                    notifyTokenCaptured(player, token, oldTileId);
                });
        }
    }
    
    private void notifyTokenReleased(Player player, int tileId, int tokenId) {
        observers.forEach(observer -> ((LudoGameController) observer).onTokenReleased(player, tileId, tokenId));
    }

    private void notifyTokenMoved(Player player, LudoToken token, int diceRoll, int oldTileId, int newTileId) {
        observers.forEach(observer -> ((LudoGameController) observer).onTokenMoved(player, token, diceRoll, oldTileId, newTileId));
    }

    private void notifyTokenCaptured(Player player, LudoToken token, int oldTileId) {
        observers.forEach(observer -> ((LudoGameController) observer).onTokenCaptured(player, token, oldTileId));
    }

    private void notifyTokenFinished(Player player, LudoToken token) {
        observers.forEach(observer -> ((LudoGameController) observer).onTokenFinished(player, token));
    }

    private void notifyTurnSkipped(Player player, int diceRoll) {
        observers.forEach(observer -> ((LudoGameController) observer).onTurnSkipped(player, diceRoll));
    }
}
