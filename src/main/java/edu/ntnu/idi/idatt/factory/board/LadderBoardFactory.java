package edu.ntnu.idi.idatt.factory.board;

import edu.ntnu.idi.idatt.filehandler.FileHandler;
import edu.ntnu.idi.idatt.filehandler.LadderGameBoardFileHandlerGson;
import edu.ntnu.idi.idatt.model.board.Board;
import edu.ntnu.idi.idatt.model.board.LadderGameBoard;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <h3>Factory class for creating LadderGameBoard objects.</h3>
 *
 * <p>This class provides methods for creating LadderGameBoard objects based on predefined variants.
 * LadderGameBoard objects can also be created from an external file.
 */
public class LadderBoardFactory implements BoardFactory {
  private static final Logger logger = LoggerFactory.getLogger(LadderBoardFactory.class);

  /**
   * Creates a LadderGameBoard object based on predefined variants stored as json files in the
   * "resources/boards" directory.
   *
   * @param variant A string specifying which board variant to create.
   * @return A configured LadderGameBoard object.
   * @throws IllegalArgumentException if the variant is not recognized.
   */
  @Override
  public Board createBoard(String variant) {
    logger.debug("Creating board variant: {}", variant);
    return switch (variant.toLowerCase()) {
      case "classic" -> createClassicBoard();
      case "teleporting" -> createPortalBoard();
      default -> {
        logger.error("Unknown variant {}", variant);
        throw new IllegalArgumentException("Unknown board variant: " + variant);
      }
    };
  }

  /**
   * Creates a Board object by reading from an external file.
   * File handling is delegated to the {@link LadderGameBoardFileHandlerGson} class.
   *
   * @see LadderGameBoardFileHandlerGson
   * @param filePath The path to the JSON file containing board data.
   * @return A Board object constructed from the file data.
   */
  @Override
  public Board createBoardFromFile(String filePath) {
    logger.debug("Attempting to create board variant from file: {}", filePath);
    FileHandler<Board> boardFileHandler = new LadderGameBoardFileHandlerGson();
    try {
      logger.debug("successfully created board from file: {}", filePath);
      return (Board) boardFileHandler.readFile(filePath);
    } catch (IOException e) {
      logger.error("Could not create board from file: {}", filePath);
      return null;
    }
  }

  /**
   * Creates a blank LadderGameBoard object with the given number of rows and columns.
   *
   * @param rows The number of rows in the board.
   * @param columns The number of columns in the board.
   * @return A LadderGameBoard object with the given number of rows and columns, that has no tile
   *     actions.
   */
  @Override
  public Board createBlankBoard(int rows, int columns) {
    logger.debug("Attempting to create blank board variant with size {}x{}", rows, columns);
    return new LadderGameBoard("Blank Board", "Blank board with " + rows
        + " rows and " + columns + " columns.", new int[]{rows, columns},
        "media/boards/whiteBoard.png", "None");
  }

  /**
   * Creates a classic board layout.
   *
   * @return A classic Board object.
   */
  private Board createClassicBoard() {
    Board board = createBoardFromFile("src/main/resources/boards/ClassicLadderGameBoard.json");
    if (board == null) {
      logger.error("Could not create classic board from file");
    }
    return board;
  }

  /**
   * Creates a board layout with portals.
   *
   * @return A Board object with portals.
   */
  private Board createPortalBoard()  {
    Board board = createBoardFromFile("src/main/resources/boards/PortalLadderGameBoard.json");
    if (board == null) {
      logger.error("Could not create portal board from file");
    }
    return board;
  }
}