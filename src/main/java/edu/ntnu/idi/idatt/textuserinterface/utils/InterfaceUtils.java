package edu.ntnu.idi.idatt.textuserinterface;

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
        |-----------------------------|
        | Welcome to the ladder game! |
        |-----------------------------|
        """);
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
