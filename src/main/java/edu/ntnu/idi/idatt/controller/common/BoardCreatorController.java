package edu.ntnu.idi.idatt.controller.common;

import edu.ntnu.idi.idatt.factory.board.BoardFactory;
import edu.ntnu.idi.idatt.model.board.Board;
import edu.ntnu.idi.idatt.observer.ButtonClickObserver;
import edu.ntnu.idi.idatt.view.common.BoardCreatorView;
import edu.ntnu.idi.idatt.view.common.BoardStackPane;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BoardCreatorController implements ButtonClickObserver {

  protected static final Logger logger = LoggerFactory.getLogger(BoardCreatorController.class);

  protected final BoardCreatorView view;
  protected final BoardStackPane boardPane;
  protected Runnable onBackToMenu;

  protected BoardFactory boardFactory;
  protected Board board;

  public BoardCreatorController(BoardCreatorView view) {
    this.view = view;
    initializeBoard();
    this.boardPane = view.getBoardStackPane();
  }

  protected abstract void initializeBoard();

  protected abstract void initializeBoardCreatorView();

  protected abstract void handleUpdateGrid();

  protected abstract void handleImportBoard(Map<String, Object> params);

  protected abstract void handleSaveBoard(Map<String, Object> params);


  @Override
  public void onButtonClicked(String buttonId) {
    logger.debug("Button clicked: {}", buttonId);
    switch (buttonId) {
      case "back_to_menu" -> handleBackToMenu();
      case "update_grid" -> handleUpdateGrid();
      default -> logger.warn("Unknown button clicked: {}", buttonId);
    }
  }

  @Override
  public void onButtonClickedWithParams(String buttonId, Map<String, Object> params) {
    logger.debug("Button clicked with params: {}, {}", buttonId, params);
    switch (buttonId) {
      case "import_board" -> handleImportBoard(params);
      case "save_board" -> handleSaveBoard(params);
      default -> logger.warn("Unknown button clicked: {}", buttonId);
    }
  }

  protected void handleBackToMenu() {
    logger.debug("Handling back to menu");
    if (onBackToMenu != null) {
      onBackToMenu.run();
    }
  }

  public void setOnBackToMenu(Runnable onBackToMenu) {
    this.onBackToMenu = onBackToMenu;
  }
} 