package edu.ntnu.idi.idatt.view.container;

import edu.ntnu.idi.idatt.model.board.Board;
import edu.ntnu.idi.idatt.model.player.Player;
import edu.ntnu.idi.idatt.observer.ButtonClickObserver;
import edu.ntnu.idi.idatt.observer.ButtonClickSubject;
import edu.ntnu.idi.idatt.view.component.GameBoardStackPane;
import edu.ntnu.idi.idatt.view.component.GameMenuBox;
import edu.ntnu.idi.idatt.view.component.GamePlayersBox;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class LadderGameView extends HBox implements ButtonClickSubject {
  private final List<ButtonClickObserver> observers;

  private GamePlayersBox playersBox;
  private GameBoardStackPane boardStackPane;
  private GameMenuBox gameMenuBox;

  public LadderGameView() {
    observers = new ArrayList<>();

    this.getStyleClass().add("game-view");
  }

  public void initialize(List<Player> players, int roundNumber, Board board) {
    this.playersBox = createPlayersBox(players, roundNumber);
    this.boardStackPane = createBoardStackPane(board, players);
    this.gameMenuBox = createGameMenuBox();

    this.getChildren().setAll(playersBox, createInfiniteSpacer(), boardStackPane, createInfiniteSpacer(),
        gameMenuBox);
  }

  public GamePlayersBox getPlayersBox() {
    return playersBox;
  }

  public GameBoardStackPane getBoardStackPane() {
    return boardStackPane;
  }

  public GameMenuBox getGameMenuBox() {
    return gameMenuBox;
  }

  public GamePlayersBox createPlayersBox(List<Player> players, int roundNumber) {
    return new GamePlayersBox(players, roundNumber);
  }

  public Region createInfiniteSpacer() {
    Region spacer = new Region();
    HBox.setHgrow(spacer, Priority.ALWAYS);
    VBox.setVgrow(spacer, Priority.ALWAYS);
    return spacer;
  }

  public GameBoardStackPane createBoardStackPane(Board board, List<Player> players) {
    return new GameBoardStackPane(board, players);
  }

  public GameMenuBox createGameMenuBox() {
    GameMenuBox box = new GameMenuBox();
    box.setOnRestartGame(() -> notifyObservers("restart_game"));
    box.setOnQuitGame(() -> notifyObservers("quit_game"));
    box.setOnRollDice(() -> notifyObservers("roll_dice"));
    return box;
  }

  @Override
  public void addObserver(ButtonClickObserver observer) {
    observers.add(observer);
  }

  @Override
  public void removeObserver(ButtonClickObserver observer) {
    observers.remove(observer);
  }

  @Override
  public void notifyObservers(String buttonId) {
    observers.forEach(observer ->
        observer.onButtonClicked(buttonId));
  }

  @Override
  public void notifyObserversWithParams(String buttonId, Map<String, Object> params) {
    // Not needed
  }
}
