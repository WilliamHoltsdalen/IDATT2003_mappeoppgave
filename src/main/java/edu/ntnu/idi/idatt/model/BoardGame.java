package edu.ntnu.idi.idatt.model;

import static edu.ntnu.idi.idatt.model.validators.ArgumentValidator.boardGameAddPlayersValidator;
import static edu.ntnu.idi.idatt.model.validators.ArgumentValidator.boardGameCreateDiceValidator;
import static edu.ntnu.idi.idatt.model.validators.ArgumentValidator.boardGameSetBoardValidator;
import static edu.ntnu.idi.idatt.model.validators.ArgumentValidator.boardGameSetCurrentPlayerValidator;
import static edu.ntnu.idi.idatt.model.validators.ArgumentValidator.boardGameSetPlayersValidator;

import edu.ntnu.idi.idatt.observer.BoardGameObserver;
import java.util.ArrayList;
import java.util.List;

public class BoardGame {
  private Board board;
  private List<Player> players;
  private Dice dice;
  private Player currentPlayer;
  private int roundNumber;
  private final List<BoardGameObserver> observers;

  public BoardGame(Board board, List<Player> players, int diceCount) {
    this.players = new ArrayList<>();
    this.roundNumber = 0;
    this.observers = new ArrayList<>();

    setBoard(board);
    addPlayers(players);
    createDice(diceCount);
  }

  public void addObserver(BoardGameObserver observer) {
    observers.add(observer);
  }

  public void removeObserver(BoardGameObserver observer) {
    observers.remove(observer);
  }

  public List<BoardGameObserver> getObservers() {
    return observers;
  }

  public void notifyPlayerMoved(Player player, int newTileId) {
    for (BoardGameObserver observer : observers) {
      observer.onPlayerMoved(player, newTileId);
    }
  }

  public void notifyGameStateChanged(String stateUpdate) {
    for (BoardGameObserver observer : observers) {
      observer.onGameStateChanged(stateUpdate);
    }
  }

  public void notifyGameFinished(Player winner) {
    for (BoardGameObserver observer : observers) {
      observer.onGameFinished(winner);
    }
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
      if (player.getCurrentTile().getTileId() == this.board.getTileCount()) return player;
    }
    return null;
  }

  public void setBoard(Board board) {
    boardGameSetBoardValidator(board);
    this.board = board;
  }

  public void setPlayers(List<Player> players) {
    boardGameSetPlayersValidator(players);
    this.players = players;
  }

  private void addPlayers(List<Player> players) {
    boardGameAddPlayersValidator(players, this.players.size());

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
