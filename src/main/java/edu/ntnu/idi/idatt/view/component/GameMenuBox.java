package edu.ntnu.idi.idatt.view.component;

import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

/**
 * GameMenuBox.
 *
 * <p>A JavaFX {@link VBox} component that serves as the in-game menu.
 * It provides controls for game actions such as restarting, quitting, and rolling dice. It also
 * includes a game log display and options like toggling dice animation or rolling for all
 * players.</p>
 *
 * @see VBox
 * @see AnimatedDie
 * @see HorizontalDivider
 */
public class GameMenuBox extends VBox {

  private final ScrollPane gameLogScrollPane;
  private final VBox gameLogRoundBoxesVbox;
  private final CheckBox rollForAllPlayersCheckBox;
  private final CheckBox animatedDiceCheckBox;
  private final AnimatedDie[] dice;
  private final HBox diceContainer;
  private Button rollDiceButton;

  private Runnable onRestartGame = null;
  private Runnable onQuitGame = null;
  private Runnable onRollDice = null;

  /**
   * Constructs a GameMenuBox.
   *
   * @param diceCount The number of {@link AnimatedDie} instances to display (max 2).
   */
  public GameMenuBox(int diceCount) {
    gameLogScrollPane = new ScrollPane();
    gameLogRoundBoxesVbox = new VBox();
    rollForAllPlayersCheckBox = new CheckBox();
    animatedDiceCheckBox = new CheckBox();
    animatedDiceCheckBox.setSelected(true); // Default to animated

    // Create dice array with specified count (max 2)
    diceCount = Math.min(diceCount, 2);
    dice = new AnimatedDie[diceCount];
    for (int i = 0; i < diceCount; i++) {
      dice[i] = new AnimatedDie(70);
    }

    // Create container for dice
    diceContainer = new HBox(10); // 10 pixels spacing between dice
    diceContainer.getStyleClass().add("game-menu-dice-container");
    diceContainer.setAlignment(javafx.geometry.Pos.CENTER);
    for (AnimatedDie die : dice) {
      diceContainer.getChildren().add(die);
    }

    this.getStylesheets().add("stylesheets/gameViewStyles.css");
    this.getStyleClass().add("game-menu-box");
    VBox.setVgrow(gameLogScrollPane, Priority.ALWAYS);
    VBox.setVgrow(this, Priority.ALWAYS);
    initialize();
  }

  /**
   * Initializes the components of the game menu box, including buttons, game log area, dice
   * display, and options checkboxes.
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

    HBox buttonsHbox = new HBox(restartGameButton, quitGameButton);
    buttonsHbox.getStyleClass().add("game-menu-buttons-h-box");

    HorizontalDivider horizontalDividerTop = new HorizontalDivider();

    VBox menuTopBox = new VBox(buttonsHbox, horizontalDividerTop);
    menuTopBox.getStyleClass().add("game-menu-top-box");

    // Middle part of the menu, containing the game log
    gameLogRoundBoxesVbox.getStyleClass().add("game-menu-game-log-round-boxes-vbox");
    gameLogScrollPane.setContent(gameLogRoundBoxesVbox);
    gameLogScrollPane.getStyleClass().add("game-menu-game-log-scroll-pane");
    gameLogScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
    gameLogScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
    gameLogRoundBoxesVbox.heightProperty().addListener(observable ->
        gameLogScrollPane.setVvalue(1.0));
    addGameLogRoundBox(1);

    rollDiceButton = new Button("Roll dice");
    rollDiceButton.getStyleClass().add("game-menu-roll-dice-button");
    rollDiceButton.setOnAction(event -> {
      if (onRollDice != null) {
        onRollDice.run();
      }
    });
    // Options section at the bottom of the menu
    GridPane optionsGrid = new GridPane();
    optionsGrid.getStyleClass().add("game-menu-options-box");
    optionsGrid.setHgap(12);
    optionsGrid.setVgap(8);

    Label optionsLabel = new Label("Options");
    optionsLabel.getStyleClass().add("game-menu-options-label");
    optionsGrid.add(optionsLabel, 0, 0, 2, 1);

    Text rollForAllPlayersText = new Text("Roll for all players");
    rollForAllPlayersText.getStyleClass().add("game-menu-roll-for-all-players-text");
    optionsGrid.add(rollForAllPlayersText, 0, 1);
    optionsGrid.add(rollForAllPlayersCheckBox, 1, 1);

    Text animatedDiceText = new Text("Animated dice");
    animatedDiceText.getStyleClass().add("game-menu-roll-for-all-players-text");
    optionsGrid.add(animatedDiceText, 0, 2);
    optionsGrid.add(animatedDiceCheckBox, 1, 2);

    // Lower part of the menu, containing the controls for rolling the dice
    HorizontalDivider horizontalDividerBottom = new HorizontalDivider();

    VBox menuBottomBox = new VBox(horizontalDividerBottom, diceContainer, rollDiceButton,
        optionsGrid);
    menuBottomBox.getStyleClass().add("game-menu-bottom-box");

    this.getChildren().setAll(menuTopBox, gameLogScrollPane, menuBottomBox);

    // Hide dice and animation option when 'Roll for all players' is checked
    rollForAllPlayersCheckBox.selectedProperty().addListener((obs,
        wasSelected, isSelected) -> {
      diceContainer.setVisible(!isSelected);
      diceContainer.setManaged(!isSelected);
      animatedDiceCheckBox.setVisible(!isSelected);
      animatedDiceCheckBox.setManaged(!isSelected);
      animatedDiceCheckBox.setSelected(!isSelected);
      animatedDiceText.setVisible(!isSelected);
      animatedDiceText.setManaged(!isSelected);
    });
    // Set initial state for the options
    boolean isSelected = rollForAllPlayersCheckBox.isSelected();
    diceContainer.setVisible(!isSelected);
    diceContainer.setManaged(!isSelected);
    animatedDiceCheckBox.setVisible(!isSelected);
    animatedDiceCheckBox.setManaged(!isSelected);
    animatedDiceText.setVisible(!isSelected);
    animatedDiceText.setManaged(!isSelected);
  }

  /**
   * Adds a new round section to the game log. Each round section is a {@link VBox} titled with the
   * round number.
   *
   * @param roundNumber The number of the round to add.
   */
  public void addGameLogRoundBox(int roundNumber) {
    Text roundBoxRoundNumberText = new Text("Round " + roundNumber);
    roundBoxRoundNumberText.getStyleClass().add("game-menu-game-log-round-number");

    VBox gameLogRoundBox = new VBox(roundBoxRoundNumberText);
    HBox.setHgrow(gameLogRoundBox, Priority.ALWAYS);
    gameLogRoundBox.getStyleClass().add("game-menu-game-log-round-box");
    gameLogRoundBoxesVbox.getChildren().add(gameLogRoundBox);
  }

  /**
   * Adds a text entry (log line) to the most recently added round box in the game log.
   *
   * @param text The log message to add.
   */
  public void addGameLogRoundBoxEntry(String text) {
    Text entryText = new Text(text);
    entryText.getStyleClass().add("game-menu-game-log-round-box-entry");
    entryText.setWrappingWidth(200);

    VBox roundBox = (VBox) gameLogRoundBoxesVbox.getChildren().getLast();
    roundBox.getChildren().add(entryText);
  }

  /**
   * Sets the {@link Runnable} action to be executed when the "Restart Game" button is clicked.
   *
   * @param onRestartGame The action to perform on restart.
   */
  public void setOnRestartGame(Runnable onRestartGame) {
    this.onRestartGame = onRestartGame;
  }

  /**
   * Sets the {@link Runnable} action to be executed when the "Quit Game" button is clicked.
   *
   * @param onQuitGame The action to perform on quit.
   */
  public void setOnQuitGame(Runnable onQuitGame) {
    this.onQuitGame = onQuitGame;
  }

  /**
   * Sets the {@link Runnable} action to be executed when the "Roll Dice" button is clicked.
   *
   * @param onRollDice The action to perform on roll dice.
   */
  public void setOnRollDice(Runnable onRollDice) {
    this.onRollDice = onRollDice;
  }

  /**
   * Checks if the "Roll for all players" checkbox is currently selected.
   *
   * @return True if selected, false otherwise.
   */
  public boolean getRollForAllPlayersSelected() {
    return rollForAllPlayersCheckBox.isSelected();
  }

  /**
   * Checks if the "Animated dice" checkbox is currently selected.
   *
   * @return True if animated dice are enabled, false otherwise.
   */
  public boolean isAnimatedDiceEnabled() {
    return animatedDiceCheckBox.isSelected();
  }

  /**
   * Disables the "Roll Dice" button.
   */
  public void disableRollDiceButton() {
    rollDiceButton.setDisable(true);
  }

  /**
   * Enables the "Roll Dice" button.
   */
  public void enableRollDiceButton() {
    rollDiceButton.setDisable(false);
  }

  /**
   * Triggers the dice roll animation (if enabled) or directly sets the dice values. Executes the
   * {@code onFinished} callback once all dice have completed their roll (or immediately if
   * animation is disabled).
   *
   * @param values     An array of integer values to display on the dice. The length of this array
   *                   should not exceed the number of dice in this GameMenuBox.
   * @param onFinished A {@link Runnable} to be executed after all dice have finished rolling.
   * @throws IllegalArgumentException if the length of {@code values} exceeds the number of dice.
   */
  public void animateDiceRoll(int[] values, Runnable onFinished) {
    if (values.length > dice.length) {
      throw new IllegalArgumentException("Too many dice values provided");
    }

    if (!isAnimatedDiceEnabled()) {
      // If animation is disabled, just show the values immediately
      for (int i = 0; i < values.length; i++) {
        dice[i].setValue(values[i]);
      }
      onFinished.run();
      return;
    }

    // Create a counter to track completed animations
    final int[] completedAnimations = {0};

    // Animate each die
    for (int i = 0; i < values.length; i++) {
      final int index = i;
      dice[i].roll(values[i], () -> {
        completedAnimations[0]++;
        // When all animations are complete, run the onFinished callback
        if (completedAnimations[0] == values.length) {
          onFinished.run();
        }
      });
    }
  }
}
