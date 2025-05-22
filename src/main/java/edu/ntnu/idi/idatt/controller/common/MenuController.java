package edu.ntnu.idi.idatt.controller.common;

import edu.ntnu.idi.idatt.factory.board.BoardFactory;
import edu.ntnu.idi.idatt.factory.player.PlayerFactory;
import edu.ntnu.idi.idatt.filehandler.FileHandler;
import edu.ntnu.idi.idatt.filehandler.PlayerFileHandlerCsv;
import edu.ntnu.idi.idatt.model.board.Board;
import edu.ntnu.idi.idatt.model.player.Player;
import edu.ntnu.idi.idatt.observer.ButtonClickObserver;
import edu.ntnu.idi.idatt.view.common.MenuView;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * MenuController.
 *
 * <p>Abstract controller for handling the logic of menu views.</p>
 *
 * <p>It manages interactions between a {@link MenuView} and the underlying game setup components,
 * such as {@link BoardFactory} and {@link PlayerFactory}. It handles board selection, player
 * configuration, and navigation to other parts of the application (e.g., starting a game, creating
 * a board).</p>
 *
 * @see MenuView
 * @see BoardFactory
 * @see PlayerFactory
 * @see ButtonClickObserver
 */
public abstract class MenuController implements ButtonClickObserver {

  protected final Logger logger = LoggerFactory.getLogger(MenuController.class);

  protected static final int DEFAULT_BOARD_INDEX = 1;
  protected final MenuView menuView;
  protected BoardFactory boardFactory;
  protected final Map<Integer, Board> boardVariants;
  protected int currentBoardIndex;

  /**
   * Consumer for handling the start game action, taking a Board and a List of Players.
   */
  protected BiConsumer<Board, List<Player>> onStartGame;
  /**
   * Runnable action for navigating to the board creation screen.
   */
  protected Runnable onCreateBoard;
  /**
   * Runnable action for navigating back to the game selection screen.
   */
  protected Runnable onBackToGameSelection;

  /**
   * Constructs a MenuController with the specified menu view.
   *
   * @param menuView The {@link MenuView} associated with this controller.
   */
  protected MenuController(MenuView menuView) {
    logger.debug("MenuController initialized with default board index");
    this.menuView = menuView;
    this.boardVariants = new HashMap<>();
    this.currentBoardIndex = DEFAULT_BOARD_INDEX;
  }

  /**
   * Loads available board variants using the configured {@link #boardFactory}. Subclasses must
   * implement this to populate the {@link #boardVariants} map.
   */
  protected abstract void loadBoardsFromFactory();

  /**
   * Initializes the menu view. Subclasses should implement this to set up UI elements, display
   * initial data (like boards), and configure event handlers within the {@link #menuView}.
   */
  protected abstract void initializeMenuView();

  /**
   * Retrieves the list of players configured in the menu. Subclasses must implement this to get
   * player data, typically from the {@link #menuView}.
   *
   * @return A list of {@link Player}s.
   */
  protected abstract List<Player> getPlayers();

  /**
   * Sets the {@link BoardFactory} to be used for creating and loading boards.
   *
   * @param boardFactory The board factory instance.
   */
  protected void setBoardFactory(BoardFactory boardFactory) {
    logger.debug("board factory set");
    this.boardFactory = boardFactory;
  }

  /**
   * Sets the action to be performed when the game is started.
   *
   * @param onStartGame A {@link BiConsumer} that accepts the selected {@link Board} and list of
   *                    {@link Player}s.
   */
  public void setOnStartGame(BiConsumer<Board, List<Player>> onStartGame) {
    this.onStartGame = onStartGame;
  }

  /**
   * Sets the action to be performed when navigating back to the game selection screen.
   *
   * @param onBackToGameSelection A {@link Runnable} action.
   */
  public void setOnBackToGameSelection(Runnable onBackToGameSelection) {
    this.onBackToGameSelection = onBackToGameSelection;
  }

  /**
   * Sets the action to be performed when navigating to the board creation screen.
   *
   * @param onCreateBoard A {@link Runnable} action.
   */
  public void setOnCreateBoard(Runnable onCreateBoard) {
    this.onCreateBoard = onCreateBoard;
  }

  /**
   * Handles button click events without parameters from the {@link MenuView}. Delegates to specific
   * handlers based on the button ID.
   *
   * @param buttonId The ID of the button that was clicked.
   */
  @Override
  public void onButtonClicked(String buttonId) {
    logger.debug("button clicked: {}", buttonId);
    switch (buttonId) {
      case "next_board" -> handleNextBoard();
      case "previous_board" -> handlePreviousBoard();
      case "start_game" -> handleStartGame();
      case "create_board" -> handleCreateBoard();
      case "back_to_game_selection" -> handleBackToGameSelection();
      default -> {
        break;
      }
    }
  }

  /**
   * Handles button click events with parameters from the {@link MenuView}. Delegates to specific
   * handlers based on the button ID.
   *
   * @param buttonId The ID of the button that was clicked.
   * @param params   A map of parameters associated with the button click.
   */
  @Override
  public void onButtonClickedWithParams(String buttonId, Map<String, Object> params) {
    logger.debug("button clicked with params: {}", buttonId);
    switch (buttonId) {
      case "import_players" -> handleImportPlayers(params);
      case "save_players" -> handleSavePlayers(params);
      case "import_board" -> handleImportBoard(params);
      default -> {
        break;
      }
    }
  }

  /**
   * Handles the action to start the game. Retrieves the currently selected board and configured
   * players, then triggers the {@link #onStartGame} action.
   */
  private void handleStartGame() {
    logger.debug("start game with board index {}", currentBoardIndex);
    Board board = boardVariants.get(currentBoardIndex);
    List<Player> players = getPlayers();

    onStartGame.accept(board, players);
  }

  /**
   * Handles the action to display the next available board variant. Updates
   * {@link #currentBoardIndex} and calls {@link #showBoardVariant(int)}.
   */
  public void handleNextBoard() {
    currentBoardIndex = (currentBoardIndex % boardVariants.size()) + 1;
    logger.debug("switched to next board, current board index: {}", currentBoardIndex);
    showBoardVariant(currentBoardIndex);
  }

  /**
   * Handles the action to display the previous available board variant. Updates
   * {@link #currentBoardIndex} and calls {@link #showBoardVariant(int)}.
   */
  public void handlePreviousBoard() {
    currentBoardIndex = (currentBoardIndex - 2 + boardVariants.size()) % boardVariants.size() + 1;
    logger.debug("switched to previous board, current board index: {}", currentBoardIndex);
    showBoardVariant(currentBoardIndex);
  }

  /**
   * Handles the action to navigate to the board creation screen by running {@link #onCreateBoard}.
   */
  private void handleCreateBoard() {
    onCreateBoard.run();
  }

  /**
   * Handles the action to navigate back to the game selection screen by running
   * {@link #onBackToGameSelection}.
   */
  private void handleBackToGameSelection() {
    onBackToGameSelection.run();
  }

  /**
   * Handles the import of players from a file. Retrieves the file from parameters and calls
   * {@link #loadPlayersFromFile(String)}.
   *
   * @param params A map containing the "file" ({@link File}) to import from.
   */
  private void handleImportPlayers(Map<String, Object> params) {
    File file = (File) params.get("file");
    logger.debug("attempting import players from file: {}", file.getAbsolutePath());
    try {
      loadPlayersFromFile(file.getAbsolutePath());
    } catch (NullPointerException e) {
      logger.error("Failed to import players - invalid file path");
      menuView.showErrorAlert("Failed to import players", "Invalid file path");
    }
  }

  /**
   * Handles saving the current list of players to a file. Retrieves the file from parameters and
   * calls {@link #savePlayersToFile(String)}.
   *
   * @param params A map containing the "file" ({@link File}) to save to.
   */
  private void handleSavePlayers(Map<String, Object> params) {
    File file = (File) params.get("file");
    logger.debug("attempting save players from file: {}", file.getAbsolutePath());
    try {
      savePlayersToFile(file.getAbsolutePath());
    } catch (NullPointerException e) {
      logger.error("Failed to save players - invalid file path");
      menuView.showErrorAlert("Error", "Could not save players");
    }
  }

  /**
   * Handles the import of a board from a file. Retrieves the file from parameters and calls
   * {@link #loadBoardFromFile(String)}.
   *
   * @param params A map containing the "file" ({@link File}) to import from.
   */
  private void handleImportBoard(Map<String, Object> params) {
    File file = (File) params.get("file");
    logger.debug("attempting import board from file: {}", file.getAbsolutePath());
    try {
      loadBoardFromFile(file.getAbsolutePath());
    } catch (NullPointerException e) {
      logger.error("Failed to import board - invalid file path");
      menuView.showErrorAlert("Failed to import board", "Invalid file path");
    }
  }

  /**
   * Displays the board variant corresponding to the given index in the {@link #menuView}.
   *
   * @param boardIndex The index of the board in {@link #boardVariants} to display.
   */
  private void showBoardVariant(int boardIndex) {
    Board board = boardVariants.get(boardIndex);
    menuView.setSelectedBoard(board);
  }

  /**
   * Loads players from a specified file path using {@link PlayerFactory} and updates the
   * {@link #menuView}. Shows appropriate alerts on success or failure.
   *
   * @param filePath The path to the file containing player data.
   */
  public void loadPlayersFromFile(String filePath) {
    logger.debug("attempting to load players from file: {}", filePath);
    try {
      menuView.setPlayers(PlayerFactory.createPlayersFromFile(filePath));
      menuView.showInfoAlert("Success", "Players loaded successfully");
      logger.debug("successfully loaded players");
    } catch (IOException e) {
      logger.error("Failed to load players - invalid file path");
      menuView.showErrorAlert("Error", "Could not load players");
    }
  }

  /**
   * Saves the current list of players (obtained via {@link #getPlayers()}) to a specified file path
   * using a {@link PlayerFileHandlerCsv}. Shows appropriate alerts on success or failure.
   *
   * @param filePath The path to the file where players should be saved.
   */
  public void savePlayersToFile(String filePath) {
    logger.debug("attempting to save players to file: {}", filePath);
    try {
      FileHandler<Player> fileHandler = new PlayerFileHandlerCsv();
      fileHandler.writeFile(filePath, getPlayers());
      menuView.showInfoAlert("Success", "Players saved successfully");
      logger.debug("successfully saved players");
    } catch (IOException e) {
      logger.error("Failed to save players. Invalid file path");
      menuView.showErrorAlert("Error", "Could not save players");
    }
  }

  /**
   * Loads a board from a specified file path using the {@link #boardFactory}. Adds the loaded board
   * to {@link #boardVariants} if a board with the same name doesn't already exist. Updates the view
   * to show the newly imported board. Shows appropriate alerts on success or failure.
   *
   * @param filePath The path to the file containing board data.
   */
  private void loadBoardFromFile(String filePath) {
    logger.debug("attempting to load board from file: {}", filePath);
    try {
      Board board = boardFactory.createBoardFromFile(filePath);
      if (boardVariants.values().stream().anyMatch(b -> b.getName().equals(board.getName()))) {
        logger.warn("Attempted to load board from file with duplicate name: {}", filePath);
        menuView.showErrorAlert("An error occured",
            "Board with name " + board.getName() + " already exists");
        return;
      }
      boardVariants.put(boardVariants.size() + 1, board);
      currentBoardIndex = boardVariants.size();
      showBoardVariant(currentBoardIndex);
      menuView.showInfoAlert("Success", "Board imported successfully");
      logger.debug("successfully loaded board");
    } catch (IllegalArgumentException e) {
      logger.error("Failed to load board - invalid file path");
      menuView.showErrorAlert("An error occurred", "Could not load board");
    }
  }

}