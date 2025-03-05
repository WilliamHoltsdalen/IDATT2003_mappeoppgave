package edu.ntnu.idi.idatt.model;

import static edu.ntnu.idi.idatt.model.validators.ArgumentValidator.boardGameAddPlayersValidator;
import static edu.ntnu.idi.idatt.model.validators.ArgumentValidator.boardGameCreateBoardValidator;
import static edu.ntnu.idi.idatt.model.validators.ArgumentValidator.boardGameCreateDiceValidator;
import static edu.ntnu.idi.idatt.model.validators.ArgumentValidator.boardGameSetCurrentPlayerValidator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BoardGame {
  private Board board;
  private final List<Player> players;
  private Dice dice;
  private Player currentPlayer;
  private int roundNumber;

  public BoardGame(int boardRows, int boardColumns, List<Player> players, int diceCount) {
    this.players = new ArrayList<>();
    this.roundNumber = 0;

    createBoard(boardRows, boardColumns);
    addTileActions();
    addPlayers(players);
    createDice(diceCount);
  }

  public Board getBoard() {
    return board;
  }

  public List<Player> getPlayers() {
    return players;
  }

  public Dice getDice() {
    return dice;
  }

  public Player getCurrentPlayer() {
    return currentPlayer;
  }

  public int getRoundNumber() {
    return roundNumber;
  }

  public Player getWinner() {
    for (Player player : players) {
      if (player.getCurrentTile().getTileId() == this.board.getTileCount()) {
        return player;
      }
    }
    return null;
  }

  private void createBoard(int rows, int columns) {
    boardGameCreateBoardValidator(rows, columns);

    this.board = new Board(rows, columns);
  }

  private void addPlayers(List<Player> players) {
    boardGameAddPlayersValidator(players);

    this.players.addAll(players);
  }

  private void createDice(int diceCount) {
    boardGameCreateDiceValidator(diceCount);

    this.dice = new Dice(diceCount);
  }

  public void setCurrentPlayer(Player player) {
    boardGameSetCurrentPlayerValidator(player);

    this.currentPlayer = player;
  }

  public void incrementRoundNumber() {
    roundNumber++;
  }

  private void addTileActions() {
    Map<Integer, Integer> ladderMap = new HashMap<>();
    ladderMap.put(5, 44);
    ladderMap.put(39, 58);
    ladderMap.put(46, 87);

    Map<Integer, Integer> slideMap = new HashMap<>();
    slideMap.put(34, 13);
    slideMap.put(82, 78);
    slideMap.put(89, 68);

    for (Map.Entry<Integer, Integer> entry : ladderMap.entrySet()) {
      this.board.getTile(entry.getKey()).setLandAction(new LadderAction(entry.getValue(), "Ladder"));
    }
    for (Map.Entry<Integer, Integer> entry : slideMap.entrySet()) {
      this.board.getTile(entry.getKey()).setLandAction(new LadderAction(entry.getValue(), "Slide"));
    }
  }
}
