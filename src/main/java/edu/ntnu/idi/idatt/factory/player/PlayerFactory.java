package edu.ntnu.idi.idatt.factory.player;

import java.io.IOException;
import java.util.List;

import edu.ntnu.idi.idatt.filehandler.FileHandler;
import edu.ntnu.idi.idatt.filehandler.PlayerFileHandlerCsv;
import edu.ntnu.idi.idatt.model.player.LadderGamePlayer;
import edu.ntnu.idi.idatt.model.player.LudoPlayer;
import edu.ntnu.idi.idatt.model.player.Player;
import edu.ntnu.idi.idatt.model.player.PlayerTokenType;

/**
 * <h3>Factory class for creating Player objects.</h3>
 *
 * <p>This class provides methods for creating Player objects based on predefined variants.
 * Player objects can be loaded/created from an external file, or from hardcoded variants in this
 * factory class.
 */
public class PlayerFactory {

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
    return (List<Player>) playerFileHandler.readFile(filePath);
  }

  /**
   * Creates a single LadderGame player with the given name and colorHex.
   *
   * @param name      The player's name.
   * @param colorHex  The player's color in hex (e.g. "#FF0000").
   * @return A new LadderGamePlayer instance with the given attributes.
   */
  public static Player createLadderGamePlayer(String name, String colorHex, PlayerTokenType playerTokenType) {
    if (!validatePlayer(name, colorHex, playerTokenType)) {
      throw new IllegalArgumentException("Invalid player parameters.");
    }

    return new LadderGamePlayer(name, colorHex, playerTokenType);
  }

  /**
   * Creates a single Ludo player with the given name and colorHex.
   *
   * @param name      The player's name.
   * @param colorHex  The player's color in hex (e.g. "#FF0000").
   * @return A new LudoPlayer instance with the given attributes.
   */
  public static Player createLudoPlayer(String name, String colorHex, PlayerTokenType playerTokenType) {
    if (!validatePlayer(name, colorHex, playerTokenType)) {
      throw new IllegalArgumentException("Invalid player parameters.");
    }

    return new LudoPlayer(name, colorHex, playerTokenType);
  }

  private static boolean validatePlayer(String name, String colorHex, PlayerTokenType playerTokenType) {
    return name != null && !name.isEmpty() && colorHex != null && !colorHex.isEmpty() && playerTokenType != null;
  }
}