package edu.ntnu.idi.idatt.controller;

import edu.ntnu.idi.idatt.factory.board.BoardFactory;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import edu.ntnu.idi.idatt.factory.board.LadderBoardFactory;
import edu.ntnu.idi.idatt.factory.player.PlayerFactory;
import edu.ntnu.idi.idatt.model.board.Board;
import edu.ntnu.idi.idatt.model.player.Player;
import edu.ntnu.idi.idatt.observer.ButtonClickObserver;
import edu.ntnu.idi.idatt.view.container.MainMenuView;
import javafx.stage.FileChooser;

public class MainMenuController implements ButtonClickObserver {
  private static final int DEFAULT_BOARD_INDEX = 1;
  private final BoardFactory boardFactory;
  private final Map<Integer, Board> boardVariants;
  private int currentBoardIndex;

  BiConsumer<Board, List<Player>> onStartGame;
  Runnable onCreateBoard;

  private final MainMenuView mainMenuView;

  /**
   * Constructor for MenuController class.
   */
  public MainMenuController(MainMenuView mainMenuView) {
    this.boardFactory = new LadderBoardFactory();
    this.boardVariants = new HashMap<>();
    this.currentBoardIndex = DEFAULT_BOARD_INDEX;
    this.mainMenuView = mainMenuView;

    initializeMainMenuView();
  }

  @Override
  public void onButtonClicked(String buttonId) {
    switch (buttonId) {
      case "import_players" -> handleImportPlayersButtonAction();
      case "import_board" -> handleImportBoardButtonAction();
      case "next_board" -> handleNextBoard();
      case "previous_board" -> handlePreviousBoard();
      case "start_game" -> handleStartGame();
      case "create_board" -> handleCreateBoard();
      default -> {
        break;
      }
    }
  }

  @Override
  public void onButtonClickedWithParams(String buttonId, Map<String, Object> params) {
    // Not needed
  }

  public void setOnStartGame(BiConsumer<Board, List<Player>> onStartGame) {
    this.onStartGame = onStartGame;
  }

  /**
   * Initializes the main menu view.
   */
  private void initializeMainMenuView() {
    loadBoardsFromFactory();
    mainMenuView.setSelectedBoard(boardFactory.createBoard("Classic"));
    mainMenuView.initialize();
  }

  /**
   * Handles the action of the 'start game' button in the main menu.
   */
  private void handleStartGame() {
    Board board = boardVariants.get(currentBoardIndex);
    List<Player> players = new ArrayList<>();
    mainMenuView.getPlayerRows().forEach(playerRow ->
        players.add(new Player(playerRow.getName(), playerRow.getColor().toString(),
            playerRow.getPlayerTokenType())));

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

  private void handleImportPlayersButtonAction() {
    File file = new FileChooser().showOpenDialog(null);
    if (file == null) {
      return;
    }
    loadPlayersFromFile(file.getAbsolutePath());
  }

  private void handleImportBoardButtonAction() {
    File file = new FileChooser().showOpenDialog(null);
    if (file == null) {
      return;
    }
    loadBoardFromFile(file.getAbsolutePath());
    mainMenuView.setSelectedBoard(boardVariants.get(boardVariants.size()));
  }

  private void showBoardVariant(int boardIndex) {
    Board board = boardVariants.get(boardIndex);
    mainMenuView.setSelectedBoard(board);
  }

  /**
   * Loads the players from the file at the given path and adds them to the main menu.
   *
   * @see PlayerFactory#createPlayersFromFile(String)
   * @param filePath The path to the file containing the players.
   */
  public void loadPlayersFromFile(String filePath) {
    try {
      mainMenuView.setPlayers(PlayerFactory.createPlayersFromFile(filePath));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Loads the hardcoded boards from the factory, and adds them to the {@link #boardVariants} map.
   *
   * @see LadderBoardFactory#createBoard(String)
   */
  private void loadBoardsFromFactory() {
    this.boardVariants.put(1, boardFactory.createBoard("Classic"));
    this.boardVariants.put(2, boardFactory.createBoard("Teleporting"));
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
      boardVariants.put(boardVariants.size() + 1, board);
    } catch (IllegalArgumentException e) {
      e.printStackTrace();
    }
  }

  private void handleCreateBoard() {
    onCreateBoard.run();
  }

  public void setOnCreateBoard(Runnable onCreateBoard) {
    this.onCreateBoard = onCreateBoard;
  }
}
