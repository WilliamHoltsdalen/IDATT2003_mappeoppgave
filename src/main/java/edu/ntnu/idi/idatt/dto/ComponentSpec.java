package edu.ntnu.idi.idatt.dto;

public record ComponentSpec(
    int widthTiles,
    Direction widthDirection,
    int heightTiles,
    Direction heightDirection,
    ComponentType type
) {
  public enum Direction {
    UP('U'),
    DOWN('D'),
    LEFT('L'),
    RIGHT('R');

    Direction(char code) {
    }

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

  public enum ComponentType {
    LADDER,
    SLIDE,
    PORTAL;

    public static ComponentType fromString(String type) {
      return switch (type.toLowerCase()) {
        case "ladder" -> LADDER;
        case "slide" -> SLIDE;
        case "portal" -> PORTAL;
        default -> throw new IllegalArgumentException("Invalid component type: " + type);
      };
    }
  }

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

  public double calculateWidth(double tileWidth) {
    return widthTiles * tileWidth;
  }

  public double calculateHeight(double tileHeight) {
    return heightTiles * tileHeight;
  }

  public double calculateTranslateX(double tileWidth) {
    return widthDirection == Direction.LEFT ? -widthTiles * tileWidth : 0;
  }

  public double calculateTranslateY(double tileHeight) {
    return heightDirection == Direction.UP ? -heightTiles * tileHeight : 0;
  }
} 