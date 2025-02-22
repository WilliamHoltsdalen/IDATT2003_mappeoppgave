package edu.ntnu.idi.idatt.entities;

import edu.ntnu.idi.idatt.utils.DiceUtils;

public class Die {
  int lastRolledValue;

  public void roll() {
    lastRolledValue = DiceUtils.randomDieRoll();
  }

  public int getValue() {
    return lastRolledValue;
  }
}