package edu.ntnu.idi.idatt.model.board;

import java.util.List;

import edu.ntnu.idi.idatt.model.tile.Tile;

/**
 * <h3>Board interface</h3>
 *
 * <p>Interface defining common operations for all board game boards.
 */
public interface Board {
    /**
     * Gets the name of the board.
     *
     * @return the name of the board
     */
    String getName();

    /**
     * Gets the description of the board.
     *
     * @return the description of the board
     */
    String getDescription();

    /**
     * Gets the number of rows and columns in the board.
     *
     * @return an array containing [rows, columns]
     */
    int[] getRowsAndColumns();

    /**
     * Gets a tile by its ID.
     *
     * @param tileId the ID of the tile to get
     * @return the tile with the given ID
     */
    Tile getTile(int tileId);

    /**
     * Gets all tiles on the board.
     *
     * @return a list of all tiles
     */
    List<Tile> getTiles();

    /**
     * Gets the total number of tiles on the board.
     *
     * @return the number of tiles
     */
    int getTileCount();

    /**
     * Gets the background image path of the board.
     *
     * @return the background image path
     */
    String getBackground();

    /**
     * Gets the pattern of the board.
     *
     * @return the pattern of the board
     */
    String getPattern();

    /**
     * Sets the name of the board.
     *
     * @param name the name to set
     */
    void setName(String name);

    /**
     * Sets the description of the board.
     *
     * @param description the description to set
     */
    void setDescription(String description);

    /**
     * Sets the number of rows and columns in the board.
     *
     * @param rowsAndColumns an array containing [rows, columns]
     */
    void setRowsAndColumns(int[] rowsAndColumns);

    /**
     * Sets the background image path of the board.
     *
     * @param background the background image path to set
     */
    void setBackground(String background);

    /**
     * Sets the pattern of the board.
     *
     * @param pattern the pattern to set
     */
    void setPattern(String pattern);

    /**
     * Adds a tile to the board.
     *
     * @param tile the tile to add
     */
    void addTile(Tile tile);

    /**
     * Creates the tiles for the board.
     *
     * @param rows the number of rows
     * @param columns the number of columns
     */
    void createTiles(int rows, int columns);
}
