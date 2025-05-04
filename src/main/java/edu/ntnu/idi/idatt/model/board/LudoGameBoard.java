package edu.ntnu.idi.idatt.model.board;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.ntnu.idi.idatt.model.tile.LudoTile;
import edu.ntnu.idi.idatt.model.tile.Tile;
import static edu.ntnu.idi.idatt.model.validator.ArgumentValidator.ludoGameBoardSetBoardSizeValidator;
import static edu.ntnu.idi.idatt.model.validator.ArgumentValidator.ludoGameBoardSetColorsValidator;
import javafx.scene.paint.Color;

public class LudoGameBoard extends BaseBoard {
  protected int boardSize;
  private Color[] colors;

  public LudoGameBoard(String name, String description, int boardSize, Color[] colors) {
    super(name, description);

    setBoardSize(boardSize);
    setColors(colors);
  }

  public Color[] getColors() {
    return colors;
  }

  public int getBoardSize() {
    return boardSize;
  }

  public void setBoardSize(int boardSize) {
    ludoGameBoardSetBoardSizeValidator(boardSize);
    this.boardSize = boardSize;
    createTiles(boardSize, boardSize);

  }

  public void setColors(Color[] colors) {
    ludoGameBoardSetColorsValidator(colors);
    this.colors = colors;
  }

  @Override
  public void createTiles(int rows, int columns) {
    Map<Integer, Tile> newTiles = new HashMap<>();

    final int totalTileCount = rows * columns;
    final int startAreaSize = (boardSize - 3) / 2;
    final int tilesPerStartArea = startAreaSize * startAreaSize;
    final int finishTrackSize = startAreaSize - 1;
    final int startTilesStartIndex = totalTileCount - (tilesPerStartArea * 4) + 1;
    final int[] startTilesStartIndexes = {startTilesStartIndex, startTilesStartIndex + tilesPerStartArea, startTilesStartIndex + (tilesPerStartArea * 2), startTilesStartIndex + (tilesPerStartArea * 3)};
    final int trackTileCount = 3 + (finishTrackSize * 2);
    final int totalTrackTileCount = trackTileCount * 4; // The track areas are 3 tiles wide with the same length as the start areas
    final int middleTileCount = 3 * 3; // The middle area is 3x3, with 4 finish tiles and 5 "spacing" tiles
    final int middleTilesStartIndex = startTilesStartIndex - middleTileCount;
    final int finishTrackStartIndex = middleTilesStartIndex - (finishTrackSize * 4);
    final int[] finishTrackStartIndexes = {finishTrackStartIndex, finishTrackStartIndex + finishTrackSize, finishTrackStartIndex + (finishTrackSize * 2), finishTrackStartIndex + (finishTrackSize * 3)};

    List<Tile> trackSection1 = createTrackSection(1, totalTrackTileCount, finishTrackStartIndexes, startAreaSize, 1);
    List<Tile> trackSection2 = createTrackSection(1 + (trackTileCount * 1), (trackTileCount * 1), finishTrackStartIndexes, startAreaSize, 2);
    List<Tile> trackSection3 = createTrackSection(1 + (trackTileCount * 2), (trackTileCount * 2), finishTrackStartIndexes, startAreaSize, 3);
    List<Tile> trackSection4 = createTrackSection(1 + (trackTileCount * 3), (trackTileCount * 3), finishTrackStartIndexes, startAreaSize, 4);
    List<Tile> finishSection = createFinishSection(middleTilesStartIndex);

    // For each row in upper start areas
    for (int row = 0; row < startAreaSize; row++) {
      final int rowZeroIndex = row;
      // For each column in start area 1 (upper left)
      for (int column = 0; column < startAreaSize; column++) { 
        int tileId = startTilesStartIndexes[0] + (startAreaSize * row) + column;
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
        int tileId = startTilesStartIndexes[1] + (startAreaSize * row) + columnZeroIndex;
        Tile tile = new LudoTile(tileId, new int[] {row, column}, tileId + 1, "start-2");
        newTiles.put(tileId, tile);
      }
    }

    // For each row between upper and lower start areas
    for (int row = startAreaSize; row < boardSize - startAreaSize; row++) {
      final int rowZeroIndex = row - startAreaSize;
      // For each column in the left track area (between start areas 1 and 3)
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
    
      // For each column in the right track area (between start areas 2 and 4)
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
      // For each column in start area 3 (lower left)
      int rowZeroIndex = row - boardSize + startAreaSize;
      for (int column = 0; column < startAreaSize; column++) { 
        int tileId = startTilesStartIndexes[2] + (startAreaSize * rowZeroIndex) + column;
        Tile tile = new LudoTile(tileId, new int[] {row, column}, tileId + 1, "start-3");
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

      // For each column in start area 4 (lower right)
      for (int column = boardSize - startAreaSize; column < boardSize; column++) { 
        int colZeroIndex = column - boardSize + startAreaSize;
        int tileId = startTilesStartIndexes[3] + (startAreaSize * rowZeroIndex) + colZeroIndex;
        Tile tile = new LudoTile(tileId, new int[] {row, column}, tileId + 1, "start-4");
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
        String type;
        if (row == 0 && column == 0 ) { // The upper left tile
            tileId = endId;
            type = "track";
        } else if (row == 1 && column == 0) { // The middle left tile
            tileId = endId - 1;
            type = "track";
        } else if (row == 2 && column == 0) { // The lower left tile
            tileId = endId - 2;
            type = "track";
        } else if (row == 1) { // The finish-track path
            tileId = finishTrackStartIndexes[startAreaIndex - 1] + column - 1;
            type = "finish-" + startAreaIndex;
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
        Tile tile = new LudoTile(tileId, coordinates, tileId + 1, type);
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
          type = "finish-2";
          nextTileId = 0;
        } else if (tileId - startId == 3) {
          type = "finish-1";
          nextTileId = 0;
        } else if (tileId - startId == 5) {
          type = "finish-3";
          nextTileId = 0;
        } else if (tileId - startId == 7) {
          type = "finish-4";
          nextTileId = 0;
        } else {
          type = "finish";
        }
        int[] coordinates = new int[] {row, column};
        Tile tile = new LudoTile(tileId, coordinates, nextTileId, type);
        tiles.add(tile);
      }
    }
    return tiles;
  }
}
