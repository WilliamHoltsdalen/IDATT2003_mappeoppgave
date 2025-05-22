package edu.ntnu.idi.idatt.controller.laddergame;

import edu.ntnu.idi.idatt.controller.common.MenuController;
import edu.ntnu.idi.idatt.factory.board.LadderBoardFactory;
import edu.ntnu.idi.idatt.model.player.LadderGamePlayer;
import edu.ntnu.idi.idatt.model.player.Player;
import edu.ntnu.idi.idatt.model.player.PlayerTokenType;
import edu.ntnu.idi.idatt.view.common.MenuView;
import java.util.ArrayList;
import java.util.List;

/**
 * LadderGameMenuController.
 *
 * <p>This class extends {@link MenuController} to manage the specific setup and logic for the
 * Ladder Game main menu. It is responsible for initializing the menu view with Ladder Game specific
 * options, such as available board variants, player token types, and player limits.</p>
 *
 * <p>Key responsibilities include:
 * <ul>
 *   <li>Setting up the {@link LadderBoardFactory} to create ladder game boards.</li>
 *   <li>Defining constraints for the Ladder Game, such as minimum/maximum players and allowed
 *       player token types (though currently, all token types and colors are permitted without
 *       restriction).</li>
 *   <li>Populating the list of players by creating {@link LadderGamePlayer} instances from the
 *       information entered in the menu view's player rows.</li>
 *   <li>Initializing the {@link MenuView} with Ladder Game specific title, token types, colors,
 *       and player limits.</li>
 *   <li>Loading predefined/hardcoded ladder board variants (e.g., "Classic", "Teleporting") using
 *       the board factory.</li>
 * </ul>
 *
 *
 * @see MenuController
 * @see LadderBoardFactory
 * @see LadderGamePlayer
 * @see MenuView
 * @see PlayerTokenType
 */
public class LadderGameMenuController extends MenuController {

  private static final int MIN_PLAYERS = 2;
  private static final int MAX_PLAYERS = 5;
  private static final List<PlayerTokenType> ALLOWED_PLAYER_TOKEN_TYPES = List.of(
      PlayerTokenType.values());
  private static final List<String> ALLOWED_PLAYER_COLORS = List.of();

  /**
   * Constructs a new {@code LadderGameMenuController}. Initializes the controller with the provided
   * {@link MenuView}, sets up the {@link LadderBoardFactory}, and initializes the menu view for
   * Ladder Game specifics.
   *
   * @param menuView The {@link MenuView} associated with this controller.
   */
  public LadderGameMenuController(MenuView menuView) {
    super(menuView);

    super.setBoardFactory(new LadderBoardFactory());
    logger.debug("LadderBoardFactory set in LadderGameMenuController");
    initializeMenuView();
  }

  /**
   * Retrieves and returns a list of {@link Player} objects based on the current data entered in the
   * player configuration rows of the {@link #menuView}. Each player is created as a
   * {@link LadderGamePlayer} instance.
   *
   * @return A list of {@link LadderGamePlayer}s configured in the menu.
   */
  @Override
  protected List<Player> getPlayers() {
    List<Player> players = new ArrayList<>();
    menuView.getPlayerRows().forEach(playerRow ->
        players.add(new LadderGamePlayer(playerRow.getName(), playerRow.getColor().toString(),
            playerRow.getPlayerTokenType(), playerRow.isBot())));
    logger.debug("Created {} player(s) from menu input", players.size());
    return players;
  }

  /**
   * Initializes the {@link #menuView} for the Ladder Game. This involves loading predefined board
   * variants from the {@link #boardFactory}, setting a default selected board (e.g., "Classic"),
   * and then calling the menu view's initialize method with Ladder Game specific parameters such as
   * the title, allowed token types, allowed colors (currently unrestricted), and min/max players.
   */
  @Override
  protected void initializeMenuView() {
    loadBoardsFromFactory();
    menuView.setSelectedBoard(boardFactory.createBoard("Classic"));
    menuView.initialize("Ladder Game Menu", ALLOWED_PLAYER_TOKEN_TYPES, ALLOWED_PLAYER_COLORS,
        MIN_PLAYERS, MAX_PLAYERS);
    logger.debug("Ladder Game Menu initialized with board 'Classic'");
  }

  /**
   * Loads predefined ladder board variants (e.g., "Classic", "Teleporting") using the
   * {@link #boardFactory} and stores them in the {@link #boardVariants} map. This method is called
   * during the initialization of the menu view.
   *
   * @see LadderBoardFactory#createBoard(String)
   */
  @Override
  protected void loadBoardsFromFactory() {
    super.boardVariants.put(1, boardFactory.createBoard("Classic"));
    super.boardVariants.put(2, boardFactory.createBoard("Teleporting"));
    logger.debug("Loaded boards: Classic and Teleporting into boardVariants");
  }
}
