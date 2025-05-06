package edu.ntnu.idi.idatt.filehandler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

  @Override
  public List<Player> readFile(String path) throws IOException {
    List<Player> players = new ArrayList<>();

    try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
      String line;
      while ((line = reader.readLine()) != null) {
        String playerType = "";
        if (line.equals("name, colorHex, playerTokenType")) {
          playerType = "ladderGamePlayer";
          continue;
        } else if (line.equals("name, colorHex")) {
          playerType = "ludoPlayer";
          continue;
        }

        
        Player player = null;
        if (playerType.equals("ladderGamePlayer")) {
          player = LadderGamePlayerfromCsvLine(line);
        } else if (playerType.equals("ludoPlayer")) {
          player = LudoPlayerfromCsvLine(line);
        }

        if (player == null) {
          return Collections.emptyList();
        }
        players.add(player);
      }
    } catch (IOException e) {
      throw new IOException("Could not read players from file: " + path);
    }
    return players;
  }

  @Override
  public void writeFile(String path, List<Player> players) throws IOException {
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
      for (Player player : players) {
        writer.write(toCsvLine(player));
        writer.newLine();
      }
    } catch (IOException e) {
      throw new IOException("Could not write players to file: " + path);
    }
  }

  private Player LadderGamePlayerfromCsvLine(String line) {
    String[] segments = line.split(",");
    if (segments.length != 3) {
      return null;
    }
    try {
      String playerName = segments[0].trim();
      String playerColorHex = segments[1].trim();
      String playerTokenType = segments[2].trim();
      return new LadderGamePlayer(playerName, playerColorHex, PlayerTokenType.valueOf(playerTokenType.toUpperCase()));
    } catch (NumberFormatException e) {
      e.printStackTrace();
    }
    return null;
  }

  private Player LudoPlayerfromCsvLine(String line) {
    String[] segments = line.split(",");
    if (segments.length != 2) {
      return null;
    }
    try {
      String playerName = segments[0].trim();
      String playerColorHex = segments[1].trim();
      return new LudoPlayer(playerName, playerColorHex, PlayerTokenType.CIRCLE);
    } catch (NumberFormatException e) {
      e.printStackTrace();
    }
    return null;
  }

  private String toCsvLine(Player player) {
    return String.format("%s,%s,%s", player.getName(), player.getColorHex(), player.getPlayerTokenType().name());
  }

}
