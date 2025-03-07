package edu.ntnu.idi.idatt.model.utils;

import java.util.Random;

public final class DiceUtils {
  private static final Random random = new Random();

  /** Prevent instantiation of the utility class.*/
  private DiceUtils() {}

  public static int randomDieRoll() {
    return random.nextInt(6) + 1;
  }
}
