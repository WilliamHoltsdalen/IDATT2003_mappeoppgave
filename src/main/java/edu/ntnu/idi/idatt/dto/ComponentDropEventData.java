package edu.ntnu.idi.idatt.dto;

import javafx.scene.shape.Rectangle;

public record ComponentDropEventData(String componentIdentifier, Rectangle cell) {
}