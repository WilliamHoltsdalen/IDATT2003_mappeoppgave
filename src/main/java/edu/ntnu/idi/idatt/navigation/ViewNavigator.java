package edu.ntnu.idi.idatt.navigation;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.ntnu.idi.idatt.controller.BoardCreatorController;
import edu.ntnu.idi.idatt.controller.common.GameController;
import edu.ntnu.idi.idatt.controller.common.GameSelectionController;
import edu.ntnu.idi.idatt.controller.common.MenuController;
import edu.ntnu.idi.idatt.controller.laddergame.LadderGameController;
import edu.ntnu.idi.idatt.controller.laddergame.LadderGameMenuController;
import edu.ntnu.idi.idatt.controller.ludo.LudoGameController;
import edu.ntnu.idi.idatt.controller.ludo.LudoMenuController;
import edu.ntnu.idi.idatt.model.board.Board;
import edu.ntnu.idi.idatt.model.board.LudoGameBoard;
import edu.ntnu.idi.idatt.model.player.Player;
import edu.ntnu.idi.idatt.observer.ButtonClickObserver;
import edu.ntnu.idi.idatt.view.app.AppView;
import edu.ntnu.idi.idatt.view.common.GameSelectionView;
import edu.ntnu.idi.idatt.view.common.GameView;
import edu.ntnu.idi.idatt.view.common.MenuView;
import edu.ntnu.idi.idatt.view.laddergame.LadderGameBoardCreatorView;
import edu.ntnu.idi.idatt.view.laddergame.LadderGameMenuView;
import edu.ntnu.idi.idatt.view.laddergame.LadderGameView;
import edu.ntnu.idi.idatt.view.ludo.LudoGameMenuView;
import edu.ntnu.idi.idatt.view.ludo.LudoGameView;
import javafx.scene.Node;
import javafx.scene.paint.Color;

public class ViewNavigator implements ButtonClickObserver {
  private final AppView appView;

  public ViewNavigator(AppView appView) {
    this.appView = appView;
  }

  @Override
  public void onButtonClicked(String buttonId) {
    try {
      navigateTo(ViewType.valueOf(buttonId), Collections.emptyMap());
    } catch (IllegalArgumentException e) {
      System.err.println("Invalid button ID: " + buttonId);
    }
  }

  @Override
  public void onButtonClickedWithParams(String buttonId, Map<String, Object> params) {
    try {
      navigateTo(ViewType.valueOf(buttonId), params);
    } catch (IllegalArgumentException e) {
      System.err.println("Invalid button ID: " + buttonId);
    }
  }

  public void navigateTo(ViewType viewType, Map<String, Object> params) {
    switch (viewType) {
      case GAME_SELECTION -> appView.showView(createGameSelectionView());
      case LADDER_GAME_MENU -> appView.showView(createLadderGameMenuView());
      case LADDER_GAME -> appView.showView(createLadderGameView(params));
      case LADDER_GAME_BOARD_CREATOR -> appView.showView(createLadderGameBoardCreatorView());
      case LUDO_GAME_MENU -> appView.showView(createLudoGameMenuView());
      case LUDO_GAME -> appView.showView(createLudoGameView(params));
      default -> throw new IllegalArgumentException("Unknown view type: " + viewType);
    }
  }

  public void navigateTo(ViewType viewType) {
    navigateTo(viewType, Collections.emptyMap());
  }

  private Node createGameSelectionView() {
    GameSelectionView view = new GameSelectionView();
    GameSelectionController controller = new GameSelectionController(view);
    controller.setOnLadderGame(() -> navigateTo(ViewType.LADDER_GAME_MENU));
    controller.setOnLudoGame(() -> navigateTo(ViewType.LUDO_GAME_MENU));
    view.addObserver(controller);
    return view;
  }

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

  private Node createLadderGameBoardCreatorView() {
    LadderGameBoardCreatorView view = new LadderGameBoardCreatorView();
    BoardCreatorController controller = new BoardCreatorController(view);
    controller.setOnBackToMenu(() -> navigateTo(ViewType.LADDER_GAME_MENU, Collections.emptyMap()));
    return view;
  }

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
}
