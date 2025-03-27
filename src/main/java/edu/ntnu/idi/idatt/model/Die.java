package edu.ntnu.idi.idatt.model;

import static edu.ntnu.idi.idatt.model.validators.ArgumentValidator.dieSetValueValidator;

import java.util.Random;

/**
 * <h3>Die class</h3>
 *
 * <p>This class represents a single die. It has a value and can be rolled to get a new value.
 */
public class Die {
  private final Random random;
  private int lastRolledValue;

  /**
   * Constructor for Die class.
   */
  public Die() {
    random = new Random();
  }

  /**
   * Returns the value of the die.
   *
   * @return The value of the die.
   */
  public int getValue() {
    return lastRolledValue;
  }

  /**
   * Rolls the die.
   */
  public void roll() {
    setValue(random.nextInt(6) + 1);
  }

  /**
   * Sets the value of the die.
   *
   * @param value The value to set.
   */
  private void setValue(int value) {
    dieSetValueValidator(value);

    this.lastRolledValue = value;
  }
}