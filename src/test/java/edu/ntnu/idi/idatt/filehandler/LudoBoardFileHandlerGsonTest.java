package edu.ntnu.idi.idatt.filehandler;

import static org.junit.jupiter.api.Assertions.*;

import com.google.gson.JsonObject;
import edu.ntnu.idi.idatt.model.board.Board;
import edu.ntnu.idi.idatt.model.board.LudoGameBoard;
import edu.ntnu.idi.idatt.model.tile.LudoTile;
import java.lang.reflect.Method;
import javafx.scene.paint.Color;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LudoBoardFileHandlerGsonTest {

  private LudoBoardFileHandlerGson fileHandler;
  private static final String TEST_BOARD_NAME = "Test Ludo Board";
  private static final String TEST_BOARD_DESCRIPTION = "Test Ludo Description";
  private static final int TEST_BOARD_SIZE = 15;
  private static final String TEST_BOARD_BACKGROUND = "test_ludo_background.png";
  private static final Color[] TEST_COLORS = {
      Color.RED,
      Color.BLUE,
      Color.GREEN,
      Color.YELLOW
  };

  @BeforeEach
  void setUp() {
    fileHandler = new LudoBoardFileHandlerGson();
  }

  @Test
  @DisplayName("Test serializing and deserializing a basic ludo board")
  void testSerializeAndDeserializeBasicBoard() throws Exception {
    LudoGameBoard board = new LudoGameBoard(
        TEST_BOARD_NAME,
        TEST_BOARD_DESCRIPTION,
        TEST_BOARD_BACKGROUND,
        TEST_BOARD_SIZE,
        TEST_COLORS
    );

    board.addTile(new LudoTile(1, new int[]{0, 0}, 2, "START"));
    board.addTile(new LudoTile(2, new int[]{1, 0}, 3, "TRACK"));
    board.addTile(new LudoTile(3, new int[]{2, 0}, 4, "TRACK"));
    board.addTile(new LudoTile(4, new int[]{3, 0}, 5, "FINISH"));

    Method serializeMethod = LudoBoardFileHandlerGson.class.getDeclaredMethod("serializeBoard", LudoGameBoard.class);
    Method deserializeMethod = LudoBoardFileHandlerGson.class.getDeclaredMethod("deserializeBoard", String.class);
    serializeMethod.setAccessible(true);
    deserializeMethod.setAccessible(true);

    JsonObject boardJson = (JsonObject) serializeMethod.invoke(fileHandler, board);
    String jsonString = boardJson.toString();
    Board deserializedBoard = (Board) deserializeMethod.invoke(fileHandler, jsonString);

    assertNotNull(deserializedBoard);
    assertInstanceOf(LudoGameBoard.class, deserializedBoard);
    LudoGameBoard ludoGameBoard = (LudoGameBoard) deserializedBoard;
    assertEquals(TEST_BOARD_NAME, ludoGameBoard.getName());
    assertEquals(TEST_BOARD_DESCRIPTION, ludoGameBoard.getDescription());
    assertEquals(TEST_BOARD_SIZE, ludoGameBoard.getBoardSize());
    assertEquals(TEST_BOARD_BACKGROUND, ludoGameBoard.getBackground());
    assertArrayEquals(TEST_COLORS, ludoGameBoard.getColors());
    assertEquals(225, ludoGameBoard.getTiles().size());

    LudoTile startTile = (LudoTile) ludoGameBoard.getTiles().get(0);
    assertEquals("START", startTile.getType());
    LudoTile trackTile = (LudoTile) ludoGameBoard.getTiles().get(1);
    assertEquals("TRACK", trackTile.getType());
    LudoTile finishTile = (LudoTile) ludoGameBoard.getTiles().get(3);
    assertEquals("FINISH", finishTile.getType());
  }

  @Test
  @DisplayName("Test serializing and deserializing a ludo board with player indexes")
  void testSerializeAndDeserializeBoardWithPlayerIndexes() throws Exception {
    LudoGameBoard board = new LudoGameBoard(
        TEST_BOARD_NAME,
        TEST_BOARD_DESCRIPTION,
        TEST_BOARD_BACKGROUND,
        TEST_BOARD_SIZE,
        TEST_COLORS
    );

    // Set player indexes using reflection
    setIntArrayField(board, "playerStartIndexes", new int[]{0, 1, 2, 3});
    setIntArrayField(board, "playerTrackStartIndexes", new int[]{4, 5, 6, 7});
    setIntArrayField(board, "playerFinishStartIndexes", new int[]{8, 9, 10, 11});
    setIntArrayField(board, "playerFinishIndexes", new int[]{12, 13, 14, 15});
    setIntField(board, "startAreaSize", 5);
    setIntField(board, "totalTrackTileCount", 60);

    Method serializeMethod = LudoBoardFileHandlerGson.class.getDeclaredMethod("serializeBoard", LudoGameBoard.class);
    Method deserializeMethod = LudoBoardFileHandlerGson.class.getDeclaredMethod("deserializeBoard", String.class);
    serializeMethod.setAccessible(true);
    deserializeMethod.setAccessible(true);

    JsonObject boardJson = (JsonObject) serializeMethod.invoke(fileHandler, board);
    String jsonString = boardJson.toString();
    Board deserializedBoard = (Board) deserializeMethod.invoke(fileHandler, jsonString);

    assertNotNull(deserializedBoard);
    assertInstanceOf(LudoGameBoard.class, deserializedBoard);
    LudoGameBoard ludoGameBoard = (LudoGameBoard) deserializedBoard;

    assertArrayEquals(new int[]{0, 1, 2, 3}, ludoGameBoard.getPlayerStartIndexes());
    assertArrayEquals(new int[]{4, 5, 6, 7}, ludoGameBoard.getPlayerTrackStartIndexes());
    assertArrayEquals(new int[]{8, 9, 10, 11}, ludoGameBoard.getPlayerFinishStartIndexes());
    assertArrayEquals(new int[]{12, 13, 14, 15}, ludoGameBoard.getPlayerFinishIndexes());
    assertEquals(5, ludoGameBoard.getStartAreaSize());
    assertEquals(60, ludoGameBoard.getTotalTrackTileCount());
  }

  @Test
  @DisplayName("Test deserializing null JSON string")
  void testDeserializeNullJsonString() throws Exception {
    Method deserializeMethod = LudoBoardFileHandlerGson.class.getDeclaredMethod("deserializeBoard", String.class);
    deserializeMethod.setAccessible(true);

    Board deserializedBoard = (Board) deserializeMethod.invoke(fileHandler, (String) null);
    assertNull(deserializedBoard);
  }

  @Test
  @DisplayName("Test deserializing empty JSON string")
  void testDeserializeEmptyJsonString() throws Exception {
    Method deserializeMethod = LudoBoardFileHandlerGson.class.getDeclaredMethod("deserializeBoard", String.class);
    deserializeMethod.setAccessible(true);

    Board deserializedBoard = (Board) deserializeMethod.invoke(fileHandler, "");
    assertNull(deserializedBoard);
  }

  @Test
  @DisplayName("Test deserializing invalid JSON string")
  void testDeserializeInvalidJsonString() throws Exception {
    Method deserializeMethod = LudoBoardFileHandlerGson.class.getDeclaredMethod("deserializeBoard", String.class);
    deserializeMethod.setAccessible(true);

    assertThrows(Exception.class, () -> deserializeMethod.invoke(fileHandler, "invalid json content"));
  }

  @Test
  @DisplayName("Test serializing null board")
  void testSerializeNullBoard() throws Exception {
    Method serializeMethod = LudoBoardFileHandlerGson.class.getDeclaredMethod("serializeBoard", LudoGameBoard.class);
    serializeMethod.setAccessible(true);

    JsonObject boardJson = (JsonObject) serializeMethod.invoke(fileHandler, (LudoGameBoard) null);
    assertNull(boardJson);
  }
  
  // Helper method for reflection
  private void setIntArrayField(LudoGameBoard board, String fieldName, int[] value) throws Exception {
    java.lang.reflect.Field field = LudoGameBoard.class.getDeclaredField(fieldName);
    field.setAccessible(true);
    field.set(board, value);
  }

  // Helper method for reflection
  private void setIntField(LudoGameBoard board, String fieldName, int value) throws Exception {
    java.lang.reflect.Field field = LudoGameBoard.class.getDeclaredField(fieldName);
    field.setAccessible(true);
    field.setInt(board, value);
  }
} 