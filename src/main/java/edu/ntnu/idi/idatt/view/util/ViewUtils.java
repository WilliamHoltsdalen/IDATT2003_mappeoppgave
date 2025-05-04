package edu.ntnu.idi.idatt.view.util;
import edu.ntnu.idi.idatt.model.board.LadderGameBoard;
import java.util.List;
import java.util.Random;

import edu.ntnu.idi.idatt.model.board.Board;

/**
 * Utility class containing helper methods for view-related operations. This
 * class provides static methods for common view calculations and conversions.
 */
public class ViewUtils {
  private static final Random random = new Random();

  /**
   * Private constructor to prevent instantiation of utility class.
   */
  private ViewUtils() {
  }

  /**
   * Converts board coordinates (row, column) to screen coordinates (x, y).
   * The board uses a coordinate system with origin at the bottom left, while
   * the screen uses a coordinate system with origin at the top left.
   *
   * @param coordinates the array of coordinates (in the board's coordinate
   * system (row, column))
   * @param board the game board containing dimension information
   * @param boardWidth the visual width of the board on screen
   * @param boardHeight the visual height of the board on screen
   * @return the array of coordinates in the screen's coordinate system (x, y)
   */
  public static double[] boardToScreenCoordinates(int[] coordinates, LadderGameBoard board,
      double boardWidth, double boardHeight) {
    int row = coordinates[0];
    int col = coordinates[1];

    int maxRows = board.getRowsAndColumns()[0];
    int maxCols = board.getRowsAndColumns()[1];

    double x = (boardWidth / maxCols) * col;
    double y = boardHeight - ((boardHeight / maxRows) * row);

    return new double[]{x, y};
  }

  /**
   * Calculates the tile ID for a given row and column in a snake pattern
   * board. The pattern starts from bottom left and snakes upward.
   *
   * @param row the row number (0-based, from bottom)
   * @param col the column number (0-based, from left)
   * @param columns total number of columns
   * @return the tile ID for the given position
   */
  public static int calculateTileId(int row, int col, int columns) {
    if (row % 2 == 0) { // Even rows (0-based) go left to right
      return row * columns + col + 1;
    }
    return row * columns + (columns - col); // Odd rows go right to left
  }

  public static int randomPortalDestination(int portalId, int tiles, List<Integer> occupiedTiles) {
    int id;
    do {
      id = random.nextInt(1, tiles);
    } while (id == portalId || occupiedTiles.contains(id) || id == tiles - 1);
    return id;
  }
}
