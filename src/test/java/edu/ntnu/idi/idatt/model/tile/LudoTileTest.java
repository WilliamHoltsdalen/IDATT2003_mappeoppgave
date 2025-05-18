package edu.ntnu.idi.idatt.model.tile;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LudoTileTest {
  
  private final int defaultTileId = 5;
  private final int[] defaultCoordinates = {1, 2};
  private final int defaultNextTileId = 6;
  private final String defaultType = "track";
  
  @Nested
  @DisplayName("Constructor Tests")
  class ConstructorTests {
    
    @Test
    @DisplayName("Test constructor sets all fields correctly")
    void testConstructorSetsAllFields() {
      LudoTile tile = new LudoTile(defaultTileId, defaultCoordinates, defaultNextTileId, defaultType);
      assertEquals(defaultTileId, tile.getTileId());
      assertArrayEquals(defaultCoordinates, tile.getCoordinates());
      assertEquals(defaultNextTileId, tile.getNextTileId());
      assertEquals(defaultType, tile.getType(), "Type should be set by constructor");
    }
  }
  
  @Nested
  @DisplayName("Getter and Setter for Type")
  class TypeTests {
    
    @Test
    @DisplayName("Test setType and getType")
    void testSetAndGetType() {
      LudoTile tile = new LudoTile(defaultTileId, defaultCoordinates, defaultNextTileId, defaultType);
      assertEquals(defaultType, tile.getType());
      
      String newType = "finish-1";
      tile.setType(newType);
      assertEquals(newType, tile.getType(), "getType should return the newly set type");
    }
  }
  
  @Nested
  @DisplayName("Negative Tests")
  class NegativeTests {
    
    @Test
    @DisplayName("Test constructor with invalid tileId")
    void testConstructorInvalidTileId() {
      assertThrows(IllegalArgumentException.class, () -> 
      new LudoTile(-1, defaultCoordinates, defaultNextTileId, defaultType)
      );
    }
    
    @Test
    @DisplayName("Test constructor with null coordinates")
    void testConstructorNullCoordinates() {
      assertThrows(IllegalArgumentException.class, () -> 
      new LudoTile(defaultTileId, null, defaultNextTileId, defaultType)
      );
    }
    
    @Test
    @DisplayName("Test constructor with coordinates array of incorrect length")
    void testConstructorInvalidCoordinatesLength() {
      assertThrows(IllegalArgumentException.class, () -> 
      new LudoTile(defaultTileId, new int[]{0}, defaultNextTileId, defaultType)
      );
      assertThrows(IllegalArgumentException.class, () -> 
      new LudoTile(defaultTileId, new int[]{0,1,2}, defaultNextTileId, defaultType)
      );
    }
    
    @Test
    @DisplayName("Test constructor with nextTileId same as tileId")
    void testConstructorNextTileIdSameAsTileId() {
      assertThrows(IllegalArgumentException.class, () -> 
      new LudoTile(defaultTileId, defaultCoordinates, defaultTileId, defaultType)
      );
    }
    
    @Test
    @DisplayName("Test constructor with negative nextTileId")
    void testConstructorNegativeNextTileId() {
      assertThrows(IllegalArgumentException.class, () -> 
      new LudoTile(defaultTileId, defaultCoordinates, -1, defaultType)
      );
    }
  }
} 