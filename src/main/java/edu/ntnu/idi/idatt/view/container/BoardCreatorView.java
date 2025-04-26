package edu.ntnu.idi.idatt.view.container;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import edu.ntnu.idi.idatt.observer.ButtonClickObserver;
import edu.ntnu.idi.idatt.observer.ButtonClickSubject;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

public class BoardCreatorView extends BorderPane implements ButtonClickSubject {
  private final List<ButtonClickObserver> observers;

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
  }

  private VBox createComponentSelectionPanel() {
    VBox panel = new VBox(15);
    panel.setPadding(new Insets(20));
    panel.setMinWidth(300);

    Text title = new Text("Click to Add components");
    title.getStyleClass().add("panel-title");

    VBox[] sections = new VBox[4];
    String[] sectionTitles = {"Ladders", "Slides", "Portals", "Other"};

    for (int i = 0; i < sections.length; i++) {
      sections[i] = new VBox(10);
      sections[i].getStyleClass().add("component-section");
      Text sectionTitle = new Text(sectionTitles[i]);
      sections[i].getChildren().add(sectionTitle);
    }

    panel.getChildren().addAll(title);
    panel.getChildren().addAll(sections);
    panel.getStyleClass().add("board-creator-panel");
    return panel;
  }

  private VBox createBoardConfigurationPanel() {
    VBox panel = new VBox(15);
    panel.getStyleClass().add("board-config-section");
    panel.setAlignment(Pos.CENTER_LEFT);
    panel.setPadding(new Insets(20));

    HBox nameAndDescriptionBox = new HBox(20);
    nameAndDescriptionBox.setAlignment(Pos.CENTER_LEFT);
    nameField.setPromptText("Board Name");
    descriptionField.setPromptText("Description");
    nameAndDescriptionBox.getChildren().setAll(nameField, descriptionField);

    HBox boardOptionsBox = new HBox(20);

    VBox backgroundSelectionBox = new VBox(5);
    Label backgroundLabel = new Label("Background");
    backgroundComboBox.getItems().addAll("White", "Gray", "Blue");
    backgroundComboBox.setValue("White");
    backgroundSelectionBox.getChildren().addAll(backgroundLabel, backgroundComboBox);

    VBox patternSelectionBox = new VBox(5);
    Label patternLabel = new Label("Pattern");
    patternComboBox.getItems().addAll("Blue checker", "Solid", "Dots");
    patternComboBox.setValue("Blue checker");
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

    boardOptionsBox.getChildren().addAll(backgroundSelectionBox, patternSelectionBox, rowsBox, colsBox);

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

    ScrollPane componentList = new ScrollPane();
    componentList.setFitToWidth(true);
    VBox componentListContent = new VBox(10);
    componentList.setContent(componentListContent);

    panel.getChildren().addAll(header, title, componentList);
    panel.getStyleClass().add("board-creator-panel");
    return panel;
  }

  private void setupBoardPreview() {
    boardImageView.setImage(new Image("boardImages/BasicLadderBoard.png"));
    boardImageView.setPreserveRatio(true);
    boardImageView.setFitWidth(500);

    rowsSpinner.valueProperty().addListener((obs, oldVal, newVal) -> updateGrid());
    columnsSpinner.valueProperty().addListener((obs, oldVal, newVal) -> updateGrid());

    boardContainer.getChildren().addAll(boardImageView, gridContainer);
    boardContainer.getStyleClass().add("board-creator-board-container");
    boardContainer.setMaxWidth(boardImageView.getFitWidth() + 40); // 40px for padding (20px each side)
    updateGrid();
  }

  private void updateGrid() {
    gridContainer.getChildren().clear();
    int rows = rowsSpinner.getValue();
    int columns = columnsSpinner.getValue();

    double cellWidth = boardImageView.getFitWidth() / columns;
    double cellHeight = (boardImageView.getFitWidth() / boardImageView.getImage().getWidth()
        * boardImageView.getImage().getHeight()) / rows;

    for (int i = 0; i < rows; i++) {
      HBox row = new HBox();
      row.setAlignment(Pos.CENTER);
      for (int j = 0; j < columns; j++) {
        Rectangle cell = new Rectangle(cellWidth, cellHeight);
        cell.setFill(Color.TRANSPARENT);
        cell.setStroke(Color.BLACK);
        cell.setStrokeWidth(0);
        cell.getStyleClass().add("grid-cell");
        row.getChildren().add(cell);
      }
      gridContainer.getChildren().add(row);
    }
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