package edu.ntnu.idi.idatt.factory;

import java.io.IOException;
import java.util.List;
import java.util.Random;

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
  private static final Random random = new Random();

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
   * File handling is delegated to the BoardFileHandlerGson class.
   *
   * @param filePath The path to the JSON file containing board data.
   * @return A Board object constructed from the file data.
   * @throws IOException if an error occurs during file processing
   * @throws IllegalArgumentException if the file does not contain a valid board.
   */
  public static Board createBoardFromFile(String filePath) throws IOException {
    FileHandler<Board> boardFileHandler = new BoardFileHandlerGson();
    List<Board> boards = boardFileHandler.readFile(filePath);
    if (boards.isEmpty()) {
      throw new IllegalArgumentException("No boards found in file: " + filePath);
    }

    // Todo: support multiple boards.

    return boards.getFirst();
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
    try {
      return createBoardFromFile("src/main/resources/boards/ClassicBoard.json");
    } catch (IOException e) {
      e.printStackTrace(); // TODO: Handle exception
    }
    return null;
  }

  /**
   * Creates a board layout with portals.
   *
   * @return A Board object with portals.
   */
  private static Board createPortalBoard() {
    try {
      return createBoardFromFile("src/main/resources/boards/PortalBoard.json");
    } catch (IOException e) {
      e.printStackTrace(); // TODO: Handle exception
    }
    return null;
  }
}