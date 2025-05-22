package edu.ntnu.idi.idatt.filehandler;

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


import edu.ntnu.idi.idatt.model.player.LadderGamePlayer;
import edu.ntnu.idi.idatt.model.player.LudoPlayer;
import edu.ntnu.idi.idatt.model.player.Player;
import edu.ntnu.idi.idatt.model.player.PlayerTokenType;

/**
 * <h3>FileHandler implementation for Player objects.</h3>
 *
 * <p>This class provides methods for reading and writing Player objects to and from csv formatted
 * files.
 */
public class PlayerFileHandlerCsv implements FileHandler<Player> {

  private static final Logger logger = LoggerFactory.getLogger(PlayerFileHandlerCsv.class);


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
          logger.debug("Detected ladder game player type: {}", playerType);
          continue;
        } else if (line.equals("name, colorHex, isBot")) {
          playerType = "ludoPlayer";
          logger.debug("Detected ludo game player type: {}", playerType);
          continue;
        }

        Player player = null;
        if (playerType.equals("ladderGamePlayer")) {
          player = ladderGamePlayerFromCsvLine(line);
        } else if (playerType.equals("ludoPlayer")) {
          player = ludoPlayerFromCsvLine(line);
        }

        if (player == null) {
          logger.error("Failed to parse player from line: '{}'", line);
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

  public Player ladderGamePlayerFromCsvLine(String line) {
    String[] parts = line.split(",");
    if (parts.length != 4) {
      logger.debug("Invalid ladder game player CSV line: '{}'", line);
      return null;
    }
    return new LadderGamePlayer(parts[0].trim(), parts[1].trim(), 
        PlayerTokenType.valueOf(parts[2].trim()), Boolean.parseBoolean(parts[3].trim()));
  }

  public Player ludoPlayerFromCsvLine(String line) {
    String[] parts = line.split(",");
    if (parts.length != 3) {
      logger.debug("Invalid ludo player CSV line: '{}'", line);
      return null;
    }
    return new LudoPlayer(parts[0].trim(), parts[1].trim(), 
        PlayerTokenType.CIRCLE, Boolean.parseBoolean(parts[2].trim()));
  }

  public String toCsvLine(Player player) {
    if (player instanceof LudoPlayer) {
      return String.format("%s,%s,%s", player.getName(), player.getColorHex(), player.isBot());
    } else {
      return String.format("%s,%s,%s,%s", player.getName(), player.getColorHex(),
          player.getPlayerTokenType().name(), player.isBot());
    }
  }
}
