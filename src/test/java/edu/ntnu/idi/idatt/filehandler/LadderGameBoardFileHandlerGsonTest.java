package edu.ntnu.idi.idatt.filehandler;

import static org.junit.jupiter.api.Assertions.*;

import com.google.gson.JsonObject;
import edu.ntnu.idi.idatt.model.board.Board;
import edu.ntnu.idi.idatt.model.board.LadderGameBoard;
import edu.ntnu.idi.idatt.model.tile.LadderAction;
import edu.ntnu.idi.idatt.model.tile.LadderGameTile;
import edu.ntnu.idi.idatt.model.tile.PortalAction;
import edu.ntnu.idi.idatt.model.tile.SlideAction;
import java.lang.reflect.Method;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LadderGameBoardFileHandlerGsonTest {
  
  private LadderGameBoardFileHandlerGson fileHandler;
  private static final String TEST_BOARD_NAME = "Test Board";
  private static final String TEST_BOARD_DESCRIPTION = "Test Description";
  private static final int[] TEST_BOARD_DIMENSIONS = {5, 5};
  private static final String TEST_BOARD_BACKGROUND = "test_background.png";
  private static final String TEST_BOARD_PATTERN = "test_pattern.png";
  
  @BeforeEach
  void setUp() {
    fileHandler = new LadderGameBoardFileHandlerGson();
  }
  
  @Test
  @DisplayName("Test serializing and deserializing a board with no special tiles")
  void testSerializeAndDeserializeBasicBoard() throws Exception {
    LadderGameBoard board = new LadderGameBoard(
    TEST_BOARD_NAME,
    TEST_BOARD_DESCRIPTION,
    TEST_BOARD_DIMENSIONS,
    TEST_BOARD_BACKGROUND,
    TEST_BOARD_PATTERN
    );
    
    // Get private methods using reflection
    Method serializeMethod = LadderGameBoardFileHandlerGson.class.getDeclaredMethod("serializeBoard", LadderGameBoard.class);
    Method deserializeMethod = LadderGameBoardFileHandlerGson.class.getDeclaredMethod("deserializeBoard", String.class);
    serializeMethod.setAccessible(true);
    deserializeMethod.setAccessible(true);
    
    JsonObject boardJson = (JsonObject) serializeMethod.invoke(fileHandler, board);
    String jsonString = boardJson.toString();
    Board deserializedBoard = (Board) deserializeMethod.invoke(fileHandler, jsonString);
    
    assertNotNull(deserializedBoard);
    assertTrue(deserializedBoard instanceof LadderGameBoard);
    LadderGameBoard ladderGameBoard = (LadderGameBoard) deserializedBoard;
    assertEquals(TEST_BOARD_NAME, ladderGameBoard.getName());
    assertEquals(TEST_BOARD_DESCRIPTION, ladderGameBoard.getDescription());
    assertArrayEquals(TEST_BOARD_DIMENSIONS, ladderGameBoard.getRowsAndColumns());
    assertEquals(TEST_BOARD_BACKGROUND, ladderGameBoard.getBackground());
    assertEquals(TEST_BOARD_PATTERN, ladderGameBoard.getPattern());
    assertEquals(26, ladderGameBoard.getTiles().size());
  }
  
  @Test
  @DisplayName("Test serializing and deserializing a board with special tiles")
  void testSerializeAndDeserializeBoardWithSpecialTiles() throws Exception {
    LadderGameBoard board = new LadderGameBoard(
    TEST_BOARD_NAME,
    TEST_BOARD_DESCRIPTION,
    TEST_BOARD_DIMENSIONS,
    TEST_BOARD_BACKGROUND,
    TEST_BOARD_PATTERN
    );
    
    board.addTile(new LadderGameTile(0, new int[]{0, 0}, 1));
    board.addTile(new LadderGameTile(1, new int[]{1, 0}, 2,
    new LadderAction("1R_1U_ladder", 3, "Climb up!")));
    board.addTile(new LadderGameTile(2, new int[]{2, 0}, 3,
    new SlideAction("1R_1D_slide", 4, "Slide down!")));
    board.addTile(new LadderGameTile(3, new int[]{3, 0}, 4,
    new PortalAction("1R_1U_portal", 5, "Teleport!")));
    
    Method serializeMethod = LadderGameBoardFileHandlerGson.class.getDeclaredMethod("serializeBoard", LadderGameBoard.class);
    Method deserializeMethod = LadderGameBoardFileHandlerGson.class.getDeclaredMethod("deserializeBoard", String.class);
    serializeMethod.setAccessible(true);
    deserializeMethod.setAccessible(true);
    
    JsonObject boardJson = (JsonObject) serializeMethod.invoke(fileHandler, board);
    String jsonString = boardJson.toString();
    Board deserializedBoard = (Board) deserializeMethod.invoke(fileHandler, jsonString);
    
    assertNotNull(deserializedBoard);
    assertInstanceOf(LadderGameBoard.class, deserializedBoard);
    LadderGameBoard ladderGameBoard = (LadderGameBoard) deserializedBoard;
    assertEquals(26, ladderGameBoard.getTiles().size());

    LadderGameTile ladderTile = (LadderGameTile) ladderGameBoard.getTiles().get(1);
    assertInstanceOf(LadderAction.class, ladderTile.getLandAction());
    assertEquals("Climb up!", ladderTile.getLandAction().getDescription());
    
    LadderGameTile slideTile = (LadderGameTile) ladderGameBoard.getTiles().get(2);
    assertInstanceOf(SlideAction.class, slideTile.getLandAction());
    assertEquals("Slide down!", slideTile.getLandAction().getDescription());
    
    LadderGameTile portalTile = (LadderGameTile) ladderGameBoard.getTiles().get(3);
    assertInstanceOf(PortalAction.class, portalTile.getLandAction());
    assertEquals("Teleport!", portalTile.getLandAction().getDescription());
  }
  
  @Test
  @DisplayName("Test deserializing null JSON string")
  void testDeserializeNullJsonString() throws Exception {
    // Get private method using reflection
    Method deserializeMethod = LadderGameBoardFileHandlerGson.class.getDeclaredMethod("deserializeBoard", String.class);
    deserializeMethod.setAccessible(true);
    
    Board deserializedBoard = (Board) deserializeMethod.invoke(fileHandler, (String) null);
    assertNull(deserializedBoard);
  }
  
  @Test
  @DisplayName("Test deserializing empty JSON string")
  void testDeserializeEmptyJsonString() throws Exception {
    Method deserializeMethod = LadderGameBoardFileHandlerGson.class.getDeclaredMethod("deserializeBoard", String.class);
    deserializeMethod.setAccessible(true);
    
    Board deserializedBoard = (Board) deserializeMethod.invoke(fileHandler, "");
    assertNull(deserializedBoard);
  }
  
  @Test
  @DisplayName("Test deserializing invalid JSON string")
  void testDeserializeInvalidJsonString() throws Exception {
    Method deserializeMethod = LadderGameBoardFileHandlerGson.class.getDeclaredMethod("deserializeBoard", String.class);
    deserializeMethod.setAccessible(true);
    
    assertThrows(Exception.class, () -> deserializeMethod.invoke(fileHandler, "invalid json content"));
  }
  
  @Test
  @DisplayName("Test serializing null board")
  void testSerializeNullBoard() throws Exception {
    Method serializeMethod = LadderGameBoardFileHandlerGson.class.getDeclaredMethod("serializeBoard", LadderGameBoard.class);
    serializeMethod.setAccessible(true);
    
    JsonObject boardJson = (JsonObject) serializeMethod.invoke(fileHandler, (LadderGameBoard) null);
    assertNull(boardJson);
  }
} 