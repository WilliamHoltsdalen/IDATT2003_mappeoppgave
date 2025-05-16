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
 * <h3>GameMenuBox class</h3>
 *
 * <p>This class extends the VBox class. It is used to display the game menu box
 * in the game view. It contains buttons for restarting and quitting the game,
 * a scroll pane for the game log, and controls for playing the game such as rolling
 * the dice.
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
   * Constructor for the GameMenuBox class. Initializes the game menu box by creating
   * all the components and initializing the game menu box by calling the
   * {@link #initialize()} method.
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
    gameLogRoundBoxesVbox.getChildren().add(gameLogRoundBox);
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

    VBox roundBox = (VBox) gameLogRoundBoxesVbox.getChildren().getLast();
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

  /**
   * Returns whether the 'animated dice' checkbox is selected.
   *
   * @return Whether the 'animated dice' checkbox is selected.
   */
  public boolean isAnimatedDiceEnabled() {
    return animatedDiceCheckBox.isSelected();
  }

  public void disableRollDiceButton() {
    rollDiceButton.setDisable(true);
  }

  public void enableRollDiceButton() {
    rollDiceButton.setDisable(false);
  }

  /**
   * Animates the dice roll with the given values.
   *
   * @param values The values to show on the dice.
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
