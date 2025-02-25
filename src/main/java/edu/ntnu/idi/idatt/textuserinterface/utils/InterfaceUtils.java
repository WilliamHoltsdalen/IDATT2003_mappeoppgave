package edu.ntnu.idi.idatt.textuserinterface.utils;

import java.util.Scanner;

public final class InterfaceUtils {
  private static final Scanner scanner = new Scanner(System.in);

  /**
   * Empty constructor to prevent instantiation of the utility class.
   */
  private InterfaceUtils() {}

  /**
   * Prints two newlines to act as a spacer between commands.
   */
  public static void printSpacing() {
    System.out.print("\n\n");
  }

  /**
   * Prints the welcome message to the console.
   */
  public static void printWelcomeMessage() {
    printSpacing();
    System.out.println(
        """
        |-----------------------------------------|
        | Welcome to the ladder game application! |
        |-----------------------------------------|
        """);
    printSpacing();
  }

  /**
   * Prints the goodbye message to the console.
   */
  public static void printGoodbyeMessage() {
    printSpacing();
    System.out.println(
        """
        |---------------------------------------------|
        | Thank you for playing! Have a great day! :) |
        |---------------------------------------------|
        """);
    printSpacing();
  }

  public static void printGameClientMenu() {
    System.out.print("""
    Main menu
    --------------
    1. Start game
    2. Exit
    --------------
    Your choice:\s""");
  }


  /**
   * Prints a given error message.
   *
   * @param errorMessage the error message to print
   */
  public static void printErrorMessage(String errorMessage) {
    System.out.println("Error: " + errorMessage);
  }

  /**
   * Reads a string input from the console. If the input is not a valid string, the method will
   * print an error message and prompt the user to try again. It will continue to prompt the user
   * until a valid string is entered.
   *
   * @return the string input read from the console
   */
  public static String stringInput() {
    try {
      String input = scanner.nextLine();
      if (input == null || input.isBlank()) {
        throw new IllegalArgumentException();
      }
      return input.trim();
    } catch (Exception e) {
      printErrorMessage("Please enter a valid text string.");
      System.out.print("Please try again: ");
      return stringInput();
    }
  }

  /** Reads an integer input from the console. If the input is not a valid integer, the method will
   * print an error message and prompt the user to try again. It will continue to prompt the user
   * until a valid integer is entered.
   *
   * @return the integer input read from the console
   */
  public static int integerInput() {
    try {
      int input = Integer.parseInt(scanner.nextLine());
      if (input < 0) {
        throw new IllegalArgumentException();
      }
      return input;
    } catch (Exception e) {
      printErrorMessage("Please enter a valid integer (positive whole number).");
      System.out.print("Please try again: ");
      return integerInput();

    }
  }

  /**
   * Exits the application by closing the scanner and printing the goodbye message, and then exiting
   * the application.
   */
  public static void exitApplication() {
    scanner.close();
    printGoodbyeMessage();
    System.exit(0);
  }
}
