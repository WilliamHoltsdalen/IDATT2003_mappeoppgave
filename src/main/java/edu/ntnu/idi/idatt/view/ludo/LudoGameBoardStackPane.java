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

/**
 * <h3>LudoGameBoardStackPane.</h3>
 *
 * <p>This class extends {@link BoardStackPane} to provide a specialized visual representation
 * for a {@link LudoGameBoard}. It is responsible for rendering the Ludo board's grid, cells,
 * background, and decorative patterns such as start areas and the central finish area.</p>
 *
 * <p>Key functionalities include:
 * <ul>
 *   <li>Initializing the board display with a {@link LudoGameBoard} instance and a background
 *       image.</li>
 *   <li>Updating the grid layout based on the Ludo board's size.</li>
 *   <li>Creating individual cells ({@link #createRowCell(double, double, int, int)}) and styling
 *       them based on their type (e.g., track, start, finish path) and associated player color
 *       from the {@code LudoGameBoard}.</li>
 *   <li>Applying a decorative pattern ({@link #applyPattern()}) which draws the four colored
 *       start areas with their token placeholders (circles) and the central colored triangles
 *       representing the finish zone.</li>
 * </ul>
 * </p>
 *
 * <p>Methods like {@code addComponent}, {@code updateBoardVisuals}, {@code loadComponents}, and
 * {@code getImagePath} are inherited from {@code BoardStackPane} but are not currently utilized
 * or are placeholders in this Ludo-specific implementation.</p>
 *
 * @see BoardStackPane
 * @see LudoGameBoard
 * @see LudoTile
 */
public class LudoGameBoardStackPane extends BoardStackPane {

  private final StackPane patternStackPane;

  /**
   * Constructs a new {@code LudoGameBoardStackPane}.
   * Initializes the pattern stack pane that will hold decorative elements like start areas.
   */
  public LudoGameBoardStackPane() {
    super();
    this.patternStackPane = new StackPane();
    super.getChildren().add(patternStackPane);
  }

  /**
   * Initializes the Ludo game board display.
   * Calls the superclass initialization and then updates the grid and loads components on the
   * JavaFX application thread.
   *
   * @param board             The {@link Board} (expected to be a {@link LudoGameBoard}) to display.
   * @param backgroundImagePath The path to the background image for the board.
   */
  @Override
  public void initialize(Board board, String backgroundImagePath) {
    logger.debug("Initializing LudoGameBoardStackPane");
    super.initialize(board, backgroundImagePath);

    Platform.runLater(() -> {
      updateGrid();
      loadComponents();
    });
  }

  /**
   * Updates the grid display for the Ludo board.
   * Clears existing children, recalculates cell dimensions based on the background image view's
   * size and the board's size, and recreates the grid cells. After creating the grid, it applies
   * the Ludo-specific pattern and updates board visuals.
   */
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

  /**
   * Creates a single cell {@link StackPane} for the Ludo board grid.
   * The cell is styled based on the {@link LudoTile} type (e.g., track, start, finish) and the
   * player color associated with specific track or finish tiles.
   *
   * @param cellWidth  The calculated width for the cell.
   * @param cellHeight The calculated height for the cell.
   * @param row        The row index of the cell in the grid.
   * @param col        The column index of the cell in the grid.
   * @return A {@link StackPane} representing the styled grid cell.
   */
  @Override
  public StackPane createRowCell(double cellWidth, double cellHeight, final int row,
      final int col) {
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
    Color[] colors = ((LudoGameBoard) board).getColors();

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

  /**
   * Applies a decorative pattern to the Ludo board, drawing the start areas and the central
   * finish area. This pattern is overlaid on top of the grid.
   *
   * <p>The start areas are colored rectangles with inner white rectangles and four circles
   * (placeholders for tokens). The central area consists of four colored triangles pointing
   * inwards.</p>
   */
  @Override
  public void applyPattern() {
    patternStackPane.getChildren().clear();

    int boardSize = ((LudoGameBoard) board).getBoardSize();
    int startAreaSize = ((LudoGameBoard) board).getStartAreaSize();
    double startAreaDim = ((double) startAreaSize / boardSize) * boardDimensions[0];
    Pos[] startAreaPositions = new Pos[]{Pos.TOP_LEFT, Pos.TOP_RIGHT, Pos.BOTTOM_RIGHT,
        Pos.BOTTOM_LEFT};
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

    Pos[] middleAreaPositions = new Pos[]{Pos.CENTER_LEFT, Pos.TOP_CENTER, Pos.CENTER_RIGHT,
        Pos.BOTTOM_CENTER};
    int[] rotation = new int[]{90, 180, 270, 0};
    for (int i = 0; i < 4; i++) {
      Polygon polygon = new Polygon(
          middleAreaDim / 2, 0,
          middleAreaDim, middleAreaDim / 2,
          0, middleAreaDim / 2
      );
      final Group polygonGroup = new Group(polygon);
      polygon.setRotate(rotation[i]);
      polygon.setFill(((LudoGameBoard) board).getColors()[i]);
      polygon.setStroke(Color.BLACK);
      polygon.setStrokeWidth(1);
      StackPane.setAlignment(polygonGroup, middleAreaPositions[i]);
      middleAreaPane.getChildren().add(polygonGroup);
    }
    patternStackPane.getChildren().add(middleAreaPane);
  }

  /**
   * Placeholder method. Currently not used for Ludo game board components.
   *
   * @param componentIdentifier The identifier of the component to add.
   * @param coordinates         The {@link TileCoordinates} where the component should be added.
   */
  @Override
  public void addComponent(String componentIdentifier, TileCoordinates coordinates) {
    // Not needed
  }

  /**
   * Placeholder method. Currently not used for updating Ludo board visuals directly through here.
   */
  @Override
  public void updateBoardVisuals() {
  }

  /**
   * Placeholder method. Currently not used for loading Ludo game components through here.
   */
  @Override
  public void loadComponents() {
    // Not needed
  }

  /**
   * Placeholder method. Currently not used for getting image paths for Ludo components.
   *
   * @param componentIdentifier The identifier of the component.
   * @return {@code null} as it's not implemented.
   */
  @Override
  public String getImagePath(String componentIdentifier) {
    return null;
  }
}