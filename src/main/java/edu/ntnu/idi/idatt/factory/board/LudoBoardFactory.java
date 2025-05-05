package edu.ntnu.idi.idatt.factory.board;

import edu.ntnu.idi.idatt.model.board.Board;
import edu.ntnu.idi.idatt.model.board.LudoGameBoard;
import javafx.scene.paint.Color;

public class LudoBoardFactory implements BoardFactory {

  public LudoBoardFactory() {
    super();
  }

  @Override
  public Board createBoard(String variant) {
    return switch (variant) {
      case "Classic" -> createClassicBoard();
      case "Small" -> createSmallBoard();
      case "Large" -> createLargeBoard();
      default -> throw new IllegalArgumentException("Invalid board variant: " + variant);
    };
  }

  @Override
  public Board createBoardFromFile(String filePath) {
    return null;
  }

  @Override
  public Board createBlankBoard(int rows, int columns) {
    return new LudoGameBoard("Blank ludo", "Blank ludo board", "media/boards/whiteBoard.png", rows, new Color[] {Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW});
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
