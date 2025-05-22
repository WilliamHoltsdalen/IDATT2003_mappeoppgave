package edu.ntnu.idi.idatt.view.common;

import edu.ntnu.idi.idatt.model.board.Board;
import edu.ntnu.idi.idatt.model.player.Player;
import java.util.List;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

/**
 * GameStackPane.
 *
 * <p>Abstract JavaFX {@link StackPane} designed to display the main game area, typically including
 * a {@link BoardStackPane} for the board visuals and a {@link Pane} for player tokens/pieces on 
 * top.</p>
 *
 * <p>It manages the board model, player list, and calculates tile positions for placing game
 * pieces. Subclasses are responsible for initializing the player pane and adding game pieces
 * according to the specific game's rules and visuals.</p>
 *
 * @see StackPane
 * @see Board
 * @see Player
 * @see BoardStackPane
 */
public abstract class GameStackPane extends StackPane {
  protected static final Duration TRANSITION_DURATION = Duration.seconds(2);
  protected final Board board;
  protected double[] boardDimensions;
  protected final List<Player> players;
  protected final Pane playersPane;

  protected double tileSizeX;
  protected double tileSizeY;
  protected double[] tilePositionX;
  protected double[] tilePositionY;

  /**
   * Constructs a GameStackPane.
   *
   * @param board The {@link Board} model for the game.
   * @param players A list of {@link Player}s participating in the game.
   */
  protected GameStackPane(Board board, List<Player> players) {
    this.board = board;
    this.boardDimensions = new double[2];
    this.players = players;

    this.playersPane = new Pane();
    this.getStylesheets().add("stylesheets/gameBoardStyles.css");
    this.getStyleClass().add("game-board");
  }

  /**
   * Initializes the GameStackPane by setting up the provided {@link BoardStackPane}
   * (which displays the board image and grid) and the {@link #playersPane} where game pieces will
   * reside.
   *
   * @param boardStackPane The pre-configured {@link BoardStackPane} to be used as the base layer.
   */
  protected void initialize(BoardStackPane boardStackPane) {
    boardStackPane.initialize(board, board.getBackground());
    boardStackPane.getBackgroundImageView().setFitWidth(500);
    boardStackPane.getStyleClass().add("game-board-image-view");

    StackPane stackPane = new StackPane();
    stackPane.getChildren().setAll(boardStackPane, playersPane);
    stackPane.getStyleClass().add("game-board-stack-pane");
    stackPane.maxHeightProperty().bind(stackPane.heightProperty());
    this.getChildren().add(stackPane);

    initializePlayersPane();
  }

  public Duration getAnimationDuration() {
    return TRANSITION_DURATION;
  }

  /**
   * Initializes the {@link #playersPane} where player tokens/pieces will be displayed.
   * Subclasses must implement this to prepare the pane, possibly setting its size or properties.
   */
  protected abstract void initializePlayersPane();

  /**
   * Adds the visual representations (game pieces/tokens) for all players to the
   * {@link #playersPane}. Subclasses must implement the logic for creating and positioning these
   * pieces based on player data.
   *
   * @param players A list of {@link Player}s for whom to add game pieces.
   */
  public abstract void addGamePieces(List<Player> players);

  /**
   * Converts row and column coordinates of a tile into pixel-based X and Y coordinates
   * on the game board display.
   * Subclasses must implement this based on their specific board layout (e.g., grid, path-based).
   *
   * @param rc An array containing [row, column] or a similar tile identifier.
   * @return A double array containing [xPixelCoordinate, yPixelCoordinate].
   */
  protected abstract double[] convertCoordinates(int[] rc);
}
