package edu.ntnu.idi.idatt.model.player;

import edu.ntnu.idi.idatt.model.tile.LadderGameTile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LadderGamePlayerTest {
  
  private LadderGamePlayer player;
  private final String defaultName = "TestPlayer";
  private final String defaultColorHex = "#FF0000";
  private final PlayerTokenType defaultTokenType = PlayerTokenType.CIRCLE;
  private final boolean defaultIsBot = false;
  
  private LadderGameTile mockTile1;
  private LadderGameTile mockTile2;
  
  @BeforeEach
  void setUp() {
    player = new LadderGamePlayer(defaultName, defaultColorHex, defaultTokenType, defaultIsBot);
    
    mockTile1 = new LadderGameTile(1, new int[]{0,0}, 2);
    mockTile2 = new LadderGameTile(2, new int[]{0,1}, 3);
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
    @DisplayName("Test constructor initializes currentTile to null")
    void testConstructorInitialTileIsNull() {
      assertNull(player.getCurrentTile());
    }
  }
  
  @Nested
  @DisplayName("Getters and Setters for Inherited Properties")
  class InheritedPropertyTests {
    
    @Test
    @DisplayName("Test setName and getName")
    void testSetNameAndGetName() {
      String newName = "PlayerNewName";
      player.setName(newName);
      assertEquals(newName, player.getName());
    }
    
    @Test
    @DisplayName("Test setColorHex and getColorHex")
    void testSetColorHexAndGetColorHex() {
      String newColor = "#00FF00";
      player.setColorHex(newColor);
      assertEquals(newColor, player.getColorHex());
    }
    
    @Test
    @DisplayName("Test setPlayerTokenType and getPlayerTokenType")
    void testSetPlayerTokenTypeAndGetPlayerTokenType() {
      PlayerTokenType newType = PlayerTokenType.SQUARE;
      player.setPlayerTokenType(newType);
      assertEquals(newType, player.getPlayerTokenType());
    }
    
    @Test
    @DisplayName("Test setBot and isBot")
    void testSetBotAndIsBot() {
      player.setBot(true);
      assertTrue(player.isBot());
      player.setBot(false);
      assertFalse(player.isBot());
    }
  }
  
  @Nested
  @DisplayName("LadderGamePlayer Specific Logic")
  class LadderGamePlayerLogicTests {
    
    @Test
    @DisplayName("Test placeOnTile sets currentTile correctly")
    void testPlaceOnTile() {
      player.placeOnTile(mockTile1);
      assertEquals(mockTile1, player.getCurrentTile());
      assertSame(mockTile1, player.getCurrentTile(), "Should be the same tile instance");
      
      player.placeOnTile(mockTile2);
      assertEquals(mockTile2, player.getCurrentTile());
      assertSame(mockTile2, player.getCurrentTile());
    }
    
    @Test
    @DisplayName("Test getCurrentTile returns the correct tile")
    void testGetCurrentTile() {
      assertNull(player.getCurrentTile(), "Initially current tile should be null");
      player.placeOnTile(mockTile1);
      assertEquals(mockTile1, player.getCurrentTile());
    }
  }
  
  @Nested
  @DisplayName("Negative Tests")
  class NegativeTests {
    
    @Test
    @DisplayName("Test setName with null or empty string")
    void testSetNameInvalid() {
      assertThrows(IllegalArgumentException.class, () -> player.setName(null));
      assertThrows(IllegalArgumentException.class, () -> player.setName(""));
    }
    
    @Test
    @DisplayName("Test setColorHex with invalid hex strings")
    void testSetColorHexInvalid() {
      assertThrows(IllegalArgumentException.class, () -> player.setColorHex(null));
      assertThrows(IllegalArgumentException.class, () -> player.setColorHex(""));
      assertThrows(IllegalArgumentException.class, () -> player.setColorHex("invalidColor")); // Not a hex
      assertThrows(IllegalArgumentException.class, () -> player.setColorHex("#12345")); // Too short
      assertThrows(IllegalArgumentException.class, () -> player.setColorHex("#1234567")); // Too long
      assertThrows(IllegalArgumentException.class, () -> player.setColorHex("#GGHHII")); // Invalid hex chars
    }
    
    @Test
    @DisplayName("Test setPlayerTokenType with null")
    void testSetPlayerTokenTypeNull() {
      assertThrows(IllegalArgumentException.class, () -> player.setPlayerTokenType(null));
    }
    
    @Test
    @DisplayName("Test placeOnTile with null tile")
    void testPlaceOnTileNull() {
      assertThrows(IllegalArgumentException.class, () -> player.placeOnTile(null));
    }
  }
} 