package edu.ntnu.idi.idatt.model.board;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.ntnu.idi.idatt.model.tile.Tile;
import static edu.ntnu.idi.idatt.model.validator.ArgumentValidator.boardAddTileValidator;
import static edu.ntnu.idi.idatt.model.validator.ArgumentValidator.boardGetTileValidator;
import static edu.ntnu.idi.idatt.model.validator.ArgumentValidator.boardSetBackgroundValidator;
import static edu.ntnu.idi.idatt.model.validator.ArgumentValidator.boardSetDescriptionValidator;
import static edu.ntnu.idi.idatt.model.validator.ArgumentValidator.boardSetnameValidator;

/**
 * <h3>BaseBoard class</h3>
 *
 * <p>Abstract class implementing common functionality for board game boards.
 * This class provides the base implementation for both Chutes and Ladders and Ludo boards.
 */
public abstract class BaseBoard implements Board {
    protected String name;
    protected String description;
    protected int[] rowsAndColumns;
    protected Map<Integer, Tile> tiles;
    protected String background;
    
    /**
     * Constructor for BaseBoard.
     *
     * @param name The name of the board
     * @param description The description of the board
     * @param background The background of the board
     * @param pattern The pattern of the board
     */
    protected BaseBoard(String name, String description, String background) {
        this.tiles = new HashMap<>();
        setName(name);
        setDescription(description);
        setBackground(background);
    }

    /**
     * Gets the name of the board.
     *
     * @return the name of the board
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Gets the description of the board.
     *
     * @return the description of the board
     */
    @Override
    public String getDescription() {
        return description;
    }

    /**
     * Gets the background of the board.
     *
     * @return the background of the board
     */
    @Override
    public String getBackground() {
        return background;
    }

    /**
     * Gets a tile by its ID.
     *
     * @param tileId the ID of the tile to get
     * @return the tile with the given ID
     */
    @Override
    public Tile getTile(int tileId) {
        boardGetTileValidator(tileId, tiles.size());
        return tiles.get(tileId);
    }

    /**
     * Gets all tiles on the board.
     *
     * @return a list of all tiles
     */
    @Override
    public List<Tile> getTiles() {
        return new ArrayList<>(tiles.values());
    }

    /**
     * Gets the total number of tiles on the board excluding the starting tile, which normally 
     * is outside of the board, with the id 0.
     *
     * @return the number of tiles
     */
    @Override
    public int getTileCount() {
        return tiles.size() - 1;
    }

    /**
     * Sets the name of the board.
     *
     * @param name the name to set
     */
    @Override
    public void setName(String name) {
        boardSetnameValidator(name);
        this.name = name;
    }

    /**
     * Sets the description of the board.
     *
     * @param description the description to set
     */
    @Override
    public void setDescription(String description) {
        boardSetDescriptionValidator(description);
        this.description = description;
    }

    /**
     * Sets the background of the board.
     *
     * @param background the background to set
     */
    @Override
    public void setBackground(String background) {
        boardSetBackgroundValidator(background);
        this.background = background;
    }

    /**
     * Adds a tile to the board.
     *
     * @param tile the tile to add
     */
    @Override
    public void addTile(Tile tile) {
        boardAddTileValidator(tile);
        this.tiles.put(tile.getTileId(), tile);
    }
} 