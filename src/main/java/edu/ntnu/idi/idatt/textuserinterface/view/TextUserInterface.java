package edu.ntnu.idi.idatt.textuserinterface.view;

import edu.ntnu.idi.idatt.controllers.GameController;
import edu.ntnu.idi.idatt.entities.Player;
import edu.ntnu.idi.idatt.textuserinterface.utils.InterfaceUtils;
import java.util.ArrayList;
import java.util.List;

public class TextUserInterface {
  GameController gameController;

  public void init() {
    try {
    } catch (Exception e) {
      exitByError(e.getMessage());
    }
  }

  public void start() {
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
    gameController = new GameController();
    gameController.initController(tuiGetPlayers());
  }

  private List<Player> tuiGetPlayers() {
    List<String> playerNames = getPlayerNames();
    List<Player> players = new ArrayList<>();
    for (String name : playerNames) {
      players.add(new Player(name));
    }
    return players;
  }

  private int getPlayerCount() {
    System.out.print("\nEnter player count: ");
    int playerCountInput = InterfaceUtils.integerInput();
    if (playerCountInput < 1) {
      System.out.println("Player count must be greater than 0.");
      playerCountInput = getPlayerCount();
    }
    return playerCountInput;
  }

  private List<String> getPlayerNames() {
    int playerCount = getPlayerCount();
    List<String> playerNames = new ArrayList<>();
    for (int i = 1; i <= playerCount; i++) {
      System.out.printf("%nEnter name of player %d: ", i);
      playerNames.add(InterfaceUtils.stringInput());
    }
    return playerNames;
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

  private void tuiGameClient() {
    System.out.println("Welcome to the ladder game!");
    while (gameController.getWinner() == null) {
      System.out.println("\n-----------------------------------");
      System.out.println("Round number: " + (gameController.getRoundNumber() + 1));
      for (Player player : gameController.getPlayers()) {
        gameController.playMove();
        System.out.println(player.getName() + " is on tile " + player.getCurrentTile().getTileId());
      }
    }
    System.out.printf(
        """
        
        ------------------------------------------------------------
            The winner of the game is: %s  (after %d rounds)!
        ------------------------------------------------------------
        
        """, gameController.getWinner().getName(), gameController.getRoundNumber());

  }
}


