package edu.ntnu.idi.idatt.factory.board;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import edu.ntnu.idi.idatt.filehandler.LadderGameBoardFileHandlerGson;
import edu.ntnu.idi.idatt.model.board.Board;
import edu.ntnu.idi.idatt.model.board.LadderGameBoard;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class LadderBoardFactoryTest {
  
  private LadderBoardFactory ladderBoardFactory;
  
  @BeforeEach
  void setUp() {
    ladderBoardFactory = new LadderBoardFactory();
  }
  
  @Test
  @DisplayName("Test creating a blank board with valid dimensions")
  void testCreateBlankBoard_validDimensions() {
    int rows = 8;
    int columns = 8;
    Board board = ladderBoardFactory.createBlankBoard(rows, columns);
    
    assertNotNull(board);
    assertInstanceOf(LadderGameBoard.class, board);
    LadderGameBoard ladderGameBoard = (LadderGameBoard) board;
    assertEquals("Blank Board", ladderGameBoard.getName());
    assertEquals("Blank board with " + rows + " rows and " + columns + " columns.", ladderGameBoard.getDescription());
    assertArrayEquals(new int[]{rows, columns}, ladderGameBoard.getRowsAndColumns());
    assertEquals("media/boards/whiteBoard.png", ladderGameBoard.getBackground());
    assertEquals("None", ladderGameBoard.getPattern());
    assertEquals(rows * columns + 1, ladderGameBoard.getTiles().size()); // +1 for 0th tile
  }
  
  @Test
  @DisplayName("Test createBoard with 'classic' variant loads correctly")
  void testCreateBoard_classicVariant() {
    Board mockBoard = mock(LadderGameBoard.class);
    when(mockBoard.getName()).thenReturn("Classic Board Loaded");
    
    try (var gsonHandlerMockedConstruction = Mockito.mockConstruction(LadderGameBoardFileHandlerGson.class,
    (mock, context) -> {
      // Mocking the call for the classic board path
      when(mock.readFile("src/main/resources/boards/ClassicLadderGameBoard.json")).thenReturn(mockBoard);
    })) {
      Board classicBoard = ladderBoardFactory.createBoard("classic");
      assertNotNull(classicBoard);
      assertEquals("Classic Board Loaded", classicBoard.getName());
      assertEquals(1, gsonHandlerMockedConstruction.constructed().size());
    }
  }

  @Test
  @DisplayName("Test createBoard with 'teleporting' variant loads correctly")
  void testCreateBoard_teleportingVariant() {
    Board mockBoard = mock(LadderGameBoard.class);
    when(mockBoard.getName()).thenReturn("Teleporting Board Loaded");
    
    try (var gsonHandlerMockedConstruction = Mockito.mockConstruction(LadderGameBoardFileHandlerGson.class,
    (mock, context) -> {
      when(mock.readFile("src/main/resources/boards/PortalLadderGameBoard.json")).thenReturn(mockBoard);
    })) {
      Board portalBoard = ladderBoardFactory.createBoard("teleporting");
      assertNotNull(portalBoard);
      assertEquals("Teleporting Board Loaded", portalBoard.getName());
      assertEquals(1, gsonHandlerMockedConstruction.constructed().size());
    }
  }
  
  @Test
  @DisplayName("Test createBoard with unknown variant throws IllegalArgumentException")
  void testCreateBoard_unknownVariant() {
    assertThrows(IllegalArgumentException.class, () -> {
      ladderBoardFactory.createBoard("unknownVariant");
    });
  }
  
  @Test
  @DisplayName("Test createBoardFromFile with valid path returns board")
  void testCreateBoardFromFile_validPath() {
    Board mockBoard = mock(LadderGameBoard.class);
    when(mockBoard.getName()).thenReturn("File Board");
    
    try (var gsonHandlerMockedConstruction = Mockito.mockConstruction(LadderGameBoardFileHandlerGson.class,
    (mock, context) -> {
      when(mock.readFile("some/valid/path.json")).thenReturn(mockBoard);
    })) {
      Board board = ladderBoardFactory.createBoardFromFile("some/valid/path.json");
      assertNotNull(board);
      assertEquals("File Board", board.getName());
      assertEquals(1, gsonHandlerMockedConstruction.constructed().size());
    }
  }
  
  @Test
  @DisplayName("Test createBoardFromFile when file handler returns null (e.g., IOException)")
  void testCreateBoardFromFile_handlerReturnsNull() {
    try (var gsonHandlerMockedConstruction = Mockito.mockConstruction(LadderGameBoardFileHandlerGson.class,
    (mock, context) -> {
      when(mock.readFile(anyString())).thenThrow(new IOException("Simulated Read Error"));
    })) {
      Board board = ladderBoardFactory.createBoardFromFile("failing/path.json");
      assertNull(board);
      assertEquals(1, gsonHandlerMockedConstruction.constructed().size());
    }
  }
} 