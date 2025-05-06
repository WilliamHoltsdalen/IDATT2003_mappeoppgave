package edu.ntnu.idi.idatt.view.laddergame;

import java.util.List;

import edu.ntnu.idi.idatt.model.board.Board;
import edu.ntnu.idi.idatt.model.board.LadderGameBoard;
import edu.ntnu.idi.idatt.model.player.Player;
import edu.ntnu.idi.idatt.view.common.GamePlayersBox;
import edu.ntnu.idi.idatt.view.common.GameStackPane;
import edu.ntnu.idi.idatt.view.common.GameView;
import edu.ntnu.idi.idatt.view.component.GameMenuBox;

public class LadderGameView extends GameView {

  public LadderGameView() {
    super();
  }

  @Override
  public GamePlayersBox createPlayersBox(List<Player> players, int roundNumber) {
    return new LadderGamePlayersBox(players, roundNumber);
  }

  @Override
  public GameStackPane createGameStackPane(Board board, List<Player> players) {
    return new LadderGameStackPane((LadderGameBoard) board, players);
  }

  @Override
  public GameMenuBox createGameMenuBox() {
    GameMenuBox box = new GameMenuBox(2); // Use 2 dice for Ladder game
    box.setOnRestartGame(() -> notifyObservers("restart_game"));
    box.setOnQuitGame(() -> notifyObservers("quit_game"));
    box.setOnRollDice(() -> notifyObservers("roll_dice"));
    return box;
  }
}
