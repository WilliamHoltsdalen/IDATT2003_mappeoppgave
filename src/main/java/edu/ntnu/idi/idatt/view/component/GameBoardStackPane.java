package edu.ntnu.idi.idatt.view.component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.ntnu.idi.idatt.factory.view.PlayerTokenFactory;
import edu.ntnu.idi.idatt.model.board.Board;
import edu.ntnu.idi.idatt.model.player.Player;
import edu.ntnu.idi.idatt.model.tile.Tile;
import edu.ntnu.idi.idatt.view.util.ViewUtils;
import javafx.animation.PathTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Shape;
import javafx.util.Duration;

/**
 * <h3>GameBoardStackPane class</h3>
 *
 * <p>This class extends the StackPane class. It is used to display the game board in the game view.
 * It contains a board image, a players pane, and methods for moving players.
 */
public class GameBoardStackPane extends StackPane {
  private static final Duration TRANSITION_DURATION = Duration.seconds(1);
  private final Board board;
  private final Map<Player, Tile> playerTileMap;
  private final Map<Player, Shape> playerTokenMap;
  private double[] boardDimensions;
  private final List<Player> players;
  private final Pane playersPane;

  double tileSizeX;
  double tileSizeY;
  double[] tilePositionX;
  double[] tilePositionY;
  /**
   * Constructor for GameBoardStackPane class.
   *
   * @param board the board to display
   * @param players the list of players to display
   */
  public GameBoardStackPane(Board board, List<Player> players) {
    this.board = board;
    this.playerTileMap = new HashMap<>();
    this.playerTokenMap = new HashMap<>();
    this.boardDimensions = new double[2];
    this.players = players;

    this.playersPane = new Pane();

    this.getStyleClass().add("game-board");
    initialize(players);
  }

  /**
   * Initializes the board stack pane by creating all the components and adding them to the stack
   * pane. Also initializes the board dimensions and origin position for the players.
   *
   * @param players the list of players to display
   */
  private void initialize(List<Player> players) {
    BoardStackPane boardStackPane = new BoardStackPane();
    boardStackPane.initialize(board, board.getBackground());
    boardStackPane.getBackgroundImageView().setFitWidth(500);
    boardStackPane.getStyleClass().add("game-board-image-view");

    StackPane stackPane = new StackPane();
    stackPane.getChildren().setAll(boardStackPane, playersPane);
    stackPane.getStyleClass().add("game-board-stack-pane");
    stackPane.maxHeightProperty().bind(stackPane.heightProperty());
    this.getChildren().add(stackPane);

    playersPane.layoutBoundsProperty().addListener((obs, oldVal, newVal) -> {
      if (newVal.getWidth() > 0 && newVal.getHeight() > 0) {
        boardDimensions = new double[]{newVal.getWidth(), newVal.getHeight()};
        this.tileSizeX = boardDimensions[0] / board.getRowsAndColumns()[1];
        this.tileSizeY = boardDimensions[1] / board.getRowsAndColumns()[0];
        this.tilePositionX = new double[]{(tileSizeX / 4) , (tileSizeX / 2), (tileSizeX / 4) * 3, (tileSizeX / 4), (tileSizeX / 4) * 3};
        this.tilePositionY = new double[]{(tileSizeY / 4), (tileSizeY / 2), (tileSizeY / 4), (tileSizeY / 4) * 3, (tileSizeY / 4) * 3};
        if (playersPane.getChildren().isEmpty()) {
          addGamePieces(players);
        }
      }
    });
  }

  /**
   * Adds game pieces to the board for the players in the given list, and places them on the
   * first tile of the board.
   *
   * @param players the list of players to add game pieces for
   */
  public void addGamePieces(List<Player> players) {
    players.forEach(player -> {

      Tile playerTile = player.getCurrentTile();

      double posX = tilePositionX[players.indexOf(player)];
      double posY = tilePositionY[players.indexOf(player)];

      Shape playerToken = PlayerTokenFactory.create(7, Color.web(player.getColorHex()),
          player.getPlayerTokenType());
      playerToken.getStyleClass().add("game-board-player-token");
      playerToken.setTranslateX(posX + convertCoordinates(playerTile.getCoordinates())[0]);
      playerToken.setTranslateY(convertCoordinates(playerTile.getCoordinates())[1] - posY);
      playersPane.getChildren().add(playerToken);

      playerTokenMap.put(player, playerToken);
      playerTileMap.put(player, playerTile);
    });
  }

  /**
   * Moves the player to the new tile, with an optional straight line option to allow for a tile
   * action animation. If straightLine is true, the player will move to the new tile with a pause
   * transition before the tile action animation begins to allow for normal player movement to
   * finish.
   *
   * @param player the player to move
   * @param newTile the new tile to move the player to
   * @param straightLine whether to use a straight line animation or not
   */
  public void movePlayer(Player player, Tile newTile, boolean straightLine) {
    /* Prevents the player from moving to the same tile, which would cause a null pointer exception
       when setting the moveTo coordinates in the path. */
    if (playerTileMap.get(player).getTileId() == newTile.getTileId()) {
      return;
    }
    double posX = tilePositionX[players.indexOf(player)];
    double posY = tilePositionY[players.indexOf(player)];

    double[] currentPaneCoordinates = convertCoordinates(playerTileMap.get(player).getCoordinates());
    double[] newPaneCoordinates = convertCoordinates(newTile.getCoordinates());

    double currentXPos = posX + currentPaneCoordinates[0];
    double currentYPos = currentPaneCoordinates[1] - posY;
    double newXPos = posX + newPaneCoordinates[0];
    double newYPos = newPaneCoordinates[1] - posY;

    /* Using a sequential transition with a pause transition (if straightLine is true) to delay the
       transition to allow normal player movement to finish before a tile action movement begins. */
    SequentialTransition transition = new SequentialTransition();

    Path path = new Path();
    path.getElements().add(new MoveTo(currentXPos, currentYPos));

    if (straightLine) {
      path.getElements().add(new LineTo(newXPos, newYPos));
      PauseTransition pauseTransition = new PauseTransition(TRANSITION_DURATION);
      transition.getChildren().add(pauseTransition);
    } else {
      getPathTiles(playerTileMap.get(player), newTile).forEach(tile -> {
          double[] tilePaneCoordinates = convertCoordinates(tile.getCoordinates());
          path.getElements().add(
              new LineTo(posX + tilePaneCoordinates[0], tilePaneCoordinates[1] - posY));
      });
    }
    Shape playerToken = playerTokenMap.get(player);
    PathTransition pathTransition = new PathTransition();
    pathTransition.setDuration(TRANSITION_DURATION);
    pathTransition.setNode(playerToken);
    pathTransition.setPath(path);

    transition.getChildren().add(pathTransition);
    transition.play();
    // Update the player tile map to reflect the new tile.
    playerTileMap.put(player, newTile);
  }

  /**
   * Converts the coordinates from the board's coordinate system to the pane's coordinate system.
   *
   * @param rc the array of coordinates in the board's coordinate system (row, column) with origin
   *           at the bottom left corner.
   * @return the array of coordinates in the pane's coordinate system (x, y) with origin at the
   *         top left corner.
   */
  private double[] convertCoordinates(int[] rc) {
    return ViewUtils.boardToScreenCoordinates(rc, board, boardDimensions[0], boardDimensions[1]);
  }

  /**
   * Gets the list of tiles between the start and end tiles.
   *
   * @param startTile the start tile for the path
   * @param endTile the end tile for the path
   * @return the list of tiles between the start and end tiles
   */
  private List<Tile> getPathTiles(Tile startTile, Tile endTile) {
    int fromId = startTile.getTileId();
    int toId = endTile.getTileId();

    List<Tile> pathTiles = new ArrayList<>();
    if (fromId < toId) {
      for (int id = fromId + 1; id <= toId; id++) {
        pathTiles.add(board.getTile(id));
      }
    } else {
      for (int id = fromId - 1; id >= toId; id--) {
        pathTiles.add(board.getTile(id));
      }
    }
    return pathTiles;
  }
}
