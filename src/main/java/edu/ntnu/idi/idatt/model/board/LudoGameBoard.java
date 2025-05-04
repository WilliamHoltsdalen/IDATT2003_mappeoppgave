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
    
    final int trackTileCount = (startAreaSize * 3) * 4; // The track areas are 3 tiles wide with the same length as the start areas
    
    final int middleTileCount = 3 * 3; // The middle area is 3x3, with 4 finish tiles and 5 "spacing" tiles
    final int middleTilesStartIndex = totalTileCount - (tilesPerStartArea * 4) - middleTileCount;

    final int[] finishTrackStartIndexes = {middleTilesStartIndex - ((startAreaSize - 1) * 4), middleTilesStartIndex - ((startAreaSize - 1) * 3), middleTilesStartIndex - ((startAreaSize - 1) * 2), middleTilesStartIndex - ((startAreaSize - 1) * 1)};

    List<Tile> trackSection1 = createTrackSection(1, trackTileCount, finishTrackStartIndexes[0], startAreaSize, 1);
    List<Tile> trackSection2 = createTrackSection(14, 13, finishTrackStartIndexes[1], startAreaSize, 2);
    List<Tile> trackSection3 = createTrackSection(27, 26, finishTrackStartIndexes[2], startAreaSize, 3);
    List<Tile> trackSection4 = createTrackSection(40, 39, finishTrackStartIndexes[3], startAreaSize, 4);
    List<Tile> finishSection = createFinishSection(middleTilesStartIndex);

    // For each row in upper start areas
    for (int row = 0; row < startAreaSize; row++) {
      // For each column in start area 1 (upper left)
      for (int column = 0; column < startAreaSize; column++) { 
        int tileId = (totalTileCount - (tilesPerStartArea * 4)) + (startAreaSize * row) + column;
        Tile tile = new LudoTile(tileId, new int[] {row, column}, tileId + 1, "start-1");
        newTiles.put(tileId, tile);
      }

      // For each column in the top center track area
      for (int column = startAreaSize; column < boardSize - startAreaSize; column++) {
        final int rowZeroIndex = row;
        final int columnZeroIndex = column;
        Tile tile = trackSection2.stream().filter(t -> t.getCoordinates()[0] == rowZeroIndex && t.getCoordinates()[1] == columnZeroIndex).findFirst().orElse(null);
        if (tile != null) {
          newTiles.put(tile.getTileId(), new LudoTile(tile.getTileId(), new int[] {row, column}, tile.getNextTileId(), ((LudoTile)tile).getType()));
        }
      }

      // For each column in start area 2 (upper right)
      for (int column = boardSize - startAreaSize; column < boardSize; column++) { 
        int tileId = (totalTileCount - (tilesPerStartArea * 3)) + (startAreaSize * row) + column;
        Tile tile = new LudoTile(tileId, new int[] {row, column}, tileId + 1, "start-2");
        newTiles.put(tileId, tile);
      }
    }

    // For each row between upper and lower start areas
    for (int row = startAreaSize; row < boardSize - startAreaSize; row++) {
      // For each column in the left track area (between start areas 1 and 3)
      for (int column = 0; column < startAreaSize; column++) {
        final int rowZeroIndex = row - startAreaSize;
        final int columnZeroIndex = column;
        Tile tile = trackSection1.stream()
            .filter(t -> t.getCoordinates()[0] == rowZeroIndex && t.getCoordinates()[1] == columnZeroIndex)
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("No tile found at coordinates [" + rowZeroIndex + ", " + columnZeroIndex + "]"));
        newTiles.put(tile.getTileId(), new LudoTile(tile.getTileId(), new int[] {row, column}, tile.getNextTileId(), ((LudoTile)tile).getType()));
      }
      // For each column in middle finish area (center of the board)
      for (int column = startAreaSize; column < boardSize - startAreaSize; column++) {
        final int rowZeroIndex = row - startAreaSize;
        final int columnZeroIndex = column - startAreaSize;
        Tile tile = finishSection.stream()
            .filter(t -> t.getCoordinates()[0] == rowZeroIndex && t.getCoordinates()[1] == columnZeroIndex)
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("No tile found at coordinates [" + rowZeroIndex + ", " + columnZeroIndex + "]"));
        newTiles.put(tile.getTileId(), new LudoTile(tile.getTileId(), new int[] {row, column}, tile.getNextTileId(), ((LudoTile)tile).getType()));
      }
    
      // For each column in the right track area (between start areas 2 and 4)
      for (int column = boardSize - startAreaSize; column < boardSize; column++) { 
        final int rowZeroIndex = row - startAreaSize;
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
      for (int column = 0; column < startAreaSize; column++) { 
        int tileId = (totalTileCount - (tilesPerStartArea * 2)) + (startAreaSize * row) + column;
        Tile tile = new LudoTile(tileId, new int[] {row, column}, tileId + 1, "start-3");
        newTiles.put(tileId, tile);
      }

      // For each column in the bottom center track area
      for (int column = startAreaSize; column < boardSize - startAreaSize; column++) {
        final int rowZeroIndex = row - boardSize + startAreaSize;
        final int columnZeroIndex = column - startAreaSize;
        Tile tile = trackSection4.stream()
            .filter(t -> t.getCoordinates()[0] == rowZeroIndex && t.getCoordinates()[1] == columnZeroIndex)
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("No tile found at coordinates [" + rowZeroIndex + ", " + columnZeroIndex + "]"));
        newTiles.put(tile.getTileId(), new LudoTile(tile.getTileId(), new int[] {row, column}, tile.getNextTileId(), ((LudoTile)tile).getType()));
      }

      // For each column in start area 4 (lower right)
      for (int column = boardSize - startAreaSize; column < boardSize; column++) { 
        int tileId = (totalTileCount - (tilesPerStartArea * 1)) + (startAreaSize * row) + column;
        Tile tile = new LudoTile(tileId, new int[] {row, column}, tileId + 1, "start-4");
        newTiles.put(tileId, tile);
      }
    }

    this.tiles = newTiles;
  }

  private List<Tile> createTrackSection(int startId, int endId, int finishTrackStartIndex, int startAreaSize, int startAreaIndex) {
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
            tileId = finishTrackStartIndex + (startAreaIndex * (startAreaSize - 1)) + column;
            type = "finish-" + startAreaIndex;
        } else if (row == 2) { // The lower path 
            tileId = endId - 2 - column;
            type = "track";
        } else if (row == 0 && column == 1) { // The player-start tile for the section
          tileId = startId;
          type = "start-" + startAreaIndex;
        } else { // The upper path 
            tileId = startId + column - 1;
            type = "track";
        }
        int[] coordinates = new int[] {row, column};
        System.out.println("Creating tile with ID: " + tileId + ", coordinates: [" + coordinates[0] + ", " + coordinates[1] + "]");
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
              rotatedTiles.add(new LudoTile(tile.getTileId(), new int[] {coordinates[1], startAreaSize - 1 - coordinates[0]}, tile.getNextTileId(), ((LudoTile)tile).getType()));
          } }
      case 3 -> {
          // Rotate "coordinates" 180 degrees, so that the first row becomes the last row and the first column becomes the last column
          for (Tile tile : tmpTiles) {
              int[] coordinates = tile.getCoordinates();
              rotatedTiles.add(new LudoTile(tile.getTileId(), new int[] {2 - coordinates[0], startAreaSize - 1 - coordinates[1]}, tile.getNextTileId(), ((LudoTile)tile).getType()));
          } }
      case 4 -> {
          // Rotate "coordinates" 270 degrees clockwise (or 90 degrees counter-clockwise), so that the first row becomes the first column and the last column becomes the last row
          for (Tile tile : tmpTiles) {
              int[] coordinates = tile.getCoordinates();
              rotatedTiles.add(new LudoTile(tile.getTileId(), new int[] {startAreaSize - 1 - coordinates[1], coordinates[0]}, tile.getNextTileId(), ((LudoTile)tile).getType()));
          } }
      default -> throw new IllegalArgumentException("Invalid start area index. Must be between 1 and 4.");
    }

    return rotatedTiles;
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
        System.out.println("Creating finish tile with ID: " + tileId + ", coordinates: [" + coordinates[0] + ", " + coordinates[1] + "]");
        Tile tile = new LudoTile(tileId, coordinates, nextTileId, type);
        tiles.add(tile);
      }
    }
    return tiles;
  }
}
