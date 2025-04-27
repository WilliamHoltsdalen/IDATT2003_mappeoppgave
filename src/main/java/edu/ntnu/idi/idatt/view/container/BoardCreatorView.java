package edu.ntnu.idi.idatt.view.container;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import edu.ntnu.idi.idatt.factory.BoardFactory;
import edu.ntnu.idi.idatt.model.Board;
import edu.ntnu.idi.idatt.observer.ButtonClickObserver;
import edu.ntnu.idi.idatt.observer.ButtonClickSubject;
import edu.ntnu.idi.idatt.view.component.TileActionComponent;
import edu.ntnu.idi.idatt.view.util.ViewUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

public class BoardCreatorView extends BorderPane implements ButtonClickSubject {
  private final List<ButtonClickObserver> observers;
  private final Map<Integer, TileActionComponent> placedComponents;
  private final Map<Rectangle, Integer> cellToTileId;
  private Board board;
  private double[] boardDimensions;
  
  private final Pane componentsPane;
  private final VBox componentListContent;
  private final ImageView boardImageView;
  private final ComboBox<String> backgroundComboBox;
  private final ComboBox<String> patternComboBox;
  private final Spinner<Integer> rowsSpinner;
  private final Spinner<Integer> columnsSpinner;
  private final StackPane boardContainer;
  private final VBox gridContainer;
  private final TextField nameField;
  private final TextField descriptionField;

  public BoardCreatorView() {
    this.observers = new ArrayList<>();
    this.placedComponents = new HashMap<>();
    this.cellToTileId = new HashMap<>();
    this.board = BoardFactory.createBlankBoard(9, 10);
    this.boardDimensions = new double[2];

    this.componentsPane = new Pane();
    this.componentListContent = new VBox(10);
    this.boardImageView = new ImageView();
    this.backgroundComboBox = new ComboBox<>();
    this.patternComboBox = new ComboBox<>();
    this.rowsSpinner = new Spinner<>();
    this.columnsSpinner = new Spinner<>();
    this.boardContainer = new StackPane();
    this.gridContainer = new VBox();
    this.nameField = new TextField();
    this.descriptionField = new TextField();
  }

  public void initialize() {
    this.getStyleClass().add("board-creator-view");

    VBox leftPanel = createComponentSelectionPanel();
    VBox centerPanel = new VBox(createBoardConfigurationPanel(), boardContainer);
    centerPanel.setAlignment(Pos.CENTER);
    centerPanel.setSpacing(20);
    VBox rightPanel = createComponentListPanel();

    this.setLeft(leftPanel);
    this.setCenter(centerPanel);
    this.setRight(rightPanel);

    // Bind boardDimensions to the gridContainer's layoutBounds
    // boardDimensions is used to calculate the positions of the tiles in the grid
    gridContainer.layoutBoundsProperty().addListener((obs, oldVal, newVal) -> {
      if (newVal.getWidth() > 0 && newVal.getHeight() > 0) {
        boardDimensions = new double[]{newVal.getWidth(), newVal.getHeight()};
        updateBoardVisuals();
      }
    });

    this.componentsPane.setPickOnBounds(false); // Allow mouse events on grid
    this.componentsPane.setMouseTransparent(false); // Allow mouse events on grid
  }

  private VBox createComponentSelectionPanel() {
    VBox panel = new VBox(15);
    panel.setPadding(new Insets(20));
    panel.setMinWidth(300);

    Text title = new Text("Drag components onto the board");
    title.getStyleClass().add("panel-title");

    VBox laddersSection = createComponentSection("Ladders",
        new String[]{"media/2R_ladder.png", "media/4R_ladder.png"});
    VBox slidesSection = createComponentSection("Slides",
        new String[]{"media/1R_slide.png", "media/2R_slide.png"});
    VBox portalsSection = createComponentSection("Portals",
        new String[]{"media/portal1.png", "media/portal2.png", "media/portal3.png"});
    VBox otherSection = createComponentSection("Other", new String[]{});

    panel.getChildren().addAll(title, laddersSection, slidesSection, portalsSection, otherSection);
    panel.getStyleClass().add("board-creator-panel");
    return panel;
  }

  private VBox createComponentSection(String title, String[] imagePaths) {
    VBox section = new VBox(10);
    section.getStyleClass().add("component-section");

    Text sectionTitle = new Text(title);
    section.getChildren().add(sectionTitle);

    HBox componentsBox = new HBox(10);
    componentsBox.setAlignment(Pos.CENTER_LEFT);

    for (String imagePath : imagePaths) {
      ImageView componentImage = new ImageView(new Image(imagePath));
      componentImage.setFitHeight(50);
      componentImage.setPreserveRatio(true);

      setupDragAndDrop(componentImage, imagePath);
      componentsBox.getChildren().add(componentImage);
    }

    section.getChildren().add(componentsBox);
    return section;
  }

  private void setupDragAndDrop(ImageView source, String imagePath) {
    source.setOnDragDetected(event -> {
      Dragboard db = source.startDragAndDrop(TransferMode.COPY);
      // Create a snapshot of the component image for drag visual
      SnapshotParameters params = new SnapshotParameters();
      params.setFill(Color.TRANSPARENT);
      WritableImage snapshot = source.snapshot(params, null);
      db.setDragView(snapshot);
      // Center the drag view on the mouse cursor
      db.setDragViewOffsetX(snapshot.getWidth() / 2);
      db.setDragViewOffsetY(snapshot.getHeight() / 2);
      
      ClipboardContent content = new ClipboardContent();
      String componentType = getComponentType(imagePath);
      content.putString(componentType + ":" + imagePath);
      db.setContent(content);
      event.consume();
    });
  }

  private String getComponentType(String imagePath) {
    if (imagePath.contains("ladder")) {
      return "LADDER";
    }
    if (imagePath.contains("portal")) {
      return "PORTAL";
    }
    if (imagePath.contains("slide")) {
      return "SLIDE";
    }
    return "OTHER";
  }

  private VBox createBoardConfigurationPanel() {
    VBox panel = new VBox(15);
    panel.getStyleClass().add("board-config-section");
    panel.setAlignment(Pos.CENTER_LEFT);
    panel.setPadding(new Insets(20));

    HBox nameAndDescriptionBox = new HBox(20);
    nameAndDescriptionBox.setAlignment(Pos.CENTER_LEFT);

    VBox nameBox = new VBox(5);
    Label nameLabel = new Label("Board Name");
    nameField.setPromptText("Board Name");
    nameBox.getChildren().addAll(nameLabel, nameField);

    VBox descriptionBox = new VBox(5);
    Label descriptionLabel = new Label("Description");
    descriptionField.setPromptText("Description");
    descriptionBox.getChildren().addAll(descriptionLabel, descriptionField);

    nameAndDescriptionBox.getChildren().setAll(nameBox, descriptionBox);

    HBox boardOptionsBox = new HBox(20);

    VBox backgroundSelectionBox = new VBox(5);
    Label backgroundLabel = new Label("Background");
    backgroundComboBox.getItems().addAll("White", "Gray", "Dark blue", "Green", "Red", "Yellow", "Pink");
    backgroundComboBox.setValue("White");
    backgroundComboBox.setOnAction(event -> updateBackground());
    backgroundSelectionBox.getChildren().addAll(backgroundLabel, backgroundComboBox);

    VBox patternSelectionBox = new VBox(5);
    Label patternLabel = new Label("Pattern");
    patternComboBox.getItems().addAll("None", "Blue checker", "Yellow checker", "Purple checker");
    patternComboBox.setValue("None");
    patternComboBox.setOnAction(event -> setPattern());
    patternSelectionBox.getChildren().addAll(patternLabel, patternComboBox);

    VBox rowsBox = new VBox(5);
    rowsBox.getStyleClass().add("board-config-spinner");
    Label rowsLabel = new Label("Rows");
    rowsSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(5, 20, 9));
    rowsBox.getChildren().addAll(rowsLabel, rowsSpinner);

    VBox colsBox = new VBox(5);
    colsBox.getStyleClass().add("board-config-spinner");
    Label colsLabel = new Label("Columns");
    columnsSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(5, 20, 10));
    colsBox.getChildren().addAll(colsLabel, columnsSpinner);

    boardOptionsBox.getChildren()
        .addAll(backgroundSelectionBox, patternSelectionBox, rowsBox, colsBox);

    panel.getChildren().setAll(nameAndDescriptionBox, boardOptionsBox);
    panel.getStyleClass().add("board-creator-panel");

    setupBoardPreview(); // Setting up the board image preview, so its size can be used.

    panel.setMaxWidth(boardImageView.getFitWidth() + 40); // 40px for padding
    return panel;
  }

  private VBox createComponentListPanel() {
    VBox panel = new VBox(10);
    panel.setPadding(new Insets(20));

    // Header with save button and menu
    HBox header = new HBox();
    header.setAlignment(Pos.CENTER_RIGHT);

    Button saveButton = new Button("Save board");
    saveButton.getStyleClass().add("save-button");
    saveButton.setOnAction(e -> notifyObservers("save_board"));

    Button menuButton = new Button("≡");
    menuButton.setOnAction(e -> notifyObservers("back_to_menu"));
    menuButton.getStyleClass().add("header-menu-button");

    Region spacer = new Region();
    HBox.setHgrow(spacer, Priority.ALWAYS);

    header.getChildren().addAll(saveButton, spacer, menuButton);

    Text title = new Text("Components");
    title.getStyleClass().add("panel-title");

    ScrollPane componentList = new ScrollPane(componentListContent);
    componentList.setHbarPolicy(ScrollBarPolicy.NEVER);
    componentList.setVbarPolicy(ScrollBarPolicy.NEVER);
    componentList.setFitToWidth(true);
    componentList.getStyleClass().add("component-list-scroll-pane");
    componentListContent.getStyleClass().add("component-list-content");

    panel.getChildren().addAll(header, title, componentList);
    panel.getStyleClass().add("board-creator-panel");
    return panel;
  }

  private void updateComponentList() {
    componentListContent.getChildren().clear();

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
      componentListContent.getChildren().add(componentBox);
    });
  }

  private void setupBoardPreview() {
    boardImageView.setImage(new Image(board.getImagePath()));
    boardImageView.setPreserveRatio(true);
    boardImageView.setFitWidth(500);

    rowsSpinner.valueProperty().addListener((obs, oldVal, newVal) -> updateGrid());
    columnsSpinner.valueProperty().addListener((obs, oldVal, newVal) -> updateGrid());

    boardContainer.getChildren().addAll(boardImageView, gridContainer, componentsPane);
    boardContainer.getStyleClass().add("board-creator-board-container");
    boardContainer.setMaxWidth(
        boardImageView.getFitWidth() + 40); // 40px for padding (20px each side)
    updateGrid();
  }

  private void updateBackground() {
    switch (backgroundComboBox.getValue()) {
      case "White" -> boardImageView.setImage(new Image("media/boards/whiteBoard.png"));
      case "Gray" -> boardImageView.setImage(new Image("media/boards/grayBoard.png"));
      case "Dark blue" -> boardImageView.setImage(new Image("media/boards/darkBlueBoard.png"));
      case "Green" -> boardImageView.setImage(new Image("media/boards/greenBoard.png"));
      case "Red" -> boardImageView.setImage(new Image("media/boards/redBoard.png"));
      case "Yellow" -> boardImageView.setImage(new Image("media/boards/yellowBoard.png"));
      case "Pink" -> boardImageView.setImage(new Image("media/boards/pinkBoard.png"));
      default -> throw new IllegalArgumentException(
          "Unknown background: " + backgroundComboBox.getValue());
    }
  }

  private void updateGrid() {
    gridContainer.getChildren().clear();
    cellToTileId.clear();
    int rows = rowsSpinner.getValue();
    int columns = columnsSpinner.getValue();
    board = BoardFactory.createBlankBoard(rows, columns);

    double cellWidth = boardImageView.getFitWidth() / columns;
    double cellHeight = (boardImageView.getFitWidth() / boardImageView.getImage().getWidth()
        * boardImageView.getImage().getHeight()) / rows;

    // Making the cells in the grid
    for (int i = rows - 1; i >= 0; i--) { // Filling rows from top to bottom
      HBox row = new HBox();
      row.setAlignment(Pos.CENTER);
      for (int j = 0; j < columns; j++) { // Filling columns from left to right
        row.getChildren().add(
            createRowCell(cellWidth, cellHeight, ViewUtils.calculateTileId(i, j, columns))
        );
      }
      gridContainer.getChildren().add(row); // Adding row to grid container
    }
    setPattern();
    updateBoardVisuals();
  }

  private StackPane createRowCell(double cellWidth, double cellHeight, int tileId) {
    StackPane cellPane = new StackPane();
    Rectangle cellRect = new Rectangle(cellWidth, cellHeight);
    cellRect.getStyleClass().add("grid-cell");
    cellToTileId.put(cellRect, tileId);
    setupCellDropHandling(cellRect);

    Label cellLabel = new Label(String.valueOf(tileId));
    cellLabel.getStyleClass().add("grid-cell-label");

    cellPane.setAlignment(Pos.BOTTOM_RIGHT);

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

      if (db.hasString()) {
        String[] dbStringParts = db.getString().split(":");
        success = placeComponent(dbStringParts[0], dbStringParts[1], cellToTileId.get(cell));
        updateBoardVisuals();
        setPattern();
      }

      event.setDropCompleted(success);
      event.consume();
    });
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
      showErrorAlert("Could not place component", "Tile " + tileId + " or " + destinationTileId + " is already occupied");
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
    componentsPane.getChildren().clear();

    // Reset all cell colors
    gridContainer.getChildren().forEach(row ->
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

      componentsPane.getChildren().add(component);
    });

    // Update the component list in the right panel
    updateComponentList();
  }

  private Rectangle findCellByTileId(int tileId) {
    return cellToTileId.entrySet().stream()
        .filter(entry -> entry.getValue() == tileId)
        .map(Map.Entry::getKey)
        .findFirst()
        .orElse(null);
  }

  private void setPattern() {
    String selectedPattern = patternComboBox.getValue();
    gridContainer.getChildren().forEach(row ->
        ((HBox) row).getChildren().forEach(cellPane ->
            ((StackPane) cellPane).getChildren().forEach(node -> {
              node.getStyleClass().removeAll("blue-checker", "yellow-checker", "purple-checker");

              List<Integer> occupiedTiles = Stream.concat(
                placedComponents.keySet().stream().filter(id -> !placedComponents.get(id).getType().equals("PORTAL")),
                placedComponents.values().stream().filter(component -> !component.getType().equals("PORTAL")).map(TileActionComponent::getDestinationTileId)
              ).toList();
              if (node instanceof Rectangle rect
                  && cellToTileId.get(rect) % 2 == 0
                  && !occupiedTiles.contains(cellToTileId.get(rect))) {
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

  public void showErrorAlert(String headerText, String message) {
    Alert alert = new Alert(AlertType.ERROR);
    alert.setTitle("Error");
    alert.setHeaderText(headerText);
    alert.setContentText(message); 
    alert.showAndWait();
  }

  @Override
  public void addObserver(ButtonClickObserver observer) {
    observers.add(observer);
  }

  @Override
  public void removeObserver(ButtonClickObserver observer) {
    observers.remove(observer);
  }

  @Override
  public void notifyObservers(String buttonId) {
    observers.forEach(observer -> observer.onButtonClicked(buttonId));
  }

  @Override
  public void notifyObserversWithParams(String buttonId, Map<String, Object> params) {
    observers.forEach(observer -> observer.onButtonClickedWithParams(buttonId, params));
  }
} 