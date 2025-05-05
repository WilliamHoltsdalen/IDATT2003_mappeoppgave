package edu.ntnu.idi.idatt.controller.common;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import edu.ntnu.idi.idatt.factory.board.BoardFactory;
import edu.ntnu.idi.idatt.factory.board.LadderBoardFactory;
import edu.ntnu.idi.idatt.factory.player.PlayerFactory;
import edu.ntnu.idi.idatt.filehandler.FileHandler;
import edu.ntnu.idi.idatt.filehandler.PlayerFileHandlerCsv;
import edu.ntnu.idi.idatt.model.board.Board;
import edu.ntnu.idi.idatt.model.player.Player;
import edu.ntnu.idi.idatt.observer.ButtonClickObserver;
import edu.ntnu.idi.idatt.view.common.MenuView;

public abstract class MenuController implements ButtonClickObserver {
    protected static final int DEFAULT_BOARD_INDEX = 1;
    protected final MenuView menuView;
    protected BoardFactory boardFactory;
    protected final Map<Integer, Board> boardVariants;
    protected int currentBoardIndex;
  
    protected BiConsumer<Board, List<Player>> onStartGame;
    protected Runnable onCreateBoard;
    protected Runnable onBackToGameSelection;
    
    protected MenuController(MenuView menuView) {
        this.menuView = menuView;
        this.boardVariants = new HashMap<>();
        this.currentBoardIndex = DEFAULT_BOARD_INDEX;
    }

    protected abstract void loadBoardsFromFactory();

    /**
    * Initializes the main menu view.
    */
    protected abstract void initializeMenuView();

    /**
     * Sets the board factory.
     * @param boardFactory the board factory to set
     */
    protected void setBoardFactory(BoardFactory boardFactory) {
        this.boardFactory = boardFactory;
    }

    /**
     * Sets the action to be performed when the start game button is clicked.
     * @param onStartGame the action to be performed when the start game button is clicked
     */
    public void setOnStartGame(BiConsumer<Board, List<Player>> onStartGame) {
        this.onStartGame = onStartGame;
    }

    /**
     * Sets the action to be performed when the back to game selection button is clicked.
     * @param onBackToGameSelection the action to be performed when the back to game selection button is clicked
     */
    public void setOnBackToGameSelection(Runnable onBackToGameSelection) {
        this.onBackToGameSelection = onBackToGameSelection;
    }

    /**
     * Sets the action to be performed when the create board button is clicked.
     * @param onCreateBoard the action to be performed when the create board button is clicked
     */
    public void setOnCreateBoard(Runnable onCreateBoard) {
        this.onCreateBoard = onCreateBoard;
    }

    @Override
    public void onButtonClicked(String buttonId) {
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
      switch (buttonId) {
        case "import_players" -> handleImportPlayers(params);
        case "save_players" -> handleSavePlayers(params);
        case "import_board" -> handleImportBoard(params);
        default -> {
            break;
        }
      }
    }

  private List<Player> getPlayers() {
    List<Player> players = new ArrayList<>();
    menuView.getPlayerRows().forEach(playerRow ->
        players.add(new Player(playerRow.getName(), playerRow.getColor().toString(),
            playerRow.getPlayerTokenType())));
    return players;
  }

  /**
   * Handles the action of the 'start game' button in the main menu.
   */
  private void handleStartGame() {
    Board board = boardVariants.get(currentBoardIndex);
    List<Player> players = getPlayers();

    onStartGame.accept(board, players);
  }

  public void handleNextBoard() {
    currentBoardIndex = (currentBoardIndex % boardVariants.size()) + 1;
    showBoardVariant(currentBoardIndex);
  }

  public void handlePreviousBoard() {
    currentBoardIndex = (currentBoardIndex - 2 + boardVariants.size()) % boardVariants.size() + 1;
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
    try {
      loadPlayersFromFile(file.getAbsolutePath());
    } catch (NullPointerException e) {
        menuView.showErrorAlert("Failed to import players", "Invalid file path");
    }
  }

  private void handleSavePlayers(Map<String, Object> params) {
    File file = (File) params.get("file");
    try {
      savePlayersToFile(file.getAbsolutePath());
    } catch (NullPointerException e) {
        menuView.showErrorAlert("Error", "Could not save players");
    }
  }

  private void handleImportBoard(Map<String, Object> params) {
    File file = (File) params.get("file");
    try {
      loadBoardFromFile(file.getAbsolutePath());
    } catch (NullPointerException e) {
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
   * @see PlayerFactory#createPlayersFromFile(String)
   * @param filePath The path to the file containing the players.
   */
  public void loadPlayersFromFile(String filePath) {
    try {
        menuView.setPlayers(PlayerFactory.createPlayersFromFile(filePath));
        menuView.showInfoAlert("Success", "Players loaded successfully");
    } catch (IOException e) {
        menuView.showErrorAlert("Error", "Could not load players");
    }
  }

  public void savePlayersToFile(String filePath) {
    try {
      FileHandler<Player> fileHandler = new PlayerFileHandlerCsv();
      fileHandler.writeFile(filePath, getPlayers());
      menuView.showInfoAlert("Success", "Players saved successfully");
    } catch (IOException e) {
        menuView.showErrorAlert("Error", "Could not save players");
    }
  }

  /**
   * Loads the board from the given file path, and adds it to the {@link #boardVariants} map.
   *
   * @see LadderBoardFactory#createBoardFromFile(String)
   * @param filePath The path to the file containing the board.
   */
  private void loadBoardFromFile(String filePath) {
    try {
      Board board = boardFactory.createBoardFromFile(filePath);
      if (boardVariants.values().stream().anyMatch(b -> b.getName().equals(board.getName()))) {
        menuView.showErrorAlert("An error occured", "Board with name " + board.getName() + " already exists");
        return;
      }
      boardVariants.put(boardVariants.size() + 1, board);
      currentBoardIndex = boardVariants.size();
      showBoardVariant(currentBoardIndex);
      menuView.showInfoAlert("Success", "Board imported successfully");
    } catch (IllegalArgumentException e) {
        menuView.showErrorAlert("An error occured", "Could not load board");
    }
  }
    
}