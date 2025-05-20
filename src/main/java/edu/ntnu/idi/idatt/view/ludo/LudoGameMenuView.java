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

/**
 * LudoGameMenuView.
 *
 * <p>This class extends {@link MenuView} to provide a specialized main menu view for setting up
 * Ludo games. It utilizes a {@link LudoGameBoardStackPane} for displaying board previews and
 * includes validation logic specific to Ludo game player configurations.</p>
 *
 * <p>Key Ludo-specific behaviors include:
 * <ul>
 *   <li>Using {@link LudoGameBoardStackPane} to render the selected board preview in the menu.</li>
 *   <li>Player validation ({@link #validatePlayers()}): Ensures the minimum and maximum number of
 *       players is respected and that there are no duplicate player colors selected. The start
 *       game button is enabled/disabled based on this validation.</li>
 *   <li>Updating the displayed board preview ({@link #setSelectedBoard(Board)}) when a new board is
 *       chosen, including its visual representation, title, and description.</li>
 * </ul>
 *
 *
 * @see MenuView
 * @see LudoGameBoardStackPane
 * @see MenuPlayerRow
 * @see Board
 * @see LudoGameBoard
 */
public class LudoGameMenuView extends MenuView {

  /**
   * Constructs a new {@code LudoGameMenuView}.
   * Initializes the view with a {@link LudoGameBoardStackPane} for displaying Ludo board previews.
   */
  public LudoGameMenuView() {
    super(new LudoGameBoardStackPane());
  }

  /**
   * Validates the current player configurations in the menu for a Ludo Game.
   * The start game button ({@code super.getStartGameButton()}) is disabled if:
   * <ul>
   *   <li>The number of configured players is less than {@link #minimumPlayers}.</li>
   *   <li>The number of configured players is greater than {@link #maximumPlayers}.</li>
   *   <li>Any two players have selected the same {@link Color}.</li>
   * </ul>
   * If all conditions are met, the start game button is enabled. A relevant message is displayed
   * next to the button when it's disabled.
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
   * Sets the currently selected {@link Board} to be displayed in the menu's preview area.
   * This method updates the internal {@code selectedBoard} reference, initializes the
   * {@link #boardStackPane} (a {@link LudoGameBoardStackPane}) with the new Ludo board and its
   * background, adjusts the preview size, and updates the displayed board title and description.
   * It also attempts to update the board visual within the carousel if present.
   *
   * @param board The {@link Board} (expected to be a {@link LudoGameBoard}) to be set as the
   *              selected preview.
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