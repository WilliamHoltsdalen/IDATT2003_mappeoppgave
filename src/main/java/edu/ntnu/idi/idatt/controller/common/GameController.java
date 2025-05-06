package edu.ntnu.idi.idatt.controller.common;

import java.util.List;
import java.util.Map;

import edu.ntnu.idi.idatt.model.board.Board;
import edu.ntnu.idi.idatt.model.game.BoardGame;
import edu.ntnu.idi.idatt.model.player.Player;
import edu.ntnu.idi.idatt.observer.BoardGameObserver;
import edu.ntnu.idi.idatt.observer.ButtonClickObserver;
import edu.ntnu.idi.idatt.view.common.GameView;

public abstract class GameController implements ButtonClickObserver, BoardGameObserver  {
    protected final GameView gameView;
    
    protected BoardGame boardGame;
    protected Runnable onQuitGame;
    
    public GameController(GameView gameView, Board board, List<Player> players) {
        this.gameView = gameView;
        
        initializeBoardGame(board, players);
        initializeGameView();
    }
    
    public abstract void initializeBoardGame(Board board, List<Player> players);

    protected abstract void handleRollDiceButtonAction();

    protected abstract void performPlayerTurn();

    protected abstract void restartGame();
    
    public void initializeGameView() {
        gameView.initialize(getPlayers(), getRoundNumber(), (Board) getBoard());
    }
    
    /**
    * Returns the round number of the game.
    *
    * @return The round number of the game.
    */
    protected int getRoundNumber() {
        return boardGame.getRoundNumber();
    }
    
    /**
    * Returns the players of the game.
    *
    * @return The players of the game.
    */
    protected List<Player> getPlayers() {
        return boardGame.getPlayers();
    }
    
    /**
    * Returns the board object of the game.
    *
    * @return The board of the game.
    */
    protected Board getBoard() {
        return boardGame.getBoard();
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
    * Quits the game by running the on quit game event.
    */
    protected void quitGame() {
        if (onQuitGame != null) {
            onQuitGame.run();
        }
    }
    
    
    @Override
    public abstract void onButtonClicked(String buttonId);
    
    @Override
    public abstract void onButtonClickedWithParams(String buttonId, Map<String, Object> params);
    
    @Override
    public abstract void onRoundNumberIncremented(int roundNumber);
    
    @Override
    public abstract void onCurrentPlayerChanged(Player player);
    
    @Override
    public abstract void onGameFinished(Player winner);
    
    
    
}
