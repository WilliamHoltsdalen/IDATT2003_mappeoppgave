package edu.ntnu.idi.idatt.model;

import static edu.ntnu.idi.idatt.model.validators.ArgumentValidator.playerPlaceOnTileValidator;
import static edu.ntnu.idi.idatt.model.validators.ArgumentValidator.playerSetColorHexValidator;
import static edu.ntnu.idi.idatt.model.validators.ArgumentValidator.playerSetNameValidator;

public class Player {
  private String name;
  private String colorHex;
  private Tile currentTile;

  public Player(String name, String colorHex){
    setName(name);
    setColorHex(colorHex);
  }

  public String getName() {
    return name;
  }

  public String getColorHex() {
    return colorHex;
  }

  public Tile getCurrentTile() {
    return currentTile;
  }

  public void setName(String name) {
    playerSetNameValidator(name);

    this.name = name;
  }

  public void setColorHex(String colorHex) {
    playerSetColorHexValidator(colorHex);

    this.colorHex = colorHex;
  }

  public void placeOnTile(Tile tile) {
    playerPlaceOnTileValidator(tile);

    this.currentTile = tile;
  }

  public void move(int steps) {
  }
}
