package edu.ntnu.idi.idatt.view.ludo;

import edu.ntnu.idi.idatt.model.board.Board;
import edu.ntnu.idi.idatt.model.board.LudoGameBoard;
import edu.ntnu.idi.idatt.model.player.Player;
import edu.ntnu.idi.idatt.view.common.GamePlayersBox;
import edu.ntnu.idi.idatt.view.common.GameStackPane;
import edu.ntnu.idi.idatt.view.common.GameView;
import java.util.List;
import javafx.beans.binding.DoubleBinding;

public class LudoGameView extends GameView {

  public LudoGameView() {
    super();
  }

  @Override
  public GamePlayersBox createPlayersBox(List<Player> players, int roundNumber) {
    return new LudoGamePlayersBox(players, roundNumber);
  }

  @Override
  public GameStackPane createGameStackPane(Board board, List<Player> players, DoubleBinding observableWidth) {
    return new LudoGameStackPane((LudoGameBoard) board, players, observableWidth);
  }
}
