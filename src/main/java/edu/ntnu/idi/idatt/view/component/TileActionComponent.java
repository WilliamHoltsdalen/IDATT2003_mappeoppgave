package edu.ntnu.idi.idatt.view.component;

import edu.ntnu.idi.idatt.dto.ComponentSpec;
import edu.ntnu.idi.idatt.model.LadderAction;
import edu.ntnu.idi.idatt.model.PortalAction;
import edu.ntnu.idi.idatt.model.SlideAction;
import edu.ntnu.idi.idatt.model.Tile;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class TileActionComponent extends ImageView {
  private final String type;
  private final String imagePath;
  private final Tile tile;
  private final int destinationTileId;
  private final ComponentSpec spec;
  private int portalColorNumber = 1; // Default to color 1

  public TileActionComponent(String type, String imagePath, Tile tile, int destinationTileId) {
    super(new Image(imagePath));
    this.type = type;
    this.imagePath = imagePath;
    this.tile = tile;
    this.destinationTileId = destinationTileId;
    this.spec = ComponentSpec.fromFilename(imagePath.substring(imagePath.lastIndexOf("/") + 1));
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

  public void setPortalColorNumber(int colorNumber) {
    if (colorNumber < 1 || colorNumber > 3) {
      throw new IllegalArgumentException("Portal color number must be between 1 and 3");
    }
    this.portalColorNumber = colorNumber;
    createTileAction(); // Recreate the action with the new color
  }

  public int getPortalColorNumber() {
    return portalColorNumber;
  }

  public void updateSizeAndPosition(double tileWidth, double tileHeight, double baseX, double baseY) {
    double width = spec.calculateWidth(tileWidth);
    double height = spec.calculateHeight(tileHeight);

    setFitWidth(width);
    setFitHeight(height);

    // Adjust offset for components that start from the center of the tile (portals are exceptions as they take up one tile.)
    double offsetX = !type.equals("PORTAL") ? tileWidth / 2 : 0;
    double offsetY = !type.equals("PORTAL") ? -tileHeight / 2 : 0;

    setTranslateX(offsetX + baseX + spec.calculateTranslateX(tileWidth));
    setTranslateY(offsetY + baseY + spec.calculateTranslateY(tileHeight));
  }

  private void createTileAction() {
    String typeName = type.substring(0, 1) + type.substring(1).toLowerCase();
    StringBuilder identifier = new StringBuilder();
    identifier.append(spec.widthTiles());
    identifier.append(spec.widthDirection() == ComponentSpec.Direction.LEFT ? "L" : "R");
    identifier.append("_");
    identifier.append(spec.heightTiles());
    identifier.append(spec.heightDirection() == ComponentSpec.Direction.DOWN ? "D" : "U");
    identifier.append("_");
    identifier.append(type.toLowerCase());
    if (type.equals("PORTAL")) {
        identifier.append("_").append(portalColorNumber); // Add the color number suffix for portals
    }

    String description = typeName + " from " + tile.getTileId() + " to " + destinationTileId;
    
    switch (type) {
        case "LADDER" -> tile.setLandAction(new LadderAction(identifier.toString(), destinationTileId, description));
        case "SLIDE" -> tile.setLandAction(new SlideAction(identifier.toString(), destinationTileId, description));
        case "PORTAL" -> tile.setLandAction(new PortalAction(identifier.toString(), destinationTileId, description));
        default -> throw new IllegalArgumentException("Unknown tile action type: " + type);
    }
  }
}