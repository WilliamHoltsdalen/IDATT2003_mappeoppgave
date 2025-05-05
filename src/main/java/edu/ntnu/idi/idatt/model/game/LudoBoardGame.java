package edu.ntnu.idi.idatt.model.game;

import java.util.List;

import edu.ntnu.idi.idatt.model.board.Board;
import edu.ntnu.idi.idatt.model.player.Player;
import edu.ntnu.idi.idatt.model.tile.Tile;

public class LudoBoardGame extends BoardGame {
    
    public LudoBoardGame(Board board, List<Player> players, int diceCount) {
        super(board, players, diceCount);
    }

    @Override
    public Player getWinner() {
        for (Player player : players) {
            if (player.getCurrentTile().getTileId() == board.getTileCount()) {
                return player;
            }
        }
        return null;
    }

    @Override
    public void performPlayerTurn() {
        // TODO: Replace this with Ludo rules, currently ladder game rules for testing
        rollDiceAndMovePlayer();
        handleTileAction();
        checkWinCondition();
        updateCurrentPlayer();
        handleRoundNumber();
    }

    private Tile findNextTile(Player player, int diceRoll) {
        int currentTileId = player.getCurrentTile().getTileId();
        int nextTileId = currentTileId + diceRoll;
        int tileCount = board.getTileCount();

        // TODO: Replace this with Ludo rules (currently ladder game rules for testing)
        if (nextTileId <= tileCount) {
            return board.getTile(nextTileId);
        } else {
            return board.getTile(tileCount - (nextTileId - tileCount));
        }
    }

    public void rollDiceAndMovePlayer() {
        dice.rollDice();
        Tile nextTile = findNextTile(currentPlayer, dice.getTotalValue());
        currentPlayer.placeOnTile(nextTile);
        notifyPlayerMoved(currentPlayer, dice.getTotalValue(), nextTile.getTileId());
    }

}
