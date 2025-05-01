package edu.ntnu.idi.idatt.view.container;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.ntnu.idi.idatt.model.Board;
import edu.ntnu.idi.idatt.observer.ButtonClickObserver;
import edu.ntnu.idi.idatt.observer.ButtonClickSubject;
import edu.ntnu.idi.idatt.view.component.BoardStackPane;
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
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;

public class BoardCreatorView extends BorderPane implements ButtonClickSubject {
  private final List<ButtonClickObserver> observers;
  
  private final VBox componentListContent;
  private final ComboBox<String> backgroundComboBox;
  private final ComboBox<String> patternComboBox;
  private final Spinner<Integer> rowsSpinner;
  private final Spinner<Integer> columnsSpinner;
  private final TextField nameField;
  private final TextField descriptionField;
  private final BoardStackPane boardStackPane;

  public BoardCreatorView() {
    this.observers = new ArrayList<>();

    this.componentListContent = new VBox(10);
    this.backgroundComboBox = new ComboBox<>();
    this.patternComboBox = new ComboBox<>();
    this.rowsSpinner = new Spinner<>();
    this.columnsSpinner = new Spinner<>();
    this.nameField = new TextField();
    this.descriptionField = new TextField();
    this.boardStackPane = createBoardStackPane();
  }

  public VBox getComponentListContent() {
    return componentListContent;
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

  public BoardStackPane getBoardStackPane() {
    return boardStackPane;
  }

  public void initializeView(Map<String, String[]> components, Board board, Image backgroundImage) {
    VBox leftPanel = createComponentSelectionPanel(components);
    
    boardStackPane.initialize(board, backgroundImage);
    VBox centerPanel = new VBox(createBoardConfigurationPanel(), boardStackPane);
    centerPanel.setAlignment(Pos.CENTER);
    centerPanel.setSpacing(20);
    
    VBox rightPanel = createComponentListPanel();
    
    this.setLeft(leftPanel);
    this.setCenter(centerPanel);
    this.setRight(rightPanel);
    this.getStyleClass().add("board-creator-view");
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
      String componentIdentifier = imagePath.substring(imagePath.lastIndexOf("/") + 1, imagePath.lastIndexOf("."));
      content.putString(componentIdentifier);
      db.setContent(content);
      event.consume();
    });
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
    rowsSpinner.valueProperty().addListener((obs, oldVal, newVal) -> notifyObservers("update_grid"));
    rowsBox.getChildren().addAll(rowsLabel, rowsSpinner);

    VBox colsBox = new VBox(5);
    colsBox.getStyleClass().add("board-config-spinner");
    Label colsLabel = new Label("Columns");
    columnsSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(5, 20, 10));
    columnsSpinner.valueProperty().addListener((obs, oldVal, newVal) -> notifyObservers("update_grid"));
    colsBox.getChildren().addAll(colsLabel, columnsSpinner);

    boardOptionsBox.getChildren()
        .addAll(backgroundSelectionBox, patternSelectionBox, rowsBox, colsBox);

    panel.getChildren().setAll(nameAndDescriptionBox, boardOptionsBox);
    panel.getStyleClass().add("board-creator-panel");

    panel.setMaxWidth(boardStackPane.getBackgroundImageView().getFitWidth() + 40); // 40px for padding
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
    saveButton.setOnAction(e -> handleSaveBoardClicked());

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

  public void addToComponentList(String displayName, Image componentImage, Runnable onDelete, int originTileId, int destinationTileId) {
    VBox componentBox = new VBox(5);
    componentBox.getStyleClass().add("component-item");

    HBox header = new HBox();
    header.getStyleClass().add("component-item-header");
    header.setAlignment(Pos.CENTER_LEFT);

    Label typeLabel = new Label(displayName);
    typeLabel.setStyle("-fx-font-weight: bold;");

    ImageView componentImageView = new ImageView(componentImage);
    componentImageView.setFitHeight(40);
    componentImageView.setPreserveRatio(true);

    Button deleteButton = new Button("×");
    deleteButton.getStyleClass().add("delete-button");
    deleteButton.setOnAction(e -> onDelete.run());

    Region spacer = new Region();
    HBox.setHgrow(spacer, Priority.ALWAYS);
    header.getChildren().addAll(typeLabel, spacer, deleteButton);

    HBox fromToBox = new HBox(10);
    fromToBox.setAlignment(Pos.CENTER_LEFT);

    Label fromLabel = new Label("From");
    TextField fromField = new TextField(String.valueOf(originTileId));
    fromField.getStyleClass().add("from-to-field");
    fromField.setEditable(false);
    VBox fromVBox = new VBox(10);
    fromVBox.getChildren().addAll(fromLabel, fromField);

    Label toLabel = new Label("To");
    TextField toField = new TextField(String.valueOf(destinationTileId));
    toField.getStyleClass().add("from-to-field");
    toField.setEditable(false);
    VBox toVBox = new VBox(10);
    toVBox.getChildren().addAll(toLabel, toField);

    fromToBox.getChildren().addAll(fromVBox, toVBox);

    HBox contentBox = new HBox(20);
    contentBox.getChildren().addAll(componentImageView, fromToBox);

    componentBox.getChildren().addAll(header, contentBox);
    componentListContent.getChildren().add(componentBox);
  }


  private BoardStackPane createBoardStackPane() {
    BoardStackPane boardContainer = new BoardStackPane();
    boardContainer.getStyleClass().add("board-creator-board-container");
    return boardContainer;
  }

  private void handleSaveBoardClicked() {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Save Board");
    fileChooser.setInitialFileName(nameField.getText() + ".json");

    File file = fileChooser.showSaveDialog(this.getScene().getWindow());
    if (file == null) {
      showErrorAlert("Save Board", "Invalid file path");
      return;
    }
    Map<String, Object> params = new HashMap<>();
    params.put("path", file.getAbsolutePath());
    notifyObserversWithParams("save_board", params);
  }

  public void showInfoAlert(String headerText, String message) {
    Alert alert = new Alert(AlertType.INFORMATION);
    alert.setTitle("Info");
    alert.setHeaderText(headerText);
    alert.setContentText(message);
    alert.showAndWait();
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