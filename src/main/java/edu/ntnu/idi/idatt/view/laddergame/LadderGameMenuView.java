package edu.ntnu.idi.idatt.view.laddergame;

import edu.ntnu.idi.idatt.model.board.Board;
import edu.ntnu.idi.idatt.model.player.PlayerTokenType;
import edu.ntnu.idi.idatt.view.common.MenuView;
import edu.ntnu.idi.idatt.view.component.MenuPlayerRow;
import java.util.HashSet;
import java.util.Set;
import javafx.application.Platform;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

/**
 * <h3>LadderGameMenuView.</h3>
 *
 * <p>This class extends {@link MenuView} to provide a specialized main menu view for setting up
 * Ladder Games. It utilizes a {@link LadderGameBoardStackPane} for displaying board previews and
 * includes validation logic specific to Ladder Game player configurations.</p>
 *
 * <p>Key Ladder Game specific behaviors include:
 * <ul>
 *   <li>Using {@link LadderGameBoardStackPane} to render the selected board preview in the
 *       menu.</li>
 *   <li>Player validation ({@link #validatePlayers()}): Ensures the minimum number of players is
 *       met and that there are no duplicate player colors or token types selected among the
 *       players. The start game button is enabled/disabled based on this validation.</li>
 *   <li>Updating the displayed board preview ({@link #setSelectedBoard(Board)}) when a new board is
 *       chosen, including its visual representation, title, and description.</li>
 * </ul>
 * </p>
 *
 * @see MenuView
 * @see LadderGameBoardStackPane
 * @see MenuPlayerRow
 * @see Board
 * @see PlayerTokenType
 */
public class LadderGameMenuView extends MenuView {

  /**
   * Constructs a new {@code LadderGameMenuView}. Initializes the view with a
   * {@link LadderGameBoardStackPane} for board previews.
   */
  public LadderGameMenuView() {
    super(new LadderGameBoardStackPane());
  }

  /**
   * Validates the current player configurations in the menu for a Ladder Game. The start game
   * button ({@code super.getStartGameButton()}) is disabled if:
   * <ul>
   *   <li>The number of configured players is less than {@link #minimumPlayers}.</li>
   *   <li>Any two players have selected the same {@link Color}.</li>
   *   <li>Any two players have selected the same {@link PlayerTokenType}.</li>
   * </ul>
   * If all conditions are met, the start game button is enabled. A relevant message is displayed
   * next to the button when it's disabled.
   */
  @Override
  protected void validatePlayers() {
    // Disable the start game button if there are not enough players.
    if (mainMenuPlayerRows.size() < minimumPlayers) {
      disableStartGameButton("You need at least " + minimumPlayers + " players.");
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

    // Find all the unique token types in the main menu, and disable the start game button if there
    // are any duplicates.
    Set<PlayerTokenType> uniqueTokenTypes = new HashSet<>(
        mainMenuPlayerRows.stream().map(MenuPlayerRow::getPlayerTokenType).toList());
    if (uniqueTokenTypes.size() != mainMenuPlayerRows.size()) {
      disableStartGameButton("You can't have two players with the same token type.");
    }
  }

  /**
   * Sets the currently selected {@link Board} to be displayed in the menu's preview area. This
   * method updates the internal {@code selectedBoard} reference, initializes the
   * {@link #boardStackPane} (a {@link LadderGameBoardStackPane}) with the new board and its
   * background, adjusts the preview size, and updates the displayed board title and description. It
   * also attempts to update the board visual within the carousel if present.
   *
   * @param board The {@link Board} object to be set as the selected preview.
   */
  @Override
  public void setSelectedBoard(Board board) {
    selectedBoard = board;
    boardStackPane.initialize(selectedBoard,
        selectedBoard.getBackground());
    boardStackPane.getBackgroundImageView().setFitWidth(250);
    boardStackPane.getStyleClass().add("menu-board-selection-board-view");
    boardTitle.setText(board.getName());
    boardDescription.setText(board.getDescription());
    Platform.runLater(() -> {
      // Update the board in the carousel
      if (boardSelectionBox.getChildren().size() > 1) {
        VBox carousel = (VBox) boardSelectionBox.getChildren().get(1);
        if (carousel.getChildren().size() > 0) {
          carousel.getChildren().set(0, boardStackPane);
          logger.debug("Board stack pane in carousel updated successfully");
        }
      }
    });
  }
}
