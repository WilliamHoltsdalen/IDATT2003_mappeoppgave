package edu.ntnu.idi.idatt.controller;

import java.util.Map;

import edu.ntnu.idi.idatt.observer.ButtonClickObserver;
import edu.ntnu.idi.idatt.view.container.BoardCreatorView;

public class BoardCreatorController implements ButtonClickObserver {
  private final BoardCreatorView view;
  private Runnable onBackToMenu;

  public BoardCreatorController(BoardCreatorView view) {
    this.view = view;
    view.addObserver(this);
    initializeBoardCreatorView();
  }

  private void initializeBoardCreatorView() {
    view.initialize();
  }

  @Override
  public void onButtonClicked(String buttonId) {
    switch (buttonId) {
      case "save_board" -> handleSaveBoard();
      case "back_to_menu" -> handleBackToMenu();
      default -> {
        break;
      }
    }
  }

  @Override
  public void onButtonClickedWithParams(String buttonId, Map<String, Object> params) {
    // Not needed for now
  }

  private void handleSaveBoard() {
    // TODO: Implement board saving functionality
    // Maybe we can use the snapshot functionality for the board stack pane to save as image,
    // or maybe we should just save as JSON to later be able to load it again and customize it.
  }

  private void handleBackToMenu() {
    if (onBackToMenu != null) {
      onBackToMenu.run();
    }
  }

  public void setOnBackToMenu(Runnable onBackToMenu) {
    this.onBackToMenu = onBackToMenu;
  }
} 