package edu.ntnu.idi.idatt.textuserinterface.view;

import edu.ntnu.idi.idatt.controllers.GameController;
import edu.ntnu.idi.idatt.model.Player;
import edu.ntnu.idi.idatt.textuserinterface.utils.InterfaceUtils;
import java.util.ArrayList;
import java.util.List;

public class TextUserInterface {
  private GameController gameController;

  public void init() {
    try {
    } catch (Exception e) {
      exitByError(e.getMessage());
    }
  }

  public void run() {
    try {
      InterfaceUtils.printWelcomeMessage();
      tuiMainMenu();
    } catch (Exception e) {
      exitByError(e.getMessage());
    }

    System.out.println("Exiting application...");
    InterfaceUtils.exitApplication();
  }

  private void exitByError(String errorMessage) {
    System.out.println("An error occurred: " + errorMessage);
    System.out.println("Exiting application...");
    InterfaceUtils.exitApplication();
  }

  private void initGameController() {
    this.gameController = new GameController();
  }

  private void tuiMainMenu() {
    boolean finished = false;
    while (!finished) {
      InterfaceUtils.printGameClientMenu();
      int choice = InterfaceUtils.integerInput();
      switch (choice) {
        case 1:
          initGameController();
          tuiGameClient();
          break;
        case 2:
          finished = true;
          break;
        default:
          System.out.println("Invalid choice. Please try again.");
      }
    }
  }

  private void printWinner() {
    System.out.printf(
        """
            
            ------------------------------------------------------------
                The winner of the game is: %s  (after %d rounds)!
            ------------------------------------------------------------
            
            """, gameController.getWinner().getName(), gameController.getRoundNumber());
  }

  private void printRoundNumber() {
    System.out.println("\n-----------------------------------");
    System.out.println("Round number: " + (gameController.getRoundNumber() + 1));
  }

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
