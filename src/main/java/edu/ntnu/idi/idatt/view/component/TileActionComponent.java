package edu.ntnu.idi.idatt.view.component;

import edu.ntnu.idi.idatt.dto.ComponentSpec;
import edu.ntnu.idi.idatt.model.LadderAction;
import edu.ntnu.idi.idatt.model.Tile;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class TileActionComponent extends ImageView {
  private final String type;
  private final String imagePath;
  private final Tile tile;
  private final int destinationTileId;
  private final ComponentSpec spec;

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

  public void updateSizeAndPosition(double tileWidth, double tileHeight, double baseX, double baseY) {
    // Calculate size based on tile dimensions
    double width = spec.calculateWidth(tileWidth);
    double height = spec.calculateHeight(tileHeight);

    setFitWidth(width);
    setFitHeight(height);

    // Adjust offset for other components than portals, to make them start from the center of the tile
    double offsetX = !type.equals("PORTAL") ? tileWidth / 2 : 0;
    double offsetY = !type.equals("PORTAL") ? -tileHeight / 2 : 0;

    setTranslateX(offsetX + baseX + spec.calculateTranslateX(tileWidth));
    setTranslateY(offsetY + baseY + spec.calculateTranslateY(tileHeight));
  }

  private void createTileAction() {
    String typeName = type.substring(0, 1) + type.substring(1).toLowerCase();
    
    tile.setLandAction(new LadderAction(destinationTileId, typeName + " from " + tile.getTileId() + " to " + destinationTileId));
  }
}