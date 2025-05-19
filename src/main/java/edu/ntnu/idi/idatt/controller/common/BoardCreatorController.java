package edu.ntnu.idi.idatt.controller.common;

import edu.ntnu.idi.idatt.factory.board.BoardFactory;
import edu.ntnu.idi.idatt.model.board.Board;
import edu.ntnu.idi.idatt.observer.ButtonClickObserver;
import edu.ntnu.idi.idatt.view.common.BoardCreatorView;
import edu.ntnu.idi.idatt.view.common.BoardStackPane;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <h3>BoardCreatorController.</h3>
 *
 * <p>Abstract controller responsible for handling the logic of board creation views.
 * It manages interactions between the {@link BoardCreatorView} and the underlying
 * board model, using a {@link BoardFactory} to create {@link Board} instances.</p>
 *
 * <p>This controller also observes button clicks from the view and delegates
 * actions to specific handler methods.</p>
 *
 * @see BoardCreatorView
 * @see BoardFactory
 * @see Board
 */
public abstract class BoardCreatorController implements ButtonClickObserver {

  protected static final Logger logger = LoggerFactory.getLogger(BoardCreatorController.class);

  protected final BoardCreatorView view;
  protected final BoardStackPane boardPane;
  /**
   * Runnable action to execute when the "back to menu" event is triggered.
   */
  protected Runnable onBackToMenu;

  protected BoardFactory boardFactory;
  protected Board board;

  /**
   * Constructs a BoardCreatorController with the specified view.
   *
   * @param view The {@link BoardCreatorView} associated with this controller.
   */
  public BoardCreatorController(BoardCreatorView view) {
    this.view = view;
    initializeBoard();
    this.boardPane = view.getBoardStackPane();
  }

  /**
   * Initializes the game board. This method is intended to be implemented
   * by subclasses to set up the initial state of the board.
   */
  protected abstract void initializeBoard();

  /**
   * Initializes the board creator view. Subclasses should implement this
   * method to configure the view components, such as setting up UI elements
   * and attaching event listeners.
   */
  protected abstract void initializeBoardCreatorView();

  /**
   * Handles the action to update the grid or board display.
   * Subclasses must implement this to define the specific behavior for
   * refreshing or reconfiguring the board view.
   */
  protected abstract void handleUpdateGrid();

  /**
   * Handles the action to import a board using the provided parameters.
   *
   * @param params A map of parameters required for importing the board,
   *               e.g., file path.
   */
  protected abstract void handleImportBoard(Map<String, Object> params);

  /**
   * Handles the action to save the current board configuration using the provided parameters.
   *
   * @param params A map of parameters for saving the board, e.g., file path.
   */
  protected abstract void handleSaveBoard(Map<String, Object> params);


  /**
   * Handles button click events without parameters.
   * Delegates to specific handlers based on the button ID.
   *
   * @param buttonId The ID of the button that was clicked.
   */
  @Override
  public void onButtonClicked(String buttonId) {
    logger.debug("Button clicked: {}", buttonId);
    switch (buttonId) {
      case "back_to_menu" -> handleBackToMenu();
      case "update_grid" -> handleUpdateGrid();
      default -> logger.warn("Unknown button clicked: {}", buttonId);
    }
  }

  /**
   * Handles button click events with parameters.
   * Delegates to specific handlers based on the button ID.
   *
   * @param buttonId The ID of the button that was clicked.
   * @param params A map of parameters associated with the button click.
   */
  @Override
  public void onButtonClickedWithParams(String buttonId, Map<String, Object> params) {
    logger.debug("Button clicked with params: {}, {}", buttonId, params);
    switch (buttonId) {
      case "import_board" -> handleImportBoard(params);
      case "save_board" -> handleSaveBoard(params);
      default -> logger.warn("Unknown button clicked: {}", buttonId);
    }
  }

  /**
   * Handles the "back to menu" action. If an {@code onBackToMenu} runnable
   * is set, it will be executed.
   */
  protected void handleBackToMenu() {
    logger.debug("Handling back to menu");
    if (onBackToMenu != null) {
      onBackToMenu.run();
    }
  }

  /**
   * Sets the action to be performed when the "back to menu" event is triggered.
   *
   * @param onBackToMenu The {@link Runnable} to execute.
   */
  public void setOnBackToMenu(Runnable onBackToMenu) {
    this.onBackToMenu = onBackToMenu;
  }
} 