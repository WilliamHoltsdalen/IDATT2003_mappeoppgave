package edu.ntnu.idi.idatt.view.ludo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.ntnu.idi.idatt.factory.view.PlayerTokenFactory;
import edu.ntnu.idi.idatt.model.board.LudoGameBoard;
import edu.ntnu.idi.idatt.model.player.LudoPlayer;
import edu.ntnu.idi.idatt.model.player.Player;
import edu.ntnu.idi.idatt.model.tile.Tile;
import edu.ntnu.idi.idatt.model.token.LudoToken;
import edu.ntnu.idi.idatt.view.common.GameStackPane;
import edu.ntnu.idi.idatt.view.util.ViewUtils;
import javafx.animation.PathTransition;
import javafx.scene.paint.Color;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Shape;

public class LudoGameStackPane extends GameStackPane {
  protected final Map<LudoToken, Shape> tokenShapeMap;
  protected double tileOffset;

    public LudoGameStackPane(LudoGameBoard board, List<Player> players) {
        super(board, players);
        this.tokenShapeMap = new HashMap<>();
        initialize(new LudoGameBoardStackPane());
    }

    @Override
    protected void initializePlayersPane() {
      playersPane.layoutBoundsProperty().addListener((obs, oldVal, newVal) -> {
        if (newVal.getWidth() > 0 && newVal.getHeight() > 0) {
          boardDimensions = new double[]{newVal.getWidth(), newVal.getWidth()};
          this.tileSizeX = boardDimensions[0] / ((LudoGameBoard) board).getBoardSize();
          this.tileSizeY = boardDimensions[1] / ((LudoGameBoard) board).getBoardSize();

          this.tileOffset = (tileSizeX / 2);

          if (playersPane.getChildren().isEmpty()) {
            addGamePieces(players);
          }
        }
      });
    }

    @Override
    public void addGamePieces(List<Player> players) {
      double[] startAreaDimensions = convertCoordinates(new int[]{((LudoGameBoard) board).getStartAreaSize(), ((LudoGameBoard) board).getStartAreaSize()});
      double[][] playerStartPositions = new double[][]{
        {startAreaDimensions[0] / 3 * 1, startAreaDimensions[1] / 3 * 1},
        {startAreaDimensions[0] / 3 * 2, startAreaDimensions[1] / 3 * 1},
        {startAreaDimensions[0] / 3 * 1, startAreaDimensions[1] / 3 * 2},
        {startAreaDimensions[0] / 3 * 2, startAreaDimensions[1] / 3 * 2}
      };
      players.forEach(player -> {
        Tile firstStartAreaTile = ((LudoGameBoard) board).getTile(((LudoGameBoard) board).getPlayerStartIndexes()[players.indexOf(player)]);
        double[] startAreaFirstTilePos = convertCoordinates(firstStartAreaTile.getCoordinates());
        
        ((LudoPlayer) player).getTokens().forEach(token -> {
          double startAreaPosX = playerStartPositions[token.getTokenId() - 1][0];
          double startAreaPosY = playerStartPositions[token.getTokenId() - 1][1];

          Shape playerToken = PlayerTokenFactory.create(8, Color.web(player.getColorHex()),
              player.getPlayerTokenType());
          playerToken.getStyleClass().add("game-board-player-token");
          playerToken.setTranslateX(startAreaFirstTilePos[0] + startAreaPosX);
          playerToken.setTranslateY(startAreaFirstTilePos[1] + startAreaPosY);
          playersPane.getChildren().add(playerToken);

          tokenShapeMap.put(token, playerToken);
        });
      });
    }

    public void releaseToken(LudoPlayer player, int tokenId) {
      LudoToken token = player.getTokens().get(tokenId);

      Tile startTile = token.getCurrentTile();

      double[] startTilePos = convertCoordinates(startTile.getCoordinates());
      double[] currentTokenPos = new double[]{tokenShapeMap.get(token).getTranslateX(), tokenShapeMap.get(token).getTranslateY()};
      double[] newTokenPos = new double[]{startTilePos[0] + tileOffset, startTilePos[1] + tileOffset};

      Path path = new Path();
      path.getElements().add(new MoveTo(currentTokenPos[0], currentTokenPos[1]));
      path.getElements().add(new LineTo(newTokenPos[0], newTokenPos[1]));

      PathTransition pathTransition = new PathTransition();
      pathTransition.setDuration(TRANSITION_DURATION);
      pathTransition.setNode(tokenShapeMap.get(token));
      pathTransition.setPath(path);
      pathTransition.play();
    }

    public void moveToken(LudoToken token, Tile oldTile, Tile newTile, boolean straightLine) {
      if (oldTile.getTileId() == newTile.getTileId()) {
        return;
      }

      double[] newPaneCoordinates = convertCoordinates(newTile.getCoordinates());

      double currentXPos = tokenShapeMap.get(token).getTranslateX();
      double currentYPos = tokenShapeMap.get(token).getTranslateY();
      double newXPos = tileOffset + newPaneCoordinates[0];
      double newYPos = tileOffset + newPaneCoordinates[1];

      Path path = new Path();
      path.getElements().add(new MoveTo(currentXPos, currentYPos));

      Player player = players.stream().filter(p -> ((LudoPlayer) p).getTokens().contains(token)).findFirst().orElse(null);
      int playerIndex = players.indexOf(player);

      if (straightLine) {
        path.getElements().add(new LineTo(newXPos, newYPos));
      } else {
        getPathTiles(playerIndex, oldTile, newTile).forEach(tile -> {
          double[] tilePaneCoordinates = convertCoordinates(tile.getCoordinates());
          path.getElements().add(new LineTo(tilePaneCoordinates[0] + tileOffset, tilePaneCoordinates[1] + tileOffset));
        });
      }
      Shape playerToken = tokenShapeMap.get(token);
      PathTransition pathTransition = new PathTransition();
      pathTransition.setDuration(TRANSITION_DURATION);
      pathTransition.setNode(playerToken);
      pathTransition.setPath(path);
      pathTransition.play();
    }

    @Override
    protected double[] convertCoordinates(int[] rc) {
      return ViewUtils.ludoBoardToScreenCoordinates(rc, (LudoGameBoard) board, boardDimensions[0], boardDimensions[1]);
    }

    protected List<Tile> getPathTiles(int playerIndex, Tile startTile, Tile endTile) {
      List<Tile> pathTiles = new ArrayList<>();

      int fromId = startTile.getTileId();
      int toId = endTile.getTileId();

      int totalTrackTileCount = ((LudoGameBoard) board).getTotalTrackTileCount();
      int firstTrackTileId = ((LudoGameBoard) board).getPlayerTrackStartIndexes()[0];

      if (fromId < toId) {
        for (int id = fromId + 1; id <= toId; id++) { // For players to move smoothly onto the finish track. 
          if (id == ((LudoGameBoard) board).getPlayerTrackStartIndexes()[playerIndex]) {
            id = ((LudoGameBoard) board).getPlayerFinishStartIndexes()[playerIndex];
          }
          if (id == ((LudoGameBoard) board).getPlayerTrackStartIndexes()[playerIndex] - 1) {
            continue;
          }
          if (playerIndex == 0 && id == totalTrackTileCount) { // For the first player to go onto the finish track smoothly.
            continue;
          }
          if (id > ((LudoGameBoard) board).getPlayerFinishStartIndexes()[playerIndex] + ((LudoGameBoard) board).getStartAreaSize() - 2) {
            id = toId;
          }
          pathTiles.add(board.getTile(id));
        }
      } else {
        if (fromId <= totalTrackTileCount && toId >= firstTrackTileId) { // For everyone but the first player to pass the last track tile / first track tile gap smoothly. 
          for (int id = fromId; id <= totalTrackTileCount; id++) {
            pathTiles.add(board.getTile(id));
          }
          for (int id = firstTrackTileId; id <= toId; id++) {
            pathTiles.add(board.getTile(id));
          }
        } else {
          pathTiles.add(board.getTile(toId));
        }
      }
      return pathTiles;
    }
}
