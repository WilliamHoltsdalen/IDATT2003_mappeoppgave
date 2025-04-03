package edu.ntnu.idi.idatt.controller;

import edu.ntnu.idi.idatt.factory.BoardFactory;
import edu.ntnu.idi.idatt.factory.PlayerFactory;
import edu.ntnu.idi.idatt.model.Board;
import edu.ntnu.idi.idatt.model.Player;
import edu.ntnu.idi.idatt.view.gui.container.MainMenuView;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public class MainMenuController {
  private static final int DEFAULT_BOARD_INDEX = 1;
  private final Map<Integer, Board> boardVariants;
  private int currentBoardIndex;

  private BiConsumer<Board, List<Player>> onStartGame;

  private final MainMenuView mainMenuView;

  /**
   * Constructor for MenuController class.
   */
  public MainMenuController(MainMenuView mainMenuView) {
    this.boardVariants = new HashMap<>();
    this.currentBoardIndex = DEFAULT_BOARD_INDEX;
    this.mainMenuView = mainMenuView;

    initialize();
  }

  /**
   * Initializes the main menu controller.
   */
  private void initialize() {
    mainMenuView.setOnStartGame(this::handleStartGame);
    mainMenuView.setOnImportPlayers(this::loadPlayersFromFile);
    mainMenuView.setOnImportBoard(this::loadBoardFromFile);
    mainMenuView.setOnNextBoard(this::handleNextBoard);
    mainMenuView.setOnPrevBoard(this::handlePreviousBoard);

    loadBoardsFromFactory();
    showBoardVariant(currentBoardIndex);
  }

  public void setOnStartGame(BiConsumer<Board, List<Player>> onStartGame) {
    this.onStartGame = onStartGame;
  }

  /**
   * Handles the action of the 'start game' button in the main menu.
   */
  private void handleStartGame() {
    List<Player> players = new ArrayList<>();
    mainMenuView.getPlayerRows().forEach(playerRow ->
        players.add(new Player(playerRow.getName(), playerRow.getColor().toString())));

    onStartGame.accept(boardVariants.get(currentBoardIndex), players);
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
   * @see BoardFactory#createBoard(String)
   */
  private void loadBoardsFromFactory() {
    this.boardVariants.put(1, BoardFactory.createBoard("Classic"));
    this.boardVariants.put(2, BoardFactory.createBoard("Teleporting"));
  }

  /**
   * Loads the board from the given file path, and adds it to the {@link #boardVariants} map.
   *
   * @see BoardFactory#createBoardFromFile(String)
   * @param filePath The path to the file containing the board.
   */
  private void loadBoardFromFile(String filePath) {
    try {
      Board board = BoardFactory.createBoardFromFile(filePath);
      boardVariants.put(boardVariants.size() + 1, board);
    } catch (IOException | IllegalArgumentException e) {
      e.printStackTrace();
    }
  }

  public void handleNextBoard() {
    currentBoardIndex = (currentBoardIndex % boardVariants.size()) + 1;
    System.out.println(currentBoardIndex);
    showBoardVariant(currentBoardIndex);
  }

  public void handlePreviousBoard() {
    currentBoardIndex = (currentBoardIndex - 2 + boardVariants.size()) % boardVariants.size() + 1;
    System.out.println(currentBoardIndex);
    showBoardVariant(currentBoardIndex);
  }

  private void showBoardVariant(int boardIndex) {
    Board board = boardVariants.get(boardIndex);
    mainMenuView.setSelectedBoard(board);
  }
}
