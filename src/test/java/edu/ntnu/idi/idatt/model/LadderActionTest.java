package edu.ntnu.idi.idatt.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the LadderAction class.
 * This class contains tests to verify the functionality of the LadderAction,
 * including positive and negative test cases.
 */
public class LadderActionTest {
  private LadderAction ladderAction;

  /**
   * Sets up a new LadderAction instance before each test.
   * This method initializes the ladderAction object with a valid destination tile ID
   * and description to be used in the tests.
   */
  @BeforeEach
  void setUp() {
    ladderAction = new LadderAction(5, "Climb up the ladder");
  }

  @Nested
  class PositiveLadderActionTests {
    /**
     * Test getting the destination tile ID.
     * This test verifies that the destination tile ID of the ladderAction
     * is correctly returned.
     */
    @Test
    @DisplayName("Test getting the destination tile ID.")
    void testGetDestinationTileId() {
      assertEquals(5, ladderAction.getDestinationTileId());
    }

    /**
     * Test getting the description.
     * This test checks that the description of the ladderAction
     * is correctly returned.
     */
    @Test
    @DisplayName("Test getting the description.")
    void testGetDescription() {
      assertEquals("Climb up the ladder", ladderAction.getDescription());
    }
  }

  @Nested
  class NegativeLadderActionTests {
    /**
     * Test creating a LadderAction with an invalid destination tile ID should throw an exception.
     * This test ensures that an IllegalArgumentException is thrown
     * when attempting to create a LadderAction with a negative destination tile ID.
     */
    @Test
    @DisplayName("Test creating a LadderAction with an invalid destination tile ID should throw an exception.")
    void testCreateLadderActionWithInvalidDestinationTileId() {
      assertThrows(IllegalArgumentException.class, () -> new LadderAction(-1, "Invalid action"));
    }

    /**
     * Test creating a LadderAction with a null description should throw an exception.
     * This test verifies that an IllegalArgumentException is thrown
     * when the description is null.
     */
    @Test
    @DisplayName("Test creating a LadderAction with a null description should throw an exception.")
    void testCreateLadderActionWithNullDescription() {
      assertThrows(IllegalArgumentException.class, () -> new LadderAction(5, null));
    }

    /**
     * Test creating a LadderAction with a blank description should throw an exception.
     * This test checks that an IllegalArgumentException is thrown
     * when the description is an empty string.
     */
    @Test
    @DisplayName("Test creating a LadderAction with a blank description should throw an exception.")
    void testCreateLadderActionWithBlankDescription() {
      assertThrows(IllegalArgumentException.class, () -> new LadderAction(5, ""));
    }
  }
}
