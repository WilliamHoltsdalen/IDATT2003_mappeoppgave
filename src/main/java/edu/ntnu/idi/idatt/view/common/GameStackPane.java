package edu.ntnu.idi.idatt.view.common;

import edu.ntnu.idi.idatt.model.board.Board;
import edu.ntnu.idi.idatt.model.player.Player;
import java.util.List;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

public abstract class GameStackPane extends StackPane {
  protected static final Duration TRANSITION_DURATION = Duration.seconds(1);
  protected final Board board;
  protected double[] boardDimensions;
  protected final List<Player> players;
  protected final Pane playersPane;

  protected double tileSizeX;
  protected double tileSizeY;
  protected double[] tilePositionX;
  protected double[] tilePositionY;

  protected GameStackPane(Board board, List<Player> players) {
    this.board = board;
    this.boardDimensions = new double[2];
    this.players = players;

    this.playersPane = new Pane();

    this.getStyleClass().add("game-board");
  }

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

  protected abstract void initializePlayersPane();

  public abstract void addGamePieces(List<Player> players);

  protected abstract double[] convertCoordinates(int[] rc);
}
