package edu.ntnu.idi.idatt.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import edu.ntnu.idi.idatt.model.Board;
import edu.ntnu.idi.idatt.model.Tile;
import edu.ntnu.idi.idatt.utils.interfaces.FileHandler;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.FileUtils;

public class BoardFileHandler implements FileHandler<Board> {

  @Override
  public List<Board> readFile(String path) throws IOException {
    List<Board> boards = new ArrayList<>();

    try {
      String jsonString = FileUtils.readFileToString(new File(
          path), StandardCharsets.UTF_8);

      Board board = deserializeBoard(jsonString);
      boards.add(board);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return boards;
  }

  @Override
  public void writeFile(String path, List<Board> board) throws IOException {
    // Not yet implemented
  }

  /**
   * Serializes a Board object to a JSON string.
   *
   * @param board The Board object to serialize.
   * @return A JSON string representation of the Board object.
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
      try {
        JsonObject tileJsonObject = tileJson.getAsJsonObject();
        int tileId = tileJsonObject.get("id").getAsInt();
        int nextTileId = tileJsonObject.get("nextTile").getAsInt();
        board.addTile(new Tile(tileId, nextTileId));
      } catch (UnsupportedOperationException e) {
        e.printStackTrace();
      }
    });

    return board;
  }
}
