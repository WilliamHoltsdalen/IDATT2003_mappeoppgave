package edu.ntnu.idi.idatt.textuserinterface.view;

import edu.ntnu.idi.idatt.controllers.GameController;
import edu.ntnu.idi.idatt.textuserinterface.utils.InterfaceUtils;

public class TextUserInterface {
  GameController gameController;

  public void init() {
    gameController = new GameController();
  }

  public void start() {
    try {
      gameController.startGame();
      InterfaceUtils.printWelcomeMessage();
    } catch (Exception e) {
      exitByError(e.getMessage());
    }

    System.out.println("Exiting application...");
    InterfaceUtils.exitApplication();
  }

  public void exitByError(String errorMessage) {
    System.out.println("An error occurred: " + errorMessage);
    System.out.println("Exiting application...");
    InterfaceUtils.exitApplication();
  }
}


