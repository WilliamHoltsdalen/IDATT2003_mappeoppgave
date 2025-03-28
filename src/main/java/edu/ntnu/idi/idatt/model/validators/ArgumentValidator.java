package edu.ntnu.idi.idatt.model.validators;

import edu.ntnu.idi.idatt.model.Board;
import edu.ntnu.idi.idatt.model.Player;
import edu.ntnu.idi.idatt.model.Tile;
import edu.ntnu.idi.idatt.model.interfaces.TileAction;
import java.util.List;

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
    if (tileId >= tileCount) {
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
   * Validates the arguments for the populateTiles method in Board class.
   *
   * @param rows    the number of rows
   * @param columns the number of columns
   * @throws IllegalArgumentException if rows or columns is less than 1
   */
  public static void boardPopulateTilesValidator(int rows, int columns) {
    if (rows < 1 || columns < 1) {
      throw new IllegalArgumentException("Board must have at least 1 row and 1 column");
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
   * Validates the arguments for the addPlayers method in BoardGame class.
   *
   * @param players the list of players to add
   */
  public static void boardGameAddPlayersValidator(List<Player> players, int currentPlayerCount) {
    if (players == null) {
      throw new IllegalArgumentException("List of players cannot be null");
    }
    if (players.size() + currentPlayerCount >= 5) {
      throw new IllegalArgumentException("The number of players must be less than 5");
    }

    players.forEach(player -> {
      if (player == null) {
        throw new IllegalArgumentException("Players in the provided list cannot be null");
      }
    });
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
   * Validates the arguments for the setDestinationTileId method in LadderAction class.
   *
   * @param destinationTileId the id of the destination tile
   * @throws IllegalArgumentException if destinationTileId is less than 0
   */
  public static void ladderActionSetDestinationTileIdValidator(int destinationTileId) {
    if (destinationTileId < 0) {
      throw new IllegalArgumentException("Destination tile id must be greater than 0");
    }
  }

  /**
   * Validates the arguments for the setDescription method in LadderAction class.
   *
   * @param description the description to set
   * @throws IllegalArgumentException if description is null or blank
   */
  public static void ladderActionSetDescriptionValidator(String description) {
    if (description == null || description.isBlank()) {
      throw new IllegalArgumentException("Description cannot be null or blank");
    }
  }

  /**
   * Validates the arguments for the perform method in LadderAction class.
   *
   * @param player the player to perform the action on
   * @param board the board to perform the action on
   * @throws IllegalArgumentException if player or board is null
   */
  public static void ladderActionPerformValidator(Player player, Board board) {
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
  }

  /**
   * Validates the arguments for the placeOnTile method in Player class.
   *
   * @param tile the tile to place the player on
   * @throws IllegalArgumentException if tile is null
   */
  public static void playerPlaceOnTileValidator(Tile tile) {
    if (tile == null) {
      throw new IllegalArgumentException("Tile cannot be null");
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
    if (nextTileId > 0 && nextTileId <= currentTileId) {
      throw new IllegalArgumentException("Next tile id must be 0 or greater than current tile id");
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
