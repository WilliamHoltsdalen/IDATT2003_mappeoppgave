package edu.ntnu.idi.idatt.model;

import edu.ntnu.idi.idatt.factory.board.LadderBoardFactory;
import edu.ntnu.idi.idatt.model.board.Board;
import edu.ntnu.idi.idatt.model.player.Player;
import edu.ntnu.idi.idatt.model.tile.Tile;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

/**
 * Unit tests for the BoardGame class.
 * This class contains tests to verify the functionality of the BoardGame class,
 * including positive and negative test cases.
 */
class BoardGameTest {
    BoardGame boardGame;
    Player player1;
    Player player2;

    /**
     * Sets up a new BoardGame instance before each test.
     * Initializes players for testing.
     */
    @BeforeEach
    void setUp() {
        player1 = new Player("Player 1","#00FF00");
        player2 = new Player("Player 2","#00FF00");
        Board board = LadderBoardFactory.createLadderBoard("classic");
        for (int i = 0; i <= 100; i++) {
            board.addTile(new Tile(i, i + 1));
        }
        boardGame = new BoardGame(board, Arrays.asList(player1, player2), 2);

    }

    @Nested
    class PositiveBoardGameTests {
        /**
         * Tests that the board is created correctly.
         */
        @DisplayName("Test getting the board.")
        @Test
        void testGetBoard() {
            assertEquals(100, boardGame.getBoard().getTileCount());
        }

        /**
         * Tests that players are added correctly.
         */
        @DisplayName("Test getting the players.")
        @Test
        void testGetPlayers() {
            assertEquals(2, boardGame.getPlayers().size());
            assertEquals("Player 1", boardGame.getPlayers().get(0).getName());
            assertEquals("Player 2", boardGame.getPlayers().get(1).getName());
        }

        /**
         * Tests that the current player is initially null.
         */
        @DisplayName("Test that the current player is initially null.")
        @Test
        void testGetCurrentPlayer() {
          assertNull(boardGame.getCurrentPlayer());
        }

        /**
         * Tests that the round number starts at 0.
         */
        @DisplayName("Test that the round number starts at 0.")
        @Test
        void testGetRoundNumber() {
            assertEquals(0, boardGame.getRoundNumber());
        }

        /**
         * Tests that the winner is correctly identified.
         */
        @DisplayName("Test getting the winner.")
        @Test
        void testGetWinner() {
            player1.placeOnTile(boardGame.getBoard().getTile(100));
            assertEquals(player1, boardGame.getWinner());
        }
    }

    @Nested
    class NegativeBoardGameTests {
        /**
         * Tests that creating a BoardGame with invalid board throws an exception.
         */
        @DisplayName("Test creating a BoardGame with invalid board should throw an exception.")
        @Test
        void testCreateBoardGameWithInvalidBoard() {
            assertThrows(IllegalArgumentException.class, () -> new BoardGame(null, List.of(player1), 1));
            assertThrows(IllegalArgumentException.class, () -> new BoardGame(LadderBoardFactory.createLadderBoard("doesNotExist"), Arrays.asList(player1), 1));
        }

        /**
         * Tests that creating a BoardGame with player list set to null throws an exception
         */
        @DisplayName("Test creating a BoardGame with player list set to null should throw an exception.")
        @Test
        void testCreateBoardGameWithNullPlayerList() {
            assertThrows(IllegalArgumentException.class, () -> new BoardGame(LadderBoardFactory.createLadderBoard("classic"), null, 1));
        }

        /**
         * Tests that creating a BoardGame with invalid dice count throws an exception.
         */
        @DisplayName("Test creating a BoardGame with invalid dice count should throw an exception.")
        @Test
        void testCreateBoardGameWithInvalidDiceCount() {
            assertThrows(IllegalArgumentException.class, () -> new BoardGame(LadderBoardFactory.createLadderBoard("classic"), Arrays.asList(player1), 0));
            assertThrows(IllegalArgumentException.class, () -> new BoardGame(LadderBoardFactory.createLadderBoard("classic"), Arrays.asList(player1), -1));
        }
    }
}
