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

public class LadderGameBoardStackPane extends BoardStackPane {

  public LadderGameBoardStackPane() {
    super();
  }

  /**
   * Initializes the board stack pane with the given board and background image path.
   *
   * @param board               the board
   * @param backgroundImagePath the background image path
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
   * Gets the pattern of the board.
   *
   * @return the pattern
   */
  public String getPattern() {
    return ((LadderGameBoard) board).getPattern();
  }

  /**
   * Sets the pattern of the board.
   *
   * @param pattern the pattern
   */
  public void setPattern(String pattern) {
    logger.debug("Setting pattern to: {}", pattern);
    ((LadderGameBoard) board).setPattern(pattern);
    applyPattern();
  }

  /**
   * Loads the components for the board.
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
   * Gets the image path for the given component identifier.
   *
   * @param componentIdentifier the component identifier
   * @return the image path
   */
  @Override
  protected String getImagePath(String componentIdentifier) {
    String[] identifierParts = componentIdentifier.split("_");
    return "media/assets/" + identifierParts[2] + "/" + componentIdentifier + ".png";
  }

  /**
   * Adds a component with the given identifier and coordinates to the board.
   *
   * @param componentIdentifier the component identifier
   * @param coordinates         the coordinates of the component
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
   * Updates the grid of the board.
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
   * Creates a row cell for the board.
   *
   * @param cellWidth  the width of the cell
   * @param cellHeight the height of the cell
   * @param row        the row of the cell
   * @param col        the column of the cell
   * @return the row cell as a StackPane
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
   * Sets up the cell drop handling for the given cell.
   *
   * @param cell the cell to set up
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
   * Applies the board pattern to the board.
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
   * Updates the visuals of the board.
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