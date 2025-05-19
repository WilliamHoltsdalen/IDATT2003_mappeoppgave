package edu.ntnu.idi.idatt.controller.common;

import edu.ntnu.idi.idatt.model.player.Player;
import edu.ntnu.idi.idatt.observer.ButtonClickObserver;
import edu.ntnu.idi.idatt.view.common.GameFinishedView;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * GameFinishedController
 *
 * <p>Controls the {@link GameFinishedView}. It handles user interactions on the game finished
 * screen, such as restarting the game or returning to the main menu.
 */
public class GameFinishedController implements ButtonClickObserver {

  private static final Logger logger = LoggerFactory.getLogger(GameFinishedController.class);

  private final GameFinishedView view;
  private Runnable onMainMenu;

  /**
   * Constructor for GameFinishedController.
   *
   * @param view          The {@link GameFinishedView} to control.
   * @param rankedPlayers The list of players, sorted by rank, to display.
   */
  public GameFinishedController(GameFinishedView view, List<Player> rankedPlayers) {
    this.view = view;
    initializeView(rankedPlayers);
  }

  private void initializeView(List<Player> rankedPlayers) {
    this.view.initialize(rankedPlayers);
    logger.debug("GameFinishedController initialized and view populated.");
  }

  /**
   * Sets the callback to run when the "Main Menu" button is clicked.
   *
   * @param onMainMenu The main menu callback.
   */
  public void setOnMainMenu(Runnable onMainMenu) {
    this.onMainMenu = onMainMenu;
  }

  /**
   * Handles button click events from the {@link GameFinishedView}.
   *
   * @param buttonId The ID of the button that was clicked.
   */
  @Override
  public void onButtonClicked(String buttonId) {
    logger.debug("Button clicked in GameFinishedController: {}", buttonId);
    if (buttonId.equals("main_menu")) {
      if (onMainMenu != null) {
        onMainMenu.run();
      } else {
        logger.warn("onMainMenu callback is not set.");
      }
    } else {
      logger.warn("Unknown button ID in GameFinishedController: {}", buttonId);
    }
  }

  /**
   * Handles button click events with parameters. Not directly used by this controller's view but
   * implemented as part of {@link ButtonClickObserver}.
   *
   * @param buttonId The button ID.
   * @param params   The parameters.
   */
  @Override
  public void onButtonClickedWithParams(String buttonId, Map<String, Object> params) {
    logger.debug("Button clicked with params: {}, {}", buttonId, params);
  }
} 