package edu.ntnu.idi.idatt.controller.laddergame;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.ntnu.idi.idatt.controller.common.BoardCreatorController;
import edu.ntnu.idi.idatt.dto.ComponentDropEventData;
import edu.ntnu.idi.idatt.dto.ComponentSpec;
import edu.ntnu.idi.idatt.dto.TileCoordinates;
import edu.ntnu.idi.idatt.factory.board.LadderBoardFactory;
import edu.ntnu.idi.idatt.filehandler.LadderGameBoardFileHandlerGson;
import edu.ntnu.idi.idatt.model.board.Board;
import edu.ntnu.idi.idatt.model.board.LadderGameBoard;
import edu.ntnu.idi.idatt.view.common.BoardCreatorView;
import edu.ntnu.idi.idatt.view.component.TileActionComponent;
import edu.ntnu.idi.idatt.view.laddergame.LadderGameBoardCreatorView;
import edu.ntnu.idi.idatt.view.util.ViewUtils;
import javafx.application.Platform;

public class LadderGameBoardCreatorController extends BoardCreatorController {
  private static final String LADDERS_PATH_PREFIX = "media/assets/ladder/";
  private static final String SLIDES_PATH_PREFIX = "media/assets/slide/";
  private static final String PORTALS_PATH_PREFIX = "media/assets/portal/";
  
  private final Map<String, String[]> availableComponents;
  private final Map<String, String> availableBackgrounds;
  
  public LadderGameBoardCreatorController(BoardCreatorView view) {
    super(view);
    logger.debug("Constructing LadderGameBoardCreatorController");
    
    this.availableComponents = new HashMap<>();
    this.availableBackgrounds = new HashMap<>();
    
    setAvailableComponents();
    setAvailableBackgrounds();
    
    initializeBoardCreatorView();
  }

  @Override
  protected void initializeBoard() {
    this.boardFactory = new LadderBoardFactory();
    this.board = (LadderGameBoard) boardFactory.createBlankBoard(9, 10);
  }
  
  private void setAvailableComponents() {
    logger.debug("Setting available components");
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
  
  private void setAvailableBackgrounds() {
    logger.debug("Setting available backgrounds");
    availableBackgrounds.put("White", "media/boards/whiteBoard.png");
    availableBackgrounds.put("Gray", "media/boards/grayBoard.png");
    availableBackgrounds.put("Dark blue", "media/boards/darkBlueBoard.png");
    availableBackgrounds.put("Green", "media/boards/greenBoard.png");
    availableBackgrounds.put("Red", "media/boards/redBoard.png");
    availableBackgrounds.put("Yellow", "media/boards/yellowBoard.png");
    availableBackgrounds.put("Pink", "media/boards/pinkBoard.png");
    availableBackgrounds.put("Space", "media/boards/spaceBoard.png");
  }

  @Override
  protected void initializeBoardCreatorView() {
    logger.debug("Initializing board creator view");
    if (!view.getObservers().contains(this)) {
      view.addObserver(this);
    }

    LadderGameBoardCreatorView ladderGameBoardCreatorView = (LadderGameBoardCreatorView) view;
    ladderGameBoardCreatorView.initializeView(availableComponents, (LadderGameBoard) board);
    Platform.runLater(() -> {
      boardPane.setOnComponentDropped(this::handleComponentDropped);
      boardPane.setOnRemoveComponentsOutsideGrid(this::removeComponentsOutsideGrid);
      updateViewComponentList();
    });
    logger.debug("Board creator view initialized successfully");
  }
  
  private void handleComponentDropped(ComponentDropEventData data) {
    logger.debug("Handling component dropped event");
    TileCoordinates coordinates = view.getBoardStackPane().getCellToCoordinatesMap().get(data.cell());
    placeComponent(data.componentIdentifier(), coordinates);
    boardPane.updateBoardVisuals();
  }

  private void updateBackground() {
    boardPane.setBackground(getBackgroundImagePath());
    logger.debug("Updated background");
  }

  private String getBackgroundImagePath() {
    return availableBackgrounds.get(((LadderGameBoardCreatorView) view).getBackgroundComboBox().getValue());
  }

  public void removeComponentsOutsideGrid() {
    logger.debug("Removing components outside grid");
    // Recalculate destination tiles for all placed components and remove those that have origin or
    // destination outside the new grid bounds.
    LadderGameBoard gameBoard = (LadderGameBoard) board;
    Map<TileCoordinates, TileActionComponent> newPlacedComponents = new HashMap<>();
    boardPane.getComponents().forEach((coordinates, component) -> {
      if (coordinates.row() < gameBoard.getRowsAndColumns()[0] && coordinates.col() < gameBoard.getRowsAndColumns()[1]) {
        ComponentSpec spec = ComponentSpec.fromFilename(component.getImagePath().substring(component.getImagePath().lastIndexOf("/") + 1));
        int[] destinationCoords = calculateDestinationCoordinates(coordinates, spec);

        if (destinationCoords[0] >= 0 && destinationCoords[0] < gameBoard.getRowsAndColumns()[0] &&
            destinationCoords[1] >= 0 && destinationCoords[1] < gameBoard.getRowsAndColumns()[1]) {
          int destinationTileId = ViewUtils.calculateTileId(destinationCoords[0], destinationCoords[1], gameBoard.getRowsAndColumns()[1]);
          newPlacedComponents.put(coordinates, 
              new TileActionComponent(component.getType(), component.getImagePath(),
                  gameBoard.getTile(ViewUtils.calculateTileId(coordinates.row(), coordinates.col(), gameBoard.getRowsAndColumns()[1])), 
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
        int rows = ((LadderGameBoardCreatorView) view).getRowsSpinner().getValue();
        int columns = ((LadderGameBoardCreatorView) view).getColumnsSpinner().getValue();
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
    logger.debug("Placing component: {}", componentIdentifier);
    try {
      boardPane.addComponent(componentIdentifier, coordinates);
      updateViewComponentList();
    } catch (IllegalArgumentException e) {
      Platform.runLater(() -> view.showErrorAlert("Could not place component", e.getMessage()));
      logger.warn("Could not place component: {}", componentIdentifier);
    }
  }

  private void removeComponent(TileCoordinates coordinates) {
    logger.debug("Removing component: {}", coordinates);
    boardPane.removeComponent(coordinates);
    updateViewComponentList();
  }

  private void updateViewComponentList() {
    logger.debug("Updating view component list");
    ((LadderGameBoardCreatorView) view).getComponentListContent().getChildren().clear();
    boardPane.getComponents().forEach((coordinates, component) -> {
      String displayName = component.getType().substring(0, 1).toUpperCase() + component.getType().substring(1);
      int originTileId = ViewUtils.calculateTileId(coordinates.row(), coordinates.col(), ((LadderGameBoard) board).getRowsAndColumns()[1]);
      int destinationTileId = component.getDestinationTileId();
      ((LadderGameBoardCreatorView) view).addToComponentList(displayName, component.getImage(), () -> removeComponent(coordinates), originTileId, destinationTileId);
    });
    logger.debug("Added {} components to view component list", ((LadderGameBoardCreatorView) view).getComponentListContent().getChildren().size());
  }

  
  @Override
  protected void handleUpdateGrid() {
    logger.debug("Handling update grid");
    ((LadderGameBoard) board).setRowsAndColumns(new int[]{((LadderGameBoardCreatorView) view).getRowsSpinner().getValue(), ((LadderGameBoardCreatorView) view).getColumnsSpinner().getValue()});
    boardPane.setBoard(board);
    boardPane.updateGrid();
    updateViewComponentList();
  }

  private void handleUpdatePattern() {
    logger.debug("Handling update pattern");
    ((LadderGameBoard) boardPane.getBoard()).setPattern(((LadderGameBoardCreatorView) view).getPatternComboBox().getValue());
    boardPane.applyPattern();
  }

  @Override
  protected void handleImportBoard(Map<String, Object> params) {
    logger.debug("Handling import board");
    String path = (String) params.get("path");
    Board importedBoard = boardFactory.createBoardFromFile(path);
    if (importedBoard == null) {
      Platform.runLater(() -> view.showErrorAlert("Failed to import board", "The board file is empty or invalid."));
      return;
    }
    board = (LadderGameBoard) importedBoard;

    String backgroundName = availableBackgrounds.entrySet().stream()
        .filter(entry -> entry.getValue().equals(board.getBackground()))
        .map(Map.Entry::getKey)
        .findFirst()
        .orElse("White");
    String pattern = ((LadderGameBoard) board).getPattern();
    int rows = ((LadderGameBoard) board).getRowsAndColumns()[0];
    int columns = ((LadderGameBoard) board).getRowsAndColumns()[1];

    ((LadderGameBoardCreatorView) view).setRowSpinner(rows);
    ((LadderGameBoardCreatorView) view).setColumnSpinner(columns);
    view.getNameField().setText(board.getName());
    view.getDescriptionField().setText(board.getDescription());
    ((LadderGameBoardCreatorView) view).getBackgroundComboBox().setValue(backgroundName);
    ((LadderGameBoardCreatorView) view).getPatternComboBox().setValue(pattern);

    boardPane.initialize(board, board.getBackground());
    Platform.runLater(() -> {
      updateViewComponentList();
      logger.info("Board imported successfully");
    });
  }

  @Override
  protected void handleSaveBoard(Map<String, Object> params) {
    logger.debug("Handling save board");
    try {
      board = (LadderGameBoard) boardPane.getBoard();
      board.setName(view.getNameField().getText());
      board.setDescription(view.getDescriptionField().getText());

      String path = (String) params.get("path");
      LadderGameBoardFileHandlerGson fileHandler = new LadderGameBoardFileHandlerGson();
      fileHandler.writeFile(path, List.of(board));

      Platform.runLater(() -> view.showInfoAlert("Board saved successfully!",
          "You can now load the board from the main menu, and start playing!"));
      logger.info("Board saved successfully!");
    } catch (IOException | IllegalArgumentException e) {
      Platform.runLater(() -> view.showErrorAlert("Failed to save board", e.getMessage()));
      logger.warn("Failed to save board: {}", e.getMessage());
    }
  }

  @Override
  public void onButtonClicked(String buttonId) {
    logger.debug("Button clicked: {}", buttonId);
    switch (buttonId) {
      case "back_to_menu" -> handleBackToMenu();
      case "update_grid" -> handleUpdateGrid();
      case "update_background" -> updateBackground();
      case "update_pattern" -> handleUpdatePattern();
      default -> logger.warn("Unknown button clicked: {}", buttonId);
    }
  }
}
