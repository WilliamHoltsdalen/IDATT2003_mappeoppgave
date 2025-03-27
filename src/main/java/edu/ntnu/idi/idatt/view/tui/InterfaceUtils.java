package edu.ntnu.idi.idatt.view.tui;

import java.util.Scanner;

/**
 * <h3>Text User Interface Utility Class</h3>
 *
 * <p>
 * Utility class for handling console input and output in the text user interface.
 * This class provides methods for printing messages, reading user input, and managing the game
 * interface.
 */
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
    System.out.println("\n");
  }

  /**
   * Prints the welcome message to the console.
   */
  public static void printWelcomeMessage() {
    printSpacing();
    StringBuilder welcomeMessage = new StringBuilder();
    welcomeMessage.append("\n|-----------------------------------------|");
    welcomeMessage.append("\n| Welcome to the ladder game application! |");
    welcomeMessage.append("\n|-----------------------------------------|");
    System.out.println(welcomeMessage);
    printSpacing();
  }

  /**
   * Prints the goodbye message to the console.
   */
  public static void printGoodbyeMessage() {
    printSpacing();
    StringBuilder goodbyeMessage = new StringBuilder();
    goodbyeMessage.append("\n|---------------------------------------------|");
    goodbyeMessage.append("\n| Thank you for playing! Have a great day! :) |");
    goodbyeMessage.append("\n|---------------------------------------------|");
    System.out.println(goodbyeMessage);
    printSpacing();
  }

  /**
   * Prints the main menu of the game client.
   */
  public static void printGameClientMenu() {
    StringBuilder menuMessage = new StringBuilder();
    menuMessage.append("\nMain menu");
    menuMessage.append("\n--------------");
    menuMessage.append("\n1. Start game");
    menuMessage.append("\n0. Exit");
    menuMessage.append("\n--------------");
    menuMessage.append("\nYour choice: ");
    System.out.println(menuMessage);
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
      System.out.println("Please try again: ");
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
      System.out.println("Please try again: ");
      return integerInput();

    }
  }

  /**
   * Exits the application by closing the scanner, printing an error message, and then exiting
   * the application.
   *
   * @param errorMessage the error message to print before exiting
   */
  public static void exitByError(String errorMessage) {
    System.out.println("An error occurred: " + errorMessage);
    System.out.println("Exiting application...");
    exitApplication();
  }

  /**
   * Exits the application by closing the scanner, printing the goodbye message, and then exiting
   * the application.
   */
  public static void exitApplication() {
    scanner.close();
    printGoodbyeMessage();
    System.exit(0);
  }
}
