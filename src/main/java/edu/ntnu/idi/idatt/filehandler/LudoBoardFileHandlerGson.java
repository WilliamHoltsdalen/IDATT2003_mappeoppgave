package edu.ntnu.idi.idatt.filehandler;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

import edu.ntnu.idi.idatt.model.board.Board;
import edu.ntnu.idi.idatt.model.board.LudoGameBoard;
import edu.ntnu.idi.idatt.model.tile.LudoTile;
import javafx.scene.paint.Color;

public class LudoBoardFileHandlerGson implements FileHandler<Board> {
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

    @Override
    public Board readFile(String path) throws IOException {
        try {
            String jsonString = FileUtils.readFileToString(new File(path), StandardCharsets.UTF_8);
            return deserializeBoard(jsonString);
        } catch (IOException e) {
            throw new IOException("Could not read board from file: " + path);
        }
    }

    @Override
    public void writeFile(String path, List<Board> boards) throws IOException {
        if (boards == null || boards.isEmpty()) {
            throw new IllegalArgumentException("Board list is null or empty.");
        }
        JsonObject boardJson = serializeBoard((LudoGameBoard) boards.getFirst());
        if (boardJson == null) {
            return;
        }
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String prettyJson = gson.toJson(boardJson);
        File file = new File(path);
        if (!file.createNewFile()) {
            throw new IOException("A file with the same name already exists");
        }
        FileUtils.writeStringToFile(file, prettyJson, StandardCharsets.UTF_8, false);
    }

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
        boardJson.add(TOTAL_TRACK_TILE_COUNT_PROPERTY, new JsonPrimitive(board.getTotalTrackTileCount()));
        boardJson.add(TILES_PROPERTY, tilesJsonArray);
        return boardJson;
    }

    private JsonArray intArrayToJson(int[] arr) {
        JsonArray array = new JsonArray();
        if (arr != null) {
            for (int v : arr) array.add(v);
        }
        return array;
    }

    private int[] jsonToIntArray(JsonArray arr) {
        int[] result = new int[arr.size()];
        for (int i = 0; i < arr.size(); i++) {
            result[i] = arr.get(i).getAsInt();
        }
        return result;
    }

    private Board deserializeBoard(String jsonString) {
        if (jsonString == null || jsonString.isEmpty()) {
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
        LudoGameBoard board = new LudoGameBoard(boardName, boardDescription, boardBackground, boardSize, colors);
        if (jsonObject.has(PLAYER_START_INDEXES_PROPERTY))
            setIntArrayField(board, "playerStartIndexes", jsonToIntArray(jsonObject.getAsJsonArray(PLAYER_START_INDEXES_PROPERTY)));
        if (jsonObject.has(PLAYER_TRACK_START_INDEXES_PROPERTY))
            setIntArrayField(board, "playerTrackStartIndexes", jsonToIntArray(jsonObject.getAsJsonArray(PLAYER_TRACK_START_INDEXES_PROPERTY)));
        if (jsonObject.has(PLAYER_FINISH_START_INDEXES_PROPERTY))
            setIntArrayField(board, "playerFinishStartIndexes", jsonToIntArray(jsonObject.getAsJsonArray(PLAYER_FINISH_START_INDEXES_PROPERTY)));
        if (jsonObject.has(PLAYER_FINISH_INDEXES_PROPERTY))
            setIntArrayField(board, "playerFinishIndexes", jsonToIntArray(jsonObject.getAsJsonArray(PLAYER_FINISH_INDEXES_PROPERTY)));
        if (jsonObject.has(START_AREA_SIZE_PROPERTY))
            setIntField(board, "startAreaSize", jsonObject.get(START_AREA_SIZE_PROPERTY).getAsInt());
        if (jsonObject.has(TOTAL_TRACK_TILE_COUNT_PROPERTY))
            setIntField(board, "totalTrackTileCount", jsonObject.get(TOTAL_TRACK_TILE_COUNT_PROPERTY).getAsInt());
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

    private void setIntArrayField(LudoGameBoard board, String fieldName, int[] value) {
        try {
            java.lang.reflect.Field field = LudoGameBoard.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(board, value);
        } catch (Exception e) {
            // TODO: Ignore or log
        }
    }

    private void setIntField(LudoGameBoard board, String fieldName, int value) {
        try {
            java.lang.reflect.Field field = LudoGameBoard.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.setInt(board, value);
        } catch (Exception e) {
            // TODO: Ignore or log
        }
    }
} 