package edu.ntnu.idi.idatt.model;

import edu.ntnu.idi.idatt.model.tile.LadderAction;
import edu.ntnu.idi.idatt.model.tile.Tile;
import edu.ntnu.idi.idatt.model.tile.TileAction;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the Tile class.
 * This class contains tests to verify the functionality of the Tile,
 * including positive and negative test cases.
 */
class TileTest {
  Tile tile;
  Player player;

  /**
   * Sets up a new Tile instance before each test.
   * Initializes a Player for testing.
   */
  @BeforeEach
  void setUp() {
    tile = new Tile(1, 2);
    player = new Player("Test Player", "#00FF00");
  }

  @Nested
  class PositiveTileTests {
    /**
     * Tests that the tile ID is set correctly.
     */
    @DisplayName("Test getting the tile ID.")
    @Test
    void testGetTileId() {
      assertEquals(1, tile.getTileId());
    }

    /**
     * Tests that the land action is null after tile creation.
     */
    @DisplayName("Test that land action is null after tile creation.")
    @Test
    void testGetLandAction() {
      assertNull(tile.getLandAction());
    }

    /**
     * Tests that setting a land action updates the land action correctly.
     */
    @DisplayName("Test setting a land action.")
    @Test
    void testSetLandAction() {
      TileAction action = new LadderAction(2, "test");
      tile.setLandAction(action);
      assertEquals(action, tile.getLandAction());
    }
  }

  @Nested
  class NegativeTileTests {
    /**
     * Tests that creating a tile with an invalid ID throws an exception.
     */
    @DisplayName("Test creating a tile with an invalid ID throws an exception.")
    @Test
    void testCreateTileWithInvalidId() {
      assertThrows(IllegalArgumentException.class, () -> new Tile(-1, 1));
      }

    /**
     * Tests that setting an invalid land action throws an exception.
     */
    @DisplayName("Test setting an invalid land action throws an exception.")
    @Test
    void testSetInvalidLandAction() {
      assertThrows(IllegalArgumentException.class, () -> tile.setLandAction(null));
    }
  }
}
