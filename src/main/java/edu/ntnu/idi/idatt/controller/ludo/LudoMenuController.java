package edu.ntnu.idi.idatt.controller.ludo;

import edu.ntnu.idi.idatt.controller.common.MenuController;
import edu.ntnu.idi.idatt.factory.board.LudoBoardFactory;
import edu.ntnu.idi.idatt.model.player.LudoPlayer;
import edu.ntnu.idi.idatt.model.player.Player;
import edu.ntnu.idi.idatt.model.player.PlayerTokenType;
import edu.ntnu.idi.idatt.view.common.MenuView;
import java.util.ArrayList;
import java.util.List;

public class LudoMenuController extends MenuController {

  private static final int MIN_PLAYERS = 2;
  private static final int MAX_PLAYERS = 4;
  private static final List<PlayerTokenType> ALLOWED_PLAYER_TOKEN_TYPES = List.of(
      PlayerTokenType.CIRCLE);
  private static final List<String> ALLOWED_PLAYER_COLORS = List.of("Red", "Blue", "Green",
      "Yellow");

  public LudoMenuController(MenuView view) {
    super(view);

    super.setBoardFactory(new LudoBoardFactory());
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
        players.add(new LudoPlayer(playerRow.getName(), playerRow.getColor().toString(),
            playerRow.getPlayerTokenType())));
    return players;
  }

  @Override
  protected void initializeMenuView() {
    loadBoardsFromFactory();
    menuView.setSelectedBoard(boardFactory.createBoard("Classic"));
    menuView.initialize("Ludo Game Menu", ALLOWED_PLAYER_TOKEN_TYPES, ALLOWED_PLAYER_COLORS,
        MIN_PLAYERS, MAX_PLAYERS);
  }

  @Override
  protected void loadBoardsFromFactory() {
    super.boardVariants.put(1, super.boardFactory.createBoard("Classic"));
    super.boardVariants.put(2, super.boardFactory.createBoard("Small"));
    super.boardVariants.put(3, super.boardFactory.createBoard("Large"));
  }
}