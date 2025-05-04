package edu.ntnu.idi.idatt.view.component;

import edu.ntnu.idi.idatt.model.board.LadderGameBoard;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.ntnu.idi.idatt.dto.ComponentDropEventData;
import edu.ntnu.idi.idatt.dto.ComponentSpec;
import edu.ntnu.idi.idatt.dto.TileCoordinates;
import edu.ntnu.idi.idatt.model.board.Board;
import edu.ntnu.idi.idatt.view.util.ViewUtils;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;

public class BoardStackPane extends StackPane {
  private static final Logger logger = LoggerFactory.getLogger(BoardStackPane.class);

  private final Map<Rectangle, TileCoordinates> cellToCoordinatesMap;
  private final Map<TileCoordinates, TileActionComponent> components;
  private double[] boardDimensions;
  private Consumer<ComponentDropEventData> onComponentDropped;
  private Runnable onRemoveComponentsOutsideGrid;
  private LadderGameBoard board;

  private final ImageView backgroundImageView;
  private final VBox gridContainer;
  private final Pane componentsPane;

  public BoardStackPane() {
    logger.debug("Constructing BoardStackPane");
    this.cellToCoordinatesMap = new HashMap<>();
    this.components = new HashMap<>();
    this.boardDimensions = new double[2];
    this.onComponentDropped = null;
    this.onRemoveComponentsOutsideGrid = null;
    this.board = null;

    this.backgroundImageView = new ImageView();
    this.componentsPane = new Pane();
    this.gridContainer = new VBox();

    this.componentsPane.setPickOnBounds(false); // Allow mouse events on grid beneath the pane
    this.componentsPane.setMouseTransparent(false);

    setNodeListeners();
    this.getChildren().addAll(backgroundImageView, gridContainer, componentsPane);
    this.setMaxWidth(backgroundImageView.getFitWidth() + 40); // 40px for padding (20px each side)
  }

  public void initialize(LadderGameBoard board, String backgroundImagePath) {
    logger.debug("Initializing BoardStackPane");
    reset();
    setBoard(board);
    setBackground(backgroundImagePath);
    Platform.runLater(() -> {
      setPattern(board.getPattern());
      updateGrid();
      loadComponents();
    });
  }

  private void reset() {
    logger.debug("Resetting BoardStackPane");
    cellToCoordinatesMap.clear();
    components.clear();
    gridContainer.getChildren().clear();
    componentsPane.getChildren().clear();
  }

  public Map<Rectangle, TileCoordinates> getCellToCoordinatesMap() {
    return cellToCoordinatesMap;
  }

  public Map<TileCoordinates, TileActionComponent> getComponents() {
    return components;
  }

  public double[] getBoardDimensions() {
    return boardDimensions;
  }

  public Board getBoard() {
    return board;
  }

  public ImageView getBackgroundImageView() {
    return backgroundImageView;
  }

  public Pane getComponentsPane() {
    return componentsPane;
  }

  public VBox getGridContainer() {
    return gridContainer;
  }

  public String getPattern() {
    return board.getPattern();
  }

  public void setBackground(String backgroundImagePath) {
    logger.debug("Setting background to: {}", backgroundImagePath);
    this.board.setBackground(backgroundImagePath);
    backgroundImageView.setImage(new Image(backgroundImagePath));
    backgroundImageView.setPreserveRatio(true);
  }

  public void setPattern(String pattern) {
    logger.debug("Setting pattern to: {}", pattern);
    this.board.setPattern(pattern);
    applyPattern();
  }

  public void setBoardDimensions(double[] boardDimensions) {
    logger.debug("Setting board dimensions to: {}", Arrays.toString(boardDimensions));
    this.boardDimensions = boardDimensions;
  }

  public void setOnComponentDropped(Consumer<ComponentDropEventData> onComponentDropped) {
    this.onComponentDropped = onComponentDropped;
  }

  public void setOnRemoveComponentsOutsideGrid(Runnable onRemoveComponentsOutsideGrid) {
    this.onRemoveComponentsOutsideGrid = onRemoveComponentsOutsideGrid;
  }

  public void setBoard(LadderGameBoard board) {
    logger.debug("Setting board to: {}", board.getName());
    this.board = board;
  }

  private void setNodeListeners() {
    logger.debug("Setting node listeners");
    // Bind boardDimensions to the gridContainer's layoutBounds
    // boardDimensions is used to calculate the positions of the tiles in the grid
    backgroundImageView.layoutBoundsProperty().addListener((obs, oldVal, newVal) -> {
      if (newVal.getWidth() > 0 && newVal.getHeight() > 0) {
        logger.debug("Board dimensions updated to: {}x{}", newVal.getWidth(), newVal.getHeight());
        boardDimensions = new double[]{newVal.getWidth(), newVal.getHeight()};
        updateBoardVisuals();
      }
    });
  }

  private void loadComponents() {
    board.getTiles().forEach(tile -> {
      if (tile.getLandAction() != null) {
        String identifier = tile.getLandAction().getIdentifier();
        String imagePath = getImagePath(identifier);
        ComponentSpec spec = ComponentSpec.fromFilename(imagePath.substring(imagePath.lastIndexOf("/") + 1));
        TileActionComponent component = new TileActionComponent(spec.type().toString(), imagePath, board.getTile(tile.getTileId()), tile.getLandAction().getDestinationTileId());

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
        components.put(new TileCoordinates(tile.getCoordinates()[0], tile.getCoordinates()[1]), component);
      }
    });
    logger.debug("Loaded {} components", components.size());
    updateBoardVisuals();
  }

  private String getImagePath(String componentIdentifier) {
    String[] identifierParts = componentIdentifier.split("_");
    return "media/assets/" + identifierParts[2] + "/" + componentIdentifier + ".png";
  }

  public void addComponent(String componentIdentifier, TileCoordinates coordinates) {
    logger.debug("Adding component: {}", componentIdentifier);
    String imagePath = getImagePath(componentIdentifier);
    ComponentSpec spec = ComponentSpec.fromFilename(imagePath.substring(imagePath.lastIndexOf("/") + 1));
    int[] destinationCoords;
    int destinationTileId = -1;

    List<TileCoordinates> occupiedTiles = Stream.concat(
        components.keySet().stream(),
        components.values().stream()
            .map(component -> {
              int[] coords = board.getTile(component.getTile().getLandAction().getDestinationTileId()).getCoordinates();
              return new TileCoordinates(coords[0], coords[1]);
            })
    ).toList();

    int tileId = ViewUtils.calculateTileId(coordinates.row(), coordinates.col(), board.getRowsAndColumns()[1]);

    // Calculate destination based on component specification
    switch (spec.type()) {
      case LADDER -> {
        destinationCoords = new int[]{
            coordinates.row() + spec.heightTiles(),
            coordinates.col() + (spec.widthDirection() == ComponentSpec.Direction.RIGHT ? spec.widthTiles() : -spec.widthTiles())
        };
        if (destinationCoords[0] < board.getRowsAndColumns()[0] &&
            destinationCoords[1] >= 0 &&
            destinationCoords[1] < board.getRowsAndColumns()[1]) {
          destinationTileId = ViewUtils.calculateTileId(destinationCoords[0], destinationCoords[1], board.getRowsAndColumns()[1]);
        }
      }
      case SLIDE -> {
        destinationCoords = new int[]{
            coordinates.row() - spec.heightTiles(),
            coordinates.col() + (spec.widthDirection() == ComponentSpec.Direction.RIGHT ? spec.widthTiles() : -spec.widthTiles())
        };
        if (destinationCoords[0] >= 0 &&
            destinationCoords[1] >= 0 &&
            destinationCoords[1] < board.getRowsAndColumns()[1]) {
          destinationTileId = ViewUtils.calculateTileId(destinationCoords[0], destinationCoords[1], board.getRowsAndColumns()[1]);
        }
      }
      case PORTAL -> destinationTileId = ViewUtils.randomPortalDestination(tileId, board.getTiles().size(),
          occupiedTiles.stream().map(coords -> ViewUtils.calculateTileId(coords.row(), coords.col(), board.getRowsAndColumns()[1])).toList());
    }

    if (occupiedTiles.contains(coordinates) || (destinationTileId != -1 && occupiedTiles.contains(new TileCoordinates(
        board.getTile(destinationTileId).getCoordinates()[0],
        board.getTile(destinationTileId).getCoordinates()[1])))) {
      logger.warn("Tile is already occupied: {}", coordinates);
      throw new IllegalArgumentException("Tile is already occupied"); // TODO: Replace with a more specific exception
    }

    if (destinationTileId != -1 && destinationTileId <= board.getTiles().size()) {
      TileActionComponent component = new TileActionComponent(spec.type().toString(), imagePath, board.getTile(tileId), destinationTileId);

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

  public void removeComponent(TileCoordinates coordinates) {
    logger.debug("Removing component from: {}", coordinates);
    components.remove(coordinates);
    updateBoardVisuals();
  }

  public void updateGrid() {
    logger.debug("Updating grid");
    gridContainer.getChildren().clear();
    cellToCoordinatesMap.clear();

    if (onRemoveComponentsOutsideGrid != null) {
      onRemoveComponentsOutsideGrid.run();
    }

    double cellWidth = backgroundImageView.getFitWidth() / board.getRowsAndColumns()[1];
    double cellHeight = (backgroundImageView.getFitWidth() / backgroundImageView.getImage().getWidth()
        * backgroundImageView.getImage().getHeight()) / board.getRowsAndColumns()[0];

    // Making the cells in the grid
    for (int i = board.getRowsAndColumns()[0] - 1; i >= 0; i--) { // Filling rows from top to bottom
      HBox row = new HBox();
      row.setAlignment(Pos.CENTER);
      for (int j = 0; j < board.getRowsAndColumns()[1]; j++) { // Filling columns from left to right
        row.getChildren().add(
            createRowCell(cellWidth, cellHeight, i, j)
        );
      }
      gridContainer.getChildren().add(row); // Adding row to grid container
    }

    applyPattern();
    updateBoardVisuals();
  }

  public StackPane createRowCell(double cellWidth, double cellHeight, int row, int col) {
    StackPane cellPane = new StackPane();
    Rectangle cellRect = new Rectangle(cellWidth, cellHeight);
    cellRect.getStyleClass().add("grid-cell");
    cellToCoordinatesMap.put(cellRect, new TileCoordinates(row, col));
    setupCellDropHandling(cellRect);

    Label cellLabel = new Label(String.valueOf(ViewUtils.calculateTileId(row, col, board.getRowsAndColumns()[1])));
    cellLabel.getStyleClass().add("grid-cell-label");

    cellPane.setAlignment(Pos.BOTTOM_CENTER);

    cellPane.getChildren().setAll(cellRect, cellLabel);
    return cellPane;
  }

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

  public void applyPattern() {
    logger.debug("Applying pattern: {}", board.getPattern());
    gridContainer.getChildren().forEach(row ->
        ((HBox) row).getChildren().forEach(cellPane ->
            ((StackPane) cellPane).getChildren().forEach(node -> {
              node.getStyleClass().removeAll("blue-checker", "yellow-checker", "purple-checker");

              List<TileCoordinates> occupiedTiles = Stream.concat(
                  components.keySet().stream().filter(coords -> !components.get(coords).getType().equals("PORTAL")),
                  components.values().stream()
                      .filter(component -> !component.getType().equals("PORTAL"))
                      .map(component -> {
                        int[] coords = board.getTile(component.getTile().getLandAction().getDestinationTileId()).getCoordinates();
                        return new TileCoordinates(coords[0], coords[1]);
                      })
              ).toList();

              if (node instanceof Rectangle rect) {
                TileCoordinates coords = cellToCoordinatesMap.get(rect);
                int tileId = ViewUtils.calculateTileId(coords.row(), coords.col(), board.getRowsAndColumns()[1]);
                if (tileId % 2 == 0 && !occupiedTiles.contains(coords)) {
                  switch (board.getPattern()) {
                    case "Blue checker" -> rect.getStyleClass().add("blue-checker");
                    case "Yellow checker" -> rect.getStyleClass().add("yellow-checker");
                    case "Purple checker" -> rect.getStyleClass().add("purple-checker");
                    default -> {break;} // No pattern
                  }
                }
              }
            })
        )
    );
  }

  private Rectangle findCellByCoordinates(TileCoordinates coordinates) {
    return cellToCoordinatesMap.entrySet().stream()
        .filter(entry -> entry.getValue().equals(coordinates))
        .map(Map.Entry::getKey)
        .findFirst()
        .orElse(null);
  }

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

      int destinationTileId = component.getTile().getLandAction().getDestinationTileId();
      int[] destCoords = board.getTile(destinationTileId).getCoordinates();
      Rectangle destinationCell = findCellByCoordinates(new TileCoordinates(destCoords[0], destCoords[1]));

      // Get the base position for this tile
      double[] screenCoords = ViewUtils.boardToScreenCoordinates(
          new int[]{coordinates.row(), coordinates.col()},
          board,
          boardDimensions[0],
          boardDimensions[1]
      );

      // Update component size and position based on tile dimensions and base position
      component.updateSizeAndPosition(originCell.getWidth(), originCell.getHeight(), screenCoords[0], screenCoords[1]);

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
        default -> {break;}
      }

      componentsPane.getChildren().add(component);
    });
    logger.debug("Updated board visuals for {} components", components.size());
  }
}