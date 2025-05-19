package edu.ntnu.idi.idatt.dto;

/**
 * <h3>ComponentSpec.</h3>
 *
 * <p>A record that defines the specifications for a game board component (e.g., Ladder, Slide,
 * Portal).</p>
 *
 * <p>It includes dimensions in terms of tiles, direction, and type of the component.
 * This class also provides utility methods to parse specifications from a filename and calculate
 * actual pixel dimensions and translations based on tile sizes.</p>
 *
 * @see Direction
 * @see ComponentType
 */
public record ComponentSpec(
    int widthTiles,
    Direction widthDirection,
    int heightTiles,
    Direction heightDirection,
    ComponentType type
) {
  /**
   * Enum representing the direction of a component's dimension (width or height).
   */
  public enum Direction {
    UP('U'),
    DOWN('D'),
    LEFT('L'),
    RIGHT('R');

    Direction(char code) {
    }

    /**
     * Converts a character code to its corresponding Direction enum.
     *
     * @param code The character code ('U', 'D', 'L', 'R').
     * @return The {@link Direction} enum.
     * @throws IllegalArgumentException if the code is invalid.
     */
    public static Direction fromCode(char code) {
      return switch (code) {
        case 'U' -> UP;
        case 'D' -> DOWN;
        case 'L' -> LEFT;
        case 'R' -> RIGHT;
        default -> throw new IllegalArgumentException("Invalid direction code: " + code);
      };
    }
  }

  /**
   * Enum representing the type of game board component.
   */
  public enum ComponentType {
    LADDER,
    SLIDE,
    PORTAL;

    /**
     * Converts a string to its corresponding ComponentType enum.
     * The comparison is case-insensitive.
     *
     * @param type The string representation of the component type (e.g., "ladder", "slide",
     *             "portal").
     * @return The {@link ComponentType} enum.
     * @throws IllegalArgumentException if the type string is invalid.
     */
    public static ComponentType fromString(String type) {
      return switch (type.toLowerCase()) {
        case "ladder" -> LADDER;
        case "slide" -> SLIDE;
        case "portal" -> PORTAL;
        default -> throw new IllegalArgumentException("Invalid component type: " + type);
      };
    }
  }

  /**
   * Creates a {@link ComponentSpec} by parsing a filename string.
   * The filename is expected to be in the format: "widthSpec_heightSpec_type.extension",
   * e.g., "1R_2U_ladder.png".
   *
   * @param filename The filename string to parse.
   * @return A {@link ComponentSpec} instance derived from the filename.
   * @throws IllegalArgumentException if the filename format is invalid.
   */
  public static ComponentSpec fromFilename(String filename) {
    String[] parts = filename.substring(0, filename.lastIndexOf('.')).split("_");

    if (parts.length < 3) {
      throw new IllegalArgumentException("Invalid filename format: " + filename);
    }

    // Parse width specification (like 1R)
    String widthSpec = parts[0];
    int widthTiles = Integer.parseInt(widthSpec.substring(0, widthSpec.length() - 1));
    Direction widthDirection = Direction.fromCode(widthSpec.charAt(widthSpec.length() - 1));

    // Parse height specification (like 2U)
    String heightSpec = parts[1];
    int heightTiles = Integer.parseInt(heightSpec.substring(0, heightSpec.length() - 1));
    Direction heightDirection = Direction.fromCode(heightSpec.charAt(heightSpec.length() - 1));

    // Parse component type
    ComponentType type = ComponentType.fromString(parts[2]);

    return new ComponentSpec(widthTiles, widthDirection, heightTiles, heightDirection, type);
  }

  /**
   * Calculates the width of the component in pixels.
   *
   * @param tileWidth The width of a single tile in pixels.
   * @return The total width of the component in pixels.
   */
  public double calculateWidth(double tileWidth) {
    return widthTiles * tileWidth;
  }

  /**
   * Calculates the height of the component in pixels.
   *
   * @param tileHeight The height of a single tile in pixels.
   * @return The total height of the component in pixels.
   */
  public double calculateHeight(double tileHeight) {
    return heightTiles * tileHeight;
  }

  /**
   * Calculates the X-axis translation needed for the component based on its width and direction.
   * This is typically used for components that extend to the left.
   *
   * @param tileWidth The width of a single tile in pixels.
   * @return The X-axis translation in pixels.
   */
  public double calculateTranslateX(double tileWidth) {
    return widthDirection == Direction.LEFT ? -widthTiles * tileWidth : 0;
  }

  /**
   * Calculates the Y-axis translation needed for the component based on its height and direction.
   *
   * @param tileHeight The height of a single tile in pixels.
   * @return The Y-axis translation in pixels.
   */
  public double calculateTranslateY(double tileHeight) {
    return heightDirection == Direction.UP ? -heightTiles * tileHeight : 0;
  }
} 