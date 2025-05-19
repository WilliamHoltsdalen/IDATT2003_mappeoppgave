package edu.ntnu.idi.idatt.navigation;

/**
 * ViewType.
 *
 * <p>Enumerates the different types of views or screens that can be displayed within the
 * application. This is used by the {@link ViewNavigator} to switch between different parts
 * of the user interface, such as game menus, game boards, and board creators.</p>
 *
 * @see ViewNavigator
 */
public enum ViewType {
  /** The main game selection screen where users can choose a game type. */
  GAME_SELECTION,
  /** The menu screen for setting up a Ladder game. */
  LADDER_GAME_MENU,
  /** The main view for playing a Ladder game. */
  LADDER_GAME,
  /** The view for creating or editing Ladder game boards. */
  LADDER_GAME_BOARD_CREATOR,
  /** The menu screen for setting up a Ludo game. */
  LUDO_GAME_MENU,
  /** The main view for playing a Ludo game. */
  LUDO_GAME,
  /** The view for creating or editing Ludo game boards. */
  LUDO_GAME_BOARD_CREATOR,
  /**
   * Represents the view displayed when a game (Ludo or Ladder Game) has finished,
   * showing rankings and options to restart or go to the main menu.
   */
  GAME_FINISHED
}
