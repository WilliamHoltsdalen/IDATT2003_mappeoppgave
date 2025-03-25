package edu.ntnu.idi.idatt.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import edu.ntnu.idi.idatt.model.Board;
import edu.ntnu.idi.idatt.model.LadderAction;
import edu.ntnu.idi.idatt.model.Tile;
import edu.ntnu.idi.idatt.model.interfaces.TileAction;
import edu.ntnu.idi.idatt.utils.interfaces.FileHandler;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.apache.commons.io.FileUtils;

public class BoardFileHandlerGson implements FileHandler<Board> {

  /**
   * Reads a file at the given path and returns a list of Board objects. If there is only one board
   * in the file, it will be returned as a list with one element. If the file does not exist or
   * cannot be read, an empty list will be returned.
   *
   * @param path The path to the file.
   * @return A list of Board objects or null if the file does not exist or cannot be read.
   * @throws IOException If an error occurs while reading the file.
   */
  @Override
  public List<Board> readFile(String path) throws IOException {
    try {
      String jsonString = FileUtils.readFileToString(new File(path), StandardCharsets.UTF_8);
      Board board = deserializeBoard(jsonString);
      return List.of(board);
    } catch (IOException e) {
      return List.of();
    }
  }

  @Override
  public void writeFile(String path, List<Board> board) throws IOException {
    // Not yet implemented
  }

  /**
   * Serializes a Board object to a JSON string.
   *
   * @param board The Board object to serialize.
   * @return A JSON string representation of the Board object or null if the given Board object is
   *         null.
   */
  private JsonObject serializeBoard(Board board) {
    if (board == null) {
      return null;
    }

    JsonArray tilesJsonArray = new JsonArray();

    board.getTiles().forEach(tile -> {
      JsonObject tileJson = new JsonObject();
      tileJson.addProperty("id", tile.getTileId());
      tileJson.addProperty("nextTile", tile.getNextTileId());
      tilesJsonArray.add(tileJson);
    });

    JsonObject boardJson = new JsonObject();
    boardJson.add("name", new JsonPrimitive("Board name"));
    boardJson.add("description", new JsonPrimitive("Board description"));
    boardJson.add("tiles", tilesJsonArray);
    return boardJson;
  }

  /**
   * Deserializes a JSON string to a Board object.
   *
   * @param jsonString The JSON string to deserialize.
   * @return A Board object representing the deserialized JSON string.
   */
  private Board deserializeBoard(String jsonString) {
    if (jsonString == null || jsonString.isEmpty()) {
      return null;
    }

    JsonObject tilesJson = JsonParser.parseString(jsonString).getAsJsonObject();
    JsonArray tilesJsonArray = tilesJson.getAsJsonArray("tiles");
    Board board = new Board();

    tilesJsonArray.forEach(tileJson -> {
      JsonObject tileJsonObject = tileJson.getAsJsonObject();
      int tileId = 0;
      int nextTileId = 0;
      TileAction tileAction = null;

      try {
        tileId = tileJsonObject.get("id").getAsInt();
        nextTileId = tileJsonObject.get("nextTile").getAsInt();
        JsonObject actionJsonObject = tileJsonObject.getAsJsonObject("action");

        int destinationTileId = actionJsonObject.get("destinationTileId").getAsInt();
        String actionDescription = actionJsonObject.get("description").getAsString();
        tileAction = new LadderAction(destinationTileId, actionDescription);
      } catch (NullPointerException e) {
        // Todo: Handle null pointer exception if any of the tile properties are missing
      }

      if (tileAction != null) {
        board.addTile(new Tile(tileId, nextTileId, tileAction));
        return;
      }
      board.addTile(new Tile(tileId, nextTileId));
    });

    return board;
  }
}
