package edu.ntnu.idi.idatt.textuserinterface.view;

import edu.ntnu.idi.idatt.controllers.GameController;
import edu.ntnu.idi.idatt.model.Player;
import edu.ntnu.idi.idatt.textuserinterface.utils.InterfaceUtils;
import java.util.ArrayList;
import java.util.List;

/**
 * <h3>TextUserInterface</h3>
 *
 * <p>TextUserInterface is responsible for handling the text-based user interface of the game.
 * It provides methods to initialize the game, display menus, and handle user input.
 * It uses a {@link GameController} to manage the game logic and player interactions.
 *
 * @author William Holtsdalen
 */
public class TextUserInterface {
  private GameController gameController;

  /**
   * Initializes the game by creating a new GameController instance.
   * This method should be called before any other method in this class.
   */
  public void init() {
    try {
      InterfaceUtils.printWelcomeMessage();
      this.gameController = new GameController();
    } catch (Exception e) {
      InterfaceUtils.exitByError(e.getMessage());
    }
  }

  /**
   * Starts the text user interface and runs the game loop. This method should be called after
   * the {@link #init()} method.
   */
  public void run() {
    try {
      tuiMainMenu();
    } catch (Exception e) {
      InterfaceUtils.exitByError(e.getMessage());
    }

    System.out.println("Exiting application...");
    InterfaceUtils.exitApplication();
  }


  /**
   * Displays the main and handles game flow, allowing the user to start a new game or exit the
   * application.
   */
  private void tuiMainMenu() {
    boolean finished = false;
    while (!finished) {
      InterfaceUtils.printGameClientMenu();
      int choice = InterfaceUtils.integerInput();
      switch (choice) {
        case 1:
          init();
          tuiGameClient();
          break;
        case 0:
          finished = true;
          break;
        default:
          System.out.println("Invalid choice. Please try again.");
      }
    }
  }


  private void printWinner() {
    StringBuilder winnerMessage = new StringBuilder();
    winnerMessage.append("\n------------------------------------------------------------");
    winnerMessage.append("\n    The winner of the game is: ");
    winnerMessage.append(gameController.getWinner().getName());
    winnerMessage.append("  (after ");
    winnerMessage.append(gameController.getRoundNumber());
    winnerMessage.append(" rounds)!");
    winnerMessage.append("\n------------------------------------------------------------\n");
    System.out.println(winnerMessage);
  }

  /**
   * Displays the current round number.
   */
  private void printRoundNumber() {
    System.out.println("\n-----------------------------------");
    System.out.println("Round number: " + (gameController.getRoundNumber() + 1));
  }

  /**
   * Displays the game client and handles player moves until a winner is found.
   */
  private void tuiGameClient() {
    while (gameController.getWinner() == null) {
      printRoundNumber();
      for (Player player : gameController.getPlayers()) {
        gameController.performPlayerMove();
        System.out.println(player.getName() + " is on tile " + player.getCurrentTile().getTileId());
      }
    }
    printWinner();
  }
}
