package edu.ntnu.idi.idatt.factory;

import java.io.IOException;

import edu.ntnu.idi.idatt.filehandler.BoardFileHandlerGson;
import edu.ntnu.idi.idatt.filehandler.FileHandler;
import edu.ntnu.idi.idatt.model.Board;

/**
 * <h3>Factory class for creating Board objects.</h3>
 *
 * <p>This class provides methods for creating Board objects based on predefined variants.
 * Board objects can be created from an external file, or from scratch.
 */
public class BoardFactory {

  /** Private constructor to prevent instantiation. */
  private BoardFactory() {}

  /**
   * Creates a Board object based on a predefined variant (hardcoded).
   *
   * @param variant A string specifying which board variant to create.
   * @return A configured Board object.
   * @throws IllegalArgumentException if the variant is not recognized.
   */
  public static Board createBoard(String variant) {
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
   * @param filePath The path to the JSON file containing board data.
   * @return A Board object constructed from the file data.
   */
  public static Board createBoardFromFile(String filePath){
    FileHandler<Board> boardFileHandler = new BoardFileHandlerGson();
    try {
      return (Board) boardFileHandler.readFile(filePath);
    } catch (IOException e) {
      return null;
    }
  }

  public static Board createBlankBoard(int rows, int columns) {
    return new Board("Blank Board", "Blank board with " + rows + " rows and " + columns + " columns.",
        new int[]{rows, columns}, "media/boards/whiteBoard.png", "None");
  }

  /**
   * Creates a classic board layout.
   *
   * @return A classic Board object.
   */
  private static Board createClassicBoard() {
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
  private static Board createPortalBoard()  {
    Board board = createBoardFromFile("src/main/resources/boards/PortalBoard.json");
    if (board == null) {
      // TODO: Do something in case of null board, even if its just logging.
    }
    return board;
  }
}