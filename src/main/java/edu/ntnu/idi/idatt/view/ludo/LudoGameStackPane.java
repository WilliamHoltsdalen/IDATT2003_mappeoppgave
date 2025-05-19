package edu.ntnu.idi.idatt.view.ludo;

import edu.ntnu.idi.idatt.factory.view.PlayerTokenFactory;
import edu.ntnu.idi.idatt.model.board.LudoGameBoard;
import edu.ntnu.idi.idatt.model.player.LudoPlayer;
import edu.ntnu.idi.idatt.model.player.Player;
import edu.ntnu.idi.idatt.model.tile.Tile;
import edu.ntnu.idi.idatt.model.token.LudoToken;
import edu.ntnu.idi.idatt.view.common.GameStackPane;
import edu.ntnu.idi.idatt.view.util.ViewUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.animation.PathTransition;
import javafx.scene.paint.Color;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Shape;

/**
 * LudoGameStackPane.
 *
 * <p>Extends {@link GameStackPane} to manage the visual representation of a Ludo game board,
 * including the tokens and their movements. It handles placing tokens in their start areas,
 * releasing tokens onto the board, moving tokens between tiles, and returning tokens to the
 * start area when captured.</p>
 *
 * <p>Animations for token movements are implemented using {@link PathTransition}. The class
 * maintains a map of {@link LudoToken} objects to their corresponding JavaFX {@link Shape}
 * representations. It relies on a {@link LudoGameBoardStackPane} for the underlying board
 * graphics.</p>
 *
 * @see GameStackPane
 * @see LudoGameBoard
 * @see LudoPlayer
 * @see LudoToken
 * @see PlayerTokenFactory
 * @see LudoGameBoardStackPane
 */
public class LudoGameStackPane extends GameStackPane {

  protected final Map<LudoToken, Shape> tokenShapeMap;
  protected double tileOffset;

  /**
   * Constructs a {@code LudoGameStackPane} for the given Ludo game board and list of players.
   * Initializes the token-to-shape map and sets up the underlying board view using a
   * {@link LudoGameBoardStackPane}.
   *
   * @param board   The {@link LudoGameBoard} to be displayed.
   * @param players The list of {@link Player}s (expected to be {@link LudoPlayer}s) in the game.
   */
  public LudoGameStackPane(LudoGameBoard board, List<Player> players) {
    super(board, players);
    this.tokenShapeMap = new HashMap<>();
    initialize(new LudoGameBoardStackPane());
  }

  /**
   * Initializes the pane where player tokens will be displayed and moved.
   * It sets up listeners to determine board dimensions and tile sizes once the pane is laid out.
   * If game pieces haven't been added yet, it calls {@link #addGamePieces(List)}.
   */
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

  /**
   * Adds the visual representations (game pieces) for each player's tokens to the board.
   * For each {@link LudoToken} of each {@link LudoPlayer}, a {@link Shape} is created using
   * {@link PlayerTokenFactory}, styled, and stored in the {@code tokenShapeMap}.
   * Each token is then initially placed in its designated start area using
   * {@link #placeTokenInStartArea(Player, LudoToken)}.
   *
   * @param players The list of {@link Player}s whose tokens are to be added.
   */
  @Override
  public void addGamePieces(List<Player> players) {
    players.forEach(player -> {
      ((LudoPlayer) player).getTokens().forEach(token -> {
        Shape playerToken = PlayerTokenFactory.create(8, Color.web(player.getColorHex()),
            player.getPlayerTokenType());
        playerToken.getStyleClass().add("game-board-player-token");
        tokenShapeMap.put(token, playerToken);

        placeTokenInStartArea(player, token);
        playersPane.getChildren().add(playerToken);
      });
    });
  }

  /**
   * Places a specific Ludo token into its designated position within the player's start area.
   * The position is determined by {@link #getTokenStartPosition(Player, LudoToken)}.
   *
   * @param player The {@link Player} who owns the token.
   * @param token  The {@link LudoToken} to be placed.
   */
  private void placeTokenInStartArea(Player player, LudoToken token) {
    double[] startAreaPos = getTokenStartPosition(player, token);

    Shape playerToken = tokenShapeMap.get(token);
    playerToken.setTranslateX(startAreaPos[0]);
    playerToken.setTranslateY(startAreaPos[1]);
  }

  /**
   * Animates the movement of a player's token from its start area to its designated start tile
   * on the main track.
   *
   * @param player  The {@link LudoPlayer} whose token is being released.
   * @param tokenId The ID of the token to be released (typically 0-3).
   */
  public void releaseToken(LudoPlayer player, int tokenId) {
    LudoToken token = player.getTokens().get(tokenId);

    Tile startTile = token.getCurrentTile();

    double[] startTilePos = convertCoordinates(startTile.getCoordinates());
    double[] currentTokenPos = new double[]{tokenShapeMap.get(token).getTranslateX(),
        tokenShapeMap.get(token).getTranslateY()};
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

  /**
   * Animates the movement of a captured Ludo token back to its start area.
   * The animation includes a slight delay to visually signify capture before movement.
   *
   * @param player The {@link Player} who owns the token.
   * @param token  The {@link LudoToken} that was captured and is being moved to its start area.
   */
  public void moveTokenToStartArea(Player player, LudoToken token) {
    double[] startAreaPos = getTokenStartPosition(player, token);
    Shape playerToken = tokenShapeMap.get(token);

    double[] currentTokenPos = new double[]{playerToken.getTranslateX(),
        playerToken.getTranslateY()};

    Path path = new Path();
    path.getElements().add(new MoveTo(currentTokenPos[0], currentTokenPos[1]));
    path.getElements().add(new LineTo(startAreaPos[0], startAreaPos[1]));

    PathTransition pathTransition = new PathTransition();
    pathTransition.setDelay(
        TRANSITION_DURATION); // Allow the token to be visually captured before being moved.
    pathTransition.setDuration(TRANSITION_DURATION);
    pathTransition.setNode(playerToken);
    pathTransition.setPath(path);
    pathTransition.play();
  }

  /**
   * Animates the movement of a Ludo token from an old tile to a new tile on the game board.
   * If {@code straightLine} is true, the token moves directly. Otherwise, it follows the path
   * defined by {@link #getPathTiles(int, Tile, Tile)}.
   *
   * @param token        The {@link LudoToken} to move.
   * @param oldTile      The {@link Tile} the token is currently on.
   * @param newTile      The {@link Tile} the token is moving to.
   * @param straightLine If true, move in a direct line; otherwise, follow the board path.
   */
  public void moveToken(LudoToken token, Tile oldTile, Tile newTile, boolean straightLine) {
    if (oldTile.getTileId() == newTile.getTileId()) {
      return;
    }
    Shape playerToken = tokenShapeMap.get(token);
    playerToken.setCache(true);

    double[] newPaneCoordinates = convertCoordinates(newTile.getCoordinates());

    double currentXpos = playerToken.getTranslateX();
    double currentYpos = playerToken.getTranslateY();
    double newXpos = tileOffset + newPaneCoordinates[0];
    double newYpos = tileOffset + newPaneCoordinates[1];

    Path path = new Path();
    path.getElements().add(new MoveTo(currentXpos, currentYpos));

    Player player = players.stream().filter(p -> ((LudoPlayer) p).getTokens().contains(token))
        .findFirst().orElse(null);
    int playerIndex = players.indexOf(player);

    if (straightLine) {
      path.getElements().add(new LineTo(newXpos, newYpos));
    } else {
      getPathTiles(playerIndex, oldTile, newTile).forEach(tile -> {
        double[] tilePaneCoordinates = convertCoordinates(tile.getCoordinates());
        path.getElements().add(
            new LineTo(tilePaneCoordinates[0] + tileOffset, tilePaneCoordinates[1] + tileOffset));
      });
    }
    PathTransition pathTransition = new PathTransition();
    pathTransition.setDuration(TRANSITION_DURATION);
    pathTransition.setNode(playerToken);
    pathTransition.setPath(path);
    pathTransition.play();
  }

  /**
   * Converts logical board coordinates (row, column) to screen coordinates (x, y)
   * for the Ludo game board.
   *
   * @param rc An array containing the row and column (e.g., {@code [row, col]}).
   * @return An array containing the x and y screen coordinates (e.g., {@code [x, y]}).
   */
  @Override
  protected double[] convertCoordinates(int[] rc) {
    return ViewUtils.ludoBoardToScreenCoordinates(rc, (LudoGameBoard) board, boardDimensions[0],
        boardDimensions[1]);
  }

  /**
   * Calculates the precise starting screen coordinates for a given Ludo token within its
   * player's start area. This depends on the token's ID (1-4) and the player's assigned
   * start area.
   *
   * @param player The {@link Player} who owns the token.
   * @param token  The {@link LudoToken} for which to find the start position.
   * @return A double array {@code [x, y]} representing the screen coordinates.
   */
  private double[] getTokenStartPosition(Player player, LudoToken token) {
    double[] startAreaDimensions = convertCoordinates(
        new int[]{((LudoGameBoard) board).getStartAreaSize(),
            ((LudoGameBoard) board).getStartAreaSize()});
    double[][] playerStartPositions = new double[][]{
        {startAreaDimensions[0] / 3 * 1, startAreaDimensions[1] / 3 * 1},
        {startAreaDimensions[0] / 3 * 2, startAreaDimensions[1] / 3 * 1},
        {startAreaDimensions[0] / 3 * 1, startAreaDimensions[1] / 3 * 2},
        {startAreaDimensions[0] / 3 * 2, startAreaDimensions[1] / 3 * 2}
    };
    Tile firstStartAreaTile = ((LudoGameBoard) board).getTile(
        ((LudoGameBoard) board).getPlayerStartIndexes()[players.indexOf(player)]);
    double[] startAreaFirstTilePos = convertCoordinates(firstStartAreaTile.getCoordinates());

    double startAreaPosX = playerStartPositions[token.getTokenId() - 1][0];
    double startAreaPosY = playerStartPositions[token.getTokenId() - 1][1];

    return new double[]{startAreaFirstTilePos[0] + startAreaPosX,
        startAreaFirstTilePos[1] + startAreaPosY};
  }

  /**
   * Determines the sequence of tiles a Ludo token must traverse to move from a start tile
   * to an end tile, accounting for wrapping around the board and moving onto finish tracks.
   *
   * @param playerIndex The index of the player who owns the token.
   * @param startTile   The {@link Tile} where the movement begins.
   * @param endTile     The {@link Tile} where the movement ends.
   * @return A list of {@link Tile} objects representing the path. For direct moves or when
   *         start and end are the same, this list might be empty or contain only the end tile.
   */
  protected List<Tile> getPathTiles(int playerIndex, Tile startTile, Tile endTile) {
    List<Tile> pathTiles = new ArrayList<>();

    int fromId = startTile.getTileId();
    int toId = endTile.getTileId();

    int totalTrackTileCount = ((LudoGameBoard) board).getTotalTrackTileCount();
    int firstTrackTileId = ((LudoGameBoard) board).getPlayerTrackStartIndexes()[0];

    if (fromId < toId) {
      for (int id = fromId + 1; id <= toId;
          id++) { // For players to move smoothly onto the finish track.
        if (id == ((LudoGameBoard) board).getPlayerTrackStartIndexes()[playerIndex]) {
          id = ((LudoGameBoard) board).getPlayerFinishStartIndexes()[playerIndex];
        }
        if (id == ((LudoGameBoard) board).getPlayerTrackStartIndexes()[playerIndex] - 1) {
          continue;
        }
        if (playerIndex == 0 && id
            == totalTrackTileCount) { // For the first player to go onto the finish track smoothly.
          continue;
        }
        if (id > ((LudoGameBoard) board).getPlayerFinishStartIndexes()[playerIndex]
            + ((LudoGameBoard) board).getStartAreaSize() - 2) {
          id = toId;
        }
        pathTiles.add(board.getTile(id));
      }
    } else {
      // For everyone but the first player to pass the last / first track tile gap smoothly.
      if (fromId <= totalTrackTileCount && toId
          >= firstTrackTileId) {
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
