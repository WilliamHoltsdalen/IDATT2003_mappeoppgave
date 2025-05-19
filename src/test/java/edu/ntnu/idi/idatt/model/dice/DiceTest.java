package edu.ntnu.idi.idatt.model.dice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

class DiceTest {
  
  private Dice dice2;
  private Dice dice5;
  
  @BeforeEach
  void setUp() {
    dice2 = new Dice(2); // A pair of dice
    dice5 = new Dice(5); // Five dice
  }
  
  @Nested
  @DisplayName("Constructor and Initial State")
  class ConstructorAndInitialStateTests {
    
    @Test
    @DisplayName("Test constructor creates correct number of dice")
    void testConstructorCreatesCorrectNumberOfDice() {
      assertEquals(2, dice2.getNumberOfDice());
      assertEquals(2, dice2.getDiceList().size());
      assertEquals(5, dice5.getNumberOfDice());
      assertEquals(5, dice5.getDiceList().size());
    }
    
    @Test
    @DisplayName("Test initial values of all dice are 0")
    void testInitialDiceValuesAreZero() {
      for (Die die : dice2.getDiceList()) {
        assertEquals(0, die.getValue(), "Each die should initially be 0.");
      }
      assertEquals(0, dice2.getTotalValue(), "Total value should be 0 initially.");
      
      for (Die die : dice5.getDiceList()) {
        assertEquals(0, die.getValue(), "Each die should initially be 0.");
      }
      assertEquals(0, dice5.getTotalValue(), "Total value should be 0 initially.");
    }
    
    @Test
    @DisplayName("Test getDiceList returns an unmodifiable list")
    void testGetDiceListUnmodifiable() {
      List<Die> list = dice2.getDiceList();
      assertThrows(UnsupportedOperationException.class, () -> list.add(new Die()));
      assertThrows(UnsupportedOperationException.class, () -> list.removeFirst());
    }
  }
  
  @Nested
  @DisplayName("Rolling Dice")
  class RollingDiceTests {
    
    @RepeatedTest(10)
    @DisplayName("Test rollDice() rolls all dice and values are in range")
    void testRollDiceAllDiceRolled() {
      dice2.rollDice();
      int totalValue = 0;
      for (int i = 1; i <= dice2.getNumberOfDice(); i++) {
        int dieValue = dice2.getDieValue(i);
        assertTrue(dieValue >= 1 && dieValue <= 6, "Die " + i + " value out of range: " + dieValue);
        totalValue += dieValue;
      }
      assertEquals(totalValue, dice2.getTotalValue(), "Total value should match sum of individual dice.");
      assertTrue(dice2.getTotalValue() >= dice2.getNumberOfDice() && dice2.getTotalValue() <= dice2.getNumberOfDice() * 6);
    }
    
    @RepeatedTest(10)
    @DisplayName("Test rollSingleDie() rolls only the specified die")
    void testRollSingleDie() {
      dice5.rollDice();
      int[] initialValues = IntStream.rangeClosed(1, 5).map(dice5::getDieValue).toArray();
      
      // Roll only the 3rd die
      dice5.rollSingleDie(3);
      int newDie3Value = dice5.getDieValue(3);
      assertTrue(newDie3Value >= 1 && newDie3Value <= 6);
      
      int newTotalValue = 0;
      for (int i = 1; i <= 5; i++) {
        int currentValue = dice5.getDieValue(i);
        if (i == 3) {
          assertEquals(newDie3Value, currentValue, "Value of die 3 should be the new rolled value.");
        } else {
          assertEquals(initialValues[i-1], currentValue, "Value of die " + i + " should not have changed.");
        }
        newTotalValue += currentValue;
      }
      assertEquals(newTotalValue, dice5.getTotalValue());
    }
  }
  
  @Nested
  @DisplayName("Value Retrieval")
  class ValueRetrievalTests {
    
    @Test
    @DisplayName("Test getDieValue retrieves correct value after roll")
    void testGetDieValue() {
      dice2.rollDice();
      int val1 = dice2.getDiceList().get(0).getValue();
      int val2 = dice2.getDiceList().get(1).getValue();
      assertEquals(val1, dice2.getDieValue(1));
      assertEquals(val2, dice2.getDieValue(2));
    }
    
    @Test
    @DisplayName("Test getTotalValue is sum of individual dice after roll")
    void testGetTotalValue() {
      dice2.rollDice();
      int expectedTotal = dice2.getDiceList().get(0).getValue() + dice2.getDiceList().get(1).getValue();
      assertEquals(expectedTotal, dice2.getTotalValue());
      
      dice5.rollDice();
      int expectedTotal5 = 0;
      for(Die d : dice5.getDiceList()) expectedTotal5 += d.getValue();
      assertEquals(expectedTotal5, dice5.getTotalValue());
      assertTrue(dice5.getTotalValue() >= 5 && dice5.getTotalValue() <= 30);
    }
    
    @Test
    @DisplayName("Test getNumberOfDice is correct")
    void testGetNumberOfDice() {
      assertEquals(2, dice2.getNumberOfDice());
      assertEquals(5, dice5.getNumberOfDice());
    }
  }
  
  @Nested
  @DisplayName("Negative Tests")
  class NegativeTests {
    
    @Test
    @DisplayName("Test constructor with zero or negative number of dice throws Exception")
    void testConstructorInvalidNumberOfDice() {
      assertThrows(IllegalArgumentException.class, () -> new Dice(0));
      assertThrows(IllegalArgumentException.class, () -> new Dice(-1));
    }
    
    @Test
    @DisplayName("Test getDieValue with invalid die number throws Exception")
    void testGetDieValueInvalidNumber() {
      assertThrows(IllegalArgumentException.class, () -> dice2.getDieValue(0));
      assertThrows(IllegalArgumentException.class, () -> dice2.getDieValue(3));
      assertThrows(IllegalArgumentException.class, () -> dice2.getDieValue(-1));
    }
    
    @Test
    @DisplayName("Test rollSingleDie with invalid die number throws Exception")
    void testRollSingleDieInvalidNumber() {
      assertThrows(IllegalArgumentException.class, () -> dice2.rollSingleDie(0));
      assertThrows(IllegalArgumentException.class, () -> dice2.rollSingleDie(3));
      assertThrows(IllegalArgumentException.class, () -> dice2.rollSingleDie(-1));
    }
  }
} 