package edu.ntnu.idi.idatt.factory.board;

import edu.ntnu.idi.idatt.filehandler.FileHandler;
import edu.ntnu.idi.idatt.filehandler.LudoBoardFileHandlerGson;
import edu.ntnu.idi.idatt.model.board.Board;
import edu.ntnu.idi.idatt.model.board.LudoGameBoard;
import java.io.IOException;
import javafx.scene.paint.Color;

/**
 * <h3>LudoBoardFactory.</h3>
 *
 * <p>This class implements the {@link BoardFactory} interface to create Ludo game boards.
 * It provides methods to create Ludo boards based on predefined variants (e.g., "Classic",
 * "Small", "Large"), from an external file, or as a blank board of a specified size.</p>
 *
 * <p>Board variants are typically loaded from JSON files, and file handling for creating
 * boards from external files is delegated to {@link LudoBoardFileHandlerGson}.</p>
 *
 * @see BoardFactory
 * @see LudoGameBoard
 * @see LudoBoardFileHandlerGson
 */
public class LudoBoardFactory implements BoardFactory {

  /**
   * Constructs a new {@code LudoBoardFactory}.
   */
  public LudoBoardFactory() {
    super();
  }

  /**
   * Creates a {@link LudoGameBoard} object based on predefined variants stored as json files in the
   * "resources/boards" directory.
   *
   * @param variant A string specifying which board variant to create. Supported variants are
   *                "Classic", "Small", and "Large".
   * @return A configured {@link LudoGameBoard} object corresponding to the specified variant.
   * @throws IllegalArgumentException if the specified variant is not recognized.
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
   * Creates a {@link LudoGameBoard} object from an external file. File handling is delegated to the
   * {@link LudoBoardFileHandlerGson} class.
   *
   * @param filePath The path to the file containing the board data.
   * @return A configured {@link LudoGameBoard} object, or {@code null} if an IOException occurs
   *         during file reading.
   * @see LudoBoardFileHandlerGson
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
   * Creates a blank {@link LudoGameBoard} object with the given size and default player colors.
   *
   * @param rows    The number of rows for the board (determines board size).
   * @param columns The number of columns for the board (typically same as rows for Ludo).
   * @return A newly configured, blank {@link LudoGameBoard} object.
   */
  @Override
  public Board createBlankBoard(int rows, int columns) {
    return new LudoGameBoard("Blank ludo board", "Blank ludo board", "media/boards/whiteBoard.png",
        rows, new Color[]{Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW});
  }

  /**
   * Creates a Classic {@link LudoGameBoard} object by loading its configuration from the
   * "src/main/resources/boards/ClassicLudoBoard.json" file.
   *
   * @return A configured Classic {@link LudoGameBoard} object, or {@code null} if file reading
   *     fails.
   */
  private Board createClassicBoard() {
    Board board = createBoardFromFile("src/main/resources/boards/ClassicLudoBoard.json");
    if (board == null) {
      // TODO: Do something in case of null board, even if its just logging.
    }
    return board;
  }

  /**
   * Creates a Small {@link LudoGameBoard} object by loading its configuration from the
   * "src/main/resources/boards/SmallLudoBoard.json" file.
   *
   * @return A configured Small {@link LudoGameBoard} object, or {@code null} if file reading fails.
   */
  private Board createSmallBoard() {
    Board board = createBoardFromFile("src/main/resources/boards/SmallLudoBoard.json");
    if (board == null) {
      // TODO: Do something in case of null board, even if its just logging.
    }
    return board;
  }

  /**
   * Creates a Large (XL) {@link LudoGameBoard} object by loading its configuration from the
   * "src/main/resources/boards/XlLudoBoard.json" file.
   *
   * @return A configured Large {@link LudoGameBoard} object, or {@code null} if file reading fails.
   */
  private Board createLargeBoard() {
    Board board = createBoardFromFile("src/main/resources/boards/XlLudoBoard.json");
    if (board == null) {
      // TODO: Do something in case of null board, even if its just logging.
    }
    return board;
  }
}
