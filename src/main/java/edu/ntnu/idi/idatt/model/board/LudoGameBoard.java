package edu.ntnu.idi.idatt.model.board;

import static edu.ntnu.idi.idatt.model.validator.ArgumentValidator.ludoGameBoardCreateTilesValidator;
import static edu.ntnu.idi.idatt.model.validator.ArgumentValidator.ludoGameBoardSetBoardSizeValidator;
import static edu.ntnu.idi.idatt.model.validator.ArgumentValidator.ludoGameBoardSetColorsValidator;

import edu.ntnu.idi.idatt.model.tile.LudoTile;
import edu.ntnu.idi.idatt.model.tile.Tile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.scene.paint.Color;

/**
 * <h3>LudoGameBoard.</h3>
 *
 * <p>This class extends {@link BaseBoard} to represent the specific structure and properties of a
 * Ludo game board. It is responsible for creating and organizing the {@link LudoTile}s that form
 * the Ludo tracks, start areas, and finish areas for up to four players.</p>
 *
 * <p>Key features and properties defined by this class include:
 * <ul>
 *   <li>{@code boardSize}: An integer (typically odd, e.g., 9, 11, ..., 21) that determines the
 *       overall dimensions of the board and the length of player tracks and home areas.</li>
 *   <li>Player colors: An array of {@link Color} objects for the four players.</li>
 *   <li>Indexes for critical tile locations for each player:
 *     <ul>
 *       <li>{@code playerStartIndexes}: The starting tile ID for each player's tokens in their home
 *           area.</li>
 *       <li>{@code playerTrackStartIndexes}: The tile ID where each player enters the main track
 *           .</li>
 *       <li>{@code playerFinishStartIndexes}: The tile ID where each player enters their finish
 *           track.</li>
 *       <li>{@code playerFinishIndexes}: The final tile ID for each player in their home stretch
 *           .</li>
 *     </ul>
 *   </li>
 *   <li>Sizes of specific areas: {@code startAreaSize} (dimensions of the square start area) and
 *       {@code totalTrackTileCount} (total number of tiles on the main circular track).</li>
 *   <li>Detailed tile creation logic ({@link #createTiles(int, int)}) that procedurally generates
 *       all {@link LudoTile} instances with appropriate types ("start-N", "track", "track-start-N",
 *       "finish-blank") and {@code nextTileId} connections based on the {@code boardSize}.
 *       This involves complex calculations and rotations to form the characteristic cross shape of
 *       a Ludo board.</li>
 * </ul>
 * </p>
 *
 * @see BaseBoard
 * @see LudoTile
 * @see Color
 */
public class LudoGameBoard extends BaseBoard {

  protected int boardSize;
  private Color[] colors;
  private int[] playerStartIndexes;
  private int[] playerTrackStartIndexes;
  private int[] playerFinishStartIndexes;
  private int[] playerFinishIndexes;
  private int startAreaSize;
  private int totalTrackTileCount;

  /**
   * Constructs a new {@code LudoGameBoard}.
   *
   * @param name        The name of the Ludo board.
   * @param description A description of the Ludo board.
   * @param background  The path to the background image for the board.
   * @param boardSize   The size of the board (e.g., 15), which dictates the track lengths. Must be
   *                    an odd integer between 9 and 21, inclusive.
   * @param colors      An array of {@link Color} objects representing the four player colors. Must
   *                    not be null and must contain exactly 4 non-null colors.
   */
  public LudoGameBoard(String name, String description, String background, int boardSize,
      Color[] colors) {
    super(name, description, background);

    playerStartIndexes = new int[4];
    playerTrackStartIndexes = new int[4];
    playerFinishStartIndexes = new int[4];
    playerFinishIndexes = new int[4];

    setBoardSize(boardSize);
    setColors(colors);
  }

  /**
   * Returns the array of {@link Color}s assigned to the players on this Ludo board.
   *
   * @return The array of player colors.
   */
  public Color[] getColors() {
    return colors;
  }

  /**
   * Returns the size of the Ludo board. This integer typically defines the length of one side of
   * the square playing area or a similar characteristic dimension.
   *
   * @return The board size.
   */
  public int getBoardSize() {
    return boardSize;
  }

  /**
   * Returns an array containing the starting tile IDs for each of the four players' token areas.
   * Index 0 corresponds to player 1, index 1 to player 2, and so on.
   *
   * @return Array of player start tile IDs.
   */
  public int[] getPlayerStartIndexes() {
    return playerStartIndexes;
  }

  /**
   * Returns an array containing the tile IDs where each of the four players' main tracks begin
   * (i.e., where tokens enter the shared playing path from their start areas). Index 0 corresponds
   * to player 1, index 1 to player 2, and so on.
   *
   * @return Array of player track start tile IDs.
   */
  public int[] getPlayerTrackStartIndexes() {
    return playerTrackStartIndexes;
  }

  /**
   * Returns an array containing the tile IDs where each of the four players' finish tracks (home
   * stretches) begin. Index 0 corresponds to player 1, index 1 to player 2, and so on.
   *
   * @return Array of player finish track start tile IDs.
   */
  public int[] getPlayerFinishStartIndexes() {
    return playerFinishStartIndexes;
  }

  /**
   * Returns an array containing the final (goal) tile IDs for each of the four players' finish
   * tracks. Index 0 corresponds to player 1, index 1 to player 2, and so on.
   *
   * @return Array of player finish (goal) tile IDs.
   */
  public int[] getPlayerFinishIndexes() {
    return playerFinishIndexes;
  }

  /**
   * Returns the size (width/height) of one player's square start area. This is derived from the
   * main {@code boardSize}.
   *
   * @return The size of the start area (e.g., if boardSize is 15, startAreaSize might be 6).
   */
  public int getStartAreaSize() {
    return startAreaSize;
  }

  /**
   * Returns the total number of tiles that make up the main shared playing track (the circular path
   * excluding individual finish tracks and start areas).
   *
   * @return The total count of tiles on the main track.
   */
  public int getTotalTrackTileCount() {
    return totalTrackTileCount;
  }

  /**
   * Sets the player colors for this Ludo board. Input is validated to ensure it is not null and
   * contains exactly four non-null colors.
   *
   * @param colors An array of four {@link Color} objects.
   * @throws IllegalArgumentException if the colors array is invalid.
   */
  public void setColors(Color[] colors) {
    ludoGameBoardSetColorsValidator(colors);
    this.colors = colors;
  }

  /**
   * Sets the player start indexes for this Ludo board.
   *
   * @param playerStartIndexes An array of player start tile IDs.
   */
  public void setPlayerStartIndexes(int[] playerStartIndexes) {
    this.playerStartIndexes = playerStartIndexes;
  }

  /**
   * Sets the player track start indexes for this Ludo board.
   *
   * @param playerTrackStartIndexes An array of player track start tile IDs.
   */
  public void setPlayerTrackStartIndexes(int[] playerTrackStartIndexes) {
    this.playerTrackStartIndexes = playerTrackStartIndexes;
  }

  /**
   * Sets the player finish start indexes for this Ludo board.
   *
   * @param playerFinishStartIndexes An array of player finish track start tile IDs.
   */
  public void setPlayerFinishStartIndexes(int[] playerFinishStartIndexes) {
    this.playerFinishStartIndexes = playerFinishStartIndexes;
  }

  /**
   * Sets the player finish indexes for this Ludo board.
   *
   * @param playerFinishIndexes An array of player finish (goal) tile IDs.
   */
  public void setPlayerFinishIndexes(int[] playerFinishIndexes) {
    this.playerFinishIndexes = playerFinishIndexes;
  }

  /**
   * Sets the start area size for this Ludo board.
   *
   * @param startAreaSize The size of the start area.
   */
  public void setStartAreaSize(int startAreaSize) {
    this.startAreaSize = startAreaSize;
  }

  /**
   * Sets the total track tile count for this Ludo board.
   *
   * @param totalTrackTileCount The total count of tiles on the main track.
   */
  public void setTotalTrackTileCount(int totalTrackTileCount) {
    this.totalTrackTileCount = totalTrackTileCount;
  }

  /**
   * Sets the size of the Ludo board. This also triggers the {@link #createTiles(int, int)} method
   * to regenerate the board structure based on the new size. Input is validated to ensure it's an
   * odd integer between 9 and 21.
   *
   * @param boardSize The new board size (e.g., 15).
   * @throws IllegalArgumentException if the board size is invalid.
   */
  public void setBoardSize(int boardSize) {
    ludoGameBoardSetBoardSizeValidator(boardSize);
    this.boardSize = boardSize;
    createTiles(boardSize, boardSize);
  }

  /**
   * Creates all the {@link LudoTile} instances for the Ludo board based on the current
   * {@code boardSize}. This method is responsible for calculating the coordinates, tile IDs, next
   * tile IDs, and types for all tiles, including start areas, the main track, and finish tracks for
   * four players. The process involves:
   * <ol>
   *   <li>Calculating key dimensions like {@code startAreaSize}, {@code tilesPerStartArea},
   *       etc.</li>
   *   <li>Determining start indexes for various sections (player start areas, finish tracks, main
   *       track sections).</li>
   *   <li>Calling helper methods {@link #createFinishSection(int)} and
   *       {@link #createTrackSection(int, int, int[], int, int)} to generate conceptual tile lists
   *       for these parts.</li>
   *   <li>Iterating through a grid representing the board and placing tiles from the generated
   *       sections into their correct (row, column) positions, instantiating them as
   *       {@link LudoTile} objects with appropriate types.</li>
   * </ol>
   * The method ensures that the {@code rows} and {@code columns} arguments match the current
   * {@code boardSize}.
   *
   * @param rows    The number of rows for the board, must match {@code boardSize}.
   * @param columns The number of columns for the board, must match {@code boardSize}.
   * @throws IllegalArgumentException if {@code rows} or {@code columns} do not match
   *                                  {@code boardSize}.
   */
  @Override
  public void createTiles(int rows, int columns) {
    ludoGameBoardCreateTilesValidator(rows, columns);
    final Map<Integer, Tile> newTiles = new HashMap<>();

    this.startAreaSize = (boardSize - 3) / 2;

    final int tilesPerStartArea = startAreaSize * startAreaSize;
    final int startTilesStartIndex = (rows * columns) - (tilesPerStartArea * 4) + 1;
    this.playerStartIndexes = new int[]{startTilesStartIndex,
        startTilesStartIndex + tilesPerStartArea, startTilesStartIndex + (tilesPerStartArea * 2),
        startTilesStartIndex + (tilesPerStartArea * 3)};

    final int finishTrackSize = startAreaSize - 1;
    final int trackTileCount = 3 + (finishTrackSize * 2);
    this.totalTrackTileCount = trackTileCount * 4;
    final int middleTilesStartIndex =
        startTilesStartIndex - (3 * 3); // (The middle area is always 3x3)
    final int finishTrackStartIndex = middleTilesStartIndex - (finishTrackSize * 4);
    this.playerFinishStartIndexes = new int[]{finishTrackStartIndex,
        finishTrackStartIndex + finishTrackSize, finishTrackStartIndex + (finishTrackSize * 2),
        finishTrackStartIndex + (finishTrackSize * 3)};
    this.playerTrackStartIndexes = new int[]{1, 1 + (trackTileCount * 1), 1 + (trackTileCount * 2),
        1 + (trackTileCount * 3)};

    final List<Tile> finishSection = createFinishSection(middleTilesStartIndex);
    final List<Tile> trackSection1 = createTrackSection(playerTrackStartIndexes[0],
        totalTrackTileCount, playerFinishStartIndexes, startAreaSize, 1);
    final List<Tile> trackSection2 = createTrackSection(playerTrackStartIndexes[1],
        (trackTileCount * 1), playerFinishStartIndexes, startAreaSize, 2);
    final List<Tile> trackSection3 = createTrackSection(playerTrackStartIndexes[2],
        (trackTileCount * 2), playerFinishStartIndexes, startAreaSize, 3);
    final List<Tile> trackSection4 = createTrackSection(playerTrackStartIndexes[3],
        (trackTileCount * 3), playerFinishStartIndexes, startAreaSize, 4);

    // For each row in upper start areas
    for (int row = 0; row < startAreaSize; row++) {
      final int rowZeroIndex = row;
      // For each column in start area 1 (upper left)
      for (int column = 0; column < startAreaSize; column++) {
        int tileId = playerStartIndexes[0] + (startAreaSize * row) + column;
        Tile tile = new LudoTile(tileId, new int[]{row, column}, tileId + 1, "start-1");
        newTiles.put(tileId, tile);
      }

      // For each column in the top center track area
      for (int column = startAreaSize; column < boardSize - startAreaSize; column++) {
        final int columnZeroIndex = column - startAreaSize;
        Tile tile = trackSection2.stream().filter(
                t -> t.getCoordinates()[0] == rowZeroIndex
                    && t.getCoordinates()[1] == columnZeroIndex)
            .findFirst().orElse(null);
        newTiles.put(tile.getTileId(),
            new LudoTile(tile.getTileId(), new int[]{row, column}, tile.getNextTileId(),
                ((LudoTile) tile).getType()));
      }

      // For each column in start area 2 (upper right)
      for (int column = boardSize - startAreaSize; column < boardSize; column++) {
        final int columnZeroIndex = column - boardSize + startAreaSize;
        int tileId = playerStartIndexes[1] + (startAreaSize * row) + columnZeroIndex;
        Tile tile = new LudoTile(tileId, new int[]{row, column}, tileId + 1, "start-2");
        newTiles.put(tileId, tile);
      }
    }

    // For each row between upper and lower start areas
    for (int row = startAreaSize; row < boardSize - startAreaSize; row++) {
      final int rowZeroIndex = row - startAreaSize;
      // For each column in the left track area (between start areas 1 and 4)
      for (int column = 0; column < startAreaSize; column++) {
        final int columnZeroIndex = column;
        Tile tile = trackSection1.stream().filter(
                t -> t.getCoordinates()[0] == rowZeroIndex
                    && t.getCoordinates()[1] == columnZeroIndex)
            .findFirst().orElseThrow(() -> new IllegalStateException(
                "No tile found at coordinates [" + rowZeroIndex + ", " + columnZeroIndex + "]"));
        newTiles.put(tile.getTileId(),
            new LudoTile(tile.getTileId(), new int[]{row, column}, tile.getNextTileId(),
                ((LudoTile) tile).getType()));
      }
      // For each column in middle finish area (center of the board)
      for (int column = startAreaSize; column < boardSize - startAreaSize; column++) {
        final int columnZeroIndex = column - startAreaSize;
        Tile tile = finishSection.stream().filter(
                t -> t.getCoordinates()[0] == rowZeroIndex
                    && t.getCoordinates()[1] == columnZeroIndex)
            .findFirst().orElseThrow(() -> new IllegalStateException(
                "No tile found at coordinates [" + rowZeroIndex + ", " + columnZeroIndex + "]"));
        newTiles.put(tile.getTileId(),
            new LudoTile(tile.getTileId(), new int[]{row, column}, tile.getNextTileId(),
                ((LudoTile) tile).getType()));
      }

      // For each column in the right track area (between start areas 2 and 3)
      for (int column = boardSize - startAreaSize; column < boardSize; column++) {
        final int columnZeroIndex = column - boardSize + startAreaSize;
        Tile tile = trackSection3.stream().filter(
                t -> t.getCoordinates()[0] == rowZeroIndex
                    && t.getCoordinates()[1] == columnZeroIndex)
            .findFirst().orElseThrow(() -> new IllegalStateException(
                "No tile found at coordinates [" + rowZeroIndex + ", " + columnZeroIndex + "]"));
        newTiles.put(tile.getTileId(),
            new LudoTile(tile.getTileId(), new int[]{row, column}, tile.getNextTileId(),
                ((LudoTile) tile).getType()));
      }
    }

    // For each row in lower start areas
    for (int row = boardSize - startAreaSize; row < boardSize; row++) {
      // For each column in start area 4 (lower left)
      int rowZeroIndex = row - boardSize + startAreaSize;
      for (int column = 0; column < startAreaSize; column++) {
        int tileId = playerStartIndexes[3] + (startAreaSize * rowZeroIndex) + column;
        Tile tile = new LudoTile(tileId, new int[]{row, column}, tileId + 1, "start-4");
        newTiles.put(tileId, tile);
      }

      // For each column in the bottom center track area
      for (int column = startAreaSize; column < boardSize - startAreaSize; column++) {
        final int columnZeroIndex = column - startAreaSize;
        Tile tile = trackSection4.stream().filter(
                t -> t.getCoordinates()[0] == rowZeroIndex
                    && t.getCoordinates()[1] == columnZeroIndex)
            .findFirst().orElseThrow(() -> new IllegalStateException(
                "No tile found at coordinates [" + rowZeroIndex + ", " + columnZeroIndex + "]"));
        newTiles.put(tile.getTileId(),
            new LudoTile(tile.getTileId(), new int[]{row, column}, tile.getNextTileId(),
                ((LudoTile) tile).getType()));
      }

      // For each column in start area 3 (lower right)
      for (int column = boardSize - startAreaSize; column < boardSize; column++) {
        int colZeroIndex = column - boardSize + startAreaSize;
        int tileId = playerStartIndexes[2] + (startAreaSize * rowZeroIndex) + colZeroIndex;
        Tile tile = new LudoTile(tileId, new int[]{row, column}, tileId + 1, "start-3");
        newTiles.put(tileId, tile);
      }
    }

    this.tiles = newTiles;
  }

  /**
   * Creates a conceptual list of {@link Tile} objects representing one of the four main track
   * sections of the Ludo board, including its associated finish track entrance. The tiles are
   * initially created as if for the first player's section and then mathematically rotated based on
   * {@code startAreaIndex} to position them correctly for other players.
   *
   * @param startId                 The starting tile ID for this track section on the main path.
   * @param endId                   The ending tile ID for this track section (before it loops or
   *                                enters a finish track).
   * @param finishTrackStartIndexes An array of starting tile IDs for each player's finish track.
   * @param startAreaSize           The size of a player's start area, used to determine finish
   *                                track length.
   * @param startAreaIndex          The index of the player/start area (1-4) this track section is
   *                                for.
   * @return A list of {@link Tile} objects for the (potentially rotated) track section.
   * @throws IllegalArgumentException if {@code startAreaIndex} is not between 1 and 4.
   */
  private List<Tile> createTrackSection(int startId, int endId, int[] finishTrackStartIndexes,
      int startAreaSize, int startAreaIndex) {
    List<Tile> tmpTiles = new ArrayList<>();
    for (int row = 0; row < 3; row++) {
      for (int column = 0; column < startAreaSize; column++) {
        int tileId;
        int nextTileId = 0;
        String type;
        if (row == 0 && column == 0) { // The upper left tile
          tileId = endId;
          type = "track";
          nextTileId = startId;
        } else if (row == 1 && column == 0) { // The middle left tile
          tileId = endId - 1;
          type = "track";
        } else if (row == 2 && column == 0) { // The lower left tile
          tileId = endId - 2;
          type = "track";
        } else if (row == 1) { // The finish-track path
          tileId = finishTrackStartIndexes[startAreaIndex - 1] + column - 1;
          type = "finish-" + startAreaIndex;
          nextTileId = column == startAreaSize - 1 ? playerFinishIndexes[startAreaIndex - 1] : 0;
        } else if (row == 2) { // The lower path
          tileId = endId - 2 - column;
          type = "track";
        } else if (row == 0 && column == 1) { // The player-start tile for the section
          tileId = startId;
          type = "track-start-" + startAreaIndex;
        } else { // The upper path 
          tileId = startId + column - 1;
          type = "track";
        }
        int[] coordinates = new int[]{row, column};
        nextTileId = nextTileId == 0 ? tileId + 1 : nextTileId;
        Tile tile = new LudoTile(tileId, coordinates, nextTileId, type);
        tmpTiles.add(tile);
      }
    }
    List<Tile> rotatedTiles = new ArrayList<>();
    switch (startAreaIndex) {
      case 1 -> tmpTiles.forEach(tile -> rotatedTiles.add(tile));
      case 2 -> {
        // Rotate "coordinates" 90 degrees clockwise, so that the first row becomes the last column
        for (Tile tile : tmpTiles) {
          int[] coordinates = tile.getCoordinates();
          rotatedTiles.add(
              new LudoTile(tile.getTileId(), rotateCoordinates(coordinates, 3, startAreaSize),
                  tile.getNextTileId(), ((LudoTile) tile).getType()));
        }
      }
      case 3 -> {
        // Rotate "coordinates" 180 degrees, so that the first row becomes the last row and the
        // first column becomes the last column
        for (Tile tile : tmpTiles) {
          int[] coordinates = tile.getCoordinates();
          rotatedTiles.add(new LudoTile(tile.getTileId(),
              rotateCoordinates(rotateCoordinates(coordinates, 3, startAreaSize), startAreaSize,
                  3),
              tile.getNextTileId(), ((LudoTile) tile).getType()));
        }
      }
      case 4 -> {
        // Rotate "coordinates" 270 degrees clockwise (or 90 degrees counter-clockwise), so that
        // the first row becomes the first column and the last column becomes the last row
        for (Tile tile : tmpTiles) {
          int[] coordinates = tile.getCoordinates();
          rotatedTiles.add(new LudoTile(tile.getTileId(), rotateCoordinates(
              rotateCoordinates(rotateCoordinates(coordinates, 3, startAreaSize), startAreaSize,
                  3),
              3, startAreaSize), tile.getNextTileId(), ((LudoTile) tile).getType()));
        }
      }
      default ->
          throw new IllegalArgumentException("Invalid start area index. Must be between 1 and 4.");
    }

    return rotatedTiles;
  }

  /**
   * Rotates a given set of 2D coordinates by 90 degrees clockwise within a bounding box defined by
   * {@code rows} and {@code columns}. This is a utility method used during Ludo board tile
   * generation.
   *
   * @param coordinates The original {@code [row, column]} coordinates.
   * @param rows        The number of rows in the bounding box for rotation.
   * @param columns     The number of columns in the bounding box for rotation (not directly used in
   *                    this specific 90-degree rotation formula but implies context).
   * @return A new array {@code [newRow, newColumn]} with the rotated coordinates.
   */
  private int[] rotateCoordinates(int[] coordinates, int rows, int columns) {
    return new int[]{coordinates[1], (rows - 1) - coordinates[0]};
  }

  /**
   * Creates a list of {@link Tile} objects representing the central 3x3 finish area of the Ludo
   * board. This area contains the final goal tiles for each of the four players, as well as blank
   * interstitial tiles. It sets the {@code playerFinishIndexes} for each player based on their goal
   * tile ID within this section.
   *
   * @param startId The starting tile ID for the first tile in this 3x3 finish section.
   * @return A list of {@link Tile} objects for the central finish area.
   */
  private List<Tile> createFinishSection(int startId) {
    List<Tile> tiles = new ArrayList<>();
    int[] blankTiles = {startId, startId + 2, startId + 4, startId + 6, startId + 8};
    for (int row = 0; row < 3; row++) {
      for (int column = 0; column < 3; column++) {
        int tileId = startId + (row * 3) + column;
        int nextTileId = tileId + 1;
        String type;
        if (Arrays.asList(blankTiles).contains(tileId)) {
          type = "finish-blank";
        } else if (tileId - startId == 1) {
          playerFinishIndexes[1] = tileId;
          type = "finish-2";
          nextTileId = 0;
        } else if (tileId - startId == 3) {
          playerFinishIndexes[0] = tileId;
          type = "finish-1";
          nextTileId = 0;
        } else if (tileId - startId == 5) {
          playerFinishIndexes[2] = tileId;
          type = "finish-3";
          nextTileId = 0;
        } else if (tileId - startId == 7) {
          playerFinishIndexes[3] = tileId;
          type = "finish-4";
          nextTileId = 0;
        } else {
          type = "error";
        }
        int[] coordinates = new int[]{row, column};
        Tile tile = new LudoTile(tileId, coordinates, nextTileId, type);
        tiles.add(tile);
      }
    }
    return tiles;
  }
}
