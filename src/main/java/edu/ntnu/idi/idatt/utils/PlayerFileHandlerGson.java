package edu.ntnu.idi.idatt.utils;

import edu.ntnu.idi.idatt.model.Player;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.ntnu.idi.idatt.utils.interfaces.FileHandler;

public class PlayerFileHandlerGson implements FileHandler<Player>{

  @Override
  public List<Player> readFile(String path) throws IOException {
    List<Player> players = new ArrayList<>();

    try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
      String line;
      while ((line = reader.readLine()) != null) {
        if (line.equals("name, colorHex")) {
          continue;
        }

        Player player = fromCsvLine(line);
        if (player == null) {
          return Collections.emptyList();
        }
        players.add(player);
      }
    } catch (IOException e) {
      throw new IOException("Could not read file: " + path);
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
      e.printStackTrace();
    }
  }

  private Player fromCsvLine(String line) {
    String[] segments = line.split(",");
    if (segments.length != 2) {
      return null;
    }
    try {
      String playerName = segments[0].trim();
      String playerColorHex = segments[1].trim();
      return new Player(playerName, playerColorHex);
    } catch (NumberFormatException e) {
      e.printStackTrace();
    }
    return null;
  }

  private String toCsvLine(Player player) {
    return String.format("%s,%s", player.getName(), player.getColorHex());
  }

}
