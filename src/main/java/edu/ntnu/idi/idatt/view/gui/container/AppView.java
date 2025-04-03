package edu.ntnu.idi.idatt.view.gui.container;

import edu.ntnu.idi.idatt.controller.GameController;
import edu.ntnu.idi.idatt.controller.MainMenuController;
import edu.ntnu.idi.idatt.model.Board;
import edu.ntnu.idi.idatt.model.Player;
import java.util.List;
import javafx.scene.layout.StackPane;

public class AppView extends StackPane {
  MainMenuView mainMenuView;
  MainMenuController mainMenuController;
  GameController gameController;
  GameView gameView;

  public AppView() {
    this.getStyleClass().add("app-view");
  }

  public void showMainMenu() {
    mainMenuView = new MainMenuView();
    mainMenuController = new MainMenuController(mainMenuView);
    mainMenuController.setOnStartGame(this::showGameView);

    this.getChildren().setAll(mainMenuView.getView());
  }

  public void showGameView(Board board, List<Player> players) {
    gameController = new GameController(board, players);
    gameController.setOnRestartGame(this::showGameView);
    gameController.setOnQuitGame(this::showMainMenu);

    gameView = new GameView(gameController);
    this.getChildren().setAll(gameView.getView());
  }
}
