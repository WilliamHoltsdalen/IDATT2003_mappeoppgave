package edu.ntnu.idi.idatt.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.ntnu.idi.idatt.dto.ComponentDropEventData;
import edu.ntnu.idi.idatt.dto.ComponentSpec;
import edu.ntnu.idi.idatt.dto.TileCoordinates;
import edu.ntnu.idi.idatt.factory.BoardFactory;
import edu.ntnu.idi.idatt.filehandler.BoardFileHandlerGson;
import edu.ntnu.idi.idatt.model.Board;
import edu.ntnu.idi.idatt.observer.ButtonClickObserver;
import edu.ntnu.idi.idatt.view.component.BoardStackPane;
import edu.ntnu.idi.idatt.view.component.TileActionComponent;
import edu.ntnu.idi.idatt.view.container.BoardCreatorView;
import edu.ntnu.idi.idatt.view.util.ViewUtils;
import javafx.application.Platform;

public class BoardCreatorController implements ButtonClickObserver {
  private static final String LADDERS_PATH_PREFIX = "media/assets/ladder/";
  private static final String SLIDES_PATH_PREFIX = "media/assets/slide/";
  private static final String PORTALS_PATH_PREFIX = "media/assets/portal/";

  private final BoardCreatorView view;
  private final BoardStackPane boardPane;
  private Runnable onBackToMenu;
  private final Map<String, String[]> availableComponents;
  private Board board;

  public BoardCreatorController(BoardCreatorView view) {
    this.view = view;
    this.availableComponents = new HashMap<>();
    this.board = BoardFactory.createBlankBoard(9, 10);

    setAvailableComponents();
    initializeBoardCreatorView();

    this.boardPane = view.getBoardStackPane();
  }

  private void setAvailableComponents() {
    availableComponents.put("Ladder", new String[]{
      LADDERS_PATH_PREFIX + "1R_1U_ladder.png",
      LADDERS_PATH_PREFIX + "1L_1U_ladder.png",
      LADDERS_PATH_PREFIX + "1R_2U_ladder.png",
      LADDERS_PATH_PREFIX + "1L_2U_ladder.png",
      LADDERS_PATH_PREFIX + "2R_4U_ladder.png",
      LADDERS_PATH_PREFIX + "2L_4U_ladder.png"
    });
    availableComponents.put("Slide", new String[]{
      SLIDES_PATH_PREFIX + "1R_1D_slide.png",
      SLIDES_PATH_PREFIX + "1L_1D_slide.png",
      SLIDES_PATH_PREFIX + "1R_2D_slide.png",
      SLIDES_PATH_PREFIX + "1L_2D_slide.png"
    });
    availableComponents.put("Portal", new String[]{
      PORTALS_PATH_PREFIX + "1R_1U_portal_1.png",
      PORTALS_PATH_PREFIX + "1R_1U_portal_2.png",
      PORTALS_PATH_PREFIX + "1R_1U_portal_3.png"
    });
    availableComponents.put("Other", new String[]{});
  }

  private void initializeBoardCreatorView() {
    view.addObserver(this);
    
    view.initializeView(availableComponents, board, "media/boards/whiteBoard.png");
    Platform.runLater(() -> {
      boardPane.setOnComponentDropped(this::handleComponentDropped);
      boardPane.setOnRemoveComponentsOutsideGrid(this::removeComponentsOutsideGrid);
    });
  }

  private void handleComponentDropped(ComponentDropEventData data) {
    TileCoordinates coordinates = view.getBoardStackPane().getCellToCoordinatesMap().get(data.cell());
    placeComponent(data.componentIdentifier(), coordinates);
    boardPane.updateBoardVisuals();
    boardPane.applyPattern();
  }

  private void updateBackground() {
    boardPane.setBackground(getBackgroundImagePath());
  }

  private String getBackgroundImagePath() {
    return switch (view.getBackgroundComboBox().getValue()) {
      case "Gray" -> "media/boards/grayBoard.png";
      case "Dark blue" -> "media/boards/darkBlueBoard.png";
      case "Green" -> "media/boards/greenBoard.png";
      case "Red" -> "media/boards/redBoard.png";
      case "Yellow" -> "media/boards/yellowBoard.png";
      case "Pink" -> "media/boards/pinkBoard.png";
      case "Space" -> "media/boards/spaceBoard.png";
      default -> "media/boards/whiteBoard.png"; // for white and default
    };
  }

  public void removeComponentsOutsideGrid() {
    // Recalculate destination tiles for all placed components and remove those that have origin or
    // destination outside the new grid bounds.
    Map<TileCoordinates, TileActionComponent> newPlacedComponents = new HashMap<>();
    boardPane.getComponents().forEach((coordinates, component) -> {
      if (coordinates.row() < board.getRowsAndColumns()[0] && coordinates.col() < board.getRowsAndColumns()[1]) {
        ComponentSpec spec = ComponentSpec.fromFilename(component.getImagePath().substring(component.getImagePath().lastIndexOf("/") + 1));
        int[] destinationCoords = calculateDestinationCoordinates(coordinates, spec);

        if (destinationCoords[0] >= 0 && destinationCoords[0] < board.getRowsAndColumns()[0] &&
            destinationCoords[1] >= 0 && destinationCoords[1] < board.getRowsAndColumns()[1]) {
          int destinationTileId = ViewUtils.calculateTileId(destinationCoords[0], destinationCoords[1], board.getRowsAndColumns()[1]);
          newPlacedComponents.put(coordinates, 
              new TileActionComponent(component.getType(), component.getImagePath(),
                  board.getTile(ViewUtils.calculateTileId(coordinates.row(), coordinates.col(), board.getRowsAndColumns()[1])), 
                  destinationTileId));
        }
      }
    });
    boardPane.getComponents().clear();
    boardPane.getComponents().putAll(newPlacedComponents);
  }

  private int[] calculateDestinationCoordinates(TileCoordinates origin, ComponentSpec spec) {
    return switch (spec.type()) {
      case LADDER -> new int[]{
          origin.row() + spec.heightTiles(),
          origin.col() + (spec.widthDirection() == ComponentSpec.Direction.RIGHT ? spec.widthTiles() : -spec.widthTiles())
      };
      case SLIDE -> new int[]{
          origin.row() - spec.heightTiles(),
          origin.col() + (spec.widthDirection() == ComponentSpec.Direction.RIGHT ? spec.widthTiles() : -spec.widthTiles())
      };
      case PORTAL -> {
        // Portals get a new random destination every time.
        int rows = view.getRowsSpinner().getValue();
        int columns = view.getColumnsSpinner().getValue();
        int originTileId = ViewUtils.calculateTileId(origin.row(), origin.col(), columns);
        List<Integer> occupiedTiles = boardPane.getComponents().values().stream()
            .map(TileActionComponent::getDestinationTileId)
            .toList();
        int destinationTileId = ViewUtils.randomPortalDestination(originTileId, rows * columns, occupiedTiles);
        int[] coords = board.getTile(destinationTileId).getCoordinates();
        yield new int[]{coords[0], coords[1]};
      }
    };
  }

  private void placeComponent(String componentIdentifier, TileCoordinates coordinates) {
    try {
      boardPane.addComponent(componentIdentifier, coordinates);
      updateViewComponentList();
    } catch (IllegalArgumentException e) {
      Platform.runLater(() -> view.showErrorAlert("Could not place component", e.getMessage()));
    }
  }

  private void removeComponent(TileCoordinates coordinates) {
    boardPane.removeComponent(coordinates);
    updateViewComponentList();
  }

  private void updateViewComponentList() {
    view.getComponentListContent().getChildren().clear();
    boardPane.getComponents().forEach((coordinates, component) -> {
      String displayName = component.getType().substring(0, 1).toUpperCase() + component.getType().substring(1);
      int originTileId = ViewUtils.calculateTileId(coordinates.row(), coordinates.col(), board.getRowsAndColumns()[1]);
      int destinationTileId = component.getDestinationTileId();
      view.addToComponentList(displayName, component.getImage(), () -> removeComponent(coordinates), originTileId, destinationTileId);
    });
  }

  @Override
  public void onButtonClicked(String buttonId) {
    switch (buttonId) {
      case "back_to_menu" -> handleBackToMenu();
      case "update_grid" -> handleUpdateGrid();
      case "update_background" -> updateBackground();
      case "update_pattern" -> handleUpdatePattern();
      default -> {
        break;
      }
    }
  }

  @Override
  public void onButtonClickedWithParams(String buttonId, Map<String, Object> params) {
    if (buttonId.equals("save_board")) {
      handleSaveBoard(params);
    }
  }

  private void handleBackToMenu() {
    if (onBackToMenu != null) {
      onBackToMenu.run();
    }
  }

  private void handleUpdateGrid() {
    board = BoardFactory.createBlankBoard(view.getRowsSpinner().getValue(), view.getColumnsSpinner().getValue());
    boardPane.setBoard(board);
    boardPane.updateGrid();
  }

  private void handleUpdatePattern() {
    boardPane.setPattern(view.getPatternComboBox().getValue());
    boardPane.applyPattern();
  }

  private void handleSaveBoard(Map<String, Object> params) {
    try {
      board = boardPane.getBoard();
      board.setName(view.getNameField().getText());
      board.setDescription(view.getDescriptionField().getText());

      String path = (String) params.get("path");
      BoardFileHandlerGson fileHandler = new BoardFileHandlerGson();
      fileHandler.writeFile(path, List.of(board));

      Platform.runLater(() -> view.showInfoAlert("Board saved successfully!",
          "You can now load the board from the main menu, and start playing!"));
    } catch (IOException | IllegalArgumentException e) {
      Platform.runLater(() -> view.showErrorAlert("Failed to save board", e.getMessage()));
    }
  }

  public void setOnBackToMenu(Runnable onBackToMenu) {
    this.onBackToMenu = onBackToMenu;
  }
} 