package edu.ntnu.idi.idatt.model.board;

import edu.ntnu.idi.idatt.model.tile.LudoTile;
import edu.ntnu.idi.idatt.model.tile.Tile;
import javafx.scene.paint.Color;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LudoGameBoardTest {

  private LudoGameBoard ludoGameBoard;
  private final String boardName = "Test Ludo Board";
  private final String boardDescription = "A board for testing LudoGameBoard";
  private final String defaultBackground = "ludo_background.png";
  private final int defaultBoardSize = 15; // Standard Ludo board size
  private final Color[] defaultColors = {Color.RED, Color.GREEN, Color.YELLOW, Color.BLUE};

  @BeforeEach
  void setUp() {
    ludoGameBoard = new LudoGameBoard(boardName, boardDescription, defaultBackground, defaultBoardSize, defaultColors);
  }

  @Nested
  @DisplayName("Constructor and Getters")
  class ConstructorAndGetterTests {

    @Test
    @DisplayName("Test constructor with valid arguments")
    void testConstructorValidArguments() {
      assertNotNull(ludoGameBoard);
      assertEquals(boardName, ludoGameBoard.getName());
      assertEquals(boardDescription, ludoGameBoard.getDescription());
      assertEquals(defaultBackground, ludoGameBoard.getBackground());
      assertEquals(defaultBoardSize, ludoGameBoard.getBoardSize());
      assertArrayEquals(defaultColors, ludoGameBoard.getColors());

      // Verify derived properties are set
      assertTrue(ludoGameBoard.getStartAreaSize() > 0);
      assertTrue(ludoGameBoard.getTotalTrackTileCount() > 0);
      assertEquals(4, ludoGameBoard.getPlayerStartIndexes().length);
      assertEquals(4, ludoGameBoard.getPlayerTrackStartIndexes().length);
      assertEquals(4, ludoGameBoard.getPlayerFinishStartIndexes().length);
      assertEquals(4, ludoGameBoard.getPlayerFinishIndexes().length);

      // Check total tile count
      assertEquals(ludoGameBoard.getTileCount(),
          ludoGameBoard.getBoardSize() * ludoGameBoard.getBoardSize(),
          "Tile count should be equal to board size squared");
    }

    @Test
    @DisplayName("Test getColors returns correct array")
    void testGetColors() {
      assertArrayEquals(defaultColors, ludoGameBoard.getColors());
    }

    @Test
    @DisplayName("Test getBoardSize returns correct size")
    void testGetBoardSize() {
      assertEquals(defaultBoardSize, ludoGameBoard.getBoardSize());
    }

    @Test
    @DisplayName("Test getPlayerStartIndexes returns non-empty array")
    void testGetPlayerStartIndexes() {
      int[] startIndexes = ludoGameBoard.getPlayerStartIndexes();
      assertNotNull(startIndexes);
      assertEquals(4, startIndexes.length);
      assertTrue(Arrays.stream(startIndexes).allMatch(i -> i > 0));
    }

    @Test
    @DisplayName("Test getPlayerTrackStartIndexes returns non-empty array")
    void testGetPlayerTrackStartIndexes() {
      int[] trackStartIndexes = ludoGameBoard.getPlayerTrackStartIndexes();
      assertNotNull(trackStartIndexes);
      assertEquals(4, trackStartIndexes.length);
      assertTrue(Arrays.stream(trackStartIndexes).allMatch(i -> i > 0));
    }

    @Test
    @DisplayName("Test getPlayerFinishStartIndexes returns non-empty array")
    void testGetPlayerFinishStartIndexes() {
      int[] finishStartIndexes = ludoGameBoard.getPlayerFinishStartIndexes();
      assertNotNull(finishStartIndexes);
      assertEquals(4, finishStartIndexes.length);
      assertTrue(Arrays.stream(finishStartIndexes).allMatch(i -> i > 0));
    }

    @Test
    @DisplayName("Test getStartAreaSize calculates correctly")
    void testGetStartAreaSize() {
      assertEquals((defaultBoardSize - 3) / 2, ludoGameBoard.getStartAreaSize());
    }

    @Test
    @DisplayName("Test getTotalTrackTileCount calculates correctly")
    void testGetTotalTrackTileCount() {
      int startAreaSize = (defaultBoardSize - 3) / 2;
      int finishTrackSize = startAreaSize - 1;
      int trackTileCountPerPlayer = 3 + (finishTrackSize * 2);
      assertEquals(trackTileCountPerPlayer * 4, ludoGameBoard.getTotalTrackTileCount());
    }
  }

  @Nested
  @DisplayName("Setter Methods")
  class SetterMethodTests {

    @Test
    @DisplayName("Test setColors with valid input")
    void testSetColorsValid() {
      Color[] newColors = {Color.ORANGE, Color.PURPLE, Color.PINK, Color.CYAN};
      ludoGameBoard.setColors(newColors);
      assertArrayEquals(newColors, ludoGameBoard.getColors());
    }

    @Test
    @DisplayName("Test setBoardSize with valid input, re-creates tiles")
    void testSetBoardSizeValid() {
      int newBoardSize = 19;
      ludoGameBoard.setBoardSize(newBoardSize);
      assertEquals(newBoardSize, ludoGameBoard.getBoardSize());
      // Check that new tiles were created
      assertEquals(newBoardSize * newBoardSize, ludoGameBoard.getTileCount(),
          "Tile count should change with board size");

      // Verify derived properties are updated
      assertEquals((newBoardSize - 3) / 2, ludoGameBoard.getStartAreaSize());
    }
  }

  @Nested
  @DisplayName("Tile Creation and Structure")
  class TileCreationTests {

    @Test
    @DisplayName("Test createTiles generates a positive number of tiles")
    void testCreateTilesGeneratesTiles() {
      // Default setup already calls createTiles via constructor
      assertTrue(ludoGameBoard.getTileCount() > 0, "Tile count should be positive after init.");
      List<Tile> tileList = ludoGameBoard.getTiles();
      assertFalse(tileList.isEmpty(), "Tiles list should not be empty.");
    }

    @Test
    @DisplayName("Test start area tiles are created and have correct type")
    void testStartAreaTiles() {
      int tilesPerStartArea = ludoGameBoard.getStartAreaSize() * ludoGameBoard.getStartAreaSize();
      int[] playerStartIndexes = ludoGameBoard.getPlayerStartIndexes();

      for (int p = 0; p < 4; p++) {
        int startTileId = playerStartIndexes[p];
        for (int i = 0; i < tilesPerStartArea; i++) {
          Tile tile = ludoGameBoard.getTile(startTileId + i);
          assertNotNull(tile, "Start area tile " + (startTileId + i) + " should exist for player " + (p + 1));
          assertInstanceOf(LudoTile.class, tile, "Start area tile should be LudoTile");
          assertEquals("start-" + (p + 1), ((LudoTile) tile).getType(), "Incorrect type for start tile");
        }
      }
    }

    @Test
    @DisplayName("Test main track tiles exist and are LudoTiles")
    void testMainTrackTilesExist() {
      int[] playerTrackStartIndexes = ludoGameBoard.getPlayerTrackStartIndexes();
      for (int p = 0; p < 4; p++) {
        Tile trackStartTile = ludoGameBoard.getTile(playerTrackStartIndexes[p]);
        assertNotNull(trackStartTile, "Track start tile for player " + (p+1) + " should exist.");
        assertInstanceOf(LudoTile.class, trackStartTile);
      }
    }

    @Test
    @DisplayName("Test finish area tiles exist and are LudoTiles")
    void testFinishAreaTilesExist() {
      int[] playerFinishStartIndexes = ludoGameBoard.getPlayerFinishStartIndexes();
      int startAreaSize = ludoGameBoard.getStartAreaSize();
      int finishTrackSize = startAreaSize - 1; // Each finish track (excluding center) has this many tiles

      for (int p = 0; p < 4; p++) {
        int finishStartId = playerFinishStartIndexes[p];
        for(int i=0; i < finishTrackSize; i++) {
          Tile finishTile = ludoGameBoard.getTile(finishStartId + i);
          assertNotNull(finishTile, "Finish tile "+ (finishStartId+i) +" for player " + (p+1) + " should exist.");
          assertInstanceOf(LudoTile.class, finishTile);
          assertEquals("finish-"+(p+1), ((LudoTile)finishTile).getType());
        }
      }
    }
  }

  @Nested
  @DisplayName("Negative Tests - Invalid Inputs")
  class NegativeTests {

    @Test
    @DisplayName("Test constructor with null name")
    void testConstructorNullName() {
      assertThrows(IllegalArgumentException.class, () ->
          new LudoGameBoard(null, boardDescription, defaultBackground, defaultBoardSize, defaultColors)
      );
    }

    @Test
    @DisplayName("Test constructor with empty name")
    void testConstructorEmptyName() {
      assertThrows(IllegalArgumentException.class, () ->
          new LudoGameBoard("", boardDescription, defaultBackground, defaultBoardSize, defaultColors)
      );
    }

    @Test
    @DisplayName("Test constructor with null description")
    void testConstructorNullDescription() {
      assertThrows(IllegalArgumentException.class, () ->
          new LudoGameBoard(boardName, null, defaultBackground, defaultBoardSize, defaultColors)
      );
    }

    @Test
    @DisplayName("Test constructor with empty description")
    void testConstructorEmptyDescription() {
      assertThrows(IllegalArgumentException.class, () ->
          new LudoGameBoard(boardName, "", defaultBackground, defaultBoardSize, defaultColors)
      );
    }

    @Test
    @DisplayName("Test constructor with null background")
    void testConstructorNullBackground() {
      assertThrows(IllegalArgumentException.class, () ->
          new LudoGameBoard(boardName, boardDescription, null, defaultBoardSize, defaultColors)
      );
    }

    @Test
    @DisplayName("Test constructor with empty background")
    void testConstructorEmptyBackground() {
      assertThrows(IllegalArgumentException.class, () ->
          new LudoGameBoard(boardName, boardDescription, "", defaultBoardSize, defaultColors)
      );
    }

    @Test
    @DisplayName("Test constructor with invalid board size (too small)")
    void testConstructorInvalidBoardSizeSmall() {
      // Minimum board size (e.g., 11 for startAreaSize = 4)
      // Validator: boardSize >= 7 && boardSize % 2 != 0
      assertThrows(IllegalArgumentException.class, () ->
          new LudoGameBoard(boardName, boardDescription, defaultBackground, 5, defaultColors) // Too small
      );
      assertThrows(IllegalArgumentException.class, () ->
          new LudoGameBoard(boardName, boardDescription, defaultBackground, 6, defaultColors) // Even
      );
    }

    @Test
    @DisplayName("Test constructor with null colors array")
    void testConstructorNullColors() {
      assertThrows(IllegalArgumentException.class, () ->
          new LudoGameBoard(boardName, boardDescription, defaultBackground, defaultBoardSize, null)
      );
    }

    @Test
    @DisplayName("Test constructor with too few colors")
    void testConstructorTooFewColors() {
      Color[] fewColors = {Color.RED, Color.GREEN};
      assertThrows(IllegalArgumentException.class, () ->
          new LudoGameBoard(boardName, boardDescription, defaultBackground, defaultBoardSize, fewColors)
      );
    }

    @Test
    @DisplayName("Test constructor with too many colors")
    void testConstructorTooManyColors() {
      Color[] manyColors = {Color.RED, Color.GREEN, Color.YELLOW, Color.BLUE, Color.ORANGE};
      assertThrows(IllegalArgumentException.class, () ->
          new LudoGameBoard(boardName, boardDescription, defaultBackground, defaultBoardSize, manyColors)
      );
    }

    @Test
    @DisplayName("Test constructor with null color in colors array")
    void testConstructorNullInColors() {
      Color[] colorsWithNull = {Color.RED, null, Color.YELLOW, Color.BLUE};
      assertThrows(IllegalArgumentException.class, () ->
          new LudoGameBoard(boardName, boardDescription, defaultBackground, defaultBoardSize, colorsWithNull)
      );
    }

    @Test
    @DisplayName("Test setColors with null array")
    void testSetColorsNull() {
      assertThrows(IllegalArgumentException.class, () -> ludoGameBoard.setColors(null));
    }

    @Test
    @DisplayName("Test setColors with too few colors")
    void testSetColorsTooFew() {
      Color[] fewColors = {Color.RED};
      assertThrows(IllegalArgumentException.class, () -> ludoGameBoard.setColors(fewColors));
    }

    @Test
    @DisplayName("Test setColors with too many colors")
    void testSetColorsTooMany() {
      Color[] manyColors = {Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW, Color.PURPLE};
      assertThrows(IllegalArgumentException.class, () -> ludoGameBoard.setColors(manyColors));
    }

    @Test
    @DisplayName("Test setColors with null element in array")
    void testSetColorsNullElement() {
      Color[] colorsWithNull = {Color.RED, Color.GREEN, null, Color.BLUE};
      assertThrows(IllegalArgumentException.class, () -> ludoGameBoard.setColors(colorsWithNull));
    }

    @Test
    @DisplayName("Test setBoardSize with invalid size (too small)")
    void testSetBoardSizeInvalidSmall() {
      assertThrows(IllegalArgumentException.class, () -> ludoGameBoard.setBoardSize(5));
    }

    @Test
    @DisplayName("Test setBoardSize with invalid size (even number)")
    void testSetBoardSizeInvalidEven() {
      assertThrows(IllegalArgumentException.class, () -> ludoGameBoard.setBoardSize(10));
    }
  }
} 