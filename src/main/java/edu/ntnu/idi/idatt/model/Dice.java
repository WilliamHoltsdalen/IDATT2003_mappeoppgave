package edu.ntnu.idi.idatt.model;

import static edu.ntnu.idi.idatt.model.validators.ArgumentValidator.diceAddDiceValidator;
import static edu.ntnu.idi.idatt.model.validators.ArgumentValidator.diceGetDieValueValidator;
import static edu.ntnu.idi.idatt.model.validators.ArgumentValidator.diceRollSingleDieValidator;
import static java.util.List.copyOf;

import java.util.ArrayList;
import java.util.List;

public class Dice {
  private final List<Die> diceList;

  public Dice(int numberOfDice) {
    diceList = new ArrayList<>();
    addDice(numberOfDice);
  }

  public List<Die> getDiceList() {
    return copyOf(diceList);
  }

  public int getDieValue(int dieNumber) {
    diceGetDieValueValidator(dieNumber, diceList.size());

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

  private void addDice(int numberOfDice) {
    diceAddDiceValidator(numberOfDice);

    for (int i = 0; i < numberOfDice; i++) {
      diceList.add(new Die());
    }
  }

  public void rollDice() {
    diceList.forEach(Die::roll);
  }

  public void rollSingleDie(int dieNumber) {
    diceRollSingleDieValidator(dieNumber, diceList.size());

    diceList.get(dieNumber - 1).roll();
  }
}
