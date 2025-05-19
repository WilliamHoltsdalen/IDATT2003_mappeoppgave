package edu.ntnu.idi.idatt.filehandler;

import edu.ntnu.idi.idatt.model.player.LadderGamePlayer;
import edu.ntnu.idi.idatt.model.player.LudoPlayer;
import edu.ntnu.idi.idatt.model.player.Player;
import edu.ntnu.idi.idatt.model.player.PlayerTokenType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerFileHandlerCsvTest {
  
  private PlayerFileHandlerCsv fileHandler;
  
  @BeforeEach
  void setUp() {
    fileHandler = new PlayerFileHandlerCsv();
  }
  
  @Nested
  @DisplayName("toCsvLine Tests")
  class ToCsvLineTests {
    
    @Test
    @DisplayName("Test with LadderGamePlayer")
    void testToCsvLine_LadderGamePlayer() {
      Player player = new LadderGamePlayer("Player 1", "#FF0000", PlayerTokenType.TRIANGLE, false);
      String expectedCsv = "Player 1,#FF0000,TRIANGLE,false";
      String actualCsv = fileHandler.toCsvLine(player);
      assertEquals(expectedCsv, actualCsv);
    }
    
    @Test
    @DisplayName("Test with LudoPlayer")
    void testToCsvLine_LudoPlayer() {
      Player player = new LudoPlayer("Player 2", "#0000FF", PlayerTokenType.CIRCLE, true);
      String expectedCsv = "Player 2,#0000FF,true"; // name,colorHex,isBot
      String actualCsv = fileHandler.toCsvLine(player);
      assertEquals(expectedCsv, actualCsv);
    }
  }
  
  @Nested
  @DisplayName("ladderGamePlayerfromCsvLine Tests")
  class LadderGamePlayerFromCsvLineTests {
    
    @Test
    @DisplayName("Valid CSV line for LadderGamePlayer")
    void testValidLine() {
      String line = "Player 3,#00FF00,TRIANGLE,true";
      LadderGamePlayer player = (LadderGamePlayer) fileHandler.ladderGamePlayerFromCsvLine(line);
      assertNotNull(player);
      assertEquals("Player 3", player.getName());
      assertEquals("#00FF00", player.getColorHex());
      assertEquals(PlayerTokenType.TRIANGLE, player.getPlayerTokenType());
      assertTrue(player.isBot());
    }
    
    @Test
    @DisplayName("CSV line with incorrect number of parts for LadderGamePlayer")
    void testIncorrectParts() {
      String line = "Player 4,#FFFF00,true"; // Missing a part for 4-field format
      Player player = fileHandler.ladderGamePlayerFromCsvLine(line);
      assertNull(player);
    }
    
    @Test
    @DisplayName("CSV line with invalid PlayerTokenType for LadderGamePlayer")
    void testInvalidTokenType() {
      String line = "Player 5,#123456,INVALID_TOKEN,false";
      assertThrows(IllegalArgumentException.class, () -> {
        fileHandler.ladderGamePlayerFromCsvLine(line);
      });
    }
    @Test
    @DisplayName("CSV line with non-true/false boolean string for isBot (LadderGamePlayer)")
    void testNonStandardBooleanString() {
      String line = "Player 6,#654321,CIRCLE,not_a_boolean";
      LadderGamePlayer player = (LadderGamePlayer) fileHandler.ladderGamePlayerFromCsvLine(line);
      assertNotNull(player);
      assertFalse(player.isBot(), "Boolean.parseBoolean('not_a_boolean') should result in false");
    }
  }
  
  @Nested
  @DisplayName("ludoPlayerfromCsvLine Tests")
  class LudoPlayerFromCsvLineTests {
    
    @Test
    @DisplayName("Valid 3-field CSV line for LudoPlayer")
    void testValidLine_LudoFormat() {
      String line = "Player 7,#ABCDEF,false"; // name,colorHex,isBot
      LudoPlayer player = (LudoPlayer) fileHandler.ludoPlayerFromCsvLine(line);
      assertNotNull(player);
      assertEquals("Player 7", player.getName());
      assertEquals("#ABCDEF", player.getColorHex());
      assertEquals(PlayerTokenType.CIRCLE, player.getPlayerTokenType(), "LudoPlayer should default to CIRCLE token type");
      assertFalse(player.isBot());
    }
    
    @Test
    @DisplayName("Valid 3-field CSV line for LudoPlayer")
    void testValidLine_LudoFormat_isBotTrue() {
      String line = "Player 8,#123456,true"; // name,colorHex,isBot
      LudoPlayer player = (LudoPlayer) fileHandler.ludoPlayerFromCsvLine(line);
      assertNotNull(player);
      assertEquals("Player 8", player.getName());
      assertEquals("#123456", player.getColorHex());
      assertEquals(PlayerTokenType.CIRCLE, player.getPlayerTokenType());
      assertTrue(player.isBot());
    }
    
    @Test
    @DisplayName("CSV line with incorrect number of parts for LudoPlayer")
    void testIncorrectParts_LudoFormat() {
      String lineTooFew = "Player 9,#FEDCBA"; // Too few parts
      Player player1 = fileHandler.ludoPlayerFromCsvLine(lineTooFew);
      assertNull(player1);
      
      String lineTooMany = "Player 10,#1A2B3C,SQUARE,true"; // Too many parts for 3-field format
      Player player2 = fileHandler.ludoPlayerFromCsvLine(lineTooMany);
      assertNull(player2);
    }
    
    @Test
    @DisplayName("CSV line with non-true/false boolean string for isBot (LudoPlayer)")
    void testNonStandardBooleanString_LudoFormat() {
      String line = "Player 11,#C0FFEE,not_true";
      LudoPlayer player = (LudoPlayer) fileHandler.ludoPlayerFromCsvLine(line);
      assertNotNull(player);
      assertFalse(player.isBot(), "Boolean.parseBoolean('not_true') should result in false");
    }
  }
} 