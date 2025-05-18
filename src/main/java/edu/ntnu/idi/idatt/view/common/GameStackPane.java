package edu.ntnu.idi.idatt.view.common;

import edu.ntnu.idi.idatt.model.board.Board;
import edu.ntnu.idi.idatt.model.player.Player;
import java.util.List;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.geometry.Bounds;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

public abstract class GameStackPane extends StackPane {
  protected static final double GAME_BOARD_PADDING = 25.0;
  protected static final Duration TRANSITION_DURATION = Duration.seconds(1);
  protected final Board board;
  protected double[] boardDimensions;
  protected final List<Player> players;
  protected final Pane playersPane;

  protected double tileSizeX;
  protected double tileSizeY;

  protected GameStackPane(Board board, List<Player> players) {
    this.board = board;
    this.boardDimensions = new double[2];
    this.players = players;

    this.playersPane = new Pane();
    this.getStylesheets().add("stylesheets/gameBoardStyles.css");
    this.getStyleClass().add("game-board-stack-pane");
  }

  protected void initialize(BoardStackPane boardStackPane, DoubleBinding observableWidthFromParent) {
    boardStackPane.initialize(board, board.getBackground());
    
    ImageView backgroundImageView = boardStackPane.getBackgroundImageView();
    backgroundImageView.setPreserveRatio(true);

    this.maxWidthProperty().bind(observableWidthFromParent);
    this.prefWidthProperty().bind(observableWidthFromParent);
    this.setMinWidth(0.0);
    this.setMinHeight(Region.USE_COMPUTED_SIZE);
    
    // Game
    DoubleBinding imageViewHeightBinding = Bindings.createDoubleBinding(() -> {
      Bounds bounds = backgroundImageView.getLayoutBounds();
      if (backgroundImageView.getImage() != null && bounds != null && bounds.getWidth() > 0 && bounds.getHeight() > 0) {
        return bounds.getHeight();
      }
      return 0.0; // Fallback to 0 height if image not loaded or bounds invalid
    }, backgroundImageView.layoutBoundsProperty(), backgroundImageView.imageProperty());

    DoubleBinding contentAreaWidthForBoard = Bindings.createDoubleBinding(() -> {
      double availableWidthForContentHolder = observableWidthFromParent.get() - (GAME_BOARD_PADDING * 2);
      return Math.max(0, availableWidthForContentHolder);
    }, observableWidthFromParent, this.paddingProperty()); 

    StackPane contentHolder = new StackPane();
    contentHolder.getChildren().setAll(boardStackPane, playersPane);
    contentHolder.setMinSize(0.0, Region.USE_COMPUTED_SIZE);
    contentHolder.prefHeightProperty().bind(imageViewHeightBinding);
    contentHolder.maxHeightProperty().bind(imageViewHeightBinding);

    this.prefHeightProperty().bind(Bindings.createDoubleBinding(() ->
    imageViewHeightBinding.get() + (GAME_BOARD_PADDING * 2),
    imageViewHeightBinding
    ));
    this.maxHeightProperty().bind(Bindings.createDoubleBinding(() ->
    imageViewHeightBinding.get() + (GAME_BOARD_PADDING * 2),
    imageViewHeightBinding
    ));
    
    contentHolder.prefWidthProperty().bind(contentAreaWidthForBoard);
    contentHolder.maxWidthProperty().bind(contentAreaWidthForBoard);
    
    boardStackPane.maxWidthProperty().bind(contentAreaWidthForBoard);
    boardStackPane.prefWidthProperty().bind(contentAreaWidthForBoard);
    boardStackPane.setMinWidth(0.0);

    playersPane.prefWidthProperty().bind(contentAreaWidthForBoard);
    playersPane.maxWidthProperty().bind(contentAreaWidthForBoard);
    playersPane.setMinWidth(0.0);
    
    backgroundImageView.fitWidthProperty().bind(contentAreaWidthForBoard);
    
    
    this.getChildren().add(contentHolder);
    initializePlayersPane();
  }

  protected abstract void initializePlayersPane();

  public abstract void addGamePieces(List<Player> players);

  protected abstract double[] convertCoordinates(int[] rc);

  protected abstract void updatePerPlayerTileOffsets();

  protected abstract void refreshPlayersPane();
}
