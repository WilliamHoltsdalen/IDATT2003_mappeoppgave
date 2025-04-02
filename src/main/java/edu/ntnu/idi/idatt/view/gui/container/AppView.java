package edu.ntnu.idi.idatt.view.gui.container;

import edu.ntnu.idi.idatt.controller.MainMenuController;
import javafx.scene.layout.StackPane;

public class AppView extends StackPane {
  MainMenuView mainMenuView;
  MainMenuController mainMenuController;
  GameView gameView;

  public AppView() {
    mainMenuView = new MainMenuView();
    mainMenuController = new MainMenuController(this, mainMenuView);
    gameView = new GameView();

    this.getStyleClass().add("app-view");
  }

  public void showMainMenu() {
    this.getChildren().setAll(mainMenuView.getView());
  }

  public void showGameView() {
    this.getChildren().setAll(gameView.getView());
  }
}
