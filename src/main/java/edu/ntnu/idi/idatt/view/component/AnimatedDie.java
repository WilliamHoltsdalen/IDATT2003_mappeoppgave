package edu.ntnu.idi.idatt.view.component;

import java.util.Random;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.InnerShadow;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.util.Duration;

/**
 * <h3>AnimatedDie.</h3>
 *
 * <p>A JavaFX component that represents a single die with rolling animation.</p>
 *
 * <p>It displays a standard six-sided die face and provides methods to roll the die
 * to a specific value with an animation or set its value directly.</p>
 * 
 * <p>Extends the {@link Group} class to allow for easy addition of the die to a JavaFX scene.</p>
 *
 * @see Group
 */
public class AnimatedDie extends Group {

  private static final double DOT_RADIUS = 0.1; // Relative to dice size
  private static final int ROLL_ANIMATION_DURATION = 800;
  private static final int ROLL_ANIMATION_STEPS = 10;
  private final Canvas diceFace;
  private final GraphicsContext gc;
  private Timeline rollAnimation;
  private int currentValue = 1;
  private final Random random = new Random();

  /**
   * Constructs an AnimatedDie with a specified size.
   *
   * @param size The width and height of the die.
   */
  public AnimatedDie(double size) {
    // Create the dice face
    diceFace = new Canvas(size, size);
    gc = diceFace.getGraphicsContext2D();

    // Draw initial face
    drawDots(1);

    getChildren().add(diceFace);
  }

  /**
   * Draws the dots on the die face corresponding to the given value.
   *
   * @param value The number of dots to draw (1-6).
   */
  private void drawDots(int value) {
    final double size = diceFace.getWidth();
    final double dotSize = size * DOT_RADIUS;
    final double arc = size * 0.18; // for rounded corners

    // Clear the canvas (make background transparent)
    gc.clearRect(0, 0, size, size);

    // Draw drop shadow for the dice face
    DropShadow faceShadow = new DropShadow();
    faceShadow.setRadius(8);
    faceShadow.setOffsetY(3);
    faceShadow.setColor(Color.rgb(0, 0, 0, 0.18));
    gc.applyEffect(faceShadow);

    // Draw subtle gradient for the dice face
    LinearGradient gradient = new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
        new Stop(0, Color.web("#ffffff")),
        new Stop(1, Color.web("#e0e0e0")));
    gc.setFill(gradient);
    gc.fillRoundRect(2, 2, size - 4, size - 4, arc, arc);

    // Draw border with rounded corners (with inset)
    gc.setEffect(null);
    gc.setStroke(Color.BLACK);
    gc.setLineWidth(2);
    gc.strokeRoundRect(2, 2, size - 4, size - 4, arc, arc);

    // Draw dots with inner shadow
    InnerShadow dotShadow = new InnerShadow();
    dotShadow.setRadius(dotSize * 0.7);
    dotShadow.setChoke(0.7);
    dotShadow.setColor(Color.rgb(0, 0, 0, 0.55));
    gc.setEffect(dotShadow);
    gc.setFill(Color.BLACK);

    switch (value) {
      case 1:
        drawDot(size / 2, size / 2, dotSize);
        break;
      case 2:
        drawDot(size / 4, size / 4, dotSize);
        drawDot(3 * size / 4, 3 * size / 4, dotSize);
        break;
      case 3:
        drawDot(size / 4, size / 4, dotSize);
        drawDot(size / 2, size / 2, dotSize);
        drawDot(3 * size / 4, 3 * size / 4, dotSize);
        break;
      case 4:
        drawDot(size / 4, size / 4, dotSize);
        drawDot(3 * size / 4, size / 4, dotSize);
        drawDot(size / 4, 3 * size / 4, dotSize);
        drawDot(3 * size / 4, 3 * size / 4, dotSize);
        break;
      case 5:
        drawDot(size / 4, size / 4, dotSize);
        drawDot(3 * size / 4, size / 4, dotSize);
        drawDot(size / 2, size / 2, dotSize);
        drawDot(size / 4, 3 * size / 4, dotSize);
        drawDot(3 * size / 4, 3 * size / 4, dotSize);
        break;
      case 6:
        drawDot(size / 4, size / 4, dotSize);
        drawDot(3 * size / 4, size / 4, dotSize);
        drawDot(size / 4, size / 2, dotSize);
        drawDot(3 * size / 4, size / 2, dotSize);
        drawDot(size / 4, 3 * size / 4, dotSize);
        drawDot(3 * size / 4, 3 * size / 4, dotSize);
        break;
      default:
        break;
    }
    gc.setEffect(null); // Reset effect
  }

  /**
   * Draws a single dot at the specified coordinates with the given radius.
   * Includes an inner shadow and a highlight for a 3D effect.
   *
   * @param x The x-coordinate of the center of the dot.
   * @param y The y-coordinate of the center of the dot.
   * @param radius The radius of the dot.
   */
  private void drawDot(double x, double y, double radius) {
    // Draw main dot with inner shadow
    gc.fillOval(x - radius, y - radius, radius * 2, radius * 2);
    // Draw white highlight (upper left)
    gc.setEffect(null); // Remove inner shadow for highlight
    gc.setFill(Color.rgb(255, 255, 255, 0.7));
    double highlightRadius = radius * 0.38;
    gc.fillOval(x - radius * 0.55, y - radius * 0.55, highlightRadius, highlightRadius);
    // Restore effect for next dot
    InnerShadow dotShadow = new InnerShadow();
    dotShadow.setRadius(radius * 0.7);
    dotShadow.setChoke(0.7);
    dotShadow.setColor(Color.rgb(0, 0, 0, 0.55));
    gc.setEffect(dotShadow);
    gc.setFill(Color.BLACK);
  }

  /**
   * Initiates a roll animation for the die.
   * The die will visually cycle through random values before settling on the specified final value.
   *
   * @param value The final value the die should show after rolling (1-6).
   * @param onFinished A {@link Runnable} to be executed when the roll animation completes.
   */
  public void roll(int value, Runnable onFinished) {
    currentValue = value;

    // Create a timeline that cycles through values
    rollAnimation = new Timeline();

    // Add keyframes to cycle through values
    for (int i = 0; i < ROLL_ANIMATION_STEPS; i++) {
      final int frameValue = random.nextInt(6) + 1;
      rollAnimation.getKeyFrames().add(
          new KeyFrame(Duration.millis(i * ROLL_ANIMATION_DURATION / ROLL_ANIMATION_STEPS),
              event -> drawDots(frameValue))
      );
    }

    // Add final keyframe to show the actual value
    rollAnimation.getKeyFrames().add(
        new KeyFrame(Duration.millis(ROLL_ANIMATION_DURATION),
            event -> drawDots(value))
    );

    rollAnimation.play();

    // Run the onFinished runnable when the animation is finished
    rollAnimation.setOnFinished(event -> {
      onFinished.run();
    });
  }

  /**
   * Gets the current value displayed on the die.
   *
   * @return The current value of the die (1-6).
   */
  public int getCurrentValue() {
    return currentValue;
  }

  /**
   * Sets the value of the die directly without playing an animation.
   *
   * @param value The value to set the die to (1-6).
   * @throws IllegalArgumentException if the value is not between 1 and 6.
   */
  public void setValue(int value) {
    if (value < 1 || value > 6) {
      throw new IllegalArgumentException("Value must be between 1 and 6");
    }
    drawDots(value);
  }
} 