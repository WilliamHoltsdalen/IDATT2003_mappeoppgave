package edu.ntnu.idi.idatt.view.ludo;

import java.util.HashSet;
import java.util.Set;

import edu.ntnu.idi.idatt.view.common.MenuView;
import edu.ntnu.idi.idatt.view.component.MenuPlayerRow;
import javafx.scene.paint.Color;

public class LudoGameMenuView extends MenuView {
    public LudoGameMenuView() {
        super();
    }

    /**
   * Updates the controls of the main menu view based on the number of players in the main menu,
   * and disables the start game button if there are not enough players. The button is also disabled
   * if there are any players with the same color and token type.
   */
  @Override
  protected void updateControls() {
    // Hide / show the add player buttons box based on the number of players in the main menu.
    if (mainMenuPlayerRows.size() == maximumPlayers) {
      playerSelectionBox.getChildren().remove(addPlayerButtonsBox);
    } else if (mainMenuPlayerRows.size() < maximumPlayers) {
      playerSelectionBox.getChildren().setAll(playerSelectionHeader, playerListBox, addPlayerButtonsBox);
    }

    // Disable the start game button if there are not enough players.
    if (mainMenuPlayerRows.size() < minimumPlayers) {
      disableStartGameButton("You need at least " + minimumPlayers + " players.");
    } else {
      enableStartGameButton();
    }

    /* Find all the unique colors and token types in the main menu, and disable the start game button
     * if there are any duplicates.*/
    Set<Color> uniqueColors = new HashSet<>(
        mainMenuPlayerRows.stream().map(MenuPlayerRow::getColor).toList());
    if (uniqueColors.size() != mainMenuPlayerRows.size()) {
      disableStartGameButton("You can't have two players with the same color.");
    }
  }
}