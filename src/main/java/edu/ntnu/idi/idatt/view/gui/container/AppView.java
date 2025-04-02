package edu.ntnu.idi.idatt.view.gui.container;

import edu.ntnu.idi.idatt.view.gui.MainMenuView;
import javafx.scene.layout.StackPane;

public class AppView extends StackPane {
  MainMenuView mainMenuView;

  public AppView() {
    mainMenuView = new MainMenuView();

    this.getStyleClass().add("app-view");
  }

  public void showMainMenu() {
    this.getChildren().setAll(mainMenuView.getView());
  }
}
