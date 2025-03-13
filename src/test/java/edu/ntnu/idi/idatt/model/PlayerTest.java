package edu.ntnu.idi.idatt.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the Player class.
 * This class contains tests to verify the functionality of the Player,
 * including positive and negative test cases.
 */
public class PlayerTest {
    Player player;
    Tile tile;

    /**
     * Sets up a new Player instance before each test.
     * Initializes a Tile for testing.
     */
    @BeforeEach
    void setUp() {
        player = new Player("Test Player");
        tile = new Tile(1);
    }

    @Nested
    class PositivePlayerTests {
        /**
         * Tests that the player's name is set correctly.
         */
        @DisplayName("Test getting the player's name.")
        @Test
        void testGetName() {
            assertEquals("Test Player", player.getName());
        }

        /**
         * Tests that the current tile after player creation is null.
         */
        @DisplayName("Test that tile is null after player creation.")
        @Test
        void testGetCurrentTile() {
            assertNull(player.getCurrentTile());
        }

        /**
         * Tests that placing the player on a tile sets the current tile correctly.
         */
        @DisplayName("Test placing the player on a tile.")
        @Test
        void testPlaceOnTile() {
            player.placeOnTile(tile);
            assertEquals(tile, player.getCurrentTile());
        }
    }

    @Nested
    class NegativePlayerTests {
        /**
         * Tests that setting an invalid name throws an exception.
         */
        @DisplayName("Test setting an invalid player name should throw an exception.")
        @Test
        void testSetInvalidName() {
            assertThrows(IllegalArgumentException.class, () -> player.setName(null));
            assertThrows(IllegalArgumentException.class, () -> player.setName(""));
        }

        /**
         * Tests that placing the player on an invalid tile throws an exception.
         */
        @DisplayName("Test placing the player on an invalid tile should throw an exception.")
        @Test
        void testPlaceOnInvalidTile() {
            assertThrows(IllegalArgumentException.class, () -> player.placeOnTile(null));
        }
    }
}
