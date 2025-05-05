package edu.ntnu.idi.idatt.view.ludo;

import java.util.List;

import edu.ntnu.idi.idatt.model.board.Board;
import edu.ntnu.idi.idatt.model.board.LudoGameBoard;
import edu.ntnu.idi.idatt.model.player.Player;
import edu.ntnu.idi.idatt.view.common.GameStackPane;
import edu.ntnu.idi.idatt.view.common.GameView;
import edu.ntnu.idi.idatt.view.component.GamePlayersBox;

public class LudoGameView extends GameView {

    public LudoGameView() {
        super();
    }

    @Override
    public GamePlayersBox createPlayersBox(List<Player> players, int roundNumber) {
        return new GamePlayersBox(players, roundNumber);
    }

    @Override
    public GameStackPane createGameStackPane(Board board, List<Player> players) {
        return new LudoGameStackPane((LudoGameBoard) board, players);
    }
}
