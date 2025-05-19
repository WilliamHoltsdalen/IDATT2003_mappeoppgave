package edu.ntnu.idi.idatt.view.laddergame;

import edu.ntnu.idi.idatt.dto.ComponentDropEventData;
import edu.ntnu.idi.idatt.dto.ComponentSpec;
import edu.ntnu.idi.idatt.dto.TileCoordinates;
import edu.ntnu.idi.idatt.model.board.Board;
import edu.ntnu.idi.idatt.model.board.LadderGameBoard;
import edu.ntnu.idi.idatt.model.tile.LadderGameTile;
import edu.ntnu.idi.idatt.view.common.BoardStackPane;
import edu.ntnu.idi.idatt.view.component.TileActionComponent;
import edu.ntnu.idi.idatt.view.util.ViewUtils;
import java.util.List;
import java.util.stream.Stream;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;

/**
 * <h3>LadderGameBoardStackPane.</h3>
 *
 * <p>This class extends {@link BoardStackPane} to provide a specialized view for displaying
 * {@link LadderGameBoard}s. It handles the visual representation of the game board, including its
 * background, grid, placed components (ladders, slides, portals), and tile numbering.</p>
 *
 * <p>Key functionalities include:
 * <ul>
 *   <li>Initialization with a {@link LadderGameBoard} and background image.</li>
 *   <li>Management of board patterns (e.g., checkers).</li>
 *   <li>Loading and displaying {@link TileActionComponent}s based on the board state.</li>
 *   <li>Dynamically updating the grid when rows or columns change.</li>
 *   <li>Handling drag-and-drop events for placing components onto grid cells.</li>
 *   <li>Applying visual styles to cells based on placed components or patterns.</li>
 *   <li>Converting board coordinates to screen coordinates for component placement.</li>
 * </ul>
 * </p>
 *
 * @see BoardStackPane
 * @see LadderGameBoard
 * @see TileActionComponent
 * @see ComponentSpec
 * @see ComponentDropEventData
 */
public class LadderGameBoardStackPane extends BoardStackPane {

  /**
   * Constructs a new {@code LadderGameBoardStackPane}. Calls the superclass constructor.
   */
  public LadderGameBoardStackPane() {
    super();
  }

  /**
   * Initializes the board stack pane with the given {@link Board} (expected to be a
   * {@link LadderGameBoard}) and the path to its background image. This method calls the superclass
   * initialization and then, on the JavaFX application thread, sets the board pattern, updates the
   * grid display, and loads any existing components from the board model.
   *
   * @param board               The {@link Board} instance (must be a {@link LadderGameBoard}).
   * @param backgroundImagePath The file path to the background image for the board.
   */
  @Override
  public void initialize(Board board, String backgroundImagePath) {
    super.initialize(board, backgroundImagePath);

    Platform.runLater(() -> {
      setPattern(((LadderGameBoard) board).getPattern());
      updateGrid();
      loadComponents();
    });
  }

  /**
   * Gets the current visual pattern applied to the board (e.g., "Blue checker").
   *
   * @return The string representation of the current pattern.
   */
  public String getPattern() {
    return ((LadderGameBoard) board).getPattern();
  }

  /**
   * Sets the visual pattern for the board and applies it to the grid display.
   *
   * @param pattern The name of the pattern to apply (e.g., "Blue checker", "None").
   */
  public void setPattern(String pattern) {
    logger.debug("Setting pattern to: {}", pattern);
    ((LadderGameBoard) board).setPattern(pattern);
    applyPattern();
  }

  /**
   * Loads {@link TileActionComponent}s onto the board based on the {@link TileAction}s associated
   * with each {@link LadderGameTile} in the current {@link LadderGameBoard}. For each tile with a
   * land action, it creates a corresponding visual component (e.g., ladder, slide, portal image)
   * and places it on the board. Portal components may also have a color number set based on their
   * identifier. After loading, it updates the overall board visuals.
   */
  @Override
  protected void loadComponents() {
    board.getTiles().forEach(tile -> {
      LadderGameTile ladderGameTile = (LadderGameTile) tile;
      if (ladderGameTile.getLandAction() != null) {
        String identifier = ladderGameTile.getLandAction().getIdentifier();
        String imagePath = getImagePath(identifier);
        ComponentSpec spec = ComponentSpec.fromFilename(
            imagePath.substring(imagePath.lastIndexOf("/") + 1));
        TileActionComponent component = new TileActionComponent(spec.type().toString(), imagePath,
            board.getTile(tile.getTileId()), ladderGameTile.getLandAction().getDestinationTileId());

        // Set portal color number if it's a portal
        if (spec.type() == ComponentSpec.ComponentType.PORTAL) {
          String[] parts = identifier.split("_");
          if (parts.length >= 4) {
            try {
              int colorNumber = Integer.parseInt(parts[3]);
              component.setPortalColorNumber(colorNumber);
            } catch (NumberFormatException e) {
              // If no valid color number is found, keep the default (1)
              // TODO: Handle NumberFormatException
            }
          }
        }
        components.put(new TileCoordinates(tile.getCoordinates()[0], tile.getCoordinates()[1]),
            component);
      }
    });
    logger.debug("Loaded {} components", components.size());
    updateBoardVisuals();
  }

  /**
   * Constructs the image file path for a given component identifier. The path is built assuming a
   * structure like.
   *
   * @param componentIdentifier The unique identifier of the component.
   * @return The full string path to the component's image file.
   */
  @Override
  protected String getImagePath(String componentIdentifier) {
    String[] identifierParts = componentIdentifier.split("_");
    return "media/assets/" + identifierParts[2] + "/" + componentIdentifier + ".png";
  }

  /**
   * Adds a new visual component to the board at the specified coordinates, based on the provided
   * component identifier. It calculates the destination tile for components like ladders and
   * slides, or a random valid destination for portals. It also checks for tile occupancy to prevent
   * overlapping placements. If the placement is valid, a {@link TileActionComponent} is created and
   * added to the internal collection of components, and the board visuals are updated.
   *
   * @param componentIdentifier The string identifier of the component to add.
   * @param coordinates         The {@link TileCoordinates} (row, column) where the component
   *                            originates.
   * @throws IllegalArgumentException if the target tile or destination tile is already occupied.
   */
  @Override
  public void addComponent(String componentIdentifier, TileCoordinates coordinates) {
    logger.debug("Adding component: {}", componentIdentifier);
    String imagePath = getImagePath(componentIdentifier);
    ComponentSpec spec = ComponentSpec.fromFilename(
        imagePath.substring(imagePath.lastIndexOf("/") + 1));
    int[] destinationCoords;
    int destinationTileId = -1;

    List<TileCoordinates> occupiedTiles = Stream.concat(
        components.keySet().stream(),
        components.values().stream()
            .map(component -> {
              int[] coords = board.getTile(
                      ((LadderGameTile) component.getTile()).getLandAction().getDestinationTileId())
                  .getCoordinates();
              return new TileCoordinates(coords[0], coords[1]);
            })
    ).toList();

    int tileId = ViewUtils.calculateTileId(coordinates.row(), coordinates.col(),
        ((LadderGameBoard) board).getRowsAndColumns()[1]);

    // Calculate destination based on component specification
    switch (spec.type()) {
      case LADDER -> {
        destinationCoords = new int[]{
            coordinates.row() + spec.heightTiles(),
            coordinates.col() + (spec.widthDirection() == ComponentSpec.Direction.RIGHT
                ? spec.widthTiles() : -spec.widthTiles())
        };
        if (destinationCoords[0] < ((LadderGameBoard) board).getRowsAndColumns()[0]
            && destinationCoords[1] >= 0
            && destinationCoords[1] < ((LadderGameBoard) board).getRowsAndColumns()[1]) {
          destinationTileId = ViewUtils.calculateTileId(destinationCoords[0], destinationCoords[1],
              ((LadderGameBoard) board).getRowsAndColumns()[1]);
        }
      }
      case SLIDE -> {
        destinationCoords = new int[]{
            coordinates.row() - spec.heightTiles(),
            coordinates.col() + (spec.widthDirection() == ComponentSpec.Direction.RIGHT
                ? spec.widthTiles() : -spec.widthTiles())
        };
        if (destinationCoords[0] >= 0
            && destinationCoords[1] >= 0
            && destinationCoords[1] < ((LadderGameBoard) board).getRowsAndColumns()[1]) {
          destinationTileId = ViewUtils.calculateTileId(destinationCoords[0], destinationCoords[1],
              ((LadderGameBoard) board).getRowsAndColumns()[1]);
        }
      }
      case PORTAL ->
          destinationTileId = ViewUtils.randomPortalDestination(tileId, board.getTiles().size(),
              occupiedTiles.stream().map(
                  coords -> ViewUtils.calculateTileId(coords.row(), coords.col(),
                      ((LadderGameBoard) board).getRowsAndColumns()[1])).toList());
      default -> {
        break;
      }
    }

    if (occupiedTiles.contains(coordinates) || (destinationTileId != -1 && occupiedTiles.contains(
        new TileCoordinates(
            board.getTile(destinationTileId).getCoordinates()[0],
            board.getTile(destinationTileId).getCoordinates()[1])))) {
      logger.warn("Tile is already occupied: {}", coordinates);
      throw new IllegalArgumentException(
          "Tile is already occupied"); // TODO: Replace with a more specific exception
    }

    if (destinationTileId != -1 && destinationTileId <= board.getTiles().size()) {
      TileActionComponent component = new TileActionComponent(spec.type().toString(), imagePath,
          board.getTile(tileId), destinationTileId);

      // Set portal color number if it's a portal
      if (spec.type() == ComponentSpec.ComponentType.PORTAL) {
        String[] parts = componentIdentifier.split("_");
        if (parts.length >= 4) {
          try {
            int colorNumber = Integer.parseInt(parts[3]);
            component.setPortalColorNumber(colorNumber);
          } catch (NumberFormatException e) {
            // If no valid color number is found, keep the default (1)
          }
        }
      }
      components.put(coordinates, component);
    }
    logger.debug("Added {} components", components.size());
    updateBoardVisuals();
  }

  /**
   * Updates the visual grid display of the board. This involves clearing the existing grid,
   * removing any components that are now outside the grid boundaries (if dimensions changed), and
   * then recreating the grid cells based on the current number of rows and columns in the
   * {@link LadderGameBoard}. Each cell is created with a label showing its tile ID and is
   * configured for drag-and-drop operations. After recreating the grid, the current pattern and
   * component visuals are reapplied.
   */
  @Override
  public void updateGrid() {
    logger.debug("Updating grid");
    gridContainer.getChildren().clear();
    cellToCoordinatesMap.clear();

    if (onRemoveComponentsOutsideGrid != null) {
      onRemoveComponentsOutsideGrid.run();
    }

    double cellWidth =
        backgroundImageView.getFitWidth() / ((LadderGameBoard) board).getRowsAndColumns()[1];
    double cellHeight =
        (backgroundImageView.getFitWidth() / backgroundImageView.getImage().getWidth()
            * backgroundImageView.getImage().getHeight())
            / ((LadderGameBoard) board).getRowsAndColumns()[0];

    // Making the cells in the grid
    for (int i = ((LadderGameBoard) board).getRowsAndColumns()[0] - 1; i >= 0;
        i--) { // Filling rows from top to bottom
      HBox row = new HBox();
      row.setAlignment(Pos.CENTER);
      for (int j = 0; j < ((LadderGameBoard) board).getRowsAndColumns()[1];
          j++) { // Filling columns from left to right
        row.getChildren().add(
            createRowCell(cellWidth, cellHeight, i, j)
        );
      }
      gridContainer.getChildren().add(row); // Adding row to grid container
    }

    applyPattern();
    updateBoardVisuals();
  }

  /**
   * Creates a single visual cell (a {@link StackPane}) for the grid display. Each cell consists of
   * a {@link Rectangle} for its visual appearance and a {@link Label} displaying its tile ID. The
   * cell is also mapped to its board coordinates and set up to handle drag-and-drop events for
   * component placement.
   *
   * @param cellWidth  The width of the cell in pixels.
   * @param cellHeight The height of the cell in pixels.
   * @param row        The zero-indexed row number of the cell on the board.
   * @param col        The zero-indexed column number of the cell on the board.
   * @return A {@link StackPane} representing the created grid cell.
   */
  @Override
  public StackPane createRowCell(double cellWidth, double cellHeight, int row, int col) {
    final StackPane cellPane = new StackPane();
    Rectangle cellRect = new Rectangle(cellWidth, cellHeight);
    cellRect.getStyleClass().add("grid-cell");
    cellToCoordinatesMap.put(cellRect, new TileCoordinates(row, col));
    setupCellDropHandling(cellRect);

    Label cellLabel = new Label(String.valueOf(
        ViewUtils.calculateTileId(row, col, ((LadderGameBoard) board).getRowsAndColumns()[1])));
    cellLabel.getStyleClass().add("grid-cell-label");

    cellPane.setAlignment(Pos.BOTTOM_CENTER);

    cellPane.getChildren().setAll(cellRect, cellLabel);
    return cellPane;
  }

  /**
   * Configures drag-and-drop event handlers for a given grid cell {@link Rectangle}.
   * <ul>
   *   <li>{@code setOnDragOver}: Accepts {@link TransferMode#COPY} if the dragboard has a
   *   string.</li>
   *   <li>{@code setOnDragEntered}: Adds a preview style class to the cell.</li>
   *   <li>{@code setOnDragExited}: Removes the preview style class.</li>
   *   <li>{@code setOnDragDropped}: If the dragboard contains a string (component identifier)
   *       and a drop handler ({@code onComponentDropped}) is set, it invokes the handler with
   *       the component identifier and the target cell.</li>
   * </ul>
   *
   * @param cell The {@link Rectangle} representing the grid cell to set up.
   */
  private void setupCellDropHandling(Rectangle cell) {
    cell.setOnDragOver(event -> {
      if (event.getGestureSource() != cell && event.getDragboard().hasString()) {
        event.acceptTransferModes(TransferMode.COPY);
      }
      event.consume();
    });

    cell.setOnDragEntered(event -> {
      if (event.getGestureSource() != cell && event.getDragboard().hasString()) {
        cell.getStyleClass().add("grid-cell-drag-preview");
        cell.getStyleClass().remove("grid-cell");
      }
      event.consume();
    });

    cell.setOnDragExited(event -> {
      cell.getStyleClass().remove("grid-cell-drag-preview");
      cell.getStyleClass().add("grid-cell");
      event.consume();
    });

    cell.setOnDragDropped(event -> {
      Dragboard db = event.getDragboard();
      boolean success = false;

      if (db.hasString() && onComponentDropped != null) {
        String componentIdentifier = db.getString();
        onComponentDropped.accept(new ComponentDropEventData(componentIdentifier, cell));
      }

      event.setDropCompleted(success);
      event.consume();
    });
  }

  /**
   * Applies the currently selected visual pattern (e.g., "Blue checker") to the grid cells. It
   * iterates through each cell in the grid. If a cell corresponds to an even-numbered tile ID and
   * is not occupied by a non-portal component (origin or destination), the appropriate CSS style
   * class for the pattern is added to the cell's {@link Rectangle}.
   */
  @Override
  public void applyPattern() {
    logger.debug("Applying pattern: {}", ((LadderGameBoard) board).getPattern());
    gridContainer.getChildren().forEach(row ->
        ((HBox) row).getChildren().forEach(cellPane ->
            ((StackPane) cellPane).getChildren().forEach(node -> {
              node.getStyleClass().removeAll("blue-checker", "yellow-checker", "purple-checker");

              List<TileCoordinates> occupiedTiles = Stream.concat(
                  components.keySet().stream()
                      .filter(coords -> !components.get(coords).getType().equals("PORTAL")),
                  components.values().stream()
                      .filter(component -> !component.getType().equals("PORTAL"))
                      .map(component -> {
                        int[] coords = board.getTile(
                            ((LadderGameTile) component.getTile()).getLandAction()
                                .getDestinationTileId()).getCoordinates();
                        return new TileCoordinates(coords[0], coords[1]);
                      })
              ).toList();

              if (node instanceof Rectangle rect) {
                TileCoordinates coords = cellToCoordinatesMap.get(rect);
                int tileId = ViewUtils.calculateTileId(coords.row(), coords.col(),
                    ((LadderGameBoard) board).getRowsAndColumns()[1]);
                if (tileId % 2 == 0 && !occupiedTiles.contains(coords)) {
                  switch (((LadderGameBoard) board).getPattern()) {
                    case "Blue checker" -> rect.getStyleClass().add("blue-checker");
                    case "Yellow checker" -> rect.getStyleClass().add("yellow-checker");
                    case "Purple checker" -> rect.getStyleClass().add("purple-checker");
                    default -> {
                      break;
                    } // No pattern
                  }
                }
              }
            })
        )
    );
  }

  /**
   * Updates the visual representation of all components (ladders, slides, portals) on the board.
   * This method first clears any existing visual components from the {@code componentsPane}. It
   * then reapplies the board pattern. For each {@link TileActionComponent} stored:
   * <ul>
   *   <li>It finds the origin and destination {@link Rectangle} cells on the grid.</li>
   *   <li>Calculates the screen coordinates for the component based on its origin cell.</li>
   *   <li>Updates the component's size and position.</li>
   *   <li>Applies specific CSS style classes to the origin and destination cells based on the
   *       component type (e.g., "grid-cell-has-ladder", "grid-cell-ladder-destination").</li>
   *   <li>Adds the updated visual component to the {@code componentsPane}.</li>
   * </ul>
   */
  @Override
  public void updateBoardVisuals() {
    logger.debug("Updating board visuals");
    // Clear all existing visual components
    componentsPane.getChildren().clear();

    applyPattern();

    // Reset all cell action colors
    gridContainer.getChildren().forEach(row ->
        ((HBox) row).getChildren().forEach(cellPane ->
            ((StackPane) cellPane).getChildren().forEach(rect -> {
              if (rect instanceof Rectangle) {
                rect.getStyleClass().removeAll("grid-cell-has-ladder", "grid-cell-has-slide",
                    "grid-cell-ladder-destination", "grid-cell-slide-destination");
              }
            })
        )
    );

    // Place the visual tile action components
    components.forEach((coordinates, component) -> {
      Rectangle originCell = findCellByCoordinates(coordinates);
      if (originCell == null) {
        return;
      }

      int destinationTileId = ((LadderGameTile) component.getTile()).getLandAction()
          .getDestinationTileId();
      int[] destCoords = board.getTile(destinationTileId).getCoordinates();
      Rectangle destinationCell = findCellByCoordinates(
          new TileCoordinates(destCoords[0], destCoords[1]));

      // Get the base position for this tile
      double[] screenCoords = ViewUtils.ladderBoardToScreenCoordinates(
          new int[]{coordinates.row(), coordinates.col()},
          ((LadderGameBoard) board),
          boardDimensions[0],
          boardDimensions[1]
      );

      // Update component size and position based on tile dimensions and base position
      component.updateSizeAndPosition(originCell.getWidth(), originCell.getHeight(),
          screenCoords[0], screenCoords[1]);

      // Add style classes based on component type
      switch (component.getType()) {
        case "LADDER" -> {
          originCell.getStyleClass().add("grid-cell-has-ladder");
          destinationCell.getStyleClass().add("grid-cell-ladder-destination");
        }
        case "PORTAL" -> originCell.getStyleClass().add("grid-cell-has-portal");
        case "SLIDE" -> {
          originCell.getStyleClass().add("grid-cell-has-slide");
          destinationCell.getStyleClass().add("grid-cell-slide-destination");
        }
        default -> {
          break;
        }
      }

      componentsPane.getChildren().add(component);
    });
    logger.debug("Updated board visuals for {} components", components.size());
  }
}