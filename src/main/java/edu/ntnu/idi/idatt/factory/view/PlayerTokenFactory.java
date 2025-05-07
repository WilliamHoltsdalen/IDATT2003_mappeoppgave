package edu.ntnu.idi.idatt.factory.view;

import edu.ntnu.idi.idatt.model.player.PlayerTokenType;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;

/**
 * <h3>PlayerTokenCreator class</h3>
 *
 * <p>This class is responsible for creating player tokens of different types. It takes in a radius,
 * color, and player token type, and returns a {@link Shape} object representing the player token.
 */
public class PlayerTokenFactory {

  /** Private constructor to prevent instantiation. */
  private PlayerTokenFactory() {}

  /**
   * Constructor for PlayerTokenCreator class.
   *
   * @param radius the radius of the player token
   * @param color the color of the player token
   * @param playerTokenType the type of player token to create
   */
  public static Shape create(double radius, Color color, PlayerTokenType playerTokenType) {
    Shape token = switch (playerTokenType) {
      case HEXAGON -> createHexagon(radius);
      case SQUARE -> createSquare(radius);
      case TRIANGLE -> createTriangle(radius);
      case DIAMOND -> createDiamond(radius);
      default -> createCircle(radius); // Both for case CIRCLE and default
    };

    token.setFill(color);
    token.setStroke(darken(color));
    token.setStrokeWidth(radius * 0.4);
    token.setSmooth(true);
    return token;
  }

  private static Color darken(Color color) {
    return new Color(
        Math.max(0, color.getRed() * (1 - 0.35)),
        Math.max(0, color.getGreen() * (1 - 0.35)),
        Math.max(0, color.getBlue() * (1 - 0.35)),
        color.getOpacity()
    );
  }

  private static Shape createHexagon(double radius) {
    return new Polygon(
        radius * Math.cos(Math.toRadians(0)), radius * Math.sin(Math.toRadians(0)),
        radius * Math.cos(Math.toRadians(60)), radius * Math.sin(Math.toRadians(60)),
        radius * Math.cos(Math.toRadians(120)), radius * Math.sin(Math.toRadians(120)),
        radius * Math.cos(Math.toRadians(180)), radius * Math.sin(Math.toRadians(180)),
        radius * Math.cos(Math.toRadians(240)), radius * Math.sin(Math.toRadians(240)),
        radius * Math.cos(Math.toRadians(300)), radius * Math.sin(Math.toRadians(300))
    );
  }

  private static Shape createCircle(double radius) {
    return new Circle(radius);
  }

  /**
   * Creates a square token using a Polygon instead of a Rectangle.
   * This approach allows for more consistent rendering and styling
   * in JavaFX, especially when applying transformations or effects.
   *
   * @param radius the half-length of the square's side
   * @return a Shape representing the square token
   */
  private static Shape createSquare(double radius) {
    return new Polygon(
        -radius, -radius,
        radius, -radius,
        radius, radius,
        -radius, radius
    );
  }

  private static Shape createTriangle(double radius) {
    return new Polygon(
        0, -radius,
        radius, radius,
        -radius, radius
    );
  }

  private static Shape createDiamond(double radius) {
    return new Polygon(
        0, -radius,
        radius, 0,
        0, radius,
        -radius, 0
    );
  }
}
