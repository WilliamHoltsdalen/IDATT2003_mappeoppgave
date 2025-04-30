package edu.ntnu.idi.idatt.dto;

import javafx.scene.Node;

public record ComponentDropEventData(String componentType, String imagePath, Node cell) {

}