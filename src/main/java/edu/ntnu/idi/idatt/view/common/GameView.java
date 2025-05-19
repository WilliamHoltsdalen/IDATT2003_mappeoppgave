package edu.ntnu.idi.idatt.view.common;

import edu.ntnu.idi.idatt.controller.common.GameController;
import edu.ntnu.idi.idatt.model.board.Board;
import edu.ntnu.idi.idatt.model.player.Player;
import edu.ntnu.idi.idatt.observer.ButtonClickObserver;
import edu.ntnu.idi.idatt.observer.ButtonClickSubject;
import edu.ntnu.idi.idatt.view.component.GameMenuBox;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * <h3>GameView.</h3>
 *
 * <p>Abstract JavaFX {@link HBox} serving as the main container for an active game screen.
 * It typically arranges three main sections: a {@link GamePlayersBox} for player information,
 * a {@link GameStackPane} for the game board and pieces, and a {@link GameMenuBox} for game
 * controls (like rolling dice, restarting, or quitting).</p>
 *
 * <p>This class implements {@link ButtonClickSubject} to notify observers (usually a
 * {@link GameController}) of button clicks from the {@link GameMenuBox}.</p>
 *
 * @see HBox
 * @see ButtonClickSubject
 * @see GamePlayersBox
 * @see GameStackPane
 * @see GameMenuBox
 */
public abstract class GameView extends HBox implements ButtonClickSubject {
  protected final List<ButtonClickObserver> observers;
  
  protected GamePlayersBox playersBox;
  protected GameStackPane gameStackPane;
  protected GameMenuBox gameMenuBox;

  /**
   * Constructs a GameView, initializing the list for observers.
   */
  public GameView() {
    this.observers = new ArrayList<>();

    this.getStylesheets().add("stylesheets/gameViewStyles.css");
    this.getStyleClass().add("game-view");
  }

  /**
   * Initializes the main components of the game view: the player information box,
   * the game board area, and the game menu/controls box.
   *
   * @param players A list of {@link Player}s in the game.
   * @param roundNumber The initial round number.
   * @param board The {@link Board} model for the game.
   */
  public void initialize(List<Player> players, int roundNumber, Board board) {
    this.playersBox = createPlayersBox(players, roundNumber);
    this.gameStackPane = createGameStackPane(board, players);
    this.gameMenuBox = createGameMenuBox();

    this.getChildren().setAll(playersBox, createInfiniteSpacer(), gameStackPane,
        createInfiniteSpacer(), gameMenuBox);
  }

  /**
   * Creates and returns the {@link GamePlayersBox} to display player information.
   * Subclasses must implement this to provide a game-specific player box.
   *
   * @param players A list of {@link Player}s to display.
   * @param roundNumber The initial round number to display.
   * @return The created {@link GamePlayersBox}.
   */
  public abstract GamePlayersBox createPlayersBox(List<Player> players, int roundNumber);

  /**
   * Creates and returns the {@link GameStackPane} that will display the game board and pieces.
   * Subclasses must implement this to provide a game-specific board display area.
   *
   * @param board The {@link Board} model for the game.
   * @param players A list of {@link Player}s in the game (for placing pieces).
   * @return The created {@link GameStackPane}.
   */
  public abstract GameStackPane createGameStackPane(Board board, List<Player> players);

  /**
   * Gets the {@link GamePlayersBox} used in this view.
   *
   * @return The player information box.
   */
  public GamePlayersBox getPlayersBox() {
    return playersBox;
  }

  /**
   * Gets the {@link GameStackPane} used in this view.
   *
   * @return The game board display area.
   */
  public GameStackPane getGameStackPane() {
    return gameStackPane;
  }

  /**
   * Gets the {@link GameMenuBox} containing game controls.
   *
   * @return The game menu box.
   */
  public GameMenuBox getGameMenuBox() {
    return gameMenuBox;
  }

  /**
   * Creates a {@link Region} that will grow to fill available space, used as a spacer
   * in HBox or VBox layouts.
   *
   * @return The created spacer region.
   */
  protected Region createInfiniteSpacer() {
    Region spacer = new Region();
    HBox.setHgrow(spacer, Priority.ALWAYS);
    VBox.setVgrow(spacer, Priority.ALWAYS);
    return spacer;
  }

  /**
   * Creates and configures the {@link GameMenuBox} with standard game actions
   * (restart, quit, roll dice) and links them to notify observers.
   *
   * @return The configured {@link GameMenuBox}.
   */
  public GameMenuBox createGameMenuBox() {
    GameMenuBox box = new GameMenuBox(1);
    box.setOnRestartGame(() -> notifyObservers("restart_game"));
    box.setOnQuitGame(() -> notifyObservers("quit_game"));
    box.setOnRollDice(() -> notifyObservers("roll_dice"));
    return box;
  }
  
  /**
   * Adds a {@link ButtonClickObserver} to be notified of button clicks.
   *
   * @param observer The observer to add.
   */
  @Override
  public void addObserver(ButtonClickObserver observer) {
    observers.add(observer);
  }

  /**
   * Removes a {@link ButtonClickObserver}.
   *
   * @param observer The observer to remove.
   */
  @Override
  public void removeObserver(ButtonClickObserver observer) {
    observers.remove(observer);
  }

  /**
   * Notifies all registered observers of a button click event.
   *
   * @param buttonId The ID of the button that was clicked.
   */
  @Override
  public void notifyObservers(String buttonId) {
    observers.forEach(observer ->
        observer.onButtonClicked(buttonId));
  }

  /**
   * Notifies observers of a button click event with parameters. Currently not used in GameView.
   *
   * @param buttonId The ID of the button that was clicked.
   * @param params A map of parameters associated with the button click.
   */
  @Override
  public void notifyObserversWithParams(String buttonId, Map<String, Object> params) {
    // Not needed
  }
}
