package edu.ntnu.idi.idatt.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import edu.ntnu.idi.idatt.dto.ComponentDropEventData;
import edu.ntnu.idi.idatt.factory.BoardFactory;
import edu.ntnu.idi.idatt.model.Board;
import edu.ntnu.idi.idatt.observer.ButtonClickObserver;
import edu.ntnu.idi.idatt.view.component.TileActionComponent;
import edu.ntnu.idi.idatt.view.container.BoardCreatorView;
import edu.ntnu.idi.idatt.view.util.ViewUtils;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;

public class BoardCreatorController implements ButtonClickObserver {
  private final BoardCreatorView view;
  private Runnable onBackToMenu;
  private final Map<String, String[]> availableComponents;
  private final Map<Integer, TileActionComponent> placedComponents;
  private Board board;
  private double[] boardDimensions;

  public BoardCreatorController(BoardCreatorView view) {
    this.view = view;
    this.availableComponents = new HashMap<>();
    this.placedComponents = new HashMap<>();
    this.board = BoardFactory.createBlankBoard(9, 10);
    this.boardDimensions = new double[2];

    initializeAvailableComponents();
    initializeBoardCreatorView();
  }

  private void initializeAvailableComponents() {
    availableComponents.put("Ladder", new String[]{"media/2R_ladder.png", "media/4R_ladder.png"});
    availableComponents.put("Slide", new String[]{"media/1R_slide.png", "media/2R_slide.png"});
    availableComponents.put("Portal", new String[]{"media/portal1.png", "media/portal2.png", "media/portal3.png"});
    availableComponents.put("Other", new String[]{});
  }

  private void initializeBoardCreatorView() {
    view.addObserver(this);
    view.initializeBoardImage(new Image(board.getImagePath()));
    view.initializeView(availableComponents);
    view.setOnComponentDropped(this::handleComponentDropped);
    setNodeListeners();
    }

  private void setNodeListeners() {
    // Bind boardDimensions to the gridContainer's layoutBounds
    // boardDimensions is used to calculate the positions of the tiles in the grid
    view.getGridContainer().layoutBoundsProperty().addListener((obs, oldVal, newVal) -> {
      if (newVal.getWidth() > 0 && newVal.getHeight() > 0) {
        boardDimensions = new double[]{newVal.getWidth(), newVal.getHeight()};
        updateBoardVisuals();
      }
    });

    // For updating the grid when the rows or columns are changed
    view.getRowsSpinner().valueProperty().addListener((obs, oldVal, newVal) -> updateGrid());
    view.getColumnsSpinner().valueProperty().addListener((obs, oldVal, newVal) -> updateGrid());
  }

  private void handleComponentDropped(ComponentDropEventData data) {
    placeComponent(data.componentType(), data.imagePath(), view.getCellToTileIdMap().get((Rectangle)data.cell()));
    updateBoardVisuals();
    setPattern();
  }

  private void updateComponentList() {
    view.getComponentListContent().getChildren().clear();

    placedComponents.forEach((tileId, component) -> {
      VBox componentBox = new VBox(5);
      componentBox.getStyleClass().add("component-item");

      // Component header with type and delete button
      HBox header = new HBox();
      header.getStyleClass().add("component-item-header");
      header.setAlignment(Pos.CENTER_LEFT);

      // Component type and image
      String displayName = switch (component.getType()) {
        case "LADDER" -> {
          String rows = component.getImagePath().contains("4R") ? "(4R)" : "(2R)";
          yield "Ladder " + rows;
        }
        case "SLIDE" -> {
          String rows = component.getImagePath().contains("2R") ? "(2R)" : "(1R)";
          yield "Slide " + rows;
        }
        case "PORTAL" -> "Portal";
        default -> component.getType();
      };

      Label typeLabel = new Label(displayName);
      typeLabel.setStyle("-fx-font-weight: bold;");

      ImageView componentImage = new ImageView(component.getImage());
      componentImage.setFitHeight(40);
      componentImage.setPreserveRatio(true);

      // Delete button
      Button deleteButton = new Button("×");
      deleteButton.getStyleClass().add("delete-button");
      deleteButton.setOnAction(e -> {
        placedComponents.remove(tileId);
        updateBoardVisuals();
      });

      Region spacer = new Region();
      HBox.setHgrow(spacer, Priority.ALWAYS);
      header.getChildren().addAll(typeLabel, spacer, deleteButton);

      // From-To fields
      HBox fromToBox = new HBox(10);
      fromToBox.setAlignment(Pos.CENTER_LEFT);

      Label fromLabel = new Label("From");
      TextField fromField = new TextField(String.valueOf(tileId));
      fromField.getStyleClass().add("from-to-field");
      fromField.setEditable(false);
      VBox fromVBox = new VBox(10);
      fromVBox.getChildren().addAll(fromLabel, fromField);

      Label toLabel = new Label("To");
      TextField toField = new TextField(String.valueOf(component.getDestinationTileId()));
      toField.getStyleClass().add("from-to-field");
      toField.setEditable(false);
      VBox toVBox = new VBox(10);
      toVBox.getChildren().addAll(toLabel, toField);

      fromToBox.getChildren().addAll(fromVBox, toVBox);

      HBox contentBox = new HBox(20);
      contentBox.getChildren().addAll(componentImage, fromToBox);

      componentBox.getChildren().addAll(header, contentBox);
      view.getComponentListContent().getChildren().add(componentBox);
    });
  }

  private void updateBackground() {
    switch (view.getBackgroundComboBox().getValue()) {
      case "White" -> view.setBoardImage(new Image("media/boards/whiteBoard.png"));
      case "Gray" -> view.setBoardImage(new Image("media/boards/grayBoard.png"));
      case "Dark blue" -> view.setBoardImage(new Image("media/boards/darkBlueBoard.png"));
      case "Green" -> view.setBoardImage(new Image("media/boards/greenBoard.png"));
      case "Red" -> view.setBoardImage(new Image("media/boards/redBoard.png"));
      case "Yellow" -> view.setBoardImage(new Image("media/boards/yellowBoard.png"));
      case "Pink" -> view.setBoardImage(new Image("media/boards/pinkBoard.png"));
      default -> throw new IllegalArgumentException(
          "Unknown background: " + view.getBackgroundComboBox().getValue());
    }
  }

  private void updateGrid() {
    view.getGridContainer().getChildren().clear();
    view.getCellToTileIdMap().clear();
    int rows = view.getRowsSpinner().getValue();
    int columns = view.getColumnsSpinner().getValue();
    board = BoardFactory.createBlankBoard(rows, columns);

    double cellWidth = view.getBoardImageView().getFitWidth() / columns;
    double cellHeight = (view.getBoardImageView().getFitWidth() / view.getBoardImageView().getImage().getWidth()
        * view.getBoardImageView().getImage().getHeight()) / rows;

    // Making the cells in the grid
    for (int i = rows - 1; i >= 0; i--) { // Filling rows from top to bottom
      HBox row = new HBox();
      row.setAlignment(Pos.CENTER);
      for (int j = 0; j < columns; j++) { // Filling columns from left to right
        row.getChildren().add(
            view.createRowCell(cellWidth, cellHeight, ViewUtils.calculateTileId(i, j, columns))
        );
      }
      view.getGridContainer().getChildren().add(row); // Adding row to grid container
    }
    setPattern();
    updateBoardVisuals();
  }

  private boolean placeComponent(String componentType, String imagePath, int tileId) {
    int[] coordinates;
    int destinationTileId = -1;
    List<Integer> occupiedTiles = Stream.concat(
        placedComponents.keySet().stream(),
        placedComponents.values().stream().map(TileActionComponent::getDestinationTileId)
    ).toList();
    switch (imagePath.substring(imagePath.lastIndexOf("/") + 1)) {
      case "4R_ladder.png" -> {
        coordinates = new int[]{board.getTile(tileId).getCoordinates()[0] + 4, board.getTile(tileId).getCoordinates()[1] + 1};
        if (coordinates[0] < board.getRowsAndColumns()[0] && coordinates[1] < board.getRowsAndColumns()[1]) {
          destinationTileId = ViewUtils.calculateTileId(coordinates[0], coordinates[1], board.getRowsAndColumns()[1]);
        }
      }
      case "2R_ladder.png" -> {
        coordinates = new int[]{board.getTile(tileId).getCoordinates()[0] + 2, board.getTile(tileId).getCoordinates()[1] + 1};
        if (coordinates[0] < board.getRowsAndColumns()[0] && coordinates[1] < board.getRowsAndColumns()[1]) {
          destinationTileId = ViewUtils.calculateTileId(coordinates[0], coordinates[1], board.getRowsAndColumns()[1]);
        }
      }
      case "portal1.png", "portal2.png", "portal3.png" -> destinationTileId = ViewUtils.randomPortalDestination(tileId, board.getTiles().size(), occupiedTiles);
      case "2R_slide.png" -> {
        coordinates = new int[]{board.getTile(tileId).getCoordinates()[0] - 2, board.getTile(tileId).getCoordinates()[1] + 1};
        if (coordinates[0] >= 0 && coordinates[1] < board.getRowsAndColumns()[1]) {
          destinationTileId = ViewUtils.calculateTileId(coordinates[0], coordinates[1], board.getRowsAndColumns()[1]);
        }
      }
      case "1R_slide.png" -> {
        coordinates = new int[]{board.getTile(tileId).getCoordinates()[0] - 1, board.getTile(tileId).getCoordinates()[1] + 1};
        if (coordinates[0] >= 0 && coordinates[1] < board.getRowsAndColumns()[1]) {
          destinationTileId = ViewUtils.calculateTileId(coordinates[0], coordinates[1], board.getRowsAndColumns()[1]);
        }
      }
      default -> {
        return false;
      }
    }

    if (occupiedTiles.contains(tileId) || occupiedTiles.contains(destinationTileId)) {
      view.showErrorAlert("Could not place component", "Tile " + tileId + " or " + destinationTileId + " is already occupied");
      return false;
    }
    if (destinationTileId != -1 && destinationTileId <= board.getTiles().size()) {
      placedComponents.put(tileId,
          new TileActionComponent(componentType, imagePath, board.getTile(tileId), destinationTileId));
    }
    return true;
  }

  private void updateBoardVisuals() {
    // Clear all existing visual components
    view.getComponentsPane().getChildren().clear();

    // Reset all cell colors
    view.getGridContainer().getChildren().forEach(row ->
        ((HBox) row).getChildren().forEach(cellPane ->
            ((StackPane) cellPane).getChildren().forEach(rect -> {
              if (rect instanceof Rectangle) {
                rect.getStyleClass().removeAll("grid-cell-has-ladder", "grid-cell-has-slide", "grid-cell-ladder-destination", "grid-cell-slide-destination");
              }
            })
        )
    );

    // Place the visual tile action components
    placedComponents.forEach((tileId, component) -> {
      Rectangle cell = findCellByTileId(tileId);
      if (cell == null) {
        return;
      }

      int destinationTileId = component.getTile().getLandAction().getDestinationTileId();
      Rectangle destinationCell = findCellByTileId(destinationTileId);

      double[] coordinates = ViewUtils.boardToScreenCoordinates(component.getTile().getCoordinates(), board, boardDimensions[0], boardDimensions[1]);

      // Set component properties like sizing, positioning, and style classes for the cells
      switch (component.getType()) {
        case "LADDER" -> {
          component.setFitWidth(cell.getWidth() * 1.5);
          component.setTranslateX(coordinates[0] + component.getFitWidth() *0.2);
          component.setTranslateY(coordinates[1] - (component.getImage().getHeight() * (component.getFitWidth() / component.getImage().getWidth())) - cell.getHeight() * 0.2);
          cell.getStyleClass().add("grid-cell-has-ladder");
          destinationCell.getStyleClass().add("grid-cell-ladder-destination");
        }
        case "PORTAL" -> {
          component.setFitWidth(cell.getWidth() * 1);
          component.setTranslateX(coordinates[0]);
          component.setTranslateY(coordinates[1] - component.getImage().getHeight() * (component.getFitWidth() / component.getImage().getWidth()));
          cell.getStyleClass().add("grid-cell-has-portal");
        }
        case "SLIDE" -> {
          component.setFitWidth(cell.getWidth() * 1.35);
          component.setTranslateX(coordinates[0] + component.getFitWidth() * 0.35);
          component.setTranslateY(coordinates[1]  - cell.getHeight() * 0.3);
          cell.getStyleClass().add("grid-cell-has-slide");
          destinationCell.getStyleClass().add("grid-cell-slide-destination");
        }
        default -> {break;}
      }

      view.getComponentsPane().getChildren().add(component);
    });

    // Update the component list in the right panel
    updateComponentList();
  }

  private Rectangle findCellByTileId(int tileId) {
    return view.getCellToTileIdMap().entrySet().stream()
        .filter(entry -> entry.getValue() == tileId)
        .map(Map.Entry::getKey)
        .findFirst()
        .orElse(null);
  }

  private void setPattern() {
    String selectedPattern = view.getPatternComboBox().getValue();
    view.getGridContainer().getChildren().forEach(row ->
        ((HBox) row).getChildren().forEach(cellPane ->
            ((StackPane) cellPane).getChildren().forEach(node -> {
              node.getStyleClass().removeAll("blue-checker", "yellow-checker", "purple-checker");

              List<Integer> occupiedTiles = Stream.concat(
                  placedComponents.keySet().stream().filter(id -> !placedComponents.get(id).getType().equals("PORTAL")),
                  placedComponents.values().stream().filter(component -> !component.getType().equals("PORTAL")).map(TileActionComponent::getDestinationTileId)
              ).toList();
              if (node instanceof Rectangle rect
                  && view.getCellToTileIdMap().get(rect) % 2 == 0
                  && !occupiedTiles.contains(view.getCellToTileIdMap().get(rect))) {
                switch (selectedPattern) {
                  case "Blue checker" -> rect.getStyleClass().add("blue-checker");
                  case "Yellow checker" -> rect.getStyleClass().add("yellow-checker");
                  case "Purple checker" -> rect.getStyleClass().add("purple-checker");
                  default -> {break;} // No pattern
                }
              }
            })
        )
    );
  }

  @Override
  public void onButtonClicked(String buttonId) {
    switch (buttonId) {
      case "save_board" -> handleSaveBoard();
      case "back_to_menu" -> handleBackToMenu();
      case "update_grid" -> updateGrid();
      case "update_background" -> updateBackground();
      case "update_pattern" -> setPattern();
      default -> {
        break;
      }
    }
  }

  @Override
  public void onButtonClickedWithParams(String buttonId, Map<String, Object> params) {
    // Not needed for now
  }

  private void handleSaveBoard() {
    // TODO: Implement board saving functionality
    // Maybe we can use the snapshot functionality for the board stack pane to save as image,
    // or maybe we should just save as JSON to later be able to load it again and customize it.
  }

  private void handleBackToMenu() {
    if (onBackToMenu != null) {
      onBackToMenu.run();
    }
  }

  public void setOnBackToMenu(Runnable onBackToMenu) {
    this.onBackToMenu = onBackToMenu;
  }
} 