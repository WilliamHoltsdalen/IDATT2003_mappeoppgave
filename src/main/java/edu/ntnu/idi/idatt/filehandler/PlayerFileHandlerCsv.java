package edu.ntnu.idi.idatt.filehandler;

import edu.ntnu.idi.idatt.model.player.LadderGamePlayer;
import edu.ntnu.idi.idatt.model.player.LudoPlayer;
import edu.ntnu.idi.idatt.model.player.Player;
import edu.ntnu.idi.idatt.model.player.PlayerTokenType;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * PlayerFileHandlerCsv.
 *
 * <p>Implements the {@link FileHandler} interface for {@link Player} objects, providing
 * functionality to read player data from and write player data to CSV (Comma Separated Values)
 * formatted files. This handler can distinguish between {@link LadderGamePlayer} and
 * {@link LudoPlayer} types based on the CSV header and structure.</p>
 *
 * <p>When reading, it determines the player type from the header line and parses subsequent lines
 * accordingly. For Ludo players, it expects "name, colorHex, isBot". For Ladder Game players,
 * it expects "name, colorHex, playerTokenType, isBot".</p>
 *
 * <p>When writing, it generates the appropriate header based on the type of the first player in
 * the list and then formats each player object into a CSV line.</p>
 *
 * @see FileHandler
 * @see Player
 * @see LudoPlayer
 * @see LadderGamePlayer
 */
public class PlayerFileHandlerCsv implements FileHandler<Player> {

  private static final Logger logger = LoggerFactory.getLogger(PlayerFileHandlerCsv.class);


  /**
   * Reads a list of {@link Player} objects from a CSV file at the specified path.
   * The method automatically detects if the players are {@link LudoPlayer}s or
   * {@link LadderGamePlayer}s based on the header row in the CSV file.
   *
   * @param path The path to the CSV file.
   * @return A list of {@link Player} objects deserialized from the file. Returns an empty list
   *         if a player line is malformed or if an I/O error (other than file not found) occurs.
   * @throws IOException if the file cannot be found or if an I/O error occurs during reading that
   *                     prevents processing (e.g., permissions issues).
   */
  @Override
  public List<Player> readFile(String path) throws IOException {
    logger.debug("Reading players from CSV file: {}", path);
    List<Player> players = new ArrayList<>();
    try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
      String line;
      String playerType = "";
      while ((line = reader.readLine()) != null) {
        if (playerType.equals("ludoPlayer") && players.size() == 4) {
          break;
        }
        if (line.equals("name, colorHex, playerTokenType, isBot")) {
          playerType = "ladderGamePlayer";
          logger.debug("Detected ladder game player type.");
          continue;
        } else if (line.equals("name, colorHex, isBot")) {
          playerType = "ludoPlayer";
          logger.debug("Detected ludo game player type.");
          continue;
        }

        Player player = null;
        if (playerType.equals("ladderGamePlayer")) {
          player = ladderGamePlayerFromCsvLine(line);
        } else if (playerType.equals("ludoPlayer")) {
          player = ludoPlayerFromCsvLine(line);
        }

        if (player == null) {
          logger.debug("Failed to parse player from line: '{}'", line);
          return Collections.emptyList();
        }
        players.add(player);
      }
      logger.info("Successfully read {} player(s) from file: {}", players.size(), path);
    } catch (IOException e) {
      logger.error("Could not read players form file: {}", path);
      throw new IOException("Could not read players from file: " + path);
    }
    return players;
  }

  /**
   * Writes a list of {@link Player} objects to a CSV file at the specified path.
   * The CSV header and line format are determined by the type of the first player in the list
   * (either {@link LudoPlayer} or {@link LadderGamePlayer}).
   *
   * @param path    The path to the CSV file where players will be written.
   * @param players The list of {@link Player} objects to serialize.
   * @throws IOException if an error occurs during file writing (e.g., path not found, permissions).
   */
  @Override
  public void writeFile(String path, List<Player> players) throws IOException {
    logger.debug("Writing {} players to CSV file: {}", players.size(), path);
    String header;
    if (players.getFirst() instanceof LudoPlayer) {
      header = "name, colorHex, isBot";
    } else {
      header = "name, colorHex, playerTokenType, isBot";
    }
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
      writer.write(header);
      writer.newLine();
      for (Player player : players) {
        writer.write(toCsvLine(player));
        writer.newLine();
      }
      logger.info("Successfully wrote {} player(s) to file: {}", players.size(), path);
    } catch (IOException e) {
      logger.error("Could not write players to file: {}", path);
      throw new IOException("Could not write players to file: " + path);
    }
  }

  /**
   * Parses a line from a CSV file, expecting {@link LadderGamePlayer} data format.
   * The expected format is: "name,colorHex,playerTokenType,isBot".
   *
   * @param line The CSV line string.
   * @return A {@link LadderGamePlayer} object, or {@code null} if the line is malformed.
   */
  public Player ladderGamePlayerFromCsvLine(String line) {
    String[] parts = line.split(",");
    if (parts.length != 4) {
      logger.debug("Invalid ladder game player CSV line: '{}'", line);
      return null;
    }
    return new LadderGamePlayer(parts[0].trim(), parts[1].trim(), 
        PlayerTokenType.valueOf(parts[2].trim()), Boolean.parseBoolean(parts[3].trim()));
  }

  /**
   * Parses a line from a CSV file, expecting {@link LudoPlayer} data format.
   * The expected format is: "name,colorHex,isBot". {@link PlayerTokenType#CIRCLE} is assumed.
   *
   * @param line The CSV line string.
   * @return A {@link LudoPlayer} object, or {@code null} if the line is malformed.
   */
  public Player ludoPlayerFromCsvLine(String line) {
    String[] parts = line.split(",");
    if (parts.length != 3) {
      logger.debug("Invalid ludo player CSV line: '{}'", line);
      return null;
    }
    return new LudoPlayer(parts[0].trim(), parts[1].trim(), 
        PlayerTokenType.CIRCLE, Boolean.parseBoolean(parts[2].trim()));
  }

  /**
   * Converts a {@link Player} object to its CSV string representation.
   * The format depends on whether the player is an instance of {@link LudoPlayer}
   * (name,colorHex,isBot) or another {@link Player} type, assumed to be {@link LadderGamePlayer}
   * (name,colorHex,playerTokenType,isBot).
   *
   * @param player The {@link Player} object to convert.
   * @return A string representing the player in CSV format.
   */
  public String toCsvLine(Player player) {
    if (player instanceof LudoPlayer) {
      return String.format("%s,%s,%s", player.getName(), player.getColorHex(), player.isBot());
    } else {
      return String.format("%s,%s,%s,%s", player.getName(), player.getColorHex(),
          player.getPlayerTokenType().name(), player.isBot());
    }
  }
}
