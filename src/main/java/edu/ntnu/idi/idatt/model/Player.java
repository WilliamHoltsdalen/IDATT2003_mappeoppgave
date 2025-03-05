package edu.ntnu.idi.idatt.model;

import static edu.ntnu.idi.idatt.model.validators.ArgumentValidator.playerPlaceOnTileValidator;
import static edu.ntnu.idi.idatt.model.validators.ArgumentValidator.playerSetNameValidator;

public class Player {
  private String name;
  private Tile currentTile;

  public Player(String name){
    setName(name);
  }

  public String getName() {
    return name;
  }

  public Tile getCurrentTile() {
    return currentTile;
  }

  public void setName(String name) {
    playerSetNameValidator(name);

    this.name = name;
  }

  public void placeOnTile(Tile tile) {
    playerPlaceOnTileValidator(tile);

    this.currentTile = tile;
  }

  public void move(int steps) {
  }
}
