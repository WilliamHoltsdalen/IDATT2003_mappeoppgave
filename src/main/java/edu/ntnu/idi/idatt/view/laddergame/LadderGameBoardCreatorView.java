package edu.ntnu.idi.idatt.view.laddergame;

import edu.ntnu.idi.idatt.controller.laddergame.LadderGameBoardCreatorController;
import edu.ntnu.idi.idatt.model.board.LadderGameBoard;
import edu.ntnu.idi.idatt.view.common.BoardCreatorView;
import edu.ntnu.idi.idatt.view.common.BoardStackPane;
import java.util.Map;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.SnapshotParameters;
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
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import org.kordamp.ikonli.javafx.FontIcon;

/**
 * LadderGameBoardCreatorView.
 *
 * <p>This class extends {@link BoardCreatorView} and is responsible for presenting the UI
 * for creating and customizing {@link LadderGameBoard}s. It allows users to drag and drop
 * components (ladders, slides, portals) onto a visual representation of the board,
 * configure board dimensions (rows, columns), select background images and patterns,
 * and manage the list of placed components.</p>
 *
 * <p>The view provides input fields for the board's name and description, controls for
 * importing existing board configurations, and buttons for saving the created board or
 * returning to the main menu.</p>
 *
 * @see BoardCreatorView
 * @see LadderGameBoard
 * @see LadderGameBoardCreatorController
 * @see LadderGameBoardStackPane
 * @see ComboBox
 * @see Spinner
 * @see Button
 * @see Label
 * @see TextField
 * @see ImageView
 * @see VBox
 * @see HBox
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
   * Constructs a new {@code LadderGameBoardCreatorView} instance.
   * Initializes the UI elements for component listing, background/pattern selection,
   * and board dimension controls.
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
   * Returns the VBox container that holds the list of placed components.
   * Each item in this list typically displays information about a component,
   * such as its type, image, and origin/destination tiles.
   *
   * @return The {@link VBox} for the component list.
   */
  public VBox getComponentListContent() {
    return componentListContent;
  }

  /**
   * Returns the ComboBox used for selecting the board's background image.
   *
   * @return The {@link ComboBox} for background selection.
   */
  public ComboBox<String> getBackgroundComboBox() {
    return backgroundComboBox;
  }

  /**
   * Returns the ComboBox used for selecting the visual pattern applied to the board's grid.
   *
   * @return The {@link ComboBox} for pattern selection.
   */
  public ComboBox<String> getPatternComboBox() {
    return patternComboBox;
  }

  /**
   * Returns the Spinner used for adjusting the number of rows on the board.
   *
   * @return The {@link Spinner} for row selection.
   */
  public Spinner<Integer> getRowsSpinner() {
    return rowsSpinner;
  }

  /**
   * Returns the Spinner used for adjusting the number of columns on the board.
   *
   * @return The {@link Spinner} for column selection.
   */
  public Spinner<Integer> getColumnsSpinner() {
    return columnsSpinner;
  }

  /**
   * Sets the value of the rows spinner and re-attaches its listener.
   * This is typically used when loading an existing board configuration.
   *
   * @param rows The number of rows to set.
   */
  public void setRowSpinner(int rows) {
    rowsSpinner.valueProperty().removeListener(rowsListener);
    rowsSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(5, 12, rows));
    rowsSpinner.valueProperty().addListener(rowsListener);
  }

  /**
   * Sets the value of the columns spinner and re-attaches its listener.
   * This is typically used when loading an existing board configuration.
   *
   * @param columns The number of columns to set.
   */
  public void setColumnSpinner(int columns) {
    columnsSpinner.valueProperty().removeListener(columnsListener);
    columnsSpinner.setValueFactory(
        new SpinnerValueFactory.IntegerSpinnerValueFactory(5, 12, columns));
    columnsSpinner.valueProperty().addListener(columnsListener);
  }

  /**
   * Initializes the main view components of the ladder game board creator.
   * This method sets up the component selection panel, the board configuration panel,
   * and the central board display area. It also configures the overall layout and
   * styling of the view.
   *
   * @param components A map where keys are component categories (e.g., "Ladder", "Slide")
   *                   and values are arrays of image paths for the components in that category.
   * @param board      The {@link LadderGameBoard} instance to be displayed and configured.
   */
  public void initializeView(Map<String, String[]> components, LadderGameBoard board) {
    logger.debug("Initializing LadderGameBoardCreatorView");
    final VBox leftPanel = createComponentSelectionPanel(components);

    super.getBoardStackPane().initialize(board, board.getBackground());
    super.getBoardStackPane().getBackgroundImageView().setFitWidth(500);
    final VBox centerPanel = new VBox(createBoardConfigurationPanel(), super.getBoardStackPane());
    centerPanel.setAlignment(Pos.CENTER);
    centerPanel.setSpacing(20);

    final VBox rightPanel = createComponentListPanel();

    this.setLeft(leftPanel);
    this.setCenter(centerPanel);
    this.setRight(rightPanel);
    this.getStyleClass().add("board-creator-view");
    logger.debug("Board creator view initialized successfully");
  }

  /**
   * Creates and returns a {@link LadderGameBoardStackPane} instance to be used as the
   * central area for displaying the board being created. This method overrides the
   * parent class's method to provide a specific type of board container.
   *
   * @return A new {@link LadderGameBoardStackPane} for the board display.
   */
  @Override
  protected BoardStackPane createBoardStackPane() {
    LadderGameBoardStackPane boardContainer = new LadderGameBoardStackPane();
    boardContainer.getStyleClass().add("board-creator-board-container");
    return boardContainer;
  }

  /**
   * Creates the left panel of the view, which allows users to select and drag
   * game components (like ladders and slides) onto the board.
   *
   * @param components A map where keys are component type names (e.g., "Ladder")
   *                   and values are arrays of image paths for those components.
   * @return A {@link VBox} containing the component selection UI.
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
   * Creates a section within the component selection panel for a specific type of component.
   * This section includes a title (e.g., "Ladders") and a horizontal box of draggable
   * images representing the available components of that type.
   *
   * @param title      The title for the component section (e.g., "Ladder", "Slide").
   * @param imagePaths An array of string paths to the images for the components in this section.
   * @return A {@link VBox} representing the component section.
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
      componentImage.getStyleClass().add("component-section-image");
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
   * Configures drag-and-drop functionality for a given component {@link ImageView}.
   * When a drag is detected on the source image:
   * <ul>
   *   <li>A dragboard is initiated with {@link TransferMode#COPY}.</li>
   *   <li>A snapshot of the image is used as the drag view, centered on the cursor.</li>
   *   <li>The component's identifier (derived from its image filename) is put onto the
   *       dragboard.</li>
   * </ul>
   *
   * @param source    The {@link ImageView} that will act as the drag source.
   * @param imagePath The path to the image file, used to derive the component identifier.
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
      String componentIdentifier = imagePath.substring(imagePath.lastIndexOf("/") + 1,
          imagePath.lastIndexOf("."));
      content.putString(componentIdentifier);
      db.setContent(content);
      event.consume();
    });
  }

  /**
   * Creates the central panel located above the board display, used for configuring
   * general board properties. This panel includes:
   * <ul>
   *   <li>Input fields for the board's name and description.</li>
   *   <li>An "Import board" button.</li>
   *   <li>ComboBoxes for selecting the board background and grid pattern.</li>
   *   <li>Spinners for adjusting the number of rows and columns.</li>
   * </ul>
   *
   * @return A {@link VBox} containing the board configuration controls.
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

    final HBox boardOptionsBox = new HBox(20);

    final VBox backgroundSelectionBox = new VBox(5);
    final Label backgroundLabel = new Label("Background");
    backgroundComboBox.getItems()
        .addAll("White", "Gray", "Dark blue", "Green", "Red", "Yellow", "Pink", "Space");
    backgroundComboBox.setValue("White");
    backgroundComboBox.setOnAction(event -> notifyObservers("update_background"));
    backgroundSelectionBox.getChildren().addAll(backgroundLabel, backgroundComboBox);

    final VBox patternSelectionBox = new VBox(5);
    final Label patternLabel = new Label("Pattern");
    patternComboBox.getItems().addAll("None", "Blue checker", "Yellow checker", "Purple checker");
    patternComboBox.setValue("None");
    patternComboBox.setOnAction(event -> notifyObservers("update_pattern"));
    patternSelectionBox.getChildren().addAll(patternLabel, patternComboBox);

    final VBox rowsBox = new VBox(5);
    rowsBox.getStyleClass().add("board-config-spinner");
    final Label rowsLabel = new Label("Rows");
    rowsSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(5, 12, 9));
    rowsListener = (obs, oldVal, newVal) -> notifyObservers("update_grid");
    rowsSpinner.valueProperty().addListener(rowsListener);
    rowsBox.getChildren().addAll(rowsLabel, rowsSpinner);

    VBox colsBox = new VBox(5);
    colsBox.getStyleClass().add("board-config-spinner");
    final Label colsLabel = new Label("Columns");
    columnsSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(5, 12, 10));
    columnsListener = (obs, oldVal, newVal) -> notifyObservers("update_grid");
    columnsSpinner.valueProperty().addListener(columnsListener);
    colsBox.getChildren().addAll(colsLabel, columnsSpinner);

    boardOptionsBox.getChildren()
        .addAll(backgroundSelectionBox, patternSelectionBox, rowsBox, colsBox);

    panel.getChildren().setAll(nameAndDescriptionBox, boardOptionsBox);
    panel.getStyleClass().add("board-creator-panel");

    panel.setMaxWidth(
        super.getBoardStackPane().getBackgroundImageView().getFitWidth() + 40); // 40px for padding
    return panel;
  }

  /**
   * Creates the right panel of the view, which displays a list of components
   * currently placed on the board. It also includes buttons for saving the board
   * and returning to the main menu.
   *
   * @return A {@link VBox} containing the component list and action buttons.
   */
  private VBox createComponentListPanel() {
    VBox panel = new VBox(10);
    panel.setPadding(new Insets(20));

    HBox buttonBox = new HBox(10);
    buttonBox.setAlignment(Pos.CENTER);

    Button saveButton = new Button("Save board", new FontIcon("fas-save"));
    saveButton.getStyleClass().add("save-button");
    saveButton.setOnAction(e -> handleSaveBoardClicked());

    Button backButton = new Button("Back to menu", new FontIcon("fas-home"));
    backButton.getStyleClass().add("back-button");
    backButton.setOnAction(e -> notifyObservers("back_to_menu"));

    buttonBox.getChildren().addAll(saveButton, backButton);

    Text title = new Text("Components");
    title.getStyleClass().add("panel-title");

    ScrollPane componentList = new ScrollPane(componentListContent);
    componentList.setHbarPolicy(ScrollBarPolicy.NEVER);
    componentList.setVbarPolicy(ScrollBarPolicy.NEVER);
    componentList.setFitToWidth(true);
    componentList.getStyleClass().add("component-list-scroll-pane");
    componentListContent.getStyleClass().add("component-list-content");

    panel.getChildren().addAll(buttonBox, title, componentList);
    panel.getStyleClass().add("board-creator-panel");
    return panel;
  }

  /**
   * Adds a visual representation of a placed component to the component list panel
   * on the right side of the view. Each entry includes the component's display name,
   * its image, its origin and destination tile IDs, and a delete button.
   *
   * @param displayName       The user-friendly name of the component type.
   * @param componentImage    The {@link Image} of the component.
   * @param onDelete          A {@link Runnable} action to be executed when the delete button
   *                          for this component is clicked.
   * @param originTileId      The ID of the tile where the component originates.
   * @param destinationTileId The ID of the tile where the component leads or ends.
   */
  public void addToComponentList(String displayName, Image componentImage, Runnable onDelete,
      int originTileId, int destinationTileId) {
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
    VBox fromVbox = new VBox(10);
    fromVbox.getChildren().addAll(fromLabel, fromField);

    Label toLabel = new Label("To");
    TextField toField = new TextField(String.valueOf(destinationTileId));
    toField.getStyleClass().add("from-to-field");
    toField.setEditable(false);
    VBox toVbox = new VBox(10);
    toVbox.getChildren().addAll(toLabel, toField);

    fromToBox.getChildren().addAll(fromVbox, toVbox);

    HBox contentBox = new HBox(20);
    contentBox.getChildren().addAll(componentImageView, fromToBox);

    componentBox.getChildren().addAll(header, contentBox);
    componentListContent.getChildren().add(componentBox);
  }
} 