package edu.ntnu.idi.idatt.model.player;

import edu.ntnu.idi.idatt.model.tile.LudoTile;
import edu.ntnu.idi.idatt.model.tile.Tile;
import edu.ntnu.idi.idatt.model.token.LudoToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LudoPlayerTest {
  
  private LudoPlayer player;
  private final String defaultName = "LudoTestPlayer";
  private final String defaultColorHex = "#0000FF";
  private final PlayerTokenType defaultTokenType = PlayerTokenType.CIRCLE;
  private final boolean defaultIsBot = true;
  
  private Tile mockTile1;
  private Tile mockTile2;
  
  @BeforeEach
  void setUp() {
    player = new LudoPlayer(defaultName, defaultColorHex, defaultTokenType, defaultIsBot);
    
    mockTile1 = new LudoTile(1, new int[]{0,0}, 2, "start-1"); 
    mockTile2 = new LudoTile(10, new int[]{1,2}, 11, "track");
  }
  
  @Nested
  @DisplayName("Constructor and Initial State")
  class ConstructorTests {
    
    @Test
    @DisplayName("Test constructor sets all inherited fields correctly")
    void testConstructorSetsInheritedFields() {
      assertEquals(defaultName, player.getName());
      assertEquals(defaultColorHex, player.getColorHex());
      assertEquals(defaultTokenType, player.getPlayerTokenType());
      assertEquals(defaultIsBot, player.isBot());
    }
    
    @Test
    @DisplayName("Test constructor initializes four LudoTokens")
    void testConstructorInitializesTokens() {
      List<LudoToken> tokens = player.getTokens();
      assertNotNull(tokens);
      assertEquals(4, tokens.size());
      for (int i = 0; i < 4; i++) {
        LudoToken token = tokens.get(i);
        assertNotNull(token);
        assertEquals(i + 1, token.getTokenId());
        assertEquals(LudoToken.TokenStatus.NOT_RELEASED, token.getStatus());
        assertNull(token.getCurrentTile());
      }
    }
  }
  
  @Nested
  @DisplayName("Getters for LudoPlayer Specific Properties")
  class LudoPlayerGetterTests {
    
    @Test
    @DisplayName("Test getTokens returns the list of tokens")
    void testGetTokens() {
      List<LudoToken> tokens = player.getTokens();
      assertNotNull(tokens);
      assertEquals(4, tokens.size());
    }
    
    @Test
    @DisplayName("Test getToken with valid ID returns correct token")
    void testGetTokenValidId() {
      LudoToken token1 = player.getToken(1);
      assertNotNull(token1);
      assertEquals(1, token1.getTokenId());
      
      LudoToken token4 = player.getToken(4);
      assertNotNull(token4);
      assertEquals(4, token4.getTokenId());
    }
  }
  
  @Nested
  @DisplayName("LudoPlayer Specific Logic")
  class LudoPlayerLogicTests {
    
    @Test
    @DisplayName("Test moveToken updates token's tile and status")
    void testMoveToken() {
      LudoToken token = player.getToken(1);
      assertNull(token.getCurrentTile());
      assertEquals(LudoToken.TokenStatus.NOT_RELEASED, token.getStatus());
      
      player.moveToken(1, mockTile1, LudoToken.TokenStatus.RELEASED);
      assertEquals(mockTile1, token.getCurrentTile());
      assertSame(mockTile1, token.getCurrentTile());
      assertEquals(LudoToken.TokenStatus.RELEASED, token.getStatus());
      
      player.moveToken(1, mockTile2, LudoToken.TokenStatus.FINISHED);
      assertEquals(mockTile2, token.getCurrentTile());
      assertSame(mockTile2, token.getCurrentTile());
      assertEquals(LudoToken.TokenStatus.FINISHED, token.getStatus());
    }
  }
  
  @Nested
  @DisplayName("Negative Tests")
  class NegativeLudoPlayerTests {
    
    @Test
    @DisplayName("Test getToken with invalid ID throws IllegalArgumentException")
    void testGetTokenInvalidId() {
      assertThrows(IllegalArgumentException.class, () -> player.getToken(0));
      assertThrows(IllegalArgumentException.class, () -> player.getToken(5));
      assertThrows(IllegalArgumentException.class, () -> player.getToken(-1));
    }
    
    @Test
    @DisplayName("Test moveToken with invalid token ID throws IllegalArgumentException")
    void testMoveTokenInvalidId() {
      assertThrows(IllegalArgumentException.class, () -> 
      player.moveToken(5, mockTile1, LudoToken.TokenStatus.RELEASED)
      );
    }
    
    @Test
    @DisplayName("Test moveToken with null tile throws IllegalArgumentException")
    void testMoveTokenNullTile() {
      assertThrows(IllegalArgumentException.class, () -> 
      player.moveToken(1, null, LudoToken.TokenStatus.RELEASED)
      );
    }
    
    @Test
    @DisplayName("Test moveToken with null status throws IllegalArgumentException")
    void testMoveTokenNullStatus() {
      assertThrows(IllegalArgumentException.class, () -> 
      player.moveToken(1, mockTile1, null)
      );
    }
  }
  
  // Inherited negative tests (setName, setColorHex, setPlayerTokenType) are identical 
  // to LadderGamePlayerTest and are omitted here for that reason. 
} 