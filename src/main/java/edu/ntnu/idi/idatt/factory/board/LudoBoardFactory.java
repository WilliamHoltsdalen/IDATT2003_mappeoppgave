package edu.ntnu.idi.idatt.factory.board;

import edu.ntnu.idi.idatt.model.board.Board;
import edu.ntnu.idi.idatt.model.board.LudoGameBoard;
import javafx.scene.paint.Color;

public class LudoBoardFactory {

  public LudoBoardFactory() {

  }

  public Board createBoard(String variant) {
    return switch (variant) {
      case "classic" -> createClassicBoard();
      case "small" -> createSmallBoard();
      case "large" -> createLargeBoard();
      default -> throw new IllegalArgumentException("Invalid board variant: " + variant);
    };
  }

  private Board createClassicBoard() {
    Board board = new LudoGameBoard("Classic ludo", "Classic 15x15 ludo board", "media/boards/whiteBoard.png", 15, new Color[] {Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW});
    return board;
  }

  private Board createSmallBoard() {
    Board board = new LudoGameBoard("Small ludo", "Small 9x9 ludo board", "media/boards/whiteBoard.png", 9, new Color[] {Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW});
    return board;
  }

  private Board createLargeBoard() {
    Board board = new LudoGameBoard("Large ludo", "Large 21x21 ludo board", "media/boards/whiteBoard.png", 21, new Color[] {Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW});
    return board;
  }
}
