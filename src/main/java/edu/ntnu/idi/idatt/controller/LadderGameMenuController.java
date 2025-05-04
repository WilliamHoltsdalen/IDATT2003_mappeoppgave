package edu.ntnu.idi.idatt.controller;

import edu.ntnu.idi.idatt.controller.common.MenuController;
import edu.ntnu.idi.idatt.factory.board.LadderBoardFactory;
import edu.ntnu.idi.idatt.view.common.MenuView;

public class LadderGameMenuController extends MenuController {

  /**
   * Constructor for LadderGameMenuController class.
   */
  public LadderGameMenuController(MenuView menuView) {
    super(menuView);

    super.setBoardFactory(new LadderBoardFactory());
    super.initializeMenuView();
  }

  /**
   * Loads the hardcoded boards from the factory, and adds them to the {@link #boardVariants} map.
   *
   * @see LadderBoardFactory#createBoard(String)
   */
  @Override
  protected void loadBoardsFromFactory() {
    super.boardVariants.put(1, boardFactory.createBoard("Classic"));
    super.boardVariants.put(2, boardFactory.createBoard("Teleporting"));
  }
}
