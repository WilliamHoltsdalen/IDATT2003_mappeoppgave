package edu.ntnu.idi.idatt.model.board;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.ntnu.idi.idatt.model.tile.LudoTile;
import edu.ntnu.idi.idatt.model.tile.Tile;
import static edu.ntnu.idi.idatt.model.validator.ArgumentValidator.ludoGameBoardCreateTilesValidator;
import static edu.ntnu.idi.idatt.model.validator.ArgumentValidator.ludoGameBoardSetBoardSizeValidator;
import static edu.ntnu.idi.idatt.model.validator.ArgumentValidator.ludoGameBoardSetColorsValidator;
import javafx.scene.paint.Color;

public class LudoGameBoard extends BaseBoard {
  protected int boardSize;
  private Color[] colors;
  private int[] playerStartIndexes;
  private int[] playerTrackStartIndexes;
  private int[] playerFinishStartIndexes;
  private int[] playerFinishIndexes;
  private int startAreaSize;
  private int totalTrackTileCount;

  public LudoGameBoard(String name, String description, String background, int boardSize, Color[] colors) {
    super(name, description, background);

    playerStartIndexes = new int[4];
    playerTrackStartIndexes = new int[4];
    playerFinishStartIndexes = new int[4];
    playerFinishIndexes = new int[4];
    
    setBoardSize(boardSize);
    setColors(colors);
  }

  public Color[] getColors() {
    return colors;
  }

  public int getBoardSize() {
    return boardSize;
  }

  public int[] getPlayerStartIndexes() {
    return playerStartIndexes;
  }

  public int[] getPlayerTrackStartIndexes() {
    return playerTrackStartIndexes;
  }

  public int[] getPlayerFinishStartIndexes() {
    return playerFinishStartIndexes;
  }

  public int[] getPlayerFinishIndexes() {
    return playerFinishIndexes;
  }

  public int getStartAreaSize() {
    return startAreaSize;
  }

  public int getTotalTrackTileCount() {
    return totalTrackTileCount;
  }

  public void setColors(Color[] colors) {
    ludoGameBoardSetColorsValidator(colors);
    this.colors = colors;
  }

  public void setBoardSize(int boardSize) {
    ludoGameBoardSetBoardSizeValidator(boardSize);
    this.boardSize = boardSize;
    createTiles(boardSize, boardSize);
  }

  @Override
  public void createTiles(int rows, int columns) {
    ludoGameBoardCreateTilesValidator(rows, columns);
    Map<Integer, Tile> newTiles = new HashMap<>();

    this.startAreaSize = (boardSize - 3) / 2;
    
    final int tilesPerStartArea = startAreaSize * startAreaSize;
    final int startTilesStartIndex = (rows * columns) - (tilesPerStartArea * 4) + 1;
    this.playerStartIndexes = new int[] {startTilesStartIndex, startTilesStartIndex + tilesPerStartArea, startTilesStartIndex + (tilesPerStartArea * 2), startTilesStartIndex + (tilesPerStartArea * 3)};
    
    final int finishTrackSize = startAreaSize - 1;
    final int trackTileCount = 3 + (finishTrackSize * 2);
    this.totalTrackTileCount = trackTileCount * 4;
    final int middleTilesStartIndex = startTilesStartIndex - (3 * 3); // (The middle area is always 3x3)
    final int finishTrackStartIndex = middleTilesStartIndex - (finishTrackSize * 4);
    this.playerFinishStartIndexes = new int[] {finishTrackStartIndex, finishTrackStartIndex + finishTrackSize, finishTrackStartIndex + (finishTrackSize * 2), finishTrackStartIndex + (finishTrackSize * 3)};
    this.playerTrackStartIndexes = new int[]{1, 1 + (trackTileCount * 1), 1 + (trackTileCount * 2), 1 + (trackTileCount * 3)}; 

    final List<Tile> finishSection = createFinishSection(middleTilesStartIndex);
    final List<Tile> trackSection1 = createTrackSection(playerTrackStartIndexes[0], totalTrackTileCount, playerFinishStartIndexes, startAreaSize, 1);
    final List<Tile> trackSection2 = createTrackSection(playerTrackStartIndexes[1], (trackTileCount * 1), playerFinishStartIndexes, startAreaSize, 2);
    final List<Tile> trackSection3 = createTrackSection(playerTrackStartIndexes[2], (trackTileCount * 2), playerFinishStartIndexes, startAreaSize, 3);
    final List<Tile> trackSection4 = createTrackSection(playerTrackStartIndexes[3], (trackTileCount * 3), playerFinishStartIndexes, startAreaSize, 4);

    // For each row in upper start areas
    for (int row = 0; row < startAreaSize; row++) {
      final int rowZeroIndex = row;
      // For each column in start area 1 (upper left)
      for (int column = 0; column < startAreaSize; column++) { 
        int tileId = playerStartIndexes[0] + (startAreaSize * row) + column;
        Tile tile = new LudoTile(tileId, new int[] {row, column}, tileId + 1, "start-1");
        newTiles.put(tileId, tile);
      }

      // For each column in the top center track area
      for (int column = startAreaSize; column < boardSize - startAreaSize; column++) {
        final int columnZeroIndex = column - startAreaSize;
        Tile tile = trackSection2.stream().filter(t -> t.getCoordinates()[0] == rowZeroIndex && t.getCoordinates()[1] == columnZeroIndex).findFirst().orElse(null);
        newTiles.put(tile.getTileId(), new LudoTile(tile.getTileId(), new int[] {row, column}, tile.getNextTileId(), ((LudoTile)tile).getType()));
      }

      // For each column in start area 2 (upper right)
      for (int column = boardSize - startAreaSize; column < boardSize; column++) { 
        final int columnZeroIndex = column - boardSize + startAreaSize;
        int tileId = playerStartIndexes[1] + (startAreaSize * row) + columnZeroIndex;
        Tile tile = new LudoTile(tileId, new int[] {row, column}, tileId + 1, "start-2");
        newTiles.put(tileId, tile);
      }
    }

    // For each row between upper and lower start areas
    for (int row = startAreaSize; row < boardSize - startAreaSize; row++) {
      final int rowZeroIndex = row - startAreaSize;
      // For each column in the left track area (between start areas 1 and 4)
      for (int column = 0; column < startAreaSize; column++) {
        final int columnZeroIndex = column;
        Tile tile = trackSection1.stream()
            .filter(t -> t.getCoordinates()[0] == rowZeroIndex && t.getCoordinates()[1] == columnZeroIndex)
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("No tile found at coordinates [" + rowZeroIndex + ", " + columnZeroIndex + "]"));
        newTiles.put(tile.getTileId(), new LudoTile(tile.getTileId(), new int[] {row, column}, tile.getNextTileId(), ((LudoTile)tile).getType()));
      }
      // For each column in middle finish area (center of the board)
      for (int column = startAreaSize; column < boardSize - startAreaSize; column++) {
        final int columnZeroIndex = column - startAreaSize;
        Tile tile = finishSection.stream()
            .filter(t -> t.getCoordinates()[0] == rowZeroIndex && t.getCoordinates()[1] == columnZeroIndex)
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("No tile found at coordinates [" + rowZeroIndex + ", " + columnZeroIndex + "]"));
        newTiles.put(tile.getTileId(), new LudoTile(tile.getTileId(), new int[] {row, column}, tile.getNextTileId(), ((LudoTile)tile).getType()));
      }
    
      // For each column in the right track area (between start areas 2 and 3)
      for (int column = boardSize - startAreaSize; column < boardSize; column++) { 
        final int columnZeroIndex = column - boardSize + startAreaSize;
        Tile tile = trackSection3.stream()
            .filter(t -> t.getCoordinates()[0] == rowZeroIndex && t.getCoordinates()[1] == columnZeroIndex)
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("No tile found at coordinates [" + rowZeroIndex + ", " + columnZeroIndex + "]"));
        newTiles.put(tile.getTileId(), new LudoTile(tile.getTileId(), new int[] {row, column}, tile.getNextTileId(), ((LudoTile)tile).getType()));
      }
    }

    // For each row in lower start areas
    for (int row = boardSize - startAreaSize; row < boardSize; row++) {
      // For each column in start area 4 (lower left)
      int rowZeroIndex = row - boardSize + startAreaSize;
      for (int column = 0; column < startAreaSize; column++) { 
        int tileId = playerStartIndexes[3] + (startAreaSize * rowZeroIndex) + column;
        Tile tile = new LudoTile(tileId, new int[] {row, column}, tileId + 1, "start-4");
        newTiles.put(tileId, tile);
      }

      // For each column in the bottom center track area
      for (int column = startAreaSize; column < boardSize - startAreaSize; column++) {
        final int columnZeroIndex = column - startAreaSize;
        Tile tile = trackSection4.stream()
            .filter(t -> t.getCoordinates()[0] == rowZeroIndex && t.getCoordinates()[1] == columnZeroIndex)
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("No tile found at coordinates [" + rowZeroIndex + ", " + columnZeroIndex + "]"));
        newTiles.put(tile.getTileId(), new LudoTile(tile.getTileId(), new int[] {row, column}, tile.getNextTileId(), ((LudoTile)tile).getType()));
      }

      // For each column in start area 3 (lower right)
      for (int column = boardSize - startAreaSize; column < boardSize; column++) { 
        int colZeroIndex = column - boardSize + startAreaSize;
        int tileId = playerStartIndexes[2] + (startAreaSize * rowZeroIndex) + colZeroIndex;
        Tile tile = new LudoTile(tileId, new int[] {row, column}, tileId + 1, "start-3");
        newTiles.put(tileId, tile);
      }
    }

    this.tiles = newTiles;
  }

  private List<Tile> createTrackSection(int startId, int endId, int[] finishTrackStartIndexes, int startAreaSize, int startAreaIndex) {
    List<Tile> tmpTiles = new ArrayList<>();
    for (int row = 0; row < 3; row++) {
      for (int column = 0; column < startAreaSize; column++) { 
        int tileId;
        int nextTileId = 0;
        String type;
        if (row == 0 && column == 0 ) { // The upper left tile
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
        int[] coordinates = new int[] {row, column};
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
              rotatedTiles.add(new LudoTile(tile.getTileId(), rotateCoordinates(coordinates, 3, startAreaSize), tile.getNextTileId(), ((LudoTile)tile).getType()));
          } 
        }
      case 3 -> {
          // Rotate "coordinates" 180 degrees, so that the first row becomes the last row and the first column becomes the last column
          for (Tile tile : tmpTiles) {
              int[] coordinates = tile.getCoordinates();
              rotatedTiles.add(new LudoTile(tile.getTileId(), rotateCoordinates(rotateCoordinates(coordinates, 3, startAreaSize), startAreaSize, 3), tile.getNextTileId(), ((LudoTile)tile).getType()));
          } 
        }
      case 4 -> {
          // Rotate "coordinates" 270 degrees clockwise (or 90 degrees counter-clockwise), so that the first row becomes the first column and the last column becomes the last row
          for (Tile tile : tmpTiles) {
              int[] coordinates = tile.getCoordinates();
              rotatedTiles.add(new LudoTile(tile.getTileId(), rotateCoordinates(rotateCoordinates(rotateCoordinates(coordinates, 3, startAreaSize), startAreaSize, 3), 3, startAreaSize), tile.getNextTileId(), ((LudoTile)tile).getType()));
          } 
        }
      default -> throw new IllegalArgumentException("Invalid start area index. Must be between 1 and 4.");
    }

    return rotatedTiles;
  }

  private int[] rotateCoordinates(int[] coordinates, int rows, int columns) {
    return new int[] {coordinates[1], (rows - 1) -coordinates[0]};
  }

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
        int[] coordinates = new int[] {row, column};
        Tile tile = new LudoTile(tileId, coordinates, nextTileId, type);
        tiles.add(tile);
      }
    }
    return tiles;
  }
}
