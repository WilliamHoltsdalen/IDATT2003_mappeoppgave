package edu.ntnu.idi.idatt.factory.board;

import java.io.IOException;

import edu.ntnu.idi.idatt.filehandler.FileHandler;
import edu.ntnu.idi.idatt.filehandler.LudoBoardFileHandlerGson;
import edu.ntnu.idi.idatt.model.board.Board;
import edu.ntnu.idi.idatt.model.board.LudoGameBoard;
import javafx.scene.paint.Color;

public class LudoBoardFactory implements BoardFactory {

  public LudoBoardFactory() {
    super();
  }

  /**
   * Creates a LudoGameBoard object based on predefined variants stored as json files in the
   * "resources/boards" directory.
   *
   * @param variant A string specifying which board variant to create.
   * @return A configured LudoGameBoard object.
   */
  @Override
  public Board createBoard(String variant) {
    return switch (variant) {
      case "Classic" -> createClassicBoard();
      case "Small" -> createSmallBoard();
      case "Large" -> createLargeBoard();
      default -> throw new IllegalArgumentException("Invalid board variant: " + variant);
    };
  }

  /**
   * Creates a LudoGameBoard object from an external file.
   * File handling is delegated to the {@link LudoBoardFileHandlerGson} class.
   *
   * @see LudoBoardFileHandlerGson
   * @param filePath The path to the file containing the board data.
   * @return A configured LudoGameBoard object.
   */
  @Override
  public Board createBoardFromFile(String filePath) {
    FileHandler<Board> fileHandler = new LudoBoardFileHandlerGson();
    try {
      return (Board) fileHandler.readFile(filePath);
    } catch (IOException e) {
      return null;
    }
  }

  /**
   * Creates a blank LudoGameBoard object with the given size.
   *
   * @param rows The size of the board.
   * @param columns The size of the board.
   * @return A configured LudoGameBoard object.
   */
  @Override
  public Board createBlankBoard(int rows, int columns) {
    return new LudoGameBoard("Blank ludo board", "Blank ludo board", "media/boards/whiteBoard.png", rows, new Color[] {Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW});
  }

  /**
   * Creates a Classic LudoGameBoard object.
   *
   * @return A configured LudoGameBoard object.
   */
  private Board createClassicBoard() {
    Board board = new LudoGameBoard("Classic ludo", "Classic 15x15 ludo board", "media/boards/whiteBoard.png", 15, new Color[] {Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW});
    return board;
  }

  /**
   * Creates a Small LudoGameBoard object.
   *
   * @return A configured LudoGameBoard object.
   */
  private Board createSmallBoard() {
    Board board = new LudoGameBoard("Small ludo", "Small 9x9 ludo board", "media/boards/whiteBoard.png", 9, new Color[] {Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW});
    return board;
  }

  /**
   * Creates a Large LudoGameBoard object.
   *
   * @return A configured LudoGameBoard object.
   */
  private Board createLargeBoard() {
    Board board = new LudoGameBoard("Large ludo", "Large 21x21 ludo board", "media/boards/whiteBoard.png", 21, new Color[] {Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW});
    return board;
  }
}
