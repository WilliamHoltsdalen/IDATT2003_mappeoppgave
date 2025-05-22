package edu.ntnu.idi.idatt.filehandler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import edu.ntnu.idi.idatt.model.board.Board;
import edu.ntnu.idi.idatt.model.board.LadderGameBoard;
import edu.ntnu.idi.idatt.model.tile.LadderAction;
import edu.ntnu.idi.idatt.model.tile.LadderGameTile;
import edu.ntnu.idi.idatt.model.tile.PortalAction;
import edu.ntnu.idi.idatt.model.tile.SlideAction;
import edu.ntnu.idi.idatt.model.tile.TileAction;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * FileHandler implementation for Board objects.
 *
 * <p>This class provides methods for reading and writing Board objects to and from JSON files.
 * It uses the Gson library for serialization and deserialization.
 */
public class LadderGameBoardFileHandlerGson implements FileHandler<Board> {

  private static final Logger logger = LoggerFactory.getLogger(LadderGameBoardFileHandlerGson.class);

  private static final String NAME_PROPERTY = "name";
  private static final String DESCRIPTION_PROPERTY = "description";
  private static final String ROWS_PROPERTY = "rows";
  private static final String COLUMNS_PROPERTY = "columns";
  private static final String BACKGROUND_PROPERTY = "background";
  private static final String PATTERN_PROPERTY = "pattern";
  private static final String TILES_PROPERTY = "tiles";
  private static final String TILE_ID_PROPERTY = "id";
  private static final String TILE_COORDINATES_PROPERTY = "coordinates";
  private static final String TILE_NEXT_TILE_ID_PROPERTY = "nextTileId";
  private static final String TILE_ACTION_PROPERTY = "action";
  private static final String TILE_ACTION_IDENTIFIER_PROPERTY = "identifier";
  private static final String TILE_ACTION_DESTINATION_TILE_ID_PROPERTY = "destinationTileId";
  private static final String TILE_ACTION_DESCRIPTION_PROPERTY = "description";

  /**
   * Reads a file at the given path and returns a list of Board objects. If there is only one board
   * in the file, it will be returned as a list with one element. If the file does not exist or
   * cannot be read, an empty list will be returned.
   *
   * @param path The path to the file.
   * @return A list of Board objects or null if the file does not exist or cannot be read.
   * @throws IOException if an error occurs while reading the file.
   */
  @Override
  public Board readFile(String path) throws IOException {
    logger.debug("Reading board file {}", path);
    try {
      String jsonString = FileUtils.readFileToString(new File(path), StandardCharsets.UTF_8);
      logger.debug("Successfully read JSON string from file {}", path);
      return deserializeBoard(jsonString);
    } catch (IOException e) {
      logger.error("Could not read board file {}", path);
      throw new IOException("Could not read board from file: " + path);
    }
  }

  /**
   * Writes a list of Board objects to a JSON file at the given path.
   *
   * @param path   The path to the file.
   * @param boards The list of Board objects to write.
   * @throws IOException If an error occurs while writing to the file.
   */
  @Override
  public void writeFile(String path, List<Board> boards) throws IOException {
    if (boards == null || boards.isEmpty()) {
      logger.error("Attempted to write empty board list");
      throw new IllegalArgumentException("Board list is null or empty.");
    }
    JsonObject boardJson = serializeBoard((LadderGameBoard) boards.getFirst());
    if (boardJson == null) {
      logger.error("Failed to serialize board. Board JSON was null");
      // Todo: Handle null boardJson, perhaps by throwing a ( custom ? ) exception
      return;
    }

    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    String prettyJson = gson.toJson(boardJson);

    File file = new File(path);

    if (!file.createNewFile()) {
      logger.error("File with the same name already exists: {}", path);
      throw new IOException("A file with the same name already exists");
    }
    FileUtils.writeStringToFile(file, prettyJson, StandardCharsets.UTF_8, false);
    logger.info("Board saved successfully");
  }

  /**
   * Serializes a Board object to a JSON string.
   *
   * @param board The Board object to serialize.
   * @return A JSON string representation of the Board object or null if the given Board object is
   *     null.
   */
  private JsonObject serializeBoard(LadderGameBoard board) {
    if (board == null) {
      return null;
    }

    JsonArray tilesJsonArray = new JsonArray();

    board.getTiles().forEach(tile -> {
      final LadderGameTile ladderGameTile = (LadderGameTile) tile;
      final JsonObject tileJson = new JsonObject();
      final JsonObject actionJson = new JsonObject();

      tileJson.addProperty(TILE_ID_PROPERTY, tile.getTileId());
      JsonArray coordinatesArray = new JsonArray();
      coordinatesArray.add(tile.getCoordinates()[0]);
      coordinatesArray.add(tile.getCoordinates()[1]);
      tileJson.add(TILE_COORDINATES_PROPERTY, coordinatesArray);
      tileJson.addProperty(TILE_NEXT_TILE_ID_PROPERTY, tile.getNextTileId());

      if (ladderGameTile.getLandAction() != null) {
        actionJson.addProperty(TILE_ACTION_IDENTIFIER_PROPERTY, ladderGameTile.getLandAction()
            .getIdentifier());
        actionJson.addProperty(TILE_ACTION_DESTINATION_TILE_ID_PROPERTY,
            ladderGameTile.getLandAction()
                .getDestinationTileId());
        actionJson.addProperty(TILE_ACTION_DESCRIPTION_PROPERTY, ladderGameTile.getLandAction()
            .getDescription());
        tileJson.add(TILE_ACTION_PROPERTY, actionJson);
      }
      tilesJsonArray.add(tileJson);
    });

    JsonObject boardJson = new JsonObject();
    boardJson.add(NAME_PROPERTY, new JsonPrimitive(board.getName()));
    boardJson.add(DESCRIPTION_PROPERTY, new JsonPrimitive(board.getDescription()));
    boardJson.add(ROWS_PROPERTY, new JsonPrimitive(board.getRowsAndColumns()[0]));
    boardJson.add(COLUMNS_PROPERTY, new JsonPrimitive(board.getRowsAndColumns()[1]));
    boardJson.add(BACKGROUND_PROPERTY, new JsonPrimitive(board.getBackground()));
    boardJson.add(PATTERN_PROPERTY, new JsonPrimitive(board.getPattern()));
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
      logger.error("Cannot deserialize a null or empty JSON string");
      return null;
    }

    JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();
    String boardName = jsonObject.get(NAME_PROPERTY).getAsString();
    String boardDescription = jsonObject.get(DESCRIPTION_PROPERTY).getAsString();
    int[] rowsAndColumns = new int[2];
    rowsAndColumns[0] = jsonObject.get(ROWS_PROPERTY).getAsInt();
    rowsAndColumns[1] = jsonObject.get(COLUMNS_PROPERTY).getAsInt();
    String boardBackground = jsonObject.get(BACKGROUND_PROPERTY).getAsString();
    String boardPattern = jsonObject.get(PATTERN_PROPERTY).getAsString();
    Board board = new LadderGameBoard(boardName, boardDescription, rowsAndColumns, boardBackground,
        boardPattern);

    JsonArray tilesJsonArray = jsonObject.getAsJsonArray(TILES_PROPERTY);
    tilesJsonArray.forEach(tileJson -> {
      JsonObject tileJsonObject = tileJson.getAsJsonObject();
      int tileId = 0;
      int[] coordinates = null;
      int nextTileId = 0;
      TileAction tileAction = null;

      try {
        tileId = tileJsonObject.get(TILE_ID_PROPERTY).getAsInt();
        JsonArray coordinatesArray = tileJsonObject.get(TILE_COORDINATES_PROPERTY).getAsJsonArray();
        coordinates = new int[coordinatesArray.size()];
        for (int i = 0; i < coordinatesArray.size(); i++) {
          coordinates[i] = coordinatesArray.get(i).getAsInt();
        }
        nextTileId = tileJsonObject.get(TILE_NEXT_TILE_ID_PROPERTY).getAsInt();
        JsonObject actionJsonObject = tileJsonObject.getAsJsonObject(TILE_ACTION_PROPERTY);

        String actionIdentifier = actionJsonObject.get(TILE_ACTION_IDENTIFIER_PROPERTY)
            .getAsString();
        int destinationTileId = actionJsonObject.get(TILE_ACTION_DESTINATION_TILE_ID_PROPERTY)
            .getAsInt();
        String actionDescription = actionJsonObject.get(TILE_ACTION_DESCRIPTION_PROPERTY)
            .getAsString();

        tileAction = createTileAction(actionIdentifier, destinationTileId, actionDescription);
      } catch (NullPointerException e) {
        // Todo: Handle null pointer exception if any of the tile properties are missing
        logger.error("Missing tile properties while parsing tile. Tile JSON: {}", tileJsonObject);
      }

      if (tileAction != null) {
        board.addTile(new LadderGameTile(tileId, coordinates, nextTileId, tileAction));
        return;
      }
      board.addTile(new LadderGameTile(tileId, coordinates, nextTileId));
    });
    logger.debug("Deserializing board from JSON");
    return board;
  }

  private static TileAction createTileAction(String actionIdentifier, int destinationTileId,
      String actionDescription) {
    logger.debug("Creating tile action with identifier: {}", actionIdentifier);
    TileAction tileAction;
    switch (actionIdentifier.split("_")[2]) {
      case "ladder" ->
          tileAction = new LadderAction(actionIdentifier, destinationTileId, actionDescription);
      case "slide" ->
          tileAction = new SlideAction(actionIdentifier, destinationTileId, actionDescription);
      case "portal" ->
          tileAction = new PortalAction(actionIdentifier, destinationTileId, actionDescription);
      default -> tileAction = null;
    }
    return tileAction;
  }
}
