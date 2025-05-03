package edu.ntnu.idi.idatt.factory.board;

import java.io.IOException;

import edu.ntnu.idi.idatt.filehandler.BoardFileHandlerGson;
import edu.ntnu.idi.idatt.filehandler.FileHandler;
import edu.ntnu.idi.idatt.model.board.Board;
import edu.ntnu.idi.idatt.model.board.LadderBoard;

/**
 * <h3>Factory class for creating LadderBoard objects.</h3>
 *
 * <p>This class provides methods for creating LadderBoard objects based on predefined variants.
 * LadderBoard objects can also be created from an external file. 
 */
public class LadderBoardFactory implements BoardFactory {

  /**
   * Creates a LadderBoard object based on predefined variants stored as json files in the
   * "resources/boards" directory.
   *
   * @param variant A string specifying which board variant to create.
   * @return A configured LadderBoard object.
   * @throws IllegalArgumentException if the variant is not recognized.
   */
  @Override
  public Board createBoard(String variant) {
    return switch (variant.toLowerCase()) {
      case "classic" -> createClassicBoard();
      case "teleporting" -> createPortalBoard();
      default -> throw new IllegalArgumentException("Unknown board variant: " + variant);
    };
  }

  /**
   * Creates a Board object by reading from an external file.
   * File handling is delegated to the {@link BoardFileHandlerGson} class.
   *
   * @see BoardFileHandlerGson
   * @param filePath The path to the JSON file containing board data.
   * @return A Board object constructed from the file data.
   */
  @Override
  public Board createBoardFromFile(String filePath){
    FileHandler<Board> boardFileHandler = new BoardFileHandlerGson();
    try {
      return (Board) boardFileHandler.readFile(filePath);
    } catch (IOException e) {
      return null;
    }
  }

  /**
   * Creates a blank LadderBoard object with the given number of rows and columns.
   *
   * @param rows The number of rows in the board.
   * @param columns The number of columns in the board.
   * @return A LadderBoard object with the given number of rows and columns, that has no tile actions.
   */
  @Override
  public Board createBlankBoard(int rows, int columns) {
    return new LadderBoard("Blank Board", "Blank board with " + rows + " rows and " + columns + " columns.",
        new int[]{rows, columns}, "media/boards/whiteBoard.png", "None");
  }

  /**
   * Creates a classic board layout.
   *
   * @return A classic Board object.
   */
  private Board createClassicBoard() {
    Board board = createBoardFromFile("src/main/resources/boards/ClassicBoard.json");
    if (board == null) {
      // TODO: Do something in case of null board, even if its just logging.
    }
    return board;
  }

  /**
   * Creates a board layout with portals.
   *
   * @return A Board object with portals.
   */
  private Board createPortalBoard()  {
    Board board = createBoardFromFile("src/main/resources/boards/PortalBoard.json");
    if (board == null) {
      // TODO: Do something in case of null board, even if its just logging.
    }
    return board;
  }
}