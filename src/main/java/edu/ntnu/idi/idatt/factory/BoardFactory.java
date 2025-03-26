package edu.ntnu.idi.idatt.factory;

import edu.ntnu.idi.idatt.model.Board;
import edu.ntnu.idi.idatt.model.LadderAction;
import edu.ntnu.idi.idatt.model.Tile;
import edu.ntnu.idi.idatt.utils.BoardFileHandlerGson;
import edu.ntnu.idi.idatt.utils.interfaces.FileHandler;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <h3>Factory class for creating Board objects.</h3>
 *
 * <p>This class provides methods for creating Board objects based on predefined variants.
 * Board objects can be created from an external file, or from scratch.
 */
public class BoardFactory {

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

  /**
   * Creates a classic board layout.
   *
   * @return A classic Board object.
   */
  private static Board createClassicBoard() {
    Board board = new Board();
    createTiles(9, 10).forEach(board::addTile);

    Map<Integer, Integer> ladderMap = new HashMap<>();
    ladderMap.put(5, 44);
    ladderMap.put(39, 58);
    ladderMap.put(46, 87);

    Map<Integer, Integer> slideMap = new HashMap<>();
    slideMap.put(34, 13);
    slideMap.put(82, 78);
    slideMap.put(89, 68);

    for (Map.Entry<Integer, Integer> entry : ladderMap.entrySet()) {
      board.getTile(entry.getKey()).setLandAction(new LadderAction(entry.getValue(),
          "Ladder from " + entry.getKey() + " to " + entry.getValue()));
    }
    for (Map.Entry<Integer, Integer> entry : slideMap.entrySet()) {
      board.getTile(entry.getKey()).setLandAction(new LadderAction(entry.getValue(),
          "Slide from " + entry.getKey() + " to " + entry.getValue()));
    }
    return board;
  }

  /**
   * Creates a board layout with portals.
   *
   * @return A Board object with portals.
   */
  private static Board createPortalBoard() {
    Board board = new Board();
    createTiles(10, 10).forEach(board::addTile);

    Map<Integer, Integer> portalMap = new HashMap<>();
    portalMap.put(6, 64);
    portalMap.put(11, 31);
    portalMap.put(22, 50);
    portalMap.put(40, 90);
    portalMap.put(13, 2);
    portalMap.put(34, 5);

    for (Map.Entry<Integer, Integer> entry : portalMap.entrySet()) {
      board.getTile(entry.getKey()).setLandAction(new LadderAction(entry.getValue(),
          "Portal from " + entry.getKey() + " to " + entry.getValue()));
    }
    return board;
  }

  /**
   * Creates a list of Tile objects that are arranged in a grid pattern with alternating directions
   * in each row. The first tile is in the lower left corner of the grid, and the direction of the
   * first row is left to right.
   *
   * @param rows The number of rows in the grid.
   * @param columns The number of columns in the grid.
   * @return The list of Tile objects.
   */
  private static List<Tile> createTiles(int rows, int columns) {
    List<Tile> tiles = new ArrayList<>();
    tiles.add(new Tile(0, 1));

    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < columns; j++) {
        int baseId = i * columns;
        int tileId;
        // Even numbered rows (left to right)
        if (i % 2 == 0) {
          tileId = baseId + j + 1;
        } else { // Odd numbered rows (right to left)
          tileId = baseId + (columns - j);
        }
        tiles.add(new Tile(tileId, tileId + 1));
      }
    }
    return tiles;
  }
}