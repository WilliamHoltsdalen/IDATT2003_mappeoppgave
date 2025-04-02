package edu.ntnu.idi.idatt.controller;

import edu.ntnu.idi.idatt.factory.PlayerFactory;
import edu.ntnu.idi.idatt.view.gui.component.MainMenuPlayerRow;
import edu.ntnu.idi.idatt.view.gui.container.AppView;
import edu.ntnu.idi.idatt.view.gui.container.MainMenuView;
import java.io.IOException;
import java.util.List;

public class MainMenuController {
  private final AppView appView;
  private final MainMenuView mainMenuView;

  /**
   * Constructor for MenuController class.
   */
  public MainMenuController(AppView appView, MainMenuView mainMenuView) {
    this.appView = appView;
    this.mainMenuView = mainMenuView;

    init();
  }

  /**
   * Initializes the main menu controller.
   */
  private void init() {
    mainMenuView.setOnStartGame(this::handleStartGame);
    mainMenuView.setOnImportPlayers(this::loadPlayersFromFile);
  }

  /**
   * Handles the action of the 'start game' button in the main menu.
   */
  private void handleStartGame() {
    List<MainMenuPlayerRow> playerRows = mainMenuView.getPlayerRows();

    playerRows.forEach(playerRow -> System.out.println(playerRow.getName() + " is " + playerRow.getColor()));

    appView.showGameView();
  }

  /**
   * Loads the players from the file at the given path and adds them to the main menu.
   *
   * @see PlayerFactory#createPlayersFromFile(String)
   * @param filePath The path to the file containing the players.
   */
  public void loadPlayersFromFile(String filePath) {
    try {
      mainMenuView.importPlayers(PlayerFactory.createPlayersFromFile(filePath));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
