package edu.ntnu.idi.idatt.view.common;

import edu.ntnu.idi.idatt.observer.ButtonClickObserver;
import edu.ntnu.idi.idatt.observer.ButtonClickSubject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * GameSelectionView
 *
 * <p>This class is responsible for displaying the game selection view. It displays a list of games
 * and allows the user to select one by clicking on it.
 *
 * <p>It extends {@link BorderPane} and implements {@link ButtonClickSubject}.
 *
 * @see BorderPane
 * @see ButtonClickSubject
 */
public class GameSelectionView extends BorderPane implements ButtonClickSubject {

  private static final Logger logger = LoggerFactory.getLogger(GameSelectionView.class);
  private final List<ButtonClickObserver> observers;
  private VBox contentBox;

  /**
   * Constructor for the GameSelectionView.
   */
  public GameSelectionView() {
    this.observers = new ArrayList<>();
    this.contentBox = new VBox();

    this.getStylesheets().add("stylesheets/gameSelectionStyles.css");
  }

  /**
   * Initializes the game selection view.
   *
   * @param availableGames The list of available games to display.
   */
  public void initialize(List<Pair<String, Image>> availableGames) {
    contentBox = createGameSelectionBox(availableGames);
    contentBox.getStyleClass().add("content-box");

    this.setCenter(contentBox);
    logger.debug("GameSelectionView initialized successfully");
  }

  /**
   * Creates the game selection box.
   *
   * @param availableGames The list of available games to display.
   * @return The game selection box.
   */
  private VBox createGameSelectionBox(List<Pair<String, Image>> availableGames) {
    VBox mainBox = new VBox();
    mainBox.setAlignment(Pos.CENTER);
    mainBox.setSpacing(30);
    mainBox.setMinSize(0, 0);

    Label gameSelectionTitle = new Label("Select a game");
    gameSelectionTitle.getStyleClass().add("title");

    HBox gameSelectionBox = new HBox();
    gameSelectionBox.setAlignment(Pos.CENTER);
    gameSelectionBox.setSpacing(40);
    gameSelectionBox.setMinSize(0, 0);
    VBox.setVgrow(gameSelectionBox, Priority.ALWAYS);

    availableGames.forEach(game -> {
      VBox gameBox = new VBox();
      gameBox.setOnMouseClicked(event -> notifyObservers(game.getKey()));
      gameBox.getStyleClass().add("game-box");
      gameBox.setAlignment(Pos.CENTER);
      gameBox.setSpacing(15);
      gameBox.setMinSize(120, 120);
      gameBox.prefWidthProperty().bind(gameSelectionBox.widthProperty()
          .divide(availableGames.size()).subtract(mainBox.getSpacing() / 2));
      gameBox.prefHeightProperty().bind(gameSelectionBox.heightProperty());

      Pane imagePane = new StackPane();
      imagePane.setMinSize(0, 0);
      imagePane.prefWidthProperty().bind(gameBox.prefWidthProperty().multiply(0.95));
      imagePane.prefHeightProperty().bind(gameBox.prefHeightProperty().multiply(0.75));

      ImageView gameImageView = new ImageView(game.getValue());
      gameImageView.setPreserveRatio(true);
      gameImageView.fitHeightProperty().bind(imagePane.prefHeightProperty());

      imagePane.widthProperty()
          .addListener((obs, oldVal, newVal) -> {
            double imgAspectRatio =
                gameImageView.getImage().getWidth() / gameImageView.getImage().getHeight();
            double paneAspectRatio = newVal.doubleValue() / imagePane.getPrefHeight();
            if (paneAspectRatio < imgAspectRatio) {
              gameImageView.setFitWidth(newVal.doubleValue());
            }
          });

      imagePane.getChildren().add(gameImageView);

      Label gameNameLabel = new Label(game.getKey());
      gameNameLabel.getStyleClass().add("game-name");

      gameBox.getChildren().addAll(imagePane, gameNameLabel);
      gameSelectionBox.getChildren().add(gameBox);
    });

    mainBox.getChildren().addAll(gameSelectionTitle, gameSelectionBox);
    return mainBox;
  }

  /**
   * Adds an observer to the game selection view.
   *
   * @param observer The observer to add.
   */
  @Override
  public void addObserver(ButtonClickObserver observer) {
    observers.add(observer);
  }

  /**
   * Removes an observer from the game selection view.
   *
   * @param observer The observer to remove.
   */
  @Override
  public void removeObserver(ButtonClickObserver observer) {
    observers.remove(observer);
  }

  /**
   * Notifies all observers of a button click.
   *
   * @param buttonId The id of the button that was clicked.
   */
  @Override
  public void notifyObservers(String buttonId) {
    observers.forEach(observer -> observer.onButtonClicked(buttonId));
  }

  /**
   * Notifies all observers of a button click with parameters.
   *
   * @param buttonId The id of the button that was clicked.
   * @param params   The parameters of the button click.
   */
  @Override
  public void notifyObserversWithParams(String buttonId, Map<String, Object> params) {
    observers.forEach(observer -> observer.onButtonClickedWithParams(buttonId, params));
  }
}
