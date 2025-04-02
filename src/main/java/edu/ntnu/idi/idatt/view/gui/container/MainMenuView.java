package edu.ntnu.idi.idatt.view.gui.container;

import edu.ntnu.idi.idatt.controller.MainMenuController;
import edu.ntnu.idi.idatt.view.gui.component.MainMenuPlayerRow;
import java.util.ArrayList;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class MainMenuView extends VBox {
  private MainMenuController controller;
  private final List<MainMenuPlayerRow> mainMenuPlayerRows;
  private final Text title;
  private final VBox playerListBox;
  private final HBox addPlayerButtonsBox;
  private final Button startGameButton;

  public MainMenuView() {
    this.mainMenuPlayerRows = new ArrayList<>();
    this.title = new Text();
    this.playerListBox = new VBox();
    this.addPlayerButtonsBox = new HBox();
    this.startGameButton = new Button();

    this.getStyleClass().add("main-menu-view");

    initialize();
  }

  public VBox getView() {
    return this;
  }

  private void initialize() {
    title.setText("Main Menu");
    title.getStyleClass().add("main-menu-title");

    playerListBox.getStyleClass().add("main-menu-player-list-box");
    addPlayerRow("Player 1", Color.RED, false);
    addPlayerRow("Player 2", Color.GREEN, true);

    Button addPlayerButton = new Button("Add Player");
    addPlayerButton.setOnAction(event -> addPlayerRow(
        "Player " + (mainMenuPlayerRows.size() + 1), Color.BLUE, true));
    addPlayerButton.getStyleClass().add("main-menu-add-player-button");

    Button addBotButton = new Button("Add Bot");
    addBotButton.setOnAction(event -> addPlayerRow(
        "Bot " + (mainMenuPlayerRows.size() + 1), Color.PURPLE, true));
    addBotButton.getStyleClass().add("main-menu-add-player-button");

    addPlayerButtonsBox.getChildren().addAll(addPlayerButton, addBotButton);
    addPlayerButtonsBox.getStyleClass().add("main-menu-add-player-buttons-box");

    startGameButton.setText("Start Game");
    startGameButton.setOnAction(event -> System.out.println("Start game"));

    this.getChildren().setAll(title, playerListBox, addPlayerButtonsBox, startGameButton);
  }

  private void addPlayerRow(String defaultName, Color color, boolean removable) {
    MainMenuPlayerRow mainMenuPlayerRow = new MainMenuPlayerRow(defaultName, color, removable);
    mainMenuPlayerRow.setRunnable(() -> removePlayerRow(mainMenuPlayerRow));
    mainMenuPlayerRows.add(mainMenuPlayerRow);
    playerListBox.getChildren().add(mainMenuPlayerRow);

    updateControls();
  }

  private void removePlayerRow(MainMenuPlayerRow mainMenuPlayerRow) {
    mainMenuPlayerRows.remove(mainMenuPlayerRow);
    playerListBox.getChildren().remove(mainMenuPlayerRow);

    updateControls();
  }

  private void updateControls() {
    if (mainMenuPlayerRows.size() == 5) {
      this.getChildren().remove(addPlayerButtonsBox);
    } else if (mainMenuPlayerRows.size() < 5) {
      this.getChildren().setAll(title, playerListBox, addPlayerButtonsBox, startGameButton);
    }

    if (mainMenuPlayerRows.size() == 1) {
      disableStartGameButton();
    } else {
      enableStartGameButton();
    }
  }

  private void disableStartGameButton() {
    startGameButton.setOnAction(null);
    startGameButton.getStyleClass().add("button-disabled");
    Tooltip tooltip = new Tooltip("You need at least two players.");
    tooltip.setShowDelay(Duration.millis(0));
    Tooltip.install(startGameButton, tooltip);
  }

  private void enableStartGameButton() {
    startGameButton.setOnAction(handleStartGameButtonAction());
    startGameButton.getStyleClass().remove("button-disabled");
    Tooltip.uninstall(startGameButton, startGameButton.getTooltip());
  }

  private EventHandler<ActionEvent> handleStartGameButtonAction() {
    return event -> System.out.println("Start game");
  }

  public List<MainMenuPlayerRow> getPlayerRows() {
    return mainMenuPlayerRows;
  }
}
