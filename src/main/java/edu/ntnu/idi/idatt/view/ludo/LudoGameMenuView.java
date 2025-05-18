package edu.ntnu.idi.idatt.view.ludo;

import edu.ntnu.idi.idatt.model.board.Board;
import edu.ntnu.idi.idatt.model.board.LudoGameBoard;
import edu.ntnu.idi.idatt.view.common.MenuView;
import edu.ntnu.idi.idatt.view.component.MenuPlayerRow;
import java.util.HashSet;
import java.util.Set;
import javafx.application.Platform;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class LudoGameMenuView extends MenuView {

  public LudoGameMenuView() {
    super(new LudoGameBoardStackPane());
  }

  /**
   * Validates the players in the main menu. Disables the start game button if there are not enough
   * players, or if there are any duplicate colors or token types.
   */
  @Override
  protected void validatePlayers() {
    // Disable the start game button if there are not enough players.
    if (mainMenuPlayerRows.size() < minimumPlayers) {
      disableStartGameButton("You need at least " + minimumPlayers + " players.");
    } else if (mainMenuPlayerRows.size() > maximumPlayers) {
      disableStartGameButton("You can't have more than " + maximumPlayers + " players.");
    } else {
      enableStartGameButton();
    }

    // Find all the unique colors in the main menu, and disable the start game button if there are
    // any duplicates.
    Set<Color> uniqueColors = new HashSet<>(
        mainMenuPlayerRows.stream().map(MenuPlayerRow::getColor).toList());
    if (uniqueColors.size() != mainMenuPlayerRows.size()) {
      disableStartGameButton("You can't have two players with the same color.");
    }
  }

  /**
   * Sets the selected board in the main menu to the given board object.
   *
   * @param board The board object to set.
   */
  @Override
  public void setSelectedBoard(Board board) {
    logger.debug("Setting selected board: {}", board.getName());
    selectedBoard = board;
    boardStackPane.initialize((LudoGameBoard) selectedBoard,
        ((LudoGameBoard) selectedBoard).getBackground());
    boardStackPane.getBackgroundImageView().setFitWidth(250);
    boardStackPane.getStyleClass().add("main-menu-board-selection-board-view");
    boardTitle.setText(board.getName());
    boardDescription.setText(board.getDescription());
    Platform.runLater(() -> {
      // Update the board in the carousel
      VBox carousel = (VBox) boardSelectionBox.getChildren().get(1);
      carousel.getChildren().set(0, boardStackPane);
      logger.debug("Board stack pane in carousel updated successfully");
    });
  }
}