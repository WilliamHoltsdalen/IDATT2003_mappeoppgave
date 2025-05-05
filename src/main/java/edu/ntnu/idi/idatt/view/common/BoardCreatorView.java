package edu.ntnu.idi.idatt.view.common;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.ntnu.idi.idatt.observer.ButtonClickObserver;
import edu.ntnu.idi.idatt.observer.ButtonClickSubject;
import edu.ntnu.idi.idatt.view.laddergame.LadderGameBoardStackPane;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;

/**
 * <h3>BoardCreatorView</h3>
 * 
 * <p>Abstract class for all board creator views. This class has a name field, a description field, and a board stack pane.
 * It also provides methods for showing alerts and handling import and save board operations. 
 * 
 * <p>This class extends BorderPane and implements ButtonClickSubject.
 * 
 * @see BorderPane
 * @see ButtonClickSubject
 */
public abstract class BoardCreatorView extends BorderPane implements ButtonClickSubject {
  protected static final Logger logger = LoggerFactory.getLogger(BoardCreatorView.class);
  protected final List<ButtonClickObserver> observers;

  private final TextField nameField;
  private final TextField descriptionField;
  private final LadderGameBoardStackPane boardStackPane;

  /**
   * Constructor for BoardCreatorView.
   */
  protected BoardCreatorView() {
    this.observers = new ArrayList<>();

    this.nameField = new TextField();
    this.descriptionField = new TextField();
    this.boardStackPane = createBoardStackPane();
  }

  /**
   * Getter for the observers.
   * 
   * @return The list of observers.
   */
  public List<ButtonClickObserver> getObservers() {
    return observers;
  }

  /**
   * Getter for the name field.
   * 
   * @return The name field.
   */
  public TextField getNameField() {
    return nameField;
  }

  /**
   * Getter for the description field.
   * 
   * @return The description field.
   */
  public TextField getDescriptionField() {
    return descriptionField;
  }

  /**
   * Getter for the board stack pane.
   * 
   * @return The board stack pane.
   */
  public LadderGameBoardStackPane getBoardStackPane() {
    return boardStackPane;
  }

  /**
   * Shows an info alert.
   * 
   * @param headerText The header text of the alert.
   * @param message The message of the alert.
   */
  public void showInfoAlert(String headerText, String message) {
    Alert alert = new Alert(AlertType.INFORMATION);
    alert.setTitle("Info");
    alert.setHeaderText(headerText);
    alert.setContentText(message);
    alert.showAndWait();
  }

  /**
   * Shows an error alert.
   * 
   * @param headerText The header text of the alert.
   * @param message The message of the alert.
   */
  public void showErrorAlert(String headerText, String message) {
    Alert alert = new Alert(AlertType.ERROR);
    alert.setTitle("Error");
    alert.setHeaderText(headerText);
    alert.setContentText(message);
    alert.showAndWait();
  }

  /**
   * Creates a board stack pane.
   * 
   * @return The board stack pane.
   */
  protected LadderGameBoardStackPane createBoardStackPane() {
    LadderGameBoardStackPane boardContainer = new LadderGameBoardStackPane();
    boardContainer.getStyleClass().add("board-creator-board-container");
    return boardContainer;
  }

  /**
   * Handles the import board operation.
   */
  protected void handleImportBoard() {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Import Board");
    fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON files", "*.json"));
    File file = fileChooser.showOpenDialog(this.getScene().getWindow());
    if (file != null) {
      notifyObserversWithParams("import_board", Map.of("path", file.getAbsolutePath()));
    }
  }

  /**
   * Handles the save board operation.
   */
  protected void handleSaveBoardClicked() {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Save Board");
    fileChooser.setInitialFileName(nameField.getText() + ".json");

    File file = fileChooser.showSaveDialog(this.getScene().getWindow());
    if (file == null) {
      showErrorAlert("Could not save Board", "Invalid file path");
      return;
    }
    Map<String, Object> params = new HashMap<>();
    params.put("path", file.getAbsolutePath());
    notifyObserversWithParams("save_board", params);
  }

  /**
   * Adds an observer to the board creator view.
   * 
   * @param observer The observer to add.
   */
  @Override
  public void addObserver(ButtonClickObserver observer) {
    logger.debug("Observer added to board creator view");
    observers.add(observer);
  }

  /**
   * Removes an observer from the board creator view.
   * 
   * @param observer The observer to remove.
   */
  @Override
  public void removeObserver(ButtonClickObserver observer) {
    logger.debug("Observer removed from board creator view");
    observers.remove(observer);
  }

  /**
   * Notifies all observers of a button click.
   * 
   * @param buttonId The id of the button that was clicked.
   */
  @Override
  public void notifyObservers(String buttonId) {
    logger.debug("Notifying observers of button click: {}", buttonId);
    observers.forEach(observer -> observer.onButtonClicked(buttonId));
  }

  /**
   * Notifies all observers of a button click with parameters.
   * 
   * @param buttonId The id of the button that was clicked.
   * @param params The parameters of the button click.
   */
  @Override
  public void notifyObserversWithParams(String buttonId, Map<String, Object> params) {
    logger.debug("Notifying observers of button click with params: {} {}", buttonId, params);
    observers.forEach(observer -> observer.onButtonClickedWithParams(buttonId, params));
  }
}
