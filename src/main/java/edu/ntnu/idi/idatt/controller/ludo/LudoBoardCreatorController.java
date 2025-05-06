package edu.ntnu.idi.idatt.controller.ludo;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import edu.ntnu.idi.idatt.controller.common.BoardCreatorController;
import edu.ntnu.idi.idatt.factory.board.LudoBoardFactory;
import edu.ntnu.idi.idatt.filehandler.FileHandler;
import edu.ntnu.idi.idatt.filehandler.LudoBoardFileHandlerGson;
import edu.ntnu.idi.idatt.model.board.Board;
import edu.ntnu.idi.idatt.model.board.LudoGameBoard;
import edu.ntnu.idi.idatt.view.common.BoardCreatorView;
import edu.ntnu.idi.idatt.view.ludo.LudoBoardCreatorView;
import javafx.application.Platform;
public class LudoBoardCreatorController extends BoardCreatorController {

  public LudoBoardCreatorController(BoardCreatorView view) {
    super(view);
    logger.debug("Constructing LudoBoardCreatorController");

    initializeBoardCreatorView();
  }

  @Override
  protected void initializeBoard() {
    this.boardFactory = new LudoBoardFactory();
    this.board = (LudoGameBoard) boardFactory.createBlankBoard(15, 15);
  }

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

  @Override
  protected void handleUpdateGrid() {
    logger.debug("Handling update grid");
    ((LudoGameBoard) board).setBoardSize(((LudoBoardCreatorView) view).getBoardSizeSpinner().getValue());
    boardPane.setBoard(board);
    boardPane.updateGrid();
  }

  @Override
  protected void handleImportBoard(Map<String, Object> params) {
    // TODO: Implement
  }

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
