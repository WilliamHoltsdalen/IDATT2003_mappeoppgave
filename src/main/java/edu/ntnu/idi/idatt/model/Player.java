package edu.ntnu.idi.idatt.model;

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
