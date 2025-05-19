package edu.ntnu.idi.idatt.model.player;

/**
 * <h3>PlayerTokenType.</h3>
 *
 * <p>Enumerates the different visual shapes that a player's game token can take.
 * This is used to customize the appearance of tokens in games that support different
 * token styles (e.g., LadderGame)</p>
 */
public enum PlayerTokenType {
  /** Hexagonal token shape. */
  HEXAGON,
  /** Circular token shape. */
  CIRCLE,
  /** Square token shape. */
  SQUARE,
  /** Triangular token shape. */
  TRIANGLE,
  /** Diamond token shape. */
  DIAMOND;
}
