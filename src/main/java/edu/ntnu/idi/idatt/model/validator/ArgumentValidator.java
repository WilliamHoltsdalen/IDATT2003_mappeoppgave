package edu.ntnu.idi.idatt.model.validator;

import edu.ntnu.idi.idatt.model.board.Board;
import edu.ntnu.idi.idatt.model.player.Player;
import edu.ntnu.idi.idatt.model.player.PlayerTokenType;
import edu.ntnu.idi.idatt.model.tile.Tile;
import edu.ntnu.idi.idatt.model.tile.TileAction;
import edu.ntnu.idi.idatt.model.token.LudoToken.TokenStatus;
import java.util.List;
import javafx.scene.paint.Color;

/**
 * <h3> Validator class for arguments passed to model constructors and methods.</h3>
 */
public class ArgumentValidator {

  /** Private constructor to prevent instantiation. */
  private ArgumentValidator() {
  }

  /**
   * Validates the arguments for the setValue method in Die class.
   *
   * @param value the value to set
   * @throws IllegalArgumentException if value is less than 1 or greater than 6
   */
  public static void dieSetValueValidator(int value) {
    if (value < 1 || value > 6) {
      throw new IllegalArgumentException("Die value must be between 1 and 6");
    }
  }

  /**
   * Validates the arguments for the getDieValue method in Dice class.
   *
   * @param dieNumber the number of the die to get the value of
   * @param dieCount the total number of dice
   * @throws IllegalArgumentException if dieNumber is less than 1 or greater than the given dieCount
   */
  public static void diceGetDieValueValidator(int dieNumber, int dieCount) {
    if (dieNumber < 1 || dieNumber > dieCount) {
      throw new IllegalArgumentException("Die number must be between 1 and the number of dice.");
    }
  }

  /**
   * Validates the arguments for the addDice method in Dice class.
   *
   * @param numberOfDice the number of dice to add
   * @throws IllegalArgumentException if numberOfDice is less than 1
   */
  public static void diceAddDiceValidator(int numberOfDice) {
    if (numberOfDice < 1) {
      throw new IllegalArgumentException("Number of dice must be greater than 0.");
    }
  }

  /**
   * Validates the arguments for the rollSingleDie method in Dice class.
   *
   * @param dieNumber the number of the die to roll
   * @param dieCount the total number of dice
   * @throws IllegalArgumentException if dieNumber is less than 1 or greater than the given dieCount
   */
  public static void diceRollSingleDieValidator(int dieNumber, int dieCount) {
    if (dieNumber < 1 || dieNumber > dieCount) {
      throw new IllegalArgumentException("Die number must be between 1 and the number of dice.");
    }
  }

  /**
   * Validates the argument for the getTile method in Board class.
   *
   * @param tileId the id of the tile to get
   * @throws IllegalArgumentException if tileId is less than 0 or greater than or equal to the
   *         number of tiles on the board
   */
  public static void boardGetTileValidator(int tileId, int tileCount) {
    if (tileId < 0) {
      throw new IllegalArgumentException("Tile id must be greater than 0");
    }
    if (tileId > tileCount) {
      throw new IllegalArgumentException("Tile id must be less than the number of tiles on the "
          + "board");
    }
  }

  /**
   * Validates the arguments for the setName method in Board class.
   *
   * @param name the name to set
   * @throws IllegalArgumentException if name is null or blank
   */
  public static void boardSetnameValidator(String name) {
    if (name == null || name.isBlank()) {
      throw new IllegalArgumentException("Name cannot be null or blank");
    }
  }

  /**
   * Validates the arguments for the setDescription method in Board class.
   *
   * @param description the description to set
   * @throws IllegalArgumentException if description is null or blank
   */
  public static void boardSetDescriptionValidator(String description) {
    if (description == null || description.isBlank()) {
      throw new IllegalArgumentException("Description cannot be null or blank");
    }
  }

  /**
   * Validates the arguments for the setBackground method in LadderGameBoard class.
   *
   * @param background the background to set
   * @throws IllegalArgumentException if background is null or blank
   */
  public static void boardSetBackgroundValidator(String background) {
    if (background == null || background.isBlank()) {
      throw new IllegalArgumentException("Background cannot be null or blank");
    }
  }

  /**
   * Validates the arguments for the populateTiles method in Board class.
   *
   * @param rows    the number of rows
   * @param columns the number of columns
   * @throws IllegalArgumentException if rows or columns is less than 1
   */
  public static void ludoGameBoardCreateTilesValidator(int rows, int columns) {
    if (rows < 1 || columns < 1) {
      throw new IllegalArgumentException("Board must have at least 1 row and 1 column");
    }
    if (rows != columns) {
      throw new IllegalArgumentException("Board must be a square");
    }
  }

  /**
   * Validates the arguments for the addTile method in Board class.
   *
   * @param tile the tile to add
   * @throws IllegalArgumentException if tile is null or tile id is less than 0
   */
  public static void boardAddTileValidator(Tile tile) {
    if (tile == null) {
      throw new IllegalArgumentException("Tile cannot be null");
    }
    if (tile.getTileId() < 0) {
      throw new IllegalArgumentException("Tile id must be greater than 0");
    }
  }

  /**
   * Validates the arguments for the setRowsAndColumns method in LadderGameBoard class.
   *
   * @param rowsAndColumns the rows and columns to set
   * @throws IllegalArgumentException if rowsAndColumns is null
   */
  public static void ladderGameBoardSetRowsAndColumnsValidator(int[] rowsAndColumns) {
    if (rowsAndColumns == null) {
      throw new IllegalArgumentException("Rows and columns cannot be null");
    }
    if (rowsAndColumns.length != 2) {
      throw new IllegalArgumentException("Rows and columns must be an array of length 2");
    }
    if (rowsAndColumns[0] <= 0 || rowsAndColumns[1] <= 0) {
      throw new IllegalArgumentException("Rows and columns must be greater than 0");
    }
    if (rowsAndColumns[0] < 5 || rowsAndColumns[1] < 5) {
      throw new IllegalArgumentException("Rows and columns must be at least 5x5");
    }
    if (rowsAndColumns[0] > 12 || rowsAndColumns[1] > 12) {
      throw new IllegalArgumentException("Rows and columns must be at most 12x12");
    }
  }

  /**
   * Validates the arguments for the createTiles method in LadderGameBoard class.
   *
   * @param rows the number of rows
   * @param columns the number of columns
   * @throws IllegalArgumentException if rows or columns is less than 1
   */
  public static void ladderGameBoardCreateTilesValidator(int rows, int columns) {
    if (rows < 5 || columns < 5) {
      throw new IllegalArgumentException("Board must be at least 5x5");
    }
    if (rows > 12 || columns > 12) {
      throw new IllegalArgumentException("Board must be at most 12x12");
    }
  }

  /** 
   * Validates the arguments for the setPattern method in LadderGameBoard class.
   *
   * @param pattern the pattern to set
   * @throws IllegalArgumentException if pattern is null or blank
   */
  public static void ladderGameBoardSetPatternValidator(String pattern) {
    if (pattern == null || pattern.isBlank()) {
      throw new IllegalArgumentException("Pattern cannot be null or blank");
    }
  }

  /**
   * Validates the arguments for the setRowsAndColumns method in LudoGameBoard class.
   *
   * @param rowsAndColumns the rows and columns to set
   * @throws IllegalArgumentException if rowsAndColumns is null or the rows and columns are not
   *     equal
   */
  public static void ludoGameBoardSetRowsAndColumnsValidator(int[] rowsAndColumns) {
    if (rowsAndColumns == null) {
      throw new IllegalArgumentException("Rows and columns cannot be null");
    }
    if (rowsAndColumns[0] != rowsAndColumns[1]) {
      throw new IllegalArgumentException("Rows and columns must be equal");
    }
  }


  /**
   * Validates the arguments for the setColors method in LudoGameBoard class.
   *
   * @param colors the colors to set
   * @throws IllegalArgumentException if colors is null
   */
  public static void ludoGameBoardSetColorsValidator(Color[] colors) {
    if (colors == null) {
      throw new IllegalArgumentException("Colors cannot be null");
    }
    if (colors.length != 4) {
      throw new IllegalArgumentException("Colors must contain 4 colors");
    }
    for (Color color : colors) {
      if (color == null) {
        throw new IllegalArgumentException("Color cannot be null");
      }
    }
  }

  /**
   * Validates the arguments for the setBoardSize method in LudoGameBoard class.
   *
   * @param boardSize the board size to set
   * @throws IllegalArgumentException if boardSize is less than 1
   */
  public static void ludoGameBoardSetBoardSizeValidator(int boardSize) {
    if (boardSize < 1) {
      throw new IllegalArgumentException("Board size must be greater than 0");
    }
    if (boardSize % 2 == 0) {
      throw new IllegalArgumentException("Board size must be an odd number");
    }

    if (boardSize < 9) {
      throw new IllegalArgumentException("Board size must be at least 9");
    }


  }

  /**
   * Validates the arguments for the createDice method in BoardGame class.
   *
   * @param diceCount the number of dice
   */
  public static void boardGameCreateDiceValidator(int diceCount) {
    if (diceCount < 1) {
      throw new IllegalArgumentException("Dice count must be greater than 0");
    }
  }

  /**
   * Validates the arguments for the setCurrentPlayer method in BoardGame class.
   *
   * @param player the player to set as current
   */
  public static void boardGameSetCurrentPlayerValidator(Player player) {
    if (player == null) {
      throw new IllegalArgumentException("Player cannot be null");
    }
  }

  /**
   * Validates the arguments for the setBoard method in BoardGame class.
   *
   * @param board the board to set
   */
  public static void boardGameSetBoardValidator(Board board) {
    if (board == null) {
      throw new IllegalArgumentException("Board cannot be null");
    }
  }

  /**
   * Validates the arguments for the setPlayers method in the BoardGame class.
   *
   * @param players the players to set.
   */
  public static void boardGameSetPlayersValidator(List<Player> players) {
    if (players == null) {
      throw new IllegalArgumentException("Players cannot be null");
    }
    if (players.size() < 2 || players.size() > 5) {
      throw new IllegalArgumentException("The number of players must be between 2 and 5");
    }
  }

  /**
   * Validates the arguments for the setDestinationTileId method in TileAction class.
   *
   * @param destinationTileId the id of the destination tile
   * @throws IllegalArgumentException if destinationTileId is less than 0
   */
  public static void tileActionSetDestinationTileIdValidator(int destinationTileId) {
    if (destinationTileId < 0) {
      throw new IllegalArgumentException("Destination tile id must be greater than 0");
    }
  }

  /**
   * Validates the arguments for the setDescription method in TileAction class.
   *
   * @param description the description to set
   * @throws IllegalArgumentException if description is null or blank
   */
  public static void tileActionSetDescriptionValidator(String description) {
    if (description == null || description.isBlank()) {
      throw new IllegalArgumentException("Description cannot be null or blank");
    }
  }

  /**
   * Validates the arguments for the setIdentifier method in TileAction class.
   *
   * @param identifier the identifier to set
   * @throws IllegalArgumentException if identifier is null or blank
   */
  public static void tileActionSetIdentifierValidator(String identifier) {
    if (identifier == null || identifier.isBlank()) {
      throw new IllegalArgumentException("Identifier cannot be null or blank");
    }
  }
  
  /**
   * Validates the arguments for the perform method in LadderAction class.
   *
   * @param player the player to perform the action on
   * @param board the board to perform the action on
   * @throws IllegalArgumentException if player or board is null
   */
  public static void tileActionPerformValidator(Player player, Board board) {
    if (player == null) {
      throw new IllegalArgumentException("Player cannot be null");
    }
    if (board == null) {
      throw new IllegalArgumentException("Board cannot be null");
    }
  }


  /**
   * Validates the arguments for the setName method in Player class.
   *
   * @param name the name to set
   * @throws IllegalArgumentException if name is null or blank
   */
  public static void playerSetNameValidator(String name) {
    if (name == null || name.isBlank()) {
      throw new IllegalArgumentException("Name cannot be null or blank");
    }
  }

  /**
   * Validates the arguments for the setColorHex method in Player class.
   *
   * @param colorHex the color hex to set
   * @throws IllegalArgumentException if colorHex is null or blank
   */
  public static void playerSetColorHexValidator(String colorHex) {
    if (colorHex == null || colorHex.isBlank()) {
      throw new IllegalArgumentException("Color hex cannot be null or blank");
    }
    if (Color.valueOf(colorHex) == null) {
      throw new IllegalArgumentException("Color is not valid");
    }
  }

  /**
   * Validates the arguments for the placeOnTile method in Player class.
   *
   * @param tile the tile to place the player on
   * @throws IllegalArgumentException if tile is null
   */
  public static void ladderGamePlayerPlaceOnTileValidator(Tile tile) {
    if (tile == null) {
      throw new IllegalArgumentException("Tile cannot be null");
    }
  }

  /**
   * Validates arguments for the moveToken method in LudoPlayer class.
   *
   * @param pieceId The ID of the token/piece (1-4).
   * @param tile The target {@link Tile} (must not be null).
   * @param status The new {@link TokenStatus} (must not be null).
   * @throws IllegalArgumentException if any argument is invalid.
   */
  public static void ludoPlayerMoveTokenValidator(int pieceId, Tile tile, TokenStatus status) {
    if (pieceId < 1 || pieceId > 4) {
      throw new IllegalArgumentException("Piece id must be between 1 and 4");
    }
    if (tile == null) {
      throw new IllegalArgumentException("Tile cannot be null");
    }
    if (status == null) {
      throw new IllegalArgumentException("Status cannot be null");
    }
  }

  /**
   * Validates the arguments for the setPlayerTokenType method in Player class.
   *
   * @param playerTokenType the player token type to set
   * @throws IllegalArgumentException if playerTokenType is null
   */
  public static void playerSetPlayerTokenTypeValidator(PlayerTokenType playerTokenType) {
    if (playerTokenType == null) {
      throw new IllegalArgumentException("Player token type cannot be null");
    }
  }

  /**
   * Validates the arguments for the setCoordinates method in Tile class.
   *
   * @param coordinates the coordinates to set
   * @throws IllegalArgumentException if coordinates is null
   */
  public static void tileSetCoordinatesValidator(int[] coordinates) {
    if (coordinates == null) {
      throw new IllegalArgumentException("Coordinates cannot be null");
    }
    if (coordinates.length != 2) {
      throw new IllegalArgumentException("Coordinates must be an array of length 2");
    }
  }

  /**
   * Validates the arguments for the setTileId method in Tile class.
   *
   * @param tileId the id of the tile
   * @throws IllegalArgumentException if tileId is less than 0
   */
  public static void tileSetTileIdValidator(int tileId) {
    if (tileId < 0) {
      throw new IllegalArgumentException("Tile id must be greater than 0");
    }
  }

  /**
   * Validates the arguments for the setNextTileId method in Tile class.
   *
   * @param currentTileId the tile id of the current tile
   * @param nextTileId the tile id of the next tile
   * @throws IllegalArgumentException if nextTileId is less than 0
   */
  public static void tileSetNextTileIdValidator(int currentTileId, int nextTileId) {
    if (nextTileId < 0) {
      throw new IllegalArgumentException("Next tile id must be equal to or greater than 0");
    }
    if (nextTileId == currentTileId) {
      throw new IllegalArgumentException("Next tile id cannot be the same as the current tile id");
    }
  }

  /**
   * Validates the arguments for the setLandAction method in Tile class.
   *
   * @param landAction the land action to set
   * @throws IllegalArgumentException if landAction is null
   */
  public static void tileSetLandActionValidator(TileAction landAction) {
    if (landAction == null) {
      throw new IllegalArgumentException("Land action cannot be null");
    }
  }
}
