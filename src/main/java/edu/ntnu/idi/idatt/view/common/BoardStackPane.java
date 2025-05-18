package edu.ntnu.idi.idatt.view.common;

import edu.ntnu.idi.idatt.dto.ComponentDropEventData;
import edu.ntnu.idi.idatt.dto.TileCoordinates;
import edu.ntnu.idi.idatt.model.board.Board;
import edu.ntnu.idi.idatt.view.component.TileActionComponent;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import javafx.geometry.Insets;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.beans.binding.DoubleBinding;

public abstract class BoardStackPane extends StackPane {

  protected static final Logger logger = LoggerFactory.getLogger(BoardStackPane.class);

  protected final Map<Rectangle, TileCoordinates> cellToCoordinatesMap;
  protected final Map<TileCoordinates, TileActionComponent> components;
  protected double[] boardDimensions;
  protected Board board;

  protected final ImageView backgroundImageView;
  protected final VBox gridContainer;
  protected final Pane componentsPane;

  protected Consumer<ComponentDropEventData> onComponentDropped;
  protected Runnable onRemoveComponentsOutsideGrid;

  protected BoardStackPane() {
    this.cellToCoordinatesMap = new HashMap<>();
    this.components = new HashMap<>();
    this.boardDimensions = new double[2];

    this.backgroundImageView = new ImageView();
    this.componentsPane = new Pane();
    this.gridContainer = new VBox();
    this.gridContainer.setSpacing(0);

    this.componentsPane.setPickOnBounds(false); // Allow mouse events on grid beneath the pane
    this.componentsPane.setMouseTransparent(false);

    this.onComponentDropped = null;
    this.onRemoveComponentsOutsideGrid = null;
    this.board = null;

    setNodeListeners();
    this.getChildren().addAll(backgroundImageView, gridContainer, componentsPane);

    this.getStylesheets().add("stylesheets/gameBoardStyles.css");
  }

  /**
   * Loads the components for the board.
   */
  protected abstract void loadComponents();

  /**
   * Gets the image path for the component.
   *
   * @param componentIdentifier the identifier of the component
   * @return the image path for the component
   */
  protected abstract String getImagePath(String componentIdentifier);

  /**
   * Adds a component to the board.
   *
   * @param componentIdentifier the identifier of the component
   * @param coordinates         the coordinates of the component
   */
  public abstract void addComponent(String componentIdentifier, TileCoordinates coordinates);

  /**
   * Updates the visuals of the board.
   */
  public abstract void updateBoardVisuals();

  /**
   * Applies the pattern to the board.
   */
  public abstract void applyPattern();

  /**
   * Updates the grid of the board.
   */
  public abstract void updateGrid();

  /**
   * Creates a row cell for the board.
   *
   * @param cellWidth  the width of the cell
   * @param cellHeight the height of the cell
   * @param row        the row of the cell
   * @param col        the column of the cell
   * @return the row cell
   */
  public abstract StackPane createRowCell(double cellWidth, double cellHeight, int row, int col);

  /**
   * Initializes the board stack pane.
   *
   * @param board               the board
   * @param backgroundImagePath the background image path
   */
  public void initialize(Board board, String backgroundImagePath) {
    logger.debug("Initializing BoardStackPane");
    reset();
    setBoard(board);
    setBackground(backgroundImagePath);
  }

  /**
   * Gets the cell to coordinates map.
   *
   * @return the cell to coordinates map
   */
  public Map<Rectangle, TileCoordinates> getCellToCoordinatesMap() {
    return cellToCoordinatesMap;
  }

  /**
   * Gets the components.
   *
   * @return the components
   */
  public Map<TileCoordinates, TileActionComponent> getComponents() {
    return components;
  }

  /**
   * Gets the board dimensions.
   *
   * @return the board dimensions
   */
  public double[] getBoardDimensions() {
    return boardDimensions;
  }

  /**
   * Gets the board.
   *
   * @return the board
   */
  public Board getBoard() {
    return board;
  }

  /**
   * Gets the background image view.
   *
   * @return the background image view
   */
  public ImageView getBackgroundImageView() {
    return backgroundImageView;
  }

  /**
   * Gets the components pane.
   *
   * @return the components pane
   */
  public Pane getComponentsPane() {
    return componentsPane;
  }

  /**
   * Gets the grid container.
   *
   * @return the grid container
   */
  public VBox getGridContainer() {
    return gridContainer;
  }

  /**
   * Sets the background.
   *
   * @param backgroundImagePath the background image path
   */
  public void setBackground(String backgroundImagePath) {
    logger.debug("Setting background to: {}", backgroundImagePath);
    this.board.setBackground(backgroundImagePath);
    backgroundImageView.setImage(new Image(backgroundImagePath));
    backgroundImageView.setPreserveRatio(true);
  }

  /**
   * Sets the board.
   *
   * @param board the board
   */
  public void setBoard(Board board) {
    logger.debug("Setting board to: {}", board.getName());
    this.board = board;
  }

  /**
   * Sets the board dimensions.
   *
   * @param boardDimensions the board dimensions
   */
  public void setBoardDimensions(double[] boardDimensions) {
    logger.debug("Setting board dimensions to: {}", Arrays.toString(boardDimensions));
    this.boardDimensions = boardDimensions;
  }

  /**
   * Sets the on component dropped.
   *
   * @param onComponentDropped the on component dropped
   */
  public void setOnComponentDropped(Consumer<ComponentDropEventData> onComponentDropped) {
    this.onComponentDropped = onComponentDropped;
  }

  /**
   * Sets the on remove components outside grid.
   *
   * @param onRemoveComponentsOutsideGrid the on remove components outside grid
   */
  public void setOnRemoveComponentsOutsideGrid(Runnable onRemoveComponentsOutsideGrid) {
    this.onRemoveComponentsOutsideGrid = onRemoveComponentsOutsideGrid;
  }

  /**
   * Sets the node listeners.
   */
  protected void setNodeListeners() {
    logger.debug("Setting node listeners");
    backgroundImageView.layoutBoundsProperty().addListener((obs, oldVal, newVal) -> {
      double newBoardWidth = newVal.getWidth();
      double newBoardHeight = newVal.getHeight();

      if (newBoardWidth > 0 && newBoardHeight > 0) {
        boardDimensions = new double[]{newBoardWidth, newBoardHeight};

        /* gridContainer.setMinSize(newBoardWidth, newBoardHeight);
        gridContainer.setPrefSize(newBoardWidth, newBoardHeight);
        gridContainer.setMaxSize(newBoardWidth, newBoardHeight); */

        componentsPane.setMinSize(newBoardWidth, newBoardHeight);
        componentsPane.setPrefSize(newBoardWidth, newBoardHeight);
        componentsPane.setMaxSize(newBoardWidth, newBoardHeight);

        updateBoardVisuals(); // Update visuals after size changes
      } else {
        boardDimensions = new double[]{0, 0};
        gridContainer.getChildren().clear();
        componentsPane.getChildren().clear();

        gridContainer.setMinSize(0, 0);
        gridContainer.setPrefSize(0, 0);
        gridContainer.setMaxSize(0, 0);

        componentsPane.setMinSize(0, 0);
        componentsPane.setPrefSize(0, 0);
        componentsPane.setMaxSize(0, 0);
      }
    });
  }

  /**
   * Resets the board stack pane.
   */
  protected void reset() {
    logger.debug("Resetting BoardStackPane");
    cellToCoordinatesMap.clear();
    components.clear();
    gridContainer.getChildren().clear();
    componentsPane.getChildren().clear();
  }

  /**
   * Removes a component from the board.
   *
   * @param coordinates the coordinates of the component
   */
  public void removeComponent(TileCoordinates coordinates) {
    logger.debug("Removing component from: {}", coordinates);
    components.remove(coordinates);
    updateBoardVisuals();
  }

  /**
   * Finds a cell by coordinates.
   *
   * @param coordinates the coordinates of the cell
   * @return the cell
   */
  protected Rectangle findCellByCoordinates(TileCoordinates coordinates) {
    return cellToCoordinatesMap.entrySet().stream()
        .filter(entry -> entry.getValue().equals(coordinates))
        .map(Map.Entry::getKey)
        .findFirst()
        .orElse(null);
  }
}
