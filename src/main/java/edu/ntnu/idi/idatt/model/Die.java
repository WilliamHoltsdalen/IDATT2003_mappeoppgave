package edu.ntnu.idi.idatt.model;

import static edu.ntnu.idi.idatt.model.validators.ArgumentValidator.dieSetValueValidator;

import edu.ntnu.idi.idatt.utils.DiceUtils;

public class Die {
  private int lastRolledValue;

  public int getValue() {
    return lastRolledValue;
  }

  public void roll() {
    setValue(DiceUtils.randomDieRoll());
  }

  private void setValue(int value) {
    dieSetValueValidator(value);

    this.lastRolledValue = value;
  }
}