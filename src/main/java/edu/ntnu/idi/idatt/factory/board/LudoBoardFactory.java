package edu.ntnu.idi.idatt.factory.board;

import edu.ntnu.idi.idatt.filehandler.FileHandler;
import edu.ntnu.idi.idatt.filehandler.LudoBoardFileHandlerGson;
import edu.ntnu.idi.idatt.model.board.Board;
import edu.ntnu.idi.idatt.model.board.LudoGameBoard;
import java.io.IOException;
import javafx.scene.paint.Color;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LudoBoardFactory implements BoardFactory {
  private static final Logger logger = LoggerFactory.getLogger(LudoBoardFactory.class);

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
    logger.debug("creating ludo board with variant: {}", variant);
    return switch (variant) {
      case "Classic" -> createClassicBoard();
      case "Small" -> createSmallBoard();
      case "Large" -> createLargeBoard();
      default -> {
        logger.error("Invalid ludo board variant type: {}", variant);
        throw new IllegalArgumentException("Invalid board variant: " + variant);
      }
    };
  }

  /**
   * Creates a LudoGameBoard object from an external file. File handling is delegated to the
   * {@link LudoBoardFileHandlerGson} class.
   *
   * @param filePath The path to the file containing the board data.
   * @return A configured LudoGameBoard object.
   * @see LudoBoardFileHandlerGson
   */
  @Override
  public Board createBoardFromFile(String filePath) {
    logger.debug("attempting to create ludo board from file: {}", filePath);
    FileHandler<Board> fileHandler = new LudoBoardFileHandlerGson();
    try {
      logger.info("successfully loaded ludo board from file: {}", filePath);
      return (Board) fileHandler.readFile(filePath);
    } catch (IOException e) {
      logger.error("failed to load ludo board from file: {}", filePath);
      return null;
    }
  }

  /**
   * Creates a blank LudoGameBoard object with the given size.
   *
   * @param rows    The size of the board.
   * @param columns The size of the board.
   * @return A configured LudoGameBoard object.
   */
  @Override
  public Board createBlankBoard(int rows, int columns) {
    logger.debug("Creating blank board with size: {}x{}", rows, columns);
    return new LudoGameBoard("Blank ludo board", "Blank ludo board", "media/boards/whiteBoard.png",
        rows, new Color[]{Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW});
  }

  /**
   * Creates a Classic LudoGameBoard object.
   *
   * @return A configured LudoGameBoard object.
   */
  private Board createClassicBoard() {
    Board board = createBoardFromFile("src/main/resources/boards/ClassicLudoBoard.json");
    if (board == null) {
      logger.error("failed to create classic board from file");
    }
    return board;
  }

  /**
   * Creates a Small LudoGameBoard object.
   *
   * @return A configured LudoGameBoard object.
   */
  private Board createSmallBoard() {
    Board board = createBoardFromFile("src/main/resources/boards/SmallLudoBoard.json");
    if (board == null) {
      logger.error("failed to create small board from file");
    }
    return board;
  }

  /**
   * Creates a Large LudoGameBoard object.
   *
   * @return A configured LudoGameBoard object.
   */
  private Board createLargeBoard() {
    Board board = createBoardFromFile("src/main/resources/boards/XlLudoBoard.json");
    if (board == null) {
      logger.error("failed to create large board from file");
    }
    return board;
  }
}
