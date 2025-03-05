package edu.ntnu.idi.idatt.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class DiceTest {

  @Nested
  class PositiveDieTests {
    Dice dice;

    @DisplayName("(SETUP) Create new dice.")
    @BeforeEach
    void setUp() {
      try {
        dice = new Dice(5);
      } catch (Exception e) {
        fail();
      }
    }

    @DisplayName("Test getting the number of dice.")
    @Test
    void testGetNumberOfDice() {
      assertEquals(5, dice.getNumberOfDice());
    }

    @DisplayName("Test getting the dice list.")
    @Test
    void testGetDiceList() {
      try {
        List<Die> diceList = dice.getDiceList();
        assertEquals(5, diceList.size());
      } catch (Exception e) {
        fail();
      }
    }

    @DisplayName("Test rolling the dice.")
    @Test
    void testRollDice() {
      dice.rollDice();
      for (Die die : dice.getDiceList()) {
        if (die.getValue() == 0) {
          fail();
        }
        assertTrue(die.getValue() > 0 && die.getValue() <= 6);
      }
    }

    @DisplayName("Test rolling a single die at a time.")
    @Test
    void testRollSingleDie() {
      for (int i = 1; i <= dice.getNumberOfDice(); i++) {
        dice.rollSingleDie(i);
        assertTrue(dice.getDieValue(i) > 0 && dice.getDieValue(i) <= 6);
      }
    }

    @DisplayName("Test getting the total value of the dice.")
    @Test
    void testGetTotalValue() {
      dice.rollDice();
      assertEquals(dice.getTotalValue(), dice.getDiceList().stream().mapToInt(Die::getValue).sum());
    }
  }

  @Nested
  class NegativeDieTests {

    @DisplayName("Creating dice instance with 0 dice should throw an exception.")
    @Test
    void testCreateDiceWithInvalidAmount() {
      assertThrows(IllegalArgumentException.class, () -> new Dice(0));
    }

    @DisplayName("Creating dice instance with a negative amount should throw an exception.")
    @Test
    void testCreateDiceWithInvalidAmountNegative() {
      assertThrows(IllegalArgumentException.class, () -> new Dice(-1));
    }
  }
}
