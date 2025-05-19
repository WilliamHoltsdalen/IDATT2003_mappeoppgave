package edu.ntnu.idi.idatt.factory.board;

import edu.ntnu.idi.idatt.model.board.Board;

/**
 * BoardFactory Interface.
 *
 * <p>Defines the necessary methods for factories that create game boards.</p>
 *
 * <p>Implementations of this interface are responsible for instantiating
 * different types of boards.</p>
 */
public interface BoardFactory {

  /**
   * Creates a game board of a specific variant.
   *
   * @param variant A string identifier for the board variant.
   * @return The created {@link Board} instance.
   */
  Board createBoard(String variant);

  /**
   * Creates a game board by loading its configuration from a file.
   *
   * @param filePath The path to the file containing the board data.
   * @return The created {@link Board} instance.
   */
  Board createBoardFromFile(String filePath);

  /**
   * Creates a blank game board with the specified number of rows and columns.
   *
   * @param rows The number of rows for the board.
   * @param columns The number of columns for the board.
   * @return The created {@link Board} instance.
   */
  Board createBlankBoard(int rows, int columns);
}
