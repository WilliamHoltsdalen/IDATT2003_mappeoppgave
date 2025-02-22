package edu.ntnu.idi.idatt.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
      die = new Die();
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
