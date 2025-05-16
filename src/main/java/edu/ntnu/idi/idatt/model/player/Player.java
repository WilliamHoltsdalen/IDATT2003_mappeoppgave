package edu.ntnu.idi.idatt.model.player;

import static edu.ntnu.idi.idatt.model.validator.ArgumentValidator.playerSetColorHexValidator;
import static edu.ntnu.idi.idatt.model.validator.ArgumentValidator.playerSetNameValidator;
import static edu.ntnu.idi.idatt.model.validator.ArgumentValidator.playerSetPlayerTokenTypeValidator;

/**
 * <h3>Player class</h3>
 *
 * <p>This class represents a player in the game. It contains a name, a color, a player token type,
 * and the current tile of the player.
 */
public abstract class Player {
  private String name;
  private String colorHex;
  private PlayerTokenType playerTokenType;
  private boolean isBot;

  /**
   * Constructor for Player class.
   *
   * @param name The name of the player.
   * @param colorHex The color of the player in hex format.
   * @param playerTokenType The type of player token to use for the player.
   * @param isBot Whether the player is a bot or not.
   */
  public Player(String name, String colorHex, PlayerTokenType playerTokenType, boolean isBot) {
    setName(name);
    setColorHex(colorHex);
    setPlayerTokenType(playerTokenType);
    this.isBot = isBot;
  }

  /**
   * Returns the name of the player.
   *
   * @return The name of the player.
   */
  public String getName() {
    return name;
  }

  /**
   * Returns the color of the player in hex format.
   *
   * @return The color of the player in hex format.
   */
  public String getColorHex() {
    return colorHex;
  }

  /**
   * Returns the type of player token to use for the player.
   *
   * @return the type of player token to use for the player
   */
  public PlayerTokenType getPlayerTokenType() {
    return playerTokenType;
  }

  /**
   * Returns whether the player is a bot or not.
   *
   * @return true if the player is a bot, false otherwise.
   */
  public boolean isBot() {
    return isBot;
  }

  /**
   * Sets the name of the player.
   *
   * @param name The name to set.
   */
  public void setName(String name) {
    playerSetNameValidator(name);

    this.name = name;
  }

  /**
   * Sets the color of the player in hex format.
   *
   * @param colorHex The color to set.
   */
  public void setColorHex(String colorHex) {
    playerSetColorHexValidator(colorHex);

    this.colorHex = colorHex;
  }

  /**
   * Sets the type of player token to use for the player.
   *
   * @param playerTokenType the type of player token to use for the player
   */
  public void setPlayerTokenType(PlayerTokenType playerTokenType) {
    playerSetPlayerTokenTypeValidator(playerTokenType);

    this.playerTokenType = playerTokenType;
  }

  /**
   * Sets whether the player is a bot or not.
   *
   * @param isBot Whether the player is a bot or not.
   */
  public void setBot(boolean isBot) {
    this.isBot = isBot;
  }
}
