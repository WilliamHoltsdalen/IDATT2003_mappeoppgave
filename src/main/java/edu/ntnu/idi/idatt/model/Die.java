package edu.ntnu.idi.idatt.model;

import static edu.ntnu.idi.idatt.model.validators.ArgumentValidator.dieSetValueValidator;

import java.util.Random;

public class Die {
  private final Random random;
  private int lastRolledValue;

  public Die() {
    random = new Random();
    roll();
  }

  public int getValue() {
    return lastRolledValue;
  }

  public void roll() {
    setValue(random.nextInt(6) + 1);
  }

  private void setValue(int value) {
    dieSetValueValidator(value);

    this.lastRolledValue = value;
  }
}