package edu.ntnu.idi.idatt.controller.ludo;

import edu.ntnu.idi.idatt.controller.common.MenuController;
import edu.ntnu.idi.idatt.factory.board.LudoBoardFactory;
import edu.ntnu.idi.idatt.factory.player.PlayerFactory;
import edu.ntnu.idi.idatt.model.player.LudoPlayer;
import edu.ntnu.idi.idatt.model.player.Player;
import edu.ntnu.idi.idatt.model.player.PlayerTokenType;
import edu.ntnu.idi.idatt.view.common.MenuView;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * LudoMenuController.
 *
 * <p>Extends {@link MenuController} to manage the Ludo game setup menu. It is responsible for
 * initializing the menu view with Ludo-specific options, such as allowed player token types
 * (typically circles for Ludo) and colors. It uses a {@link LudoBoardFactory} to create and
 * provide different Ludo board variants (e.g., Classic, Small, Large).</p>
 *
 * <p>This controller handles player configuration, including creating {@link LudoPlayer} instances
 * from the menu input. It also defines the minimum and maximum number of players allowed for a
 * Ludo game and supports loading player configurations from a file, ensuring only Ludo players
 * are imported.</p>
 *
 * @see MenuController
 * @see MenuView
 * @see LudoBoardFactory
 * @see LudoPlayer
 * @see PlayerTokenType
 */
public class LudoMenuController extends MenuController {

  private static final int MIN_PLAYERS = 2;
  private static final int MAX_PLAYERS = 4;
  private static final List<PlayerTokenType> ALLOWED_PLAYER_TOKEN_TYPES = List.of(
      PlayerTokenType.CIRCLE);
  private static final List<String> ALLOWED_PLAYER_COLORS = List.of("Red", "Blue", "Green",
      "Yellow");

  /**
   * Constructs a {@code LudoMenuController} with the specified menu view.
   * Initializes the board factory to {@link LudoBoardFactory} and sets up the menu view
   * with Ludo-specific configurations.
   *
   * @param view The {@link MenuView} that this controller will manage.
   */
  public LudoMenuController(MenuView view) {
    super(view);

    super.setBoardFactory(new LudoBoardFactory());
    initializeMenuView();
  }

  /**
   * Retrieves the list of players configured in the menu, creating {@link LudoPlayer} instances.
   *
   * @return A list of {@link Player} objects, specifically {@link LudoPlayer}s, based on the
   *         menu's player row configurations.
   */
  @Override
  protected List<Player> getPlayers() {
    List<Player> players = new ArrayList<>();
    menuView.getPlayerRows().forEach(playerRow ->
        players.add(new LudoPlayer(playerRow.getName(), playerRow.getColor().toString(),
            playerRow.getPlayerTokenType(), playerRow.isBot())));
    return players;
  }

  /**
   * Initializes the menu view with settings specific to Ludo.
   * This includes loading available Ludo board variants, setting a default selected board,
   * and configuring the view with the game title, allowed token types, allowed colors,
   * and min/max player counts for Ludo.
   */
  @Override
  protected void initializeMenuView() {
    loadBoardsFromFactory();
    menuView.setSelectedBoard(boardFactory.createBoard("Classic"));
    menuView.initialize("Ludo Game Menu", ALLOWED_PLAYER_TOKEN_TYPES, ALLOWED_PLAYER_COLORS,
        MIN_PLAYERS, MAX_PLAYERS);
  }

  /**
   * Loads player configurations from a specified file path and updates the menu view.
   * This implementation ensures that only {@link LudoPlayer} instances are imported. If other
   * player types are found, an error is shown.
   *
   * @param filePath The path to the file containing player data.
   * @see PlayerFactory#createPlayersFromFile(String)
   */
  @Override
  public void loadPlayersFromFile(String filePath) {
    try {
      List<Player> players = PlayerFactory.createPlayersFromFile(filePath);
      for (Player player : players) {
        if (!(player instanceof LudoPlayer)) {
          throw new IOException("You can only import ludo players.");
        }
      }
      menuView.setPlayers(players);
      menuView.showInfoAlert("Success", "Players loaded successfully");
    } catch (IOException e) {
      menuView.showErrorAlert("Error", "Could not load players");
    }
  }

  /**
   * Loads the available Ludo board variants (Classic, Small, Large) using the
   * {@link LudoBoardFactory} and stores them in the {@code boardVariants} map.
   */
  @Override
  protected void loadBoardsFromFactory() {
    super.boardVariants.put(1, super.boardFactory.createBoard("Classic"));
    super.boardVariants.put(2, super.boardFactory.createBoard("Small"));
    super.boardVariants.put(3, super.boardFactory.createBoard("Large"));
  }
}