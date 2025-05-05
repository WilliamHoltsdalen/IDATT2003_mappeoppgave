package edu.ntnu.idi.idatt.controller.laddergame;

import java.util.ArrayList;
import java.util.List;

import edu.ntnu.idi.idatt.controller.common.MenuController;
import edu.ntnu.idi.idatt.factory.board.LadderBoardFactory;
import edu.ntnu.idi.idatt.model.player.LadderGamePlayer;
import edu.ntnu.idi.idatt.model.player.Player;
import edu.ntnu.idi.idatt.model.player.PlayerTokenType;
import edu.ntnu.idi.idatt.view.common.MenuView;

public class LadderGameMenuController extends MenuController {
  private static final int MIN_PLAYERS = 2;
  private static final int MAX_PLAYERS = 5;
  private static final List<PlayerTokenType> ALLOWED_PLAYER_TOKEN_TYPES = List.of(PlayerTokenType.values());
  private static final List<String> ALLOWED_PLAYER_COLORS = List.of();

  /**
   * Constructor for LadderGameMenuController class.
   */
  public LadderGameMenuController(MenuView menuView) {
    super(menuView);

    super.setBoardFactory(new LadderBoardFactory());
    initializeMenuView();
  }

  /**
   * Returns the players of the game.
   *
   * @return The players of the game.
   */
  @Override
  protected List<Player> getPlayers() {
    List<Player> players = new ArrayList<>();
    menuView.getPlayerRows().forEach(playerRow ->
        players.add(new LadderGamePlayer(playerRow.getName(), playerRow.getColor().toString(),
            playerRow.getPlayerTokenType())));
    return players;
  }

  /**{@inheritDoc} */
  @Override
  protected void initializeMenuView() {
    loadBoardsFromFactory();
    menuView.setSelectedBoard(boardFactory.createBoard("Classic"));
    menuView.initialize("Ladder Game Menu", ALLOWED_PLAYER_TOKEN_TYPES, ALLOWED_PLAYER_COLORS, MIN_PLAYERS, MAX_PLAYERS);
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
