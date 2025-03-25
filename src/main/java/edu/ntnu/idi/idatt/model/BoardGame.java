package edu.ntnu.idi.idatt.model;

import static edu.ntnu.idi.idatt.model.validators.ArgumentValidator.boardGameAddPlayersValidator;
import static edu.ntnu.idi.idatt.model.validators.ArgumentValidator.boardGameCreateBoardValidator;
import static edu.ntnu.idi.idatt.model.validators.ArgumentValidator.boardGameCreateDiceValidator;
import static edu.ntnu.idi.idatt.model.validators.ArgumentValidator.boardGameSetCurrentPlayerValidator;

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

  public BoardGame(Board board, List<Player> players, int diceCount) {
    this.players = new ArrayList<>();
    this.roundNumber = 0;

    this.board = board;
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
}
