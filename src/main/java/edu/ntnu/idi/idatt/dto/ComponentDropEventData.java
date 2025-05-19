package edu.ntnu.idi.idatt.dto;

import javafx.scene.shape.Rectangle;

/**
 * ComponentDropEventData.
 *
 * <p>A record to encapsulate data related to a component drop event, typically in a board creator
 * view.</p>
 *
 * <p>It holds the identifier of the component being dropped and the target cell
 * ({@link Rectangle})
 * on the grid where it was dropped.</p>
 *
 * @param componentIdentifier The string identifier of the component.
 * @param cell                The {@link Rectangle} representing the grid cell where the component
 *                            was dropped.
 */
public record ComponentDropEventData(String componentIdentifier, Rectangle cell) {

}