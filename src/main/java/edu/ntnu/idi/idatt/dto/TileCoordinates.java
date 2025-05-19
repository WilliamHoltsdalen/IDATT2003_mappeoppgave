package edu.ntnu.idi.idatt.dto;

/**
 * TileCoordinates.
 *
 * <p>A simple record class to represent the coordinates of a tile on a game board.
 * It holds the row and column index of a tile.</p>
 *
 * @param row The row index of the tile.
 * @param col The column index of the tile.
 */
public record TileCoordinates(int row, int col) {} 