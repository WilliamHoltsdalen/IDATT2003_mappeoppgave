package edu.ntnu.idi.idatt.model;

import edu.ntnu.idi.idatt.model.dice.Die;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class DieTest {

  @Nested
  class PositiveDieTests {
    Die die;

    @DisplayName("(SETUP) Create a new die.")
    @BeforeEach
    void setUp() {
      try {
        die = new Die();
      } catch (Exception e) {
        fail();
      }
    }

    @DisplayName("Test roll a die and get the value.")
    @Test
    void testDieRoll() {
      die.roll();
      assertTrue(die.getValue() > 0 && die.getValue() <= 6);
    }
  }

  @Nested
  class NegativeDieTests {

    @DisplayName("Ensure that the value of a die that has not been rolled is 0.")
    @Test
    void testNonRolledDieValue() {
      Die die = new Die();
      assertEquals(0, die.getValue());
    }
  }
}
