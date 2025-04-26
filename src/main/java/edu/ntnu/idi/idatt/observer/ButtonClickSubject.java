package edu.ntnu.idi.idatt.observer;

import java.util.Map;

/**
 * <h3>ButtonClickSubject interface</h3>
 *
 * <p>This interface defines the methods for classes that need to notify observers of button click
 * events.
 */
public interface ButtonClickSubject {
  /**
   * Adds an observer to the subject.
   *
   * @param observer the observer to add
   */
  void addObserver(ButtonClickObserver observer);

  /**
   * Removes an observer from the subject.
   *
   * @param observer the observer to remove
   */
  void removeObserver(ButtonClickObserver observer);

  /**
   * Notifies all observers.
   *
   * @param buttonId the ID of the button clicked
   */
  void notifyObservers(String buttonId);

  /**
   * Notifies all observers, with a map of parameters.
   *
   * @param buttonId the ID of the button clicked
   * @param params the parameters of the button click event
   */
  void notifyObserversWithParams(String buttonId, Map<String, Object> params);
}
