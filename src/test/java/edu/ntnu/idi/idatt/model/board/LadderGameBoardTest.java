package edu.ntnu.idi.idatt.model.board;

import edu.ntnu.idi.idatt.model.tile.LadderAction;
import edu.ntnu.idi.idatt.model.tile.LadderGameTile;
import edu.ntnu.idi.idatt.model.tile.Tile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LadderGameBoardTest {
  
  private LadderGameBoard ladderGameBoard;
  private final String boardName = "Test Ladder Board";
  private final String boardDescription = "A board for testing LadderGameBoard";
  private final int[] defaultRowsAndColumns = {10, 10};
  private final String defaultBackground = "background.png";
  private final String defaultPattern = "pattern1";
  
  @BeforeEach
  void setUp() {
    ladderGameBoard = new LadderGameBoard(boardName, boardDescription, defaultRowsAndColumns, defaultBackground, defaultPattern);
  }
  
  @Nested
  @DisplayName("Constructor and Getters")
  class ConstructorAndGetterTests {
    
    @Test
    @DisplayName("Test constructor with valid arguments")
    void testConstructorValidArguments() {
      assertNotNull(ladderGameBoard);
      assertEquals(boardName, ladderGameBoard.getName());
      assertEquals(boardDescription, ladderGameBoard.getDescription());
      assertArrayEquals(defaultRowsAndColumns, ladderGameBoard.getRowsAndColumns());
      assertEquals(defaultBackground, ladderGameBoard.getBackground());
      assertEquals(defaultPattern, ladderGameBoard.getPattern());
      assertEquals(defaultRowsAndColumns[0] * defaultRowsAndColumns[1], ladderGameBoard.getTileCount());
    }
    
    @Test
    @DisplayName("Test getRowsAndColumns returns correct array")
    void testGetRowsAndColumns() {
      assertArrayEquals(defaultRowsAndColumns, ladderGameBoard.getRowsAndColumns());
    }
    
    @Test
    @DisplayName("Test getPattern returns correct pattern")
    void testGetPattern() {
      assertEquals(defaultPattern, ladderGameBoard.getPattern());
    }
  }
  
  @Nested
  @DisplayName("Setter Methods")
  class SetterMethodTests {
    
    @Test
    @DisplayName("Test setRowsAndColumns with valid input")
    void testSetRowsAndColumnsValid() {
      int[] newRowsAndColumns = {5, 5};
      ladderGameBoard.setRowsAndColumns(newRowsAndColumns);
      assertArrayEquals(newRowsAndColumns, ladderGameBoard.getRowsAndColumns());
      assertEquals(newRowsAndColumns[0] * newRowsAndColumns[1], ladderGameBoard.getTileCount());
    }
    
    @Test
    @DisplayName("Test setPattern with valid input")
    void testSetPatternValid() {
      String newPattern = "newPattern";
      ladderGameBoard.setPattern(newPattern);
      assertEquals(newPattern, ladderGameBoard.getPattern());
    }
  }
  
  @Nested
  @DisplayName("Tile Creation Logic")
  class TileCreationTests {
    
    @Test
    @DisplayName("Test createTiles generates correct number of tiles")
    void testCreateTilesTileCount() {
      assertEquals(defaultRowsAndColumns[0] * defaultRowsAndColumns[1], ladderGameBoard.getTileCount());
      // Test with different dimensions
      ladderGameBoard.createTiles(5, 6);
      assertEquals(30, ladderGameBoard.getTileCount());
      assertEquals(5 * 6 + 1, ladderGameBoard.getTiles().size()); // +1 for 0th tile
    }
    
    @Test
    @DisplayName("Test createTiles creates 0th tile")
    void testCreateTilesCreatesZerothTile() {
      Tile zerothTile = ladderGameBoard.getTile(0);
      assertNotNull(zerothTile);
      assertEquals(0, zerothTile.getTileId());
      assertInstanceOf(LadderGameTile.class, zerothTile);
      assertArrayEquals(new int[]{0, -2}, ((LadderGameTile) zerothTile).getCoordinates());
    }
    
    @Test
    @DisplayName("Test tile IDs and coordinates for even rows (left to right)")
    void testTileCreationEvenRows() {
      // Row 0 (i=0, even): tiles 1-10
      for (int j = 0; j < defaultRowsAndColumns[1]; j++) {
        int tileId = j + 1;
        Tile tile = ladderGameBoard.getTile(tileId);
        assertNotNull(tile, "Tile " + tileId + " should exist");
        assertEquals(tileId, tile.getTileId());
        assertInstanceOf(LadderGameTile.class, tile);
        assertArrayEquals(new int[]{0, j}, ((LadderGameTile) tile).getCoordinates());
      }
      
      // Row 2 (i=2, even): tiles 21-30
      for (int j = 0; j < defaultRowsAndColumns[1]; j++) {
        int tileId = 2 * defaultRowsAndColumns[1] + j + 1;
        Tile tile = ladderGameBoard.getTile(tileId);
        assertNotNull(tile, "Tile " + tileId + " should exist");
        assertEquals(tileId, tile.getTileId());
        assertInstanceOf(LadderGameTile.class, tile);
        assertArrayEquals(new int[]{2, j}, ((LadderGameTile) tile).getCoordinates());
      }
    }
    
    @Test
    @DisplayName("Test tile IDs and coordinates for odd rows (right to left)")
    void testTileCreationOddRows() {
      // Row 1 (i=1, odd): tiles 11-20
      for (int j = 0; j < defaultRowsAndColumns[1]; j++) {
        int tileId = defaultRowsAndColumns[1] + (defaultRowsAndColumns[1] - j);
        Tile tile = ladderGameBoard.getTile(tileId);
        assertNotNull(tile, "Tile " + tileId + " should exist");
        assertEquals(tileId, tile.getTileId());
        assertInstanceOf(LadderGameTile.class, tile);
        assertArrayEquals(new int[]{1, j}, ((LadderGameTile) tile).getCoordinates());
      }
      
      // Row 3 (i=3, odd): tiles 31-40
      for (int j = 0; j < defaultRowsAndColumns[1]; j++) {
        int tileId = 3 * defaultRowsAndColumns[1] + (defaultRowsAndColumns[1] - j);
        Tile tile = ladderGameBoard.getTile(tileId);
        assertNotNull(tile, "Tile " + tileId + " should exist");
        assertEquals(tileId, tile.getTileId());
        assertInstanceOf(LadderGameTile.class, tile);
        assertArrayEquals(new int[]{3, j}, ((LadderGameTile) tile).getCoordinates());
      }
    }
    
    @Test
    @DisplayName("Test createTiles preserves existing tile actions")
    void testCreateTilesPreservesActions() {
      // Set a custom action on tile 5
      LadderGameTile originalTile5 = (LadderGameTile) ladderGameBoard.getTile(5);
      originalTile5.setLandAction(new LadderAction("TestLadderTo50", 50, "Ladder to tile 50"));
      
      // Recreate tiles with same dimensions
      ladderGameBoard.createTiles(defaultRowsAndColumns[0], defaultRowsAndColumns[1]);
      LadderGameTile newTile5 = (LadderGameTile) ladderGameBoard.getTile(5);
      assertNotNull(newTile5);
      assertNotNull(newTile5.getLandAction(), "Land action should not be null");
      assertEquals(50, newTile5.getLandAction().getDestinationTileId(), "Land action destination should be preserved");
      
      // Re-setup to ensure clean state for next check
      ladderGameBoard = new LadderGameBoard(boardName, boardDescription, defaultRowsAndColumns, defaultBackground, defaultPattern);
      LadderGameTile tile15 = (LadderGameTile) ladderGameBoard.getTile(15);
      tile15.setLandAction(new LadderAction("TestSlideTo1", 1, "Slide to tile 1"));
      
      // Recreate board with larger dimensions, action should be preserved
      ladderGameBoard.createTiles(12,12);
      LadderGameTile newTile15 = (LadderGameTile) ladderGameBoard.getTile(15);
      assertNotNull(newTile15);
      assertNotNull(newTile15.getLandAction(), "Land action should not be null after resize larger");
      assertEquals(1, newTile15.getLandAction().getDestinationTileId(), "Land action should be preserved on resize larger");
    }
  }
  
  @Nested
  @DisplayName("Negative Tests")
  class NegativeTests {
    
    @Test
    @DisplayName("Test constructor with null name")
    void testConstructorNullName() {
      assertThrows(IllegalArgumentException.class, () ->
      new LadderGameBoard(null, boardDescription, defaultRowsAndColumns, defaultBackground, defaultPattern)
      );
    }
    
    @Test
    @DisplayName("Test constructor with empty name")
    void testConstructorEmptyName() {
      assertThrows(IllegalArgumentException.class, () ->
      new LadderGameBoard("", boardDescription, defaultRowsAndColumns, defaultBackground, defaultPattern)
      );
    }
    
    @Test
    @DisplayName("Test constructor with null description")
    void testConstructorNullDescription() {
      assertThrows(IllegalArgumentException.class, () ->
      new LadderGameBoard(boardName, null, defaultRowsAndColumns, defaultBackground, defaultPattern)
      );
    }
    
    @Test
    @DisplayName("Test constructor with empty description")
    void testConstructorEmptyDescription() {
      assertThrows(IllegalArgumentException.class, () ->
      new LadderGameBoard(boardName, "", defaultRowsAndColumns, defaultBackground, defaultPattern)
      );
    }
    
    @Test
    @DisplayName("Test constructor with null rowsAndColumns")
    void testConstructorNullRowsAndColumns() {
      assertThrows(IllegalArgumentException.class, () ->
      new LadderGameBoard(boardName, boardDescription, null, defaultBackground, defaultPattern)
      );
    }
    
    @Test
    @DisplayName("Test constructor with invalid rowsAndColumns (less than 2 elements)")
    void testConstructorInvalidRowsAndColumnsLengthShort() {
      assertThrows(IllegalArgumentException.class, () ->
      new LadderGameBoard(boardName, boardDescription, new int[]{5}, defaultBackground, defaultPattern)
      );
    }
    
    @Test
    @DisplayName("Test constructor with invalid rowsAndColumns (more than 2 elements)")
    void testConstructorInvalidRowsAndColumnsLengthLong() {
      assertThrows(IllegalArgumentException.class, () ->
      new LadderGameBoard(boardName, boardDescription, new int[]{5,5,5}, defaultBackground, defaultPattern)
      );
    }
    
    @Test
    @DisplayName("Test constructor with invalid rows (<=0)")
    void testConstructorInvalidRows() {
      assertThrows(IllegalArgumentException.class, () ->
      new LadderGameBoard(boardName, boardDescription, new int[]{0, 5}, defaultBackground, defaultPattern)
      );
    }
    
    @Test
    @DisplayName("Test constructor with invalid columns (<=0)")
    void testConstructorInvalidColumns() {
      assertThrows(IllegalArgumentException.class, () ->
      new LadderGameBoard(boardName, boardDescription, new int[]{5, 0}, defaultBackground, defaultPattern)
      );
    }
    
    @Test
    @DisplayName("Test constructor with null background")
    void testConstructorNullBackground() {
      assertThrows(IllegalArgumentException.class, () ->
      new LadderGameBoard(boardName, boardDescription, defaultRowsAndColumns, null, defaultPattern)
      );
    }
    
    @Test
    @DisplayName("Test constructor with empty background")
    void testConstructorEmptyBackground() {
      assertThrows(IllegalArgumentException.class, () ->
      new LadderGameBoard(boardName, boardDescription, defaultRowsAndColumns, "", defaultPattern)
      );
    }
    
    
    @Test
    @DisplayName("Test constructor with null pattern")
    void testConstructorNullPattern() {
      assertThrows(IllegalArgumentException.class, () ->
      new LadderGameBoard(boardName, boardDescription, defaultRowsAndColumns, defaultBackground, null)
      );
    }
    
    @Test
    @DisplayName("Test constructor with empty pattern")
    void testConstructorEmptyPattern() {
      assertThrows(IllegalArgumentException.class, () ->
      new LadderGameBoard(boardName, boardDescription, defaultRowsAndColumns, defaultBackground, "")
      );
    }
    
    @Test
    @DisplayName("Test setRowsAndColumns with null array")
    void testSetRowsAndColumnsNull() {
      assertThrows(IllegalArgumentException.class, () -> ladderGameBoard.setRowsAndColumns(null));
    }
    
    @Test
    @DisplayName("Test setRowsAndColumns with invalid length (short)")
    void testSetRowsAndColumnsInvalidLengthShort() {
      assertThrows(IllegalArgumentException.class, () -> ladderGameBoard.setRowsAndColumns(new int[]{5}));
    }
    
    @Test
    @DisplayName("Test setRowsAndColumns with invalid length (long)")
    void testSetRowsAndColumnsInvalidLengthLong() {
      assertThrows(IllegalArgumentException.class, () -> ladderGameBoard.setRowsAndColumns(new int[]{5,5,5}));
    }
    
    @Test
    @DisplayName("Test setRowsAndColumns with invalid rows (<=0)")
    void testSetRowsAndColumnsInvalidRows() {
      assertThrows(IllegalArgumentException.class, () -> ladderGameBoard.setRowsAndColumns(new int[]{0, 5}));
    }
    
    @Test
    @DisplayName("Test setRowsAndColumns with invalid columns (<=0)")
    void testSetRowsAndColumnsInvalidColumns() {
      assertThrows(IllegalArgumentException.class, () -> ladderGameBoard.setRowsAndColumns(new int[]{5, 0}));
    }
    
    @Test
    @DisplayName("Test setPattern with null pattern")
    void testSetPatternNull() {
      assertThrows(IllegalArgumentException.class, () -> ladderGameBoard.setPattern(null));
    }
    
    @Test
    @DisplayName("Test setPattern with empty pattern")
    void testSetPatternEmpty() {
      assertThrows(IllegalArgumentException.class, () -> ladderGameBoard.setPattern(""));
    }
    
    @Test
    @DisplayName("Test createTiles with zero rows")
    void testCreateTilesZeroRows() {
      assertThrows(IllegalArgumentException.class, () -> ladderGameBoard.createTiles(0, 5));
    }
    
    @Test
    @DisplayName("Test createTiles with zero columns")
    void testCreateTilesZeroColumns() {
      assertThrows(IllegalArgumentException.class, () -> ladderGameBoard.createTiles(5, 0));
    }
    
    @Test
    @DisplayName("Test createTiles with negative rows")
    void testCreateTilesNegativeRows() {
      assertThrows(IllegalArgumentException.class, () -> ladderGameBoard.createTiles(-1, 5));
    }
    
    @Test
    @DisplayName("Test createTiles with negative columns")
    void testCreateTilesNegativeColumns() {
      assertThrows(IllegalArgumentException.class, () -> ladderGameBoard.createTiles(5, -1));
    }
    
    @Test
    @DisplayName("Test createTiles with rows and columns less than 5")
    void testCreateTilesRowsAndColumnsLessThan5() {
      assertThrows(IllegalArgumentException.class, () -> ladderGameBoard.createTiles(4, 4));
    }
    
    @Test
    @DisplayName("Test createTiles with rows and columns greater than 12")
    void testCreateTilesRowsAndColumnsGreaterThan12() {
      assertThrows(IllegalArgumentException.class, () -> ladderGameBoard.createTiles(13, 13));
    }
    
  }
} 