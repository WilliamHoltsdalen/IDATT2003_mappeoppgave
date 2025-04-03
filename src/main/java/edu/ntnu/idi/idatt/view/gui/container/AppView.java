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
    mainMenuView = new MainMenuView();
    mainMenuController = new MainMenuController(this, mainMenuView);
    mainMenuController.setOnStartGame(this::showGameView);

    this.getStyleClass().add("app-view");
  }

  public void showMainMenu() {
    this.getChildren().setAll(mainMenuView.getView());
  }

  public void showGameView(List<Player> players, Board board) {
    gameController = new GameController();
    gameController.initialize(board, players);

    gameView = new GameView(this, gameController);
    this.getChildren().setAll(gameView.getView());
  }
}
