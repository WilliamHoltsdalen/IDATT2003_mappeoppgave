package edu.ntnu.idi.idatt.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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

/**
 * <h3>FileHandler implementation for Board objects.</h3>
 *
 * <p>This class provides methods for reading and writing Board objects to and from JSON files.
 * It uses the Gson library for serialization and deserialization.
 */
public class BoardFileHandlerGson implements FileHandler<Board> {
  private static final String NAME_PROPERTY = "name";
  private static final String DESCRIPTION_PROPERTY = "description";
  private static final String TILES_PROPERTY = "tiles";
  private static final String TILE_ID_PROPERTY = "id";
  private static final String TILE_NEXT_TILE_ID_PROPERTY = "nextTileId";
  private static final String TILE_ACTION_PROPERTY = "action";
  private static final String TILE_ACTION_TYPE_PROPERTY = "type";
  private static final String TILE_ACTION_DESTINATION_TILE_ID_PROPERTY = "destinationTileId";
  private static final String TILE_ACTION_DESCRIPTION_PROPERTY = "description";


  /**
   * Reads a file at the given path and returns a list of Board objects. If there is only one board
   * in the file, it will be returned as a list with one element. If the file does not exist or
   * cannot be read, an empty list will be returned.
   *
   * @param path The path to the file.
   * @return A list of Board objects or null if the file does not exist or cannot be read.
   */
  @Override
  public List<Board> readFile(String path) {
    try {
      String jsonString = FileUtils.readFileToString(new File(path), StandardCharsets.UTF_8);
      Board board = deserializeBoard(jsonString); // Todo: Support multiple boards
      return List.of(board);
    } catch (IOException e) {
      return List.of();
    }
  }

  /**
   * Writes a list of Board objects to a JSON file at the given path.
   *
   * @param path The path to the file.
   * @param boards The list of Board objects to write.
   * @throws IOException If an error occurs while writing to the file.
   */
  @Override
  public void writeFile(String path, List<Board> boards) throws IOException {
    if (boards == null || boards.isEmpty()) {
      throw new IllegalArgumentException("Board list is null or empty.");
    }
    JsonObject boardJson = serializeBoard(boards.getFirst()); // Todo: Support multiple boards
    if (boardJson == null) {
      // Todo: Handle null boardJson, perhaps by throwing a ( custom ? ) exception
      return;
    }

    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    String prettyJson = gson.toJson(boardJson);

    FileUtils.writeStringToFile(new File(path), prettyJson, StandardCharsets.UTF_8, false);
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
      JsonObject actionJson = new JsonObject();

      tileJson.addProperty(TILE_ID_PROPERTY, tile.getTileId());
      tileJson.addProperty(TILE_NEXT_TILE_ID_PROPERTY, tile.getNextTileId());

      if (tile.getLandAction() != null) {
        actionJson.addProperty(TILE_ACTION_TYPE_PROPERTY, tile.getLandAction().getClass()
            .getSimpleName());
        actionJson.addProperty(TILE_ACTION_DESTINATION_TILE_ID_PROPERTY, tile.getLandAction()
            .getDestinationTileId());
        actionJson.addProperty(TILE_ACTION_DESCRIPTION_PROPERTY, tile.getLandAction()
            .getDescription());
        tileJson.add(TILE_ACTION_PROPERTY, actionJson);
      }
      tilesJsonArray.add(tileJson);
    });

    JsonObject boardJson = new JsonObject();
    boardJson.add(NAME_PROPERTY, new JsonPrimitive(board.getName()));
    boardJson.add(DESCRIPTION_PROPERTY, new JsonPrimitive(board.getDescription()));
    boardJson.add(TILES_PROPERTY, tilesJsonArray);
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

    JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();
    String boardName = jsonObject.get(NAME_PROPERTY).getAsString();
    String boardDescription = jsonObject.get(DESCRIPTION_PROPERTY).getAsString();
    Board board = new Board(boardName, boardDescription);

    JsonArray tilesJsonArray = jsonObject.getAsJsonArray(TILES_PROPERTY);
    tilesJsonArray.forEach(tileJson -> {
      JsonObject tileJsonObject = tileJson.getAsJsonObject();
      int tileId = 0;
      int nextTileId = 0;
      TileAction tileAction = null;

      try {
        tileId = tileJsonObject.get(TILE_ID_PROPERTY).getAsInt();
        nextTileId = tileJsonObject.get(TILE_NEXT_TILE_ID_PROPERTY).getAsInt();
        JsonObject actionJsonObject = tileJsonObject.getAsJsonObject(TILE_ACTION_PROPERTY);

        int destinationTileId = actionJsonObject.get(TILE_ACTION_DESTINATION_TILE_ID_PROPERTY)
            .getAsInt();
        String actionDescription = actionJsonObject.get(TILE_ACTION_DESCRIPTION_PROPERTY)
            .getAsString();
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
