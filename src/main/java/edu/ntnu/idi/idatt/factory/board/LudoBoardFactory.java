package edu.ntnu.idi.idatt.factory.board;

import edu.ntnu.idi.idatt.model.board.Board;
import edu.ntnu.idi.idatt.model.board.LudoGameBoard;
import javafx.scene.paint.Color;

public class LudoBoardFactory {

  public LudoBoardFactory() {

  }

  public Board createBoard(String variant) {
    return createClassicBoard();
  }

  private Board createClassicBoard() {
    Board board = new LudoGameBoard("Classic ludo", "Classic 9x9 ludo board", 9, new Color[] {Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW});
    return board;
  }
}
