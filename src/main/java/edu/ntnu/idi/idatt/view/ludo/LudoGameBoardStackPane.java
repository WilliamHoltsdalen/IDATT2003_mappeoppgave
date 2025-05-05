package edu.ntnu.idi.idatt.view.ludo;

import edu.ntnu.idi.idatt.dto.TileCoordinates;
import edu.ntnu.idi.idatt.model.board.Board;
import edu.ntnu.idi.idatt.model.board.LudoGameBoard;
import edu.ntnu.idi.idatt.model.tile.LudoTile;
import edu.ntnu.idi.idatt.view.common.BoardStackPane;
import javafx.application.Platform;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class LudoGameBoardStackPane extends BoardStackPane {
 
  public LudoGameBoardStackPane() {
    super();
  }

  @Override
  public void initialize(Board board, String backgroundImagePath) {
    logger.debug("Initializing LudoGameBoardStackPane");
    super.initialize((LudoGameBoard) board, backgroundImagePath);

    Platform.runLater(() -> {
        updateGrid();
        loadComponents();
    });
  }

  @Override
  public void updateGrid() {
    logger.debug("Updating grid");
    gridContainer.getChildren().clear();
    cellToCoordinatesMap.clear();

    if (onRemoveComponentsOutsideGrid != null) {
      onRemoveComponentsOutsideGrid.run();
    }

    int boardSize = ((LudoGameBoard) board).getBoardSize();

    double cellWidth = backgroundImageView.getFitWidth() / boardSize;
    double cellHeight = cellWidth;
    
    for (int row = 0; row < boardSize; row++) {
      HBox rowBox = new HBox();
      for (int col = 0; col < boardSize; col++) {
        rowBox.getChildren().add(
            createRowCell(cellWidth, cellHeight, row, col)
        );
      }
      gridContainer.getChildren().add(rowBox);
    }

    applyPattern();
    updateBoardVisuals();
  }

  @Override
  public StackPane createRowCell(double cellWidth, double cellHeight, final int row, final int col) {
    StackPane cellPane = new StackPane();
    Rectangle cellRect = new Rectangle(cellWidth, cellHeight);
    cellToCoordinatesMap.put(cellRect, new TileCoordinates(row, col));

    // Find the tile with matching coordinates
    LudoTile tile = (LudoTile) board.getTiles().stream()
        .filter(t -> t.getCoordinates()[0] == row && t.getCoordinates()[1] == col)
        .findFirst()
        .orElse(null);

    String type = tile.getType();
    Color[] colors = ((LudoGameBoard)board).getColors();

    if (type.contains("1")) {
      cellRect.setFill(colors[0]);
    } else if (type.contains("2")) {
      cellRect.setFill(colors[1]);
    } else if (type.contains("3")) {
      cellRect.setFill(colors[2]);
    } else if (type.contains("4")) {
      cellRect.setFill(colors[3]);
    } else if (type.equals("track")) {
      cellRect.setFill(Color.WHITE);
    } else {
      cellRect.setFill(Color.GRAY);
    }

    cellRect.getStyleClass().add("ludo-board-cell");
    cellPane.getChildren().add(cellRect);
    return cellPane;
  }

  @Override
  public void applyPattern() {
  }

  @Override
  public void addComponent(String componentIdentifier, TileCoordinates coordinates) {
  }

  @Override
  public void updateBoardVisuals() {
  }

  @Override
  public void loadComponents() {
  }

  @Override
  public String getImagePath(String componentIdentifier) {
    return null;
  }
}