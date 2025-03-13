package edu.ntnu.idi.idatt.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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

    // Open json file and read contents
    try {
      // Gson gson = new Gson();
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

  }

  /**
   * Serializes a Board object to a JSON string.
   *
   * @param board The Board object to serialize.
   * @return A JSON string representation of the Board object.
   */
  private JsonObject serializeBoard(Board board) {
    JsonArray tilesJsonArray = new JsonArray();

    for (int i = 0; i < board.getTileCount(); i++) {
      JsonObject tileJson = new JsonObject();
      tileJson.addProperty("id", board.getTile(i).toString());
      tileJson.addProperty("nextTile", board.getTile(i+1).toString());
      tilesJsonArray.add(tileJson);
    }

    JsonObject boardJson = new JsonObject();
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
    JsonObject tilesJson = JsonParser.parseString(jsonString).getAsJsonObject();
    JsonArray tilesJsonArray = tilesJson.getAsJsonArray("tiles");
    Board board = new Board();

    for (int i = 0; i < tilesJsonArray.size(); i++) {
      try {
        JsonObject tileJson = tilesJsonArray.get(i).getAsJsonObject();
        int tileId = tileJson.get("id").getAsInt();
        int nextTileId = tileJson.get("nextTile").getAsInt();
        board.addTile(new Tile(tileId, nextTileId));
      } catch (UnsupportedOperationException e) {
        e.printStackTrace();

      }
    }

    return board;
  }
}
