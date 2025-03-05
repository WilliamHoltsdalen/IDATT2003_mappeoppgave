package edu.ntnu.idi.idatt.model;

import edu.ntnu.idi.idatt.utils.DiceUtils;

public class Die {
  private int lastRolledValue;

  public int getValue() {
    return lastRolledValue;
  }

  public void roll() {
    setValue(DiceUtils.randomDieRoll());
  }

  private void setValue(int value) throws IllegalArgumentException {
    if (value < 1 || value > 6) {
      throw new IllegalArgumentException("Value must be between 1 and 6");
    }
    this.lastRolledValue = value;
  }
}