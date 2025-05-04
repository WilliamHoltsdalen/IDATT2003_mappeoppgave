package edu.ntnu.idi.idatt.controller.common;

import edu.ntnu.idi.idatt.observer.ButtonClickObserver;
import edu.ntnu.idi.idatt.view.common.GameSelectionView;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javafx.scene.image.Image;
import javafx.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <h3>GameSelectionController</h3>
 * 
 * <p>This class is responsible for controlling the game selection view. It implements {@link ButtonClickObserver}.
 * 
 * @see ButtonClickObserver
 */
public class GameSelectionController implements ButtonClickObserver {
    private static final Logger logger = LoggerFactory.getLogger(GameSelectionController.class);
    private final GameSelectionView view;
    private final List<Pair<String, Image>> availableGames;
    private Runnable onLadderGame;
    private Runnable onLudoGame;

    /**
     * Constructor for the GameSelectionController.
     * 
     * @param view The view to control.
     */
    public GameSelectionController(GameSelectionView view) {
        this.view = view;
        this.availableGames = new ArrayList<>();
        initializeView();
    }

    /**
     * Initializes the view.
     */
    private void initializeView() {
        logger.debug("Initializing GameSelectionView");
        availableGames.add(new Pair<>("Chutes and Ladders", new Image("boardImages/classicGameBoard.png")));
        availableGames.add(new Pair<>("Ludo", new Image("boardImages/ludoGameBoard.png")));

        view.initialize(availableGames);
    }

    /**
     * Sets the callback to run when the ladder game is selected.
     * 
     * @param onLadderGame The ladder game callback.
     */
    public void setOnLadderGame(Runnable onLadderGame) {
        this.onLadderGame = onLadderGame;
    }

    /**
     * Sets the callback to run when the ludo game is selected.
     * 
     * @param onLudoGame The ludo game callback.
     */
    public void setOnLudoGame(Runnable onLudoGame) {
        this.onLudoGame = onLudoGame;
    }

    /**
     * Handles the button click event.
     * 
     * @param buttonId The button ID.
     */
    @Override
    public void onButtonClicked(String buttonId) {
        logger.debug("Button clicked: {}", buttonId);
        switch (buttonId) {
            case "Chutes and Ladders" -> onLadderGame.run();
            case "Ludo" -> onLudoGame.run();
            default -> logger.debug("Invalid button ID: {}", buttonId);
        }
    }

    /**
     * Handles the button click event with parameters.
     * 
     * @param buttonId The button ID.
     * @param params The parameters.
     */
    @Override
    public void onButtonClickedWithParams(String buttonId, Map<String, Object> params) {
        logger.debug("Button clicked with params: {}", params);
        view.notifyObserversWithParams(buttonId, params);
    }
}