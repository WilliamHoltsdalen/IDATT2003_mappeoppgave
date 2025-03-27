package edu.ntnu.idi.idatt;

import edu.ntnu.idi.idatt.view.tui.TextUserInterface;

/**
 * <h3>Main class</h3>
 *
 * <p>This class is the main class of the application. It initializes the TextUserInterface and
 * runs it.
 */
public class App {

  /**
   * Main method of the application.
   *
   * @param args Command line arguments.
   */
  public static void main(String[] args) {
    TextUserInterface tui = new TextUserInterface();
    tui.init();
    tui.run();
  }
}