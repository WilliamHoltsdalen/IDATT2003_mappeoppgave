package edu.ntnu.idi.idatt.entities;

public class Player {
  private String name;
  private Tile currentTile;

  public Player(String name, BoardGame game) throws IllegalArgumentException {
    if (game == null) {
      throw new IllegalArgumentException("Game cannot be null");
    }

    setName(name);
    placeOnTile(game.getBoard().getTile(0));
  }

  public String getName() {
    return name;
  }

  public Tile getCurrentTile() {
    return currentTile;
  }

  public void setName(String name) throws IllegalArgumentException {
    if (name == null || name.isBlank()) {
      throw new IllegalArgumentException("Name cannot be null or blank");
    }
    this.name = name;
  }

  public void placeOnTile(Tile tile) throws IllegalArgumentException {
    if (tile == null) {
      throw new IllegalArgumentException("Tile cannot be null");
    }
    this.currentTile = tile;
  }

  public void move(int steps) {
  }
}
