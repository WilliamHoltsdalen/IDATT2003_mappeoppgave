package edu.ntnu.idi.idatt.textuserinterface.view;

import edu.ntnu.idi.idatt.controllers.GameController;
import edu.ntnu.idi.idatt.factory.PlayerFactory;
import edu.ntnu.idi.idatt.model.Board;
import edu.ntnu.idi.idatt.model.Player;
import edu.ntnu.idi.idatt.textuserinterface.utils.InterfaceUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
          userSelectBoard();
          userSelectPlayers();
          tuiGameClient();
          break;
        case 0:
          finished = true;
          break;
        default:
          System.out.println("Invalid choice. Please try again.");
          break;
      }
    }
  }

  /**
   * Asks the user to select a board variant, and then sets the game to that board by calling the
   * appropriate method in the {@link GameController} class.
   */
  private void userSelectBoard() {
    Map<Integer, Board> boardVariants = gameController.getBoardVariants();

    StringBuilder prompt = new StringBuilder();
    prompt.append("\nAvailable board variants");
    prompt.append("\n------------------------");
    for (Map.Entry<Integer, Board> entry : boardVariants.entrySet()) {
      prompt.append("\n").append(entry.getKey()).append(". ").append(entry.getValue().getName());
    }
    prompt.append("\n------------------------");
    prompt.append("\nPlease select a board variant: ");

    System.out.println(prompt);

    int boardVariantIndex = 0;
    do {
      int choice = InterfaceUtils.integerInput();
      if (boardVariants.containsKey(choice)) {
        boardVariantIndex = choice;
      } else {
        InterfaceUtils.printErrorMessage("Invalid choice. Please try again.");
      }
    } while (boardVariantIndex == 0);

    gameController.setBoardVariant(boardVariantIndex);
  }

  /**
   * Asks the user to select between creating or importing players, and then calls the appropriate
   * methods to handle the selection.
   */
  private void userSelectPlayers() {
    StringBuilder createOrImportPrompt = new StringBuilder();
    createOrImportPrompt.append("\nWould you like to import or create players?")
        .append("\n1. Create players")
        .append("\n2. Import players")
        .append("\nEnter your choice: ");
    System.out.println(createOrImportPrompt);

    int choice = InterfaceUtils.integerInput();
    switch (choice) {
      case 1:
        userCreatePlayers();
        break;
      case 2:
        userImportPlayers();
        break;
      default:
        System.out.println("Invalid choice. Please try again.");
        userSelectPlayers();
        break;
    }
  }

  /**
   * Asks the user to create new players, and then adds them to the list of players, which is then
   * passed to the {@link GameController} class to be added to the game.
   */
  private void userCreatePlayers() {
    List<Player> players = new ArrayList<>();

    boolean finished = false;
    while (!finished) {
      System.out.println("\nPlease enter the name of player #" + (players.size() + 1) + ": ");
      String playerName = InterfaceUtils.stringInput();
      if (playerName.isBlank()) {
        continue;
      }

      System.out.println("\nPlease enter the hex color of player #" + (players.size() + 1) + ": ");
      String playerColor = InterfaceUtils.stringInput();
      if (playerColor == null || playerColor.isBlank()) {
        System.out.println("Please enter a valid hex color.");
        continue;
      }

      Player player = new Player(playerName, playerColor);
      players.add(player);

      System.out.println("Would you like to add another player? (y/n): ");
      String choice = InterfaceUtils.stringInput();
      if (choice.equalsIgnoreCase("n")) {
        finished = true;
      }
    }
    players.addAll(userCreateBots());
    gameController.setPlayers(players);
  }

  /**
   * Asks the user if they would like to create any bots, and then creates them and adds them to the
   * list of players. If the user does not want to create any bots, an empty list is returned.
   *
   * @return A list of bot players, or an empty list if the user does not want to create any bots.
   */
  private List<Player> userCreateBots() {
    System.out.println("\nWould you like to create any bots? (y/n): ");
    String choice = InterfaceUtils.stringInput();
    if (choice.equalsIgnoreCase("n")) {
      return List.of();
    }

    List<Player> botPlayers = new ArrayList<>();
    boolean finished = false;
    while (!finished) {
      Player botPlayer = PlayerFactory.createBot(String.valueOf(botPlayers.size() + 1));
      botPlayers.add(botPlayer);
      System.out.println("Added bot with name: '" + botPlayer.getName() + "' with color hex: " + botPlayer.getColorHex());

      System.out.println("\nWould you like to add another bot? (y/n)");
      choice = InterfaceUtils.stringInput();
      if (choice.equalsIgnoreCase("y")) {
        continue;
      }
      finished = true;
    }
    return botPlayers;
  }

  /**
   * Asks the user to enter the path to a csv file containing players, and then calls the
   * appropriate method in the {@link GameController} class to load the players from the file. A
   * message is printed to the console to confirm that the players were loaded successfully, or
   * that an error occurred. If an error occurs, the user can choose to try again. If the user
   * does not want to try again, the method calls the {@link #userSelectPlayers()} method again to
   * restart the player selection/creation process.
   */
  private void userImportPlayers() {
    System.out.println("Enter the path to the csv file containing the players: ");
    String filePath = InterfaceUtils.stringInput();
    if (gameController.loadPlayersFromFile(filePath)) {
      System.out.println("Players successfully loaded from file.");
      return;
    }
    System.out.println("Failed to load players from file. Try again? (y/n): ");
    String choice = InterfaceUtils.stringInput();
    if (choice.equalsIgnoreCase("y")) {
      userImportPlayers();
      return;
    }
    userSelectPlayers();
  }

  /**
   * Prints a message to the console indicating the winner of the game and the number of rounds
   * played.
   */
  private void printWinner() {
    StringBuilder winnerMessage = new StringBuilder();
    winnerMessage.append("\n------------------------------------------------------------");
    winnerMessage.append("\n    The winner of the game is: ")
        .append(gameController.getWinner().getName()).append("  (after ")
        .append(gameController.getRoundNumber()).append(" rounds)!");
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
