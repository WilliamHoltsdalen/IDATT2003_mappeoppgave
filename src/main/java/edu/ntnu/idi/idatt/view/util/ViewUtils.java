package edu.ntnu.idi.idatt.view.util;

import edu.ntnu.idi.idatt.model.board.LadderGameBoard;
import edu.ntnu.idi.idatt.model.board.LudoGameBoard;
import java.util.List;
import java.util.Random;

/**
 * ViewUtils.
 *
 * <p>A utility class providing static helper methods for various view-related operations.
 * This includes coordinate transformations between board-specific coordinate systems
 * and screen coordinates, as well as other common calculations useful for rendering
 * game boards and their elements.</p>
 *
 * <p>This class is not meant to be instantiated; all its methods are static.</p>
 */
public class ViewUtils {

  private static final Random random = new Random();

  /**
   * Private constructor to prevent instantiation of this utility class.
   */
  private ViewUtils() {
  }

  /**
   * Converts ladder board coordinates (row, column) to screen coordinates (x, y).
   * The ladder board typically uses a coordinate system with the origin (0,0) at the
   * bottom-left, while the screen's origin (0,0) is at the top-left.
   * This method performs the necessary transformation.
   *
   * @param coordinates An integer array {@code [row, col]} representing the coordinates in the
   *                    ladder board's system.
   * @param board       The {@link LadderGameBoard} instance, used to get total rows and columns.
   * @param boardWidth  The visual width of the board as displayed on the screen.
   * @param boardHeight The visual height of the board as displayed on the screen.
   * @return A double array {@code [x, y]} representing the corresponding coordinates in the
   *         screen's system.
   */
  public static double[] ladderBoardToScreenCoordinates(int[] coordinates, LadderGameBoard board,
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
   * Converts Ludo board coordinates (row, column) to screen coordinates (x, y).
   * For Ludo, both the board coordinate system and the screen coordinate system typically
   * have their origin (0,0) at the top-left, so this is a direct scaling.
   *
   * @param coordinates An integer array {@code [row, col]} representing the coordinates in the
   *                    Ludo board's system.
   * @param board       The {@link LudoGameBoard} instance, used to get the board size.
   * @param boardWidth  The visual width of the board as displayed on the screen.
   * @param boardHeight The visual height of the board as displayed on the screen.
   * @return A double array {@code [x, y]} representing the corresponding coordinates in the
   *         screen's system.
   */
  public static double[] ludoBoardToScreenCoordinates(int[] coordinates, LudoGameBoard board,
      double boardWidth, double boardHeight) {
    int row = coordinates[0];
    int col = coordinates[1];

    double x = (boardWidth / board.getBoardSize()) * col;
    double y = (boardHeight / board.getBoardSize() * row);

    return new double[]{x, y};
  }

  /**
   * Calculates the tile ID for a given row and column on a board with a "snake" 
   * numbering pattern. The pattern starts from tile 1 at the bottom-left
   * (row 0, col 0), proceeds right on even rows (0, 2, ...), and left on odd rows (1, 3, ...).
   *
   * @param row     The row number (0-indexed from the bottom).
   * @param col     The column number (0-indexed from the left).
   * @param columns The total number of columns in the grid.
   * @return The calculated tile ID (1-indexed).
   */
  public static int calculateTileId(int row, int col, int columns) {
    if (row % 2 == 0) { // Even rows (0-based) go left to right
      return row * columns + col + 1;
    }
    return row * columns + (columns - col); // Odd rows go right to left
  }

  /**
   * Generates a random destination tile ID for a portal (e.g., a snake or ladder).
   * The destination will be different from the portal's own ID, not already occupied by
   * another portal, and not the very last tile of the game.
   *
   * @param portalId      The ID of the tile where the portal starts.
   * @param tiles         The total number of tiles on the board.
   * @param occupiedTiles A list of tile IDs that are already occupied by other portals
   *                      (either as starts or ends).
   * @return A randomly selected, valid tile ID for the portal's destination.
   */
  public static int randomPortalDestination(int portalId, int tiles, List<Integer> occupiedTiles) {
    int id;
    do {
      id = random.nextInt(1, tiles);
    } while (id == portalId || occupiedTiles.contains(id) || id == tiles - 1);
    return id;
  }
}
