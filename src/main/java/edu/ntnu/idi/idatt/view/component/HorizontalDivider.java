package edu.ntnu.idi.idatt.view.component;

import javafx.scene.layout.Region;

/**
 * HorizontalDivider.
 *
 * <p>A simple JavaFX {@link Region} styled to act as a horizontal visual divider.</p>
 *
 * <p>It typically takes its appearance from CSS rules associated with the "horizontal-divider"
 * style class.</p>
 *
 * @see Region
 */
public class HorizontalDivider extends Region {

  /**
   * Constructs a HorizontalDivider and applies the "horizontal-divider" style class.
   */
  public HorizontalDivider() {
    this.getStyleClass().add("horizontal-divider");
  }
}
