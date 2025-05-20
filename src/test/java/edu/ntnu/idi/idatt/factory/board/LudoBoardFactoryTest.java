package edu.ntnu.idi.idatt.factory.board;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import edu.ntnu.idi.idatt.filehandler.LudoBoardFileHandlerGson;
import edu.ntnu.idi.idatt.model.board.Board;
import edu.ntnu.idi.idatt.model.board.LudoGameBoard;
import java.io.IOException;
import javafx.scene.paint.Color;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class LudoBoardFactoryTest {

  private LudoBoardFactory ludoBoardFactory;
  private static final Color[] DEFAULT_COLORS = {Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW};

  @BeforeEach
  void setUp() {
    ludoBoardFactory = new LudoBoardFactory();
  }

  @Test
  @DisplayName("Test creating a blank Ludo board")
  void testCreateBlankBoard() {
    int boardSize = 15;
    Board board = ludoBoardFactory.createBlankBoard(boardSize, boardSize);

    assertNotNull(board);
    assertInstanceOf(LudoGameBoard.class, board);
    LudoGameBoard ludoGameBoard = (LudoGameBoard) board;
    assertEquals("Blank ludo board", ludoGameBoard.getName());
    assertEquals("Blank ludo board", ludoGameBoard.getDescription());
    assertEquals("media/boards/whiteBoard.png", ludoGameBoard.getBackground());
    assertEquals(boardSize, ludoGameBoard.getBoardSize());
    assertArrayEquals(DEFAULT_COLORS, ludoGameBoard.getColors());
    assertEquals(boardSize * boardSize, ludoGameBoard.getTiles().size());
  }

  @Test
  @DisplayName("Test createBoard with 'Classic' variant loads correctly")
  void testCreateBoard_classicVariant() {
    Board mockBoard = mock(LudoGameBoard.class);
    when(mockBoard.getName()).thenReturn("Classic Ludo Loaded");

    try (var gsonHandlerMockedConstruction = Mockito.mockConstruction(LudoBoardFileHandlerGson.class,
        (mock, context) -> when(mock.readFile("src/main/resources/boards/ClassicLudoBoard.json")).thenReturn(mockBoard)
        )) {

      Board classicBoard = ludoBoardFactory.createBoard("Classic");
      assertNotNull(classicBoard);
      assertEquals("Classic Ludo Loaded", classicBoard.getName());
      assertEquals(1, gsonHandlerMockedConstruction.constructed().size());
    }
  }

  @Test
  @DisplayName("Test createBoard with 'Small' variant loads correctly")
  void testCreateBoard_smallVariant() {
    Board mockBoard = mock(LudoGameBoard.class);
    when(mockBoard.getName()).thenReturn("Small Ludo Loaded");

    try (var gsonHandlerMockedConstruction = Mockito.mockConstruction(LudoBoardFileHandlerGson.class,
        (mock, context) -> when(mock.readFile("src/main/resources/boards/SmallLudoBoard.json")).thenReturn(mockBoard)
        )) {

      Board smallBoard = ludoBoardFactory.createBoard("Small");
      assertNotNull(smallBoard);
      assertEquals("Small Ludo Loaded", smallBoard.getName());
      assertEquals(1, gsonHandlerMockedConstruction.constructed().size());
    }
  }

  @Test
  @DisplayName("Test createBoard with 'Large' variant loads correctly")
  void testCreateBoard_largeVariant() {
    Board mockBoard = mock(LudoGameBoard.class);
    when(mockBoard.getName()).thenReturn("Large Ludo Loaded");

    try (var gsonHandlerMockedConstruction = Mockito.mockConstruction(LudoBoardFileHandlerGson.class,
        (mock, context) -> when(mock.readFile("src/main/resources/boards/XlLudoBoard.json")).thenReturn(mockBoard)
        )) {

      Board largeBoard = ludoBoardFactory.createBoard("Large");
      assertNotNull(largeBoard);
      assertEquals("Large Ludo Loaded", largeBoard.getName());
      assertEquals(1, gsonHandlerMockedConstruction.constructed().size());
    }
  }

  @Test
  @DisplayName("Test createBoard with unknown variant throws IllegalArgumentException")
  void testCreateBoard_unknownVariant() {
    assertThrows(IllegalArgumentException.class, () -> ludoBoardFactory.createBoard("unknownVariant")
    );
  }

  @Test
  @DisplayName("Test createBoardFromFile with valid path returns board")
  void testCreateBoardFromFile_validPath() {
    Board mockBoard = mock(LudoGameBoard.class);
    when(mockBoard.getName()).thenReturn("File Ludo Board");

    try (var gsonHandlerMockedConstruction = Mockito.mockConstruction(LudoBoardFileHandlerGson.class,
        (mock, context) -> when(mock.readFile("some/valid/ludo_path.json")).thenReturn(mockBoard)
        )) {

      Board board = ludoBoardFactory.createBoardFromFile("some/valid/ludo_path.json");
      assertNotNull(board);
      assertEquals("File Ludo Board", board.getName());
      assertEquals(1, gsonHandlerMockedConstruction.constructed().size());
    }
  }

  @Test
  @DisplayName("Test createBoardFromFile when file handler returns null (e.g., IOException)")
  void testCreateBoardFromFile_handlerReturnsNull() {
    try (var gsonHandlerMockedConstruction = Mockito.mockConstruction(LudoBoardFileHandlerGson.class,
        (mock, context) -> when(mock.readFile(anyString())).thenThrow(new IOException("Simulated Ludo Read Error"))
        )) {

      Board board = ludoBoardFactory.createBoardFromFile("failing/ludo_path.json");
      assertNull(board);
      assertEquals(1, gsonHandlerMockedConstruction.constructed().size());
    }
  }
} 