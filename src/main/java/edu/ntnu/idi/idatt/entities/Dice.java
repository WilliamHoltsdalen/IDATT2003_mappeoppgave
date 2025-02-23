package edu.ntnu.idi.idatt.entities;

import static java.util.List.copyOf;

import java.util.ArrayList;
import java.util.List;

public class Dice {
  private static final String DIE_NUMBER_OUT_OF_RANGE_ERROR =
      "Die number must be between 1 and the number of dice.";

  private final List<Die> diceList;

  public Dice(int numberOfDice) throws IllegalArgumentException {
    diceList = new ArrayList<>();
    addDice(numberOfDice);
  }

  public List<Die> getDiceList() {
    return copyOf(diceList);
  }

  public int getDieValue(int dieNumber) throws IllegalArgumentException {
    if (dieNumber < 1 || dieNumber > diceList.size()) {
      throw new IllegalArgumentException(DIE_NUMBER_OUT_OF_RANGE_ERROR);
    }
    return diceList.get(dieNumber - 1).getValue();
  }

  public int getTotalValue() {
    int totalValue = 0;
    for (Die die : diceList) {
      totalValue += die.getValue();
    }
    return totalValue;
  }

  public int getNumberOfDice() {
    return diceList.size();
  }

  private void addDice(int numberOfDice) throws IllegalArgumentException {
    if (numberOfDice < 1) {
      throw new IllegalArgumentException("Number of dice must be greater than 0.");
    }
    for (int i = 0; i < numberOfDice; i++) {
      diceList.add(new Die());
    }
  }

  public void rollDice() {
    for (Die die : diceList) {
      die.roll();
    }
  }

  public void rollSingleDie(int dieNumber) throws IllegalArgumentException {
    if (dieNumber < 1 || dieNumber > diceList.size()) {
      throw new IllegalArgumentException(DIE_NUMBER_OUT_OF_RANGE_ERROR);
    }
    diceList.get(dieNumber - 1).roll();
  }



}
