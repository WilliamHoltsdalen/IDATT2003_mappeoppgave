package edu.ntnu.idi.idatt.view.ludo;

import org.kordamp.ikonli.javafx.FontIcon;

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

public class LudoBoardCreatorView extends BoardCreatorView {
  private final Spinner<Integer> boardSizeSpinner;
  private ChangeListener<Integer> boardSizeListener;
  
  public LudoBoardCreatorView() {
    super();
    
    this.boardSizeSpinner = new Spinner<>();
  }

  @Override
  protected BoardStackPane createBoardStackPane() {
    LudoGameBoardStackPane boardContainer = new LudoGameBoardStackPane();
    boardContainer.getStyleClass().add("board-creator-board-container");
    return boardContainer;
  }
  
  public Spinner<Integer> getBoardSizeSpinner() {
    return boardSizeSpinner;
  }

  public void setBoardSizeSpinner(int size) {
    boardSizeSpinner.valueProperty().removeListener(boardSizeListener);
    boardSizeSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(9, 21, size, 2));
    boardSizeSpinner.valueProperty().addListener(boardSizeListener);
  }
  
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
    
    VBox boardSizeBox = new VBox(5);
    boardSizeBox.getStyleClass().add("board-config-spinner");
    Label boardSizeLabel = new Label("Board Size");
    boardSizeSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(9, 21, 15, 2));
    boardSizeListener = (obs, oldVal, newVal) -> notifyObservers("update_grid");
    boardSizeSpinner.valueProperty().addListener(boardSizeListener);
    boardSizeBox.getChildren().addAll(boardSizeLabel, boardSizeSpinner);
    
    boardOptionsBox.getChildren().addAll(boardSizeBox);
    
    panel.getChildren().setAll(nameAndDescriptionBox, boardOptionsBox);
    panel.getStyleClass().add("board-creator-panel");
    
    panel.setMaxWidth(super.getBoardStackPane().getBackgroundImageView().getFitWidth() + 40); // 40px for padding
    return panel;
  }

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
