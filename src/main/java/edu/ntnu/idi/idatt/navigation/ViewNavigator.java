package edu.ntnu.idi.idatt.navigation;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.ntnu.idi.idatt.controller.LadderGameController;
import edu.ntnu.idi.idatt.controller.MainMenuController;
import edu.ntnu.idi.idatt.model.Board;
import edu.ntnu.idi.idatt.model.Player;
import edu.ntnu.idi.idatt.observer.ButtonClickObserver;
import edu.ntnu.idi.idatt.view.container.AppView;
import edu.ntnu.idi.idatt.view.container.LadderGameView;
import edu.ntnu.idi.idatt.view.container.MainMenuView;
import javafx.scene.Node;

public class ViewNavigator implements ButtonClickObserver {
  private final AppView appView;

  public ViewNavigator(AppView appView) {
    this.appView = appView;
  }

  @Override
  public void onButtonClicked(String buttonId) {
    navigateTo(ViewType.valueOf(buttonId), Collections.emptyMap());
  }

  @Override
  public void onButtonClickedWithParams(String buttonId, Map<String, Object> params) {
    navigateTo(ViewType.valueOf(buttonId), params);
  }

  public void navigateTo(ViewType viewType, Map<String, Object> params) {
    switch (viewType) {
      case MAIN_MENU -> appView.showView(createMainMenuView());
      case LADDER_GAME -> appView.showView(createLadderGameView(params));
      default -> throw new IllegalArgumentException("Unknown view type: " + viewType);
    }
  }

  public void navigateTo(ViewType viewType) {
    navigateTo(viewType, Collections.emptyMap());
  }

  private Node createMainMenuView() {
    MainMenuView view = new MainMenuView();
    MainMenuController controller = new MainMenuController(view);
    controller.setOnStartGame((board, players) -> {
      Map<String, Object> params = new HashMap<>();
      params.put("board", board);
      params.put("players", players);
      navigateTo(ViewType.LADDER_GAME, params);
    });
    view.addObserver(controller);
    return view;
  }

  private Node createLadderGameView(Map<String, Object> params) {
    Board board = (Board) params.get("board");
    List<Player> players = (List<Player>) params.get("players");
    LadderGameView view = new LadderGameView();
    LadderGameController controller = new LadderGameController(
        view, board, players);
    controller.setOnQuitGame(() -> navigateTo(ViewType.MAIN_MENU, Collections.emptyMap()));
    view.addObserver(controller);
    return view;
  }
}
