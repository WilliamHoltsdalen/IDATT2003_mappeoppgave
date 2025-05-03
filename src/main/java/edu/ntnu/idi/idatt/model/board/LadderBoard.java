package edu.ntnu.idi.idatt.model.board;

import java.util.HashMap;
import java.util.Map;

import edu.ntnu.idi.idatt.model.tile.Tile;

/**
 * <h3>LadderBoard class</h3>
 *
 * <p>Concrete implementation of the Board interface for the Chutes and Ladders game.
 * This class extends AbstractBoard and implements game-specific board functionality.
 * 
 * @see AbstractBoard
 * @see Board
 * @see Tile
 */
public class LadderBoard extends AbstractBoard {

    /**
     * Constructor for LadderBoard.
     *
     * @param name The name of the board
     * @param description The description of the board
     * @param rowsAndColumns The number of rows and columns in the board
     * @param background The background of the board
     * @param pattern The pattern of the board
     */
    public LadderBoard(String name, String description, int[] rowsAndColumns, String background, String pattern) {
        super(name, description, rowsAndColumns, background, pattern);
    }

    /**
     * Creates a list of Tile objects that are arranged in a grid pattern with alternating directions
     * in each row. The first tile is in the lower left corner of the grid, and the direction of the
     * first row is left to right. This is the standard way to create a board for the Chutes and Ladders game.
     * 
     * <p>If the board already contains tiles, and they have any tile actions, these will be added to the new tiles if 
     * they are within the new board dimensions.
     *
     * @param rows The number of rows in the grid
     * @param columns The number of columns in the grid
     */
    @Override
    public void createTiles(int rows, int columns) {
        Map<Integer, Tile> newTiles = new HashMap<>();
        newTiles.put(0, new Tile(0, new int[]{0, -2}, 1)); // Add 0th tile outside of board

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                int baseId = i * columns;
                int tileId;
                int[] coordinates = new int[]{i, j};

                if (i % 2 == 0) { // Even numbered rows (left to right)
                    tileId = baseId + j + 1;
                } else { // Odd numbered rows (right to left)
                    tileId = baseId + (columns - j);
                }
                newTiles.put(tileId, new Tile(tileId, coordinates, tileId + 1));
                try {
                    newTiles.get(tileId).setLandAction(tiles.get(tileId).getLandAction());
                } catch (NullPointerException | IllegalArgumentException e) {
                    // Do nothing if the tile doesn't have an action
                }
            }
        }
        this.tiles = newTiles;
    }
} 