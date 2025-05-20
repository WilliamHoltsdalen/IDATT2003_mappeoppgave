package edu.ntnu.idi.idatt.factory.player;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import edu.ntnu.idi.idatt.filehandler.PlayerFileHandlerCsv;
import edu.ntnu.idi.idatt.model.player.LadderGamePlayer;
import edu.ntnu.idi.idatt.model.player.LudoPlayer;
import edu.ntnu.idi.idatt.model.player.Player;
import edu.ntnu.idi.idatt.model.player.PlayerTokenType;
import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class PlayerFactoryTest {

  @Test
  @DisplayName("Test creating a LadderGamePlayer with valid parameters")
  void testCreateLadderGamePlayer_validParameters() {
    String name = "Player1";
    String colorHex = "#FF0000";
    PlayerTokenType tokenType = PlayerTokenType.CIRCLE;
    boolean isBot = false;

    Player player = PlayerFactory.createLadderGamePlayer(name, colorHex, tokenType, isBot);

    assertNotNull(player);
    assertInstanceOf(LadderGamePlayer.class, player);
    assertEquals(name, player.getName());
    assertEquals(colorHex, player.getColorHex());
    assertEquals(tokenType, player.getPlayerTokenType());
    assertEquals(isBot, player.isBot());
  }

  @Test
  @DisplayName("Test creating a LadderGamePlayer with invalid name throws IllegalArgumentException")
  void testCreateLadderGamePlayer_invalidName() {
    assertThrows(IllegalArgumentException.class, () -> {
      PlayerFactory.createLadderGamePlayer(null, "#FF0000", PlayerTokenType.CIRCLE, false);
    });
    assertThrows(IllegalArgumentException.class, () -> {
      PlayerFactory.createLadderGamePlayer("", "#FF0000", PlayerTokenType.CIRCLE, false);
    });
  }

  @Test
  @DisplayName("Test creating a LadderGamePlayer with invalid colorHex throws IllegalArgumentException")
  void testCreateLadderGamePlayer_invalidColorHex() {
    assertThrows(IllegalArgumentException.class, () -> {
      PlayerFactory.createLadderGamePlayer("Player1", null, PlayerTokenType.CIRCLE, false);
    });
    assertThrows(IllegalArgumentException.class, () -> {
      PlayerFactory.createLadderGamePlayer("Player1", "", PlayerTokenType.CIRCLE, false);
    });
  }

  @Test
  @DisplayName("Test creating a LadderGamePlayer with null token type throws IllegalArgumentException")
  void testCreateLadderGamePlayer_nullTokenType() {
    assertThrows(IllegalArgumentException.class, () -> {
      PlayerFactory.createLadderGamePlayer("Player1", "#FF0000", null, false);
    });
  }

  @Test
  @DisplayName("Test creating a LudoPlayer with valid parameters")
  void testCreateLudoPlayer_validParameters() {
    String name = "Player2";
    String colorHex = "#0000FF";
    PlayerTokenType tokenType = PlayerTokenType.TRIANGLE;
    boolean isBot = true;

    Player player = PlayerFactory.createLudoPlayer(name, colorHex, tokenType, isBot);

    assertNotNull(player);
    assertInstanceOf(LudoPlayer.class, player);
    assertEquals(name, player.getName());
    assertEquals(colorHex, player.getColorHex());
    assertEquals(tokenType, player.getPlayerTokenType());
    assertEquals(isBot, player.isBot());
  }

 @Test
  @DisplayName("Test creating a LudoPlayer with invalid name throws IllegalArgumentException")
  void testCreateLudoPlayer_invalidName() {
    assertThrows(IllegalArgumentException.class, () -> {
      PlayerFactory.createLudoPlayer(null, "#0000FF", PlayerTokenType.TRIANGLE, true);
    });
    assertThrows(IllegalArgumentException.class, () -> {
      PlayerFactory.createLudoPlayer("", "#0000FF", PlayerTokenType.TRIANGLE, true);
    });
  }

  @Test
  @DisplayName("Test creating a LudoPlayer with invalid colorHex throws IllegalArgumentException")
  void testCreateLudoPlayer_invalidColorHex() {
    assertThrows(IllegalArgumentException.class, () -> {
      PlayerFactory.createLudoPlayer("Player2", null, PlayerTokenType.TRIANGLE, true);
    });
    assertThrows(IllegalArgumentException.class, () -> {
      PlayerFactory.createLudoPlayer("Player2", "", PlayerTokenType.TRIANGLE, true);
    });
  }

  @Test
  @DisplayName("Test creating a LudoPlayer with null token type throws IllegalArgumentException")
  void testCreateLudoPlayer_nullTokenType() {
    assertThrows(IllegalArgumentException.class, () -> {
      PlayerFactory.createLudoPlayer("Player2", "#0000FF", null, true);
    });
  }

  @Test
  @DisplayName("Test createPlayersFromFile with valid file path returns players")
  void testCreatePlayersFromFile_validPath() throws IOException {
    Player mockPlayer = mock(Player.class);
    try (var csvHandlerMockedConstruction = Mockito.mockConstruction(PlayerFileHandlerCsv.class,
        (mock, context) -> {
          when(mock.readFile(anyString())).thenReturn(List.of(mockPlayer));
        })) {

      List<Player> players = PlayerFactory.createPlayersFromFile("valid/path.csv");
      assertNotNull(players);
      assertEquals(1, players.size());
      assertEquals(mockPlayer, players.getFirst());
      assertEquals(1, csvHandlerMockedConstruction.constructed().size());
    }
  }

 @Test
  @DisplayName("Test createPlayersFromFile with null or empty path returns empty list")
  void testCreatePlayersFromFile_nullOrEmptyPath() throws IOException {
    List<Player> playersEmpty = PlayerFactory.createPlayersFromFile("");
    assertNotNull(playersEmpty);
    assertTrue(playersEmpty.isEmpty());

    List<Player> playersNull = PlayerFactory.createPlayersFromFile(null);
    assertNotNull(playersNull);
    assertTrue(playersNull.isEmpty());
  }

  @Test
  @DisplayName("Test createPlayersFromFile when handler throws IOException")
  void testCreatePlayersFromFile_handlerThrowsIOException() {
    // Mocking PlayerFileHandlerCsv
    try (var csvHandlerMockedConstruction = Mockito.mockConstruction(PlayerFileHandlerCsv.class,
        (mock, context) -> {
          when(mock.readFile(anyString())).thenThrow(new IOException("Test Exception"));
        })) {

      assertThrows(IOException.class, () -> {
        PlayerFactory.createPlayersFromFile("throwing/path.csv");
      });
      assertEquals(1, csvHandlerMockedConstruction.constructed().size());
    }
  }
} 