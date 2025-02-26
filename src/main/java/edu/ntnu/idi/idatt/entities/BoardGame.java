package edu.ntnu.idi.idatt.entities;

import java.util.ArrayList;
import java.util.List;

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

  private void createBoard(int rows, int columns) throws IllegalArgumentException {
    if (rows < 1 || columns < 1) {
      throw new IllegalArgumentException("Board must have at least 1 row and 1 column");
    }
    this.board = new Board(rows, columns);
  }

  private void addPlayers(List<Player> players) throws IllegalArgumentException {
    if (players == null) {
      throw new IllegalArgumentException("Player cannot be null");
    }
    for (Player player : players) {
      if (player == null) {
        throw new IllegalArgumentException("Players in the provided list cannot be null");
      }
      this.players.add(player);
    }
  }

  private void createDice(int diceCount) throws IllegalArgumentException {
    if (diceCount < 1) {
      throw new IllegalArgumentException("Dice count must be greater than 0");
    }
    this.dice = new Dice(diceCount);
  }

  public void setCurrentPlayer(Player player) throws IllegalArgumentException {
    if (player == null) {
      throw new IllegalArgumentException("Player cannot be null");
    }
    this.currentPlayer = player;
  }

  public void incrementRoundNumber() {
    roundNumber++;
  }
}
