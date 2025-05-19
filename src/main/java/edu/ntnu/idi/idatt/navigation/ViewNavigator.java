package edu.ntnu.idi.idatt.navigation;

import edu.ntnu.idi.idatt.controller.common.BoardCreatorController;
import edu.ntnu.idi.idatt.controller.common.GameController;
import edu.ntnu.idi.idatt.controller.common.GameSelectionController;
import edu.ntnu.idi.idatt.controller.common.MenuController;
import edu.ntnu.idi.idatt.controller.laddergame.LadderGameBoardCreatorController;
import edu.ntnu.idi.idatt.controller.laddergame.LadderGameController;
import edu.ntnu.idi.idatt.controller.laddergame.LadderGameMenuController;
import edu.ntnu.idi.idatt.controller.ludo.LudoBoardCreatorController;
import edu.ntnu.idi.idatt.controller.ludo.LudoGameController;
import edu.ntnu.idi.idatt.controller.ludo.LudoMenuController;
import edu.ntnu.idi.idatt.model.board.Board;
import edu.ntnu.idi.idatt.model.player.Player;
import edu.ntnu.idi.idatt.observer.ButtonClickObserver;
import edu.ntnu.idi.idatt.view.app.AppView;
import edu.ntnu.idi.idatt.view.common.GameSelectionView;
import edu.ntnu.idi.idatt.view.common.GameView;
import edu.ntnu.idi.idatt.view.common.MenuView;
import edu.ntnu.idi.idatt.view.laddergame.LadderGameBoardCreatorView;
import edu.ntnu.idi.idatt.view.laddergame.LadderGameMenuView;
import edu.ntnu.idi.idatt.view.laddergame.LadderGameView;
import edu.ntnu.idi.idatt.view.ludo.LudoBoardCreatorView;
import edu.ntnu.idi.idatt.view.ludo.LudoGameMenuView;
import edu.ntnu.idi.idatt.view.ludo.LudoGameView;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.scene.Node;

/**
 * <h3>ViewNavigator.</h3>
 *
 * <p>Handles navigation between different views/screens within the application.</p>
 *
 * <p>It acts as a central point for view switching, responding to button clicks (as a
 * {@link ButtonClickObserver})
 * or direct calls to navigate to specific {@link ViewType}s. It creates the necessary views and
 * their controllers, setting up dependencies and event handlers (like {@code onStartGame},
 * {@code onQuitGame}) to link them together.</p>
 *
 * @see AppView
 * @see ViewType
 * @see ButtonClickObserver
 */
public class ViewNavigator implements ButtonClickObserver {

  private final AppView appView;

  /**
   * Constructs a ViewNavigator.
   *
   * @param appView The main {@link AppView} container where different views will be displayed.
   */
  public ViewNavigator(AppView appView) {
    this.appView = appView;
  }

  /**
   * Handles button click events without parameters. Attempts to navigate to a {@link ViewType}
   * matching the button ID.
   *
   * @param buttonId The ID of the button, expected to match a {@link ViewType} enum name.
   */
  @Override
  public void onButtonClicked(String buttonId) {
    try {
      navigateTo(ViewType.valueOf(buttonId), Collections.emptyMap());
    } catch (IllegalArgumentException e) {
      System.err.println("Invalid button ID: " + buttonId);
    }
  }

  /**
   * Handles button click events with parameters. Attempts to navigate to a {@link ViewType}
   * matching the button ID, passing along the parameters.
   *
   * @param buttonId The ID of the button, expected to match a {@link ViewType} enum name.
   * @param params   A map of parameters to be passed to the new view/controller.
   */
  @Override
  public void onButtonClickedWithParams(String buttonId, Map<String, Object> params) {
    try {
      navigateTo(ViewType.valueOf(buttonId), params);
    } catch (IllegalArgumentException e) {
      System.err.println("Invalid button ID: " + buttonId);
    }
  }

  /**
   * Navigates to the specified {@link ViewType}, creating and displaying the corresponding view in
   * the {@link #appView}.
   *
   * @param viewType The type of view to navigate to.
   * @param params   A map of parameters required by the target view or its controller (e.g., board
   *                 and players for a game view).
   * @throws IllegalArgumentException if the viewType is unknown.
   */
  public void navigateTo(ViewType viewType, Map<String, Object> params) {
    switch (viewType) {
      case GAME_SELECTION -> appView.showView(createGameSelectionView());
      case LADDER_GAME_MENU -> appView.showView(createLadderGameMenuView());
      case LADDER_GAME -> appView.showView(createLadderGameView(params));
      case LADDER_GAME_BOARD_CREATOR -> appView.showView(createLadderGameBoardCreatorView());
      case LUDO_GAME_MENU -> appView.showView(createLudoGameMenuView());
      case LUDO_GAME -> appView.showView(createLudoGameView(params));
      case LUDO_GAME_BOARD_CREATOR -> appView.showView(createLudoBoardCreatorView());
      default -> throw new IllegalArgumentException("Unknown view type: " + viewType);
    }
  }

  /**
   * Navigates to the specified {@link ViewType} without any parameters. Calls
   * {@link #navigateTo(ViewType, Map)} with an empty parameter map.
   *
   * @param viewType The type of view to navigate to.
   */
  public void navigateTo(ViewType viewType) {
    navigateTo(viewType, Collections.emptyMap());
  }

  /**
   * Creates and configures the {@link GameSelectionView} and its {@link GameSelectionController}.
   * Sets up navigation actions for selecting different games.
   *
   * @return The configured {@link GameSelectionView} node.
   */
  private Node createGameSelectionView() {
    GameSelectionView view = new GameSelectionView();
    GameSelectionController controller = new GameSelectionController(view);
    controller.setOnLadderGame(() -> navigateTo(ViewType.LADDER_GAME_MENU));
    controller.setOnLudoGame(() -> navigateTo(ViewType.LUDO_GAME_MENU));
    view.addObserver(controller);
    return view;
  }

  /**
   * Creates and configures the {@link LadderGameMenuView} and its {@link LadderGameMenuController}.
   * Sets up actions for starting a game, creating a board, and going back to game selection.
   *
   * @return The configured {@link LadderGameMenuView} node.
   */
  private Node createLadderGameMenuView() {
    MenuView view = new LadderGameMenuView();
    MenuController controller = new LadderGameMenuController(view);
    controller.setOnStartGame((board, players) -> {
      Map<String, Object> params = new HashMap<>();
      params.put("board", board);
      params.put("players", players);
      navigateTo(ViewType.LADDER_GAME, params);
    });
    controller.setOnCreateBoard(() -> navigateTo(ViewType.LADDER_GAME_BOARD_CREATOR));
    controller.setOnBackToGameSelection(() -> navigateTo(ViewType.GAME_SELECTION));
    view.addObserver(controller);
    return view;
  }

  /**
   * Creates and configures the {@link LadderGameView} and its {@link LadderGameController}.
   * Retrieves board and player data from the provided parameters. Sets up the action for quitting
   * the game.
   *
   * @param params A map containing "board" ({@link Board}) and "players" (List of {@link Player})
   *               data.
   * @return The configured {@link LadderGameView} node.
   */
  private Node createLadderGameView(Map<String, Object> params) {
    Board board = (Board) params.get("board");
    List<Player> players = (List<Player>) params.get("players");
    GameView view = new LadderGameView();
    GameController controller = new LadderGameController(
        (LadderGameView) view, board, players);
    controller.setOnQuitGame(() -> navigateTo(ViewType.LADDER_GAME_MENU, Collections.emptyMap()));
    view.addObserver(controller);
    return view;
  }

  /**
   * Creates and configures the {@link LadderGameBoardCreatorView} and its
   * {@link LadderGameBoardCreatorController}. Sets up the action for navigating back to the ladder
   * game menu.
   *
   * @return The configured {@link LadderGameBoardCreatorView} node.
   */
  private Node createLadderGameBoardCreatorView() {
    LadderGameBoardCreatorView view = new LadderGameBoardCreatorView();
    BoardCreatorController controller = new LadderGameBoardCreatorController(view);
    controller.setOnBackToMenu(() -> navigateTo(ViewType.LADDER_GAME_MENU, Collections.emptyMap()));
    return view;
  }

  /**
   * Creates and configures the {@link LudoGameMenuView} and its {@link LudoMenuController}. Sets up
   * actions for starting a game, creating a board, and going back to game selection.
   *
   * @return The configured {@link LudoGameMenuView} node.
   */
  private Node createLudoGameMenuView() {
    LudoGameMenuView view = new LudoGameMenuView();
    MenuController controller = new LudoMenuController(view);
    controller.setOnStartGame((board, players) -> {
      Map<String, Object> params = new HashMap<>();
      params.put("board", board);
      params.put("players", players);
      navigateTo(ViewType.LUDO_GAME, params);
    });
    controller.setOnCreateBoard(() -> navigateTo(ViewType.LUDO_GAME_BOARD_CREATOR));
    controller.setOnBackToGameSelection(() -> navigateTo(ViewType.GAME_SELECTION));
    view.addObserver(controller);
    return view;
  }

  /**
   * Creates and configures the {@link LudoGameView} and its {@link LudoGameController}. Retrieves
   * board and player data from the provided parameters. Sets up the action for quitting the game.
   *
   * @param params A map containing "board" ({@link Board}) and "players" (List of {@link Player})
   *               data.
   * @return The configured {@link LudoGameView} node.
   */
  private Node createLudoGameView(Map<String, Object> params) {
    Board board = (Board) params.get("board");
    List<Player> players = (List<Player>) params.get("players");
    GameView view = new LudoGameView();
    GameController controller = new LudoGameController(
        (LudoGameView) view, board, players);
    controller.setOnQuitGame(() -> navigateTo(ViewType.LUDO_GAME_MENU, Collections.emptyMap()));
    view.addObserver(controller);
    return view;
  }

  /**
   * Creates and configures the {@link LudoBoardCreatorView} and its
   * {@link LudoBoardCreatorController}. Sets up the action for navigating back to the ludo game
   * menu.
   *
   * @return The configured {@link LudoBoardCreatorView} node.
   */
  private Node createLudoBoardCreatorView() {
    LudoBoardCreatorView view = new LudoBoardCreatorView();
    BoardCreatorController controller = new LudoBoardCreatorController(view);
    controller.setOnBackToMenu(() -> navigateTo(ViewType.LUDO_GAME_MENU, Collections.emptyMap()));
    return view;
  }
}