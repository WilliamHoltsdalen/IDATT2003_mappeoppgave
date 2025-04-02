package edu.ntnu.idi.idatt.view.gui.container;

import edu.ntnu.idi.idatt.model.Player;
import edu.ntnu.idi.idatt.view.gui.component.MainMenuPlayerRow;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import org.kordamp.ikonli.javafx.FontIcon;

public class MainMenuView extends VBox {
  private Runnable onStartGame;
  private Consumer<String> onImportPlayers;
  private final List<MainMenuPlayerRow> mainMenuPlayerRows;
  private final HBox headerBox;
  private final VBox playerListBox;
  private final HBox addPlayerButtonsBox;
  private final Button startGameButton;

  /**
   * Constructor for MainMenuView class.
   */
  public MainMenuView() {
    this.mainMenuPlayerRows = new ArrayList<>();
    this.headerBox = new HBox();
    this.playerListBox = new VBox();
    this.addPlayerButtonsBox = new HBox();
    this.startGameButton = new Button();

    this.getStyleClass().add("main-menu-view");

    initialize();
  }

  /**
   * Returns the main menu view.
   *
   * @return The main menu view.
   */
  public VBox getView() {
    return this;
  }

  /**
   * Initializes the main menu view.
   */
  private void initialize() {
    Text title = new Text("Main Menu");
    title.getStyleClass().add("main-menu-title");

    MenuButton moreOptionsMenu = new MenuButton("", new FontIcon("fas-bars"));
    moreOptionsMenu.getStyleClass().add("main-menu-more-options-menu");
    MenuItem importPlayersMenuItem = new MenuItem("Import players");
    importPlayersMenuItem.setGraphic(new FontIcon("fas-file-import"));
    moreOptionsMenu.getItems().addAll(importPlayersMenuItem);
    importPlayersMenuItem.setOnAction(event -> handleImportPlayersButtonAction());

    headerBox.getChildren().setAll(title, moreOptionsMenu);
    headerBox.getStyleClass().add("main-menu-header-box");

    playerListBox.getStyleClass().add("main-menu-player-list-box");
    addPlayerRow("Player 1", Color.RED, false);
    addPlayerRow("Player 2", Color.GREEN, true);

    Button addPlayerButton = new Button("Add Player");
    addPlayerButton.setOnAction(event -> addPlayerRow(
        "Player " + (mainMenuPlayerRows.size() + 1), Color.BLUE, true));

    Button addBotButton = new Button("Add Bot");
    addBotButton.setOnAction(event -> addPlayerRow(
        "Bot " + (mainMenuPlayerRows.size() + 1), Color.PURPLE, true));

    addPlayerButtonsBox.getChildren().addAll(addPlayerButton, addBotButton);
    addPlayerButtonsBox.getChildren().forEach(button -> button.getStyleClass()
        .add("main-menu-add-player-button"));
    addPlayerButtonsBox.getStyleClass().add("main-menu-add-player-buttons-box");

    startGameButton.setText("Start Game");
    startGameButton.setOnAction(event -> handleStartGameButtonAction());

    this.getChildren().setAll(headerBox, playerListBox, addPlayerButtonsBox, startGameButton);
  }

  /**
   * Adds a new player row to the main menu with the given name, color, and removable status.
   *
   * @param defaultName The default name of the player.
   * @param color The color of the player.
   * @param removable The removable status of the player.
   */
  private void addPlayerRow(String defaultName, Color color, boolean removable) {
    MainMenuPlayerRow mainMenuPlayerRow = new MainMenuPlayerRow(defaultName, color, removable);
    mainMenuPlayerRow.setRunnable(() -> removePlayerRow(mainMenuPlayerRow));
    mainMenuPlayerRows.add(mainMenuPlayerRow);
    playerListBox.getChildren().add(mainMenuPlayerRow);

    updateControls();
  }

  /**
   * Removes a player row from the main menu.
   *
   * @param mainMenuPlayerRow The player row to remove.
   */
  private void removePlayerRow(MainMenuPlayerRow mainMenuPlayerRow) {
    mainMenuPlayerRows.remove(mainMenuPlayerRow);
    playerListBox.getChildren().remove(mainMenuPlayerRow);

    updateControls();
  }

  /**
   * Updates the controls of the main menu view based on the number of players in the main menu.
   */
  private void updateControls() {
    if (mainMenuPlayerRows.size() == 5) {
      this.getChildren().remove(addPlayerButtonsBox);
    } else if (mainMenuPlayerRows.size() < 5) {
      this.getChildren().setAll(headerBox, playerListBox, addPlayerButtonsBox, startGameButton);
    }

    if (mainMenuPlayerRows.size() < 2) {
      disableStartGameButton();
    } else {
      enableStartGameButton();
    }
  }

  /**
   * Disables the 'start game' button and shows a tooltip when hovering over it that a minimum
   * of two players are required to start the game.
   */
  private void disableStartGameButton() {
    startGameButton.setOnAction(null);
    startGameButton.getStyleClass().add("button-disabled");
    Tooltip tooltip = new Tooltip("You need at least two players.");
    tooltip.setShowDelay(Duration.millis(0));
    Tooltip.install(startGameButton, tooltip);
  }

  /**
   * Enables the 'start game' button and removes the tooltip when hovering over it.
   */
  private void enableStartGameButton() {
    startGameButton.setOnAction(event -> handleStartGameButtonAction());
    startGameButton.getStyleClass().remove("button-disabled");
    Tooltip.uninstall(startGameButton, startGameButton.getTooltip());
  }

  /**
   * Handles the action of the 'start game' button by calling the onStartGame callback if it is
   * not null.
   */
  private void handleStartGameButtonAction() {
    if (onStartGame != null) {
      onStartGame.run();
    }
  }

  /**
   * Handles the action of the 'import players' button by calling the onImportPlayers callback if
   * it is not null. If the file is null, it does nothing.
   */
  private void handleImportPlayersButtonAction() {
    if (onImportPlayers == null) {
      return;
    }

    File file = new FileChooser().showOpenDialog(null);
    if (file == null) {
      return;
    }
    onImportPlayers.accept(file.getAbsolutePath());
  }

  /**
   * Imports the players from the given list of players and adds them to the main menu.
   *
   * @param players The list of players to import.
   */
  public void importPlayers(List<Player> players) {
    mainMenuPlayerRows.clear();
    playerListBox.getChildren().clear();
    players.forEach(player -> addPlayerRow(player.getName(), Color.web(player.getColorHex()),
        !mainMenuPlayerRows.isEmpty()));
  }

  /**
   * Sets the callback for the 'start game' button.
   *
   * @param onStartGame The callback to set.
   */
  public void setOnStartGame(Runnable onStartGame) {
    this.onStartGame = onStartGame;
  }

  /**
   * Sets the callback for the 'import players' button.
   *
   * @param onImportPlayers The callback to set.
   */
  public void setOnImportPlayers(Consumer<String> onImportPlayers) {
    this.onImportPlayers = onImportPlayers;
  }

  /**
   * Returns the list of main menu player rows.
   *
   * @return The list of main menu player rows.
   */
  public List<MainMenuPlayerRow> getPlayerRows() {
    return mainMenuPlayerRows;
  }
}
