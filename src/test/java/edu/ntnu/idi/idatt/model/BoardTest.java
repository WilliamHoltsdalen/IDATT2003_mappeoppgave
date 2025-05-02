package edu.ntnu.idi.idatt.model;

import edu.ntnu.idi.idatt.model.tile.Tile;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the Board class.
 * This class contains tests to verify the functionality of the Board,
 * including positive and negative test cases.
 */
class BoardTest {
    Board board;

    /**
     * Sets up a new Board instance before each test.
     * Initializes a 3x3 board for testing.
     */
    @DisplayName("(SETUP) Create new board.")
    @BeforeEach
    void setUp() {
        board = new Board("Test board", "This board is used for testing");
        for (int i = 0; i <= 9; i++) {
            board.addTile(new Tile(i, i + 1));
        }
    }

    @Nested
    class PositiveBoardTests {
        /**
         * Tests the tile count is correct after populating the board.
         * Asserts that the tile count is equal to 9.
         */
        @DisplayName("Test getting the tile count after populating the board.")
        @Test
        void testGetTileCount() {
            int count = board.getTileCount();
            
            assertEquals(9, count);
        }

        /**
         * Tests retrieving a tile by its tileId.
         * Asserts that the retrieved tile's ID matches the requested tileId.
         */
        @DisplayName("Test getting a tile by tileId.")
        @Test
        void testGetTile() {
            int tileId = 1;
            Tile retrievedTile = board.getTile(tileId);
            
            assertEquals(tileId, retrievedTile.getTileId());
        }
    }

    @Nested
    class NegativeBoardTests {
        /**
         * Tests that retrieving a non-existent tile throws an exception.
         * Asserts that an IllegalArgumentException is thrown for invalid tileIds.
         */
        @DisplayName("Test getting a tile that does not exist should return null.")
        @Test
        void testGetNonExistentTile() {
            assertThrows(IllegalArgumentException.class, () -> board.getTile(-1));
            assertThrows(IllegalArgumentException.class, () -> board.getTile(99));
        }

        /**
         * Tests that creating a board with invalid arguments throws an exception.
         * Asserts that an IllegalArgumentException is thrown for invalid arguments.
         */
        @DisplayName("Test creating a board with invalid arguments should throw an exception.")
        @Test
        void testCreateBoardWithInvalidArguments() {
            assertThrows(IllegalArgumentException.class, () -> new Board(null, "Test board description"));
            assertThrows(IllegalArgumentException.class, () -> new Board("", "Test board description"));
            assertThrows(IllegalArgumentException.class, () -> new Board("Test board", null));
            assertThrows(IllegalArgumentException.class, () -> new Board("Test board", ""));
        }
    }
}
