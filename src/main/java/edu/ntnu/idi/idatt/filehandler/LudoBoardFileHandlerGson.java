package edu.ntnu.idi.idatt.filehandler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import edu.ntnu.idi.idatt.model.board.Board;
import edu.ntnu.idi.idatt.model.board.LudoGameBoard;
import edu.ntnu.idi.idatt.model.tile.LudoTile;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import javafx.scene.paint.Color;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * LudoBoardFileHandlerGson.
 *
 * <p>This class implements the {@link FileHandler} interface for {@link Board} objects,
 * specifically tailored for {@link LudoGameBoard} instances. It uses the Gson library to serialize
 * Ludo game boards to JSON format and deserialize them from JSON files.</p>
 *
 * <p>The JSON structure includes properties for the board's name, description, size,
 * background image, player colors, various tile index arrays (start, track start, finish start,
 * finish), start area size, total track tile count, and a list of tiles with their respective IDs,
 * coordinates, next tile IDs, and types.</p>
 *
 * <p>Reflection is used internally to set certain fields (like index arrays and integer
 * properties) on the {@code LudoGameBoard} object during deserialization to avoid overly complex
 * constructor logic or numerous setter methods for these internal state details.</p>
 *
 * @see FileHandler
 * @see LudoGameBoard
 * @see Board
 * @see LudoTile
 * @see com.google.gson.Gson
 */
public class LudoBoardFileHandlerGson implements FileHandler<Board> {

  private static final Logger logger = LoggerFactory.getLogger(LudoBoardFileHandlerGson.class);

  private static final String NAME_PROPERTY = "name";
  private static final String DESCRIPTION_PROPERTY = "description";
  private static final String BOARD_SIZE_PROPERTY = "boardSize";
  private static final String BACKGROUND_PROPERTY = "background";
  private static final String TILES_PROPERTY = "tiles";
  private static final String TILE_ID_PROPERTY = "id";
  private static final String TILE_COORDINATES_PROPERTY = "coordinates";
  private static final String TILE_NEXT_TILE_ID_PROPERTY = "nextTileId";
  private static final String TILE_TYPE_PROPERTY = "type";
  private static final String COLORS_PROPERTY = "colors";
  private static final String PLAYER_START_INDEXES_PROPERTY = "playerStartIndexes";
  private static final String PLAYER_TRACK_START_INDEXES_PROPERTY = "playerTrackStartIndexes";
  private static final String PLAYER_FINISH_START_INDEXES_PROPERTY = "playerFinishStartIndexes";
  private static final String PLAYER_FINISH_INDEXES_PROPERTY = "playerFinishIndexes";
  private static final String START_AREA_SIZE_PROPERTY = "startAreaSize";
  private static final String TOTAL_TRACK_TILE_COUNT_PROPERTY = "totalTrackTileCount";

  /**
   * Reads a Ludo game board configuration from a JSON file at the specified path.
   *
   * @param path The path to the JSON file.
   * @return A {@link Board} (specifically a {@link LudoGameBoard}) object deserialized from the
   *     file.
   * @throws IOException if an error occurs during file reading or parsing.
   */
  @Override
  public Board readFile(String path) throws IOException {
    logger.debug("Reading ludo board form file {}", path);
    try {
      String jsonString = FileUtils.readFileToString(new File(path), StandardCharsets.UTF_8);
      return deserializeBoard(jsonString);
    } catch (IOException e) {
      logger.error("Failed to read ludo board.");
      throw new IOException("Could not read board from file: " + path);
    }
  }

  /**
   * Writes a list of Ludo game boards to a JSON file at the specified path.
   *
   * @param path   The path to the JSON file.
   * @param boards A list containing the {@link Board} (expected to be a {@link LudoGameBoard}) to
   *               be written. Only the first board in the list is processed.
   * @throws IOException              if an error occurs during file writing, such as if a file with
   *                                  the same name already exists or if there's a general I/O
   *                                  issue.
   * @throws IllegalArgumentException if the provided list of boards is null or empty.
   */
  @Override
  public void writeFile(String path, List<Board> boards) throws IOException {
    if (boards == null || boards.isEmpty()) {
      logger.error("Boards parameter is null or empty");
      throw new IllegalArgumentException("Board list is null or empty.");
    }
    JsonObject boardJson = serializeBoard((LudoGameBoard) boards.getFirst());
    if (boardJson == null) {
      logger.debug("Could not serialize board. BoardJson is null");
      return;
    }
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    String prettyJson = gson.toJson(boardJson);
    File file = new File(path);
    if (!file.createNewFile()) {
      logger.error("Could not create file {}, a file with the same name already exists", path);
      throw new IOException("A file with the same name already exists");
    }
    FileUtils.writeStringToFile(file, prettyJson, StandardCharsets.UTF_8, false);
    logger.info("Successfully saved ludo board to {}", path);
  }

  /**
   * Serializes a Ludo game board to a JSON object.
   *
   * @param board The {@link LudoGameBoard} to serialize.
   * @return A {@link JsonObject} representing the serialized board, or {@code null} if the input
   *     board is {@code null}.
   */
  private JsonObject serializeBoard(LudoGameBoard board) {
    if (board == null) {
      return null;
    }
    JsonArray tilesJsonArray = new JsonArray();
    board.getTiles().forEach(tile -> {
      LudoTile ludoTile = (LudoTile) tile;
      JsonObject tileJson = new JsonObject();
      tileJson.addProperty(TILE_ID_PROPERTY, ludoTile.getTileId());
      JsonArray coordinatesArray = new JsonArray();
      coordinatesArray.add(ludoTile.getCoordinates()[0]);
      coordinatesArray.add(ludoTile.getCoordinates()[1]);
      tileJson.add(TILE_COORDINATES_PROPERTY, coordinatesArray);
      tileJson.addProperty(TILE_NEXT_TILE_ID_PROPERTY, ludoTile.getNextTileId());
      tileJson.addProperty(TILE_TYPE_PROPERTY, ludoTile.getType());
      tilesJsonArray.add(tileJson);
    });
    JsonArray colorsArray = new JsonArray();
    if (board.getColors() != null) {
      for (Color color : board.getColors()) {
        colorsArray.add(color != null ? color.toString() : "");
      }
    }
    JsonArray playerStartIndexesArray = intArrayToJson(board.getPlayerStartIndexes());
    JsonArray playerTrackStartIndexesArray = intArrayToJson(board.getPlayerTrackStartIndexes());
    JsonArray playerFinishStartIndexesArray = intArrayToJson(board.getPlayerFinishStartIndexes());
    JsonArray playerFinishIndexesArray = intArrayToJson(board.getPlayerFinishIndexes());
    JsonObject boardJson = new JsonObject();
    boardJson.add(NAME_PROPERTY, new JsonPrimitive(board.getName()));
    boardJson.add(DESCRIPTION_PROPERTY, new JsonPrimitive(board.getDescription()));
    boardJson.add(BOARD_SIZE_PROPERTY, new JsonPrimitive(board.getBoardSize()));
    boardJson.add(BACKGROUND_PROPERTY, new JsonPrimitive(board.getBackground()));
    boardJson.add(COLORS_PROPERTY, colorsArray);
    boardJson.add(PLAYER_START_INDEXES_PROPERTY, playerStartIndexesArray);
    boardJson.add(PLAYER_TRACK_START_INDEXES_PROPERTY, playerTrackStartIndexesArray);
    boardJson.add(PLAYER_FINISH_START_INDEXES_PROPERTY, playerFinishStartIndexesArray);
    boardJson.add(PLAYER_FINISH_INDEXES_PROPERTY, playerFinishIndexesArray);
    boardJson.add(START_AREA_SIZE_PROPERTY, new JsonPrimitive(board.getStartAreaSize()));
    boardJson.add(TOTAL_TRACK_TILE_COUNT_PROPERTY,
        new JsonPrimitive(board.getTotalTrackTileCount()));
    boardJson.add(TILES_PROPERTY, tilesJsonArray);
    return boardJson;
  }

  /**
   * Converts an integer array to a JSON array.
   *
   * @param arr The integer array to convert.
   * @return A {@link JsonArray} containing the elements of the input array.
   */
  private JsonArray intArrayToJson(int[] arr) {
    JsonArray array = new JsonArray();
    if (arr != null) {
      for (int v : arr) {
        array.add(v);
      }
    }
    return array;
  }

  /**
   * Converts a JSON array to an integer array.
   *
   * @param arr The {@link JsonArray} to convert.
   * @return An integer array containing the elements from the input JSON array.
   */
  private int[] jsonToIntArray(JsonArray arr) {
    int[] result = new int[arr.size()];
    for (int i = 0; i < arr.size(); i++) {
      result[i] = arr.get(i).getAsInt();
    }
    return result;
  }

  /**
   * Deserializes a Ludo game board from a JSON string.
   *
   * @param jsonString The JSON string representing the Ludo game board.
   * @return A {@link Board} (specifically a {@link LudoGameBoard}) deserialized from the JSON
   *     string, or {@code null} if the string is null or empty.
   */
  private Board deserializeBoard(String jsonString) {
    if (jsonString == null || jsonString.isEmpty()) {
      logger.debug("Failed to deserialize. JSON string is null or empty");
      return null;
    }
    JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();
    String boardName = jsonObject.get(NAME_PROPERTY).getAsString();
    String boardDescription = jsonObject.get(DESCRIPTION_PROPERTY).getAsString();
    int boardSize = jsonObject.get(BOARD_SIZE_PROPERTY).getAsInt();
    String boardBackground = jsonObject.get(BACKGROUND_PROPERTY).getAsString();
    Color[] colors = null;
    if (jsonObject.has(COLORS_PROPERTY)) {
      JsonArray colorsArray = jsonObject.getAsJsonArray(COLORS_PROPERTY);
      colors = new Color[colorsArray.size()];
      for (int i = 0; i < colorsArray.size(); i++) {
        String colorStr = colorsArray.get(i).getAsString();
        colors[i] = colorStr.isEmpty() ? null : Color.web(colorStr);
      }
    }
    LudoGameBoard board = new LudoGameBoard(boardName, boardDescription, boardBackground, boardSize,
        colors);
    if (jsonObject.has(PLAYER_START_INDEXES_PROPERTY)) {
      board.setPlayerStartIndexes(
          jsonToIntArray(jsonObject.getAsJsonArray(PLAYER_START_INDEXES_PROPERTY)));
    }
    if (jsonObject.has(PLAYER_TRACK_START_INDEXES_PROPERTY)) {
      board.setPlayerTrackStartIndexes(
          jsonToIntArray(jsonObject.getAsJsonArray(PLAYER_TRACK_START_INDEXES_PROPERTY)));
    }
    if (jsonObject.has(PLAYER_FINISH_START_INDEXES_PROPERTY)) {
      board.setPlayerFinishStartIndexes(
          jsonToIntArray(jsonObject.getAsJsonArray(PLAYER_FINISH_START_INDEXES_PROPERTY)));
    }
    if (jsonObject.has(PLAYER_FINISH_INDEXES_PROPERTY)) {
      board.setPlayerFinishIndexes(
          jsonToIntArray(jsonObject.getAsJsonArray(PLAYER_FINISH_INDEXES_PROPERTY)));
    }
    if (jsonObject.has(START_AREA_SIZE_PROPERTY)) {
      board.setStartAreaSize(jsonObject.get(START_AREA_SIZE_PROPERTY).getAsInt());
    }
    if (jsonObject.has(TOTAL_TRACK_TILE_COUNT_PROPERTY)) {
      board.setTotalTrackTileCount(
          jsonObject.get(TOTAL_TRACK_TILE_COUNT_PROPERTY).getAsInt());
    }
    JsonArray tilesJsonArray = jsonObject.getAsJsonArray(TILES_PROPERTY);
    for (int i = 0; i < tilesJsonArray.size(); i++) {
      JsonObject tileJsonObject = tilesJsonArray.get(i).getAsJsonObject();
      int tileId = tileJsonObject.get(TILE_ID_PROPERTY).getAsInt();
      JsonArray coordinatesArray = tileJsonObject.get(TILE_COORDINATES_PROPERTY).getAsJsonArray();
      int[] coordinates = new int[coordinatesArray.size()];
      for (int j = 0; j < coordinatesArray.size(); j++) {
        coordinates[j] = coordinatesArray.get(j).getAsInt();
      }
      int nextTileId = tileJsonObject.get(TILE_NEXT_TILE_ID_PROPERTY).getAsInt();
      String type = tileJsonObject.get(TILE_TYPE_PROPERTY).getAsString();
      board.addTile(new LudoTile(tileId, coordinates, nextTileId, type));
    }
    return board;
  }
} 