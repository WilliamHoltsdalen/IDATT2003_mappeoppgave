package edu.ntnu.idi.idatt.view.gui;

import edu.ntnu.idi.idatt.controller.MainMenuController;
import edu.ntnu.idi.idatt.view.gui.component.PlayerRow;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class MainMenuView extends VBox {
  private MainMenuController controller;
  private final List<PlayerRow> playerRows;
  private final Text title;
  private final VBox playerListBox;
  private final HBox addPlayerButtonsBox;
  private final Button startGameButton;

  public MainMenuView() {
    this.playerRows = new ArrayList<>();
    this.title = new Text();
    this.playerListBox = new VBox();
    this.addPlayerButtonsBox = new HBox();
    this.startGameButton = new Button();

    this.getStyleClass().add("main-menu-view");
    VBox.setVgrow(this, Priority.NEVER);

    initialize();
  }

  public VBox getView() {
    return this;
  }

  private void initialize() {
    title.setText("Main Menu");
    title.getStyleClass().add("main-menu-title");

    playerListBox.getStyleClass().add("player-list-box");
    addPlayerRow("Player 1", Color.RED, false);
    addPlayerRow("Player 2", Color.GREEN, true);

    Button addPlayerButton = new Button("Add Player");
    addPlayerButton.setOnAction(event -> addPlayerRow(
        "Player " + (playerRows.size() + 1), Color.BLUE, true));

    Button addBotButton = new Button("Add Bot");
    addBotButton.setOnAction(event -> addPlayerRow(
        "Bot " + (playerRows.size() + 1), Color.YELLOW, true));

    addPlayerButtonsBox.getChildren().addAll(addPlayerButton, addBotButton);
    addPlayerButtonsBox.getStyleClass().add("main-menu-add-player-buttons-box");

    startGameButton.setText("Start Game");
    startGameButton.setOnAction(event -> System.out.println("Start game"));

    this.getChildren().setAll(title, playerListBox, addPlayerButtonsBox, startGameButton);
  }

  private void addPlayerRow(String defaultName, Color color, boolean removable) {
    PlayerRow playerRow = new PlayerRow(defaultName, color, removable);
    playerRow.setRunnable(() -> removePlayerRow(playerRow));
    playerRows.add(playerRow);
    playerListBox.getChildren().add(playerRow);

    if (playerRows.size() == 5) {
      this.getChildren().remove(addPlayerButtonsBox);
    }
  }

  private void removePlayerRow(PlayerRow playerRow) {
    playerRows.remove(playerRow);
    playerListBox.getChildren().remove(playerRow);

    if (playerRows.size() < 5) {
      this.getChildren().setAll(title, playerListBox, addPlayerButtonsBox, startGameButton);
    }
  }


  public List<PlayerRow> getPlayerRows() {
    return playerRows;
  }
}
