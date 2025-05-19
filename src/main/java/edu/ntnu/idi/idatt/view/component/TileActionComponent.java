package edu.ntnu.idi.idatt.view.component;

import edu.ntnu.idi.idatt.dto.ComponentSpec;
import edu.ntnu.idi.idatt.model.tile.LadderAction;
import edu.ntnu.idi.idatt.model.tile.LadderGameTile;
import edu.ntnu.idi.idatt.model.tile.PortalAction;
import edu.ntnu.idi.idatt.model.tile.SlideAction;
import edu.ntnu.idi.idatt.model.tile.Tile;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * TileActionComponent.
 *
 * <p>A JavaFX {@link ImageView} used to display visual representations of tile actions
 * (like ladders, slides, or portals) on a game board.</p>
 *
 * <p>It links an image asset to a specific {@link Tile} and its action, using a
 * {@link ComponentSpec} to determine its size and positioning relative to the tile. It also handles
 * the creation of the corresponding action (e.g., {@link LadderAction}, {@link PortalAction}) on
 * the tile itself.</p>
 *
 * @see ImageView
 * @see ComponentSpec
 * @see Tile
 * @see LadderGameTile
 */
public class TileActionComponent extends ImageView {

  private final String type;
  private final String imagePath;
  private final Tile tile;
  private final int destinationTileId;
  private final ComponentSpec spec;
  private int portalColorNumber = 1; // Default to color 1

  /**
   * Constructs a TileActionComponent.
   *
   * @param type              The type of the action component (e.g., "LADDER", "SLIDE", "PORTAL").
   * @param imagePath         The path to the image resource for this component.
   * @param tile              The {@link Tile} this component is associated with (where the action
   *                          originates).
   * @param destinationTileId The ID of the tile where this action leads.
   */
  public TileActionComponent(String type, String imagePath, Tile tile, int destinationTileId) {
    super(new Image(imagePath, true));
    this.type = type;
    this.imagePath = imagePath;
    this.tile = tile;
    this.destinationTileId = destinationTileId;
    this.spec = ComponentSpec.fromFilename(imagePath.substring(imagePath.lastIndexOf("/") + 1));
    createTileAction();
    this.getStyleClass().add("tile-action-component");
  }

  /**
   * Gets the type of this action component.
   *
   * @return The component type string.
   */
  public String getType() {
    return type;
  }

  /**
   * Gets the image path for this action component.
   *
   * @return The image path string.
   */
  public String getImagePath() {
    return imagePath;
  }

  /**
   * Gets the source {@link Tile} for this action component.
   *
   * @return The tile where the action originates.
   */
  public Tile getTile() {
    return tile;
  }

  /**
   * Gets the ID of the destination tile for this action.
   *
   * @return The destination tile ID.
   */
  public int getDestinationTileId() {
    return destinationTileId;
  }

  /**
   * Sets the color number for portal components (1-3). This will also recreate the underlying
   * {@link PortalAction} on the tile.
   *
   * @param colorNumber The portal color number.
   * @throws IllegalArgumentException if colorNumber is not between 1 and 3.
   */
  public void setPortalColorNumber(int colorNumber) {
    if (colorNumber < 1 || colorNumber > 3) {
      throw new IllegalArgumentException("Portal color number must be between 1 and 3");
    }
    this.portalColorNumber = colorNumber;
    createTileAction(); // Recreate the action with the new color
  }

  /**
   * Gets the current color number for portal components.
   *
   * @return The portal color number (1-3).
   */
  public int getPortalColorNumber() {
    return portalColorNumber;
  }

  /**
   * Updates the size and position of this component based on tile dimensions and a base coordinate.
   * Uses the internal {@link ComponentSpec} for calculations.
   *
   * @param tileWidth  The width of a single tile on the board.
   * @param tileHeight The height of a single tile on the board.
   * @param baseX      The base x-coordinate for positioning (usually the x-coordinate of the source
   *                   tile).
   * @param baseY      The base y-coordinate for positioning (usually the y-coordinate of the source
   *                   tile).
   */
  public void updateSizeAndPosition(double tileWidth, double tileHeight, double baseX,
      double baseY) {
    double width = spec.calculateWidth(tileWidth);
    double height = spec.calculateHeight(tileHeight);

    setFitWidth(width);
    setFitHeight(height);

    // Adjust offset for components that start from the center of the tile (portals are exceptions
    // as they take up exactly one tile.)
    double offsetX = !type.equals("PORTAL") ? tileWidth / 2 : 0;
    double offsetY = !type.equals("PORTAL") ? -tileHeight / 2 : 0;

    setTranslateX(offsetX + baseX + spec.calculateTranslateX(tileWidth));
    setTranslateY(offsetY + baseY + spec.calculateTranslateY(tileHeight));
  }

  /**
   * Creates and sets the appropriate tile action ({@link LadderAction}, {@link SlideAction}, or
   * {@link PortalAction}) on the associated {@link LadderGameTile}. The action's identifier and
   * description are derived from the component's properties and {@link ComponentSpec}.
   *
   * @throws IllegalArgumentException if the component type is unknown.
   */
  private void createTileAction() {
    final String typeName = type.substring(0, 1) + type.substring(1).toLowerCase();
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
      case "LADDER" -> ((LadderGameTile) tile).setLandAction(
          new LadderAction(identifier.toString(), destinationTileId, description));
      case "SLIDE" -> ((LadderGameTile) tile).setLandAction(
          new SlideAction(identifier.toString(), destinationTileId, description));
      case "PORTAL" -> ((LadderGameTile) tile).setLandAction(
          new PortalAction(identifier.toString(), destinationTileId, description));
      default -> throw new IllegalArgumentException("Unknown tile action type: " + type);
    }
  }
}