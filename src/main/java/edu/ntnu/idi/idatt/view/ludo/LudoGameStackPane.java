package edu.ntnu.idi.idatt.view.ludo;

import java.util.ArrayList;
import java.util.List;

import edu.ntnu.idi.idatt.factory.view.PlayerTokenFactory;
import edu.ntnu.idi.idatt.model.board.LudoGameBoard;
import edu.ntnu.idi.idatt.model.player.Player;
import edu.ntnu.idi.idatt.model.tile.Tile;
import edu.ntnu.idi.idatt.view.common.GameStackPane;
import edu.ntnu.idi.idatt.view.util.ViewUtils;
import javafx.animation.PathTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.scene.paint.Color;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Shape;

public class LudoGameStackPane extends GameStackPane {

    public LudoGameStackPane(LudoGameBoard board, List<Player> players) {
        super(board, players);
        initialize(players, new LudoGameBoardStackPane());
    }

    @Override
    protected void initializePlayersPane() {
      playersPane.layoutBoundsProperty().addListener((obs, oldVal, newVal) -> {
        if (newVal.getWidth() > 0 && newVal.getHeight() > 0) {
          boardDimensions = new double[]{newVal.getWidth(), newVal.getHeight()};
          this.tileSizeX = boardDimensions[0] / ((LudoGameBoard) board).getBoardSize();
          this.tileSizeY = boardDimensions[1] / ((LudoGameBoard) board).getBoardSize();

          this.tilePositionX = new double[]{(tileSizeX / 2) , (tileSizeX / 2), (tileSizeX / 2), (tileSizeX / 2)};
          this.tilePositionY = new double[]{(tileSizeY / 2), (tileSizeY / 2), (tileSizeY / 2), (tileSizeY / 2)};

          if (playersPane.getChildren().isEmpty()) {
            addGamePieces(players);
          }
        }
      });
    }

    @Override
    public void addGamePieces(List<Player> players) {
      players.forEach(player -> {
        Tile playerTile = player.getCurrentTile();

        double posX = tilePositionX[players.indexOf(player)];
        double posY = tilePositionY[players.indexOf(player)];

        Shape playerToken = PlayerTokenFactory.create(8, Color.web(player.getColorHex()),
            player.getPlayerTokenType());
        playerToken.getStyleClass().add("game-board-player-token");
        // TODO: Make this work for ludo, placing the player tokens in the player's starting area
        playerToken.setTranslateX(posX + playerTile.getCoordinates()[0]);
        playerToken.setTranslateY(playerTile.getCoordinates()[1] - posY);
        playersPane.getChildren().add(playerToken);

        playerTokenMap.put(player, playerToken);
        playerTileMap.put(player, playerTile);
      });
    }

    @Override
    public void movePlayer(Player player, Tile newTile, boolean straightLine) {
      if (playerTileMap.get(player).getTileId() == newTile.getTileId()) {
        return;
      }
      double posX = tilePositionX[players.indexOf(player)];
      double posY = tilePositionY[players.indexOf(player)];

      double[] currentPaneCoordinates = convertCoordinates(playerTileMap.get(player).getCoordinates());
      double[] newPaneCoordinates = convertCoordinates(newTile.getCoordinates());

      double currentXPos = posX + currentPaneCoordinates[0];
      double currentYPos = currentPaneCoordinates[1] + posY;
      double newXPos = posX + newPaneCoordinates[0];
      double newYPos = newPaneCoordinates[1] + posY;

      SequentialTransition transition = new SequentialTransition();

      Path path = new Path();
      path.getElements().add(new MoveTo(currentXPos, currentYPos));

      if (straightLine) {
        path.getElements().add(new LineTo(newXPos, newYPos));
        PauseTransition pauseTransition = new PauseTransition(TRANSITION_DURATION);
        transition.getChildren().add(pauseTransition);
      } else {
        getPathTiles(playerTileMap.get(player), newTile).forEach(tile -> {
          double[] tilePaneCoordinates = convertCoordinates(tile.getCoordinates());
          path.getElements().add(new LineTo(posX + tilePaneCoordinates[0], tilePaneCoordinates[1] + posY));
        });
      }
      Shape playerToken = playerTokenMap.get(player);
      PathTransition pathTransition = new PathTransition();
      pathTransition.setDuration(TRANSITION_DURATION);
      pathTransition.setNode(playerToken);
      pathTransition.setPath(path);

      transition.getChildren().add(pathTransition);
      transition.play();
      // Update the player tile map to reflect the new tile.
      playerTileMap.put(player, newTile);
    }

    @Override
    protected double[] convertCoordinates(int[] rc) {
      return ViewUtils.ludoBoardToScreenCoordinates(rc, (LudoGameBoard) board, boardDimensions[0], boardDimensions[1]);
    }

    @Override
    protected List<Tile> getPathTiles(Tile startTile, Tile endTile) {
      int fromId = startTile.getTileId();
      int toId = endTile.getTileId();

      List<Tile> pathTiles = new ArrayList<>();
      if (fromId < toId) {
        for (int id = fromId + 1; id <= toId; id++) {
          pathTiles.add(board.getTile(id));
        }
      } else {
        for (int id = fromId - 1; id >= toId; id--) {
          pathTiles.add(board.getTile(id));
        }
      }
      return pathTiles;
    }
    
    
    
}
