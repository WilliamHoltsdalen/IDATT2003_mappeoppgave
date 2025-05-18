package edu.ntnu.idi.idatt.view.common;

import edu.ntnu.idi.idatt.model.board.Board;
import edu.ntnu.idi.idatt.model.player.Player;
import edu.ntnu.idi.idatt.observer.ButtonClickObserver;
import edu.ntnu.idi.idatt.observer.ButtonClickSubject;
import edu.ntnu.idi.idatt.view.component.GameMenuBox;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.geometry.Insets;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

public abstract class GameView extends BorderPane implements ButtonClickSubject {
  private static final double GAME_BOARD_PADDING = 25;
  protected final List<ButtonClickObserver> observers;
  
  protected GamePlayersBox playersBox;
  protected Pane centerStackPane;
  protected GameStackPane gameStackPane;
  protected GameMenuBox gameMenuBox;

  protected GameView() {
    this.observers = new ArrayList<>();

    this.getStylesheets().add("stylesheets/gameViewStyles.css");
    this.getStyleClass().add("game-view");
  }

  public void initialize(List<Player> players, int roundNumber, Board board) {
    this.playersBox = createPlayersBox(players, roundNumber);

    this.centerStackPane = new StackPane();
    this.centerStackPane.maxWidthProperty().bind(this.widthProperty().multiply(0.55));
    this.centerStackPane.setPadding(new Insets(GAME_BOARD_PADDING));
    DoubleBinding centerContentWidthProperty = Bindings.createDoubleBinding(() -> {
      double paneWidth = centerStackPane.widthProperty().get();
      return Math.max(0, paneWidth - (GAME_BOARD_PADDING * 2));
    }, centerStackPane.widthProperty());
    this.gameStackPane = createGameStackPane(board, players, centerContentWidthProperty);
    this.centerStackPane.getChildren().add(gameStackPane);

    this.gameMenuBox = createGameMenuBox();

    this.setLeft(playersBox);
    this.setCenter(centerStackPane);
    this.setRight(gameMenuBox);
  }

  public abstract GamePlayersBox createPlayersBox(List<Player> players, int roundNumber);

  public abstract GameStackPane createGameStackPane(Board board, List<Player> players, DoubleBinding observableWidth);

  public GamePlayersBox getPlayersBox() {
    return playersBox;
  }

  public GameStackPane getGameStackPane() {
    return gameStackPane;
  }

  public GameMenuBox getGameMenuBox() {
    return gameMenuBox;
  }

  public GameMenuBox createGameMenuBox() {
    GameMenuBox box = new GameMenuBox(1);
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
