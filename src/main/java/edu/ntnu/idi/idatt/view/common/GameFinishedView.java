package edu.ntnu.idi.idatt.view.common;

import edu.ntnu.idi.idatt.model.player.Player;
import edu.ntnu.idi.idatt.observer.ButtonClickObserver;
import edu.ntnu.idi.idatt.observer.ButtonClickSubject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * GameFinishedView
 *
 * <p>This class is responsible for displaying the game finished screen. It shows player rankings
 * and provides options to restart the game or return to the main menu.
 *
 * <p>It extends {@link BorderPane} and implements {@link ButtonClickSubject}.
 */
public class GameFinishedView extends BorderPane implements ButtonClickSubject {

  private static final Logger logger = LoggerFactory.getLogger(GameFinishedView.class);
  private static final String MAIN_MENU_BUTTON_ID = "main_menu";

  private final List<ButtonClickObserver> observers = new ArrayList<>();
  private VBox rankingsBox;

  /**
   * Constructor for the GameFinishedView.
   */
  public GameFinishedView() {
    this.getStylesheets().add("stylesheets/gameFinishedStyles.css");
    this.getStyleClass().add("game-finished-view");
  }

  /**
   * Initializes the game finished view with player rankings.
   *
   * @param rankedPlayers The list of players, sorted by rank (1st, 2nd, etc.).
   */
  public void initialize(List<Player> rankedPlayers) {
    VBox centerContent = new VBox(20);
    centerContent.setAlignment(Pos.CENTER);
    centerContent.getStyleClass().add("content-box");
    centerContent.setPadding(new Insets(20));
    centerContent.setMaxWidth(400);

    Label titleLabel = new Label("Game Finished!");
    titleLabel.getStyleClass().add("title");

    this.rankingsBox = new VBox(10);
    this.rankingsBox.setAlignment(Pos.CENTER_LEFT);
    this.rankingsBox.getStyleClass().add("rankings-box");

    populateRankings(rankedPlayers);

    Button mainMenuButton = new Button("Main Menu");
    mainMenuButton.getStyleClass().add("main-menu-button");
    mainMenuButton.setOnAction(event -> notifyObservers(MAIN_MENU_BUTTON_ID));
    mainMenuButton.setMaxWidth(Double.MAX_VALUE);

    HBox buttonBox = new HBox(20, mainMenuButton);
    buttonBox.setAlignment(Pos.CENTER);

    HBox rankingsBoxWrapper = new HBox(this.rankingsBox);
    rankingsBoxWrapper.setAlignment(Pos.CENTER);

    centerContent.getChildren().addAll(titleLabel, rankingsBoxWrapper, buttonBox);
    this.setCenter(centerContent);

    logger.debug("GameFinishedView initialized successfully");
  }

  private void populateRankings(List<Player> rankedPlayers) {
    rankingsBox.getChildren().clear();
    if (rankedPlayers == null || rankedPlayers.isEmpty()) {
      Label noPlayersLabel = new Label("No ranking information available.");
      noPlayersLabel.getStyleClass().add("ranking-entry");
      rankingsBox.getChildren().add(noPlayersLabel);
      return;
    }

    for (int i = 0; i < rankedPlayers.size(); i++) {
      Player player = rankedPlayers.get(i);
      String rankText = (i + 1) + ".    " + player.getName();
      if (i == 0) {
        rankText = rankText + " 🥇";
      } else if (i == 1) {
        rankText = rankText + " 🥈";
      } else if (i == 2) {
        rankText = rankText + " 🥉";
      }
      Label rankEntry = new Label(rankText);
      rankEntry.getStyleClass().add("ranking-entry");
      rankingsBox.getChildren().add(rankEntry);
    }
  }

  @Override
  public void addObserver(ButtonClickObserver observer) {
    if (!observers.contains(observer)) {
      observers.add(observer);
    }
  }

  @Override
  public void removeObserver(ButtonClickObserver observer) {
    observers.remove(observer);
  }

  @Override
  public void notifyObservers(String buttonId) {
    logger.debug("Button clicked: {}", buttonId);
    for (ButtonClickObserver observer : observers) {
      observer.onButtonClicked(buttonId);
    }
  }

  @Override
  public void notifyObserversWithParams(String buttonId, Map<String, Object> params) {
    logger.debug("Button clicked with params: {}, {}", buttonId, params);
    for (ButtonClickObserver observer : observers) {
      observer.onButtonClickedWithParams(buttonId, params);
    }
  }
} 