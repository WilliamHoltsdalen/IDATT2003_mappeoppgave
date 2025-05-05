package edu.ntnu.idi.idatt.view.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import edu.ntnu.idi.idatt.model.board.Board;
import edu.ntnu.idi.idatt.model.player.Player;
import edu.ntnu.idi.idatt.observer.ButtonClickObserver;
import edu.ntnu.idi.idatt.observer.ButtonClickSubject;
import edu.ntnu.idi.idatt.view.component.GameMenuBox;
import edu.ntnu.idi.idatt.view.component.GamePlayersBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public abstract class GameView extends HBox implements ButtonClickSubject {
  protected final List<ButtonClickObserver> observers;
  
  protected GamePlayersBox playersBox;
  protected GameStackPane gameStackPane;
  protected GameMenuBox gameMenuBox;

  public GameView() {
    this.observers = new ArrayList<>();

    this.getStyleClass().add("game-view");
  }

  public void initialize(List<Player> players, int roundNumber, Board board) {
    this.playersBox = createPlayersBox(players, roundNumber);
    this.gameStackPane = createGameStackPane(board, players);
    this.gameMenuBox = createGameMenuBox();

    this.getChildren().setAll(playersBox, createInfiniteSpacer(), gameStackPane, createInfiniteSpacer(),
    gameMenuBox);
  }

  public abstract GamePlayersBox createPlayersBox(List<Player> players, int roundNumber);

  public abstract GameStackPane createGameStackPane(Board board, List<Player> players);

  public GamePlayersBox getPlayersBox() {
    return playersBox;
  }

  public GameStackPane getGameStackPane() {
    return gameStackPane;
  }

  public GameMenuBox getGameMenuBox() {
    return gameMenuBox;
  }

  protected Region createInfiniteSpacer() {
    Region spacer = new Region();
    HBox.setHgrow(spacer, Priority.ALWAYS);
    VBox.setVgrow(spacer, Priority.ALWAYS);
    return spacer;
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
