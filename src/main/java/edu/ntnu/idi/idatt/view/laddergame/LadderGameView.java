package edu.ntnu.idi.idatt.view.laddergame;

import java.util.List;

import edu.ntnu.idi.idatt.model.board.Board;
import edu.ntnu.idi.idatt.model.board.LadderGameBoard;
import edu.ntnu.idi.idatt.model.player.Player;
import edu.ntnu.idi.idatt.view.common.GameStackPane;
import edu.ntnu.idi.idatt.view.common.GameView;
import edu.ntnu.idi.idatt.view.component.GamePlayersBox;

public class LadderGameView extends GameView {

  public LadderGameView() {
    super();
  }

  @Override
  public GamePlayersBox createPlayersBox(List<Player> players, int roundNumber) {
    return new GamePlayersBox(players, roundNumber);
  }

  @Override
  public GameStackPane createGameStackPane(Board board, List<Player> players) {
    return new LadderGameStackPane((LadderGameBoard) board, players);
  }
}
