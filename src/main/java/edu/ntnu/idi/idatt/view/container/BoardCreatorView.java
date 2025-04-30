package edu.ntnu.idi.idatt.view.container;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import edu.ntnu.idi.idatt.dto.ComponentDropEventData;
import edu.ntnu.idi.idatt.dto.TileCoordinates;
import edu.ntnu.idi.idatt.observer.ButtonClickObserver;
import edu.ntnu.idi.idatt.observer.ButtonClickSubject;
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
  private Consumer<ComponentDropEventData> onComponentDropped;

  private final Map<Rectangle, TileCoordinates> cellToCoordinatesMap;
  
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

    this.cellToCoordinatesMap = new HashMap<>();

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

  public Map<Rectangle, TileCoordinates> getCellToCoordinatesMap() {
    return cellToCoordinatesMap;
  }

  public VBox getGridContainer() {
    return gridContainer;
  }

  public Pane getComponentsPane() {
    return componentsPane;
  }

  public VBox getComponentListContent() {
    return componentListContent;
  }

  public ImageView getBoardImageView() {
    return boardImageView;
  }

  public TextField getNameField() {
    return nameField;
  }

  public TextField getDescriptionField() {
    return descriptionField;
  }

  public ComboBox<String> getBackgroundComboBox() {
    return backgroundComboBox;
  }

  public ComboBox<String> getPatternComboBox() {
    return patternComboBox;
  }

  public Spinner<Integer> getRowsSpinner() {
    return rowsSpinner;
  }

  public Spinner<Integer> getColumnsSpinner() {
    return columnsSpinner;
  }

  public void setBoardImage(Image image) {
    boardImageView.setImage(image);
  }

  public void setOnComponentDropped(Consumer<ComponentDropEventData> onComponentDropped) {
    this.onComponentDropped = onComponentDropped;
  }

  public void initializeView(Map<String, String[]> components) {
    this.getStyleClass().add("board-creator-view");

    VBox leftPanel = createComponentSelectionPanel(components);
    VBox centerPanel = new VBox(createBoardConfigurationPanel(), boardContainer);
    centerPanel.setAlignment(Pos.CENTER);
    centerPanel.setSpacing(20);
    VBox rightPanel = createComponentListPanel();

    this.setLeft(leftPanel);
    this.setCenter(centerPanel);
    this.setRight(rightPanel);

    this.componentsPane.setPickOnBounds(false); // Allow mouse events on grid
    this.componentsPane.setMouseTransparent(false); // Allow mouse events on grid
  }

  public void initializeBoardImage(Image image) {
    boardImageView.setImage(image);
    boardImageView.setPreserveRatio(true);
    boardImageView.setFitWidth(500);
  }

  private VBox createComponentSelectionPanel(Map<String, String[]> components) {
    VBox panel = new VBox(15);
    panel.setPadding(new Insets(20));
    panel.setMinWidth(300);

    Text title = new Text("Drag components onto the board");
    title.getStyleClass().add("panel-title");

    components.forEach((key, value) -> {
      VBox section = createComponentSection(key, value);
      panel.getChildren().add(section);
    });

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
      String imageName = imagePath.substring(imagePath.lastIndexOf("/") + 1);
      int widthTiles = Integer.parseInt(imageName.substring(0, 1));
      componentImage.setFitWidth(25.0 * widthTiles);
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
    backgroundComboBox.setOnAction(event -> notifyObservers("update_background"));
    backgroundSelectionBox.getChildren().addAll(backgroundLabel, backgroundComboBox);

    VBox patternSelectionBox = new VBox(5);
    Label patternLabel = new Label("Pattern");
    patternComboBox.getItems().addAll("None", "Blue checker", "Yellow checker", "Purple checker");
    patternComboBox.setValue("None");
    patternComboBox.setOnAction(event -> notifyObservers("update_pattern"));
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

  private void setupBoardPreview() {
    boardContainer.getChildren().addAll(boardImageView, gridContainer, componentsPane);
    boardContainer.getStyleClass().add("board-creator-board-container");
    boardContainer.setMaxWidth(
        boardImageView.getFitWidth() + 40); // 40px for padding (20px each side)
    notifyObservers("update_grid");
  }

  public StackPane createRowCell(double cellWidth, double cellHeight, int row, int col) {
    StackPane cellPane = new StackPane();
    Rectangle cellRect = new Rectangle(cellWidth, cellHeight);
    cellRect.getStyleClass().add("grid-cell");
    cellToCoordinatesMap.put(cellRect, new TileCoordinates(row, col));
    setupCellDropHandling(cellRect);

    Label cellLabel = new Label(String.valueOf(ViewUtils.calculateTileId(row, col, columnsSpinner.getValue())));
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

      if (db.hasString() && onComponentDropped != null) {
        String[] dbStringParts = db.getString().split(":");
        onComponentDropped.accept(new ComponentDropEventData(dbStringParts[0], dbStringParts[1], cell));
      }

      event.setDropCompleted(success);
      event.consume();
    });
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