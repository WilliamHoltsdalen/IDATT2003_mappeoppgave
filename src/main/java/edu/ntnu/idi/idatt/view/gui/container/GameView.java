package edu.ntnu.idi.idatt.view.gui.container;

import edu.ntnu.idi.idatt.controller.GameController;
import edu.ntnu.idi.idatt.model.Player;
import edu.ntnu.idi.idatt.observer.BoardGameObserver;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

public class GameView extends HBox implements BoardGameObserver {
  private final GameController gameController;
  private final VBox playersBox;
  private final StackPane boardStackPane;

  public GameView(GameController gameController) {
    this.gameController = gameController;

    gameController.addObserver(this);

    playersBox = getPlayersBox();
    boardStackPane = getBoardStackPane();
    initialize();
  }

  private void initialize() {

    this.getChildren().setAll(playersBox, boardStackPane);
    this.getStyleClass().add("game-view");
  }

  public HBox getView() {
    return this;
  }

  private VBox getPlayersBox() {
    VBox vBox = new VBox();
    vBox.getStyleClass().add("game-players-box");
    vBox.maxHeightProperty().bind(vBox.heightProperty());

    for (Player player : gameController.getPlayers()) {
      Circle playerCircle = new Circle(10, Color.TRANSPARENT);
      playerCircle.setStroke(Color.web(player.getColorHex()));
      playerCircle.setStrokeWidth(8);

      Text playerName = new Text(player.getName());
      playerName.getStyleClass().add("game-players-box-player-name");

      Region spacer = new Region();
      spacer.getStyleClass().add("game-players-box-player-spacer");
      HBox.setHgrow(spacer, Priority.ALWAYS);

      Label playerTile = new Label(String.valueOf(player.getCurrentTile().getTileId()));
      playerTile.getStyleClass().add("game-players-box-player-tile");

      HBox playerRow = new HBox(playerCircle, playerName, spacer, playerTile);
      playerRow.getStyleClass().add("game-players-box-player-row");
      vBox.getChildren().add(playerRow);
    }

    return vBox;
  }

  private StackPane getBoardStackPane() {
    ImageView imageView = new ImageView();
    imageView.setImage(new Image("/images/Classic90.png"));
    imageView.getStyleClass().add("game-board-image-view");

    StackPane stackPane = new StackPane();
    stackPane.getChildren().add(imageView);
    stackPane.getStyleClass().add("game-board-stack-pane");
    return stackPane;
  }

  @Override
  public void onPlayerMoved(Player player, int diceRoll, int newTileId) {
    System.out.println(player.getName() + " moved to tile " + newTileId);
  }

  @Override
  public void onGameStateChanged(String stateUpdate) {
    System.out.println(stateUpdate);
  }

  @Override
  public void onTileActionPerformed(edu.ntnu.idi.idatt.model.interfaces.TileAction tileAction) {
    System.out.println(tileAction.getDescription());
  }

  @Override
  public void onGameFinished(Player winner) {
    System.out.println("Game finished! Winner: " + winner.getName());
  }
}
