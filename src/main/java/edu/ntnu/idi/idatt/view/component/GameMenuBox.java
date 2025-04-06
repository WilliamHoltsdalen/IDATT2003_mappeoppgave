package edu.ntnu.idi.idatt.view.component;

import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

/**
 * <h3>GameMenuBox class</h3>
 *
 * <p>This class extends the VBox class. It is used to display the game menu box
 * in the game view. It contains buttons for restarting and quitting the game,
 * a scroll pane for the game log, and controls for playing the game such as rolling
 * the dice.
 */
public class GameMenuBox extends VBox {
  private final ScrollPane gameLogScrollPane;
  private final VBox gameLogRoundBoxesVBox;
  private final CheckBox rollForAllPlayersCheckBox;

  private Runnable onRestartGame = null;
  private Runnable onQuitGame = null;
  private Runnable onRollDice = null;

  /**
   * Constructor for the GameMenuBox class. Initializes the game menu box by creating
   * all the components and initializing the game menu box by calling the
   * {@link #initialize()} method.
   */
  public GameMenuBox() {
    gameLogScrollPane = new ScrollPane();
    gameLogRoundBoxesVBox = new VBox();
    rollForAllPlayersCheckBox = new CheckBox();

    this.getStyleClass().add("game-menu-box");
    VBox.setVgrow(gameLogScrollPane, Priority.ALWAYS);
    VBox.setVgrow(this, Priority.ALWAYS);
    initialize();
  }

  /**
   * Initializes the game menu box by creating all the components.
   */
  private void initialize() {
    // Upper part of the menu, containing the buttons
    Button restartGameButton = new Button("Restart game");
    restartGameButton.getStyleClass().add("game-menu-restart-button");
    restartGameButton.setOnAction(event -> {
      if (onRestartGame != null) {
        onRestartGame.run();
      }
    });

    Button quitGameButton = new Button("Quit game");
    quitGameButton.getStyleClass().add("game-menu-quit-button");
    quitGameButton.setOnAction(event -> {
      if (onQuitGame != null) {
        onQuitGame.run();
      }
    });

    HBox buttonsHBox = new HBox(restartGameButton, quitGameButton);
    buttonsHBox.getStyleClass().add("game-menu-buttons-h-box");

    HorizontalDivider horizontalDividerTop = new HorizontalDivider();

    VBox menuTopBox = new VBox(buttonsHBox, horizontalDividerTop);
    menuTopBox.getStyleClass().add("game-menu-top-box");

    // Middle part of the menu, containing the game log
    gameLogRoundBoxesVBox.getStyleClass().add("game-menu-game-log-round-boxes-vbox");
    gameLogScrollPane.setContent(gameLogRoundBoxesVBox);
    gameLogScrollPane.getStyleClass().add("game-menu-game-log-scroll-pane");
    gameLogScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
    gameLogScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
    gameLogRoundBoxesVBox.heightProperty().addListener(observable ->
        gameLogScrollPane.setVvalue(1.0));
    addGameLogRoundBox(1);

    // Lower part of the menu, containing the controls for rolling the dice
    HorizontalDivider horizontalDividerBottom = new HorizontalDivider();

    Button rollDiceButton = new Button("Roll dice");
    rollDiceButton.getStyleClass().add("game-menu-roll-dice-button");
    rollDiceButton.setOnAction(event -> {
      if (onRollDice != null) {
        onRollDice.run();
      }
    });

    Text rollForAllPlayersText = new Text("Roll for all players");
    rollForAllPlayersText.getStyleClass().add("game-menu-roll-for-all-players-text");

    rollForAllPlayersCheckBox.getStyleClass().add("game-menu-roll-for-all-players-check-box");

    HBox rollForAllPlayersHBox = new HBox(rollForAllPlayersText, rollForAllPlayersCheckBox);
    rollForAllPlayersHBox.getStyleClass().add("game-menu-roll-for-all-players-box");

    VBox menuBottomBox = new VBox(horizontalDividerBottom, rollDiceButton, rollForAllPlayersHBox);
    menuBottomBox.getStyleClass().add("game-menu-bottom-box");

    this.getChildren().setAll(menuTopBox, gameLogScrollPane, menuBottomBox);
  }

  /**
   * Adds a new round box to the game log. The round number is added to the text
   * of the round box.
   *
   * @param roundNumber The round number of the round box to add.
   */
  public void addGameLogRoundBox(int roundNumber) {
    Text roundBoxRoundNumberText = new Text("Round " + roundNumber);
    roundBoxRoundNumberText.getStyleClass().add("game-menu-game-log-round-number");

    VBox gameLogRoundBox = new VBox(roundBoxRoundNumberText);
    HBox.setHgrow(gameLogRoundBox, Priority.ALWAYS);
    gameLogRoundBox.getStyleClass().add("game-menu-game-log-round-box");
    gameLogRoundBoxesVBox.getChildren().add(gameLogRoundBox);
  }

  /**
   * Adds a new text entry (line) to the box for the current, latest, round in the
   * game log. The text will be wrapped to fit the width of the box.
   *
   * @param text The text to add.
   */
  public void addGameLogRoundBoxEntry(String text) {
    Text entryText = new Text(text);
    entryText.getStyleClass().add("game-menu-game-log-round-box-entry");
    entryText.setWrappingWidth(200);

    VBox roundBox = (VBox) gameLogRoundBoxesVBox.getChildren().getLast();
    roundBox.getChildren().add(entryText);
  }

  /**
   * Sets the callback for the 'restart game' button.
   *
   * @param onRestartGame The callback to set.
   */
  public void setOnRestartGame(Runnable onRestartGame) {
    this.onRestartGame = onRestartGame;
  }

  /**
   * Sets the callback for the 'quit game' button.
   *
   * @param onQuitGame The callback to set.
   */
  public void setOnQuitGame(Runnable onQuitGame) {
    this.onQuitGame = onQuitGame;
  }

  /**
   * Sets the callback for the 'roll dice' button.
   *
   * @param onRollDice The callback to set.
   */
  public void setOnRollDice(Runnable onRollDice) {
    this.onRollDice = onRollDice;
  }

  /**
   * Returns whether the 'roll for all players' checkbox is selected.
   *
   * @return Whether the 'roll for all players' checkbox is selected.
   */
  public boolean getRollForAllPlayersSelected() {
    return rollForAllPlayersCheckBox.isSelected();
  }
}
