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
   * Validates the players in the main menu. Disables the start game button if there are not enough players,
   * or if there are any duplicate colors or token types.
   */
  @Override
  protected void validatePlayers() {
    // Disable the start game button if there are not enough players.
    if (mainMenuPlayerRows.size() < minimumPlayers) {
      disableStartGameButton("You need at least " + minimumPlayers + " players.");
    } else {
      enableStartGameButton();
    }

    // Find all the unique colors in the main menu, and disable the start game button if there are any duplicates.
    Set<Color> uniqueColors = new HashSet<>(
        mainMenuPlayerRows.stream().map(MenuPlayerRow::getColor).toList());
    if (uniqueColors.size() != mainMenuPlayerRows.size()) {
      disableStartGameButton("You can't have two players with the same color.");
    }
  }
}