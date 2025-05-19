package edu.ntnu.idi.idatt.observer;

import java.util.Map;

/**
 * ButtonClickObserver interface
 *
 * <p>This interface defines methods for observing button click events, such as when a button is
 * clicked in a view.
 */
public interface ButtonClickObserver {

  /**
   * Handles button click events.
   *
   * @param buttonId the ID of the button clicked
   */
  void onButtonClicked(String buttonId);

  /**
   * Handles button click events with parameters.
   *
   * @param buttonId the ID of the button clicked
   * @param params the parameters of the button click event
   */
  void onButtonClickedWithParams(String buttonId, Map<String, Object> params);
}
