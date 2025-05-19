package edu.ntnu.idi.idatt.model.dice;

import static edu.ntnu.idi.idatt.model.validator.ArgumentValidator.diceAddDiceValidator;
import static edu.ntnu.idi.idatt.model.validator.ArgumentValidator.diceGetDieValueValidator;
import static edu.ntnu.idi.idatt.model.validator.ArgumentValidator.diceRollSingleDieValidator;
import static java.util.List.copyOf;

import java.util.ArrayList;
import java.util.List;

/**
 * Dice class
 *
 * <p>This class represents a collection of dice. It contains a list of Die objects, which are
 * rolled when the dice are rolled.
 */
public class Dice {
  private final List<Die> diceList;

  /**
   * Constructor for Dice class.
   *
   * @param numberOfDice The number of dice to create.
   */
  public Dice(int numberOfDice) {
    diceList = new ArrayList<>();
    addDice(numberOfDice);
  }

  /**
   * Returns the list of dice.
   *
   * @return The list of dice.
   */
  public List<Die> getDiceList() {
    return copyOf(diceList);
  }

  /**
   * Returns the value of the die at the given index.
   *
   * @param dieNumber The index of the die to get the value of.
   * @return The value of the die at the given index.
   */
  public int getDieValue(int dieNumber) {
    diceGetDieValueValidator(dieNumber, diceList.size());

    return diceList.get(dieNumber - 1).getValue();
  }

  /**
   * Returns the total value of all the dice.
   *
   * @return The total value of all the dice.
   */
  public int getTotalValue() {
    int totalValue = 0;
    for (Die die : diceList) {
      totalValue += die.getValue();
    }
    return totalValue;
  }

  /**
   * Returns the number of dice.
   *
   * @return The number of dice.
   */
  public int getNumberOfDice() {
    return diceList.size();
  }

  /**
   * Adds the given number of dice to the list.
   *
   * @param numberOfDice The number of dice to add.
   */
  private void addDice(int numberOfDice) {
    diceAddDiceValidator(numberOfDice);

    for (int i = 0; i < numberOfDice; i++) {
      diceList.add(new Die());
    }
  }

  /**
   * Rolls all the dice in the list.
   */
  public void rollDice() {
    diceList.forEach(Die::roll);
  }

  /**
   * Rolls a single die at the given index.
   *
   * @param dieNumber The index of the die to roll.
   */
  public void rollSingleDie(int dieNumber) {
    diceRollSingleDieValidator(dieNumber, diceList.size());

    diceList.get(dieNumber - 1).roll();
  }
}
