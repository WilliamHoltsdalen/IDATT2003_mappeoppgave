package edu.ntnu.idi.idatt.model.game;

import edu.ntnu.idi.idatt.model.board.Board;
import edu.ntnu.idi.idatt.model.board.LadderGameBoard;
import edu.ntnu.idi.idatt.model.player.LadderGamePlayer;
import edu.ntnu.idi.idatt.model.player.Player;
import edu.ntnu.idi.idatt.model.player.PlayerTokenType;
import edu.ntnu.idi.idatt.model.tile.LadderAction;
import edu.ntnu.idi.idatt.model.tile.LadderGameTile;
import edu.ntnu.idi.idatt.observer.BoardGameObserver;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import edu.ntnu.idi.idatt.controller.laddergame.LadderGameController;

class LadderBoardGameTest {
  
  private LadderBoardGame game;
  private Board mockBoard;
  private List<Player> players;
  private LadderGamePlayer player1;
  private LadderGamePlayer player2;
  
  private final int defaultDiceCount = 1;
  private final int boardSize = 100;
  
  // Mock Tiles
  private LadderGameTile tile0, tile10, tile20, tile95, tile100;
  
  @BeforeEach
  void setUp() {
    mockBoard = Mockito.mock(LadderGameBoard.class);
    when(mockBoard.getTileCount()).thenReturn(boardSize);
    
    // Setup mock tiles
    tile0 = Mockito.mock(LadderGameTile.class); when(tile0.getTileId()).thenReturn(0);
    tile10 = Mockito.mock(LadderGameTile.class); when(tile10.getTileId()).thenReturn(10);
    tile20 = Mockito.mock(LadderGameTile.class); when(tile20.getTileId()).thenReturn(20);
    tile95 = Mockito.mock(LadderGameTile.class); when(tile95.getTileId()).thenReturn(95);
    tile100 = Mockito.mock(LadderGameTile.class); when(tile100.getTileId()).thenReturn(boardSize); // Winning tile
    
    when(mockBoard.getTile(0)).thenReturn(tile0);
    when(mockBoard.getTile(10)).thenReturn(tile10);
    when(mockBoard.getTile(20)).thenReturn(tile20);
    when(mockBoard.getTile(95)).thenReturn(tile95);
    when(mockBoard.getTile(boardSize)).thenReturn(tile100);
    // For overshoot calculation: e.g., if on 95, rolls 10 (to 105), overshoot is 5, lands on 100-5=95
    // if boardSize = 100, tileCount = 100. current = 95, roll = 10 -> next = 105. overshoot = 105 - 100 = 5. target = 100 - 5 = 95
    when(mockBoard.getTile(boardSize - ( (95+10) - boardSize ) )).thenReturn(tile95); // 100 - (105-100) = 95
    
    // Define tile96 and its direct mock first
    LadderGameTile tile96 = Mockito.mock(LadderGameTile.class); 
    when(tile96.getTileId()).thenReturn(96);
    when(mockBoard.getTile(96)).thenReturn(tile96);
    
    // Now use tile96 in the overshoot calculation that should result in tile 96
    // The expression (boardSize - ( (98+6) - boardSize )) calculates to 96
    when(mockBoard.getTile(boardSize - ( (98+6) - boardSize ) )).thenReturn(tile96); 
    
    player1 = new LadderGamePlayer("Player1", "#FF0000", PlayerTokenType.CIRCLE, false);
    player2 = new LadderGamePlayer("Player2", "#0000FF", PlayerTokenType.SQUARE, false);
    players = new ArrayList<>(Arrays.asList(player1, player2));
    
    game = new LadderBoardGame(mockBoard, players, defaultDiceCount);
  }
  
  @Nested
  @DisplayName("Initialization")
  class InitializationTests {
    @Test
    @DisplayName("Test initializeGame places players on tile 0 and sets current player")
    void testInitializeGame() {
      // initializeGame is called by constructor
      assertEquals(tile0, player1.getCurrentTile(), "Player 1 should start on tile 0");
      assertEquals(tile0, player2.getCurrentTile(), "Player 2 should start on tile 0");
      assertEquals(player1, game.getCurrentPlayer(), "Player 1 should be the current player");
      assertEquals(1, game.getRoundNumber());
    }
    
    @Test
    @DisplayName("Test constructor sets up board, players, and dice")
    void testConstructor() {
      assertSame(mockBoard, game.getBoard());
      assertEquals(players, game.getPlayers());
      assertEquals(defaultDiceCount, game.getDice().getNumberOfDice());
      assertEquals(1, game.getRoundNumber());
    }
  }
  
  @Nested
  @DisplayName("Game Flow and Player Turns")
  class GameFlowTests {
    
    @Test
    @DisplayName("Test performPlayerTurn updates player position and current player")
    void testPerformPlayerTurn() {
      int diceRoll = 10;
      
      // Player 1 is current, starts at tile 0
      game.performPlayerTurn(diceRoll);
      assertEquals(tile10, player1.getCurrentTile(), "Player 1 should move to tile 10");
      assertEquals(player2, game.getCurrentPlayer(), "Current player should be Player2 (Player 2)");
      assertEquals(1, game.getRoundNumber(), "Round number should still be 1");
      
      // Player 2is current, starts at tile 0
      player2.placeOnTile(tile0);
      int diceRollPlayer2 = 20;
      game.performPlayerTurn(diceRollPlayer2);
      assertEquals(tile20, player2.getCurrentTile(), "Player 2 should move to tile 20");
      assertEquals(player1, game.getCurrentPlayer(), "Current player should be Player1 (Player 1) again");
      assertEquals(2, game.getRoundNumber(), "Round number should increment to 2");
    }
    
    @Test
    @DisplayName("Test findNextTile logic - normal move")
    void testFindNextTileNormal() {
      player1.placeOnTile(tile10); // Current tile ID is 10
      game.setCurrentPlayer(player1);
      
      LadderGameTile tile15 = Mockito.mock(LadderGameTile.class); when(tile15.getTileId()).thenReturn(15);
      when(mockBoard.getTile(15)).thenReturn(tile15);
      
      game.movePlayer(5); // Calls findNextTile(player1, 5)
      assertEquals(tile15, player1.getCurrentTile());
    }
    
    @Test
    @DisplayName("Test findNextTile logic - exact win")
    void testFindNextTileExactWin() {
      player1.placeOnTile(tile95); // Current tile ID is 95
      game.setCurrentPlayer(player1);
      // Player on tile 95, board size 100. Rolls 5, lands on 100.
      
      game.movePlayer(5); // Calls findNextTile(player1, 5)
      assertEquals(tile100, player1.getCurrentTile());
    }
    
    @Test
    @DisplayName("Test findNextTile logic - overshoot")
    void testFindNextTileOvershoot() {
      player1.placeOnTile(tile95); // Current tile ID is 95
      game.setCurrentPlayer(player1);
      // Player on tile 95, board size 100. Rolls 10 (lands on 105 -> overshoot 5 -> target 100-5 = 95)
      // Mock for board.getTile(95) is already set up in @BeforeEach
      
      game.movePlayer(10);
      assertEquals(tile95, player1.getCurrentTile(), "Player should move back due to overshoot");
    }
    
    @Test
    @DisplayName("Test handleTileAction - performs action if present")
    void testHandleTileActionWithAction() {
      LadderAction ladder = new LadderAction("TestLadder", tile20.getTileId(), "Climb ladder");
      when(tile10.getLandAction()).thenReturn(ladder);
      player1.placeOnTile(tile10); // Player is on tile 10 which has a ladder to tile 20
      game.setCurrentPlayer(player1);
      
      game.handleTileAction(); // Should call perform on ladder
      assertEquals(tile20, player1.getCurrentTile(), "Player should move to tile 20 due to ladder");
    }
    
    @Test
    @DisplayName("Test handleTileAction - does nothing if no action")
    void testHandleTileActionNoAction() {
      when(tile10.getLandAction()).thenReturn(null); // Tile 10 has no action
      player1.placeOnTile(tile10);
      game.setCurrentPlayer(player1);
      
      game.handleTileAction();
      assertEquals(tile10, player1.getCurrentTile(), "Player should remain on tile 10 as there is no action");
    }
    
    @Test
    @DisplayName("Test notifyPlayerMoved notifies controller")
    void testNotifyPlayerMovedNotifiesController() {
      LadderGameController mockController = Mockito.mock(LadderGameController.class);
      game.addObserver(mockController);
      
      player1.placeOnTile(tile0);
      game.setCurrentPlayer(player1);
      int diceRoll = 5;
      LadderGameTile tile5 = Mockito.mock(LadderGameTile.class);
      when(tile5.getTileId()).thenReturn(5);
      when(mockBoard.getTile(5)).thenReturn(tile5);
      
      game.movePlayer(diceRoll);
      
      verify(mockController).onPlayerMoved(player1, diceRoll, tile5.getTileId());
    }
    
    @Test
    @DisplayName("Test notifyTileActionPerformed notifies controller")
    void testNotifyTileActionPerformedNotifiesController() {
      LadderGameController mockController = Mockito.mock(LadderGameController.class);
      game.addObserver(mockController);
      
      LadderAction mockAction = Mockito.mock(LadderAction.class);
      when(mockAction.getDescription()).thenReturn("Test Action");
      when(tile10.getLandAction()).thenReturn(mockAction);
      player1.placeOnTile(tile10);
      game.setCurrentPlayer(player1);
      
      game.handleTileAction(); // This calls notifyTileActionPerformed
      
      verify(mockController).onTileActionPerformed(player1, mockAction);
    }
  }
  
  @Nested
  @DisplayName("Win Condition")
  class WinConditionTests {
    @Test
    @DisplayName("Test getWinner returns null when no winner")
    void testGetWinnerNoWinner() {
      player1.placeOnTile(tile10);
      player2.placeOnTile(tile20);
      assertNull(game.getWinner());
    }
    
    @Test
    @DisplayName("Test getWinner returns winner when player is on last tile")
    void testGetWinnerPlayer1Wins() {
      player1.placeOnTile(tile100); // Player 1 on winning tile
      player2.placeOnTile(tile20);
      assertEquals(player1, game.getWinner());
    }
    
    @Test
    @DisplayName("Test checkWinCondition notifies observers on win")
    void testCheckWinConditionNotifies() {
      LadderGameController mockObserver = Mockito.mock(LadderGameController.class);
      game.addObserver(mockObserver);
      
      player1.placeOnTile(tile100); // Player 1 wins
      game.setCurrentPlayer(player1); // Ensure currentPlayer is set to trigger win check path properly
      
      player1.placeOnTile(tile95); // Setup P1 near win
      game.performPlayerTurn(5); // P1 rolls 5, lands on 100, wins.
      
      verify(mockObserver).onGameFinished(player1);
    }
  }
  
  @Nested
  @DisplayName("Round Handling")
  class RoundHandlingTests {
    @Test
    @DisplayName("Test handleRoundNumber increments round and notifies when current player is first player")
    void testHandleRoundNumberIncrement() {
      BoardGameObserver mockObserver = Mockito.mock(BoardGameObserver.class);
      game.addObserver(mockObserver);
      
      game.setCurrentPlayer(player2); // Set to player2 initially
      game.handleRoundNumber(); // Should not increment
      assertEquals(1, game.getRoundNumber());
      verify(mockObserver, never()).onRoundNumberIncremented(anyInt());
      
      game.setCurrentPlayer(player1); // Set to player1 (first player)
      game.handleRoundNumber(); // Should increment
      assertEquals(2, game.getRoundNumber());
      verify(mockObserver).onRoundNumberIncremented(2);
    }
  }
} 