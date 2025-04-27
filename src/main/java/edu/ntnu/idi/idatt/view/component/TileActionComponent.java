package edu.ntnu.idi.idatt.view.component;

import edu.ntnu.idi.idatt.model.LadderAction;
import edu.ntnu.idi.idatt.model.Tile;
import edu.ntnu.idi.idatt.view.util.ViewUtils;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class TileActionComponent extends ImageView {
  private final String type;
  private final String imagePath;
  private final Tile tile;
  private final int destinationTileId;

  public TileActionComponent(String type, String imagePath, Tile tile, int destinationTileId) {
    this.type = type;
    this.imagePath = imagePath;
    this.tile = tile;
    this.destinationTileId = destinationTileId;

    setImage(new Image(imagePath));
    setPreserveRatio(true);
    System.out.println("Setting tile action " + type + " for " + tile.getTileId() + " to " + destinationTileId);
    createTileAction();
    this.getStyleClass().add("tile-action-component");
  }

  public String getType() {
    return type;
  }

  public String getImagePath() {
    return imagePath;
  }

  public Tile getTile() {
    return tile;
  }

  public int getDestinationTileId() {
    return destinationTileId;
  }

  private void createTileAction() {
    switch (type) {
      case "LADDER" -> tile.setLandAction(new LadderAction(destinationTileId, "Ladder from " + tile.getTileId() + " to " + destinationTileId));
      case "PORTAL" -> {
        int randomDestination = ViewUtils.randomPortalDestination(destinationTileId);
        tile.setLandAction(new LadderAction(randomDestination, "Portal from " + tile.getTileId() + " to " + randomDestination));
      }
      case "SLIDE" -> tile.setLandAction(new LadderAction(destinationTileId, "Slide from " + tile.getTileId() + " to " + destinationTileId));
      default -> {break;}
    }
  }
}