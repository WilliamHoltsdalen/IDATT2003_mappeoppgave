package edu.ntnu.idi.idatt.model.game;

import edu.ntnu.idi.idatt.model.board.LudoGameBoard;
import edu.ntnu.idi.idatt.model.player.LudoPlayer;
import edu.ntnu.idi.idatt.model.player.Player;
import edu.ntnu.idi.idatt.model.player.PlayerTokenType;
import edu.ntnu.idi.idatt.model.tile.LudoTile;
import edu.ntnu.idi.idatt.model.token.LudoToken;
import edu.ntnu.idi.idatt.controller.ludo.LudoGameController;
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

class LudoBoardGameTest {
  
  private LudoBoardGame game;
  private LudoGameBoard mockBoard;
  private List<Player> playersList;
  private LudoPlayer player1, player2;
  // Dice is created by BoardGame, can be accessed via game.getDice()
  
  // Player specific indices from LudoGameBoard
  private final int p1StartTileId = 50; // Arbitrary ID for player 1 start area tile
  private final int p2StartTileId = 55; // Arbitrary ID for player 2 start area tile
  private final int p1TrackStartTileId = 1;
  private final int p2TrackStartTileId = 20; // Example
  private final int p1FinishTileId = 15; // Tile ID that means token is finished for P1
  private final int p2FinishTileId = 35; // Tile ID that means token is finished for P2
  
  private LudoTile mockP1StartTile, mockP2StartTile, mockP1TrackStartTile, mockP1NormalTrackTile, mockP1FinishTile;
  private LudoToken p1Token1, p2Token1;
  
  @BeforeEach
  void setUp() {
    mockBoard = Mockito.mock(LudoGameBoard.class);
    
    player1 = new LudoPlayer("Alice", "#FF0000", PlayerTokenType.CIRCLE, false);
    player2 = new LudoPlayer("Bob", "#0000FF", PlayerTokenType.SQUARE, false);
    playersList = new ArrayList<>(Arrays.asList(player1, player2));
    
    // Mock LudoGameBoard methods
    when(mockBoard.getPlayerStartIndexes()).thenReturn(new int[]{p1StartTileId, p2StartTileId, 0, 0}); // Only 2 players
    when(mockBoard.getPlayerTrackStartIndexes()).thenReturn(new int[]{p1TrackStartTileId, p2TrackStartTileId, 0, 0});
    when(mockBoard.getPlayerFinishIndexes()).thenReturn(new int[]{p1FinishTileId, p2FinishTileId, 0, 0});
    
    // Mock Tiles
    mockP1StartTile = Mockito.mock(LudoTile.class); when(mockP1StartTile.getTileId()).thenReturn(p1StartTileId);
    mockP2StartTile = Mockito.mock(LudoTile.class); when(mockP2StartTile.getTileId()).thenReturn(p2StartTileId);
    mockP1TrackStartTile = Mockito.mock(LudoTile.class); when(mockP1TrackStartTile.getTileId()).thenReturn(p1TrackStartTileId);
    mockP1NormalTrackTile = Mockito.mock(LudoTile.class); when(mockP1NormalTrackTile.getTileId()).thenReturn(p1TrackStartTileId + 1);
    mockP1FinishTile = Mockito.mock(LudoTile.class); when(mockP1FinishTile.getTileId()).thenReturn(p1FinishTileId);
    
    when(mockBoard.getTile(p1StartTileId)).thenReturn(mockP1StartTile);
    when(mockBoard.getTile(p2StartTileId)).thenReturn(mockP2StartTile);
    when(mockBoard.getTile(p1TrackStartTileId)).thenReturn(mockP1TrackStartTile);
    when(mockBoard.getTile(p1TrackStartTileId + 1)).thenReturn(mockP1NormalTrackTile);
    when(mockBoard.getTile(p1FinishTileId)).thenReturn(mockP1FinishTile);
    
    // Initialize tokens for direct manipulation in tests
    p1Token1 = player1.getToken(1);
    for(int i=1; i<=4; i++) player1.getToken(i).setCurrentTile(mockP1StartTile);
    
    p2Token1 = player2.getToken(1);
    for(int i=1; i<=4; i++) player2.getToken(i).setCurrentTile(mockP2StartTile);
    
    game = new LudoBoardGame(mockBoard, playersList, 1); // 1 die
  }
  
  @Nested
  @DisplayName("Initialization")
  class InitializationTests {
    @Test
    @DisplayName("Test initializeGame places tokens on their start tiles")
    void testInitializeGame() {
      assertEquals(player1, game.getCurrentPlayer());
      for (LudoToken token : player1.getTokens()) {
        assertEquals(mockP1StartTile, token.getCurrentTile(), "P1 Token should be on P1 start tile");
        assertEquals(LudoToken.TokenStatus.NOT_RELEASED, token.getStatus(), "Token should be NOT_RELEASED initially by LudoToken constructor");
      }
      for (LudoToken token : player2.getTokens()) {
        assertEquals(mockP2StartTile, token.getCurrentTile(), "P2 Token should be on P2 start tile");
      }
    }
  }
  
  @Nested
  @DisplayName("Token Release")
  class TokenReleaseTests {
    @Test
    @DisplayName("Test performPlayerTurn with dice roll 6 releases a token if none are out")
    void testReleaseTokenOnRoll6() {
      game.setCurrentPlayer(player1);
      // All player1 tokens are NOT_RELEASED and on mockP1StartTile
      
      game.performPlayerTurn(6); // Roll 6
      
      LudoToken releasedToken = player1.getTokens().stream()
      .filter(t -> t.getStatus() == LudoToken.TokenStatus.RELEASED)
      .findFirst().orElse(null);
      
      assertNotNull(releasedToken, "One token should be released");
      assertEquals(mockP1TrackStartTile, releasedToken.getCurrentTile(), "Token should move to track start tile");
      assertEquals(player2, game.getCurrentPlayer(), "Turn should pass to player 2");
    }
    
    @Test
    @DisplayName("Test performPlayerTurn with non-6 roll does not release token and skips turn")
    void testNoReleaseOnNon6RollAndSkips() {
      LudoGameController observer = Mockito.mock(LudoGameController.class);
      game.addObserver(observer);
      game.setCurrentPlayer(player1);
      
      game.performPlayerTurn(3); // Roll 3
      
      player1.getTokens().forEach(t -> assertEquals(LudoToken.TokenStatus.NOT_RELEASED, t.getStatus()));
      assertEquals(player2, game.getCurrentPlayer());
      verify(observer).onTurnSkipped(player1, 3);
    }
  }
  
  @Nested
  @DisplayName("Token Movement")
  class TokenMovementTests {
    @BeforeEach
    void setUpMovement() {
      game.setCurrentPlayer(player1);
      // Release P1 Token1 for movement tests
      p1Token1.setStatus(LudoToken.TokenStatus.RELEASED);
      p1Token1.setCurrentTile(mockP1TrackStartTile);
      // Ensure other tokens are not released to simplify findFirst logic for moveToken
      player1.getToken(2).setStatus(LudoToken.TokenStatus.NOT_RELEASED);
      player1.getToken(3).setStatus(LudoToken.TokenStatus.NOT_RELEASED);
      player1.getToken(4).setStatus(LudoToken.TokenStatus.NOT_RELEASED);
      
      int p1TrackStartTileActualId = mockP1TrackStartTile.getTileId();
      LudoTile p1TrackStartTileForStub = (LudoTile) mockBoard.getTile(p1TrackStartTileActualId);
      int p1NormalTrackTileIdToReturn = mockP1NormalTrackTile.getTileId();
      when(p1TrackStartTileForStub.getNextTileId()).thenReturn(p1NormalTrackTileIdToReturn);
      
      int p1NormalTrackTileActualId = mockP1NormalTrackTile.getTileId();
      LudoTile p1NormalTrackTileForStub = (LudoTile) mockBoard.getTile(p1NormalTrackTileActualId); 
      int nextTileIdAfterNormalToReturn = p1NormalTrackTileActualId + 1;
      when(p1NormalTrackTileForStub.getNextTileId()).thenReturn(nextTileIdAfterNormalToReturn);
      
      LudoTile nextTileAfterNormal = Mockito.mock(LudoTile.class); 
      int idForNextTileAfterNormal = mockP1NormalTrackTile.getTileId() + 1;
      when(nextTileAfterNormal.getTileId()).thenReturn(idForNextTileAfterNormal);
      when(mockBoard.getTile(idForNextTileAfterNormal)).thenReturn(nextTileAfterNormal);
    }
    
    @Test
    @DisplayName("Test performPlayerTurn moves a released token")
    void testMoveReleasedToken() {
      game.performPlayerTurn(1); // Roll 1
      assertEquals(mockP1NormalTrackTile, p1Token1.getCurrentTile(), "Token should move 1 step");
      assertEquals(player2, game.getCurrentPlayer());
    }
    
    @Test
    @DisplayName("Test findNextTile moves token correctly along path")
    void testFindNextTileLogic() {
      game.moveToken(1);
      assertEquals(mockP1NormalTrackTile, p1Token1.getCurrentTile());
    }
  }
  
  @Nested
  @DisplayName("Token Finishing")
  class TokenFinishingTests {
    @Test
    @DisplayName("Test token status becomes FINISHED upon reaching finish tile")
    void testTokenFinishes() {
      LudoGameController observer = Mockito.mock(LudoGameController.class);
      game.addObserver(observer);
      game.setCurrentPlayer(player1);
      p1Token1.setStatus(LudoToken.TokenStatus.RELEASED);
      
      LudoTile tileBeforeFinish = Mockito.mock(LudoTile.class);
      int tileBeforeFinishId = p1FinishTileId -1;
      when(tileBeforeFinish.getTileId()).thenReturn(tileBeforeFinishId);
      when(mockBoard.getTile(tileBeforeFinishId)).thenReturn(tileBeforeFinish);
      p1Token1.setCurrentTile(tileBeforeFinish);
      
      when(mockBoard.getTile(tileBeforeFinish.getTileId()).getNextTileId()).thenReturn(p1FinishTileId);
      
      game.performPlayerTurn(1); // Roll 1 and land on finish tile
      
      assertEquals(mockP1FinishTile, p1Token1.getCurrentTile());
      assertEquals(LudoToken.TokenStatus.FINISHED, p1Token1.getStatus());
      verify(observer).onTokenFinished(player1, p1Token1);
    }
  }
  
  @Nested
  @DisplayName("Win Condition")
  class WinConditionTests {
    @Test
    @DisplayName("Test getWinner returns player if all tokens are FINISHED")
    void testGetWinnerAllTokensFinished() {
      player1.getTokens().forEach(token -> token.setStatus(LudoToken.TokenStatus.FINISHED));
      player2.getTokens().forEach(token -> token.setStatus(LudoToken.TokenStatus.RELEASED)); // P2 not finished
      
      assertEquals(player1, game.getWinner());
    }
    
    @Test
    @DisplayName("Test getWinner returns null if no player has all tokens FINISHED")
    void testGetWinnerNotAllFinished() {
      player1.getToken(1).setStatus(LudoToken.TokenStatus.FINISHED);
      player1.getToken(2).setStatus(LudoToken.TokenStatus.RELEASED);
      assertNull(game.getWinner());
    }
  }
  
  @Nested
  @DisplayName("Token Capture")
  class TokenCaptureTests {
    private LudoTile sharedTile;
    
    @BeforeEach
    void setUpCapture() {
      sharedTile = Mockito.mock(LudoTile.class); 
      int sharedTileId = 10;
      when(sharedTile.getTileId()).thenReturn(sharedTileId);
      when(mockBoard.getTile(sharedTileId)).thenReturn(sharedTile);
      
      p1Token1.setStatus(LudoToken.TokenStatus.RELEASED);
      p1Token1.setCurrentTile(sharedTile);
      
      LudoTile p2TileBeforeShared = Mockito.mock(LudoTile.class);
      int p2TileBeforeSharedId = sharedTileId -1;
      when(p2TileBeforeShared.getTileId()).thenReturn(p2TileBeforeSharedId);
      when(mockBoard.getTile(p2TileBeforeSharedId)).thenReturn(p2TileBeforeShared);
      p2Token1.setStatus(LudoToken.TokenStatus.RELEASED);
      p2Token1.setCurrentTile(p2TileBeforeShared);
      
      game.setCurrentPlayer(player2);
      when(mockBoard.getTile(p2TileBeforeShared.getTileId()).getNextTileId()).thenReturn(sharedTileId);
    }
    
    @Test
    @DisplayName("Test landing on opponent token captures it")
    void testTokenCapture() {
      LudoGameController observer = Mockito.mock(LudoGameController.class);
      game.addObserver(observer);
      
      game.performPlayerTurn(1); // Player 2 rolls 1, lands on sharedTile, captures p1Token1
      
      assertEquals(LudoToken.TokenStatus.NOT_RELEASED, p1Token1.getStatus(), "P1's token should be captured (NOT_RELEASED)");
      assertEquals(mockP1StartTile, p1Token1.getCurrentTile(), "P1's token should return to its start tile");
      assertEquals(sharedTile, p2Token1.getCurrentTile(), "P2's token should be on the shared tile");
      verify(observer).onTokenCaptured(player1, p1Token1, sharedTile.getTileId());
    }
  }
} 