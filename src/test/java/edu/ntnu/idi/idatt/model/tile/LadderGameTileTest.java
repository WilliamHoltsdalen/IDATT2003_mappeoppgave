package edu.ntnu.idi.idatt.model.tile;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LadderGameTileTest {
  
  private final int defaultTileId = 1;
  private final int[] defaultCoordinates = {0, 0};
  private final int defaultNextTileId = 2;
  private LadderGameTile tile;
  private LadderAction mockAction;
  
  @BeforeEach
  void setUp() {
    mockAction = new LadderAction("TestAction", 10, "Move to 10");
    tile = new LadderGameTile(defaultTileId, defaultCoordinates, defaultNextTileId, mockAction);
  }
  
  @Nested
  @DisplayName("Constructor Tests")
  class ConstructorTests {
    
    @Test
    @DisplayName("Test constructor without TileAction sets inherited fields and null action")
    void testConstructorWithoutAction() {
      tile = new LadderGameTile(defaultTileId, defaultCoordinates, defaultNextTileId);
      assertEquals(defaultTileId, tile.getTileId());
      assertArrayEquals(defaultCoordinates, tile.getCoordinates());
      assertEquals(defaultNextTileId, tile.getNextTileId());
      assertNull(tile.getLandAction(), "LandAction should be null when not provided in constructor");
    }
    
    @Test
    @DisplayName("Test constructor with TileAction sets all fields correctly")
    void testConstructorWithAction() {
      tile = new LadderGameTile(defaultTileId, defaultCoordinates, defaultNextTileId, mockAction);
      assertEquals(defaultTileId, tile.getTileId());
      assertArrayEquals(defaultCoordinates, tile.getCoordinates());
      assertEquals(defaultNextTileId, tile.getNextTileId());
      assertSame(mockAction, tile.getLandAction(), "LandAction should be the one provided in constructor");
    }
  }
  
  @Nested
  @DisplayName("Getter and Setter for LandAction")
  class LandActionTests {
    
    @Test
    @DisplayName("Test setLandAction and getLandAction")
    void testSetAndGetLandAction() {
      tile = new LadderGameTile(defaultTileId, defaultCoordinates, defaultNextTileId);
      assertNull(tile.getLandAction(), "Initially LandAction should be null");
      
      tile.setLandAction(mockAction);
      assertSame(mockAction, tile.getLandAction(), "getLandAction should return the set action");
      
      LadderAction anotherAction = new LadderAction("AnotherAction", 5, "Move to 5");
      tile.setLandAction(anotherAction);
      assertSame(anotherAction, tile.getLandAction(), "getLandAction should return the newly set action");
    }
  }
  
  @Nested
  @DisplayName("Negative Tests")
  class NegativeTests {
    
    @Test
    @DisplayName("Test constructor with invalid tileId (e.g., negative)")
    void testConstructorInvalidTileId() {
      assertThrows(IllegalArgumentException.class, () -> 
      new LadderGameTile(-1, defaultCoordinates, defaultNextTileId)
      , "Constructor should throw for invalid tileId.");
    }
    
    @Test
    @DisplayName("Test constructor with null coordinates")
    void testConstructorNullCoordinates() {
      assertThrows(IllegalArgumentException.class, () -> 
      new LadderGameTile(defaultTileId, null, defaultNextTileId)
      , "Constructor should throw for null coordinates.");
    }
    
    @Test
    @DisplayName("Test constructor with coordinates array of incorrect length")
    void testConstructorInvalidCoordinatesLength() {
      assertThrows(IllegalArgumentException.class, () -> 
      new LadderGameTile(defaultTileId, new int[]{0}, defaultNextTileId)
      , "Constructor should throw for coordinates array not of length 2 (too short).");
      assertThrows(IllegalArgumentException.class, () -> 
      new LadderGameTile(defaultTileId, new int[]{0,1,2}, defaultNextTileId)
      , "Constructor should throw for coordinates array not of length 2 (too long).");
    }
    
    @Test
    @DisplayName("Test constructor with nextTileId same as tileId")
    void testConstructorNextTileIdSameAsTileId() {
      assertThrows(IllegalArgumentException.class, () -> 
      new LadderGameTile(defaultTileId, defaultCoordinates, defaultTileId)
      , "Constructor should throw if nextTileId is same as tileId.");
    }
    
    @Test
    @DisplayName("Test constructor with negative nextTileId")
    void testConstructorNegativeNextTileId() {
      assertThrows(IllegalArgumentException.class, () -> 
      new LadderGameTile(defaultTileId, defaultCoordinates, -1)
      , "Constructor should throw for negative nextTileId.");
    }
  }
  
  @Nested
  @DisplayName("Negative Tests - LadderGameTile Specific")
  class NegativeLadderGameTileTests {
    
    @Test
    @DisplayName("Test setLandAction with null action")
    void testSetLandActionNull() {
      tile = new LadderGameTile(defaultTileId, defaultCoordinates, defaultNextTileId);
      assertThrows(IllegalArgumentException.class, () -> tile.setLandAction(null));
      assertNull(tile.getLandAction());
    }
  }
} 