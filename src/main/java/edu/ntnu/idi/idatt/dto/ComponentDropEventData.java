package edu.ntnu.idi.idatt.dto;

import javafx.scene.shape.Rectangle;

public record ComponentDropEventData(String componentType, String imagePath, Rectangle cell) {

}