package edu.ntnu.idi.idatt.model.dice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DieTest {
  
  private Die die;
  
  @BeforeEach
  void setUp() {
    die = new Die();
  }
  
  @Nested
  @DisplayName("Initial State and Rolling")
  class InitialStateAndRollingTests {
    
    @Test
    @DisplayName("Test initial die value is 0 before first roll")
    void testInitialValueIsZero() {
      assertEquals(0, die.getValue(), "Initial value of a die should be 0 before any roll.");
    }
    
    @RepeatedTest(20) // Repeating the test to increase confidence in randomness within the bounds
    @DisplayName("Test roll() sets value between 1 and 6")
    void testRollSetsValueInRange() {
      die.roll();
      int value = die.getValue();
      assertTrue(value >= 1 && value <= 6, "Die value should be between 1 and 6 after roll. Was: " + value);
    }
    
    @Test
    @DisplayName("Test getValue returns the last rolled value")
    void testGetValueReturnsLastRolled() {
      die.roll();
      int firstRollValue = die.getValue();
      assertEquals(firstRollValue, die.getValue(), "getValue should consistently return the last rolled value.");
      
      die.roll();
      int secondRollValue = die.getValue();
      assertEquals(secondRollValue, die.getValue());
    }
  }
} 