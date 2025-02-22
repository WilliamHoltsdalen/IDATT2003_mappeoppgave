package edu.ntnu.idi.idatt.entities;

import static java.util.List.copyOf;

import java.util.ArrayList;
import java.util.List;

public class Dice {
  List<Die> diceList;

  public Dice(int numberOfDice) {
    diceList = new ArrayList<>();
    for (int i = 0; i < numberOfDice; i++) {
      diceList.add(new Die());
    }
  }

  public void rollDice() {
    for (Die die : diceList) {
      die.roll();
    }
  }

  public void rollSingleDie(int dieNumber) {
    diceList.get(dieNumber - 1).roll();
    }

  public List<Die> getDiceList() {
    return copyOf(diceList);
  }

  public int getDieValue(int dieNumber) {
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
}
