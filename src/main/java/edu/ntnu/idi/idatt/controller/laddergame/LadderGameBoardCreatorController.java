package edu.ntnu.idi.idatt.controller.laddergame;

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
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.application.Platform;

/**
 * <h3>LadderGameBoardCreatorController.</h3>
 *
 * <p>Controller responsible for the logic behind creating and editing {@link LadderGameBoard}s.
 * It extends {@link BoardCreatorController} and manages interactions with a
 * {@link LadderGameBoardCreatorView}.</p>
 *
 * <p>This controller handles tasks such as initializing the board with default or imported values,
 * managing available components (ladders, slides, portals) and backgrounds, processing component
 * placement and removal, updating the grid based on user input (rows, columns, pattern), and
 * saving/importing board configurations.</p>
 *
 * @see BoardCreatorController
 * @see LadderGameBoard
 * @see LadderGameBoardCreatorView
 * @see LadderBoardFactory
 * @see LadderGameBoardFileHandlerGson
 */
public class LadderGameBoardCreatorController extends BoardCreatorController {

  private static final String LADDERS_PATH_PREFIX = "media/assets/ladder/";
  private static final String SLIDES_PATH_PREFIX = "media/assets/slide/";
  private static final String PORTALS_PATH_PREFIX = "media/assets/portal/";

  private final Map<String, String[]> availableComponents;
  private final Map<String, String> availableBackgrounds;

  /**
   * Constructs a LadderGameBoardCreatorController.
   *
   * @param view The {@link BoardCreatorView} (expected to be a {@link LadderGameBoardCreatorView})
   *             associated with this controller.
   */
  public LadderGameBoardCreatorController(BoardCreatorView view) {
    super(view);
    logger.debug("Constructing LadderGameBoardCreatorController");

    this.availableComponents = new HashMap<>();
    this.availableBackgrounds = new HashMap<>();

    setAvailableComponents();
    setAvailableBackgrounds();

    initializeBoardCreatorView();
  }

  /**
   * Initializes the {@link #board} instance as a new, blank {@link LadderGameBoard} using a
   * {@link LadderBoardFactory} with default dimensions.
   */
  @Override
  protected void initializeBoard() {
    this.boardFactory = new LadderBoardFactory();
    this.board = (LadderGameBoard) boardFactory.createBlankBoard(9, 10);
  }

  /**
   * Populates the {@link #availableComponents} map with predefined paths to image assets for
   * ladders, slides, and portals.
   */
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

  /**
   * Populates the {@link #availableBackgrounds} map with names and paths for predefined board
   * background images.
   */
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

  /**
   * Initializes the {@link LadderGameBoardCreatorView}. Sets this controller as an observer to the
   * view, initializes the view with available components and the current board, and sets up event
   * handlers for component drops and removal.
   */
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

  /**
   * Handles the event when a component is dropped onto the board grid in the view. Retrieves the
   * target cell's coordinates and calls {@link #placeComponent(String, TileCoordinates)}.
   *
   * @param data The {@link ComponentDropEventData} containing the component identifier and target
   *             cell.
   */
  private void handleComponentDropped(ComponentDropEventData data) {
    logger.debug("Handling component dropped event");
    TileCoordinates coordinates = view.getBoardStackPane().getCellToCoordinatesMap()
        .get(data.cell());
    placeComponent(data.componentIdentifier(), coordinates);
    boardPane.updateBoardVisuals();
  }

  /**
   * Updates the background of the board in the {@link #boardPane} based on the selection in the
   * view's background combo box.
   */
  private void updateBackground() {
    boardPane.setBackground(getBackgroundImagePath());
    logger.debug("Updated background");
  }

  /**
   * Retrieves the file path for the currently selected background image from the view's combo box.
   *
   * @return The file path string for the selected background.
   */
  private String getBackgroundImagePath() {
    return availableBackgrounds.get(((LadderGameBoardCreatorView) view).getBackgroundComboBox()
        .getValue());
  }

  /**
   * Removes components from the board model and view if their origin or calculated destination
   * falls outside the current grid boundaries (e.g., after grid dimensions are changed). It
   * recalculates destinations for remaining valid components.
   */
  public void removeComponentsOutsideGrid() {
    logger.debug("Removing components outside grid");
    // Recalculate destination tiles for all placed components and remove those that have origin or
    // destination outside the new grid bounds.
    LadderGameBoard gameBoard = (LadderGameBoard) board;
    Map<TileCoordinates, TileActionComponent> newPlacedComponents = new HashMap<>();
    boardPane.getComponents().forEach((coordinates, component) -> {
      if (coordinates.row() < gameBoard.getRowsAndColumns()[0]
          && coordinates.col() < gameBoard.getRowsAndColumns()[1]) {
        ComponentSpec spec = ComponentSpec.fromFilename(component.getImagePath()
            .substring(component.getImagePath().lastIndexOf("/") + 1));
        int[] destinationCoords = calculateDestinationCoordinates(coordinates, spec);

        if (destinationCoords[0] >= 0 && destinationCoords[0] < gameBoard.getRowsAndColumns()[0]
            && destinationCoords[1] >= 0
            && destinationCoords[1] < gameBoard.getRowsAndColumns()[1]) {
          int destinationTileId = ViewUtils.calculateTileId(destinationCoords[0],
              destinationCoords[1], gameBoard.getRowsAndColumns()[1]);
          newPlacedComponents.put(coordinates,
              new TileActionComponent(component.getType(), component.getImagePath(),
                  gameBoard.getTile(ViewUtils.calculateTileId(coordinates.row(), coordinates.col(),
                      gameBoard.getRowsAndColumns()[1])), destinationTileId));
        }
      }
    });
    boardPane.getComponents().clear();
    boardPane.getComponents().putAll(newPlacedComponents);
  }

  /**
   * Calculates the destination coordinates for a component based on its origin and specification.
   * For portals, a random valid destination is chosen.
   *
   * @param origin The {@link TileCoordinates} of the component's origin.
   * @param spec   The {@link ComponentSpec} defining the component's type, size, and direction.
   * @return An array [row, column] of the destination coordinates.
   */
  private int[] calculateDestinationCoordinates(TileCoordinates origin, ComponentSpec spec) {
    return switch (spec.type()) {
      case LADDER -> new int[]{
          origin.row() + spec.heightTiles(),
          origin.col() + (spec.widthDirection() == ComponentSpec.Direction.RIGHT
              ? spec.widthTiles() : -spec.widthTiles())
      };
      case SLIDE -> new int[]{
          origin.row() - spec.heightTiles(),
          origin.col() + (spec.widthDirection() == ComponentSpec.Direction.RIGHT
              ? spec.widthTiles() : -spec.widthTiles())
      };
      case PORTAL -> {
        // Portals get a new random destination every time.
        int rows = ((LadderGameBoardCreatorView) view).getRowsSpinner().getValue();
        int columns = ((LadderGameBoardCreatorView) view).getColumnsSpinner().getValue();
        int originTileId = ViewUtils.calculateTileId(origin.row(), origin.col(), columns);
        List<Integer> occupiedTiles = boardPane.getComponents().values().stream()
            .map(TileActionComponent::getDestinationTileId)
            .toList();
        int destinationTileId = ViewUtils.randomPortalDestination(originTileId,
            rows * columns, occupiedTiles);
        int[] coords = board.getTile(destinationTileId).getCoordinates();
        yield new int[]{coords[0], coords[1]};
      }
    };
  }

  /**
   * Places a component with the given identifier at the specified coordinates on the board. Updates
   * the view's component list. Shows an error alert if placement fails.
   *
   * @param componentIdentifier The string identifier of the component to place.
   * @param coordinates         The {@link TileCoordinates} for the component's origin.
   */
  private void placeComponent(String componentIdentifier, TileCoordinates coordinates) {
    logger.debug("Placing component: {}", componentIdentifier);
    try {
      boardPane.addComponent(componentIdentifier, coordinates);
      updateViewComponentList();
    } catch (IllegalArgumentException e) {
      Platform.runLater(() -> view.showErrorAlert("Could not place component",
          e.getMessage()));
      logger.warn("Could not place component: {}", componentIdentifier);
    }
  }

  /**
   * Removes a component from the specified coordinates on the board. Updates the view's component
   * list.
   *
   * @param coordinates The {@link TileCoordinates} of the component to remove.
   */
  private void removeComponent(TileCoordinates coordinates) {
    logger.debug("Removing component: {}", coordinates);
    boardPane.removeComponent(coordinates);
    updateViewComponentList();
  }

  /**
   * Updates the list of placed components displayed in the {@link LadderGameBoardCreatorView}.
   * Clears the existing list and repopulates it based on components currently in
   * {@link #boardPane}.
   */
  private void updateViewComponentList() {
    logger.debug("Updating view component list");
    ((LadderGameBoardCreatorView) view).getComponentListContent().getChildren().clear();
    boardPane.getComponents().forEach((coordinates, component) -> {
      String displayName = component.getType().substring(0, 1).toUpperCase()
          + component.getType().substring(1);
      int originTileId = ViewUtils.calculateTileId(coordinates.row(), coordinates.col(),
          ((LadderGameBoard) board).getRowsAndColumns()[1]);
      int destinationTileId = component.getDestinationTileId();
      ((LadderGameBoardCreatorView) view).addToComponentList(displayName, component.getImage(),
          () -> removeComponent(coordinates), originTileId, destinationTileId);
    });
    logger.debug("Added {} components to view component list", ((LadderGameBoardCreatorView) view)
        .getComponentListContent().getChildren().size());
  }

  /**
   * Handles the action to update the board's grid dimensions (rows and columns) based on values
   * from the view's spinners. Updates the board model and refreshes the grid display.
   */
  @Override
  protected void handleUpdateGrid() {
    logger.debug("Handling update grid");
    ((LadderGameBoard) board).setRowsAndColumns(new int[]{((LadderGameBoardCreatorView) view)
        .getRowsSpinner().getValue(),
        ((LadderGameBoardCreatorView) view).getColumnsSpinner().getValue()});
    boardPane.setBoard(board);
    boardPane.updateGrid();
    updateViewComponentList();
  }

  /**
   * Handles the action to update the visual pattern of the board's grid based on the selection in
   * the view's pattern combo box.
   */
  private void handleUpdatePattern() {
    logger.debug("Handling update pattern");
    ((LadderGameBoard) boardPane.getBoard()).setPattern(((LadderGameBoardCreatorView) view)
        .getPatternComboBox().getValue());
    boardPane.applyPattern();
  }

  /**
   * Handles the import of a board configuration from a file. Updates the internal {@link #board}
   * model and refreshes the view's input fields (name, description, rows, columns, background,
   * pattern) and the board display.
   *
   * @param params A map containing the "path" (String) to the board file.
   */
  @Override
  protected void handleImportBoard(Map<String, Object> params) {
    logger.debug("Handling import board");
    String path = (String) params.get("path");
    Board importedBoard = boardFactory.createBoardFromFile(path);
    if (importedBoard == null) {
      Platform.runLater(() -> view.showErrorAlert("Failed to import board",
          "The board file is empty or invalid."));
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

  /**
   * Handles saving the current board configuration to a file. Updates the board model with values
   * from the view's input fields before saving. Uses {@link LadderGameBoardFileHandlerGson} for
   * serialization.
   *
   * @param params A map containing the "path" (String) to save the file to.
   */
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
      Platform.runLater(() -> view.showErrorAlert("Failed to save board",
          e.getMessage()));
      logger.warn("Failed to save board: {}", e.getMessage());
    }
  }

  /**
   * Handles button click events from the {@link LadderGameBoardCreatorView}. Delegates actions
   * based on the button ID (e.g., back to menu, update grid, update background, update pattern).
   *
   * @param buttonId The ID of the button that was clicked.
   */
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
