package edu.ntnu.idi.idatt.entities;

import java.util.List;

public class BoardGame {
  Board board;
  Player currentPlayer;
  List<Player> players;
  Dice dice;


  public Player getWinner() {
    return null;
  }

  public Board getBoard() {
    return board;
  }

  public Player getCurrentPlayer() {
    return currentPlayer;
  }

  public List<Player> getAllPlayers() {
    return players;
  }

  public Dice getDice() {
    return dice;
  }

  public void addPlayer(Player player) {

  }

  public void createBoard() {

  }

  public void createDice() {

  }

  public void play() {

  }
}
