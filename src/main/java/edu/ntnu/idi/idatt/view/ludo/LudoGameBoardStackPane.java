package edu.ntnu.idi.idatt.view.ludo;

import edu.ntnu.idi.idatt.dto.TileCoordinates;
import edu.ntnu.idi.idatt.model.board.Board;
import edu.ntnu.idi.idatt.model.board.LudoGameBoard;
import edu.ntnu.idi.idatt.model.tile.LudoTile;
import edu.ntnu.idi.idatt.view.common.BoardStackPane;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;

public class LudoGameBoardStackPane extends BoardStackPane {
  private final StackPane patternStackPane;
  
  public LudoGameBoardStackPane() {
    super();
    this.patternStackPane = new StackPane();
    super.getChildren().add(patternStackPane);
  }
  
  @Override
  public void initialize(Board board, String backgroundImagePath) {
    logger.debug("Initializing LudoGameBoardStackPane");
    super.initialize(board, backgroundImagePath);
    
    Platform.runLater(() -> {
      updateGrid();
      loadComponents();
    });
  }

  @Override
  public void updateGrid() {
    logger.debug("Updating grid");
    gridContainer.getChildren().clear();
    cellToCoordinatesMap.clear();

    if (onRemoveComponentsOutsideGrid != null) {
      onRemoveComponentsOutsideGrid.run();
    }

    int boardSize = ((LudoGameBoard) board).getBoardSize();

    double cellWidth = backgroundImageView.getFitWidth() / boardSize;
    double cellHeight = cellWidth;

    for (int row = 0; row < boardSize; row++) {
      HBox rowBox = new HBox();
      for (int col = 0; col < boardSize; col++) {
        rowBox.getChildren().add(
        createRowCell(cellWidth, cellHeight, row, col)
        );
      }
      gridContainer.getChildren().add(rowBox);
    }

    applyPattern();
    updateBoardVisuals();
  }

  @Override
  public StackPane createRowCell(double cellWidth, double cellHeight, final int row, final int col) {
    StackPane cellPane = new StackPane();
    cellPane.getStyleClass().add("ludo-board-cell-pane");
    Rectangle cellRect = new Rectangle(cellWidth, cellHeight);
    cellRect.setSmooth(false);
    cellRect.setStrokeType(StrokeType.INSIDE);
    cellToCoordinatesMap.put(cellRect, new TileCoordinates(row, col));

    // Find the tile with matching coordinates
    LudoTile tile = (LudoTile) board.getTiles().stream()
    .filter(t -> t.getCoordinates()[0] == row && t.getCoordinates()[1] == col)
    .findFirst()
    .orElse(null);

    String type = tile.getType();
    Color[] colors = ((LudoGameBoard)board).getColors();

    if ((type.startsWith("track") || type.startsWith("finish")) && type.contains("1")) {
      cellRect.setFill(colors[0]);
    } else if ((type.startsWith("track") || type.startsWith("finish")) && type.contains("2")) {
      cellRect.setFill(colors[1]);
    } else if ((type.startsWith("track") || type.startsWith("finish")) && type.contains("3")) {
      cellRect.setFill(colors[2]);
    } else if ((type.startsWith("track") || type.startsWith("finish")) && type.contains("4")) {
      cellRect.setFill(colors[3]);
    } else if (type.equals("track")) {
      cellRect.setFill(Color.WHITE);
    } else {
      cellRect.setFill(Color.GRAY);
    }
    
    if (type.contains("track") || type.contains("finish") || type.contains("error")) {
      cellRect.getStyleClass().add("ludo-board-track-cell");
    } else {
      cellRect.getStyleClass().add("ludo-board-cell");
    }
    
    cellPane.getChildren().add(cellRect);
    return cellPane;
  }

  @Override
  public void applyPattern() {
    patternStackPane.getChildren().clear();
    
    int boardSize = ((LudoGameBoard) board).getBoardSize();
    int startAreaSize = ((LudoGameBoard) board).getStartAreaSize();
    double startAreaDim = ((double)startAreaSize / boardSize) * boardDimensions[0];
    Pos[] startAreaPositions = new Pos[]{Pos.TOP_LEFT, Pos.TOP_RIGHT, Pos.BOTTOM_RIGHT, Pos.BOTTOM_LEFT};
    double[][] circlePositions = new double[][]{
      {-(startAreaDim / 6), -(startAreaDim / 6)},
      {(startAreaDim / 6), -(startAreaDim / 6)},
      {(startAreaDim / 6), (startAreaDim / 6)},
      {-(startAreaDim / 6), (startAreaDim / 6)}
    };
    
    // Start areas
    for (int i = 0; i < 4; i++) {
      StackPane startAreaPane = new StackPane();
      startAreaPane.setMaxSize(startAreaDim, startAreaDim);
      
      Rectangle coloredRect = new Rectangle(startAreaDim, startAreaDim);
      coloredRect.setFill(((LudoGameBoard) board).getColors()[i]);
      coloredRect.setStroke(Color.BLACK);
      coloredRect.setStrokeWidth(2);
      startAreaPane.getChildren().add(coloredRect);
      
      Rectangle whiteRect = new Rectangle(startAreaDim * 0.75, startAreaDim * 0.75);
      whiteRect.setFill(Color.WHITE);
      whiteRect.setStroke(Color.BLACK);
      whiteRect.setStrokeWidth(2);
      startAreaPane.getChildren().add(whiteRect);
      
      // Circles in the start areas
      double circleRadius = boardDimensions[0] * 0.03;
      for (int j = 0; j < 4; j++) {
        Circle coloredCircle = new Circle(circleRadius + 6);
        coloredCircle.setTranslateX(circlePositions[j][0] - 1);
        coloredCircle.setTranslateY(circlePositions[j][1] - 1);
        coloredCircle.setFill(((LudoGameBoard) board).getColors()[i]);
        coloredCircle.setStroke(Color.BLACK);
        coloredCircle.setStrokeWidth(1);
        startAreaPane.getChildren().add(coloredCircle);
        
        Circle whiteCircle = new Circle(circleRadius);
        whiteCircle.setTranslateX(circlePositions[j][0] - 1);
        whiteCircle.setTranslateY(circlePositions[j][1] - 1);
        whiteCircle.setFill(Color.WHITE);
        whiteCircle.setStroke(Color.BLACK);
        whiteCircle.setStrokeWidth(1);
        startAreaPane.getChildren().add(whiteCircle);
      }
      StackPane.setAlignment(startAreaPane, startAreaPositions[i]);
      patternStackPane.getChildren().add(startAreaPane);
    }
    
    // Triangles in the middle "finish" area
    StackPane middleAreaPane = new StackPane();
    middleAreaPane.setStyle("-fx-background-color: black;");
    double middleAreaDim = ((double) 3 / boardSize) * boardDimensions[0];
    middleAreaPane.setMaxSize(middleAreaDim, middleAreaDim);
    StackPane.setAlignment(middleAreaPane, Pos.CENTER);
    
    Pos[] middleAreaPositions = new Pos[]{Pos.CENTER_LEFT, Pos.TOP_CENTER, Pos.CENTER_RIGHT, Pos.BOTTOM_CENTER};
    int[] rotation = new int[]{90, 180, 270, 0};
    for (int i = 0; i < 4; i++) {
      Polygon polygon = new Polygon(
      middleAreaDim / 2, 0,
      middleAreaDim, middleAreaDim / 2,
      0, middleAreaDim / 2
      );
      Group polygonGroup = new Group(polygon);
      polygon.setRotate(rotation[i]);
      polygon.setFill(((LudoGameBoard) board).getColors()[i]);
      polygon.setStroke(Color.BLACK);
      polygon.setStrokeWidth(1);
      StackPane.setAlignment(polygonGroup, middleAreaPositions[i]);
      middleAreaPane.getChildren().add(polygonGroup);
    }
    patternStackPane.getChildren().add(middleAreaPane);
  }

  @Override
  public void addComponent(String componentIdentifier, TileCoordinates coordinates) {
    // Not needed
  }

  @Override
  public void updateBoardVisuals() {
  }

  @Override
  public void loadComponents() {
    // Not needed
  }

  @Override
  public String getImagePath(String componentIdentifier) {
    return null;
  }
}