package edu.ntnu.idi.idatt.view.ludo;

import edu.ntnu.idi.idatt.model.board.LudoGameBoard;
import edu.ntnu.idi.idatt.view.common.BoardCreatorView;
import edu.ntnu.idi.idatt.view.common.BoardStackPane;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.kordamp.ikonli.javafx.FontIcon;

/**
 * <h3>LudoBoardCreatorView.</h3>
 *
 * <p>This class extends {@link BoardCreatorView} to provide a specialized UI for creating
 * and configuring {@link LudoGameBoard}s. It allows users to set the board's name,
 * description, and size (which determines the dimensions of the Ludo board.
 *
 * <p>The view includes:
 * <ul>
 *   <li>Input fields for board name and description.</li>
 *   <li>A {@link Spinner} to adjust the Ludo board size (e.g., 9, 11, 13, ..., 21).</li>
 *   <li>Buttons to import an existing board, save the current board, or return to the menu.</li>
 *   <li>A central display area ({@link LudoGameBoardStackPane}) to show the Ludo board.</li>
 *   <li>An informational panel, as Ludo tile actions are noted as under development.</li>
 * </ul>
 * </p>
 *
 * @see BoardCreatorView
 * @see LudoGameBoard
 * @see LudoBoardCreatorController
 * @see LudoGameBoardStackPane
 * @see Spinner
 */
public class LudoBoardCreatorView extends BoardCreatorView {

  private final Spinner<Integer> boardSizeSpinner;
  private ChangeListener<Integer> boardSizeListener;

  /**
   * Constructs a new {@code LudoBoardCreatorView}.
   * Initializes the board size spinner.
   */
  public LudoBoardCreatorView() {
    super();

    this.boardSizeSpinner = new Spinner<>();
  }

  /**
   * Creates and returns a {@link LudoGameBoardStackPane} instance to be used as the
   * central area for displaying the Ludo board being created. This method overrides the
   * parent class's method to provide a Ludo-specific board container.
   *
   * @return A new {@link LudoGameBoardStackPane} for the board display.
   */
  @Override
  protected BoardStackPane createBoardStackPane() {
    LudoGameBoardStackPane boardContainer = new LudoGameBoardStackPane();
    boardContainer.getStyleClass().add("board-creator-board-container");
    return boardContainer;
  }

  /**
   * Returns the {@link Spinner} used for adjusting the size of the Ludo board.
   * The board size typically refers to the number of tiles along one side of the board.
   *
   * @return The {@link Spinner} for board size selection.
   */
  public Spinner<Integer> getBoardSizeSpinner() {
    return boardSizeSpinner;
  }

  /**
   * Sets the value of the board size spinner and re-attaches its change listener.
   * This is typically used when loading an existing Ludo board configuration to update
   * the spinner to the loaded board's size. The spinner is configured for typical Ludo
   * board sizes (e.g., odd numbers from 9 to 21).
   *
   * @param size The board size to set
   */
  public void setBoardSizeSpinner(int size) {
    boardSizeSpinner.valueProperty().removeListener(boardSizeListener);
    boardSizeSpinner.setValueFactory(
        new SpinnerValueFactory.IntegerSpinnerValueFactory(9, 21, size, 2));
    boardSizeSpinner.valueProperty().addListener(boardSizeListener);
  }

  /**
   * Initializes the main view components of the Ludo board creator.
   * This method sets up the board configuration panel (for name, description, size),
   * the central board display area, an informational left panel, and a right menu panel
   * with save/back buttons. It configures the overall layout and styling of the view.
   *
   * @param board The {@link LudoGameBoard} instance to be displayed and configured.
   */
  public void initializeView(LudoGameBoard board) {
    logger.debug("Initializing LudoBoardCreatorView");

    super.getBoardStackPane().initialize(board, board.getBackground());
    super.getBoardStackPane().getBackgroundImageView().setFitWidth(455);

    VBox centerPanel = new VBox(createBoardConfigurationPanel(), super.getBoardStackPane());
    centerPanel.setAlignment(Pos.CENTER);
    centerPanel.setSpacing(20);

    VBox leftPanel = createLeftInfoPanel();
    VBox rightPanel = createMenuPanel();
    rightPanel.setMaxHeight(Region.USE_PREF_SIZE);

    this.setLeft(leftPanel);
    this.setCenter(centerPanel);
    this.setRight(rightPanel);
    this.getStyleClass().add("board-creator-view");
    logger.debug("LudoBoardCreatorView initialized successfully");
  }

  /**
   * Creates the central panel located above the board display, used for configuring
   * general Ludo board properties. This panel includes:
   * <ul>
   *   <li>Input fields for the board's name and description.</li>
   *   <li>An "Import board" button.</li>
   *   <li>A {@link Spinner} for adjusting the Ludo board size.</li>
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

    VBox boardSizeBox = new VBox(5);
    boardSizeBox.getStyleClass().add("board-config-spinner");
    final Label boardSizeLabel = new Label("Board Size");
    boardSizeSpinner.setValueFactory(
        new SpinnerValueFactory.IntegerSpinnerValueFactory(9, 21, 15, 2));
    boardSizeListener = (obs, oldVal, newVal) -> notifyObservers("update_grid");
    boardSizeSpinner.valueProperty().addListener(boardSizeListener);
    boardSizeBox.getChildren().addAll(boardSizeLabel, boardSizeSpinner);

    boardOptionsBox.getChildren().addAll(boardSizeBox);

    panel.getChildren().setAll(nameAndDescriptionBox, boardOptionsBox);
    panel.getStyleClass().add("board-creator-panel");

    panel.setMaxWidth(
        super.getBoardStackPane().getBackgroundImageView().getFitWidth() + 40); // 40px for padding
    return panel;
  }

  /**
   * Creates the left informational panel of the view. This panel currently displays a message
   * indicating that tile actions for Ludo are still under development, along with a tools icon.
   *
   * @return A {@link VBox} containing the informational content.
   */
  private VBox createLeftInfoPanel() {
    VBox panel = new VBox(15);
    panel.setPadding(new Insets(20));
    panel.setMinWidth(300);
    panel.setAlignment(Pos.TOP_CENTER);
    panel.getStyleClass().add("board-creator-panel");

    FontIcon icon = new FontIcon("fas-tools");
    icon.setIconSize(48);
    icon.setFill(Color.GRAY);

    Label infoLabel = new Label("Tile actions for Ludo are still under development.");
    infoLabel.setWrapText(true);
    infoLabel.setStyle("-fx-font-size: 14px; -fx-text-alignment: center;");
    panel.getChildren().addAll(icon, infoLabel);
    return panel;
  }

  /**
   * Creates the right menu panel of the view. This panel contains buttons for saving the
   * currently configured Ludo board and for returning to the main menu.
   *
   * @return A {@link VBox} containing the save and back buttons.
   */
  private VBox createMenuPanel() {
    VBox panel = new VBox(20);
    panel.setPadding(new Insets(10));
    panel.setAlignment(Pos.TOP_CENTER);
    panel.setMaxWidth(300);

    HBox buttonBox = new HBox(10);
    buttonBox.setAlignment(Pos.CENTER);

    Button saveButton = new Button("Save board", new FontIcon("fas-save"));
    saveButton.getStyleClass().add("save-button");
    saveButton.setOnAction(e -> handleSaveBoardClicked());

    Button backButton = new Button("Back to menu", new FontIcon("fas-home"));
    backButton.getStyleClass().add("back-button");
    backButton.setOnAction(e -> notifyObservers("back_to_menu"));

    buttonBox.getChildren().addAll(saveButton, backButton);

    panel.getChildren().add(buttonBox);
    panel.getStyleClass().add("board-creator-panel");
    return panel;
  }
}
