package edu.ntnu.idi.idatt.view.container;

import java.util.Map;

import org.kordamp.ikonli.javafx.FontIcon;

import edu.ntnu.idi.idatt.model.board.Board;
import edu.ntnu.idi.idatt.view.common.BoardCreatorView;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
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
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

/**
 * <h3>LadderGameBoardCreatorView</h3>
 * 
 * <p>This class extends BoardCreatorView and is used to create a ladder game board creator view. It adds a component list panel, 
 * as well as background and pattern selection ComboBoxes, and rows and columns spinners.
 * 
 * @see BoardCreatorView
 * @see ComboBox
 * @see Spinner
 */
public class LadderGameBoardCreatorView extends BoardCreatorView {
  private final VBox componentListContent;
  private final ComboBox<String> backgroundComboBox;
  private final ComboBox<String> patternComboBox;
  private final Spinner<Integer> rowsSpinner;
  private final Spinner<Integer> columnsSpinner;
  private ChangeListener<Integer> rowsListener;
  private ChangeListener<Integer> columnsListener;

  /**
   * Constructor for LadderGameBoardCreatorView.
   */
  public LadderGameBoardCreatorView() {
    super();

    this.componentListContent = new VBox(10);
    this.backgroundComboBox = new ComboBox<>();
    this.patternComboBox = new ComboBox<>();
    this.rowsSpinner = new Spinner<>();
    this.columnsSpinner = new Spinner<>();
  }

  /**
   * Getter for the component list content.
   * 
   * @return The component list content.
   */
  public VBox getComponentListContent() {
    return componentListContent;
  }

  /**
   * Getter for the background ComboBox.
   * 
   * @return The background ComboBox.
   */
  public ComboBox<String> getBackgroundComboBox() {
    return backgroundComboBox;
  }

  /**
   * Getter for the pattern ComboBox.
   * 
   * @return The pattern ComboBox.
   */
  public ComboBox<String> getPatternComboBox() {
    return patternComboBox;
  }

  /**
   * Getter for the rows Spinner.
   * 
   * @return The rows Spinner.
   */
  public Spinner<Integer> getRowsSpinner() {
    return rowsSpinner;
  }

  /**
   * Getter for the columns Spinner.
   * 
   * @return The columns Spinner.
   */
  public Spinner<Integer> getColumnsSpinner() {
    return columnsSpinner;
  }

  /**
   * Initializes the view with the given available components and board.
   * 
   * @param components The components to display.
   * @param board The board to display.
   */
  public void initializeView(Map<String, String[]> components, Board board) {
    logger.debug("Initializing LadderGameBoardCreatorView");
    VBox leftPanel = createComponentSelectionPanel(components);

    super.getBoardStackPane().initialize(board, board.getBackground());
    super.getBoardStackPane().getBackgroundImageView().setFitWidth(500);
    VBox centerPanel = new VBox(createBoardConfigurationPanel(), super.getBoardStackPane());
    centerPanel.setAlignment(Pos.CENTER);
    centerPanel.setSpacing(20);
    
    VBox rightPanel = createComponentListPanel();
    
    this.setLeft(leftPanel);
    this.setCenter(centerPanel);
    this.setRight(rightPanel);
    this.getStyleClass().add("board-creator-view");
    logger.debug("Board creator view initialized successfully");
  }

  /**
   * Creates the component selection panel.
   * 
   * @param components The components to display.
   * @return The component selection panel.
   */
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

  /**
   * Creates a component section. This is a section of the component selection panel, 
   * which contains a title and a list of component images.
   * 
   * @param title The title of the section.
   * @param imagePaths The image paths of the components in the section.
   * @return The component section.
   */
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

  /**
   * Sets up the drag and drop functionality for a component image.
   * 
   * @param source The component image to set up.
   * @param imagePath The image path of the component.
   */
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

  /**
   * Creates the board configuration panel. This is a panel that contains the board name and description, 
   * as well as the background and pattern selection ComboBoxes, and rows and columns spinners.
   * 
   * @return The board configuration panel.
   */
  private VBox createBoardConfigurationPanel() {
    VBox panel = new VBox(15);
    panel.getStyleClass().add("board-config-section");
    panel.setAlignment(Pos.CENTER_LEFT);
    panel.setPadding(new Insets(20));

    HBox nameAndDescriptionBox = new HBox(20);
    nameAndDescriptionBox.setAlignment(Pos.CENTER_LEFT);

    VBox nameBox = new VBox(5);
    Label nameLabel = new Label("Board Name");
    super.getNameField().setPromptText("Board Name");
    nameBox.getChildren().addAll(nameLabel, super.getNameField());

    VBox descriptionBox = new VBox(5);
    Label descriptionLabel = new Label("Description");
    super.getDescriptionField().setPromptText("Description");
    descriptionBox.getChildren().addAll(descriptionLabel, super.getDescriptionField());

    Button importBoardButton = new Button("Import board");
    importBoardButton.getStyleClass().add("import-board-button");
    importBoardButton.setOnAction(e -> handleImportBoard());
    nameAndDescriptionBox.getChildren().setAll(nameBox, descriptionBox, importBoardButton);

    HBox boardOptionsBox = new HBox(20);

    VBox backgroundSelectionBox = new VBox(5);
    Label backgroundLabel = new Label("Background");
    backgroundComboBox.getItems().addAll("White", "Gray", "Dark blue", "Green", "Red", "Yellow", "Pink", "Space");
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
    rowsSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(5, 12, 9));
    rowsListener = (obs, oldVal, newVal) -> notifyObservers("update_grid");
    rowsSpinner.valueProperty().addListener(rowsListener);
    rowsBox.getChildren().addAll(rowsLabel, rowsSpinner);

    VBox colsBox = new VBox(5);
    colsBox.getStyleClass().add("board-config-spinner");
    Label colsLabel = new Label("Columns");
    columnsSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(5, 12, 10));
    columnsListener = (obs, oldVal, newVal) -> notifyObservers("update_grid");
    columnsSpinner.valueProperty().addListener(columnsListener);
    colsBox.getChildren().addAll(colsLabel, columnsSpinner);

    boardOptionsBox.getChildren()
        .addAll(backgroundSelectionBox, patternSelectionBox, rowsBox, colsBox);

    panel.getChildren().setAll(nameAndDescriptionBox, boardOptionsBox);
    panel.getStyleClass().add("board-creator-panel");

    panel.setMaxWidth(super.getBoardStackPane().getBackgroundImageView().getFitWidth() + 40); // 40px for padding
    return panel;
  }

  /**
   * Creates the component list panel. This is a panel that contains the component list, 
   * as well as a save button and a menu button.
   * 
   * @return The component list panel.
   */
  private VBox createComponentListPanel() {
    VBox panel = new VBox(10);
    panel.setPadding(new Insets(20));

    HBox header = new HBox();
    header.setAlignment(Pos.CENTER_RIGHT);

    Button saveButton = new Button("Save board");
    saveButton.getStyleClass().add("save-button");
    saveButton.setOnAction(e -> handleSaveBoardClicked());

    MenuButton menuButton = new MenuButton();
    menuButton.setGraphic(new FontIcon("fas-ellipsis-v"));
    menuButton.getStyleClass().add("header-menu-button");

    MenuItem backToMenuMenuItem = new MenuItem("Back to menu");
    backToMenuMenuItem.setGraphic(new FontIcon("fas-home"));
    backToMenuMenuItem.setOnAction(e -> notifyObservers("back_to_menu"));
    menuButton.getItems().add(backToMenuMenuItem);

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

  /**
   * Adds a component to the component list.
   * 
   * @param displayName The display name of the component.
   * @param componentImage The image of the component.
   * @param onDelete The action to perform when the component is deleted.
   * @param originTileId The origin tile id of the component.
   * @param destinationTileId The destination tile id of the component.
   */
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

  public void setRowSpinner(int rows) {
    rowsSpinner.valueProperty().removeListener(rowsListener);
    rowsSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(5, 12, rows));
    rowsSpinner.valueProperty().addListener(rowsListener);
  }

  public void setColumnSpinner(int columns) {
    columnsSpinner.valueProperty().removeListener(columnsListener);
    columnsSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(5, 12, columns));
    columnsSpinner.valueProperty().addListener(columnsListener);
  }
} 