package edu.ntnu.idi.idatt.view.ludo;

import edu.ntnu.idi.idatt.model.board.LudoGameBoard;
import edu.ntnu.idi.idatt.model.tile.LudoTile;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class LudoGameView extends BorderPane {
    private LudoGameBoard board;
    
    public LudoGameView() {
        super();
        initialize();
    }

    public void initialize() {
        this.board = new LudoGameBoard("Classic ludo", "Classic 9x9 ludo board", 15, new Color[] {Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW});
    
        this.setCenter(createTestBoardPane());
    }

    private Pane createTestBoardPane() {
        Pane boardPane = new Pane();
        boardPane.setPrefSize(600, 600);
        boardPane.setStyle("-fx-background-color: white;");

        int tileSize = 600 / board.getBoardSize();

        VBox boardContainer = new VBox();
        for (int row = 0; row < board.getBoardSize(); row++) {
            HBox rowBox = new HBox();
            for (int col = 0; col < board.getBoardSize(); col++) {
                final int currentRow = row;
                final int currentCol = col;
                // Find the tile with matching coordinates
                LudoTile tile = (LudoTile) board.getTiles().stream()
                    .filter(t -> t.getCoordinates()[0] == currentRow && t.getCoordinates()[1] == currentCol)
                    .findFirst()
                    .orElse(null);
                StackPane tileStackPane = new StackPane();
                Rectangle tileRect = new Rectangle(tileSize, tileSize);
                
                if (tile == null) {
                    tileRect.setFill(Color.WHITE);
                } else if (tile.getType().startsWith("start")) {
                    tileRect.setFill(Color.GREEN);
                } else if (tile.getType().startsWith("finish")) {
                    tileRect.setFill(Color.YELLOW);
                } else if (tile.getType().equals("normal")) {
                    tileRect.setFill(Color.LIGHTGREEN);
                } else {
                    tileRect.setFill(Color.GRAY);
                }
                tileRect.setStroke(Color.BLACK);
                tileRect.setStrokeWidth(1);
                tileStackPane.getChildren().add(tileRect);

                Label tileLabel = new Label(tile == null ? "null" : String.valueOf(tile.getTileId()));
                tileStackPane.getChildren().add(tileLabel);
                rowBox.getChildren().add(tileStackPane);
            }
            boardContainer.getChildren().add(rowBox);
        }
        boardPane.getChildren().add(boardContainer);
        return boardPane;
    }
}
