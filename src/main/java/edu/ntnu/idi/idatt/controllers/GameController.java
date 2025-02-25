package edu.ntnu.idi.idatt.controllers;

import edu.ntnu.idi.idatt.entities.BoardGame;
import edu.ntnu.idi.idatt.entities.Player;
import java.util.List;

public class GameController {
  private BoardGame boardGame;

  public Player getWinner() {
    return this.boardGame.getWinner();
  }

  public int getRoundNumber() {
    return this.boardGame.getRoundNumber();
  }

  public List<Player> getPlayers() {
    return this.boardGame.getPlayers();
  }

  public void initController(List<Player> players) {
    this.boardGame = new BoardGame(10, 9, players, 2);
    for (Player player : this.boardGame.getPlayers()) {
      player.placeOnTile(this.boardGame.getBoard().getTile(0));
    }
    this.boardGame.setCurrentPlayer(this.boardGame.getPlayers().getFirst());
  }

  public void playMove() {
    if (this.boardGame.getCurrentPlayer() == this.boardGame.getPlayers().getFirst()) {
      this.boardGame.incrementRoundNumber();
    }
    this.boardGame.rollDiceForCurrentPlayer();
    this.boardGame.updateCurrentPlayer();
  }
}
