package edu.ntnu.idi.idatt.view.component;

import edu.ntnu.idi.idatt.model.Board;
import edu.ntnu.idi.idatt.model.Player;
import edu.ntnu.idi.idatt.model.Tile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.animation.PathTransition;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.util.Duration;

public class GameBoardStackPane extends StackPane {
  private final Board board;
  private final Map<Player, Tile> playerTileMap;
  private final Map<Player, Circle> playerCircleMap;

  private final Pane playersPane;

  private double[] boardDimensions;
  private double tileSize;
  private final double originPos;

  public GameBoardStackPane(Board board) {
    this.board = board;
    this.playerTileMap = new HashMap<>();
    this.playerCircleMap = new HashMap<>();

    this.playersPane = new Pane();

    this.boardDimensions = new double[2];
    boardDimensions[0] = 578;
    boardDimensions[1] = 520;
    this.tileSize = boardDimensions[0] / board.getRowsAndColumns()[1];
    this.originPos = tileSize / 2;

    this.getStyleClass().add("game-board");
    initialize();
  }

  private void initialize() {
    ImageView boardImageView = new ImageView();
    boardImageView.setImage(new Image(board.getImagePath()));
    boardImageView.getStyleClass().add("game-board-image-view");

    playersPane.getStyleClass().add("game-players-pane");
    VBox.setVgrow(playersPane, Priority.NEVER);

    StackPane stackPane = new StackPane();
    stackPane.getChildren().setAll(boardImageView, playersPane);
    stackPane.getStyleClass().add("game-board-stack-pane");
    stackPane.maxHeightProperty().bind(stackPane.heightProperty());
    this.getChildren().add(stackPane);
  }

  public void addPlayer(Player player, Tile tile) {
    Circle playerCircle = new Circle(10, Color.TRANSPARENT);
    playerCircle.setStroke(Color.web(player.getColorHex()));
    playerCircle.setStrokeWidth(8);

    playerCircle.setTranslateX(originPos + convertCoordinates(tile.getCoordinates())[0]);
    playerCircle.setTranslateY(convertCoordinates(tile.getCoordinates())[1] - originPos);

    playersPane.getChildren().add(playerCircle);
    playerCircleMap.put(player, playerCircle);
    playerTileMap.put(player, tile);
  }

  public void movePlayer(Player player, Tile newTile, boolean straightLine) {
    double[] currentPlaneCoordinates = convertCoordinates(playerTileMap.get(player).getCoordinates());
    double[] newPlaneCoordinates = convertCoordinates(newTile.getCoordinates());

    double currentXPos = originPos + currentPlaneCoordinates[0];
    double currentYPos = currentPlaneCoordinates[1] - originPos;
    double newXPos = originPos + newPlaneCoordinates[0];
    double newYPos = newPlaneCoordinates[1] - originPos;

    Path path = new Path();
    path.getElements().add(new MoveTo(currentXPos, currentYPos));
    if (straightLine) {
      path.getElements().add(new LineTo(newXPos, newYPos));
    } else {
      getPathTiles(playerTileMap.get(player), newTile).forEach(tile -> {
        double[] tilePlaneCoordinates = convertCoordinates(tile.getCoordinates());
        path.getElements().add(new LineTo(originPos + tilePlaneCoordinates[0], tilePlaneCoordinates[1] - originPos));
      });
    }
    Circle playerCircle = playerCircleMap.get(player);
    PathTransition pathTransition = new PathTransition();
    pathTransition.setDuration(Duration.seconds(1));
    pathTransition.setCycleCount(1);
    pathTransition.setNode(playerCircle);
    pathTransition.setPath(path);
    pathTransition.play();

    playerTileMap.put(player, newTile);
  }

  private double[] convertCoordinates(int[] rc) {
    int r = rc[0];
    int c = rc[1];

    double boardWidth = boardDimensions[0];
    double boardHeight = boardDimensions[1];
    int rMax = board.getRowsAndColumns()[0];
    int cMax = board.getRowsAndColumns()[1];

    double x = (boardWidth / cMax) * c;
    double y = boardHeight - ((boardHeight / rMax) * r);

    return new double[]{x, y};
  }

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
