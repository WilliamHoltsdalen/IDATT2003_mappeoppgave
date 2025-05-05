package edu.ntnu.idi.idatt.view.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.ntnu.idi.idatt.model.board.Board;
import edu.ntnu.idi.idatt.model.player.Player;
import edu.ntnu.idi.idatt.model.tile.Tile;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Shape;
import javafx.util.Duration;

public abstract class GameStackPane extends StackPane {
    protected static final Duration TRANSITION_DURATION = Duration.seconds(1);
    protected final Board board;
    protected final Map<Player, Tile> playerTileMap;
    protected final Map<Player, Shape> playerTokenMap;
    protected double[] boardDimensions;
    protected final List<Player> players;
    protected final Pane playersPane;
    
    protected double tileSizeX;
    protected double tileSizeY;
    protected double[] tilePositionX;
    protected double[] tilePositionY;
    
    public GameStackPane(Board board, List<Player> players) {
        this.board = board;
        this.playerTileMap = new HashMap<>();
        this.playerTokenMap = new HashMap<>();
        this.boardDimensions = new double[2];
        this.players = players;
    
        this.playersPane = new Pane();
    
        this.getStyleClass().add("game-board");
    }

    protected void initialize(List<Player> players, BoardStackPane boardStackPane) {
        boardStackPane.initialize(board, board.getBackground());
        boardStackPane.getBackgroundImageView().setFitWidth(500);
        boardStackPane.getStyleClass().add("game-board-image-view");

        StackPane stackPane = new StackPane();
        stackPane.getChildren().setAll(boardStackPane, playersPane);
        stackPane.getStyleClass().add("game-board-stack-pane");
        stackPane.maxHeightProperty().bind(stackPane.heightProperty());
        this.getChildren().add(stackPane);

        initializePlayersPane();
    }

    protected abstract void initializePlayersPane();

    public abstract void addGamePieces(List<Player> players);

    public abstract void movePlayer(Player player, Tile newTile, boolean straightLine);

    protected abstract double[] convertCoordinates(int[] rc);

    protected abstract List<Tile> getPathTiles(Tile startTile, Tile endTile);

}
