package edu.ntnu.idi.idatt.model;

import static edu.ntnu.idi.idatt.model.validators.ArgumentValidator.playerPlaceOnTileValidator;
import static edu.ntnu.idi.idatt.model.validators.ArgumentValidator.playerSetColorHexValidator;
import static edu.ntnu.idi.idatt.model.validators.ArgumentValidator.playerSetNameValidator;

/**
 * <h3>Player class</h3>
 *
 * <p>This class represents a player in the game. It contains a name, a color, and a current tile.
 */
public class Player {
  private String name;
  private String colorHex;
  private Tile currentTile;

  /**
   * Constructor for Player class.
   *
   * @param name The name of the player.
   * @param colorHex The color of the player in hex format.
   */
  public Player(String name, String colorHex) {
    setName(name);
    setColorHex(colorHex);
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
   * Returns the current tile of the player.
   *
   * @return The current tile of the player.
   */
  public Tile getCurrentTile() {
    return currentTile;
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
   * Places the player on the given tile.
   *
   * @param tile The tile to place the player on.
   */
  public void placeOnTile(Tile tile) {
    playerPlaceOnTileValidator(tile);

    this.currentTile = tile;
  }
}
