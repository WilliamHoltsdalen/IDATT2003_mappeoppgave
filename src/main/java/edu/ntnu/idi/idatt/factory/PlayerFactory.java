package edu.ntnu.idi.idatt.factory;

import edu.ntnu.idi.idatt.model.Player;
import edu.ntnu.idi.idatt.model.PlayerTokenType;
import edu.ntnu.idi.idatt.utils.PlayerFileHandlerCsv;
import edu.ntnu.idi.idatt.utils.interfaces.FileHandler;
import java.io.IOException;
import java.util.List;

/**
 * <h3>Factory class for creating Player objects.</h3>
 *
 * <p>This class provides methods for creating Player objects based on predefined variants.
 * Player objects can be loaded/created from an external file, or from hardcoded variants in this
 * factory class.
 */
public class PlayerFactory {
  private static final String DEFAULT_BOT_NAME_PREFIX = "Bot_";
  private static final String DEFAULT_BOT_COLOR = "#FF0000";
  private static final PlayerTokenType DEFAULT_BOT_TOKEN_TYPE = PlayerTokenType.SQUARE;

  /** Private constructor to prevent instantiation. */
  private PlayerFactory() {}

  /**
   * Creates a list of Player objects by reading from the given CSV file.
   *
   * @param filePath The path to the CSV file.
   * @return A List of Player objects parsed from the file.
   * @throws IOException if an error occurs while reading the file.
   */
  public static List<Player> createPlayersFromFile(String filePath) throws IOException {
    if (filePath == null || filePath.isEmpty()) {
      return List.of();
    }

    final FileHandler<Player> playerFileHandler = new PlayerFileHandlerCsv();
    return playerFileHandler.readFile(filePath);
  }

  /**
   * Creates a single Player with the given name and colorHex.
   *
   * @param name      The player's name.
   * @param colorHex  The player's color in hex (e.g. "#FF0000").
   * @return A new Player instance with the given attributes.
   */
  public static Player createPlayer(String name, String colorHex, PlayerTokenType playerTokenType) {
    if (name == null || name.isEmpty()) {
      throw new IllegalArgumentException("Name cannot be null or empty.");
    }
    if (colorHex == null || colorHex.isEmpty()) {
      throw new IllegalArgumentException("Color hex cannot be null or empty.");
    }
    if (playerTokenType == null) {
      throw new IllegalArgumentException("Player token type cannot be null.");
    }

    return new Player(name, colorHex, playerTokenType);
  }

  /**
   * Creates a player representing a bot, with the given name prefixed with "Bot_".
   *
   * @param name The name of the bot.
   * @return A new Player instance representing the bot.
   */
  public static Player createBot(String name) {
    if (name == null || name.isEmpty()) {
      throw new IllegalArgumentException("Name cannot be null or empty.");
    }

    return new Player(DEFAULT_BOT_NAME_PREFIX + name, DEFAULT_BOT_COLOR, DEFAULT_BOT_TOKEN_TYPE);
  }
}