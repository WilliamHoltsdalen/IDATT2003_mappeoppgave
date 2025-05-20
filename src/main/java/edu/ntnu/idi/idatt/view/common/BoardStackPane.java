package edu.ntnu.idi.idatt.view.common;

import edu.ntnu.idi.idatt.dto.ComponentDropEventData;
import edu.ntnu.idi.idatt.dto.TileCoordinates;
import edu.ntnu.idi.idatt.model.board.Board;
import edu.ntnu.idi.idatt.view.component.TileActionComponent;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * BoardStackPane.
 *
 * <p>Abstract JavaFX {@link StackPane} that serves as the base for displaying game boards.
 * It manages a background image, a grid overlay, and a pane for draggable/interactive
 * {@link TileActionComponent}s (like ladders or slides), if enabled.</p>
 *
 * <p>This class handles the visual representation of the board, mapping grid cells to coordinates,
 * and managing the placement and removal of board components. Subclasses are responsible for
 * specific implementations of grid creation, component loading, and visual updates.</p>
 *
 * @see StackPane
 * @see Board
 * @see TileActionComponent
 * @see TileCoordinates
 * @see ComponentDropEventData
 */
public abstract class BoardStackPane extends StackPane {
  protected static final Logger logger = LoggerFactory.getLogger(BoardStackPane.class);

  protected final Map<Rectangle, TileCoordinates> cellToCoordinatesMap;
  protected final Map<TileCoordinates, TileActionComponent> components;
  protected double[] boardDimensions;
  protected Board board;

  protected final ImageView backgroundImageView;
  protected final VBox gridContainer;
  protected final Pane componentsPane;

  /**
   * Consumer for handling events when a component is dropped onto the board.
   */
  protected Consumer<ComponentDropEventData> onComponentDropped;
  /**
   * Runnable action for removing components that are placed outside the valid grid area.
   */
  protected Runnable onRemoveComponentsOutsideGrid;

  /**
   * Constructs a BoardStackPane, initializing its internal maps and visual layers
   * (background, grid, components pane).
   */
  public BoardStackPane() {
    this.cellToCoordinatesMap = new HashMap<>();
    this.components = new HashMap<>();
    this.boardDimensions = new double[2];

    this.backgroundImageView = new ImageView();
    this.componentsPane = new Pane();
    this.gridContainer = new VBox();

    this.componentsPane.setPickOnBounds(false); // Allow mouse events on grid beneath the pane
    this.componentsPane.setMouseTransparent(false);
    
    this.onComponentDropped = null;
    this.onRemoveComponentsOutsideGrid = null;
    this.board = null;

    setNodeListeners();
    this.getChildren().addAll(backgroundImageView, gridContainer, componentsPane);
    this.setMaxWidth(backgroundImageView.getFitWidth() + 40); // 40px for padding (20px each side)

    this.getStylesheets().add("stylesheets/gameBoardStyles.css");
  }

  /**
   * Loads visual components (e.g., ladders, slides) onto the board based on the current
   * {@link #board} model. Subclasses must implement this to populate the {@link #componentsPane}
   * with {@link TileActionComponent}s.
   */
  protected abstract void loadComponents();

  /**
   * Retrieves the image path for a given component identifier.
   * This is used to find the visual asset for a {@link TileActionComponent}.
   *
   * @param componentIdentifier The unique identifier of the component (e.g., "ladder_1R_2U").
   * @return The file path to the component's image.
   */
  protected abstract String getImagePath(String componentIdentifier);

  /**
   * Adds a new {@link TileActionComponent} to the board at the specified coordinates.
   * Subclasses must implement the logic for creating and placing the component visually.
   *
   * @param componentIdentifier The identifier for the type of component to add.
   * @param coordinates The {@link TileCoordinates} where the component should be placed.
   */
  public abstract void addComponent(String componentIdentifier, TileCoordinates coordinates);

  /**
   * Updates all visual aspects of the board, including the grid and component placements.
   * This is often called after changes to board dimensions or component configurations.
   */
  public abstract void updateBoardVisuals();

  /**
   * Applies a specific visual pattern or layout to the board's grid cells.
   * Subclasses define what a "pattern" means in their context (e.g., alternating colors,
   * numbering).
   */
  public abstract void applyPattern();

  /**
   * Updates or recreates the grid display based on the current board dimensions and configuration.
   */
  public abstract void updateGrid();

  /**
   * Creates a single cell (typically a {@link StackPane} containing a {@link Rectangle})
   * for the board grid at the specified row and column.
   *
   * @param cellWidth The calculated width for the cell.
   * @param cellHeight The calculated height for the cell.
   * @param row The row index of the cell.
   * @param col The column index of the cell.
   * @return The created {@link StackPane} representing the grid cell.
   */
  public abstract StackPane createRowCell(double cellWidth, double cellHeight, int row, int col);

  /**
   * Initializes the BoardStackPane with a specific {@link Board} model and a background image.
   * This involves resetting the pane, setting the board, and applying the background.
   *
   * @param board The game {@link Board} model to display.
   * @param backgroundImagePath The file path to the background image.
   */
  public void initialize(Board board, String backgroundImagePath) {
    logger.debug("Initializing BoardStackPane");
    reset();
    setBoard(board);
    setBackground(backgroundImagePath);
  }

  /**
   * Gets the map that links visual grid cells ({@link Rectangle}) to their {@link TileCoordinates}.
   *
   * @return The cell-to-coordinates map.
   */
  public Map<Rectangle, TileCoordinates> getCellToCoordinatesMap() {
    return cellToCoordinatesMap;
  }

  /**
   * Gets the map of placed {@link TileActionComponent}s, keyed by their {@link TileCoordinates}.
   *
   * @return The map of components.
   */
  public Map<TileCoordinates, TileActionComponent> getComponents() {
    return components;
  }

  /**
   * Gets the current dimensions (width, height) of the board area in pixels.
   *
   * @return An array containing [width, height].
   */
  public double[] getBoardDimensions() {
    return boardDimensions;
  }

  /**
   * Gets the underlying {@link Board} model associated with this display.
   *
   * @return The board model.
   */
  public Board getBoard() {
    return board;
  }

  /**
   * Gets the {@link ImageView} used to display the board's background.
   *
   * @return The background image view.
   */
  public ImageView getBackgroundImageView() {
    return backgroundImageView;
  }

  /**
   * Gets the {@link Pane} used as a layer for displaying {@link TileActionComponent}s.
   *
   * @return The components pane.
   */
  public Pane getComponentsPane() {
    return componentsPane;
  }

  /**
   * Gets the {@link VBox} container that holds the grid rows.
   *
   * @return The grid container.
   */
  public VBox getGridContainer() {
    return gridContainer;
  }

  /**
   * Sets the background image for the board.
   * Updates both the model's background property and the {@link #backgroundImageView}.
   *
   * @param backgroundImagePath The file path to the background image.
   */
  public void setBackground(String backgroundImagePath) {
    logger.debug("Setting background to: {}", backgroundImagePath);
    this.board.setBackground(backgroundImagePath);
    backgroundImageView.setImage(new Image(backgroundImagePath));
    backgroundImageView.setPreserveRatio(true);
  }

  /**
   * Sets the {@link Board} model for this display.
   *
   * @param board The board model to use.
   */
  public void setBoard(Board board) {
    logger.debug("Setting board to: {}", board.getName());
    this.board = board;
  }

  /**
   * Sets the pixel dimensions (width, height) for the board display area.
   *
   * @param boardDimensions An array containing [width, height].
   */
  public void setBoardDimensions(double[] boardDimensions) {
    logger.debug("Setting board dimensions to: {}", Arrays.toString(boardDimensions));
    this.boardDimensions = boardDimensions;
  }

  /**
   * Sets a callback {@link Consumer} to be executed when a component is dropped onto the board.
   * The consumer receives {@link ComponentDropEventData} with details about the drop.
   *
   * @param onComponentDropped The consumer to handle component drop events.
   */
  public void setOnComponentDropped(Consumer<ComponentDropEventData> onComponentDropped) {
    this.onComponentDropped = onComponentDropped;
  }

  /**
   * Sets a {@link Runnable} action to be executed when components placed outside the grid
   * need to be removed.
   *
   * @param onRemoveComponentsOutsideGrid The runnable action.
   */
  public void setOnRemoveComponentsOutsideGrid(Runnable onRemoveComponentsOutsideGrid) {
    this.onRemoveComponentsOutsideGrid = onRemoveComponentsOutsideGrid;
  }

  /**
   * Sets up listeners, particularly for changes in the {@link #backgroundImageView} layout bounds,
   * to update {@link #boardDimensions} and trigger {@link #updateBoardVisuals()}.
   */
  protected void setNodeListeners() {
    logger.debug("Setting node listeners");
    // boardDimensions is used to calculate the positions of the tiles in the grid
    backgroundImageView.layoutBoundsProperty().addListener((obs, oldVal, newVal) -> {
      if (newVal.getWidth() > 0 && newVal.getHeight() > 0) {
        logger.debug("Board dimensions updated to: {}x{}", newVal.getWidth(), newVal.getHeight());
        boardDimensions = new double[]{newVal.getWidth(), newVal.getHeight()};
        updateBoardVisuals();
      }
    });
  }

  /**
   * Resets the BoardStackPane by clearing all internal maps (cells, components)
   * and removing children from the grid and components panes.
   */
  protected void reset() {
    logger.debug("Resetting BoardStackPane");
    cellToCoordinatesMap.clear();
    components.clear();
    gridContainer.getChildren().clear();
    componentsPane.getChildren().clear();
  }

  /**
   * Removes a {@link TileActionComponent} from the board at the specified coordinates.
   * Updates the visual display after removal.
   *
   * @param coordinates The {@link TileCoordinates} of the component to remove.
   */
  public void removeComponent(TileCoordinates coordinates) {
    logger.debug("Removing component from: {}", coordinates);
    components.remove(coordinates);
    updateBoardVisuals();
  }

  /**
   * Finds and returns the visual grid cell ({@link Rectangle}) that corresponds
   * to the given {@link TileCoordinates}.
   *
   * @param coordinates The coordinates to search for.
   * @return The matching {@link Rectangle} cell, or null if not found.
   */
  protected Rectangle findCellByCoordinates(TileCoordinates coordinates) {
    return cellToCoordinatesMap.entrySet().stream()
        .filter(entry -> entry.getValue().equals(coordinates))
        .map(Map.Entry::getKey)
        .findFirst()
        .orElse(null);
  }
}
