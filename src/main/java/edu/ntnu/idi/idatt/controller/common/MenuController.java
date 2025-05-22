package edu.ntnu.idi.idatt.controller.common;

import edu.ntnu.idi.idatt.factory.board.BoardFactory;
import edu.ntnu.idi.idatt.factory.board.LadderBoardFactory;
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

public abstract class MenuController implements ButtonClickObserver {

  protected final Logger logger = LoggerFactory.getLogger(MenuController.class);

  protected static final int DEFAULT_BOARD_INDEX = 1;
  protected final MenuView menuView;
  protected BoardFactory boardFactory;
  protected final Map<Integer, Board> boardVariants;
  protected int currentBoardIndex;

  protected BiConsumer<Board, List<Player>> onStartGame;
  protected Runnable onCreateBoard;
  protected Runnable onBackToGameSelection;

  protected MenuController(MenuView menuView) {
    logger.debug("MenuController initialized with default board index");
    this.menuView = menuView;
    this.boardVariants = new HashMap<>();
    this.currentBoardIndex = DEFAULT_BOARD_INDEX;
  }

  protected abstract void loadBoardsFromFactory();

  /**
   * Initializes the main menu view.
   */
  protected abstract void initializeMenuView();


  protected abstract List<Player> getPlayers();

  /**
   * Sets the board factory.
   *
   * @param boardFactory the board factory to set
   */
  protected void setBoardFactory(BoardFactory boardFactory) {
    logger.debug("board factory set");
    this.boardFactory = boardFactory;
  }

  /**
   * Sets the action to be performed when the start game button is clicked.
   *
   * @param onStartGame the action to be performed when the start game button is clicked
   */
  public void setOnStartGame(BiConsumer<Board, List<Player>> onStartGame) {
    this.onStartGame = onStartGame;
  }

  /**
   * Sets the action to be performed when the back to game selection button is clicked.
   *
   * @param onBackToGameSelection the action to be performed when the back to game selection button
   *                              is clicked
   */
  public void setOnBackToGameSelection(Runnable onBackToGameSelection) {
    this.onBackToGameSelection = onBackToGameSelection;
  }

  /**
   * Sets the action to be performed when the create board button is clicked.
   *
   * @param onCreateBoard the action to be performed when the create board button is clicked
   */
  public void setOnCreateBoard(Runnable onCreateBoard) {
    this.onCreateBoard = onCreateBoard;
  }

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
   * Handles the action of the 'start game' button in the main menu.
   */
  private void handleStartGame() {
    logger.debug("start game with board index {}", currentBoardIndex);
    Board board = boardVariants.get(currentBoardIndex);
    List<Player> players = getPlayers();

    onStartGame.accept(board, players);
  }

  public void handleNextBoard() {
    currentBoardIndex = (currentBoardIndex % boardVariants.size()) + 1;
    logger.debug("switched to next board, current board index: {}", currentBoardIndex);
    showBoardVariant(currentBoardIndex);
  }

  public void handlePreviousBoard() {
    currentBoardIndex = (currentBoardIndex - 2 + boardVariants.size()) % boardVariants.size() + 1;
    logger.debug("switched to previous board, current board index: {}", currentBoardIndex);
    showBoardVariant(currentBoardIndex);
  }

  private void handleCreateBoard() {
    onCreateBoard.run();
  }

  private void handleBackToGameSelection() {
    onBackToGameSelection.run();
  }

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

  private void showBoardVariant(int boardIndex) {
    Board board = boardVariants.get(boardIndex);
    menuView.setSelectedBoard(board);
  }

  /**
   * Loads the players from the file at the given path and adds them to the main menu.
   *
   * @param filePath The path to the file containing the players.
   * @see PlayerFactory#createPlayersFromFile(String)
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
   * Loads the board from the given file path, and adds it to the {@link #boardVariants} map.
   *
   * @param filePath The path to the file containing the board.
   * @see LadderBoardFactory#createBoardFromFile(String)
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