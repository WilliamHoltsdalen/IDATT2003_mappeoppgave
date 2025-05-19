package edu.ntnu.idi.idatt.controller.ludo;

import edu.ntnu.idi.idatt.controller.common.BoardCreatorController;
import edu.ntnu.idi.idatt.factory.board.LudoBoardFactory;
import edu.ntnu.idi.idatt.filehandler.FileHandler;
import edu.ntnu.idi.idatt.filehandler.LudoBoardFileHandlerGson;
import edu.ntnu.idi.idatt.model.board.Board;
import edu.ntnu.idi.idatt.model.board.LudoGameBoard;
import edu.ntnu.idi.idatt.view.common.BoardCreatorView;
import edu.ntnu.idi.idatt.view.ludo.LudoBoardCreatorView;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import javafx.application.Platform;

/**
 * <h3>LudoBoardCreatorController.</h3>
 *
 * <p>Controller responsible for the logic behind creating and editing {@link LudoGameBoard}s.
 * It extends {@link BoardCreatorController} and manages interactions with a
 * {@link LudoBoardCreatorView}.</p>
 *
 * <p>This controller handles tasks such as initializing the Ludo board with a default size,
 * updating the board size based on user input, and saving/importing Ludo board configurations.
 * Unlike the ladder game board creator, it does not currently manage dynamic component placement,
 * as Ludo boards have a more fixed structure.</p>
 *
 * @see BoardCreatorController
 * @see LudoGameBoard
 * @see LudoBoardCreatorView
 * @see LudoBoardFactory
 * @see LudoBoardFileHandlerGson
 */
public class LudoBoardCreatorController extends BoardCreatorController {

  /**
   * Constructs a {@code LudoBoardCreatorController}.
   *
   * @param view The {@link BoardCreatorView} (expected to be a {@link LudoBoardCreatorView})
   *             associated with this controller.
   */
  public LudoBoardCreatorController(BoardCreatorView view) {
    super(view);
    logger.debug("Constructing LudoBoardCreatorController");

    initializeBoardCreatorView();
  }

  /**
   * Initializes the {@link #board} instance as a new, blank {@link LudoGameBoard}
   * using a {@link LudoBoardFactory} with a default size (e.g., 15x15 or a similar standard).
   */
  @Override
  protected void initializeBoard() {
    this.boardFactory = new LudoBoardFactory();
    this.board = (LudoGameBoard) boardFactory.createBlankBoard(15, 15);
  }

  /**
   * Initializes the {@link LudoBoardCreatorView}. Sets this controller as an observer to the view
   * and initializes the view with the current Ludo board.
   */
  @Override
  protected void initializeBoardCreatorView() {
    logger.debug("Initializing board creator view");
    if (!view.getObservers().contains(this)) {
      view.addObserver(this);
    }

    LudoBoardCreatorView ludoBoardCreatorView = (LudoBoardCreatorView) view;
    ludoBoardCreatorView.initializeView((LudoGameBoard) board);
    logger.debug("Board creator view initialized successfully");
  }

  /**
   * Handles the action to update the Ludo board's size based on the value from the view's
   * board size spinner. Updates the {@link #board} model and refreshes the grid display in the
   * {@link #boardPane}.
   */
  @Override
  protected void handleUpdateGrid() {
    logger.debug("Handling update grid");
    ((LudoGameBoard) board).setBoardSize(
        ((LudoBoardCreatorView) view).getBoardSizeSpinner().getValue());
    boardPane.setBoard(board);
    boardPane.updateGrid();
  }

  /**
   * Handles the import of a Ludo board configuration from a file. It attempts to create a board
   * using {@link LudoBoardFactory}. If successful, it updates the internal {@link #board} model
   * and refreshes the view's input fields (name, description, board size) and the board display.
   * Shows an error alert if the import fails or the file is invalid.
   *
   * @param params A map containing the "path" (String) to the Ludo board file.
   */
  @Override
  protected void handleImportBoard(Map<String, Object> params) {
    logger.debug("Handling import board");
    String path = (String) params.get("path");
    try {
      Board importedBoard = boardFactory.createBoardFromFile(path);
      if (importedBoard == null) {
        Platform.runLater(() -> view.showErrorAlert("Failed to import board",
            "The board file is empty or invalid."));
        return;
      }
      board = (LudoGameBoard) importedBoard;
    } catch (Exception e) {
      Platform.runLater(() -> view.showErrorAlert("Failed to import board",
          "The file does not contain a valid Ludo board."));
      logger.warn("Failed to import board. Check if the file contains a valid Ludo board");
      return;
    }

    int boardSize = ((LudoGameBoard) board).getBoardSize();
    ((LudoBoardCreatorView) view).setBoardSizeSpinner(boardSize);
    view.getNameField().setText(board.getName());
    view.getDescriptionField().setText(board.getDescription());

    boardPane.initialize(board, board.getBackground());
    Platform.runLater(() -> {
      view.showInfoAlert("Success!",
          "The board has been imported successfully!");
      logger.info("Board imported successfully");
    });
  }

  /**
   * Handles saving the current Ludo board configuration to a file. Updates the board model
   * with values from the view's input fields (name, description) before saving.
   * Uses {@link LudoBoardFileHandlerGson} for serialization.
   * Shows an info alert on success or an error alert on failure.
   *
   * @param params A map containing the "path" (String) to save the file to.
   */
  @Override
  protected void handleSaveBoard(Map<String, Object> params) {
    logger.debug("Handling save board");
    try {
      board = (LudoGameBoard) boardPane.getBoard();
      board.setName(view.getNameField().getText());
      board.setDescription(view.getDescriptionField().getText());

      String path = (String) params.get("path");
      FileHandler<Board> fileHandler = new LudoBoardFileHandlerGson();
      fileHandler.writeFile(path, List.of(board));

      Platform.runLater(() -> view.showInfoAlert("Board saved successfully!",
          "You can now load the board from the main menu, and start playing!"));
      logger.info("Board saved successfully!");
    } catch (IOException | IllegalArgumentException e) {
      Platform.runLater(() -> view.showErrorAlert("Failed to save board", e.getMessage()));
      logger.warn("Failed to save board: {}", e.getMessage());
    }
  }
}
